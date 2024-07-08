package search.mcts_lib;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import game.Color;
import game.MoveGenerator;
import search.ab.Evaluation;

/**
 * Creates a library-file for the first 3 moves with the best response-move for every possible move of the opponent using the MCTS-algorithm.
 */
public class OpeningBookGenerator extends Thread {
    private final int DEPTH = 3;
    private Color startingPlayer = Color.RED;
    private Color oppositeColor = (startingPlayer == Color.RED) ? Color.BLUE : Color.RED;
    public String board = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 r";

    public static void main(String[] args) {
        OpeningBookGenerator openingBookGenerator = new OpeningBookGenerator();
        openingBookGenerator.runOBG(true); // NOTE: adjust to desired player
    }

    /**
     * Runs the openingBookGenerator.
     *
     * @param starting indicator for whether starting or second move player library should be generated.
     */
    public void runOBG(boolean starting) {
        MCTS_lib mcts = new MCTS_lib();

        MoveGenerator initialState = new MoveGenerator();
        initialState.initializeBoard(board);
        initialState.printBoard(false);

        String os = System.getProperty("os.name").toLowerCase();
        String basePath = new File("").getAbsolutePath();
        String path;

        // adjust path to opening book
        if (os.contains("win")) {
            path = basePath + "\\src\\main\\java\\search\\mcts_lib\\";
        } else {
            path = basePath + "/src/main/java/search/mcts_lib/";
        }

        // starting move player
        if (starting) {
            try (FileWriter writer = new FileWriter(path + "opening_book_startingMove.txt")) {
                orchestrater(initialState, mcts, writer, startingPlayer, 0, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        // second move player
        } else {
            try (FileWriter writer = new FileWriter(path + "opening_book_secondMove.txt")) {
                orchestrater(initialState, mcts, writer, oppositeColor, 0, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates first set of opponents moves and starts multiple threads to simulate differents parts of the moveslist.
     * 
     * @param moveGenerator given Movegenerator to play the movelines on
     * @param mcts given mcts-instance to use the mcts-methods
     * @param writer given writer to write in the right document
     * @param player color of player to move
     * @param depth starting depth (which should be 0)
     * @param isStartingMoveLib boolean which decides if the library starts with own first move or opponents first move
     */
    private void orchestrater(MoveGenerator moveGenerator, MCTS_lib mcts, FileWriter writer, Color player, int depth, boolean isStartingMoveLib) {
        if (depth == DEPTH) {
            return;
        }

        if (isStartingMoveLib) {
            // find and record the best move in the current position and reset the board afterward
            String fenStorage = moveGenerator.getFenFromBoard();
            int bestMove = mcts.runMCTS(moveGenerator, player);
            moveGenerator.initializeBoard(fenStorage);

            // write FEN and best move to the file
            String schreibString = fenStorage + ", " + moveGenerator.convertMoveToFEN(bestMove);

            try {
                writer.write(schreibString + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // execute the move and save the initial board
            moveGenerator.movePiece(bestMove);
        }
        // find all first moves of the opponent
        String fenStorage = moveGenerator.getFenFromBoard();

        // find the color of the opponent
        Color oppPlayer = (player == Color.RED) ? Color.BLUE : Color.RED;

        // find possible moves of the opponent
        LinkedHashMap<Integer, List<Integer>> possMovesOpp = moveGenerator.generateAllPossibleMoves(oppPlayer);
        List<Integer> possMovesOppList = Evaluation.convertMovesToList(possMovesOpp);

        int totalRange = possMovesOppList.size(); // example for the entire range to be divided

        // create and start threads for each quarter of the list
        Thread thread1 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();  // clone the MoveGenerator for each thread
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, 0,  totalRange);
        });

        thread1.start();

        try {
            // wait until thread is completed
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs first iteration of move-simulation and calls next function.
     *
     * @param moveGenerator given Movegenerator to play the movelines on
     * @param mcts given mcts-instance to use the mcts-methods
     * @param writer given writer to write in the right document
     * @param player color of player to move
     * @param depth current depth of moves
     * @param fenStorage string which defines the current position of the game
     * @param possMovesOppList list of possible opponents moves
     * @param start int to define first move in opponents Movelist to be examined by current thread
     * @param end int to define last move in opponents Movelist to be examined by current thread
     */
    private void processMoves(MoveGenerator moveGenerator, MCTS_lib mcts, FileWriter writer, Color player, int depth, String fenStorage, List<Integer> possMovesOppList, int start, int end) {
        for (int moveCounter = start; moveCounter < end; moveCounter++) {
            // record the variable counter move of the opponent
            int oppMove = possMovesOppList.get(moveCounter);

            // execute the opponent's move and save the board
            moveGenerator.movePiece(oppMove);
            String fenAfterOppMove = moveGenerator.getFenFromBoard();

            // find the best counter-move to the opponent's move
            int bestResponseMove = mcts.runMCTS(moveGenerator, player);
            String bestResponseFEN = MoveGenerator.convertMoveToFEN(bestResponseMove);

            // record the position and best move in the document
            String schreibString = fenAfterOppMove + ", " + bestResponseFEN;

            try {
                writer.write(schreibString + "\n");
            } catch(IOException e) {
                e.printStackTrace();
            }

            // reset the board to after the opponent's move and execute the best move
            moveGenerator.initializeBoard(fenAfterOppMove);
            moveGenerator.movePiece(bestResponseMove);

            // nächste Iteration starten
            try {
                generateOpeningBook(moveGenerator, mcts, writer, player, depth + 1);
            } catch(IOException e) {
                e.printStackTrace();
            }

            // reset the board to after the player's move
            moveGenerator.initializeBoard(fenStorage);
        }
    }

    /**
     * Simulates until set depth and adds best moves into the list which is written into document later.
     *
     * @param moveGenerator given Movegenerator to play the movelines on
     * @param mcts given mcts-instance to use the mcts-methods
     * @param writer given writer to write in the right document
     * @param player color of player to move
     * @param depth current depth of moves
     * @throws IOException when there problems occur while trying to write into the writer-file
     */
    private void generateOpeningBook(MoveGenerator moveGenerator, MCTS_lib mcts, FileWriter writer, Color player, int depth) throws IOException {
        if (depth == DEPTH) {
            return;
        }

        String fenStorage = moveGenerator.getFenFromBoard();

        // find the color of the opponent
        Color oppPlayer = (player == Color.RED) ? Color.BLUE : Color.RED;

        // find possible moves of the opponent
        LinkedHashMap<Integer, List<Integer>> possMovesOpp = moveGenerator.generateAllPossibleMoves(oppPlayer);
        List<Integer> possMovesOppList = Evaluation.convertMovesToList(possMovesOpp);

        for (int moveCounter = 0; moveCounter < possMovesOppList.size(); moveCounter++) {
            // record the variable counter move of the opponent
            int oppMove = possMovesOppList.get(moveCounter);

            // execute the opponent's move and save the board
            moveGenerator.movePiece(oppMove);
            String fenAfterOppMove = moveGenerator.getFenFromBoard();

            // find the best counter-move to the opponent's move
            int bestResponseMove = mcts.runMCTS(moveGenerator, player);
            String bestResponseFEN = MoveGenerator.convertMoveToFEN(bestResponseMove);

            // record the position and best move in the document
            String schreibString = fenAfterOppMove + ", " + bestResponseFEN;
            writer.write(schreibString + "\n");

            // reset the board to after the opponent's move and execute the best move
            moveGenerator.initializeBoard(fenAfterOppMove);
            moveGenerator.movePiece(bestResponseMove);

            // start the next iteration
            generateOpeningBook(moveGenerator, mcts, writer, player, depth + 1);

            // reset the board to after the player's move
            moveGenerator.initializeBoard(fenStorage);
        }
        // reset the original board
        moveGenerator.initializeBoard(fenStorage);
    }
}
