package search;

import game.Color;
import game.MoveGenerator;
import search.ab.Evaluation;

import java.util.*;

/**
 *  An implementation of a Minimax Search for the game Jump Sturdy without Alpha-Beta Pruning,.
 */
public class Minimax_noAB extends AI {
    SearchConfig config;

    // hyperparameters
    static int maxAllowedDepth = 2;

    // derived parameters
    static int maxDepth = -1;
    public HashMap<String,Integer> positionsHM = new HashMap<String, Integer>();

    // logic
    static final int winCutOff = 100000;
    static int currentDepth = 1;
    static boolean stopSearch = false;
    static boolean isOurMove = false; // NOTE: supposed to be false, because we make a move before entering treeSearch

    // START: search without Alpha-Beta

    @Override
    public String orchestrator(String fen, SearchConfig config) {
        this.config = config;
        this.maxAllowedDepth = this.config.maxAllowedDepth;
        return MoveGenerator.convertMoveToFEN(getBestMoveNoAlphaBeta(fen));
    }

    @Override
    public String showConfig() {
        return "maxAllowedDepth = " + this.config.maxAllowedDepth;
    }

    /**
     * Finds the best move using the Minimax algorithm without Alpha-Beta pruning.
     *
     * @param fen The FEN string representing the current game state.
     * @return The best move as an integer.
     */
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
        Evaluation.orderMoves(movesList, ourColor,gameState);

        fen = fen.substring(0, fen.length() - 2);
        positionsHM.put(fen, 1); // save position

        // go through all possible moves
        for (Integer move : movesList) {

            // get board with current move made
            MoveGenerator nextState = new MoveGenerator();
            nextState.initializeBoard(fen);
            nextState.movePiece(move);

            double currentScore = iterativeDeepeningNoAlphaBeta(nextState, ourColor,ourColor, move); // get score for current move

            // return if move order contains winning move
            if (currentScore >= winCutOff) {
                return move;
            }

            // check if current move is best
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;
                // System.out.println("Current best move: " + MoveGenerator.convertMoveToFEN(bestMove) + " (score: " + bestScore + ")");
            }
        }

        return bestMove;
    }

    /**
     * Performs iterative deepening search using the Minimax algorithm without Alpha-Beta pruning.
     *
     * @param gameState The current game state.
     * @param currentColor The color of the player whose turn it is.
     * @param ourColor The color of our player.
     * @return The best score for the current game state.
     */
    public double iterativeDeepeningNoAlphaBeta(MoveGenerator gameState, Color currentColor, Color ourColor, int move) {
        int depth = 1;
        double bestScore = Integer.MIN_VALUE;

        stopSearch = false;

        // check until time has run out
        while ((depth) <= maxAllowedDepth) {
            double currentScore = treeSearchNoAlphaBeta(gameState, depth, currentColor, ourColor, -1); // get score for current move (order)
            // System.out.println("best score (for iteration): " + currentScore + " | depth: " + depth + " | move: " + MoveGenerator.convertMoveToFEN(move));
            currentDepth=1;

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

    /**
     * Performs the Minimax search without Alpha-Beta pruning.
     *
     * @param gameState The current game state.
     * @param depth The maximum depth to search.
     * @param currentColor The color of the player whose turn it is.
     * @param ourColor The color of our player.
     * @param value The value for pruning.
     * @return The best score for the current game state.
     */
    public double treeSearchNoAlphaBeta(MoveGenerator gameState, int depth, Color currentColor , Color ourColor, double value) {
        // get score for current position
        String fen = gameState.getFenFromBoard(); // convert position to FEN

        // get moves for other player
        currentColor = (currentColor == Color.RED) ? Color.BLUE : Color.RED ;  // signal player change
        LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);

        Evaluation.orderMoves(movesList, currentColor, gameState); // order moves


        double score = Evaluation.ratePosition(gameState, ourColor, currentDepth);

        // save position
        if (positionsHM.containsKey(fen)){
            positionsHM.put(fen, positionsHM.get(fen)+1);
        }
        else {
            positionsHM.put(fen,1);
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
                currentDepth +=1;
                value = Math.max(value,treeSearchNoAlphaBeta(nextState, depth - 1, currentColor,ourColor, value));

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
                currentDepth +=1;
                value=Math.min(value,treeSearchNoAlphaBeta(nextState, depth - 1, currentColor, ourColor,value));

            }
            return value;
        }
    }
    // END: search without Alpha-Beta
}