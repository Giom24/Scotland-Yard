package logic;

import java.util.List;
import java.util.Set;
import logic.board.Station;
import logic.player.Player;

/**
 * An interface that describes an API between the GUI and the Logic
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public interface GUIConnector {

    /**
     * Draws all the Player on the board.
     *
     * @param player All players
     * @param turn Current player playing
     * @param lastSeen The last seen Station of Mister-X
     */
    public void drawPlayers(List<Player> player, Player turn, Station lastSeen);

    /**
     * Shows the stats of a player.
     *
     * @param player The player to show the stats of
     */
    public void showPlayersStates(Player player);

    /**
     * Shows a dialog that ask a player to choose between multiple Tickets.
     *
     * @param station The Station to reach with multiple Tickets
     * @param tickets The Tickets
     * @return The chosen Ticket
     */
    public Ticket showChooseTicketDialog(Station station, Set<Ticket> tickets);

    /**
     * Shows a dialog that the actual Player has not enough Tickets left to reach the Station.
     *
     * @param station The chosen Station
     */
    public void showNotEnoughTicketsForMoveDialog(Station station);

    /**
     * Shows a dialog that the actual player can not reach the chosen Station.
     *
     * @param station The chosen Station
     */
    public void showStationNotReachableInMove(Station station);

    /**
     * Shows an dialog that the game is won.
     *
     * @param state The Reason why the Game has been won
     */
    public void showWinner(GameLogic.WinState state);

    /**
     * Sets a Ticket into the Logbook.
     *
     * @param round The actual round
     * @param ticket The Ticket that will be added
     */
    public void setLogbookEntry(int round, Ticket ticket);

    /**
     * Shows a Error Message.
     *
     * @param title The title
     * @param header The header
     * @param context The context
     */
    public void showError(String title, String header, String context);

    /**
     * Shows a error message if some IO Error occurs while writing to log
     */
    public void ShowLogError();

}
