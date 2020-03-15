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
public class DetectiveRankingTest {

    private static Board board;

    @Before
    public void readMap() {
        File f = new File("test/data/network.json");
        try {
            Reader reader = new FileReader(f);
            DetectiveRankingTest.board = new Board(reader);
        } catch (FileNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    // ##########################################################################
    @Test
    public void
            getRankingByReachableTargetpositions_TwoDetectives_NotReachable_NoTickets_Ranking_0() {
        Set<Station> targets = new HashSet<>();
        targets.add(board.getStation(32));

        Detective detective0 = new Detective(0, board.getStation(8), true, 0, 0, 0);
        Detective detective1 = new Detective(0, board.getStation(45), true, 0, 0, 0);

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detective0);
        detectives.add(detective1);

        Assert.assertEquals(0f, detective0.getRankingByReachableTargetStations(targets, detectives),
                0.001);
    }

    @Test
    public void getRankingByReachableTargetpositions_TwoDetectives_OneRechable() {
        Set<Station> targets = new HashSet<>();
        targets.add(board.getStation(8));
        targets.add(board.getStation(9));
        targets.add(board.getStation(19));

        Detective detective0 = new Detective(0, board.getStation(20), true, 1, 0, 0);
        Detective detective1 = new Detective(0, board.getStation(18), true, 0, 0, 0);

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detective0);
        detectives.add(detective1);
        Assert.assertEquals(1f / 3 * 10,
                detective0.getRankingByReachableTargetStations(targets, detectives), 0.001);
    }

    @Test
    public void getRankingByReachableTargetpositions_TwoDetectives_TwoReachable() {
        Set<Station> targets = new HashSet<>();
        targets.add(board.getStation(8));
        targets.add(board.getStation(9));
        targets.add(board.getStation(19));

        Detective detective0 = new Detective(0, board.getStation(20), true, 1, 0, 0);
        Detective detective1 = new Detective(0, board.getStation(18), true, 2, 0, 0);

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detective0);
        detectives.add(detective1);

        Assert.assertEquals(2f / 3 * 10,
                detective0.getRankingByReachableTargetStations(targets, detectives), 0.001);
    }

    // ##########################################################################
    @Test
    public void getRankingByDistanceToAvarageTargetPosition_DirectlyReachable() {
        Station lastSeen = board.getStation(8);
        Detective detective0 = new Detective(0, board.getStation(1), true, 0, 0, 0);
        Assert.assertEquals(9, detective0.getRankingByDistanceToStation(lastSeen));
    }

    @Test
    public void getRankingByDistanceToAvarageTargetPosition_DirectlyReachable2() {
        Station lastSeen = board.getStation(8);
        Detective detective0 = new Detective(0, board.getStation(1), true, 0, 0, 0);

        Assert.assertEquals(9, detective0.getRankingByDistanceToStation(lastSeen));
    }

    // ##########################################################################
    @Test
    public void getRankingByDirectlyReachableStations_NoTickets() {
        Detective detective0 = new Detective(0, board.getStation(1), true, 0, 0, 0);

        Assert.assertEquals(0f, detective0.getRankingByDirectlyReachableStations(), 0.001);
    }

    @Test
    public void getRankingByDirectlyReachableStations_OneBlocked_OneDoubleReachable() {
        Detective detective0 = new Detective(0, board.getStation(1), true, 1, 1, 1);
        Detective detective1 = new Detective(0, board.getStation(9), true, 1, 1, 1);

        Assert.assertEquals(3f / 13 * 4, detective0.getRankingByDirectlyReachableStations(), 0.001);
    }

    // ##########################################################################
    @Test
    public void getRankingByReachableTargetpositions_ExampleBlue() {

        MisterX mrx = new MisterX(board.getStation(116), null, null, true, 1, 0, 0, 0);
        Detective detectiveBlue = new Detective(0, board.getStation(118), true, 10, 10, 10);

        Detective detectiveRed = new Detective(0, board.getStation(71), true, 10, 10, 10);
        Detective detectiveYellow = new Detective(0, board.getStation(186), true, 10, 10, 10);

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detectiveBlue);
        detectives.add(detectiveRed);
        detectives.add(detectiveYellow);

        Set<Station> targets = new HashSet<>();
        targets.add(board.getStation(104));
        targets.add(board.getStation(117));
        targets.add(board.getStation(118));
        targets.add(board.getStation(127));

        Assert.assertEquals(0f,
                detectiveBlue.getRankingByReachableTargetStations(targets, detectives), 0.001);

    }

    @Test
    public void getRankingByDistanceToAvarageTargetPosition_ExampleBlue() {

        MisterX mrx = new MisterX(board.getStation(116), null, null, true, 1, 0, 0, 0);
        Detective detectiveBlue = new Detective(0, board.getStation(134), true, 10, 10, 10);
        Detective detectiveRed = new Detective(0, board.getStation(71), true, 10, 10, 10);
        Detective detectiveYellow = new Detective(0, board.getStation(186), true, 10, 10, 10);

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detectiveBlue);
        detectives.add(detectiveRed);
        detectives.add(detectiveYellow);

        Set<Station> targets = new HashSet<>();
        targets.add(board.getStation(104));
        targets.add(board.getStation(117));
        targets.add(board.getStation(118));
        targets.add(board.getStation(127));

        Station destination = board.getAverageStation(targets);

        Assert.assertEquals(8, detectiveBlue.getRankingByDistanceToStation(destination));

    }

    @Test
    public void getRankingByDirectlyReachableStations_ExampleBlue() {

        MisterX mrx = new MisterX(board.getStation(116), null, null, true, 1, 0, 0, 0);
        Detective detectiveBlue = new Detective(1, board.getStation(118), true, 3, 4, 3);
        Detective detectiveRed = new Detective(2, board.getStation(71), true, 10, 10, 10);
        Detective detectiveYellow = new Detective(3, board.getStation(186), true, 10, 10, 10);

        Assert.assertEquals(1.23f, detectiveBlue.getRankingByDirectlyReachableStations(), 0.001);

    }

    @Test
    public void getRankingBySmallestTicketAmount() {

        MisterX mrx = new MisterX(board.getStation(116), null, null, true, 1, 0, 0, 0);
        Detective detective0 = new Detective(0, board.getStation(118), true, 3, 4, 3);
        Detective detective1 = new Detective(0, board.getStation(71), true, 1, 10, 10);
        Detective detective2 = new Detective(0, board.getStation(186), true, 10, 10, 10);

        Assert.assertEquals(0, mrx.getRankingBySmallestTicketAmount());
        Assert.assertEquals(3, detective0.getRankingBySmallestTicketAmount());
        Assert.assertEquals(1, detective1.getRankingBySmallestTicketAmount());
        Assert.assertEquals(3, detective2.getRankingBySmallestTicketAmount());

    }

}
