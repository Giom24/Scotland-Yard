package logic.player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import logic.GameLogic.Config;
import logic.util.Logger;
import logic.Move;
import logic.Ticket;
import logic.board.Station;

/**
 * Represents MisterX in the game.
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class MisterX extends Player {

    private final List<Ticket> logbook;
    private final List<Integer> lastSeenRounds;
    private int lastSeenRound = 1;
    private Station lastSeen;

    /**
     * Constructor.
     *
     * @param startStation The start station
     * @param lastSeen The station where MisterX has been seen
     * @param logbook The logbook
     * @param ai If MisterX is controlled by AI or not
     * @param cabTickets Available cab tickets
     * @param busTickets Available bus tickets
     * @param tubeTickets Available tube tickets
     * @param blackTickets Available black tickets
     */
    public MisterX(Station startStation, Station lastSeen, List<Ticket> logbook, boolean ai,
            int cabTickets, int busTickets, int tubeTickets, int blackTickets) {
        super(0, startStation, ai, cabTickets, busTickets, tubeTickets, blackTickets);
        this.lastSeenRounds = new LinkedList<>(Config.LAST_SEEN_ROUNDS);
        this.logbook = logbook;
        this.lastSeen = lastSeen;
    }

    /**
     * Constructor. Available tickets will be taken from config. The logbook will be empty.
     *
     * @param startStation The start station
     * @param ai If MusterX is controlled by AI or not
     */
    public MisterX(Station startStation, boolean ai) {
        super(0, startStation, ai, Config.TICKET_CAB, Config.TICKET_BUS, Config.TICKET_TUBE,
                Config.TICKET_BOAT);
        this.lastSeenRounds = new LinkedList<>(Config.LAST_SEEN_ROUNDS);
        this.logbook = new LinkedList<>();
        this.lastSeen = null;
    }

    @Override
    public TacticResult play(Station lastSeen, Set<Station> targets, Station averageStation,
            List<Detective> detectives) {

        TacticResult result = this.getReachableStations().stream()
                // Transform every reachabel Station into a valid Move
                .flatMap(station -> this.getCurrentStation().getTicketsToReachableStation(station)
                        .stream().map(ticket -> new Move(station, ticket)))
                // Rank every posibleMove and transform it into TacticResult
                .map(possibleMove -> {
                    float ranking = this.getRankingForMove(possibleMove, detectives);
                    return new TacticResult(1, possibleMove, ranking);
                    // Reduce to the Best tactic considering the best Tactic and the smallest id
                }).reduce((tactic1, tactic2) -> {
                    if (tactic1.getRanking() > tactic2.getRanking()) {
                        return tactic1;
                    } else if (tactic1.getRanking() < tactic2.getRanking()) {
                        return tactic2;
                    } else {
                        int id1 = tactic1.getMove().getTo().getIdentifier();
                        int id2 = tactic2.getMove().getTo().getIdentifier();
                        return id1 < id2 ? tactic1 : tactic2;
                    }
                }).get();
        return result;
    }

    /**
     * Moves MisterX and add the current Move to the logbook
     *
     * @param move The Move
     */
    @Override
    public void move(Move move) {
        this.setCurrentStation(move.getTo());
        this.removeTicket(move.getTicket());
        this.addTicketToLogbook(move.getTicket());
    }

    // RANKING #################################################################
    /**
     * Gets the ranking for a move.
     *
     * @param move The move to rank
     * @param detectives All detectives
     * @return The ranking for the move
     */
    float getRankingForMove(Move move, List<Detective> detectives) {
        // If no Move is possible, rank ist as worst possible
        if (move == null) {
            return Float.MIN_VALUE;
        }
        Station current = this.getCurrentStation();
        // Do the move
        this.setCurrentStation(move.getTo());
        this.removeTicket(move.getTicket());
        // Rank by all possible ranking methods
        float targetPositionRanking = this.getRankingByReachableTargetStation(detectives);
        float reachAbleStationRanking = this.getRankingByDirectlyReachableStations();
        float ticketRanking = this.getRankingBySmallestTicketAmount();
        // Undo the move
        this.setCurrentStation(current);
        this.addTicket(move.getTicket());

        return targetPositionRanking + reachAbleStationRanking + ticketRanking;

    }

    /**
     * Gets the ranking for reachable target stations by all detectives.
     *
     * @param detectives All detectives
     * @return The ranking
     */
    public float getRankingByReachableTargetStation(List<Detective> detectives) {
        Set<Station> targets = new HashSet<>();
        targets.add(this.getCurrentStation());
        return this.getRankingByReachableTargetStations(targets, detectives);
    }

    /**
     * Gets the ranking for reachable target stations by all detectives.
     *
     * @param targets Possible target stations
     * @param detectives All detectives
     * @return The ranking
     */
    @Override
    public float getRankingByReachableTargetStations(Set<Station> targets,
            List<Detective> detectives) {
        int detectiveCount = detectives.size();

        long detectiveReachableCount = detectives.stream()
                .map(detective -> detective.getReachableStations()).filter(stations -> {
                    stations.retainAll(targets);
                    return !stations.isEmpty();
                }).count();

        return (detectiveCount - detectiveReachableCount) * 10;
    }
    // LOGBOOK #################################################################

    /**
     * Getter for the logbook.
     *
     * @return The logbook
     */
    public List<Ticket> getLogbook() {
        return this.logbook;
    }

    /**
     * Returns the used ticket from the last seen station to now.
     *
     * @return The list of tickets
     */
    public LinkedList<Ticket> getTicketsFromLastseenToNow() {
        LinkedList<Ticket> result = new LinkedList<>();
        this.logbook.subList(this.lastSeenRound - 1, this.logbook.size());

        return result;
    }

    /**
     * Getter for the last seen station.
     *
     * @return The last seen station
     */
    public Station getLastSeen() {
        return this.lastSeen;
    }

    /**
     * Adds a ticket to the logbook.
     *
     * @param ticket The ticket to add
     */
    private void addTicketToLogbook(Ticket ticket) {
        this.logbook.add(ticket);
        if (this.lastSeenRounds.contains(this.logbook.size())) {
            this.lastSeen = this.getCurrentStation();
            this.lastSeenRound = this.logbook.size();
        }
    }

    @Override
    public boolean isMisterX() {
        return true;
    }

}
