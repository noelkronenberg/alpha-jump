package game;

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

                    // Red
                    if (row == 0) {
                        // border
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = null;
                            colorBoard[row][column] = null;
                        // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.RED;
                        }

                    // Blue
                    } else if (row == 7) {
                        // border
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = null;
                            colorBoard[row][column] = null;
                        // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.BLUE;
                        }
                    }

                }

                // second row for each side
                else if (row == 1 || row == 6) {

                    // Red
                    if (row == 1) {
                        // no piece
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = Piece.EMPTY;
                            colorBoard[row][column] = Color.EMPTY;
                         // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.RED;
                        }

                    // Blue
                    } else if (row == 6) {
                        // no piece
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = Piece.EMPTY;
                            colorBoard[row][column] = Color.EMPTY;
                        // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.BLUE;
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
            return Color.RED; // ROT = RED
        } else {
            return Color.BLUE; // BLAU = BLUE
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

        // Red
        if (color == Color.RED) {

            // single piece
            if (pieceBoard[row][column] == Piece.SINGLE) {
                if (row > 0) {

                    // forward
                    if (colorBoard[row - 1][column] != Color.BLUE) {
                        possibleMoves.add(convertToNumber(row - 1, column));
                    }

                    // left
                    if (column > 0 && colorBoard[row][column - 1] != Color.BLUE)
                        possibleMoves.add(convertToNumber(row, column - 1));

                    // right
                    if (column < 7 && colorBoard[row][column + 1] != Color.BLUE)
                        possibleMoves.add(convertToNumber(row, column + 1));

                    // left diagonal
                    if (column > 0 && colorBoard[row - 1][column - 1] == Color.BLUE) {
                        possibleMoves.add(convertToNumber(row - 1, column - 1));
                    }

                    // right diagonal
                    if (column < 7 && colorBoard[row - 1][column + 1] == Color.BLUE) {
                        possibleMoves.add(convertToNumber(row - 1, column + 1));
                    }
                }
            }

            // double piece
            else if (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED) {
                addKnightMoves(possibleMoves, row, column, color);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.RED));

        // Blue
        } else if (color == Color.BLUE) {

            // single piece
            if (pieceBoard[row][column] == Piece.SINGLE) {
                if (row < 7) {

                    // forward
                    if (colorBoard[row + 1][column] != Color.RED) {
                        possibleMoves.add(convertToNumber(row + 1, column));
                    }

                    // left
                    if (column > 0 && colorBoard[row][column - 1] != Color.RED)
                        possibleMoves.add(convertToNumber(row, column - 1));

                    // right
                    if (column < 7 && colorBoard[row][column + 1] != Color.RED)
                        possibleMoves.add(convertToNumber(row, column + 1));

                    // left diagonal
                    if (column > 0 && colorBoard[row + 1][column - 1] == Color.RED) {
                        possibleMoves.add(convertToNumber(row + 1, column - 1));
                    }

                    // right diagonal
                    if (column < 7 && colorBoard[row + 1][column + 1] == Color.RED) {
                        possibleMoves.add(convertToNumber(row + 1, column + 1));
                    }
                }
            }

            // double piece
            else if (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED) {
                addKnightMoves(possibleMoves, row, column, color);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.BLUE));
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
            if (color == Color.RED) {
                if (newRow > row) {
                    continue;
                }
            } else if (color == Color.BLUE) {
                if (newRow < row) {
                    continue;
                }
            }

            // no moves outside the board
            if (newRow < 0 || newColumn < 0) {
                continue;
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
        boolean own = (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED)
                && colorBoard[row][column] == pieceColor;

        return withinBorder & notCut & !own;
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
            Color oppositeColor = (color == Color.RED) ? Color.BLUE : Color.RED;
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
        for (int row = 7; row >= 0; row--) {
            System.out.print(row + "   ");
            for (int column = 0; column <= 7; column++) {

                // border
                if (pieceBoard[row][column] == null) {
                    System.out.print("   ");
                }

                // no piece
                else if (pieceBoard[row][column] == Piece.EMPTY) {
                    System.out.print(" . ");
                }

                else {
                    // Red piece
                    if (colorBoard[row][column] == Color.RED) {
                        switch (pieceBoard[row][column]) {
                            case SINGLE:
                                System.out.print(" R ");
                                break;
                            case DOUBLE:
                                System.out.print("RR ");
                                break;
                            case MIXED:
                                System.out.print("BR ");
                                break;
                        }

                    // Blue piece
                    } else {
                        switch (pieceBoard[row][column]) {
                            case SINGLE:
                                System.out.print(" B ");
                                break;
                            case DOUBLE:
                                System.out.print("BB ");
                                break;
                            case MIXED:
                                System.out.print("RB ");
                                break;
                        }
                    }

                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    void exampleSequence(int rounds, int positionRed, int positionBlue) {
        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();

        System.out.println("Start:");
        moveGenerator.printBoard();

        int moveRed;
        int moveBlue;

        List<Integer> possibleMoves;

        for (int round = 1; round < rounds; round++) {

            System.out.println();
            System.out.println("Move " + round + " (Red): ");

            possibleMoves = moveGenerator.generatePossibleMoves(positionRed, Color.RED);

            if (!possibleMoves.isEmpty()) {
                System.out.println("Possible: " + possibleMoves);
                moveRed = possibleMoves.getFirst();
                System.out.println("Selected: " + moveRed);
                moveGenerator.movePiece(positionRed, moveRed, moveGenerator.getPieceAtPosition(positionRed), Color.RED);
                moveGenerator.printBoard();
                positionRed = moveRed;

                if (positionRed / 10 == 7) {
                    System.out.println();
                    System.out.println("Red wins!");
                    return;
                }
            } else {
                System.out.println("No possible moves found!");
            }

            System.out.println();
            System.out.println("Move " + round + " (Blue): ");
            possibleMoves = moveGenerator.generatePossibleMoves(positionBlue, Color.BLUE);
            if (!possibleMoves.isEmpty()) {
                System.out.println("Possible: " + possibleMoves);
                moveBlue = possibleMoves.getFirst();
                System.out.println("Selected: " + moveBlue);
                moveGenerator.movePiece(positionBlue, moveBlue, moveGenerator.getPieceAtPosition(positionBlue), Color.BLUE);
                moveGenerator.printBoard();
                positionBlue = moveBlue;

                if (positionBlue / 10 == 0) {
                    System.out.println();
                    System.out.println("Blue wins!");
                    return;
                }
            } else {
                System.out.println("No possible moves found!");
            }

        }
    }

    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        String fen = "5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04";
        for (int i = 0; i < 1; i++) {
            moveGenerator.initializeBoard(fen);
            moveGenerator.printBoard();
            System.out.println(moveGenerator.generateAllPossibleMoves(Color.RED));
        }
    }
}