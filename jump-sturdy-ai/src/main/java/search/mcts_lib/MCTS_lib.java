package search.mcts_lib;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import game.Color;
import game.MoveGenerator;
import search.ab.Evaluation;

public class MCTS_lib {
    private static final Random random = new Random();
    private static final double EXPLORATION_PARAM = 1.3;
    private static final double timeLimit = 10000;

    public static int runMCTS(MoveGenerator moveGenerator, Color color, int iterations) {
        Color startingPlayer = color;
        double endTime = System.currentTimeMillis() + timeLimit; 
        String initialState = moveGenerator.getFenFromBoard();
        MCTSNode_lib root = new MCTSNode_lib(0, null, 0); // Root hat keinen Zug, weil es der Startzustand ist

        //for (int i = 0; i < iterations; i++) {
        while (System.currentTimeMillis() < endTime) {

            MCTSNode_lib node = root;
            String state = initialState;
            Color currentPlayer = color;
            moveGenerator.initializeBoard(initialState);

            // Traverse down the tree
            while (!node.children.isEmpty() && node.isFullyExpanded()) {
                node = selectPromisingNode(node, startingPlayer, currentPlayer);
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

    private static void expand(MCTSNode_lib node, Color color, MoveGenerator moveGenerator) {
        LinkedHashMap<Integer, List<Integer>> possibleMoves = getPossibleMoves(moveGenerator, color);
        if (!moveGenerator.isGameOverMCTS_lib(possibleMoves)) {
            LinkedList<Integer> movesList;
            movesList = Evaluation.convertMovesToList(possibleMoves);
            for (int move : movesList) {
                if (!node.children.stream().anyMatch(x -> x.move == move)) {
                    node.children.add(new MCTSNode_lib(move, node, (node.depth+1)));
                }
            }
        }
    }

    private static Color simulate(MoveGenerator moveGenerator, Color color) {
        String fen = moveGenerator.getFenFromBoard();
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
            Integer raInteger = random.nextInt(movesList.size());
            Integer move = movesList.get(raInteger);
            fen = makeMove(moveGenerator, move, color);
            color = (color == Color.RED) ? Color.BLUE : Color.RED;
        }
        return winner;
    }

    private static void backpropagate(MCTSNode_lib node, Color winner, Color color) {
        while (node != null) {
            node.visits++;
            if (winner == color) {
                node.wins++;
            }
            node = node.parent;
        }
    }

    private static MCTSNode_lib selectPromisingNode(MCTSNode_lib node, Color startingPlayer, Color currPlayer) {
        if (startingPlayer == currPlayer) {
        return node.children.stream().max((n1, n2) -> {
            double uct1 = (n1.wins / n1.visits) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / (n1.visits));
            double uct2 = (n2.wins / n2.visits) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / (n2.visits));
            return Double.compare(uct1, uct2);
        }).orElseThrow(RuntimeException::new);
    } else {
        return node.children.stream().max((n1, n2) -> {
            double uct1 = (1-(n1.wins / n1.visits)) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / (n1.visits));
            double uct2 = (1-(n2.wins / n2.visits)) + EXPLORATION_PARAM * Math.sqrt(Math.log(node.visits) / (n2.visits));
            return Double.compare(uct1, uct2);
        }).orElseThrow(RuntimeException::new);
    }
    }


    private static MCTSNode_lib bestChild(MCTSNode_lib node) {
        return node.children.stream().max((n1, n2) -> Double.compare((n1.wins/n1.visits), (n2.wins/n2.visits))).orElseThrow(RuntimeException::new);
    }

    private static LinkedHashMap<Integer, List<Integer>> getPossibleMoves(MoveGenerator moveGenerator, Color color) {
        String fen = moveGenerator.getFenFromBoard();
        LinkedHashMap<Integer, List<Integer>> possibleMoves = moveGenerator.generateAllPossibleMoves(color);
        return possibleMoves;
    }

    private static String makeMove(MoveGenerator moveGenerator, int move, Color player) {
        moveGenerator.movePiece(move);
        String fen = moveGenerator.getFenFromBoard();
        return fen;
    }

    public static void main(String[] args) {
        MCTS_lib mcts = new MCTS_lib();
        MoveGenerator mg = new MoveGenerator();
        String board = "3b02/1bb6/1r0b02r02/2r05/4r03/8/2r03r01/6 r";
        mg.initializeBoard(board);
        //System.out.println(runMCTS(mg, Color.RED, 100000));
        mg.initializeBoard(board);
        mg.printBoard(false);
    }
}
