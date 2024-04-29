package main;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
                    possibleMoves.add(convertToNumber(row + 1, column));

                    // left
                    if (column > 0)
                        possibleMoves.add(convertToNumber(row, column - 1));

                    // right
                    if (column < 7)
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
                addKnightMoves(possibleMoves, row, column);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.BLACK));

        // white
        } else if (color == Color.WHITE) {

            // single piece
            if (pieceBoard[row][column] == Piece.SINGLE) {
                if (row > 0) {

                    // forward
                    possibleMoves.add(convertToNumber(row - 1, column));

                    // left
                    if (column > 0)
                        possibleMoves.add(convertToNumber(row, column - 1));

                    // right
                    if (column < 7)
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
                addKnightMoves(possibleMoves, row, column);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.WHITE));
        }

        return possibleMoves;
    }

    private void addKnightMoves(List<Integer> possibleMoves, int row, int column) {

        // possible moves
        int[][] knightMoves = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        // add moves
        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newColumn = column + move[1];
            possibleMoves.add(convertToNumber(newRow, newColumn));
        }
    }

    private boolean isValidMove(int move, Color pieceColor) {
        int row = move / 10;
        int column = move % 10;

        boolean withinBorder = row >= 0 && row < 8 && column >= 0 && column < 8;
        boolean notCut = (row != 0 || column != 0) && (row != 0 || column != 7) &&
                (row != 7 || column != 0) && (row != 7 || column != 7);
        boolean notOwn = colorBoard[row][column] == Color.EMPTY || colorBoard[row][column] != pieceColor;

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

        pieceBoard[fromRow][fromColumn] = Piece.EMPTY;
        colorBoard[fromRow][fromColumn] = Color.EMPTY;

        pieceBoard[toRow][toColumn] = piece;
        colorBoard[toRow][toColumn] = color;
    }

    Piece getPieceAtPosition(int position) {
        int row = position / 10;
        int column = position % 10;
        return pieceBoard[row][column];
    }

    void printBoard() {
        for (int row = 0; row < 8; row++) {
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

    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();

        System.out.println("Start:");
        moveGenerator.printBoard();

        int testPosition = 14;
        Color testColor = Color.BLACK;

        System.out.println();
        System.out.println("Move:");
        List<Integer> possibleMoves = moveGenerator.generatePossibleMoves(testPosition, testColor);
        moveGenerator.movePiece(testPosition, possibleMoves.getFirst(), moveGenerator.getPieceAtPosition(testPosition), testColor);
        moveGenerator.printBoard();
    }

}