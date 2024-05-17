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

    public int orchestrator(String fenAndColorFromServer) {
        // IDEE: Hier dann auch auf antwort von server warten und bestmove ausführen/zurücksenden

        MoveGenerator moveGenerator = new MoveGenerator();
        Evaluation evaluation = new Evaluation();
        char color_fen = fenAndColorFromServer.charAt(fenAndColorFromServer.length() - 1);
        Color color = moveGenerator.getColor(color_fen);

        // diese Antwort dann auf den server senden. Davor noch umformen in deren Format (UNKNOWN)
        return getBestMove(moveGenerator, evaluation, fenAndColorFromServer, color);
    }

    public int getBestMove(MoveGenerator gameState, Evaluation eval, String fen, Color color) {

        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        // TODO: Implementiere eine LinkedList oder einen Stack. Darin sind in der Reihenfolge die Ganzen vorherigen Zustände drin. wenn man durch einen durch ist, dann suche da durch

        // get the Moves here: ordered!

        LinkedHashMap<Integer, List<Integer>> moves = gameState.getMovesWrapper(fen);


        LinkedList<Integer> movesList = eval.convertMovesToList(moves); // TODO: Wirkt ineffizient: 1 Moves, dann movesList, dann OrderedmovesList.....

        long moveTimeLimit = (TIME_LIMIT - 100) / movesList.size(); // time for each move to search

        eval.orderMoves(movesList, color);

        for (Integer move : movesList) {
            MoveGenerator nextState = new MoveGenerator();  // TODO: Impl inplace updates rather than new Object
            nextState.initializeBoard(fen);
            nextState.movePiece(move); // preforms Move

            // TODO: Vielleicht die Farbe in MoveGenerator Speichern + in movePiece aktualisieren?

            int currentScore = iterativeDeepeningDFS(nextState, moveTimeLimit, eval, color) ;

            if (currentScore >= winCutOff) {
                return move;
            }

            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;
                System.out.println("Best Score: " + bestScore + "  Move: " + bestMove);
            }
        }

        return bestMove;
    }

    public int iterativeDeepeningDFS(MoveGenerator gameState, long timePerMove, Evaluation eval, Color color) {
        int depth = 1;
        int currentScore = 0;
        long endTime = System.currentTimeMillis() + timePerMove;
        stopSearch = false;

        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime) {
                break;
            }

            int scoreForMove = treeSearch(gameState, eval, Integer.MIN_VALUE, Integer.MAX_VALUE, currentTime,endTime, depth, color);

            if (scoreForMove >= winCutOff) {
                return scoreForMove;
            }

            if (scoreForMove > currentScore) {
                currentScore = scoreForMove;
            }

            depth++;
        }

        return currentScore;
    }

    public int treeSearch(MoveGenerator gameState, Evaluation eval, int alpha, int beta, long startTime, long endTime, int depth, Color color) {
        String fen = gameState.getFenFromBoard();
        int score = eval.ratePosition(gameState, color); // get the current Rating for Position

        color = (color==Color.RED) ? Color.BLUE : Color.RED;  // change color to opposite color to signal move change

        // get the moves
        LinkedHashMap<Integer, List<Integer>> moves = gameState.generateAllPossibleMoves(color);
        LinkedList<Integer> movesList = eval.convertMovesToList(moves);
        eval.orderMoves(movesList, color);


        // checks if we have time left
        if(System.currentTimeMillis() >= endTime) {
            stopSearch = true;
        }
        if (gameState.isGameOver(moves, color)) {       //TODO MAYBE FLAWED and should be in EVAL (Overload existing method to take LHM moves for isGameOver check)
            if (isOurMove) {
                return 100000;
            }
            return -100000;
        }

        if (stopSearch || (depth == 0)) { // NOTE: maybe check winCutOff
            return score;
        }
        if (maxDepth < depth) {
            maxDepth = depth;
        }

        if (isOurMove) {
            for (Integer move : movesList) {
                MoveGenerator nextState = new MoveGenerator();
                nextState.initializeBoard(fen);
                nextState.movePiece(move);
                isOurMove = false;

                alpha = Math.max(alpha, treeSearch(nextState, eval, alpha, beta, startTime, endTime, depth - 1, color));

                if (beta <= alpha) {
                    break;
                }
            }
            return alpha;
        }

        else {
            for (Integer move : movesList) {
                MoveGenerator nextState = new MoveGenerator();
                nextState.initializeBoard(fen);
                nextState.movePiece(move);
                isOurMove = true;

                beta = Math.min(beta, treeSearch(nextState, eval, alpha, beta, startTime, endTime, depth - 1, color));

                if (beta <= alpha) {
                    break;
                }
            }
            return beta;
        }
    }

    public static void main(String[] args) {
        BasisKI ki= new BasisKI();
        String fen = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
        MoveGenerator m = new MoveGenerator();
        m.initializeBoard(fen);
        m.printBoard(false);
        System.out.println(ki.orchestrator(fen));
        System.out.println(maxDepth);
    }
}
