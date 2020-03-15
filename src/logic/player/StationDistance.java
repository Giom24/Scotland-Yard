package logic.player;

import logic.board.Station;

/**
 * Represents a distance between a
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class StationDistance {

    private Station station;
    private int distance;

    /**
     * Constructor.
     *
     * @param station The Station
     * @param distance The Distance
     */
    public StationDistance(Station station, int distance) {
        this.station = station;
        this.distance = distance;
    }

    /**
     * Getter for the station.
     *
     * @return The station
     */
    public Station getStation() {
        return station;
    }

    /**
     * Setter for the station.
     *
     * @param station The station to set
     */
    public void setStation(Station station) {
        this.station = station;
    }

    /**
     * Getter for the distance.
     *
     * @return The distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Setter for the distance.
     *
     * @param distance The distance to set
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

}
