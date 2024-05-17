package search;

import game.Color;
import game.MoveGenerator;
import java.util.*;
import game.Piece;

public class Evaluation {

    // START: evaluation

    public static int getScore(Piece piece, int score, int row, int column, int weight) {
        switch (piece) {
            case Piece.SINGLE:
                score += weight;
                break;
            case Piece.DOUBLE:
                score += weight * 2;
                break;
            case Piece.MIXED:
                score += weight * 2;
                break;
        }
        return score;
    }

    public static int getScoreWrapper(MoveGenerator moveGenerator, Color player) {
        int score = 0;
        int weight = 0;

        if (player == Color.BLUE) {
            weight = 1;
            // check row
            for (int row = 0; row < moveGenerator.getPieceBoard().length; row++) {
                // check column
                for (int column = 0; column < moveGenerator.getPieceBoard()[row].length; column++) {
                    // check color
                    if (moveGenerator.getColorBoard()[row][column] == Color.BLUE) {
                        // get score of piece
                        Piece piece = moveGenerator.getPieceBoard()[row][column];
                        score = getScore(piece, score, row, column, weight);
                    }
                }
                weight += 1;
            }

        } else if (player == Color.RED) {
            weight = 8;
            // check row
            for (int row = 0; row < moveGenerator.getPieceBoard().length; row++) {
                // check column
                for (int column = 0; column < moveGenerator.getPieceBoard()[row].length; column++) {
                    // check color
                    if (moveGenerator.getColorBoard()[row][column] == Color.RED) {
                        // get score of piece
                        Piece piece = moveGenerator.getPieceBoard()[row][column];
                        score = getScore(piece, score, row, column, weight);
                    }
                    weight -= 1;
                }
            }
        }

        return score;
    }

    public static int ratePosition(MoveGenerator moveGenerator, Color color) {
        int score = 0;

        if (color == Color.BLUE) {
            // check if winning position (for BLUE)
            if (moveGenerator.doesBaseRowContainEnemy(Color.BLUE,7)) {
                return 100000;
            }
            score = getScoreWrapper(moveGenerator, Color.BLUE) - getScoreWrapper(moveGenerator, Color.RED);
        }

        else if (color == Color.RED) {
            // check if winning position (for RED)
            if (moveGenerator.doesBaseRowContainEnemy(Color.RED,0)) {
                return 100000;
            } else {
                score = getScoreWrapper(moveGenerator, Color.RED) - getScoreWrapper(moveGenerator, Color.BLUE);
            }
        }

        return score;
    }

    public int rateMove(MoveGenerator gameState, Color color, int startPosition, int endPosition) {
        int result = 0;

        MoveGenerator nextState = new MoveGenerator();
        nextState.initializeBoard();
        nextState.setColorBoard(gameState.getColorBoard());
        nextState.setPieceBoard(gameState.getPieceBoard());

        result -= ratePosition(nextState, color);
        nextState.movePiece(startPosition, endPosition);
        result += ratePosition(nextState, color);

        return result;
    }

    // END: evaluation

    // START: move ordering

    public static LinkedList<Integer> convertMovesToList(LinkedHashMap<Integer, List<Integer>> moves) {
        LinkedList<Integer> movesList = new LinkedList<>();

        for (int startPosition : moves.keySet()) {
            List<Integer> possibleEndPositions = moves.get(startPosition);
            for (int endPosition : possibleEndPositions) {
                int fullMove = startPosition * 100 + endPosition;
                movesList.add(fullMove);
            }
        }

        return movesList;
    }

    public static LinkedHashMap<Integer, List<Integer>> convertMovesToMap(List<Integer> movesList) {
        LinkedHashMap<Integer, List<Integer>> movesMap = new LinkedHashMap<>();

        for (int fullMove : movesList) {
            int startPosition = fullMove / 100;
            int endPosition = fullMove % 100;

            if (!movesMap.containsKey(startPosition)) {
                movesMap.put(startPosition, new ArrayList<>());
            }

            movesMap.get(startPosition).add(endPosition);
        }

        return movesMap;
    }

    public static void orderMoves(LinkedList<Integer> moves, Color color) {
        if (color == Color.RED) {
            moves.sort(Comparator.comparingInt(move -> (move % 100) % 100));
        } else {
            moves.sort(Comparator.comparingInt(move -> ((int) move % 100) % 100).reversed());
        }
    }

    // END: move ordering

    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        LinkedHashMap<Integer, List<Integer>> moves = moveGenerator.getMovesWrapper("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b");
        System.out.println(moves);

        System.out.println();
        System.out.println("Moves as list: ");
        LinkedList<Integer> movesList = convertMovesToList(moves);
        System.out.println(movesList);

        System.out.println();
        System.out.println("Sorted moves: ");
        orderMoves(movesList, Color.BLUE);
        System.out.println(movesList);

        System.out.println();
        System.out.println("Moves as map again: ");
        System.out.println(convertMovesToMap(movesList));

        String s = "6/8/8/4b03/3r04/8/8/6";
    }
}
