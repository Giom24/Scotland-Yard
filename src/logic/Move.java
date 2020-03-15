package logic;

import java.util.Objects;
import logic.board.Station;

/**
 * Represents a Move from a Player. A Valid move contains a Station and a Ticket.
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class Move {

    private final Station to;
    private final Ticket ticket;

    /**
     * Constructor.
     *
     * @param to The Station to move to.
     * @param ticket The Ticket used to reach the Station.
     */
    public Move(Station to, Ticket ticket) {
        this.to = to;
        this.ticket = ticket;
    }

    /**
     * Gets the Station to move to.
     *
     * @return The Station.
     */
    public Station getTo() {
        return to;
    }

    /**
     * Gets the Ticket used to move to the Station.
     *
     * @return The Ticket
     */
    public Ticket getTicket() {
        return ticket;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.to);
        hash = 11 * hash + Objects.hashCode(this.ticket);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Move other = (Move) obj;
        if (!Objects.equals(this.to, other.to)) {
            return false;
        }
        return this.ticket == other.ticket;
    }

    @Override
    public String toString() {
        return String.format("%d by %s", to.getIdentifier(), ticket.toString());
    }

}
