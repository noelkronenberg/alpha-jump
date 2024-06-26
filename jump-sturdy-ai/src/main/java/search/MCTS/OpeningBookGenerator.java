package search.MCTS;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import game.Color;
import game.MoveGenerator;
import search.Evaluation;

public class OpeningBookGenerator {
    private static final int DEPTH = 3;
    private static int mctsIterations = 1000;
    private static Color startingPlayer = Color.BLUE;
    private static String board = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
    //private static String board = "2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b";

    public static void main(String[] args) {
        MoveGenerator initialState = new MoveGenerator();
        initialState.initializeBoard(board);
        initialState.printBoard(false);
        MCTS mcts = new MCTS();
        try (FileWriter writer = new FileWriter("opening_book_startingMove.txt")) {
            generateOpeningBookStarting(initialState, mcts, writer, startingPlayer, "", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter("opening_book_secondMove.txt")) {
            generateOpeningBookSecond(initialState, mcts, writer, startingPlayer, "", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private static void generateOpeningBookStarting(MoveGenerator moveGenerator, MCTS mcts, FileWriter writer, Color player, String moveProcedure, int depth) throws IOException {
        if (depth == DEPTH) {
            return;
        }
    
        // Farbe des Gegners herausfinden
        Color oppPlayer = (player == Color.RED) ? Color.BLUE : Color.RED;
    
        // finde und notiere den besten Zug in der aktuellen Position und resette das Board danach
        String fenStorage = moveGenerator.getFenFromBoard();
        int bestMove = MCTS.runMCTS(moveGenerator, player, mctsIterations);
        moveGenerator.initializeBoard(fenStorage);
    
        // Zug ausführen und Ausgangsboard speichern
        moveGenerator.movePiece(bestMove);
        String myMoveFEN = MoveGenerator.convertMoveToFEN(bestMove);
        moveProcedure += "OM: " + myMoveFEN + "; ";
        fenStorage = moveGenerator.getFenFromBoard();
    
        // mögliche Moves des Gegners herausfinden
        LinkedHashMap<Integer, List<Integer>> possMovesOpp = moveGenerator.generateAllPossibleMoves(oppPlayer, fenStorage);
        List<Integer> possMovesOppList = Evaluation.convertMovesToList(possMovesOpp);
    
        for (int moveCounter = 0; moveCounter < possMovesOppList.size(); moveCounter++) {
            // den variablen Gegenzug des Gegners eintragen
            int oppMove = possMovesOppList.get(moveCounter);
            String oppMoveFEN = MoveGenerator.convertMoveToFEN(oppMove);
            String moveProcedureWithOppMove = moveProcedure + "EM: " + oppMoveFEN + "; ";
    
            // Zug des Gegners ausführen und Board speichern
            moveGenerator.movePiece(oppMove);
            String fenAfterOppMove = moveGenerator.getFenFromBoard();
    
            // finde den besten Gegenzug auf den Zug des Gegners
            int bestResponseMove = MCTS.runMCTS(moveGenerator, player, mctsIterations);
            String bestResponseFEN = MoveGenerator.convertMoveToFEN(bestResponseMove);
    
            // vollständige Zugfolge in die Datei schreiben
            String completeMoveProcedure = moveProcedureWithOppMove + "OM: " + bestResponseFEN + ";";
            if(depth + 1 == DEPTH) {
            writer.write(completeMoveProcedure + "\n");
            }
    
            // Board zurücksetzen auf nach dem Gegnerzug
            moveGenerator.initializeBoard(fenAfterOppMove);
    
            // nächste Iteration starten
            generateOpeningBookStarting(moveGenerator, mcts, writer, player, moveProcedureWithOppMove, depth + 1);
    
            // Board zurücksetzen auf nach dem Spielerzug
            moveGenerator.initializeBoard(fenStorage);
        }
    
        // Ursprüngliches Board zurücksetzen
        moveGenerator.initializeBoard(fenStorage);
    }

    private static void generateOpeningBookSecond(MoveGenerator moveGenerator, MCTS mcts, FileWriter writer, Color player, String moveProcedure, int depth) throws IOException {
        
        String fenStorage = moveGenerator.getFenFromBoard();

        // Farbe des Gegners herausfinden
        Color oppPlayer = (player == Color.RED) ? Color.BLUE : Color.RED;

        // mögliche Moves des Gegners herausfinden
        LinkedHashMap<Integer, List<Integer>> possMovesOpp = moveGenerator.generateAllPossibleMoves(oppPlayer, fenStorage);
        List<Integer> possMovesOppList = Evaluation.convertMovesToList(possMovesOpp);
    
        for (int moveCounter = 0; moveCounter < possMovesOppList.size(); moveCounter++) {
            // den variablen Gegenzug des Gegners eintragen
            int oppMove = possMovesOppList.get(moveCounter);
            String oppMoveFEN = MoveGenerator.convertMoveToFEN(oppMove);
            String moveProcedureWithOppMove = moveProcedure + "EM: " + oppMoveFEN + "; ";
    
            // Zug des Gegners ausführen und Board speichern
            moveGenerator.movePiece(oppMove);
            String fenAfterOppMove = moveGenerator.getFenFromBoard();
    
            // finde den besten Gegenzug auf den Zug des Gegners
            int bestResponseMove = MCTS.runMCTS(moveGenerator, player, mctsIterations);
            String bestResponseFEN = MoveGenerator.convertMoveToFEN(bestResponseMove);
    
            // vollständige Zugfolge in die Datei schreiben
            String completeMoveProcedure = moveProcedureWithOppMove + "OM: " + bestResponseFEN + ";";
            if(depth + 1 == DEPTH) {
            writer.write(completeMoveProcedure + "\n");
            }
    
            // Board zurücksetzen auf nach dem Gegnerzug
            moveGenerator.initializeBoard(fenAfterOppMove);
    
            // nächste Iteration starten
            generateOpeningBookStarting(moveGenerator, mcts, writer, player, moveProcedureWithOppMove, depth + 1);
    
            // Board zurücksetzen auf nach dem Spielerzug
            moveGenerator.initializeBoard(fenStorage);
        }
        // Ursprüngliches Board zurücksetzen
        moveGenerator.initializeBoard(fenStorage);
    }
}

