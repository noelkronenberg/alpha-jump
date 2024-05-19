package search;

import game.Color;
import game.MoveGenerator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;


public class BasisKI {
    static final int TIME_LIMIT = 20000; // TODO: Hier rumspielen um sinnvollste Zeit zu checken
    static final int winCutOff = 100000;

    static boolean stopSearch = false;
    static boolean isOurMove = false; // supposed to be false, because we make a move before entering treeSearch
    static int maxDepth = -1;
    static HashMap<String,Integer> positionsHM = new HashMap<String, Integer>();

    public String orchestrator(String fen) {
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestratorNoAlphaBeta(String fen) {
        return MoveGenerator.convertMoveToFEN(getBestMoveNoAlphaBeta(fen));
    }

    public int getBestMove(String fen) {
        double bestScore = Integer.MIN_VALUE;
        int bestMove = -1;


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

            double currentScore = iterativeDeepening(nextState, moveTimeLimit, ourColor,ourColor); // get score for current move (order)

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

        return bestMove;
    }

    public double iterativeDeepening(MoveGenerator gameState, long moveTimeLimit, Color currentColor, Color ourColor) {
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

            double currentScore = treeSearch(gameState, Integer.MIN_VALUE, Integer.MAX_VALUE, endTime, depth, currentColor, ourColor); // get score for current move (order)

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

    public double treeSearch(MoveGenerator gameState, double alpha, double beta, long endTime, int depth, Color currentColor , Color ourColor) {
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

        // our turn
        if (isOurMove) {
            for (Integer move : movesList) {

                // get board with next (now current) move made
                MoveGenerator nextState = new MoveGenerator();
                nextState.initializeBoard(fen);
                nextState.movePiece(move);

                isOurMove = false; // player change

                // update alpha
                alpha = Math.max(alpha, treeSearch(nextState, alpha, beta, endTime, depth - 1, currentColor,ourColor));

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

                // update beta
                beta = Math.min(beta, treeSearch(nextState, alpha, beta, endTime, depth - 1, currentColor, ourColor));

                // prune branch if no improvements can be made
                if (beta <= alpha) {
                    break;
                }
            }
            return beta;
        }
    }

    public int getBestMoveNoAlphaBeta(String fen) {
        double bestScore = Integer.MIN_VALUE;
        int bestMove = -1;


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
            System.out.println("Best Score for Iteration: "+currentScore+" For Depth: "+depth+" For Move: "+move);
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


    public static void main(String[] args) {
        String fen = "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 r";
        MoveGenerator m = new MoveGenerator();
        m.initializeBoard(fen);
        m.printBoard(false);

        BasisKI ki = new BasisKI();
        //String bestMove = ki.orchestrator(fen);
        System.out.println();
        //System.out.println("Best move: " + bestMove);
        //System.out.println("Depth reached: " + maxDepth);

        System.out.println();

        BasisKI kiNoAlpha = new BasisKI();
        System.out.println(kiNoAlpha.orchestratorNoAlphaBeta(fen));
    }
}
