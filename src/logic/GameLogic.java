package logic;

import logic.util.Logger;
import logic.util.JsonValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javafx.animation.AnimationTimer;
import logic.board.Board;
import logic.board.Position;
import logic.board.Station;
import logic.player.Detective;
import logic.player.MisterX;
import logic.player.Player;
import logic.util.DetectiveSerializer;
import logic.util.GameLogicSerializer;
import logic.util.MisterXSerializer;
import logic.player.TacticResult;

/**
 * Represents the Logic for the Game Scotlandyard. Extends the AnimationTimer to implement a
 * timebased statemachine.
 *
 * @author Guillaume Founier-Mayer (tinf101922)
 */
public class GameLogic extends AnimationTimer {

    /**
     * The Config for the Game.
     */
    public static final class Config {

        public static final int WIDTH = 1024;
        public static final int HEIGHT = 768;
        public static final String FILE_NAME = "game.log";
        public static final int BOARD_SIZE = 199;
        public static final int MAX_ROUNDS = 24;
        public static final List<Integer> LAST_SEEN_ROUNDS =
                new LinkedList<>(Arrays.asList(3, 8, 13, 18, 24));
        public static final List<Integer> DETECTIVES_NUMS = new LinkedList<>(Arrays.asList(3, 5));
        public static final int TICKET_CAB = 10;
        public static final int TICKET_BUS = 8;
        public static final int TICKET_TUBE = 4;
        public static final int TICKET_BOAT = 2;
        public static final int PLAYER_SIZE = 20;
        public static final double MAX_DISTANCE = 0.025;
        public static final int[] START_POSITIONS = new int[] { 13, 26, 29, 34, 50, 53, 91, 94, 103,
                112, 117, 132, 138, 141, 155, 174, 197, 198 };
        public static final int DELAY = 2000;
        public static final double NANO_TO_MILI_FACTOR = 1E6;

    }

    /**
     * Gets a List of random and unique startpositions out of predefined startpositions.
     *
     * @see Config.
     *
     * @param num The number of startpositions to return
     * @return A list of random and unique startpositions
     */
    private static List<Integer> getStartPositions(int num) {
        List<Integer> startPositions = new LinkedList<>();
        for (int i = 0; i < num; i++) {
            int startPosition = GameLogic.getRandomStartPosition();
            while (startPositions.contains(startPosition)) {
                startPosition = GameLogic.getRandomStartPosition();
            }
            startPositions.add(startPosition);
        }
        return startPositions;
    }

    /**
     * A Helper for @see getStartPositions
     *
     * @return A startposition
     */
    private static int getRandomStartPosition() {
        int index = new Random().nextInt(Config.START_POSITIONS.length - 1);
        return Config.START_POSITIONS[index];
    }

    /**
     * Represents the Gamestate.
     */
    private enum GameState {
        STOPPED, NEXT_TURN, AI_PLAYING, HUMAN_PLAYING
    }

    /**
     * Represents the Winstate
     */
    public enum WinState {
        NO_WIN, DETECTIVES_BLOCKED, MISTERX_SURROUNDED, MISTERX_CATCHED, MISTERX_WIN
    }

    final private Board board;
    private final GUIConnector gui;
    private Player turn;
    private MisterX misterX;
    private List<Detective> detectives;
    private final List<Player> players = new ArrayList<>();
    private GameState gameState = GameState.STOPPED;
    private int whosTurn = -1;
    private int gameRound = 0;
    private long turnBeginTimeStamp = 0;
    private final Gson gson;

    /**
     * A Constructor that creates a new Game by already created players. Mostly used for testing
     *
     * @param board The board
     * @param gui The gui
     * @param misterx MisterX
     * @param detectives All detectives
     */
    public GameLogic(Board board, GUIConnector gui, MisterX misterx, List<Detective> detectives) {
        this(board, gui);
        this.misterX = misterx;
        this.detectives = detectives;
    }

    /**
     * A Constructor helper. All constructors should use this.
     *
     * @param board The board
     * @param gui The gui
     */
    private GameLogic(Board board, GUIConnector gui) {
        this.board = board;
        this.gui = gui;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(MisterX.class, new MisterXSerializer());
        builder.registerTypeAdapter(GameLogic.class, new GameLogicSerializer());
        builder.registerTypeAdapter(Detective.class, new DetectiveSerializer());
        this.gson = builder.setPrettyPrinting().create();
    }

    /**
     * A constructor that creates a new Game.
     *
     * @param jsonBoard The Reader of the Board network
     * @param gui The Gui
     * @param misterXisAi If misterX should be controlled by AI or not
     * @param detectiveNum the number of detective to be created
     * @param detectivesAreAi If the detectives should be controlled by AI or not
     */
    public GameLogic(Reader jsonBoard, GUIConnector gui, boolean misterXisAi, int detectiveNum,
            boolean detectivesAreAi) {
        this(new Board(jsonBoard), gui);
        List<Integer> startPositions = this.getStartPositions(detectiveNum + 1);
        Station misterXStartPosition = this.board.getStation(startPositions.get(0));
        this.misterX = new MisterX(misterXStartPosition, misterXisAi);
        this.detectives = new LinkedList<>();
        for (int i = 1; i <= detectiveNum; i++) {
            Station startPosition = this.board.getStation(startPositions.get(i));
            detectives.add(new Detective(i, startPosition, detectivesAreAi));
        }
    }

    /**
     * A Constructor that loads a pre-saved Game.
     *
     * @param fileToLoad The Reader of the Game to be loaded
     * @param jsonBoard The Reader of the Board network
     * @param gui The Gui
     * @throws JsonIOException Throws if any IOError occurs while reading the file
     */
    public GameLogic(Reader fileToLoad, Reader jsonBoard, GUIConnector gui)
            throws JsonIOException, JsonSyntaxException, IllegalArgumentException {
        this(new Board(jsonBoard), gui);

        // String fileContent = GameLogic.readFile(reader);
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(fileToLoad);
        if (!JsonValidator.validateSaveState(root)) {
            throw new IllegalArgumentException(
                    "File seams to be curropted. Try to load another one.");
        }
        JsonObject object = root.getAsJsonObject();
        this.misterX = this.loadMisterX(object);
        for (int i = 0; i < this.misterX.getLogbook().size(); i++) {
            Ticket ticket = this.misterX.getLogbook().get(i);
            this.gui.setLogbookEntry(i + 1, ticket);
        }

        this.detectives = this.loadDetectives(object);
        this.whosTurn = object.get("whosTurn").getAsInt() - 1;
        this.gameRound = object.get("currRoundNo").getAsInt();
    }

    /**
     * Starts the Game by setting the right Gamestate.
     *
     * @throws IllegalStateException if the Game is already started
     */
    @Override
    public void start() throws IllegalStateException {
        super.start();
        if (this.gameState != GameState.STOPPED) {
            throw new IllegalStateException();
        }
        this.players.add(misterX);
        this.players.addAll(detectives);
        try {
            Logger.printNewGame(misterX, detectives);
            this.gameState = GameState.NEXT_TURN;
        } catch (IOException ex) {
            gui.ShowLogError();
        }

    }

    /**
     * Handles the gamestates of the logic. Is fired on every Frame, but only allows the next AI
     * move every x seconds (defined by Config). Is used to control the gamestate.
     *
     * @param timeStamp The round time between the last frame and the actual one
     */
    @Override
    public void handle(long timeStamp) {
        switch (this.gameState) {
            case NEXT_TURN: {
                WinState state = this.isGameWon();
                // Select whos turn is, reset if needed
                this.whosTurn++;
                if (this.whosTurn >= this.players.size()) {
                    this.whosTurn = 0;
                }
                // Check if Game is Won. If yes show the winner.
                if (state != WinState.NO_WIN) {
                    this.gui.showWinner(state);
                    this.gameState = GameState.STOPPED;
                    try {
                        Logger.printEndGame(state);
                    } catch (IOException ex) {
                        gui.ShowLogError();
                    }
                    this.stop();
                } else {
                    this.turn = this.players.get(this.whosTurn);
                    if (this.turn.isMisterX()) {
                        this.gameRound++;
                    }
                    // Update Gui for next round
                    this.gui.drawPlayers(this.players, this.turn, this.misterX.getLastSeen());
                    this.gui.showPlayersStates(this.turn);
                    // Change the Gamestate if AI or Human is playing
                    if (!this.turn.isAi()) {
                        this.gameState = GameState.HUMAN_PLAYING;
                    } else {
                        this.turnBeginTimeStamp = timeStamp;
                        this.gameState = GameState.AI_PLAYING;
                    }
                }
                break;
            }
            case AI_PLAYING: {
                // Delay every AI Move about x secounds (@see Config)
                long delay =
                        (timeStamp - this.turnBeginTimeStamp) / (long) Config.NANO_TO_MILI_FACTOR;
                if (delay >= Config.DELAY) {
                    try {
                        this.handleAiMove();
                    } catch (IOException ex) {
                        gui.ShowLogError();
                    }
                    this.gameState = GameState.NEXT_TURN;
                }
                break;
            }
        }
    }

    /**
     * Plays a move by AI. When finish it will set the gamestate to next round.
     * 
     * @thros IOException if an IO error occurs while writing to log
     */
    private void handleAiMove() throws IOException {
        // If actual player is MisterX
        if (this.turn.isMisterX()) {
            TacticResult result = this.turn.play(null, null, null, detectives);
            this.turn.move(result.getMove());
            this.gui.setLogbookEntry(this.gameRound, result.getMove().getTicket());
        } else {
            // Get all information needed for the detective tactics
            Station lastSeen = this.misterX.getLastSeen();
            LinkedList<Ticket> mrXUsedTickets = this.misterX.getTicketsFromLastseenToNow();
            Set<Station> targets = Detective.getPossibleTargetPositions(mrXUsedTickets, lastSeen);
            Station averageStation = this.board.getAverageStation(targets);
            // Try to get the best Move by regarding all informations needed for the tactics
            TacticResult result = this.turn.play(lastSeen, targets, averageStation, detectives);

            if (result != null) {
                // If The move is valid, add the Ticket to MisterX
                this.misterX.addTicket(result.getMove().getTicket());
                Station from = this.turn.getCurrentStation();
                Logger.printMove(turn, from, result.getId(), result.getRanking());
                this.turn.move(result.getMove());
            } else {
                Logger.printMove(turn, turn.getCurrentStation(), 0, 0);
            }

        }
    }

    /**
     * Handles an human click on the map by treating it as a station choose.
     *
     * @param position The position on the map where the mouse click happened
     */
    public void handleHumanPlaying(Position position) {
        // Check if the gamestate is right
        if (this.gameState == GameState.HUMAN_PLAYING) {
            Station station = this.getNearestStation(position);
            // Get needed informations of current and destination station
            Set<Ticket> neededTickets =
                    this.turn.getCurrentStation().getTicketsToReachableStation(station);
            Set<Ticket> availableTickets = this.turn.getAvailableTickets();
            availableTickets.retainAll(neededTickets);

            // check if Station is reachable in one round
            if (neededTickets.isEmpty() || station.isOccupied()) {
                this.gui.showStationNotReachableInMove(station);
                // check if enough tickets are left to reach the destination station
            } else if (availableTickets.isEmpty()) {
                this.gui.showNotEnoughTicketsForMoveDialog(station);
            } else {
                // Check if destination station is reachable by mutiple tickets.
                // If yes show an choose dialog
                Ticket ticket = availableTickets.size() > 1
                        ? this.gui.showChooseTicketDialog(station, availableTickets)
                        : availableTickets.stream().findFirst().get();

                // All information needed are present. Move the Player and write to Log
                Move move = new Move(station, ticket);
                this.turn.move(move);

                if (this.turn.isMisterX()) {
                    this.gui.setLogbookEntry(this.gameRound, move.getTicket());
                } else {
                    this.misterX.addTicket(move.getTicket());
                }

                Station from = this.turn.getCurrentStation();
                try {
                    Logger.printMove(this.turn, from, 0, 0);
                } catch (IOException ex) {
                    gui.ShowLogError();
                }
                // Prepare to next round and set the gamestate
                this.gameState = GameState.NEXT_TURN;
            }

        }
    }

    /**
     * Enforce a redraw of all Players
     */
    public void forceRedraw() {
        this.gui.drawPlayers(this.players, this.turn, this.misterX.getLastSeen());
    }

    // WINSTATE ################################################################
    /**
     * Check if the actual Game is won or not.
     *
     * @return The winstate
     */
    public WinState isGameWon() {
        if (this.isMisterXCatched()) {
            return WinState.MISTERX_CATCHED;
        } else if (this.isMisterXSurrounded()) {
            return WinState.MISTERX_SURROUNDED;
        } else if (this.areAllDetectivesBlocked()) {
            return WinState.DETECTIVES_BLOCKED;
        } else if (this.isLastRound()) {
            return WinState.MISTERX_WIN;
        } else {
            return WinState.NO_WIN;
        }
    }

    /**
     * Helper for @see isGameWon.
     *
     * @return true if all detectives are blocked or all no detective has Tickets left
     */
    private boolean areAllDetectivesBlocked() {
        return this.detectives.stream()
                .allMatch(detective -> detective.getAvailableTickets().isEmpty());
    }

    /**
     * Helper for @see isGameWon.
     *
     * @return true if MisterX has been caught
     */
    private boolean isMisterXCatched() {
        return this.detectives.stream().anyMatch(detective -> this.misterX.getCurrentStation()
                .equals(detective.getCurrentStation()));
    }

    /**
     * Helper for @see isGameWon.
     *
     * @return true if MisterX has no possibility to Move
     */
    private boolean isMisterXSurrounded() {
        return this.misterX.getReachableStations().isEmpty();
    }

    /**
     * Helper for @see isGameWon.
     *
     * @return true if last round has been reached
     */
    private boolean isLastRound() {
        return Config.MAX_ROUNDS <= this.gameRound + 1;
    }

    // Getter ##################################################################
    /**
     * Getter for MisterX.
     *
     * @return MisterX
     */
    public MisterX getMisterX() {
        return this.misterX;
    }

    /**
     * Getter for all detectives.
     *
     * @return All detectives
     */
    public List<Detective> getDetectives() {
        return this.detectives;
    }

    /**
     * Getter for actual game round.
     *
     * @return The game round
     */
    public int getGameRound() {
        return this.gameRound;
    }

    /**
     * Getter for actual turn.
     *
     * @return The player that play actually
     */
    public int getWhosTurn() {
        return this.whosTurn;
    }

    /**
     * Converts an Point on the board to the nearest Station.
     *
     * @param point the Point on the Map
     * @return The nearest Station to the point
     */
    public Station getNearestStation(Position point) {
        return this.board.getNearestStation(point, Config.MAX_DISTANCE);
    }

    // Savestate ###############################################################
    /**
     * Save the current game to an File.
     *
     * @param writer The writer of the save file
     * @throws IOException throws an IOException if an IOError occurs
     */
    public void save(FileWriter writer) throws IOException {
        String fileContent = this.gson.toJson(this);
        writer.write(fileContent);
        writer.close();
    }

    /**
     * Builds an instance of MisterX by an JsonObject.
     *
     * @param root the Root Object of an savesgame
     * @return MisterX
     */
    private MisterX loadMisterX(JsonObject root) {

        JsonObject jMisterX = root.get("misterX").getAsJsonObject();
        // is MisterX controlled by AI?
        boolean isAi = jMisterX.get("ai").getAsBoolean();

        // Convert the lastshown position
        int lastShownPos = jMisterX.get("lastShownPos").getAsInt();

        // Convert the current position to a station
        int currPos = jMisterX.get("currPos").getAsInt();

        // Convert the remaining Tickets
        JsonArray remainingTickets = jMisterX.get("remainingTickets").getAsJsonArray();
        int tubeTickets = remainingTickets.get(0).getAsInt();
        int busTickets = remainingTickets.get(1).getAsInt();
        int cabTickets = remainingTickets.get(2).getAsInt();
        int blackTickets = remainingTickets.get(3).getAsInt();

        // Convert the journeyboard
        JsonArray journeyBoard = jMisterX.get("journeyBoard").getAsJsonArray();
        List<Ticket> logbook = new LinkedList<>();
        for (JsonElement element : journeyBoard) {
            Ticket ticket = Ticket.from(element.getAsInt());
            logbook.add(ticket);
        }

        if (tubeTickets < 0 || busTickets < 0 || cabTickets < 0 || blackTickets < 0) {
            throw new IllegalArgumentException(
                    "The file seams to be curropted. Some of the values are wrong. Try to load another one.");
        }

        Station lastSeen = lastShownPos == 0 ? null : board.getStation(lastShownPos);
        Station currentStation = board.getStation(currPos);
        // Build MisterX by help of all information
        return new MisterX(currentStation, lastSeen, logbook, isAi, cabTickets, busTickets,
                tubeTickets, blackTickets);
    }

    /**
     * Builds instances of all Detectives by an JsonObject
     *
     * @param root the Root Object of an savegame
     * @return
     */
    private List<Detective> loadDetectives(JsonObject root) {

        List<Detective> detectives = new LinkedList<>();

        JsonObject jDetectives = root.get("detectives").getAsJsonObject();
        // Are all the detectives controlled by AI
        boolean isAi = jDetectives.get("ai").getAsBoolean();
        int numOfDetectives = jDetectives.get("noOfDetectives").getAsInt();
        // Get playerarray
        JsonArray jPlayers = jDetectives.get("players").getAsJsonArray();
        for (int i = 0; i < numOfDetectives; i++) {
            JsonObject object = jPlayers.get(i).getAsJsonObject();

            // Convert postition
            int position = object.get("position").getAsInt();

            // Convert remaining Tickets
            JsonArray remainingTickets = object.get("remainingTickets").getAsJsonArray();
            int tubeTickets = remainingTickets.get(0).getAsInt();
            int busTickets = remainingTickets.get(1).getAsInt();

            int cabTickets = remainingTickets.get(2).getAsInt();
            if (tubeTickets < 0 || busTickets < 0 || cabTickets < 0
                    || !Config.DETECTIVES_NUMS.contains(numOfDetectives)) {
                throw new IllegalArgumentException(
                        "The file seams to be corrupted. Some of the values are wrong. Try to load another one.");
            }
            Station startStation = this.board.getStation(position);
            // Build an Detective by help of all information and add it to List
            detectives.add(
                    new Detective(i + 1, startStation, isAi, cabTickets, busTickets, tubeTickets));
        }
        return detectives;
    }

}
