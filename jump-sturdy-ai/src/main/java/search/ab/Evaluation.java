package search.ab;

import game.Color;
import game.Piece;
import game.MoveGenerator;

import java.util.*;

/**
 * Provides methods for evaluating game positions.
 */
public class Evaluation {

    // hyperparameters
    public static double possibleMovesWeight = 0.01;
    public static double protectedPiecesWeight = 0.1;
    public static double doubleWeight = 2;
    public static double mixedWeight = 2;
    public static double closenessWeight = 1;
    public static double closenessWeightTotal = closenessWeight * 8;

    // START: evaluation

    /**
     * Computes the score (contribution) of a piece based on its type and weight.
     *
     * @param piece The type of piece (SINGLE, DOUBLE, or MIXED).
     * @param score The current score to be updated.
     * @param weight The weight multiplier for the piece type.
     * @return The updated score after adding the piece's contribution.
     */
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

    /**
     * Computes the score of the current game position for a specified player.
     *
     * @param moveGenerator The MoveGenerator object providing game state information.
     * @param player The color of the player for whom the score is computed (BLUE or RED).
     * @param depth The current depth of evaluation in the search tree.
     * @return The computed score for the player in the current position.
     */
    public static double getScoreWrapper(MoveGenerator moveGenerator, Color player, double depth) {
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

    /**
     * Computes the score of the current game position for a player considering opponent's moves.
     * Optimised for AI search
     *
     * @param moveGenerator The MoveGenerator object providing game state information.
     * @param player The color of the player for whom the score is computed (BLUE or RED).
     * @param depth The current depth of evaluation in the search tree.
     * @param moves The opponent's moves represented as a map of combined integer start and end positions.
     * @return The computed score for the player in the current position.
     */
    public static double getScoreWrapperKI(MoveGenerator moveGenerator, Color player, double depth, LinkedHashMap<Integer, List<Integer>> moves) { 
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

    /**
     * Rates the overall position of the game for a specified player.
     *
     * @param moveGenerator The MoveGenerator object providing game state information.
     * @param color The color of the player for whom the position is rated (BLUE or RED).
     * @param depth The current depth of evaluation in the search tree.
     * @return The rating of the current position for the specified player.
     */
    public static double ratePosition(MoveGenerator moveGenerator, Color color, int depth, String fen) {
        double score = 0;

        if (color == Color.BLUE) {
            score = getScoreWrapper(moveGenerator, Color.BLUE, depth) - getScoreWrapper(moveGenerator, Color.RED, depth);
        }

        else if (color == Color.RED) {
            score = getScoreWrapper(moveGenerator, Color.RED, depth) - getScoreWrapper(moveGenerator, Color.BLUE, depth);
        }
        return score;
    }

    /**
     * Rates the overall position of the game for a player considering opponent's moves.
     * Optimised for AI search
     *
     * @param moveGenerator The MoveGenerator object providing game state information.
     * @param color The color of the player for whom the position is rated (BLUE or RED).
     * @param depth The current depth of evaluation in the search tree.
     * @param moves The opponent's moves represented as a map of combined integer start and end positions.
     * @param currentColor The current color of the AI player.
     * @return The rating of the current position for the specified player.
     */
    public static double ratePositionKI(MoveGenerator moveGenerator, Color color, int depth, String fen, LinkedHashMap<Integer,List<Integer>> moves, Color currentColor) {
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

    /**
     * Computes the score of the current game position for a specified player in an old version for benchmarking.
     *
     * @param moveGenerator The MoveGenerator object providing game state information.
     * @param player The color of the player for whom the score is computed (BLUE or RED).
     * @param fen The FEN representation of the current board position.
     * @return The computed score for the player in the current position.
     */
    public static double getScoreWrapperOld(MoveGenerator moveGenerator, Color player, String fen) {
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

    /**
     * Rates the overall position of the game for a specified player in an old version for benchmarking.
     *
     * @param moveGenerator The MoveGenerator object providing game state information.
     * @param color The color of the player for whom the position is rated (BLUE or RED).
     * @param fen The FEN representation of the current board position.
     * @return The rating of the current position for the specified player.
     */
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

    /**
     * Rates a move in the game based on its impact on the game position for a specified player.
     *
     * @param gameState The current MoveGenerator object representing the game state.
     * @param color The color of the player making the move (BLUE or RED).
     * @param startPosition The combined integer starting position of the move.
     * @param endPosition The combined integer ending position of the move.
     * @param depth The current depth of evaluation in the search tree.
     * @return The computed score change due to the move.
     */
    public double rateMove(MoveGenerator gameState, Color color, int startPosition, int endPosition, int depth, String fen) {
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

    /**
     * Converts a map of moves to a list for easier manipulation.
     *
     * @param moves A map representing moves as combined integer start positions mapped to lists of end positions.
     * @return A map of moves where each move is represented as a combined integer.
     */
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

    /**
     * Converts a list of moves (as combined integers) back to a map representation.
     *
     * @param movesList A list of moves where each move is represented as a combined integer.
     * @return A map representing moves as combined integer start positions mapped to lists of end positions.
     */
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

    /**
     * Orders moves in a list based on whether a move winning, capturing, and closeness to target row.
     *
     * @param moves A list of moves where each move is represented as a combined integer.
     * @param color The color of the player for whom moves are being ordered (BLUE or RED).
     * @param game The MoveGenerator object providing game state information.
     */
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

    /**
     * Orders moves in a list in an old version for benchmarking based on whether a move winning and closeness to target row.
     *
     * @param moves A list of moves where each move is represented as a combined integer.
     * @param color The color of the player for whom moves are being ordered (BLUE or RED).
     */
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
}
