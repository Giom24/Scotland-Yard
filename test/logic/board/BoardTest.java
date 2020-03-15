package logic.board;

import logic.board.Board;
import logic.board.Position;
import logic.board.Station;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import logic.Ticket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author giom
 */
public class BoardTest {

    private static Board board;

    @Before
    public void readMap() {
        File f = new File("test/data/network.json");
        try {
            Reader reader = new FileReader(f);
            BoardTest.board = new Board(reader);
        } catch (FileNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void parseAndCheckGetters() {

        Station station2 = board.getStation(2);
        Station station4 = board.getStation(4);

        Station station10 = board.getStation(10);
        Station station20 = board.getStation(20);

        Station station3 = board.getStation(3);
        Station station13 = board.getStation(13);

        Assert.assertEquals(2, station2.getIdentifier());
        Assert.assertEquals(new Position(0.30064754856614245293, 0.02948402948402948504),
                station2.getPosition());
        Assert.assertEquals(new HashSet(Arrays.asList(station10, station20)),
                station2.getStationsReachableBy(Ticket.CAB));

        Assert.assertEquals(4, station4.getIdentifier());
        Assert.assertEquals(new Position(0.48103607770582795800, 0.02579852579852579680),
                station4.getPosition());
        Assert.assertEquals(new HashSet(Arrays.asList(station3, station13)),
                station4.getStationsReachableBy(Ticket.CAB));

    }

    @Test(expected = IllegalArgumentException.class)
    public void getStation_OutOfLowerBound_Zero() {
        Station station1 = board.getStation(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStation_OutOfLowerBound_MinusOne() {
        Station station1 = board.getStation(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStation_OutOfUpperBound() {
        Station station1 = board.getStation(200);
    }

}
