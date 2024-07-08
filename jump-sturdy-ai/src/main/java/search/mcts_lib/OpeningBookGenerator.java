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
        // Startet die Eröffnungsbibliothek entweder mit Startzug oder ohne
        if (depth == DEPTH) {
            return;
        }

        if (isStartingMoveLib) {
            // finde und notiere den besten Zug in der aktuellen Position und resette das Board danach
            String fenStorage = moveGenerator.getFenFromBoard();
            int bestMove = mcts.runMCTS(moveGenerator, player);
            moveGenerator.initializeBoard(fenStorage);

            //Fen und besten Zug in die Datei schreiben
            String schreibString = fenStorage + ", " + moveGenerator.convertMoveToFEN(bestMove);

            try {
                writer.write(schreibString + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Zug ausführen und Ausgangsboard speichern
            moveGenerator.movePiece(bestMove);
        }
        //Alle ersten Züge des Gegners herausfinden
        String fenStorage = moveGenerator.getFenFromBoard();

        // Farbe des Gegners herausfinden
        Color oppPlayer = (player == Color.RED) ? Color.BLUE : Color.RED;

        // mögliche Moves des Gegners herausfinden
        LinkedHashMap<Integer, List<Integer>> possMovesOpp = moveGenerator.generateAllPossibleMoves(oppPlayer);
        List<Integer> possMovesOppList = Evaluation.convertMovesToList(possMovesOpp);

        int totalRange = possMovesOppList.size(); // Beispiel für den gesamten Bereich, der aufgeteilt wird


        // Erstelle und starte Threads für jedes Viertel der Liste
        Thread thread1 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();  // Klone den MoveGenerator für jeden Thread
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, 0,  totalRange);
        });

        thread1.start();

        try {
            // Warte, bis alle Threads abgeschlossen sind
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
            // den variablen Gegenzug des Gegners eintragen
            int oppMove = possMovesOppList.get(moveCounter);

            // Zug des Gegners ausführen und Board speichern
            moveGenerator.movePiece(oppMove);
            String fenAfterOppMove = moveGenerator.getFenFromBoard();

            // finde den besten Gegenzug auf den Zug des Gegners
            int bestResponseMove = mcts.runMCTS(moveGenerator, player);
            String bestResponseFEN = MoveGenerator.convertMoveToFEN(bestResponseMove);

            //Position und besten Zug in Dokument eintragen
            String schreibString = fenAfterOppMove + ", " + bestResponseFEN;

            try {
                writer.write(schreibString + "\n");
            } catch(IOException e) {
                e.printStackTrace();
            }

            // Board zurücksetzen auf nach dem Gegnerzug und besten Zug ausführen
            moveGenerator.initializeBoard(fenAfterOppMove);
            moveGenerator.movePiece(bestResponseMove);

            // nächste Iteration starten
            try {
                generateOpeningBook(moveGenerator, mcts, writer, player, depth + 1);
            } catch(IOException e) {
                e.printStackTrace();
            }

            // Board zurücksetzen auf nach dem Spielerzug
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

        // Farbe des Gegners herausfinden
        Color oppPlayer = (player == Color.RED) ? Color.BLUE : Color.RED;

        // mögliche Moves des Gegners herausfinden
        LinkedHashMap<Integer, List<Integer>> possMovesOpp = moveGenerator.generateAllPossibleMoves(oppPlayer);
        List<Integer> possMovesOppList = Evaluation.convertMovesToList(possMovesOpp);

        for (int moveCounter = 0; moveCounter < possMovesOppList.size(); moveCounter++) {
            // den variablen Gegenzug des Gegners eintragen
            int oppMove = possMovesOppList.get(moveCounter);

            // Zug des Gegners ausführen und Board speichern
            moveGenerator.movePiece(oppMove);
            String fenAfterOppMove = moveGenerator.getFenFromBoard();

            // finde den besten Gegenzug auf den Zug des Gegners
            int bestResponseMove = mcts.runMCTS(moveGenerator, player);
            String bestResponseFEN = MoveGenerator.convertMoveToFEN(bestResponseMove);

            // Position und besten Zug in Dokument eintragen
            String schreibString = fenAfterOppMove + ", " + bestResponseFEN;
            writer.write(schreibString + "\n");

            // Board zurücksetzen auf nach dem Gegnerzug und besten Zug ausführen
            moveGenerator.initializeBoard(fenAfterOppMove);
            moveGenerator.movePiece(bestResponseMove);

            // nächste Iteration starten
            generateOpeningBook(moveGenerator, mcts, writer, player, depth + 1);

            // Board zurücksetzen auf nach dem Spielerzug
            moveGenerator.initializeBoard(fenStorage);
        }
        // Ursprüngliches Board zurücksetzen
        moveGenerator.initializeBoard(fenStorage);
    }
}
