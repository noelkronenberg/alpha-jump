package search.ab;

import game.Color;
import game.MoveGenerator;
import search.AI;
import search.SearchConfig;

import java.util.*;

/**
 *  An implementation of a Minimax Search for the game Jump Sturdy with Alpha-Beta Pruning,
 *  Aspiration Windows, Transposition Tables and a Quiescence Search.
 */
public class Minimax_AB extends AI {
    SearchConfig config;

    // hyperparameters (defaults)
    boolean timeCriterion = true;
    double timeLimit = bestConfig.timeLimit;
    boolean aspirationWindow = false;
    double aspirationWindowSize = 0;
    boolean transpositionTables = false;
    boolean useQuiescenceSearch = false;
    int maxAllowedDepth = 0;
    boolean dynamicTime = false;

    // derived parameters
    public int maxDepth;
    public HashMap<String, Integer> positionsHM;
    public HashMap<String, TranspositionTableObject> transpositionTable;

    // logic
    final int winCutOff = 100000;
    int currentDepth = 1;
    boolean stopSearch = false;
    boolean isOurMove = false; // NOTE: supposed to be false, because we make a move before entering treeSearch

    // START: search with Alpha-Beta

    public static SearchConfig bestConfig = new SearchConfig(true, 50000.0, true, 0.25, true, 0, true, false);

    @Override
    public String orchestrator(String fen, SearchConfig config) {
        this.config = config;
        this.timeCriterion = this.config.timeCriterion;
        this.timeLimit = this.config.timeLimit;
        this.aspirationWindow = this.config.aspirationWindow;
        this.aspirationWindowSize = this.config.aspirationWindowSize;
        this.transpositionTables = this.config.transpositionTables;
        this.maxAllowedDepth = this.config.maxAllowedDepth;
        this.dynamicTime = this.config.dynamicTime;
        this.useQuiescenceSearch = this.config.useQuiescenceSearch;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    @Override
    public String showConfig() {
        return "timeCriterion = " + timeCriterion +
                " | timeLimit = " + timeLimit +
                " | aspirationWindow = " + aspirationWindow +
                " | aspirationWindowSize = " + aspirationWindowSize +
                " | transpositionTables = " + transpositionTables +
                " | maxAllowedDepth = " + maxAllowedDepth +
                " | dynamicTime = " + dynamicTime +
                " | useQuiescenceSearch = " + useQuiescenceSearch;
    }

    /**
     * Determines the best move for a given board state represented by a FEN string.
     * This method utilizes iterative deepening search with time management to
     * evaluate possible moves and select the one with the highest score.
     *
     * @param fen The current position in FEN notation.
     * @return The best move determined based on the evaluation, represented as an integer.
     */
    public int getBestMove(String fen) {
        transpositionTable = new HashMap<>();
        positionsHM = new HashMap<>();
        maxDepth = 1;

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

        // START: time management
        double moveTimeLimit = (this.timeLimit - 100) / movesList.size(); // default (for static)
        double totalTime = this.timeLimit - 100; // for dynamic
        double remainingTime = totalTime; // for dynamic
        double totalWeight = 0.0; // for dynamic

        if (this.dynamicTime) {
            // total weight (sum of inverses of indices)
            for (int i = 1; i <= movesList.size(); i++) {
                totalWeight += 1.0 / i;
            }
        }
        // END: time management

        // go through all possible moves
        for (int i = 0; i < movesList.size(); i++) {
            Integer move = movesList.get(i);

            // START: dynamic time management
            if (this.dynamicTime) {
                if (remainingTime <= 0) {
                    break;
                } else {
                    double weight = 1.0 / (i + 1);
                    moveTimeLimit = (weight / totalWeight) * totalTime;

                    // remaining time
                    remainingTime -= moveTimeLimit;

                    // remove over-allocation
                    if (remainingTime < 0) {
                        moveTimeLimit += remainingTime;
                    }
                }
            }
            // END: dynamic time management

            // get board with current move made
            MoveGenerator nextState = new MoveGenerator();
            nextState.initializeBoard(fen);
            nextState.movePiece(move);

            // for safety (in case of TimeCutOffs)
            this.isOurMove = false;

            double currentScore = iterativeDeepening(nextState, moveTimeLimit, ourColor); // get score for current move (order)

            // evaluate move (score)

            /*
            // return if move order contains winning move
            if (currentScore >= this.winCutOff) {
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

    public double quiescenceSearch(MoveGenerator gameState, double alpha, double beta, Color currentColor, Color ourColor) {
        String fen = gameState.getFenFromBoard(); // convert position to FEN

        // rate current position without moving
        double standPat = Evaluation.ratePositionAI(gameState, ourColor, this.currentDepth, fen, new LinkedHashMap<>(), currentColor);

        // beta cutoff
        if (standPat >= beta) {
            return beta;
        }

        // found better position
        if (alpha < standPat) {
            alpha = standPat;
        }

        // move list
        LinkedList<Integer> movesList = new LinkedList<>();

        if (this.transpositionTables) {
            if (transpositionTable.containsKey(fen)) {
                movesList = transpositionTable.get(fen).movesList;
            } else {
                LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor);
                movesList = Evaluation.convertMovesToList(moves);
                Evaluation.orderMoves(movesList, currentColor, gameState);
                TranspositionTableObject ttData = new TranspositionTableObject(standPat, movesList, this.currentDepth);
                transpositionTable.put(fen, ttData);
            }
        } else {
            LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor);
            movesList = Evaluation.convertMovesToList(moves);
            Evaluation.orderMoves(movesList, currentColor, gameState);
        }

       // quiescence Search for every move
        for (Integer move : movesList) {
            MoveGenerator nextState = new MoveGenerator();
            nextState.initializeBoard(fen);
            nextState.movePiece(move);

            double score = -quiescenceSearch(nextState, -beta, -alpha, (currentColor == Color.RED) ? Color.BLUE : Color.RED, ourColor);

            if (score >= beta) {
                return beta;
            }

            if (score > alpha) {
                alpha = score;
            }
        }

        // best rating
        return alpha;
    }

    /**
     * Performs an iterative deepening search to find the best move score within the given time limit.
     * This method incrementally deepens the search depth until the time runs out or the maximum
     * allowed depth is reached. Alpha-Beta pruning is used to optimize the search.
     *
     * @param gameState The current state of the game represented by a {@code MoveGenerator} object.
     * @param moveTimeLimit The maximum time allowed for evaluating a move, in milliseconds.
     * @param ourColor The color of the player whose best move gets evaluated.
     * @return The best score obtained for the evaluated moves within the given time limit.
     */
    public double iterativeDeepening(MoveGenerator gameState, double moveTimeLimit, Color ourColor) {
        int depth = 1;
        double bestScore = Integer.MIN_VALUE;
        double alpha = Integer.MIN_VALUE;
        double beta = Integer.MAX_VALUE;
        double endTime = System.currentTimeMillis() + moveTimeLimit;
        this.stopSearch = false;

        // check until time has run out or maxAllowedDepth is reached
        while (this.timeCriterion || depth <= this.maxAllowedDepth) {
            if (this.timeCriterion) {
                long currentTime = System.currentTimeMillis();
                if (currentTime >= endTime) {
                    break;
                }
            }

            this.isOurMove = false; // to switch players for each depth

            double currentScore = treeSearch(gameState, alpha, beta, endTime, depth, ourColor, ourColor); // get score for current move (order)

            this.currentDepth = 1;

            // return if move order contains winning move
            if (currentScore >= this.winCutOff || currentScore <= -this.winCutOff) {
                return currentScore;
            }

            if (!this.stopSearch) { // this is so that the most exact (longest and deepest searched) value is always taken
                bestScore = currentScore;
            }

            // START: aspiration window
            if (this.aspirationWindow) {
                // fail high / low
                if (currentScore <= alpha || currentScore >= beta) {
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

    /**
     * Executes a recursive tree search with alpha-beta pruning to evaluate game positions and find
     * the best possible move for the current player. This method can use, based on the configuration,
     * transposition tables to store and get previously evaluated positions to optimize the search.
     *
     * @param gameState The current state of the game represented by a {@code MoveGenerator} object.
     * @param alpha The alpha value used in alpha-beta pruning.
     * @param beta The beta value used in alpha-beta pruning.
     * @param endTime The time when the search should stop, used for time management.
     * @param depth The depth left for the search tree.
     * @param currentColor The color of the player currently making a move.
     * @param ourColor The color of the player whose best move gets evaluated.
     * @return The best score obtained for the evaluated game position.
     */
    public double treeSearch(MoveGenerator gameState, double alpha, double beta, double endTime, int depth, Color currentColor , Color ourColor) {
        String fen = gameState.getFenFromBoard(); // convert position to FEN
        boolean isInTT = transpositionTable.containsKey(fen);

        if (positionsHM.containsKey(fen)) { // NOTE: takes a long time; remove if time is critical
            positionsHM.put(fen, positionsHM.get(fen) + 1);
        } else {
            positionsHM.put(fen, 1);
        }

        // START: Transposition Tables
        if (transpositionTables) {
            // save position
            if (isInTT && transpositionTable.get(fen).depth > depth) {
                // return positionsHM.get(fen);
                if (this.maxDepth < depth) {
                    this.maxDepth = depth;
                }
                if (this.timeCriterion && System.currentTimeMillis() >= endTime) {
                    this.stopSearch = true;
                }
                return transpositionTable.get(fen).overAllScore;
            }
        }
        // END: Transposition Tables

        double score;
        currentColor = (currentColor == Color.RED) ? Color.BLUE : Color.RED; // signal player change
        LinkedList<Integer> movesList;
        TranspositionTableObject ttData;

        // START: Transposition Tables
        if (transpositionTables) {
            if (!isInTT) {

                LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor); // get moves for other player
                movesList = Evaluation.convertMovesToList(moves);
                Evaluation.orderMoves(movesList, currentColor,gameState); // order moves

                score = Evaluation.ratePositionAI(gameState, ourColor, this.currentDepth,fen, moves, currentColor);
                ttData = new TranspositionTableObject(score, movesList, depth);

                } else {
                    ttData = transpositionTable.get(fen);
                    movesList = ttData.movesList; // list is already ordered
                    score = ttData.overAllScore;
                    ttData.depth = depth; // TODO: should depth be updated here or at the end?
                }
        // END: Transposition Tables
        } else {
            LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor); // get moves for other player
            movesList = Evaluation.convertMovesToList(moves);
            Evaluation.orderMoves(movesList, currentColor,gameState); // order moves

            score = Evaluation.ratePositionAI(gameState, ourColor, this.currentDepth,fen, moves, currentColor);
            ttData = new TranspositionTableObject(score, movesList, depth);
        }

        if (this.timeCriterion && System.currentTimeMillis() >= endTime) {
            this.stopSearch = true;
        }

        if (this.stopSearch || (depth == 1)|| score >= this.winCutOff || score <= -this.winCutOff) {
            return score;
        }

        // update depth
        if (this.maxDepth < depth) {
            this.maxDepth = depth;
        }

        double bestScore = Integer.MIN_VALUE; // support variables for TTs
        int bestMove = -1;

        // our turn
        if (this.isOurMove) {
            for (Integer move : movesList) {

                // get board with next (now current) move made
                MoveGenerator nextState = new MoveGenerator();
                nextState.initializeBoard(fen);
                nextState.movePiece(move);

                this.isOurMove = false; // player change

                // update alpha
                this.currentDepth += 1;
                double prevAlpha = alpha;
                alpha = Math.max(alpha, treeSearch(nextState, alpha, beta, endTime, depth - 1, currentColor, ourColor));
                if (prevAlpha < alpha) { // signals switch of best move
                    bestScore = alpha;
                    bestMove = move;
                }

                // prune branch if no improvements can be made
                if (beta <= alpha) {
                    break;
                }
            }
            ttData.bestMove = bestMove; // update TT
            ttData.overAllScore = bestScore;

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

                this.isOurMove = true; // player change

                // update beta
                this.currentDepth += 1;
                double prevBeta = beta;

                beta = Math.min(beta, treeSearch(nextState, alpha, beta, endTime, depth - 1, currentColor, ourColor));

                if (prevBeta > beta){ // signals switch of best move
                    bestScore = beta;
                    bestMove = move; // update overall score for this pos and its best move
                }

                // prune branch if no improvements can be made
                if (beta <= alpha) {
                    break;
                }
            }

            ttData.bestMove = bestMove; // update TT
            ttData.overAllScore = bestScore;

            return beta;
        }
    }
    // END: search with Alpha-Beta
}