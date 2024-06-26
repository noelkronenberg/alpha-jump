package search.MCTS;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.antlr.v4.misc.Graph.Node;

import game.Color;
import game.MoveGenerator;
import search.Evaluation;

public class MCTS {
    private static final Random random = new Random();
    private static final double EXPLORATION_PARAM = Math.sqrt(0.5);

    public static int runMCTS(MoveGenerator moveGenerator, Color color, int iterations) {
        String initialState = moveGenerator.getFenFromBoard();
        MCTSNode root = new MCTSNode(0, null, 0); // Root hat keinen Zug, weil es der Startzustand ist

        for (int i = 0; i < iterations; i++) {
            MCTSNode node = root;
            String state = initialState;
            Color currentPlayer = color;
            moveGenerator.initializeBoard(initialState);

            // Traverse down the tree
            while (!node.children.isEmpty() && node.isFullyExpanded()) {
                node = selectPromisingNode(node);
                state = makeMove(moveGenerator, node.move, currentPlayer);
                currentPlayer = (currentPlayer == Color.RED) ? Color.BLUE : Color.RED;
            }

            // Expansion
            
            expand(node, currentPlayer, moveGenerator);
            if (!node.children.isEmpty()) {
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
            if (!node.children.stream().anyMatch(x -> x.move == move)) {
                node.children.add(new MCTSNode(move, node, (node.depth+1)));
            }
        }
    }

    private static Color simulate(MoveGenerator moveGenerator, Color color) {
        String fen = moveGenerator.getFenFromBoard();
        Color winner = Color.EMPTY;
        while (winner == Color.EMPTY) {
            LinkedHashMap<Integer, List<Integer>> possibleMoves = getPossibleMoves(moveGenerator, color);
            LinkedList<Integer> movesList = Evaluation.convertMovesToList(possibleMoves);

            //checking if game is over
            if (movesList.isEmpty()) {
                winner = (color == Color.RED) ? Color.BLUE : Color.RED;
                break;
            }
            Integer raInteger = random.nextInt(movesList.size());
            Integer move = movesList.get(raInteger);
            if (moveGenerator.isGameOver(MoveGenerator.convertMoveToFEN(move), color)) {
                winner = (color == Color.RED) ? Color.BLUE : Color.RED;
                break;
            }
            fen = makeMove(moveGenerator, move, color);
            color = (color == Color.RED) ? Color.BLUE : Color.RED;
        }
        return winner;
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

    public static void main(String[] args) {
        MCTS mcts = new MCTS();
        MoveGenerator mg = new MoveGenerator();
        String board = "6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/1b0b01b01r01/2r0r03r0/5r0 b";
        mg.initializeBoard(board);
        mg.printBoard(false);
        //runMCTS(mg, Color.BLUE, 10000);
    }
}
