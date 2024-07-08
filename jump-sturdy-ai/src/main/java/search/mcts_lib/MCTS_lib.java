package search.mcts_lib;

import game.Color;
import game.MoveGenerator;
import search.ab.Evaluation;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Implementation of Monte Carlo Tree Search (MCTS) for creating an opening library document.
 */
public class MCTS_lib {

    // hyperparameters
    private final Random random = new Random();
    private final double EXPLORATION_PARAM = 1.3;
    public double timeLimit = 10000;

    /**
     * Runs the Monte Carlo Tree Search (MCTS) algorithm to determine the best move.
     *
     * @param moveGenerator The MoveGenerator object used to play the move lines on.
     * @param color The color of the player to move.
     * @return The best move integer determined by MCTS.
     */
    public int runMCTS(MoveGenerator moveGenerator, Color color) {
        Color startingPlayer = color;
        double endTime = System.currentTimeMillis() + timeLimit;
        String initialState = moveGenerator.getFenFromBoard();
        MCTSNode_lib root = new MCTSNode_lib(0, null, 0);

        // for (int i = 0; i < iterations; i++) {
        while (System.currentTimeMillis() < endTime) {

            MCTSNode_lib node = root;
            Color currentPlayer = color;
            moveGenerator.initializeBoard(initialState);

            // traverse down the tree
            while (!node.children.isEmpty() && node.isFullyExpanded()) {
                node = selectPromisingNode(node, startingPlayer, currentPlayer);
                makeMove(moveGenerator, node.move, currentPlayer);
                currentPlayer = (currentPlayer == Color.RED) ? Color.BLUE : Color.RED;
            }

            // expansion
            expand(node, currentPlayer, moveGenerator);
            if (!node.children.isEmpty()) {
                node = node.children.get(random.nextInt(node.children.size()));
                makeMove(moveGenerator, node.move, currentPlayer);
                currentPlayer = (currentPlayer == Color.RED) ? Color.BLUE : Color.RED;
            }

            // simulation
            Color winner = simulate(moveGenerator, currentPlayer);

            // backpropagation
            backpropagate(node, winner, color);
        }
        return bestChild(root).move;
    }

    /**
     * Expands the given node by adding child nodes corresponding to possible moves.
     *
     * @param node The node from the MCTS tree to be expanded.
     * @param color The color of the player to move.
     * @param moveGenerator The MoveGenerator object used to play the move lines on.
     */
    private void expand(MCTSNode_lib node, Color color, MoveGenerator moveGenerator) {
        LinkedHashMap<Integer, List<Integer>> possibleMoves = getPossibleMoves(moveGenerator, color);
        if (!moveGenerator.isGameOverMCTS_lib(possibleMoves)) {
            LinkedList<Integer> movesList;
            movesList = Evaluation.convertMovesToList(possibleMoves);
            for (int move : movesList) {
                if (!node.children.stream().anyMatch(x -> x.move == move)) {
                    node.children.add(new MCTSNode_lib(move, node, (node.depth + 1)));
                }
            }
        }
    }

    /**
     * Simulates a game from the current position until a winner is determined.
     *
     * @param moveGenerator The MoveGenerator object used to play the move lines on.
     * @param color The color of the player to move.
     * @return The color of the winning player in this simulation.
     */
    private Color simulate(MoveGenerator moveGenerator, Color color) {
        Color winner = Color.EMPTY;
        while (winner == Color.EMPTY) {
            LinkedHashMap<Integer, List<Integer>> possibleMoves = getPossibleMoves(moveGenerator, color);
            LinkedList<Integer> movesList = Evaluation.convertMovesToList(possibleMoves);

            if (moveGenerator.isGameOverMCTS_lib(possibleMoves)) {
                if (moveGenerator.getWinner(possibleMoves, color)) {
                    winner = Color.BLUE;
                    break;
                } else {
                    winner = Color.RED;
                    break;
                }
            }

            int raInteger = random.nextInt(movesList.size());
            int move = movesList.get(raInteger);
            makeMove(moveGenerator, move, color);
            color = (color == Color.RED) ? Color.BLUE : Color.RED;
        }
        return winner;
    }

    /**
     * Backpropagates the result of a simulation back through the tree.
     *
     * @param node The current node to start backpropagation from.
     * @param winner The color of the winning player in the simulation.
     * @param color The color of the player to move.
     */
    private void backpropagate(MCTSNode_lib node, Color winner, Color color) {
        while (node != null) {
            node.visits++;
            if (winner == color) {
                node.wins++;
            }
            node = node.parent;
        }
    }

    /**
     * Selects the most promising child node according to the UCB1 formula.
     *
     * @param node The parent node from which to select a child.
     * @param startingPlayer The color of the player who made the first move.
     * @param currPlayer The color of the player who makes the next move.
     * @return The most promising child node based on UCB calculation.
     */
    private MCTSNode_lib selectPromisingNode(MCTSNode_lib node, Color startingPlayer, Color currPlayer) {
        if (startingPlayer == currPlayer) {
            return node.children.stream().max((n1, n2) -> {
                double uct1 = (n1.wins / n1.visits) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / (n1.visits));
                double uct2 = (n2.wins / n2.visits) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / (n2.visits));
                return Double.compare(uct1, uct2);
            }).orElseThrow(RuntimeException::new);
        } else {
            return node.children.stream().max((n1, n2) -> {
                double uct1 = (1 - (n1.wins / n1.visits)) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / (n1.visits));
                double uct2 = (1 - (n2.wins / n2.visits)) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / (n2.visits));
                return Double.compare(uct1, uct2);
            }).orElseThrow(RuntimeException::new);
        }
    }

    /**
     * Finds the best child node of the given parent node based on the highest win rate.
     *
     * @param node The parent node for which to find the best child node.
     * @return The best child node based on the highest win rate.
     */
    private MCTSNode_lib bestChild(MCTSNode_lib node) {
        return node.children.stream().max((n1, n2) -> Double.compare((n1.wins/n1.visits), (n2.wins/n2.visits))).orElseThrow(RuntimeException::new);
    }

    /**
     * Retrieves all possible moves for the current player in the current board position.
     *
     * @param moveGenerator The MoveGenerator object used to play the move lines on.
     * @param color The color of the player to move.
     * @return A map mapping integers to lists of integers representing possible moves.
     */
    private LinkedHashMap<Integer, List<Integer>> getPossibleMoves(MoveGenerator moveGenerator, Color color) {
        LinkedHashMap<Integer, List<Integer>> possibleMoves = moveGenerator.generateAllPossibleMoves(color);
        return possibleMoves;
    }

    /**
     * Makes a move on the board using the specified move integer and player color.
     *
     * @param moveGenerator The MoveGenerator object used to play the move lines on.
     * @param move The move integer to play.
     * @param player  The color of the player making the move.
     * @return The updated FEN string representation of the board after making the move.
     */
    private String makeMove(MoveGenerator moveGenerator, int move, Color player) {
        moveGenerator.movePiece(move);
        String fen = moveGenerator.getFenFromBoard();
        return fen;
    }
}