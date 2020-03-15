package logic.board;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import logic.util.JsonValidator;

/**
 * Represents the logical gameboard that holds all Stations.
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public final class Board {

    private final List<Station> stations;

    /**
     * Constructor.
     *
     * @param jsonNetwork The board in JSON notation
     * @throws IllegalArgumentException if some invalid values has been detected
     * @throws JsonSyntaxException if the JSON is corrupted
     */
    public Board(Reader jsonNetwork) throws IllegalArgumentException, JsonSyntaxException {

        this.stations = new ArrayList<>();

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        // Parse the Network
        JsonElement root = parser.parse(jsonNetwork);
        // Validate Network
        if (!JsonValidator.validateBoard(root)) {
            throw new IllegalArgumentException("Board is corrupted. Please try antoher one.");
        }
        // Get the "stations" field and iterate over it
        JsonArray jsonStations = root.getAsJsonObject().getAsJsonArray("stations");
        for (JsonElement jsonArrayElement : jsonStations) {
            JsonObject jsonStation = jsonArrayElement.getAsJsonObject();
            int id = jsonStation.get("identifier").getAsInt();
            Position position = gson.fromJson(jsonStation.get("position"), Position.class);
            // Create a Station and add it to the List
            this.stations.add(new Station(id, position));
        }

        // Link the created Stations
        int idx = 0;
        for (JsonElement jArrayElement : jsonStations) {
            JsonObject jStation = jArrayElement.getAsJsonObject();
            Station station = this.getStation(idx + 1);

            // Get Links between Stations
            JsonArray jCabs = jStation.get("cab").getAsJsonArray();
            JsonArray jBuss = jStation.get("bus").getAsJsonArray();
            JsonArray jTubes = jStation.get("tube").getAsJsonArray();
            JsonArray jBoats = jStation.get("boat").getAsJsonArray();

            // Set CAB Link
            for (JsonElement jCab : jCabs) {
                int stationId = jCab.getAsInt();
                Station cabStation = this.getStation(stationId);
                station.addCab(cabStation);
            }
            // Set BUS Link
            for (JsonElement jBus : jBuss) {
                int stationId = jBus.getAsInt();
                Station busStation = this.getStation(stationId);
                station.addBus(busStation);
            }
            // Set TUBE Link
            for (JsonElement jTube : jTubes) {
                int stationId = jTube.getAsInt();
                Station tubeStation = this.getStation(stationId);
                station.addTube(tubeStation);
            }
            // Set BAOT Link
            for (JsonElement jBoat : jBoats) {
                int stationId = jBoat.getAsInt();
                Station boatStation = this.getStation(stationId);
                station.addBoat(boatStation);
            }
            idx++;
        }
    }

    /**
     * Gets the nearest Station to a point.
     *
     * @param point The Point on the graphical board
     * @param threshold The threshold to the nearest station
     * @return The nearest Station
     */
    public Station getNearestStation(Position point, double threshold) {
        double distance = Double.MAX_VALUE;
        Station nearestStation = null;
        for (Station currentStation : this.stations) {
            double currentDistance = this.getDistance(point, currentStation.getPosition());
            if (currentDistance <= threshold && distance > currentDistance) {
                distance = currentDistance;
                nearestStation = currentStation;
            }
        }
        return nearestStation;
    }

    /**
     * Helper for @see getNearestStation. Calculates the distance between two positions
     *
     * @param a Position A
     * @param b Position B
     * @return the Distance between Position a and Position b
     */
    private double getDistance(Position a, Position b) {
        return Math.sqrt(Math.pow((a.getX() - b.getX()), 2d) + Math.pow((a.getY() - b.getY()), 2d));
    }

    // Getter ##################################################################
    /**
     * Returns the station corresponding to the given id.
     *
     * @param id The id of the Station to get
     * @return The station corresponding to the id
     */
    public Station getStation(int id) {
        if (id > this.stations.size() || id <= 0) {
            throw new IllegalArgumentException(String.format("Station \"%d\" does not exists", id));
        }
        return this.stations.get(id - 1);
    }

    /**
     * Gets the average station position of an List
     *
     * @param stations The stations
     * @return The station that is the average station of all
     */
    public Station getAverageStation(Set<Station> stations) {
        double x = 0d;
        double y = 0d;

        for (Station station : stations) {
            x += station.getPosition().getX();
            y += station.getPosition().getY();
        }
        Position position = new Position(x / stations.size(), y / stations.size());
        return this.getNearestStation(position, Double.MAX_VALUE);

    }
}
