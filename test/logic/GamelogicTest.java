package logic;

import com.google.gson.JsonSyntaxException;
import gui.FakeGUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import logic.board.Board;
import logic.player.Detective;
import logic.player.MisterX;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GamelogicTest {

    private static Board board;

    @Before
    public void readMap() {
        File f = new File("test/data/network.json");
        try {
            Reader reader = new FileReader(f);
            GamelogicTest.board = new Board(reader);
        } catch (FileNotFoundException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test

    public void isGameWon_MISTERX_SURROUNDED() {

        MisterX misterX = new MisterX(board.getStation(19), true);
        Detective detective0 = new Detective(1, board.getStation(8), true);
        Detective detective1 = new Detective(2, board.getStation(9), true);
        Detective detective2 = new Detective(3, board.getStation(32), true);

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detective0);
        detectives.add(detective1);
        detectives.add(detective2);

        FakeGUI gui = new FakeGUI();
        GameLogic gameLogic = new GameLogic(board, gui, misterX, detectives);
        Assert.assertEquals(GameLogic.WinState.MISTERX_SURROUNDED, gameLogic.isGameWon());

    }

    @Test
    public void isGameWon_MISTERX_CATCHED() {

        MisterX misterX = new MisterX(board.getStation(19), true);

        Detective detective0 = new Detective(1, board.getStation(19), true);
        Detective detective1 = new Detective(2, board.getStation(9), true);
        Detective detective2 = new Detective(3, board.getStation(32), true);

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detective0);
        detectives.add(detective1);
        detectives.add(detective2);

        FakeGUI gui = new FakeGUI();
        GameLogic gameLogic = new GameLogic(board, gui, misterX, detectives);
        Assert.assertEquals(GameLogic.WinState.MISTERX_CATCHED, gameLogic.isGameWon());

    }

    @Test
    public void isGameWon_DETECTIVES_BLOCKED() {

        MisterX misterX = new MisterX(board.getStation(1), true);
        misterX.setCurrentStation(board.getStation(1));
        Detective detective0 = new Detective(1, board.getStation(8), true, 0, 0, 0);
        Detective detective1 = new Detective(2, board.getStation(9), true, 0, 0, 0);
        Detective detective2 = new Detective(3, board.getStation(32), true, 0, 0, 0);

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detective0);
        detectives.add(detective1);
        detectives.add(detective2);

        FakeGUI gui = new FakeGUI();
        GameLogic gameLogic = new GameLogic(board, gui, misterX, detectives);
        Assert.assertEquals(GameLogic.WinState.DETECTIVES_BLOCKED, gameLogic.isGameWon());

    }

    @Test
    public void isGameWon_NO_WIN() {

        MisterX misterX = new MisterX(board.getStation(1), true);
        Detective detective0 = new Detective(1, board.getStation(8), true);
        Detective detective1 = new Detective(2, board.getStation(9), true);
        Detective detective2 = new Detective(3, board.getStation(32), true);

        List<Detective> detectives = new LinkedList<>();
        detectives.add(detective0);
        detectives.add(detective1);
        detectives.add(detective2);

        FakeGUI gui = new FakeGUI();
        GameLogic gameLogic = new GameLogic(board, gui, misterX, detectives);
        Assert.assertEquals(GameLogic.WinState.NO_WIN, gameLogic.isGameWon());
    }

    @Test(expected = JsonSyntaxException.class)
    public void CorruptedNetwork_wrongJson() throws FileNotFoundException {

        Reader network;
        network = new FileReader(new File("test/data/network_wrongJson.json"));

        FakeGUI gui = new FakeGUI();
        new GameLogic(network, gui, true, 3, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void CorruptedNetwork_wrongFormat() throws FileNotFoundException {

        Reader network = new FileReader(new File("test/data/network_wrongFormat.json"));
        FakeGUI gui = new FakeGUI();
        new GameLogic(network, gui, true, 3, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void CorruptedNetwork_wrongValues() throws FileNotFoundException {
        Reader network = new FileReader(new File("test/data/network_wrongValues.json"));
        FakeGUI gui = new FakeGUI();
        new GameLogic(network, gui, true, 3, true);
    }

    @Test(expected = JsonSyntaxException.class)
    public void CorruptedSaveState_wrongJson() throws FileNotFoundException {
        FakeGUI gui = new FakeGUI();
        Reader saveSate = new FileReader(new File("test/data/network.json"));
        Reader network = new FileReader(new File("test/data/save_wrongJson.sy"));
        new GameLogic(saveSate, network, gui);
    }

    @Test(expected = IllegalArgumentException.class)
    public void CorruptedSaveState_wrongFromat() throws FileNotFoundException {
        FakeGUI gui = new FakeGUI();
        Reader saveSate = new FileReader(new File("test/data/network.json"));
        Reader network = new FileReader(new File("test/data/save_wrongFormat.sy"));
        new GameLogic(saveSate, network, gui);
    }

    @Test(expected = IllegalArgumentException.class)
    public void CorruptedSaveState_wrongValues() throws FileNotFoundException {
        FakeGUI gui = new FakeGUI();
        Reader saveSate = new FileReader(new File("test/data/network.json"));
        Reader network = new FileReader(new File("test/data/save_wrongValues.sy"));
        new GameLogic(saveSate, network, gui);
    }

}
