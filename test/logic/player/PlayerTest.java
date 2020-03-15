/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import logic.board.Board;
import logic.board.Station;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author giom
 */
public class PlayerTest {

    private Board board;

    @Before
    public void readMap() {
        File f = new File("test/data/network.json");
        try {
            Reader reader = new FileReader(f);
            this.board = new Board(reader);
        } catch (FileNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    // ##########################################################################
    @Test
    public void getReachableStationsForAllPlayers_NoTickets() {

        Detective detective0 = new Detective(0, board.getStation(1), true, 0, 0, 0);
        Detective detective1 = new Detective(0, board.getStation(10), true, 0, 0, 0);

        List<Detective> players = new LinkedList<>();
        players.add(detective0);
        players.add(detective1);

        Set<Station> stations = new HashSet<>();
        Assert.assertEquals(stations, Player.getReachableStationsForAllPlayers(players));
    }

    @Test
    public void getReachableStationsForAllPlayers_Ticket_10_10_10() {

        Detective detective0 = new Detective(0, board.getStation(19), true, 10, 10, 10);
        Detective detective1 = new Detective(0, board.getStation(44), true, 10, 10, 10);

        List<Detective> players = new LinkedList<>();
        players.add(detective0);
        players.add(detective1);

        Set<Station> stations = new HashSet<>();
        stations.add(board.getStation(8));
        stations.add(board.getStation(9));
        stations.add(board.getStation(31));
        stations.add(board.getStation(32));
        stations.add(board.getStation(58));
        Assert.assertEquals(stations, Player.getReachableStationsForAllPlayers(players));
    }

    @Test
    public void getReachableStationsForAllPlayers_Ticket_10_10_10_Blocked() {

        Detective detective0 = new Detective(0, board.getStation(8), true, 10, 10, 10);
        Detective detective1 = new Detective(0, board.getStation(19), true, 10, 10, 10);

        List<Detective> players = new LinkedList<>();
        players.add(detective0);
        players.add(detective1);

        Set<Station> stations = new HashSet<>();
        stations.add(board.getStation(1));
        stations.add(board.getStation(9));
        stations.add(board.getStation(18));
        stations.add(board.getStation(32));
        Assert.assertEquals(stations, Player.getReachableStationsForAllPlayers(players));
    }

    // ##########################################################################
    @Test
    public void getShortestWay_Ticket_2_0_0_Start_1_Destination_20() {

        Detective detective0 = new Detective(0, board.getStation(1), true, 2, 0, 0);
        List<Station> way = detective0.getShortestWay(board.getStation(20));
        List<Station> result = new LinkedList<>();
        result.add(board.getStation(9));
        result.add(board.getStation(20));
        Assert.assertEquals(result, way);

    }

    @Test
    public void getShortestWay_Ticket_10_0_5_Start_1_Destination_46() {

        Detective detective0 = new Detective(0, board.getStation(1), true, 10, 0, 5);
        List<Station> way = detective0.getShortestWay(board.getStation(46));
        List<Station> result = new LinkedList<>();
        result.add(board.getStation(46));
        Assert.assertEquals(result, way);

    }

    @Test
    public void getShortestWay_Ticket_10_0_5_Start_1_Destination_74() {

        Detective detective0 = new Detective(0, board.getStation(1), true, 10, 0, 5);

        List<Station> way = detective0.getShortestWay(board.getStation(74));
        List<Station> result = new LinkedList<>();
        result.add(board.getStation(46));
        result.add(board.getStation(74));
        Assert.assertEquals(result, way);

    }

    @Test
    public void getShortestWay_Ticket_10_0_5_Start_1_Destination_45() {

        Detective detective0 = new Detective(0, board.getStation(1), true, 10, 0, 5);

        List<Station> way = detective0.getShortestWay(board.getStation(45));
        List<Station> result = new LinkedList<>();
        result.add(board.getStation(46));
        result.add(board.getStation(45));
        Assert.assertEquals(result, way);
    }

    @Test
    public void getShortestWay_Ticket_10_0_5_Start_1_Destination_45_Blocked_8_9() {

        Detective detective0 = new Detective(1, board.getStation(1), true, 10, 0, 5);
        Detective detective1 = new Detective(2, board.getStation(8), true, 0, 0, 0);
        Detective detective2 = new Detective(3, board.getStation(9), true, 0, 0, 0);

        List<Station> way = detective0.getShortestWay(board.getStation(45));
        List<Station> result = new LinkedList<>();
        result.add(board.getStation(46));
        result.add(board.getStation(45));
        Assert.assertEquals(result, way);

    }

    @Test
    public void getShortestWay_Ticket_5_5_5_Blocked() {

        Detective detective0 = new Detective(1, board.getStation(6), true, 5, 5, 5);
        Detective detective2 = new Detective(2, board.getStation(7), true, 0, 0, 0);
        Detective detective3 = new Detective(3, board.getStation(29), true, 0, 0, 0);

        List<Station> way = detective0.getShortestWay(board.getStation(17));
        List<Station> result = new LinkedList<>();
        Assert.assertEquals(result, way);

    }

    // ##########################################################################
}
