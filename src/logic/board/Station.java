package logic.board;

import java.util.HashSet;
import java.util.Set;
import logic.Ticket;

/**
 * Represents a station on the logical gameboard. Holds all reachable stations.
 *
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class Station {

    private final int id;
    private final Position position;
    private boolean occupied;

    private final Set<Station> cab;
    private final Set<Station> bus;
    private final Set<Station> tube;
    private final Set<Station> boat;

    /**
     * Construct an Station without any connections.
     *
     * @param id The id of the station
     * @param position The position on the graphical board
     */
    public Station(int id, Position position) {
        this.id = id;
        this.position = position;
        this.cab = new HashSet<>();
        this.bus = new HashSet<>();
        this.tube = new HashSet<>();
        this.boat = new HashSet<>();
        this.occupied = false;
    }

    // Setter ##################################################################
    /**
     * Sets the occupied status.
     *
     * @param state The state to set
     */
    public void setOccupied(boolean state) {
        if (this.isOccupied() && state) {
            throw new IllegalStateException("Station is already Occupied");
        }
        this.occupied = state;
    }

    /**
     * Adds an Cab connection.
     *
     * @param station The station that should be connected by Cab
     */
    public void addCab(Station station) {
        this.cab.add(station);
    }

    /**
     * Adds an Bus connection.
     *
     * @param station The station that should be connected by Bus
     */
    public void addBus(Station station) {
        this.bus.add(station);
    }

    /**
     * Adds an Tube connection.
     *
     * @param station The station that should be connected by Tube
     */
    public void addTube(Station station) {
        this.tube.add(station);
    }

    /**
     * Adds an Boat connection.
     *
     * @param station The station that should be connected by Boat
     */
    public void addBoat(Station station) {
        this.boat.add(station);
    }

    // Getter ##################################################################
    /**
     * Getter for the id.
     *
     * @return The id
     */
    public int getIdentifier() {
        return id;
    }

    /**
     * Getter for the position.
     *
     * @return The position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Returns the occupied status.
     *
     * @return True if station is currently occupied
     */
    public boolean isOccupied() {
        return this.occupied;
    }

    /**
     * Returns all surrounding stations.
     *
     * @return The surrounding stations
     */
    public Set<Station> getSurroundingStations() {
        return this.getStationsReachableBy(Ticket.BLACK);
    }

    /**
     * Returns all tickets that could be used to reach the station.
     *
     * @param station The station to reach
     * @return All ticket to reach station or an empty Set if station is not reachable
     */
    public Set<Ticket> getTicketsToReachableStation(Station station) {

        Set<Ticket> tickets = new HashSet<>();

        if (this.cab.contains(station)) {
            tickets.add(Ticket.CAB);
        }
        if (this.bus.contains(station)) {
            tickets.add(Ticket.BUS);
        }
        if (this.tube.contains(station)) {
            tickets.add(Ticket.TUBE);
        }
        if (this.boat.contains(station)) {
            tickets.add(Ticket.BLACK);
        }
        if (!tickets.isEmpty()) {
            tickets.add(Ticket.BLACK);
        }

        return tickets;
    }

    /**
     * Returns all stations that are reachable by this ticket. If the blackticket is used it will
     * return all Station reachable from this station.
     *
     * @param ticket The ticket that should be used to reach a station
     * @return All station reachable by this ticket or an empty Set if no station is reachable
     * @throws IllegalArgumentException if ticket is in wrong format
     */
    public Set<Station> getStationsReachableBy(Ticket ticket) throws IllegalArgumentException {

        switch (ticket) {
            case CAB:
                return this.cab;
            case BUS:
                return this.bus;
            case TUBE:
                return this.tube;
            case BLACK:
                Set<Station> stations = new HashSet<>();
                stations.addAll(this.cab);
                stations.addAll(this.bus);
                stations.addAll(this.tube);
                stations.addAll(this.boat);
                return stations;
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Station other = (Station) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + this.id;
    }

}
