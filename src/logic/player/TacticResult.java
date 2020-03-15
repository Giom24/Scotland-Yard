package logic.player;

import logic.Move;

/**
 * Represents an result of an Tactic calculation.
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class TacticResult {

    private final Move move;
    private final int id;
    private final float ranking;

    /**
     * Constructor.
     *
     * @param id The id of the calculated tactic
     * @param move The move (result) of the tactic
     * @param ranking The ranking of this tactic/move
     */
    TacticResult(int id, Move move, float ranking) {
        this.move = move;
        this.id = id;
        this.ranking = ranking;
    }

    /**
     * Getter for move.
     *
     * @return The move
     */
    public Move getMove() {
        return move;
    }

    /**
     * Getter for id.
     *
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for ranking.
     *
     * @return The ranking
     */
    public float getRanking() {
        return ranking;
    }

}
