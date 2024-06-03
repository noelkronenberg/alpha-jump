package search;

import game.Color;
import game.MoveGenerator;

import java.util.*;

public class BasisKI {
    double timeLimit = 20000;
    boolean aspirationWindow = true;
    double aspirationWindowSize = 0.25;
    static final int winCutOff = 100000;
    static int currentDepth = 1;
    static int maxAllowedDepth = 0;
    static boolean stopSearch = false;
    static boolean isOurMove = false; // supposed to be false, because we make a move before entering treeSearch
    public int maxDepth = 1;
    public HashMap<String,Integer> positionsHM = new HashMap<>();

    // START: search with Alpha-Beta

    public String orchestrator(String fen) {
        return MoveGenerator.convertMoveToFEN(getBestMove(fen, true));
    }

    public String orchestrator(String fen, double ms) {
        timeLimit = ms;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen, true));
    }

    public String orchestrator(String fen, double ms, boolean aspirationWindow, double aspirationWindowSize) {
        timeLimit = ms;
        this.aspirationWindow = aspirationWindow;
        this.aspirationWindowSize = aspirationWindowSize;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen, true));
    }

    public String orchestrator(String fen, int actualMaxDepth) {
        maxAllowedDepth = actualMaxDepth;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen, false));
    }

    public int getBestMove(String fen, boolean timeCriterion) {
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

        fen = fen.substring(0, fen.length() - 2);
        positionsHM.put(fen,1); // save position

        double moveTimeLimit = (timeLimit - 100) / movesList.size(); // (static) time for each move to search

        // go through all possible moves
        for (Integer move : movesList) {

            // get board with current move made
            MoveGenerator nextState = new MoveGenerator();
            nextState.initializeBoard(fen);
            nextState.movePiece(move);

            // for safety (in case of TimeCutOffs)
            isOurMove = false;

            double currentScore = iterativeDeepening(nextState, moveTimeLimit, ourColor,ourColor, timeCriterion); // get score for current move (order)

            // evaluate move (score)

            /*
            // return if move order contains winning move
            if (currentScore >= winCutOff) {
                return move;
            }
            */

            // check if current move is best
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;
                // System.out.println("Current best move: " + MoveGenerator.convertMoveToFEN(bestMove) + " (score: " + bestScore + ")");
            }
        }

        return bestMove;
    }

    public double iterativeDeepening(MoveGenerator gameState, double moveTimeLimit, Color currentColor, Color ourColor, boolean timeCriterion) {
        int depth = 1;
        double bestScore = Integer.MIN_VALUE;
        double alpha=Integer.MIN_VALUE;
        double beta=Integer.MAX_VALUE;
        double endTime = System.currentTimeMillis() + moveTimeLimit;
        stopSearch = false;

        // check until time has run out or maxAllowedDepth is reached
        while (timeCriterion || depth <= maxAllowedDepth) {
            if(timeCriterion) {
                long currentTime = System.currentTimeMillis();
                if (currentTime >= endTime) {
                    break;
                }
            }

            isOurMove = false; // to switch players for each depth

            double currentScore = treeSearch(gameState, alpha, beta, endTime, depth, currentColor, ourColor, timeCriterion); // get score for current move (order)

            currentDepth = 1;

            // return if move order contains winning move
            if (currentScore >= winCutOff) {
                return currentScore;
            }

            if (!stopSearch) { // this is so that the most exact (longest and deepest searched) value is always taken
                bestScore = currentScore;
            }

            // START: aspiration window
            if (this.aspirationWindow) {
                // fail high / low
                if (currentScore <=alpha || currentScore >= beta) {
                    alpha = Integer.MIN_VALUE;
                    beta = Integer.MAX_VALUE;
                    continue;
                }

                // set window
                alpha = currentScore - this.aspirationWindowSize;
                beta = currentScore + this.aspirationWindowSize;
            }
            // END: aspiration window

            depth++;
        }
        return bestScore;
    }

    public double treeSearch(MoveGenerator gameState, double alpha, double beta, double endTime, int depth, Color currentColor , Color ourColor, boolean timeCriterion) {
        String fen = gameState.getFenFromBoard(); // convert position to FEN
        double score = Evaluation.ratePosition(gameState, ourColor, currentDepth);

        // save position
        if (positionsHM.containsKey(fen)){
            //return positionsHM.get(fen);
            positionsHM.put(fen, positionsHM.get(fen)+1);
        }
        else /*if ((depth == 1))*/{
             //score = Evaluation.ratePosition(gameState, ourColor);
            positionsHM.put(fen,1);
        }

        // get score for current position
        // double score = Evaluation.ratePosition(gameState, ourColor);

        // get moves for other player
        currentColor = (currentColor == Color.RED) ? Color.BLUE : Color.RED ; // signal player change
        LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);

        Evaluation.orderMoves(movesList, currentColor); // order moves

        /*
        // save position
        if (positionsHM.containsKey(fen)){
            positionsHM.put(fen, score);
        }
        else if ((depth == 1)){
            positionsHM.put(fen,score);
        }
        */

        if (timeCriterion && System.currentTimeMillis() >= endTime) {
            stopSearch = true;
        }

        if (stopSearch|| (depth == 1)|| score >= winCutOff ||score <= -winCutOff) {
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
                currentDepth+=1;
                alpha = Math.max(alpha, treeSearch(nextState, alpha, beta, endTime, depth - 1, currentColor,ourColor, timeCriterion));

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
                currentDepth+=1;
                beta = Math.min(beta, treeSearch(nextState, alpha, beta, endTime, depth - 1, currentColor, ourColor, timeCriterion));

                // prune branch if no improvements can be made
                if (beta <= alpha) {
                    break;
                }
            }
            return beta;
        }
    }

    // END: search with Alpha-Beta

    public static void main(String[] args) {
        String fen = "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r";
        MoveGenerator m = new MoveGenerator();
        m.initializeBoard(fen);
        m.printBoard(true);

        BasisKI ki = new BasisKI();
        String bestMove = ki.orchestrator(fen);
        System.out.println("Best move: " + bestMove);
        System.out.println("Depth reached: " + ki.maxDepth);

        System.out.println();
        System.out.println("Number of Unique Positions: " + ki.positionsHM.size());

        System.out.println();
        int numberOfPos = 0;
        for (Map.Entry < String, Integer > entry : ki.positionsHM.entrySet()){
            numberOfPos += entry.getValue();
        }
        System.out.println("Actual : " + numberOfPos);
    }
}