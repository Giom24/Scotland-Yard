package gui;

import java.util.List;
import java.util.Set;
import logic.GUIConnector;
import logic.GameLogic;
import logic.Ticket;
import logic.board.Station;
import logic.player.Player;

/**
 *
 * @author giom
 */
public class FakeGUI implements GUIConnector {

    @Override
    public void showPlayersStates(Player player) {
    }

    @Override
    public Ticket showChooseTicketDialog(Station station, Set<Ticket> tickets) {
        return null;
    }

    @Override
    public void showNotEnoughTicketsForMoveDialog(Station station) {

    }

    @Override
    public void showStationNotReachableInMove(Station station) {

    }

    @Override
    public void showWinner(GameLogic.WinState state) {

    }

    @Override
    public void setLogbookEntry(int round, Ticket ticket) {

    }

    @Override
    public void drawPlayers(List<Player> player, Player turn, Station lastSeen) {

    }

    @Override
    public void showError(String title, String header, String context) {

    }

    @Override
    public void ShowLogError() {
    }

}
