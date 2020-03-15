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
import logic.Move;
import logic.Ticket;
import logic.board.Board;
import logic.board.Station;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author giom
 */
public class DetectiveTest {

    private static Board board;

    @Before
    public void readMap() {
        File f = new File("test/data/network.json");
        try {
            Reader reader = new FileReader(f);
            DetectiveTest.board = new Board(reader);
        } catch (FileNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    // ##########################################################################
    @Test
    public void getReachableStations_NoTickets() {
        Detective detective = new Detective(0, board.getStation(1), true, 0, 0, 0);
        Set<Station> stations = new HashSet<>();
        Assert.assertEquals(stations, detective.getReachableStations());
    }

    @Test
    public void getReachableStations_Station_1_AllTickets() {
        Detective detective = new Detective(0, board.getStation(1), true, 10, 10, 10);

        Set<Station> stations = new HashSet<>();
        stations.add(board.getStation(8));
        stations.add(board.getStation(9));
        stations.add(board.getStation(46));
        stations.add(board.getStation(58));
        Assert.assertEquals(stations, detective.getReachableStations());
    }

    @Test
    public void getReachableStations_Station_1_OnlyCab() {
        Detective detective = new Detective(0, board.getStation(1), true, 10, 0, 0);
        Set<Station> stations = new HashSet<>();
        stations.add(board.getStation(8));
        stations.add(board.getStation(9));
        Assert.assertEquals(stations, detective.getReachableStations());
    }

    @Test
    public void getReachableStations_Station_1_OnlyCab_BlockedStation() {

        Detective detective0 = new Detective(0, board.getStation(134), true, 10, 0, 0);

        Set<Station> stations = new HashSet<>();
        stations.add(board.getStation(118));
        stations.add(board.getStation(127));
        stations.add(board.getStation(141));
        stations.add(board.getStation(142));

        Assert.assertEquals(stations, detective0.getReachableStations());
    }

    @Test
    public void getReachableStations_Blocked() {

        Detective detective0 = new Detective(0, board.getStation(1), true, 10, 10, 10);
        Detective detective1 = new Detective(0, board.getStation(8), true, 10, 0, 0);

        Set<Station> stations = new HashSet<>();
        stations.add(board.getStation(9));
        stations.add(board.getStation(46));
        stations.add(board.getStation(58));

        Assert.assertEquals(stations, detective0.getReachableStations());
    }

    @Test
    public void play_1() {
        Detective detectiveBlue = new Detective(0, board.getStation(134), true, 10, 10, 10);
        Detective detectiveRed = new Detective(0, board.getStation(71), true, 10, 10, 0);
        Detective detectiveYellow = new Detective(0, board.getStation(186), true, 10, 10, 10);

        LinkedList<Ticket> logbook = new LinkedList<>();
        logbook.add(Ticket.CAB);

        MisterX mrx = new MisterX(board.getStation(127), board.getStation(116), logbook, true, 1, 0,
                0, 0);
        Set<Station> targets = Detective.getPossibleTargetPositions(logbook, mrx.getLastSeen());

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detectiveBlue);
        detectives.add(detectiveRed);
        detectives.add(detectiveYellow);
        Station averageStation = board.getAverageStation(targets);

        Move moveRed = new Move(board.getStation(89), Ticket.CAB);
        Assert.assertEquals(moveRed,
                detectiveRed.play(mrx.getLastSeen(), targets, averageStation, detectives));
    }

}
