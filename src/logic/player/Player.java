package logic.player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import logic.GameLogic.Config;
import logic.Move;
import logic.Ticket;
import logic.board.Station;

/**
 * Represents a player in the game.
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public abstract class Player {

    private final int[] tickets;
    private Station currentStation;
    private final boolean ai;
    private final int id;

    /**
     * Returns the the Ticket that has the lowest quantity left.
     */
    protected final BinaryOperator<Ticket> filterTicketsByQuantityAndValue =
            (Ticket ticket, Ticket nextTicket) -> {
                int ticketNum = this.getTicketNum(ticket);
                int nextTicketNum = this.getTicketNum(nextTicket);
                if (ticketNum > nextTicketNum) {
                    return ticket;
                } else if (ticketNum < nextTicketNum) {
                    return nextTicket;
                } else {
                    return ticket.compareTo(nextTicket) <= 0 ? ticket : nextTicket;
                }
            };

    /**
     * Constructor.
     *
     * @param id The id
     * @param startStation The start station
     * @param ai If this player should be controlled by AI or not
     * @param cabTickets Amount of available cab tickets
     * @param busTickets Amount of available bus tickets
     * @param tubeTickets Amount of available tube tickets
     * @param blackTickets Amount of available black tickets
     */
    public Player(int id, Station startStation, boolean ai, int cabTickets, int busTickets,
            int tubeTickets, int blackTickets) {
        this.id = id;
        this.ai = ai;
        this.tickets = new int[Ticket.values().length];
        this.tickets[Ticket.CAB.ordinal()] = cabTickets;
        this.tickets[Ticket.BUS.ordinal()] = busTickets;
        this.tickets[Ticket.TUBE.ordinal()] = tubeTickets;
        this.tickets[Ticket.BLACK.ordinal()] = blackTickets;
        this.setCurrentStation(startStation);
    }

    /**
     * Returns a Move to a reachable Station. If there are multiple possibilities to reach a
     * station, the Ticket with with the lowest quantity will be used (@see
     * filterTicketsByQuantity).
     *
     *
     *
     * @param station The Station that should be reached
     * @return The move to the Station or null if not present
     */
    protected Move getMoveToNearestStationByBestTicket(Optional<Station> station) {
        if (!station.isPresent()) {
            return null;
        } else {
            Ticket ticket = this.getTicketFromCurrentStationTo(station.get())
                    .orElseThrow(IllegalArgumentException::new);
            return new Move(station.get(), ticket);
        }

    }

    /**
     * Moves the player.
     *
     * @param move The move that the player should do
     */
    public void move(Move move) {
        this.setCurrentStation(move.getTo());
        this.removeTicket(move.getTicket());
    }

    /**
     * Returns all stations that are reachable by all players. Reachable means that there are enough
     * tickets left and that the destination station is not occupied.
     *
     * @param <T>
     * @param players All players
     * @return A set of all reachable stations by all players
     */
    public static <T extends Player> Set<Station>
            getReachableStationsForAllPlayers(List<T> players) {
        return players.stream().map(player -> player.getReachableStations())
                .flatMap(Collection::stream).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns all stations that are reachable. Reachable means that there are enough tickets left
     * and that the destination station is not occupied.
     *
     * @return A set of all reachable stations
     */
    public Set<Station> getReachableStations() {
        return Arrays.stream(Ticket.values()).filter(ticket -> this.getTicketNum(ticket) > 0)
                .map(ticket -> this.currentStation.getStationsReachableBy(ticket))
                .flatMap(Collection::stream).distinct().filter(station -> !station.isOccupied())
                .collect(Collectors.toCollection(HashSet::new));
    }

    // PATHFINDING #############################################################
    /**
     * Returns the shortest way from current station to destination considering available tickets by
     * this player and occupied stations on the board.
     *
     * @param destination The destination to reach
     * @return A way represented as list of stations
     */
    public List<Station> getShortestWay(Station destination) {
        return Player.getShortestWay(this.getCurrentStation(), destination, this.tickets.clone());
    }

    /**
     * Returns the shortest way from start to destination considering given tickets and occupied
     * stations on the board.
     *
     * @param start The start station
     * @param destination The destination to reach
     * @param tickets The tickets to take into account
     * @return A way represented as list of stations
     */
    protected static List<Station> getShortestWay(Station start, Station destination,
            int[] tickets) {

        // Build an empty station Graph
        StationDistance[] stations = new StationDistance[Config.BOARD_SIZE];
        // Fill the grid with distane informations
        Player.getWaysWithTickets(start, destination, 1, stations, tickets);

        // Build the shortest way out of the Graph
        LinkedList<Station> way = new LinkedList<>();
        int i;
        Station station = destination;
        way.add(destination);
        do {
            i = station.getIdentifier() - 1;
            if (stations[i] != null) {
                station = stations[i].getStation();
                way.addFirst(station);
            }
        } while (stations[i] != null && !start.equals(station));
        way.removeFirst();
        return way;
    }

    /**
     * Helper for @see getShortestWay. Calculate the shortest way from start to destination
     * considering given tickets and occupied stations on the board.
     *
     * @param current The current station (start station when beginning)
     * @param destination The destination
     * @param distance The distance (recursive step)
     * @param distances The grid of distances
     * @param tickets The tickets to consider while calculating
     */
    private static void getWaysWithTickets(Station current, Station destination, int distance,
            StationDistance[] distances, int[] tickets) {

        // If the destination is reached, Stop recursion
        if (!current.equals(destination)) {
            for (Ticket ticket : Ticket.values()) {
                Set<Station> stations = current.getStationsReachableBy(ticket);
                // Check if the Player have enough Tickets left to reach the next station
                if (!stations.isEmpty() && tickets[ticket.ordinal()] > 0) {
                    // Remove used ticket
                    tickets[ticket.ordinal()]--;
                    stations.stream()
                            // Filter if next station is already occupied
                            .filter(nextStation -> !nextStation.isOccupied())
                            .filter(nextStation -> {
                                int id = nextStation.getIdentifier() - 1;
                                // Check if next station is already in grid
                                // if yes check if the new distance is smaller
                                // -> The new calculated way is shorter
                                return distances[id] == null ? true
                                        : distances[id].getDistance() > distance;
                            })
                            // All stations left are valid stations
                            // Continue recursion with this stations
                            .forEach(nextStation -> {
                                int id = nextStation.getIdentifier() - 1;
                                distances[id] = new StationDistance(current, distance);
                                getWaysWithTickets(nextStation, destination, distance + 1,
                                        distances, tickets.clone());
                            });
                }
            }
        }
    }

    /**
     * Gets the best move for this player considering multiple tactics and rankings.
     *
     * @param lastSeen The station where MisterX has been seen the last time
     * @param targets The possible stations where MisterX could be
     * @param averageStation The average station of targets
     * @param detectives All detectives
     * @return The best move
     */
    public abstract TacticResult play(Station lastSeen, Set<Station> targets,
            Station averageStation, List<Detective> detectives);

    // RANKING #################################################################
    /**
     * Gets the ranking by reachable target stations. Ranking: differs by implementation.
     *
     * @param targets All possible target stations
     * @param detectives All detectives
     * @return the ranking
     */
    public abstract float getRankingByReachableTargetStations(Set<Station> targets,
            List<Detective> detectives);

    /**
     * Gets the ranking of directly reachable station. Ranking: reachable stations divided by 13
     * multiplied by 4.
     *
     * @return the ranking
     */
    public float getRankingByDirectlyReachableStations() {
        return this.getReachableStations().size() / 13f * 4;
    }

    /**
     * Gets the ranking of smallest ticket amount. Ranking: If more then 2 Tickets available = 3. if
     * less = amount of Tickets.
     *
     * @return the ranking
     */
    public int getRankingBySmallestTicketAmount() {
        int ranking = 3;
        for (Ticket ticket : Ticket.values()) {
            int ticketNum = this.getTicketNum(ticket);
            if (ticketNum <= 2 && ticketNum < ranking) {
                ranking = ticketNum;
            }
        }
        return ranking;
    }

    /**
     * Gets all available tickets.
     *
     * @return all available tickets
     */
    public Set<Ticket> getAvailableTickets() {
        Set<Ticket> availableTickets = new HashSet<>();
        for (Ticket ticket : Ticket.values()) {
            if (this.getTicketNum(ticket) > 0) {
                availableTickets.add(ticket);
            }
        }

        return availableTickets;
    }

    // SETTER ##################################################################
    /**
     * Sets the current station. Also sets the new station as occupied and frees the old one.
     *
     * @param station The new station
     */
    public final void setCurrentStation(Station station) {

        if (station.isOccupied()) {
            throw new IllegalStateException();
        }

        if (this.currentStation != null) {
            this.currentStation.setOccupied(false);
        }

        this.currentStation = station;

        if (!this.isMisterX()) {
            this.currentStation.setOccupied(true);
        }
    }

    /**
     * Removes a ticket from this player.
     *
     * @param ticket The ticket to remove
     */
    public void removeTicket(Ticket ticket) {
        if (this.tickets[ticket.ordinal()] <= 0) {
            throw new IllegalArgumentException();
        }
        this.tickets[ticket.ordinal()] -= 1;
    }

    /**
     * Adds a ticket to this player.
     *
     * @param ticket The ticket to add
     */
    public void addTicket(Ticket ticket) {
        this.tickets[ticket.ordinal()] += 1;
    }

    // GETTER ##################################################################
    protected Optional<Ticket> getTicketFromCurrentStationTo(Station station) {
        return this.getCurrentStation().getTicketsToReachableStation(station).stream()
                .reduce(this.filterTicketsByQuantityAndValue);
    }

    /**
     * Is this player controlled by AI.
     *
     * @return if this player is controlled by AI
     */
    public boolean isAi() {
        return this.ai;
    }

    /**
     * Is this player MisterX.
     *
     * @return if this player is MisterX
     */
    public abstract boolean isMisterX();

    /**
     * Gets the current station.
     *
     * @return The current station
     */
    public Station getCurrentStation() {
        return this.currentStation;
    }

    /**
     * Gets the amount of a ticket.
     *
     * @param ticket The ticket
     * @return The amount of tickets left
     */
    public int getTicketNum(Ticket ticket) {
        return this.tickets[ticket.ordinal()];
    }

    /**
     * Gets all tickets.
     *
     * @return All tickets
     */
    public int[] getTickets() {
        return this.tickets;
    }

    /**
     * Gets the id of this player.
     *
     * @return The id
     */
    public int getId() {
        return this.id;
    }
}
