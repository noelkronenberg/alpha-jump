package search;

import game.Color;
import game.MoveGenerator;
import java.util.*;
import game.Piece;

public class Evaluation {

    // START: evaluation

    public static int getScore(Piece piece, int score, int weight) {
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
                        score = getScore(piece, score, weight);
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
                        score = getScore(piece, score, weight);
                    }
                }
                weight -= 1;
            }
        }

        return score;
    }

    public static int ratePosition(MoveGenerator moveGenerator, Color color) {
        int score = 0;

        if (color == Color.BLUE) {
            // check if winning position (for BLUE)
            if (moveGenerator.doesBaseRowContainColor(Color.BLUE,7)) {
                return 100000;
            }
            score = getScoreWrapper(moveGenerator, Color.BLUE) - getScoreWrapper(moveGenerator, Color.RED);
        }

        else if (color == Color.RED) {
            // check if winning position (for RED)
            if (moveGenerator.doesBaseRowContainColor(Color.RED,0)) {
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
        Comparator<Integer> comparator = (move1, move2) -> {
            int newRow_move1 = (move1 % 100) % 100;
            int newRow_move2 = (move2 % 100) % 100;

            boolean isMod1Zero;
            boolean isMod2Zero;

            if (color == Color.RED) {
                isMod1Zero = newRow_move1 == 0;
                isMod2Zero = newRow_move2 == 0;
            } else {
                isMod1Zero = newRow_move1 == 7;
                isMod2Zero = newRow_move2 == 7;
            }

            // winning moves first
            if (isMod1Zero && !isMod2Zero) {
                return -1;
            } else if (!isMod1Zero && isMod2Zero) {
                return 1;

            // other moves
            } else {
                if (color == Color.RED) {
                    return Integer.compare(newRow_move1, newRow_move2);
                } else {
                    return Integer.compare(newRow_move2, newRow_move1);
                }
            }
        };

        moves.sort(comparator);
    }

    // END: move ordering

    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        LinkedHashMap<Integer, List<Integer>> moves = moveGenerator.getMovesWrapper("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b");
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

        System.out.println();
        System.out.println("Score: " + ratePosition(moveGenerator, Color.BLUE));
    }
}
