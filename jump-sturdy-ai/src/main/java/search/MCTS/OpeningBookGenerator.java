package search.MCTS;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import game.Color;
import game.MoveGenerator;

public class OpeningBookGenerator {
    private static final int DEPTH = 3;
    private static int mctsIterations = 1000;
    private static Color startingPlayer = Color.BLUE;
    private static String board = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";

    public static void main(String[] args) {
        MoveGenerator initialState = new MoveGenerator();
        initialState.initializeBoard(board);
        initialState.printBoard(false);
        MCTS mcts = new MCTS();
        try (FileWriter writer = new FileWriter("opening_book.txt")) {
            generateOpeningBook(initialState, mcts, writer, 0,startingPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateOpeningBook(MoveGenerator moveGenerator, MCTS mcts, FileWriter writer, int depth, Color player) throws IOException {
        if (depth == DEPTH) {
            return;
        }
        
        int bestMove = MCTS.runMCTS(moveGenerator, player, mctsIterations);
        writer.write(depth + ": " + MoveGenerator.convertMoveToFEN(bestMove) + "\n");
        
        String fenStorage = moveGenerator.getFenFromBoard();
        moveGenerator.movePiece(bestMove);
        player = (player == Color.RED) ? Color.BLUE : Color.RED;
        generateOpeningBook(moveGenerator, mcts, writer, depth + 1, player);
        moveGenerator.initializeBoard(fenStorage); // Rückgängig machen, um den nächsten Zug zu simulieren
    }
}

