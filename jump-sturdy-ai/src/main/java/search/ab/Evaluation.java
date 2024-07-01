package search.ab;

import game.Color;
import game.Piece;
import game.MoveGenerator;

import java.util.*;

public class Evaluation {

    public static double possibleMovesWeight = 0.01;
    public static double protectedPiecesWeight = 0.1;
    public static double doubleWeight = 2;
    public static double mixedWeight = 2;
    public static double closenessWeight = 1;
    public static double closenessWeightTotal = closenessWeight * 8;

    // START: evaluation

    public static double getScore(Piece piece, double score, double weight) {
        switch (piece) {
            case Piece.SINGLE:
                score += weight;
                break;
            case Piece.DOUBLE:
                score += weight * Evaluation.doubleWeight;
                break;
            case Piece.MIXED:
                score += weight * Evaluation.mixedWeight;
                break;
        }
        return score;
    }

    public static double getScoreWrapper(MoveGenerator moveGenerator, Color player, double depth, String fen) {
        double score = 0;
        double weight;

        score += moveGenerator.getTotalPossibleMoves() * Evaluation.possibleMovesWeight;
        score += moveGenerator.getProtectedPieces() * Evaluation.protectedPiecesWeight;

        if (player == Color.BLUE) {
            weight = Evaluation.closenessWeight;

            // check gameOver of other player
            if (moveGenerator.isGameOver(moveGenerator.generateAllPossibleMoves(Color.RED), Color.RED)) {
                score += 100000 * (1 + (1 / depth));
            }

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
                weight += Evaluation.closenessWeight;
            }

        } else if (player == Color.RED) {
            weight = Evaluation.closenessWeightTotal;

            // check gameOver of other player
            if (moveGenerator.isGameOver(moveGenerator.generateAllPossibleMoves(Color.BLUE), Color.BLUE)) {
                score += 100000 * (1 + (1 / depth));
            }

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
                weight -= Evaluation.closenessWeight;
            }
        }

        return score;
    }

    public static double getScoreWrapperKI(MoveGenerator moveGenerator, Color player, double depth, LinkedHashMap<Integer, List<Integer>> moves) {   //moves Contains the enemys moves
        double score = 0;
        double weight;

        score += moveGenerator.getTotalPossibleMoves() * Evaluation.possibleMovesWeight;
        score += moveGenerator.getProtectedPieces() * Evaluation.protectedPiecesWeight;

        if (player == Color.BLUE) {
            weight = Evaluation.closenessWeight;

            // check gameOver of other player
            if (moveGenerator.isGameOver(moves, Color.RED)) {
                score += 100000 * (1 + (1 / depth));
            }

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
                weight += Evaluation.closenessWeight;
            }

        } else if (player == Color.RED) {
            weight = Evaluation.closenessWeightTotal;

            // check gameOver of other player
            if (moveGenerator.isGameOver(moves, Color.BLUE)) {
                score += 100000 * (1 + (1 / depth));
            }

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
                weight -= Evaluation.closenessWeight;
            }
        }

        return score;
    }

    public static double ratePosition(MoveGenerator moveGenerator, Color color, int depth, String fen) {
        double score = 0;

        if (color == Color.BLUE) {
            score = getScoreWrapper(moveGenerator, Color.BLUE, depth, fen) - getScoreWrapper(moveGenerator, Color.RED, depth, fen);
        }

        else if (color == Color.RED) {
            score = getScoreWrapper(moveGenerator, Color.RED, depth, fen) - getScoreWrapper(moveGenerator, Color.BLUE, depth, fen);
        }
        return score;
    }

    public static double ratePositionKI(MoveGenerator moveGenerator, Color color, int depth,String fen, LinkedHashMap<Integer,List<Integer>> moves, Color currentColor) { // a more efficient approach to ratePosition for the KI
        double score = 0;

        // TODO: difference between ourColor / currentColor

        if (color == Color.BLUE && currentColor != color) {
            score = getScoreWrapperKI(moveGenerator, Color.BLUE, depth, moves) - getScoreWrapperKI(moveGenerator, Color.RED, depth, moveGenerator.generateMaxOnePossibleMoveForKI(Color.BLUE));
        } else if (color == Color.BLUE && currentColor == color) {
            score = getScoreWrapperKI(moveGenerator, Color.BLUE, depth,moveGenerator.generateMaxOnePossibleMoveForKI(Color.RED)) - getScoreWrapperKI(moveGenerator, Color.RED, depth, moves);
        } else if (color == Color.RED && currentColor != color) {
            score = getScoreWrapperKI(moveGenerator, Color.RED, depth, moves) - getScoreWrapperKI(moveGenerator, Color.BLUE, depth,moveGenerator.generateMaxOnePossibleMoveForKI(Color.RED));
        } else {
            score = getScoreWrapperKI(moveGenerator, Color.RED, depth,moveGenerator.generateMaxOnePossibleMoveForKI(Color.BLUE)) - getScoreWrapperKI(moveGenerator, Color.BLUE, depth, moves);
        }

        return score;
    }

    // old version for benchmarking
    public static double getScoreWrapperOld(MoveGenerator moveGenerator, Color player,String fen) {
        double score = 0;
        int weight;

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

            // check gameOver of other player
            if (moveGenerator.isGameOver(moveGenerator.generateAllPossibleMoves(Color.RED), Color.RED)) {
                score = 100000;
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

            // check gameOver of other player
            if (moveGenerator.isGameOver(moveGenerator.generateAllPossibleMoves(Color.BLUE), Color.BLUE)) {
                score = 100000;
            }
        }

        return score;
    }

    // old version for benchmarking
    public static double ratePositionOld(MoveGenerator moveGenerator, Color color, String fen) {
        double score = 0;

        if (color == Color.BLUE) {
            // check if winning position (for BLUE)
            if (moveGenerator.doesBaseRowContainColor(Color.BLUE,7)) {
                return 100000;
            }
            score = getScoreWrapperOld(moveGenerator, Color.BLUE,fen) - getScoreWrapperOld(moveGenerator, Color.RED,fen);
        }

        else if (color == Color.RED) {
            // check if winning position (for RED)
            if (moveGenerator.doesBaseRowContainColor(Color.RED,0)) {
                return 100000;
            } else {
                score = getScoreWrapperOld(moveGenerator, Color.RED,fen) - getScoreWrapperOld(moveGenerator, Color.BLUE,fen);
            }
        }

        return score;
    }

    public double rateMove(MoveGenerator gameState, Color color, int startPosition, int endPosition, int depth,String fen) {
        double score = 0;

        MoveGenerator nextState = new MoveGenerator();
        nextState.initializeBoard();
        nextState.setColorBoard(gameState.getColorBoard());
        nextState.setPieceBoard(gameState.getPieceBoard());

        score -= ratePosition(nextState, color, depth,fen);
        nextState.movePiece(startPosition, endPosition);
        score += ratePosition(nextState, color, depth, fen);

        return score;
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

    public static void orderMoves(LinkedList<Integer> moves, Color color, MoveGenerator game) {
        Comparator<Integer> comparator = (move1, move2) -> {
            int newRow_move1 = (move1 % 100) / 10;
            int newRow_move2 = (move2 % 100) / 10;

            boolean isMod1Zero;
            boolean isMod2Zero;

            boolean is1TakingMove = game.capturingHM.containsKey(move1);
            boolean is2TakingMove = game.capturingHM.containsKey(move2);

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

            // taking moves second
            } else if (is1TakingMove && !is2TakingMove) {
                return -1;
            } else if (!is1TakingMove && is2TakingMove) {
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

    // old version for benchmarking
    public static void orderMovesOld(LinkedList<Integer> moves, Color color) {
        Comparator<Integer> comparator = (move1, move2) -> {
            int newRow_move1 = (move1 % 100) / 10;
            int newRow_move2 = (move2 % 100) / 10;

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

        String fen = moveGenerator.getFenFromBoard();

        System.out.println();
        System.out.println("Moves as list: ");
        LinkedList<Integer> movesList = convertMovesToList(moves);
        System.out.println(movesList);

        System.out.println();
        System.out.println("Sorted moves: ");
        orderMoves(movesList, Color.BLUE,moveGenerator);
        System.out.println(movesList);

        System.out.println();
        System.out.println("Moves as map again: ");
        System.out.println(convertMovesToMap(movesList));

        System.out.println();
        System.out.println("Score: " + ratePosition(moveGenerator, Color.BLUE,1,fen));
    }
}