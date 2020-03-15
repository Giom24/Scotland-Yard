package logic;

/**
 * Represents a Ticket.
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public enum Ticket {
    CAB,
    BUS,
    TUBE,
    BLACK;

    /**
     * Returns a Ticket to an ordinal Value.
     *
     * @param ordinal The ordinal value of the Ticket to get.
     * @return The Ticket that matches the ordinal Value.
     */
    public static Ticket from(int ordinal) {
        if (ordinal >= Ticket.values().length) {
            throw new IllegalArgumentException();
        }
        return Ticket.values()[ordinal];
    }
}
