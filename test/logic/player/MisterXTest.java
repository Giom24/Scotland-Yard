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

public class MisterXTest {

    private static Board board;

    @Before
    public void readMap() {
        File f = new File("test/data/network.json");
        try {
            Reader reader = new FileReader(f);
            MisterXTest.board = new Board(reader);
        } catch (FileNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    // ##########################################################################
    @Test
    public void getReachableStations_NoTickets() {
        MisterX misterX =
                new MisterX(board.getStation(1), null, new LinkedList<>(), true, 0, 0, 0, 0);
        Set<Station> stations = new HashSet<>();
        Assert.assertEquals(stations, misterX.getReachableStations());
    }

    @Test
    public void getReachableStations_Ticket_1_1_1_1_Start_1() {
        MisterX misterX =
                new MisterX(board.getStation(1), null, new LinkedList<>(), true, 1, 1, 1, 1);

        Set<Station> stations = new HashSet<>();
        stations.add(board.getStation(8));
        stations.add(board.getStation(9));
        stations.add(board.getStation(46));
        stations.add(board.getStation(58));
        Assert.assertEquals(stations, misterX.getReachableStations());
    }

    @Test
    public void getReachableStations_Ticket_0_0_0_1_Start_1() {
        MisterX misterX =
                new MisterX(board.getStation(1), null, new LinkedList<>(), true, 0, 0, 0, 1);

        Set<Station> stations = new HashSet<>();
        stations.add(board.getStation(8));
        stations.add(board.getStation(9));
        stations.add(board.getStation(46));
        stations.add(board.getStation(58));
        Assert.assertEquals(stations, misterX.getReachableStations());
    }

    // ##########################################################################
    @Test
    public void getLastSeen_StartPosition() {
        Station start = board.getStation(1);
        MisterX misterX = new MisterX(start, false);
        Assert.assertNull(misterX.getLastSeen());
    }

    @Test
    public void getLastSeen_FirstShow() {
        Station start = board.getStation(1);
        MisterX misterX = new MisterX(start, false);

        Move move1 = new Move(board.getStation(8), Ticket.CAB);
        misterX.move(move1);
        Move move2 = new Move(board.getStation(19), Ticket.CAB);
        misterX.move(move2);
        Move move3 = new Move(board.getStation(32), Ticket.CAB);
        misterX.move(move3);

        Assert.assertEquals(board.getStation(32), misterX.getLastSeen());
    }

    @Test
    public void getLastSeen_5_Rounds() {
        Station start = board.getStation(1);
        MisterX misterX = new MisterX(start, false);

        Move move1 = new Move(board.getStation(8), Ticket.CAB);
        misterX.move(move1);
        Move move2 = new Move(board.getStation(19), Ticket.CAB);
        misterX.move(move2);
        Move move3 = new Move(board.getStation(32), Ticket.CAB);
        misterX.move(move3);
        Move move4 = new Move(board.getStation(44), Ticket.CAB);
        misterX.move(move4);
        Move move5 = new Move(board.getStation(31), Ticket.CAB);
        misterX.move(move5);

        Assert.assertEquals(board.getStation(32), misterX.getLastSeen());
    }

    @Test
    public void getLastSeen_SecondShow() {
        Station start = board.getStation(1);
        MisterX misterX = new MisterX(start, false);

        Move move1 = new Move(board.getStation(8), Ticket.CAB);
        misterX.move(move1);
        Move move2 = new Move(board.getStation(19), Ticket.CAB);
        misterX.move(move2);
        Move move3 = new Move(board.getStation(32), Ticket.CAB);
        misterX.move(move3);
        Move move4 = new Move(board.getStation(44), Ticket.CAB);
        misterX.move(move4);
        Move move5 = new Move(board.getStation(31), Ticket.CAB);
        misterX.move(move5);
        Move move6 = new Move(board.getStation(44), Ticket.CAB);
        misterX.move(move6);
        Move move7 = new Move(board.getStation(18), Ticket.CAB);
        misterX.move(move7);
        Move move8 = new Move(board.getStation(8), Ticket.CAB);
        misterX.move(move8);

        Assert.assertEquals(board.getStation(8), misterX.getLastSeen());
    }

    // RANKING #################################################################
    @Test
    public void getRankingByReachableTargetpositions_NotReachableByNoOne() {
        MisterX misterX = new MisterX(board.getStation(43), true);
        Detective detective0 = new Detective(1, board.getStation(8), true);
        Detective detective1 = new Detective(2, board.getStation(44), true);
        List<Detective> detecvies = new LinkedList<>();
        detecvies.add(detective0);
        detecvies.add(detective1);
        float ranking = misterX.getRankingByReachableTargetStation(detecvies);
        Assert.assertEquals(20, ranking, 0.001);
    }

    @Test
    public void getRankingByReachableTargetpositions_ReachableByOne() {
        MisterX misterX = new MisterX(board.getStation(43), true);
        Detective detective0 = new Detective(0, board.getStation(31), true);
        Detective detective1 = new Detective(2, board.getStation(44), true);
        List<Detective> detecvies = new LinkedList<>();
        detecvies.add(detective0);
        detecvies.add(detective1);
        float ranking = misterX.getRankingByReachableTargetStation(detecvies);
        Assert.assertEquals(10, ranking, 0.001);
    }

    @Test
    public void getRankingByReachableTargetpositions_ReachableByAll() {
        MisterX misterX = new MisterX(board.getStation(43), true);
        Detective detective0 = new Detective(0, board.getStation(31), true);
        Detective detective1 = new Detective(2, board.getStation(57), true);
        List<Detective> detecvies = new LinkedList<>();
        detecvies.add(detective0);
        detecvies.add(detective1);
        float ranking = misterX.getRankingByReachableTargetStation(detecvies);
        Assert.assertEquals(0, ranking, 0.001);
    }

    // #########################################################################
    @Test
    public void getRankingByDirectlyReachableStations_3_StationReachable() {
        MisterX misterX = new MisterX(board.getStation(43), true);
        float ranking = misterX.getRankingByDirectlyReachableStations();
        Assert.assertEquals(3 / 13f * 4, ranking, 0.001);
    }

    @Test
    public void getRankingByDirectlyReachableStations_3_Stations_2_Blocked() {

        MisterX misterX = new MisterX(board.getStation(43), true);

        Detective detective0 = new Detective(1, board.getStation(18), true);
        Detective detective1 = new Detective(2, board.getStation(31), true);
        Detective detective2 = new Detective(3, board.getStation(1), true);

        float ranking = misterX.getRankingByDirectlyReachableStations();
        Assert.assertEquals(1 / 13f * 4, ranking, 0.001);
    }

}
