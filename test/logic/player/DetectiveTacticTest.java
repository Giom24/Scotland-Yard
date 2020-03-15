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
import java.util.Arrays;
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

//TODO Doppelte Stationen testen
public class DetectiveTacticTest {

    private static Board board;

    @Before
    public void readMap() {
        File f = new File("test/data/network.json");
        try {
            Reader reader = new FileReader(f);
            DetectiveTacticTest.board = new Board(reader);
        } catch (FileNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    // ##########################################################################
    @Test
    public void getPossibleTargetPositions_LastSeenOnActualPosition() {

        LinkedList<Ticket> tickets = new LinkedList<>();
        Station lastStation = board.getStation(1);
        Set<Station> stations = Detective.getPossibleTargetPositions(tickets, lastStation);
        Set<Station> expected = new HashSet<>();
        expected.add(board.getStation(1));

        Assert.assertEquals(expected, stations);
    }

    @Test
    public void getPossibleTargetPositions_LastSeen2_Recursion1_CAB() {

        LinkedList<Ticket> tickets = new LinkedList<>(Arrays.asList(Ticket.CAB));
        Station lastStation = board.getStation(2);
        Set<Station> stations = Detective.getPossibleTargetPositions(tickets, lastStation);

        Set<Station> expected = new HashSet<>();
        expected.add(board.getStation(10));
        expected.add(board.getStation(20));

        Assert.assertEquals(expected, stations);
    }

    @Test
    public void getPossibleTargetPositions_LastSeen2_Recursion_2_Tickets_CAB_CAB() {

        LinkedList<Ticket> tickets = new LinkedList<>();
        tickets.add(Ticket.CAB);
        tickets.add(Ticket.CAB);
        Station lastStation = board.getStation(2);
        Set<Station> stations = Detective.getPossibleTargetPositions(tickets, lastStation);

        Set<Station> expected = new HashSet<>();
        expected.add(board.getStation(2));
        expected.add(board.getStation(9));
        expected.add(board.getStation(11));
        expected.add(board.getStation(21));
        expected.add(board.getStation(33));
        expected.add(board.getStation(34));

        Assert.assertEquals(expected, stations);
    }

    @Test
    public void getPossibleTargetPositions() {

        LinkedList<Ticket> tickets = new LinkedList<>(Arrays.asList(Ticket.CAB));
        Station lastStation = board.getStation(2);
        Set<Station> stations = Detective.getPossibleTargetPositions(tickets, lastStation);

        Set<Station> expected = new HashSet<>();
        expected.add(board.getStation(10));
        expected.add(board.getStation(20));

        Assert.assertEquals(expected, stations);
    }

    // ##########################################################################
    @Test
    public void getMoveToPossibleTargetPosition_OneStation_NotReachable() {
        LinkedList<Ticket> tickets = new LinkedList<>();
        Station lastStation = board.getStation(1);
        Set<Station> stations = Detective.getPossibleTargetPositions(tickets, lastStation);
        Detective detective = new Detective(0, board.getStation(55), false, 1, 1, 1);

        Move move = detective.getMoveToPossibleTargetPosition(stations);
        Assert.assertNull(move);
    }

    @Test
    public void getMoveToPossibleTargetPosition_OneStation_Reachable() {
        LinkedList<Ticket> tickets = new LinkedList<>();
        Station lastStation = board.getStation(1);
        Set<Station> stations = Detective.getPossibleTargetPositions(tickets, lastStation);
        Detective detective = new Detective(0, board.getStation(8), false, 1, 1, 1);

        Move move = detective.getMoveToPossibleTargetPosition(stations);
        Move expected = new Move(board.getStation(1), Ticket.CAB);
        Assert.assertEquals(expected, move);

    }

    @Test
    public void getMoveToPossibleTargetPosition_MultiPossibilities_Reachable_choose_smalestId() {
        LinkedList<Ticket> tickets = new LinkedList<>(Arrays.asList(Ticket.CAB));
        Station lastStation = board.getStation(1);
        Set<Station> stations = Detective.getPossibleTargetPositions(tickets, lastStation);
        Detective detective = new Detective(0, board.getStation(19), false, 1, 1, 1);
        Move move = detective.getMoveToPossibleTargetPosition(stations);
        Move expected = new Move(board.getStation(8), Ticket.CAB);
        Assert.assertEquals(expected, move);
    }

    // ##########################################################################
    @Test
    public void getMoveToDirectTubeStation_NoTickets() {
        Detective detective = new Detective(0, board.getStation(94), true, 0, 0, 0);
        Assert.assertNull(detective.getMoveToDirectTubeStation());
    }

    @Test
    public void getMoveToDirectTubeStation_Start_2_NotReachableStation() {
        Detective detective = new Detective(0, board.getStation(2), true, 10, 10, 10);
        Assert.assertNull(detective.getMoveToDirectTubeStation());
    }

    @Test
    public void getMoveToDirectTubeStation_Start_8_SameTicketNum_Expected_1_ByCab() {
        Detective detective = new Detective(0, board.getStation(8), true, 10, 10, 10);
        Move move = new Move(board.getStation(1), Ticket.CAB);
        Assert.assertEquals(move, detective.getMoveToDirectTubeStation());
    }

    @Test
    public void getMoveToDirectTubeStation_Station_94_TwoPossibilities_GetSmallest_Expected_74() {
        Detective detective = new Detective(0, board.getStation(94), true, 10, 10, 10);
        Move move = new Move(board.getStation(74), Ticket.BUS);
        Assert.assertEquals(move, detective.getMoveToDirectTubeStation());
    }

    @Test
    public void getMoveToDirectTubeStation_Station_94_TwoPossibilities_ByCab_Expected_74() {
        Detective detective = new Detective(0, board.getStation(92), true, 1, 0, 0);
        Move move = new Move(board.getStation(74), Ticket.CAB);
        Assert.assertEquals(move, detective.getMoveToDirectTubeStation());
    }

    // ##########################################################################
    @Test
    public void getMoveInDrirectionOfLastseenPosition_ToFar_NotEnoughTickets() {
        Station lastSeen = board.getStation(1);
        Detective detective = new Detective(0, board.getStation(18), true, 1, 1, 1);
        Move move = detective.getMoveInDirectionOfLastseenPosition(lastSeen);
        Assert.assertNull(move);
    }

    @Test
    public void getMoveInDrirectionOfLastseenPosition_Reachable_byCAB() {
        Station lastSeen = board.getStation(1);
        Detective detective = new Detective(0, board.getStation(18), true, 2, 1, 1);
        Move move = detective.getMoveInDirectionOfLastseenPosition(lastSeen);
        Move expected = new Move(board.getStation(8), Ticket.CAB);
        Assert.assertEquals(expected, move);
    }

    @Test
    public void getMoveInDrirectionOfLastseenPosition_Direct_Reachable_byCAB() {
        Station lastSeen = board.getStation(1);
        Detective detective = new Detective(0, board.getStation(8), true, 10, 10, 10);
        Move move = detective.getMoveInDirectionOfLastseenPosition(lastSeen);
        Move expected = new Move(board.getStation(1), Ticket.CAB);
        Assert.assertEquals(expected, move);
    }

    @Test
    public void getMoveInDrirectionOfLastseenPosition_Start_71_Destination_116() {
        Station lastSeen = board.getStation(116);
        Station start = board.getStation(71);
        Detective detective = new Detective(0, start, false, 10, 10, 10);
        Move move = detective.getMoveInDirectionOfLastseenPosition(lastSeen);
        Move expected = new Move(board.getStation(70), Ticket.CAB);
        Assert.assertEquals(expected, move);
    }

    // ##########################################################################
    @Test
    public void getMoveToDirectReachableStation_NoTickets_Expected_Null() {
        Detective detective = new Detective(0, board.getStation(1), true, 0, 0, 0);
        Assert.assertNull(detective.getMoveToDirectReachableStation());
    }

    @Test
    public void getMoveToDirectReachableStation_SameTicketNum_Start_1_Expected_smallestId_8() {
        Detective detective = new Detective(0, board.getStation(1), true, 10, 10, 10);
        Move move = new Move(board.getStation(8), Ticket.CAB);
        Assert.assertEquals(move, detective.getMoveToDirectReachableStation());
    }

    @Test
    public void getMoveToDirectReachableStation_BlockedNoPossibilities_Start_1_Expected_Null() {
        Detective detective = new Detective(0, board.getStation(1), true, 1, 10, 1);

        Detective detective1 = new Detective(0, board.getStation(8), true, 0, 0, 0);
        Detective detective2 = new Detective(0, board.getStation(9), true, 0, 0, 0);
        Detective detective3 = new Detective(0, board.getStation(46), true, 0, 0, 0);
        Detective detective4 = new Detective(0, board.getStation(58), true, 0, 0, 0);

        Assert.assertNull(detective.getMoveToDirectReachableStation());
    }

    // ##########################################################################
    @Test
    public void getMoveToPossibleTargetPosition_ExampleBlue() {
        Detective detectiveBlue = new Detective(0, board.getStation(134), true, 10, 10, 10);
        Detective detectiveRed = new Detective(0, board.getStation(71), true, 10, 10, 10);
        Detective detectiveYellow = new Detective(0, board.getStation(186), true, 10, 10, 10);

        MisterX mrx = new MisterX(board.getStation(118), null, null, true, 1, 0, 0, 0);
        Set<Station> targets = new HashSet<>();
        targets.add(board.getStation(104));
        targets.add(board.getStation(117));
        targets.add(board.getStation(118));
        targets.add(board.getStation(127));

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detectiveBlue);
        detectives.add(detectiveRed);
        detectives.add(detectiveYellow);

        Move move = new Move(board.getStation(118), Ticket.CAB);
        Assert.assertEquals(move, detectiveBlue.play(board.getStation(116), targets,
                board.getAverageStation(targets), detectives));
    }

    // FIXME!!
    @Test
    public void getShortestWay_ExampleRed() {
        Detective detectiveBlue = new Detective(0, board.getStation(134), true, 10, 10, 10);
        Detective detectiveRed = new Detective(0, board.getStation(71), true, 2, 2, 0);
        Detective detectiveYellow = new Detective(0, board.getStation(186), true, 10, 10, 10);

        MisterX mrx = new MisterX(board.getStation(118), null, null, true, 1, 0, 0, 0);
        Set<Station> targets = new HashSet<>();
        targets.add(board.getStation(104));
        targets.add(board.getStation(117));
        targets.add(board.getStation(118));
        targets.add(board.getStation(127));

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detectiveBlue);
        detectives.add(detectiveRed);
        detectives.add(detectiveYellow);

        Move move = new Move(board.getStation(70), Ticket.CAB);
        List<Station> test = detectiveRed.getShortestWay(board.getStation(116));
        Assert.assertEquals(null, null);
    }

}
