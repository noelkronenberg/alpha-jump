package app;

import game.Color;
import game.MoveGenerator;

import java.util.Scanner;

/**
 * Represents a player in a game.
 * Allows the player to input moves and validates them based on the current game state.
 */
public class Player {

    Scanner scanner = new Scanner(System.in);
    MoveGenerator moveGenerator = new MoveGenerator();

    /**
     * Gets a valid move from the player.
     *
     * @param fen The current board state in FEN notation.
     * @param color The color of the player making the move.
     * @return A valid move input by the player in FEN notation.
     */
    public String getMove(String fen, Color color) {

        // set up board
        moveGenerator.initializeBoard(fen);

        // get move
        System.out.printf("Enter your move (as %s): \n", color);
        String move = this.scanner.nextLine();
        System.out.println();

        // convert move
        int[] moveArray = moveGenerator.convertStringToPosWrapper(move);
        int moveInt = moveArray[0] * 100 + moveArray[1];

        // check if move is valid
        boolean isValid;
        try {
            isValid = moveGenerator.isValidMove(moveInt, color);
        } catch (ArrayIndexOutOfBoundsException e) {
            isValid = false;
        }

        // ask again if move is invalid
        if (!isValid) {
            System.out.println("Invalid move!");
            System.out.printf("Enter your move (as %s): \n", color);
            move = this.scanner.nextLine();
            System.out.println();
        }

        return move;
    }

}
