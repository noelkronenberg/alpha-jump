package search.mcts_lib;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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

    public static HashMap<String,String> openingBook = new HashMap<>();
    public static LinkedHashSet<String> duplicateBook = new LinkedHashSet<>();

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
        /*try (FileWriter writer = new FileWriter("jump-sturdy-ai/src/main/java/search/mcts_lib/opening_book_startingMove.txt")) { // Dieser Block erstellt Zug-Bibliothek für Spiele mit dem ersten Zug
            orchestrater(initialState, mcts, writer, startingPlayer, 0, true);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try (FileWriter writer = new FileWriter("jump-sturdy-ai/src/main/java/search/mcts_lib/opening_book_secondMove.txt")) { // Dieser Block erstellt Zug-Bibliothek für Spiele mit dem zweiten Zug
            orchestrater(initialState, mcts, writer, startingPlayer, 0, false,"jump-sturdy-ai/src/main/java/search/mcts_lib/opening_book_startingMove.txt"); //TODO: Change to jump-sturdy-ai/src/main/java/search/mcts_lib/opening_book_secondMove.txt
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
    private void orchestrater(MoveGenerator moveGenerator, MCTS_lib mcts, FileWriter writer, Color player, int depth, boolean isStartingMoveLib, String path) {
        // Startet die Eröffnungsbibliothek entweder mit Startzug oder ohne
        if (depth == DEPTH) {
            return;
        }
        
        if (isStartingMoveLib) {
            // finde und notiere den besten Zug in der aktuellen Position und resette das Board danach
            String fenStorage = moveGenerator.getFenFromBoard();
            int bestMove = mcts.runMCTS(moveGenerator, player);
            moveGenerator.initializeBoard(fenStorage);

            String bestMoveString = moveGenerator.convertMoveToFEN(bestMove);

            synchronized (openingBook) {
                if (!openingBook.containsKey(fenStorage)){

                    openingBook.put(fenStorage, bestMoveString);
                }
                synchronized (duplicateBook) {
                    if(!duplicateBook.contains(fenStorage)){
                        duplicateBook.add(fenStorage);
                    }
                }
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
        int tenth = size / 10;
    
        // Erstelle und starte Threads für jedes Viertel der Liste
        Thread thread1 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();  // Klone den MoveGenerator für jeden Thread
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, 0, tenth,path);
        });

        Thread thread2 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, tenth, tenth * 2,path);
        });

        Thread thread3 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, tenth * 2, tenth * 3,path);
        });

        Thread thread4 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, tenth * 3, tenth * 4,path);
        });

        Thread thread5 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, tenth * 4, tenth * 5,path);
        });

        Thread thread6 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, tenth * 5, tenth * 6,path);
        });

        Thread thread7 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, tenth * 6, tenth * 7,path);
        });

        Thread thread8 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, tenth * 7, tenth * 8,path);
        });
    
        Thread thread9 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, tenth * 8, tenth * 9,path);
        });

        Thread thread10 = new Thread(() -> {
            MoveGenerator threadMoveGenerator = moveGenerator.clone();
            processMoves(threadMoveGenerator, mcts, writer, player, depth, fenStorage, possMovesOppList, tenth * 9, size,path);
        });

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();
        thread9.start();
        thread10.start();
    
        try {
            // Warte, bis alle Threads abgeschlossen sind
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();
            thread6.join();
            thread7.join();
            thread8.join();
            thread9.join();
            thread10.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Writes the Fen-Position and best move into a given file path.
     *
     * @param player The color of player to move.
     * @param pathToFile The String path where the Library File is going to be.
     * @throws IOException
     */
    public void writeToFile(Color player, String pathToFile) throws IOException {
        FileWriter writer = new FileWriter(pathToFile);

        calculateDuplicatePositionsAndWriteToFile(writer,player);

        for (Map.Entry<String, String> entry:openingBook.entrySet()) {
            //Fen und besten Zug in die Datei schreiben
            String schreibString = entry.getKey() + ", " + entry.getValue();

            try {
                writer.write(schreibString + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Recalculates all positions that would be a duplicate int the starting library with a bit more time.
     *
     * @param writer Given writer to write in the document.
     * @param player Color of player to move.
     */
    public void calculateDuplicatePositionsAndWriteToFile(FileWriter writer, Color player){
        MCTS_lib mcts = new MCTS_lib();
        MoveGenerator moveGenerator=new MoveGenerator();
        for (String fenPos:duplicateBook){
            //TODO: simulate only these positions
            moveGenerator.initializeBoard(fenPos);
            int bestResponseMove = mcts.runMCTS(moveGenerator, player);
            mcts.timeLimit=12000;           //TODO Maybe change value here, but have it a bit more than the rest of the board
            String bestMoveString = moveGenerator.convertMoveToFEN(bestResponseMove);
            String schreibString = fenPos + ", " + bestMoveString;
            try {
                writer.write(schreibString + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    private void processMoves(MoveGenerator moveGenerator, MCTS_lib mcts, FileWriter writer, Color player, int depth, String fenStorage, List<Integer> possMovesOppList, int start, int end, String path) {
        for (int moveCounter = start; moveCounter < end; moveCounter++) {
            // den variablen Gegenzug des Gegners eintragen
            int oppMove = possMovesOppList.get(moveCounter);
    
            // Zug des Gegners ausführen und Board speichern
            moveGenerator.movePiece(oppMove);
            String fenAfterOppMove = moveGenerator.getFenFromBoard();

            // finde den besten Gegenzug auf den Zug des Gegners
            int bestResponseMove = mcts.runMCTS(moveGenerator, player);
            String bestResponseFEN = MoveGenerator.convertMoveToFEN(bestResponseMove);

            synchronized (openingBook) {
                if (!openingBook.containsKey(fenStorage)){
                    openingBook.put(fenStorage, bestResponseFEN);
                }
                synchronized (duplicateBook) {
                    if(!duplicateBook.contains(fenStorage)){
                        duplicateBook.add(fenStorage);
                    }
                }
            }

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
                generateOpeningBook(moveGenerator, mcts, writer, player, depth + 1, path);
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
    private void generateOpeningBook(MoveGenerator moveGenerator, MCTS_lib mcts, FileWriter writer, Color player, int depth, String path) throws IOException {
        if (depth == DEPTH) {
            writeToFile(player,path);
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
            synchronized (openingBook) {
                if (!openingBook.containsKey(fenStorage)){
                    openingBook.put(fenStorage, bestResponseFEN);
                }
                synchronized (duplicateBook) {
                    if(!duplicateBook.contains(fenStorage)){
                        duplicateBook.add(fenStorage);
                    }
                }
            }
    
            // Board zurücksetzen auf nach dem Gegnerzug und besten Zug ausführen
            moveGenerator.initializeBoard(fenAfterOppMove);
            moveGenerator.movePiece(bestResponseMove);
    
            // nächste Iteration starten
            generateOpeningBook(moveGenerator, mcts, writer, player, depth + 1, path);
    
            // Board zurücksetzen auf nach dem Spielerzug
            moveGenerator.initializeBoard(fenStorage);
        }
        // Ursprüngliches Board zurücksetzen
        moveGenerator.initializeBoard(fenStorage);
    }
}

