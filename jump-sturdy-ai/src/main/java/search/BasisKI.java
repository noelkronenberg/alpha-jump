package search;

import game.Color;
import game.MoveGenerator;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class BasisKI {
    static final int TIME_LIMIT = 5000; // TODO: Hier rumspielen um sinnvollste Zeit zu checken
    static final int winCutOff = 100000;

    static boolean stopSearch = false;
    static boolean isOurMove = false; // supposed to be false, because we make a move before entering treeSearch
    static int maxDepth = -1;

    public int orchestrator(String fen) {
        return getBestMove(fen);
    }

    public int getBestMove(String fen) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        // TODO: Implementiere eine LinkedList oder einen Stack. Darin sind in der Reihenfolge die Ganzen vorherigen Zustände drin. wenn man durch einen durch ist, dann suche da durch

        // get moves
        MoveGenerator gameState = new MoveGenerator();
        LinkedHashMap<Integer, List<Integer>> moves = gameState.getMovesWrapper(fen);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);

        // order moves
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = gameState.getColor(color_fen);
        Evaluation.orderMoves(movesList, color);

        long moveTimeLimit = (TIME_LIMIT - 100) / movesList.size(); // (static) time for each move to search

        // go through all possible moves
        for (Integer move : movesList) {

            // get board with current move made
            MoveGenerator nextState = new MoveGenerator(); // TODO: switch to makeMOve / unmakeMove
            nextState.initializeBoard(fen);
            nextState.movePiece(move);

            int currentScore = iterativeDeepeningDFS(nextState, moveTimeLimit, color); // get score for current move (order)

            // evaluate move (score)

            // return if move order contains winning move
            if (currentScore >= winCutOff) {
                return move;
            }

            // check if current move is best
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;
                System.out.println("Best Score: " + bestScore + "  Move: " + bestMove);
            }
        }

        return bestMove;
    }

    public int iterativeDeepeningDFS(MoveGenerator gameState, long moveTimeLimit, Color color) {
        int depth = 1;
        int bestScore = 0;

        long endTime = System.currentTimeMillis() + moveTimeLimit;
        stopSearch = false;

        // check until time has run out
        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime) {
                break;
            }

            int currentScore = treeSearch(gameState, Integer.MIN_VALUE, Integer.MAX_VALUE, endTime, depth, color); // get score for current move (order)

            // return if move order contains winning move
            if (currentScore >= winCutOff) {
                return currentScore;
            }

            if (currentScore > bestScore) {
                bestScore = currentScore;
            }

            depth++;
        }

        return bestScore;
    }

    public int treeSearch(MoveGenerator gameState, int alpha, int beta, long endTime, int depth, Color color) {

        // get score for current position
        String fen = gameState.getFenFromBoard();
        int score = Evaluation.ratePosition(gameState, color);

        color = (color==Color.RED) ? Color.BLUE : Color.RED;  // signal player change

        // get moves for other player
        LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(color);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);

        Evaluation.orderMoves(movesList, color); // order moves

        // check if we have time left
        if (System.currentTimeMillis() >= endTime) {
            stopSearch = true;
        }

        // check if move is winning
        // TODO: MAYBE FLAWED
        // TODO: should be in EVAL (overload existing method to take LHM moves for isGameOver check)
        if (gameState.isGameOver(moves, color)) {
            if (isOurMove) {
                return 100000;
            }
            return -100000;
        }

        if (stopSearch || (depth == 0)) { // NOTE: maybe check winCutOff
            return score;
        }

        // update depth
        if (maxDepth < depth) {
            maxDepth = depth;
        }

        // our turn
        if (isOurMove) {
            for (Integer move : movesList) {

                // get board with next (now current) move made
                MoveGenerator nextState = new MoveGenerator();
                nextState.initializeBoard(fen);
                nextState.movePiece(move);
                isOurMove = false; // player change

                // update alpha
                alpha = Math.max(alpha, treeSearch(nextState, alpha, beta, endTime, depth - 1, color));

                // prune branch if no improvements can be made
                if (beta <= alpha) {
                    break;
                }
            }
            return alpha;
        }

        // other players turn
        else {

            // go through all possible moves
            for (Integer move : movesList) {

                // get board with next (now current) move made
                MoveGenerator nextState = new MoveGenerator();
                nextState.initializeBoard(fen);
                nextState.movePiece(move);
                isOurMove = true; // player change

                // update alpha
                beta = Math.min(beta, treeSearch(nextState, alpha, beta, endTime, depth - 1, color));

                // prune branch if no improvements can be made
                if (beta <= alpha) {
                    break;
                }
            }
            return beta;
        }
    }

    public static void main(String[] args) {
        String fen = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
        MoveGenerator m = new MoveGenerator();
        m.initializeBoard(fen);
        m.printBoard(false);

        BasisKI ki = new BasisKI();
        System.out.println(ki.orchestrator(fen));
        System.out.println(maxDepth);
    }
}
