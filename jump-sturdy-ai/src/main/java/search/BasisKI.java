package search;

import game.Color;
import game.MoveGenerator;

import java.util.*;

public class BasisKI {
    double timeLimit = 20000; // TODO: Hier rumspielen um sinnvollste Zeit zu checken
    static final int winCutOff = 100000;

    static int maxAllowedDepth = 0;
    static boolean stopSearch = false;
    static boolean isOurMove = false; // supposed to be false, because we make a move before entering treeSearch
    public int maxDepth = -1;
    public HashMap<String,Integer> positionsHM = new HashMap<String, Integer>();

    // START: search with Alpha-Beta

    public String orchestrator(String fen) {
        return MoveGenerator.convertMoveToFEN(getBestMove(fen, true));
    }

    public String orchestrator(String fen, double ms) {
        timeLimit = ms;
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

        fen=fen.substring(0,fen.length()-2);
        positionsHM.put(fen,1); // save position

        double moveTimeLimit = (timeLimit - 100) / movesList.size(); // (static) time for each move to search

        // go through all possible moves
        for (Integer move : movesList) {

            // get board with current move made
            MoveGenerator nextState = new MoveGenerator();
            nextState.initializeBoard(fen);
            nextState.movePiece(move);

            double currentScore = iterativeDeepening(nextState, moveTimeLimit, ourColor,ourColor, timeCriterion); // get score for current move (order)

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

    public double iterativeDeepening(MoveGenerator gameState, double moveTimeLimit, Color currentColor, Color ourColor, boolean timeCriterion) {
        int depth = 1;
        double bestScore = Integer.MIN_VALUE;

        double endTime = System.currentTimeMillis() + moveTimeLimit;
        stopSearch = false;

        // check until time has run out or maxAllowedDepth is reached
        while (timeCriterion||depth <= maxAllowedDepth) {
            if(timeCriterion) {
                long currentTime = System.currentTimeMillis();
                if (currentTime >= endTime) {
                    break;
                }
            }

            double currentScore = treeSearch(gameState, Integer.MIN_VALUE, Integer.MAX_VALUE, endTime, depth, currentColor, ourColor, timeCriterion); // get score for current move (order)

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

    public double treeSearch(MoveGenerator gameState, double alpha, double beta, double endTime, int depth, Color currentColor , Color ourColor, boolean timeCriterion) {
        // get score for current position
        double score = Evaluation.ratePosition(gameState, ourColor);

        // get moves for other player
        currentColor = (currentColor == Color.RED) ? Color.BLUE : Color.RED ;  // signal player change
        LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);

        Evaluation.orderMoves(movesList, currentColor); // order moves

        String fen = gameState.getFenFromBoard(); // convert position to FEN

        // save position
        if (/*(depth == 1)&&*/positionsHM.containsKey(fen)){
            positionsHM.put(fen, positionsHM.get(fen)+1);
        }
        else /*if ((depth == 1))*/{
            positionsHM.put(fen,1);
        }

        if (timeCriterion && System.currentTimeMillis() >= endTime) {
            stopSearch = true;
        }

        if (stopSearch || (depth == 1)) {
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
        String fen = "6/4bbb02/b02b01b02/1b02b03/2b01rrrr2/6r01/r01r0r0r03/5r0 r";
        MoveGenerator m = new MoveGenerator();
        m.initializeBoard(fen);
        m.printBoard(true);

        BasisKI ki = new BasisKI();
        String bestMove = ki.orchestrator(fen,3);
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