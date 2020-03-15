/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.board;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import logic.Ticket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author giom
 */
public class StationTest {

    private static Board board;

    @Before
    public void readMap() {
        File f = new File("test/data/network.json");
        try {
            Reader reader = new FileReader(f);
            StationTest.board = new Board(reader);
        } catch (FileNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void getStationReachableBy_Station1() {
        Station station1 = board.getStation(1);

        Set<Station> resultCab = new HashSet<>();
        resultCab.add(board.getStation(8));
        resultCab.add(board.getStation(9));

        Set<Station> resultBus = new HashSet<>();
        resultBus.add(board.getStation(46));
        resultBus.add(board.getStation(58));

        Set<Station> resultTube = new HashSet<>();
        resultTube.add(board.getStation(46));

        Set<Station> resultBlack = new HashSet<>();
        resultBlack.addAll(resultCab);
        resultBlack.addAll(resultBus);
        resultBlack.addAll(resultTube);

        Assert.assertEquals(resultCab, station1.getStationsReachableBy(Ticket.CAB));
        Assert.assertEquals(resultBus, station1.getStationsReachableBy(Ticket.BUS));
        Assert.assertEquals(resultTube, station1.getStationsReachableBy(Ticket.TUBE));
        Assert.assertEquals(resultBlack, station1.getStationsReachableBy(Ticket.BLACK));
    }

    @Test
    public void getSurroundingStations() {
        Station station1 = board.getStation(1);
        Set<Station> result = new HashSet<>();
        result.add(board.getStation(8));
        result.add(board.getStation(9));
        result.add(board.getStation(46));
        result.add(board.getStation(58));
        result.add(board.getStation(46));
        Assert.assertEquals(result, station1.getSurroundingStations());
    }

    @Test
    public void getTicketsToReachableStation_notReachable() {
        Station station1 = board.getStation(1);
        Set<Ticket> result = new HashSet<>();
        Assert.assertEquals(result, station1.getTicketsToReachableStation(board.getStation(2)));
    }

    @Test
    public void getTicketsToReachableStation_TwoKind() {
        Station station1 = board.getStation(1);
        Set<Ticket> result = new HashSet<>();
        result.add(Ticket.BUS);
        result.add(Ticket.TUBE);
        result.add(Ticket.BLACK);
        Assert.assertEquals(result, station1.getTicketsToReachableStation(board.getStation(46)));
    }

    @Test
    public void getTicketsToReachableStation() {
        Station station1 = board.getStation(108);
        Set<Ticket> result = new HashSet<>();
        result.add(Ticket.BLACK);
        Assert.assertEquals(result, station1.getTicketsToReachableStation(board.getStation(115)));
    }
}
