package search.mcts_lib;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import game.Color;
import game.MoveGenerator;
import search.ab.Evaluation;

/**
 * creates a library-file for the first 3 moves with the best response-move for every possible move of the opponent using the MCTS-algorithm
 */
public class OpeningBookGenerator extends Thread {
    private final int DEPTH = 3;
    private Color startingPlayer = Color.BLUE;
    public String board = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";

    public static void main(String[] args) {
        OpeningBookGenerator openingBookGenerator = new OpeningBookGenerator();
        openingBookGenerator.runOPG();
    }

    /**
     * runs the openingBookGenerator
     */
    public void runOPG() {
        MCTS_lib mcts = new MCTS_lib();
        MoveGenerator initialState = new MoveGenerator();
        initialState.initializeBoard(board);
        initialState.printBoard(false);
        
        // Im Folgenden den Block auskommentieren, welcher nicht erstellt werden soll
        try (FileWriter writer = new FileWriter("jump-sturdy-ai/src/main/java/search/mcts_lib/opening_book_startingMove.txt")) { // Dieser Block erstellt Zug-Bibliothek für Spiele mit dem ersten Zug
            orchestrater(initialState, mcts, writer, startingPlayer, 0, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter("src/main/java/search/MCTS_lib/opening_book_secondMove.txt")) { // Dieser Block erstellt Zug-Bibliothek für Spiele mit dem zweiten Zug
            orchestrater(initialState, mcts, writer, startingPlayer, 0, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
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
        int size = possMovesOppList.size();
        int quarter = size / 4;
    
        // Erstelle und starte Threads für jedes Viertel der Liste
    Thread thread1 = new Thread(() -> {
        MoveGenerator threadMoveGenerator = moveGenerator.clone();  // Klone den MoveGenerator für jeden Thread
        processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, 0, quarter);
    });

    Thread thread2 = new Thread(() -> {
        MoveGenerator threadMoveGenerator = moveGenerator.clone();
        processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, quarter, quarter * 2);
    });

    Thread thread3 = new Thread(() -> {
        MoveGenerator threadMoveGenerator = moveGenerator.clone();
        processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, quarter * 2, quarter * 3);
    });

    Thread thread4 = new Thread(() -> {
        MoveGenerator threadMoveGenerator = moveGenerator.clone();
        processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, quarter * 3, size);
    });
    
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    
        try {
            // Warte, bis alle Threads abgeschlossen sind
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
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
            
            //Position und besten Zug in Dokument eintragen
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

