package search.MCTS;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import game.Color;
import game.MoveGenerator;
import search.Evaluation;

public class MCTS {
    private static final Random random = new Random();
    private static final double EXPLORATION_PARAM = Math.sqrt(2);

    public static int runMCTS(MoveGenerator moveGenerator, Color color, int iterations) {
        MCTSNode root = new MCTSNode(0, null); // Root hat keinen Zug, weil es der Startzustand ist
        String initialState = moveGenerator.getFenFromBoard();

        for (int i = 0; i < iterations; i++) {
            MCTSNode node = root;
            String state = initialState;
            Color currentPlayer = color;

            // Traverse down the tree
            while (!node.children.isEmpty() && node.isFullyExpanded()) {
                node = selectPromisingNode(node);
                state = makeMove(moveGenerator, node.move, currentPlayer);
                currentPlayer = (currentPlayer == Color.RED) ? Color.BLUE : Color.RED;
            }

            // Expansion
            if (!node.isTerminal(state)) {
                expand(node, currentPlayer, moveGenerator);
                node = node.children.get(random.nextInt(node.children.size()));
                state = makeMove(moveGenerator, node.move, currentPlayer);
                currentPlayer = (currentPlayer == Color.RED) ? Color.BLUE : Color.RED;
            }

            // Simulation
            Color winner = simulate(moveGenerator, currentPlayer);

            // Backpropagation
            backpropagate(node, winner, color);
        }

        return bestChild(root).move;
    }

    private static void expand(MCTSNode node, Color color, MoveGenerator moveGenerator) {
        LinkedHashMap<Integer, List<Integer>> possibleMoves = getPossibleMoves(moveGenerator, color);
        LinkedList<Integer> movesList;
        movesList = Evaluation.convertMovesToList(possibleMoves);
        for (int move : movesList) {
            node.children.add(new MCTSNode(move, node));
        }
    }

    private static Color simulate(MoveGenerator moveGenerator, Color color) {
        String fen = moveGenerator.getFenFromBoard();
        while (MCTSNode.getWinner(fen) == null) {
            LinkedHashMap<Integer, List<Integer>> possibleMoves = getPossibleMoves(moveGenerator, color);
            LinkedList<Integer> movesList;
            movesList = Evaluation.convertMovesToList(possibleMoves);
            if (possibleMoves.isEmpty()) break;
            Integer move = movesList.get(random.nextInt(possibleMoves.size()));
            fen = makeMove(moveGenerator, move, color);
            color = (color == Color.RED) ? Color.BLUE : Color.RED;
        }
        return MCTSNode.getWinner(fen);
    }

    private static void backpropagate(MCTSNode node, Color winner, Color color) {
        while (node != null) {
            node.visits++;
            if (winner == color) {
                node.wins++;
            }
            node = node.parent;
        }
    }

    private static MCTSNode selectPromisingNode(MCTSNode node) {
        return node.children.stream().max((n1, n2) -> {
            double uct1 = (n1.wins / n1.visits) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / n1.visits);
            double uct2 = (n2.wins / n2.visits) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / n2.visits);
            return Double.compare(uct1, uct2);
        }).orElseThrow(RuntimeException::new);
    }

    private static MCTSNode bestChild(MCTSNode node) {
        return node.children.stream().max((n1, n2) -> Integer.compare(n1.visits, n2.visits)).orElseThrow(RuntimeException::new);
    }

    private static LinkedHashMap<Integer, List<Integer>> getPossibleMoves(MoveGenerator moveGenerator, Color color) {
        String fen = moveGenerator.getFenFromBoard();
        LinkedHashMap<Integer, List<Integer>> possibleMoves = moveGenerator.generateAllPossibleMoves(color, fen);
        return possibleMoves;
    }

    private static String makeMove(MoveGenerator moveGenerator, int move, Color player) {
        moveGenerator.movePiece(move);
        String fen = moveGenerator.getFenFromBoard();
        return fen;
    }
}
