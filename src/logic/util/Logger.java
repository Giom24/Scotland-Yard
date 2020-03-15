package logic.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import logic.GameLogic;
import logic.Ticket;
import logic.board.Station;
import logic.player.Detective;
import logic.player.MisterX;
import logic.player.Player;

/**
 * A Logger that logs the Game into a File
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class Logger {

    /**
     * Prints the Gamesettings into a file.
     *
     * @param misterX MisterX
     * @param detectives All Detectives
     * @throws IOException if an IO Error occurs
     */
    public static void printNewGame(MisterX misterX, List<Detective> detectives)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(GameLogic.Config.FILE_NAME));
        Formatter formatter = new Formatter();
        formatter.format("%d,%b,%b,%d", detectives.size() + 1, misterX.isAi(),
                detectives.get(0).isAi(), misterX.getCurrentStation().getIdentifier());
        for (Detective detective : detectives) {
            formatter.format(",%d", detective.getCurrentStation().getIdentifier());
        }
        writer.write(formatter.toString());
        writer.newLine();
        writer.close();
    }

    /**
     * Prints the winningstate to the logfile.
     * 
     * @param state The Winningstate
     * @throws IOException if an IO Error occurs
     */
    public static void printEndGame(GameLogic.WinState state) throws IOException {
        BufferedWriter writer =
                new BufferedWriter(new FileWriter(GameLogic.Config.FILE_NAME, true));
        switch (state) {
            case MISTERX_CATCHED:
            case MISTERX_SURROUNDED:
                writer.append("1");
                break;
            case DETECTIVES_BLOCKED:
            case MISTERX_WIN:
                writer.append("0");
                break;
            default:
                throw new IllegalArgumentException();
        }
        writer.close();
    }

    /**
     * Prints a Move into a file.
     *
     * @param player The Player that plays the Move
     * @param from The Station from where the players move
     * @param tactic The chosen tactic for the Move
     * @param ranking The ranking of the Move
     * @throws IOException if an IO Error occurs
     */
    public static void printMove(Player player, Station from, int tactic, float ranking)
            throws IOException {

        BufferedWriter writer =
                new BufferedWriter(new FileWriter(GameLogic.Config.FILE_NAME, true));
        Formatter formatter = new Formatter();
        formatter.format("%d,%d,%d", player.getId(), from.getIdentifier(),
                player.getCurrentStation().getIdentifier());

        for (Ticket ticket : Ticket.values()) {
            formatter.format(",%d", player.getTicketNum(ticket));
        }

        formatter.format(Locale.US, ",%d,%f", tactic, ranking);

        writer.append(formatter.toString());
        writer.newLine();
        writer.close();

    }
}
