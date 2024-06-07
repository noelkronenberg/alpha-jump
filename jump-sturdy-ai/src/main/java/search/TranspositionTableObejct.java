package search;

import java.util.LinkedList;

public class TranspositionTableObejct {
    double overAllScore;
    double currentPosRating;
    int bestMove;
    int depth;
    LinkedList<Integer> movesList;

    public TranspositionTableObejct(double currentPosRating, LinkedList<Integer> movesList, int depth) {
        this.currentPosRating = currentPosRating;
        this.movesList = movesList;
        this.depth = depth;
    }
}
