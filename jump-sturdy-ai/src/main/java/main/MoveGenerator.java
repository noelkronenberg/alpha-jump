package main;

import javax.swing.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoveGenerator {

    Piece[][] pieceBoard;
    Color[][] colorBoard;

    void initializeBoard() {
        pieceBoard = new Piece[8][8];
        colorBoard = new Color[8][8];

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                // first rows for each side
                if (row == 0 || row == 7) {

                    // black
                    if (row == 0) {
                        // border
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = null;
                            colorBoard[row][column] = null;
                        // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.BLACK;
                        }

                    // white
                    } else if (row == 7) {
                        // border
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = null;
                            colorBoard[row][column] = null;
                        // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.WHITE;
                        }
                    }

                }

                // second row for each side
                else if (row == 1 || row == 6) {

                    // black
                    if (row == 1) {
                        // no piece
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = Piece.EMPTY;
                            colorBoard[row][column] = Color.EMPTY;
                         // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.BLACK;
                        }

                    // white
                    } else if (row == 6) {
                        // no piece
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = Piece.EMPTY;
                            colorBoard[row][column] = Color.EMPTY;
                        // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.WHITE;
                        }
                    }

                }

                // middle
                else if (2 <= row & row <= 5) {
                    pieceBoard[row][column] = Piece.EMPTY;
                    colorBoard[row][column] = Color.EMPTY;
                }

            }
        }

    }

    Color getColor(char c) {
        if (c == 'r') {
            return Color.BLACK; // ROT = BLACK
        } else {
            return Color.WHITE; // BLAU = WHITE
        }
    }

    void initializeBoard(String fen) {
        pieceBoard = new Piece[8][8];
        colorBoard = new Color[8][8];
        char[] fenArray = fen.toCharArray();
        fillBoardEdges(); // is used to fill the shortened edges
        int fenCounter = 0;
        int counter = 1; // is used to jump one column, depending on whether we are in a shot or "normal" row
        for (int row = 0; row < 8; row++) {

            if (row == 7) {
                counter = 1;
            }

            for (int column = 0; column < 8; column++) {
                char fenChar = fenArray[fenCounter];
                if (Character.isLetter(fenChar)) {

                    // CASE: PIECE SINGLE
                    if (fenArray[fenCounter + 1] == '0') {
                        pieceBoard[row][column + counter] = Piece.SINGLE;
                        colorBoard[row][column + counter] = getColor(fenChar);
                    }

                    // CASE: PIECE STACKED
                    else {
                        char fenCharSecondPos = fenArray[fenCounter + 1];

                        // CASE: PIECE MIXED STACK
                        if (fenCharSecondPos != fenChar) {
                            pieceBoard[row][column + counter] = Piece.MIXED;
                            colorBoard[row][column + counter] = getColor(fenCharSecondPos);
                        }

                        // CASE: PIECE SAME COLOR STACK
                        else {
                            pieceBoard[row][column + counter] = Piece.DOUBLE;
                            colorBoard[row][column + counter] = getColor(fenCharSecondPos);
                        }
                    }

                    fenCounter += 2; // we jump 2 lines in the fen, because it either a single: e.g. r0 or stack: e.g. bb
                    if (column + counter == 7 || row == 7 && column + counter == 6) { // first case: end of a normal length line or second case: we are in the last line at the end
                        column = 7;
                        counter = 0;
                        fenCounter++;
                    }

                // CASE: fenChar is a number, then we fill the places with EMPTY
                } else if (Character.isDigit(fenChar)) {
                    int numberOfFreePlaces = fenChar - '0'; // gives the int value for the Character: alternative: Character.getNumericValue(fenChar)
                    for (int j = 0; j < numberOfFreePlaces; j++) {
                        pieceBoard[row][column + counter + j] = Piece.EMPTY;
                        colorBoard[row][column + counter + j] = Color.EMPTY;
                    }
                    counter += numberOfFreePlaces - 1; // numberOfFreePlaces-1 due to column already jumping 1 for each iteration of for-loop
                    fenCounter++;

                    // CASE: end of a normal length line or second case: we are in the last line at the end
                    if (column + counter == 7 || row == 7 && column + counter == 6) {
                        column = 7;
                        counter = 0;
                        fenCounter++;
                    }
                }

                // CASE: this is only the case when we are in row 0, and we are at the end of it: fenChar will be '/' and we use this to jump to the next line
                else {
                    column = 7;
                    fenCounter++;
                    counter = 0;
                }
            }
        }
    }

    void fillBoardEdges() {
        pieceBoard[0][0] = null;
        colorBoard[0][0] = null;
        pieceBoard[0][7] = null;
        colorBoard[0][7] = null;
        pieceBoard[7][0] = null;
        colorBoard[7][0] = null;
        pieceBoard[7][7] = null;
        colorBoard[7][7] = null;
    }

    List<Integer> generatePossibleMoves(int position, Color color) {
        int row = position / 10;
        int column = position % 10;

        List<Integer> possibleMoves = new ArrayList<>();

        // black
        if (color == Color.BLACK) {

            // single piece
            if (pieceBoard[row][column] == Piece.SINGLE) {
                if (row < 7) {

                    // forward
                    if (colorBoard[row + 1][column] != Color.WHITE) {
                        possibleMoves.add(convertToNumber(row + 1, column));
                    }

                    // left
                    if (column > 0 && colorBoard[row][column - 1] != Color.WHITE)
                        possibleMoves.add(convertToNumber(row, column - 1));

                    // right
                    if (column < 7 && colorBoard[row][column + 1] != Color.WHITE)
                        possibleMoves.add(convertToNumber(row, column + 1));

                    // left diagonal
                    if (column > 0 && colorBoard[row + 1][column - 1] == Color.WHITE) {
                        possibleMoves.add(convertToNumber(row + 1, column - 1));
                    }

                    // right diagonal
                    if (column < 7 && colorBoard[row + 1][column + 1] == Color.WHITE) {
                        possibleMoves.add(convertToNumber(row + 1, column + 1));
                    }
                }
            }

            // double piece
            else if (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED) {
                addKnightMoves(possibleMoves, row, column, color);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.BLACK));

        // white
        } else if (color == Color.WHITE) {

            // single piece
            if (pieceBoard[row][column] == Piece.SINGLE) {
                if (row > 0) {

                    // forward
                    if (colorBoard[row - 1][column] != Color.BLACK) {
                        possibleMoves.add(convertToNumber(row - 1, column));
                    }

                    // left
                    if (column > 0 && colorBoard[row][column - 1] != Color.BLACK)
                        possibleMoves.add(convertToNumber(row, column - 1));

                    // right
                    if (column < 7 && colorBoard[row][column + 1] != Color.BLACK)
                        possibleMoves.add(convertToNumber(row, column + 1));

                    // left diagonal
                    if (column > 0 && colorBoard[row - 1][column - 1] == Color.BLACK) {
                        possibleMoves.add(convertToNumber(row - 1, column - 1));
                    }

                    // right diagonal
                    if (column < 7 && colorBoard[row - 1][column + 1] == Color.BLACK) {
                        possibleMoves.add(convertToNumber(row - 1, column + 1));
                    }
                }
            }

            // double piece
            else if (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED) {
                addKnightMoves(possibleMoves, row, column, color);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.WHITE));
        }

        return possibleMoves;
    }

    private void addKnightMoves(List<Integer> possibleMoves, int row, int column, Color color) {

        // possible moves
        int[][] knightMoves = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        // add moves
        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newColumn = column + move[1];

            if (newRow >= 8 || newColumn >= 8) {
                continue;
            }

            // no moving backwards
            if (color == Color.BLACK) {
                if (newRow < row) {
                    continue;
                }
            } else if (color == Color.WHITE) {
                if (newRow > row) {
                    continue;
                }
            }

            // cannot move to own double
            if (!((pieceBoard[newRow][newColumn] == Piece.DOUBLE || pieceBoard[newRow][newColumn] == Piece.MIXED)
                    && colorBoard[newRow][newColumn] == color)) {
                possibleMoves.add(convertToNumber(newRow, newColumn));
            }
        }
    }

    private boolean isValidMove(int move, Color pieceColor) {
        int row = move / 10;
        int column = move % 10;

        boolean withinBorder = row >= 0 && row < 8 && column >= 0 && column < 8;
        boolean notCut = (row != 0 || column != 0) && (row != 0 || column != 7) &&
                (row != 7 || column != 0) && (row != 7 || column != 7);
        boolean notOwn = pieceBoard[row][column] != Piece.DOUBLE || (pieceBoard[row][column] != Piece.MIXED && colorBoard[row][column] == pieceColor);

        return withinBorder & notCut & notOwn;
    }

    private int convertToNumber(int row, int column) {
        return row * 10 + column;
    }

    void movePiece(int start, int end, Piece piece, Color color) {
        int fromRow = start / 10;
        int fromColumn = start % 10;
        int toRow = end / 10;
        int toColumn = end % 10;

        // adjust old square

        // single
        if (piece == Piece.SINGLE) {
            pieceBoard[fromRow][fromColumn] = Piece.EMPTY;
            colorBoard[fromRow][fromColumn] = Color.EMPTY;
        }

        // double
        else if (piece == Piece.DOUBLE) {
            pieceBoard[fromRow][fromColumn] = Piece.SINGLE;
        }

        // mixed
        else if (piece == Piece.MIXED) {
            pieceBoard[fromRow][fromColumn] = Piece.SINGLE;
            Color oppositeColor = (color == Color.BLACK) ? Color.WHITE : Color.BLACK;
            colorBoard[fromRow][fromColumn] = oppositeColor;
        }

        // adjust new square

        // empty or opposite color
        if (colorBoard[toRow][toColumn] == Color.EMPTY ||
                (colorBoard[toRow][toColumn] != color && pieceBoard[toRow][toColumn] == Piece.SINGLE)) {
            pieceBoard[toRow][toColumn] = Piece.SINGLE;
            colorBoard[toRow][toColumn] = color;
        }

        // opposite color and double or mixed
        else if (colorBoard[toRow][toColumn] != color &&
                (pieceBoard[toRow][toColumn] == Piece.DOUBLE || pieceBoard[toRow][toColumn] == Piece.MIXED)) {
            pieceBoard[toRow][toColumn] = Piece.MIXED;
            colorBoard[toRow][toColumn] = color;
        }

        // same color
        else if (colorBoard[toRow][toColumn] == color && pieceBoard[toRow][toColumn] == Piece.SINGLE) {
            pieceBoard[toRow][toColumn] = Piece.DOUBLE;
        }
    }

    Piece getPieceAtPosition(int position) {
        int row = position / 10;
        int column = position % 10;
        return pieceBoard[row][column];
    }

    List<Map.Entry<Integer, List<Integer>>> generateAllPossibleMoves(Color color) {
        List<Map.Entry<Integer, List<Integer>>> allPossibleMoves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (colorBoard[row][column] == color) {
                    int position = convertToNumber(row, column);
                    List<Integer> piecePossibleMoves = generatePossibleMoves(position, color);
                    allPossibleMoves.add(new AbstractMap.SimpleEntry<>(position, piecePossibleMoves));
                }
            }
        }

        return allPossibleMoves;
    }

    void printBoard() {
        System.out.println("     0   1   2   3   4   5   6   7");
        System.out.println();
        for (int row = 0; row < 8; row++) {
            System.out.print(row + "   ");
            for (int column = 0; column < 8; column++) {

                // border
                if (pieceBoard[row][column] == null) {
                    System.out.print("   ");
                }

                // no piece
                else if (pieceBoard[row][column] == Piece.EMPTY) {
                    System.out.print(" . ");
                }

                else {
                    // black piece
                    if (colorBoard[row][column] == Color.BLACK) {
                        switch (pieceBoard[row][column]) {
                            case SINGLE:
                                System.out.print(" B ");
                                break;
                            case DOUBLE:
                                System.out.print(" BB");
                                break;
                            case MIXED:
                                System.out.print(" WB");
                                break;
                        }

                    // white piece
                    } else {
                        switch (pieceBoard[row][column]) {
                            case SINGLE:
                                System.out.print(" W ");
                                break;
                            case DOUBLE:
                                System.out.print(" WW");
                                break;
                            case MIXED:
                                System.out.print(" BW");
                                break;
                        }
                    }

                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    void exampleSequence(int rounds, int positionBlack, int positionWhite) {
        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();

        System.out.println("Start:");
        moveGenerator.printBoard();

        int moveBlack;
        int moveWhite;

        List<Integer> possibleMoves;

        for (int round = 1; round < rounds; round++) {

            System.out.println();
            System.out.println("Move " + round + " (black): ");

            possibleMoves = moveGenerator.generatePossibleMoves(positionBlack, Color.BLACK);

            if (!possibleMoves.isEmpty()) {
                System.out.println("Possible: " + possibleMoves);
                moveBlack = possibleMoves.getFirst();
                System.out.println("Selected: " + moveBlack);
                moveGenerator.movePiece(positionBlack, moveBlack, moveGenerator.getPieceAtPosition(positionBlack), Color.BLACK);
                moveGenerator.printBoard();
                positionBlack = moveBlack;

                if (positionBlack / 10 == 7) {
                    System.out.println();
                    System.out.println("Black wins!");
                    return;
                }
            } else {
                System.out.println("No possible moves found!");
            }

            System.out.println();
            System.out.println("Move " + round + " (white): ");
            possibleMoves = moveGenerator.generatePossibleMoves(positionWhite, Color.WHITE);
            if (!possibleMoves.isEmpty()) {
                System.out.println("Possible: " + possibleMoves);
                moveWhite = possibleMoves.getFirst();
                System.out.println("Selected: " + moveWhite);
                moveGenerator.movePiece(positionWhite, moveWhite, moveGenerator.getPieceAtPosition(positionWhite), Color.WHITE);
                moveGenerator.printBoard();
                positionWhite = moveWhite;

                if (positionWhite / 10 == 0) {
                    System.out.println();
                    System.out.println("White wins!");
                    return;
                }
            } else {
                System.out.println("No possible moves found!");
            }

        }
    }

    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        String fen = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0";
        for (int i = 0; i < 1; i++) {
            moveGenerator.initializeBoard(fen);
            moveGenerator.printBoard();
            // moveGenerator.exampleSequence(9, 14, 61);

            moveGenerator.generateAllPossibleMoves(Color.BLACK);
        }
    }
}