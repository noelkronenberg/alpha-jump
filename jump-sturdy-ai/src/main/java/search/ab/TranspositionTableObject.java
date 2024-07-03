package search.ab;

import java.util.LinkedList;

/**
 * Represents an entry in the transposition table used for optimizing the search algorithm.
 * This class stores information about previously evaluated game positions, including
 * their scores, the best move found, the search depth, and a list of possible moves.
 */
public class TranspositionTableObject {
    double overAllScore;
    double currentPosRating;
    int bestMove;
    int depth;
    LinkedList<Integer> movesList;

    /**
     * Constructs a new {@code TranspositionTableObject} with the position rating,
     * a list of moves, and the search depth.
     *
     * @param currentPosRating The rating of the current position.
     * @param movesList A list of possible moves from the current position.
     * @param depth The depth at which the position was evaluated.
     */
    public TranspositionTableObject(double currentPosRating, LinkedList<Integer> movesList, int depth) {
        this.currentPosRating = currentPosRating;
        this.movesList = movesList;
        this.depth = depth;
    }
}
