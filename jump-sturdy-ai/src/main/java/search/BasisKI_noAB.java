package search;

import game.Color;
import game.MoveGenerator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class BasisKI_noAB {
    static final int TIME_LIMIT = 20000; // TODO: Hier rumspielen um sinnvollste Zeit zu checken
    static final int winCutOff = 100000;

    static boolean stopSearch = false;
    static boolean isOurMove = false; // supposed to be false, because we make a move before entering treeSearch
    static int maxDepth = -1;
    static HashMap<String,Integer> positionsHM = new HashMap<String, Integer>();

    // START: search without Alpha-Beta

    public String orchestratorNoAlphaBeta(String fen) {
        return MoveGenerator.convertMoveToFEN(getBestMoveNoAlphaBeta(fen));
    }

    public int getBestMoveNoAlphaBeta(String fen) {
        double bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        positionsHM.put(fen,1); // save position

        // get moves
        MoveGenerator gameState = new MoveGenerator();
        LinkedHashMap<Integer, List<Integer>> moves = gameState.getMovesWrapper(fen);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);

        // order moves
        char color_fen = fen.charAt(fen.length() - 1);
        Color ourColor = gameState.getColor(color_fen);
        Evaluation.orderMoves(movesList, ourColor);

        long moveTimeLimit = (TIME_LIMIT - 100) / movesList.size(); // (static) time for each move to search

        // go through all possible moves
        for (Integer move : movesList) {

            // get board with current move made
            MoveGenerator nextState = new MoveGenerator();
            nextState.initializeBoard(fen);
            nextState.movePiece(move);


            double currentScore = iterativeDeepeningNoAlphaBeta(nextState, moveTimeLimit, ourColor,ourColor, move); // get score for current move (order)

            // evaluate move (score)

            // return if move order contains winning move
            if (currentScore >= winCutOff) {
                return move;
            }

            // check if current move is best
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;
                //System.out.println("Current best move: " + MoveGenerator.convertMoveToFEN(bestMove) + " (score: " + bestScore + ")");
            }
        }

        System.out.println("Number of Unique Positions: " + positionsHM.size());

        return bestMove;
    }

    public double iterativeDeepeningNoAlphaBeta(MoveGenerator gameState, long moveTimeLimit, Color currentColor, Color ourColor, int move) {
        int depth = 1;
        double bestScore = Integer.MIN_VALUE;

        long endTime = System.currentTimeMillis() + moveTimeLimit;
        stopSearch = false;

        // check until time has run out
        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime) {
                break;
            }

            double currentScore = treeSearchNoAlphaBeta(gameState, endTime, depth, currentColor, ourColor, -1); // get score for current move (order)
            System.out.println("best score (for iteration): " + currentScore + " | depth: " + depth + " | move: " + MoveGenerator.convertMoveToFEN(move));

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

    public double treeSearchNoAlphaBeta(MoveGenerator gameState, long endTime, int depth, Color currentColor , Color ourColor, double value) {
        // get score for current position
        double score = Evaluation.ratePosition(gameState, ourColor);

        // get moves for other player
        currentColor = (currentColor == Color.RED) ? Color.BLUE : Color.RED ;  // signal player change
        LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);

        Evaluation.orderMoves(movesList, currentColor); // order moves

        // check if we have time left
        if (System.currentTimeMillis() >= endTime) {
            stopSearch = true;
        }

        if (stopSearch || (depth == 0)) {
            return score;
        }

        // update depth
        if (maxDepth < depth) {
            maxDepth = depth;
        }

        String fen = gameState.getFenFromBoard(); // convert position to FEN

        // save position
        if (positionsHM.containsKey(fen)){
            positionsHM.put(fen, positionsHM.get(fen)+1);
        }
        else {
            positionsHM.put(fen,1);
        }

        // our turn
        if (isOurMove) {
            for (Integer move : movesList) {

                // get board with next (now current) move made
                MoveGenerator nextState = new MoveGenerator();
                nextState.initializeBoard(fen);
                nextState.movePiece(move);

                isOurMove = false; // player change

                value = Math.max(value,treeSearchNoAlphaBeta(nextState, endTime, depth - 1, currentColor,ourColor, value));

            }
            return value;
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

                value=Math.min(value,treeSearchNoAlphaBeta(nextState, endTime, depth - 1, currentColor, ourColor,value));

            }
            return value;
        }
    }

    // END: search without Alpha-Beta

    public static void main(String[] args) {
        String fen = "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b";
        MoveGenerator m = new MoveGenerator();
        m.initializeBoard(fen);
        m.printBoard(true);

        BasisKI_noAB ki = new BasisKI_noAB();
        String bestMove = ki.orchestratorNoAlphaBeta(fen);
        System.out.println("Best move: " + bestMove);
        System.out.println("Depth reached: " + maxDepth);
    }
}