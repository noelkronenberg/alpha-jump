package search;

import game.Color;
import game.MoveGenerator;

import java.util.*;

public class BasisKI {
    // hyperparameters (defaults)
    boolean timeCriterion = true;
    double timeLimit = 20000.0;
    boolean aspirationWindow = false; // TODO: turn on by default (with 0.25); requires adjustment of benchmarks / tests
    double aspirationWindowSize = 0;
    boolean transpositionTables = false;
    int maxAllowedDepth = 0;
    boolean dynamicTime = false;

    // derived parameters
    public int maxDepth = 1;
    public HashMap<String, Integer> positionsHM = new HashMap<>();
    public HashMap<String, TranspositionTableObejct> transpositionTable = new HashMap<>();

    // logic
    final int winCutOff = 100000;
    int currentDepth = 1;
    boolean stopSearch = false;
    boolean isOurMove = false; // supposed to be false, because we make a move before entering treeSearch

    // START: search with Alpha-Beta

    public static SearchConfig bestConfig = new SearchConfig(true, 50000.0, true, 0.25, true, 0, true);

    public String orchestrator(String fen, SearchConfig config) {
        this.timeCriterion = config.timeCriterion;
        this.timeLimit = config.timeLimit;
        this.aspirationWindow = config.aspirationWindow;
        this.aspirationWindowSize = config.aspirationWindowSize;
        this.transpositionTables = config.transpositionTables;
        this.maxAllowedDepth = config.maxAllowedDepth;
        this.dynamicTime = config.dynamicTime;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen) {
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, double ms) {
        this.timeCriterion = true;
        this.timeLimit = ms;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, double ms, boolean dynamicTime) {
        this.timeCriterion = true;
        this.timeLimit = ms;
        this.dynamicTime = dynamicTime;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, double ms, double aspirationWindowSize) {
        this.timeCriterion = true;
        this.timeLimit = ms;
        this.aspirationWindow = true;
        this.aspirationWindowSize = aspirationWindowSize;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, int actualMaxDepth, double aspirationWindowSize) {
        this.timeCriterion = false;
        this.maxAllowedDepth = actualMaxDepth;
        this.aspirationWindow = true;
        this.aspirationWindowSize = aspirationWindowSize;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, int actualMaxDepth, double aspirationWindowSize, boolean transpositionTables) {
        this.timeCriterion = false;
        this.maxAllowedDepth = actualMaxDepth;
        this.aspirationWindow = true;
        this.aspirationWindowSize = aspirationWindowSize;
        this.transpositionTables=transpositionTables;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, int actualMaxDepth, double aspirationWindowSize, boolean transpositionTables, boolean dynamicTime) {
        this.timeCriterion = false;
        this.dynamicTime=dynamicTime;
        this.maxAllowedDepth = actualMaxDepth;
        this.aspirationWindow = true;
        this.aspirationWindowSize = aspirationWindowSize;
        this.transpositionTables=transpositionTables;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, int actualMaxDepth, boolean transpositionTables, double ms, boolean dynamicTime) {
        this.timeCriterion = true;
        this.timeLimit = ms;
        this.dynamicTime=dynamicTime;
        this.maxAllowedDepth = actualMaxDepth;
        this.aspirationWindow = false;
        this.transpositionTables=transpositionTables;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, int actualMaxDepth, double ms, double aspirationWindowSize, boolean transpositionTables, boolean dynamicTime) {
        this.timeCriterion = true;
        this.timeLimit = ms;
        this.dynamicTime=dynamicTime;
        this.maxAllowedDepth = actualMaxDepth;
        this.aspirationWindow = true;
        this.aspirationWindowSize = aspirationWindowSize;
        this.transpositionTables=transpositionTables;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, double ms, double aspirationWindowSize, boolean transpositionTables, boolean dynamicTime, boolean timeCriterion, boolean aspirationWindow){
        this.timeCriterion = timeCriterion;
        this.timeLimit = ms;
        this.dynamicTime=dynamicTime;
        this.aspirationWindow = aspirationWindow;
        this.aspirationWindowSize = aspirationWindowSize;
        this.transpositionTables=transpositionTables;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
    }

    public String orchestrator(String fen, int actualMaxDepth) {
        this.timeCriterion = false;
        this.maxAllowedDepth = actualMaxDepth;
        return MoveGenerator.convertMoveToFEN(getBestMove(fen));
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

        fen = fen.substring(0, fen.length() - 2);
        positionsHM.put(fen, 1); // save position

        //TODO: Maybe impl. the starting position for Transposition tables as well?

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

            double currentScore = iterativeDeepening(nextState, moveTimeLimit, ourColor,ourColor); // get score for current move (order)

            // evaluate move (score)

            // return if move order contains winning move
//            if (currentScore >= this.winCutOff) {
//                return move;
//            }



            // check if current move is best
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;
                // System.out.println("Current best move: " + MoveGenerator.convertMoveToFEN(bestMove) + " (score: " + bestScore + ")");
            }
        }

        return bestMove;
    }

    public double iterativeDeepening(MoveGenerator gameState, double moveTimeLimit, Color currentColor, Color ourColor) {
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

            double currentScore = treeSearch(gameState, alpha, beta, endTime, depth, currentColor, ourColor); // get score for current move (order)

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

    public double treeSearch(MoveGenerator gameState, double alpha, double beta, double endTime, int depth, Color currentColor , Color ourColor) {
        String fen = gameState.getFenFromBoard(); // convert position to FEN
        boolean isInTT = transpositionTable.containsKey(fen);

        if (positionsHM.containsKey(fen)) {         //Dauert 1 sekunde dieser ganze block... vielleicht löschen?!
            positionsHM.put(fen, positionsHM.get(fen) + 1);
        } else {
            positionsHM.put(fen, 1);
        }

        if (transpositionTables){
            // save position
            // START: Transposition Tables
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
        double score;
        currentColor = (currentColor == Color.RED) ? Color.BLUE : Color.RED; // signal player change
        LinkedList<Integer> movesList;
        TranspositionTableObejct ttData;
        if (transpositionTables) {
            if (!isInTT) {
                LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor,fen); // get moves for other player
                movesList = Evaluation.convertMovesToList(moves);
                Evaluation.orderMoves(movesList, currentColor); // order moves

                score = Evaluation.ratePositionKI(gameState, ourColor, this.currentDepth,fen, moves, currentColor);
                ttData = new TranspositionTableObejct(score, movesList, depth);
                } else {
                    ttData = transpositionTable.get(fen);
                    movesList = ttData.movesList; // list is already ordered
                    score = ttData.overAllScore;
                    ttData.depth = depth; // TODO: should depth be updated here or at the end?
                }
        }
        else {
            LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(currentColor,fen); // get moves for other player
            movesList = Evaluation.convertMovesToList(moves);
            Evaluation.orderMoves(movesList, currentColor); // order moves

            score = Evaluation.ratePositionKI(gameState, ourColor, this.currentDepth,fen, moves, currentColor);
            ttData = new TranspositionTableObejct(score, movesList, depth);
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
                alpha = Math.max(alpha, treeSearch(nextState, alpha, beta, endTime, depth - 1, currentColor,ourColor));
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

                // prune branchif no improvements can be made
                if (beta <= alpha) {
                    break;
                }
            }
            ttData.bestMove = bestMove; // update TT
            ttData.overAllScore = bestScore;

            return beta;
        }
    }
        // END: Transposition Tables

    // END: search with Alpha-Beta

    public static void main(String[] args) {
        String fen = "6/8/8/8/8/1r0b0r0b0r02/4r03/3rr2 r";
        MoveGenerator m = new MoveGenerator();
        m.initializeBoard(fen);
        m.printBoard(true);

        BasisKI ki = new BasisKI();
        String bestMove = ki.orchestrator(fen, 20000000.0, 0.25);
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