package logic.player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import java.util.Set;
import java.util.function.BinaryOperator;
import logic.GameLogic.Config;
import logic.util.Logger;
import logic.Move;
import logic.Ticket;
import logic.board.Station;

/**
 * Represents a detective in the game. Subclass of @see Player.
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class Detective extends Player {

    /**
     * Reduces all station by their id. Only the station with the smallest id will be left.
     */
    private static final BinaryOperator<Station> filterStationsBySmallestId =
            (Station result, Station nextStation) -> {
                return result.getIdentifier() > nextStation.getIdentifier() ? nextStation : result;
            };

    /**
     * Gets the possible stations where misterX could be.
     *
     * @param logbook The logbook of MisterX
     * @param lastStation The last station where MisterX showed up
     * @return The possible stations where MisterX could be
     */
    public static Set<Station> getPossibleTargetPositions(LinkedList<Ticket> logbook,
            Station lastStation) {
        return Detective.getPossibleTargetPositions(logbook, lastStation, new HashSet<>());
    }

    /**
     * Gets the possible stations where misterX could be.
     *
     * @param logbook The logbook of MisterX
     * @param lastStation The last station where MisterX showed up
     * @param targetStations The possible stations where misterX could be
     * @return The Stations
     */
    private static Set<Station> getPossibleTargetPositions(LinkedList<Ticket> logbook,
            Station lastStation, Set<Station> targetStations) {

        if (lastStation == null) {
            return new HashSet<>();
        } else if (logbook.isEmpty()) {
            return new HashSet<>(Arrays.asList(lastStation));
        }

        // Get each Ticket from logbook and calculate reachable stations
        Ticket ticket = logbook.pollFirst();
        Set<Station> stations = lastStation.getStationsReachableBy(ticket);
        stations.forEach((station) -> {
            targetStations.addAll(
                    getPossibleTargetPositions(new LinkedList<>(logbook), station, targetStations));
        });
        return targetStations;
    }

    /**
     * Constructor.
     *
     * @param id The id
     * @param startStation The start station
     * @param ai If the detective should be controlled by AI or not
     * @param cabTickets Amount of available cab tickets
     * @param busTickets Amount of available bus tickets
     * @param tubeTickets Amount of available tube tickets
     */
    public Detective(int id, Station startStation, boolean ai, int cabTickets, int busTickets,
            int tubeTickets) {
        super(id, startStation, ai, cabTickets, busTickets, tubeTickets, 0);
    }

    /**
     * Constructor. The ticket amount will be get by the global game config.
     *
     * @param id The id
     * @param startStation the start station
     * @param ai If the detective should be controlled by AI or not
     */
    public Detective(int id, Station startStation, boolean ai) {
        this(id, startStation, ai, Config.TICKET_CAB, Config.TICKET_BUS, Config.TICKET_TUBE);
    }

    @Override
    public TacticResult play(Station lastSeen, Set<Station> targets, Station averageStation,
            List<Detective> detectives) {

        List<Move> moves = new LinkedList<>();
        // Get a move from each tactic
        moves.add(this.getMoveToPossibleTargetPosition(targets));
        moves.add(this.getMoveToDirectTubeStation());
        moves.add(this.getMoveInDirectionOfLastseenPosition(lastSeen));
        moves.add(this.getMoveToDirectReachableStation());

        TacticResult result = null;
        // Get the ranking for each move. Return only the best ranked move.
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            if (move != null) {
                float ranking = this.getRankingForMove(move, targets, averageStation, detectives);
                if (result == null || result.getRanking() < ranking) {
                    result = new TacticResult(i + 1, move, ranking);
                }
            }
        }
        return result;
    }

    // TACTICS #################################################################
    /**
     * Gets the move to one possible target positions. If multiple stations are found it will choose
     * the stations with the smallest ID with ticket of highest amount. Tactic id 1.
     *
     * @param targets The possible target positions
     * @return the Move or null if no target position is possible.
     */
    public Move getMoveToPossibleTargetPosition(Set<Station> targets) {

        Set<Station> reachableStation = this.getReachableStations();
        reachableStation.containsAll(targets);
        Optional<Station> station = reachableStation.stream().reduce(filterStationsBySmallestId);
        return this.getMoveToNearestStationByBestTicket(station);
    }

    /**
     * Get the move to nearest tube station.If multiple stations are found it will choose the
     * stations with the smallest ID with ticket of highest amount. Tactic id 2.
     *
     * @return the move or null if no tube station is reachable.
     */
    public Move getMoveToDirectTubeStation() {

        Optional<Station> station = this.getReachableStations().stream().filter(
                reachAbleStation -> !reachAbleStation.getStationsReachableBy(Ticket.TUBE).isEmpty())
                .reduce(Detective.filterStationsBySmallestId);
        return this.getMoveToNearestStationByBestTicket(station);
    }

    /**
     * Get the move to Direction if last seen position of MisterX, If multiple stations are found it
     * will choose the stations with the smallest ID with ticket of highest amount. Tactic id 3.
     *
     * @param lastSeen the Station as destination
     * @return The Move into the direction
     */
    public Move getMoveInDirectionOfLastseenPosition(Station lastSeen) {
        if (lastSeen == null) {
            return null;
        }
        List<Station> way = this.getShortestWay(lastSeen);
        return way.isEmpty() ? null
                : this.getMoveToNearestStationByBestTicket(Optional.of(way.get(0)));
    }

    /**
     * Gets the move to the direct reachable station. If multiple stations are found it will choose
     * the stations with the smallest ID with ticket of highest amount. Tactic id 4.
     *
     * @return the move or null if no station is reachable.
     */
    public Move getMoveToDirectReachableStation() {
        Optional<Station> station =
                this.getReachableStations().stream().reduce(Detective.filterStationsBySmallestId);

        return this.getMoveToNearestStationByBestTicket(station);
    }

    // RANKING #################################################################
    /**
     * Gets the ranking for a Move.
     *
     * @param move The move to rank
     * @param targets The possible positions of MisterX
     * @param averageStation The average of possible positions of MisterX
     * @param detectives All detectives
     * @return The ranking for the move
     */
    private float getRankingForMove(Move move, Set<Station> targets, Station averageStation,
            List<Detective> detectives) {
        Station current = this.getCurrentStation();
        // If no Move is possible, rank ist as worst possible
        if (move == null) {
            return Float.MIN_VALUE;
        }
        // Do the move
        this.move(move);
        // Rank by all possible ranking methods
        float reachbaleTargetPositionsRanking =
                this.getRankingByReachableTargetStations(targets, detectives);
        float distanceRanking = this.getRankingByDistanceToStation(averageStation);
        float directylReachableRanking = this.getRankingByDirectlyReachableStations();
        float ticketAmountRanking = this.getRankingBySmallestTicketAmount();
        // Undo the move
        this.setCurrentStation(current);
        this.addTicket(move.getTicket());

        return reachbaleTargetPositionsRanking + distanceRanking + directylReachableRanking
                + ticketAmountRanking;

    }

    /**
     * Gets the ranking by reachable target stations. Ranking: Amount of reachable target stations
     * divided by amount of target stations multiplied by 10.
     *
     * @param targets All possible target stations
     * @param detectives All detectives
     * @return the ranking
     */
    @Override
    public float getRankingByReachableTargetStations(Set<Station> targets,
            List<Detective> detectives) {
        if (targets.isEmpty()) {
            return 0;
        }

        Set<Station> reachableStations = Player.getReachableStationsForAllPlayers(detectives);

        reachableStations.retainAll(targets);
        float targetCount = targets.size();
        float reachableTargetCount = reachableStations.size();
        return reachableTargetCount / targetCount * 10;
    }

    /**
     * Gets the ranking by calculating the distance to a station. Ranking: 10 - "Number of stations
     * to destination".
     *
     * @param destination The station
     * @return The ranking by distance to the station
     */
    public int getRankingByDistanceToStation(Station destination) {
        if (destination == null) {
            return 0;
        }
        int distance = this.getShortestDistance(this.getCurrentStation(), destination);
        return distance >= 10 ? 0 : 10 - distance;
    }

    /**
     * Helper for @see getRankingByDistanceToStation. Gets the needed steps from start to
     * destination. Limits the Tickets to ten.
     *
     * @param start The start station
     * @param destination The destination station
     * @return the steps needed from start to destination
     */
    private int getShortestDistance(Station start, Station destination) {
        int[] tickets = new int[Ticket.values().length];
        tickets[Ticket.CAB.ordinal()] = 10;
        tickets[Ticket.BUS.ordinal()] = 10;
        tickets[Ticket.TUBE.ordinal()] = 10;
        tickets[Ticket.BLACK.ordinal()] = 0;

        return Player.getShortestWay(start, destination, tickets).size();
    }

    /**
     * Gets the ranking of smallest ticket amount by ignoring the black tickets. Ranking: If more
     * then 2 Tickets available = 3. if less = amount of Tickets.
     *
     * @return the ranking
     */
    @Override
    public int getRankingBySmallestTicketAmount() {
        int ranking = 3;
        for (Ticket ticket : Ticket.values()) {
            int ticketNum = this.getTicketNum(ticket);
            if (ticket != Ticket.BLACK && ticketNum <= 2 && ticketNum < ranking) {
                ranking = ticketNum;
            }
        }
        return ranking;

    }

    @Override
    public boolean isMisterX() {
        return false;
    }

}
