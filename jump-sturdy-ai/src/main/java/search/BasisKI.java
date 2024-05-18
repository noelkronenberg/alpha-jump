package search;

import game.Color;
import game.MoveGenerator;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class BasisKI {
    static final int TIME_LIMIT = 5000000; // TODO: Hier rumspielen um sinnvollste Zeit zu checken
    static final int winCutOff = 100000;

    static boolean stopSearch = false;
    static boolean isOurMove = false; // supposed to be false, because we make a move before entering treeSearch
    static int maxDepth = -1;
    public int orchestrator(String fen) {
        return getBestMove(fen);
    }

    public int getBestMove(String fen) {
        double bestScore = Integer.MIN_VALUE;
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
            Stack<Integer> moveStack = new Stack<>();
            moveStack.push(move);

            // get board with current move made
            MoveGenerator nextState = new MoveGenerator(); // TODO: switch to makeMOve / unmakeMove
            nextState.initializeBoard(fen);
            nextState.movePiece(move);

            double currentScore = iterativeDeepening(nextState, moveTimeLimit, color, moveStack,color); // get score for current move (order)

            // evaluate move (score)

            // return if move order contains winning move
            if (currentScore >= winCutOff) {
                return move;
            }

            // check if current move is best
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;
                System.out.println("Current best move: " + bestMove + " (score: " + bestScore + ")");
            }

            moveStack.clear();
        }

        return bestMove;
    }

    public double iterativeDeepening(MoveGenerator gameState, long moveTimeLimit, Color color,Stack<Integer> moveStack, Color playerColor) {
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

            double currentScore = treeSearch(gameState, Integer.MIN_VALUE, Integer.MAX_VALUE, endTime, depth, color, moveStack, playerColor); // get score for current move (order)

            // return if move order contains winning move
            if (currentScore >= winCutOff) {
                return currentScore;
            }

            if (currentScore > bestScore) {
                bestScore = currentScore;
                //System.out.println(moveStack);
            }

            moveStack.pop();
            depth++;
        }
        return bestScore;
    }

    public double treeSearch(MoveGenerator gameState, double alpha, double beta, long endTime, int depth, Color color ,Stack<Integer> moveStack, Color playerColor) {
        boolean firstIt = true;
        double prevAlpha = alpha;
        double prevBeta = beta;

        // get score for current position
        double score = Evaluation.ratePosition(gameState, playerColor);

        // get moves for other player
        color = (color == Color.RED) ? Color.BLUE : Color.RED ;  // signal player change
        LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(color);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);

        Evaluation.orderMoves(movesList, color); // order moves

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
                moveStack.push(move);

                // get board with next (now current) move made
                MoveGenerator nextState = new MoveGenerator();
                nextState.initializeBoard(fen);
                nextState.movePiece(move);

                isOurMove = false; // player change

                Stack<Integer> moveStackForBelow = new Stack<>();
                moveStackForBelow.addAll(moveStack);

                // update alpha
                alpha = Math.max(alpha, treeSearch(nextState, alpha, beta, endTime, depth - 1, color, moveStackForBelow,playerColor));

                if (alpha <= prevAlpha) {
                    moveStack.pop();
                }
                else if (firstIt){
                    moveStack = moveStackForBelow;
                    prevAlpha = alpha;
                    firstIt = false;
                }
                else {
                    moveStack = moveStackForBelow;
                    moveStack.remove(moveStack.size()-2);
                    prevAlpha = alpha;
                }

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
                moveStack.push(move);

                // get board with next (now current) move made
                MoveGenerator nextState = new MoveGenerator();
                nextState.initializeBoard(fen);
                nextState.movePiece(move);

                isOurMove = true; // player change

                Stack<Integer> moveStackForBelow = new Stack<>();
                moveStackForBelow.addAll(moveStack);

                // update beta
                beta = Math.min(beta, treeSearch(nextState, alpha, beta, endTime, depth - 1, color, moveStack, playerColor));

                if (beta >= prevBeta) {
                    moveStack.pop();
                }
                else if (firstIt) {
                    moveStack = moveStackForBelow;
                    firstIt = false;
                    prevBeta = beta;
                }
                else {
                    moveStack = moveStackForBelow;
                    moveStack.remove(moveStack.size()-2);
                    prevBeta = beta;
                }

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
        int bestMove = ki.orchestrator(fen);
        System.out.println();
        System.out.println("Best move: " + bestMove);
        System.out.println("Depth reached: " + maxDepth);

        System.out.println();
        Stack<Integer> moveStack = new Stack<>();
        moveStack.push(1);
        moveStack.push(2);
        moveStack.push(3);
        moveStack.push(4);
        moveStack.remove(1);
        System.out.println(moveStack);
    }
}
