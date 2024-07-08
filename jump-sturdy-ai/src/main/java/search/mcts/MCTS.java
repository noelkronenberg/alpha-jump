package search.mcts;

import game.Color;
import game.MoveGenerator;
import search.AI;
import search.ab.Evaluation;
import search.SearchConfig;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * An implementation of a Monte Carlo Tree Search for the game Jump Sturdy.
 */
public class MCTS extends AI {
    SearchConfig config;

    // hyperparameter
    double timeLimit = 20000;

    // logic
    public double numberOfAllSimulations;
    Color ourColor = Color.BLUE;
    Random random = new Random();
    MCTSNode parentNode = null;
    // double endTime = 0;

    @Override
    public String orchestrator(String fen, SearchConfig config) {
        this.config = config;
        MoveGenerator gameState = new MoveGenerator();

        char color_fen = fen.charAt(fen.length() - 1);
        Color ourColor = gameState.getColor(color_fen);
        this.ourColor = ourColor;

        this.timeLimit = this.config.timeLimit;
        this.numberOfAllSimulations = 0;

        LinkedHashMap<Integer, List<Integer>> moves = gameState.getMovesWrapper(fen);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);
        MCTSNode parentNode = new MCTSNode(ourColor);
        this.parentNode = parentNode;

        MoveGenerator parentGameState = new MoveGenerator();
        parentGameState.initializeBoard(gameState.getFenFromBoard());

        Color color = (ourColor == Color.RED) ? Color.BLUE : Color.RED;

        if (movesList.isEmpty()) {
            return "";
        }

        MCTSNode node = expandAndReturnRandomNode(parentNode, gameState, color, movesList);

        // MCTSNode node = expandAndReturnRandomNode(parentNode, gameState, color);
        int reward = simulateToEnd(color, gameState, ourColor);
        propagateDataToRoot(node, reward, node.color);
        treePolicy(parentGameState, parentNode, ourColor);

        /*
        for (MCTSNode m : parentNode.children) {
                System.out.println("Node For Move " + m.move+", Value: " + m.getNodeValue() + " Visits: " + m.numberOfVisits + " Wins: " + m.numberOfWins);
        }
        System.out.println("Number Of all: " + numberOfAllSimulations);
        System.out.println("Number Of all: " + parentNode.numberOfVisits);
        */

        // System.out.println(MoveGenerator.convertMoveToFEN(getBestMove(parentNode)));

        String s = MoveGenerator.convertMoveToFEN(getBestMove(parentNode));
        // System.out.println(s);

        return s;
    }

    @Override
    public String showConfig() {
        return "timeLimit = " + this.config.timeLimit;
    }

    /**
     * Determines the average wins per visit for each child of a give node and chooses the child that maximizes this value.
     *
     * @param node The {@code MCTSNode} representing the current state in the search tree.
     * @return An integer representing the best move.
     */
    public int getBestMove(MCTSNode node) {
        double max = Integer.MIN_VALUE;
        MCTSNode maxChild = null;
        for (MCTSNode child : node.children) {
            double value = child.numberOfWins / child.numberOfVisits;
            if (value > max && child.numberOfVisits > 100) {
                max = value;
                maxChild = child;
            }
        }

        if (maxChild == null) {
            return node.move;
        } else {
            return maxChild.move;
        }
    }

    /**
     * Simulates the game to the end using a probabilistic approach weighted by the number of possible moves or each player.
     *
     * @param color The color of the player to move.
     * @param moveGenerator The {@code MoveGenerator} object used to generate and execute moves.
     * @param parentColor The color of the root node in the Monte Carlo Tree Search (MCTS).
     * @return An integer representing the simulation result: 1 if the parent color wins, otherwise 0
     */
    public int simulateToEnd(Color color, MoveGenerator moveGenerator, Color parentColor) {

        // Alternative: generate and pick random move
        /*
        while (true) {
            color = (color == Color.RED) ? Color.BLUE : Color.RED;
            LinkedHashMap<Integer,List<Integer>> moves = moveGenerator.generateAllPossibleMoves(color);

            int move = moveGenerator.getRandomMoveInt(moves);
            int res = moveGenerator.isGameOverMCTS(moves, color);

            if (parentColor == Color.RED && res == -1 || parentColor == Color.BLUE && res == 1) {
                return 1;
            } else if (parentColor == Color.BLUE && res == -1 || parentColor == Color.RED && res == 1) {
                return 0;
            }

            if (moves.size() != 0) {
                moveGenerator.movePiece(move);
            }
        }
        */

        LinkedHashMap<Integer,List<Integer>> moves1 = moveGenerator.generateAllPossibleMoves(color);
        color = (color == Color.RED) ? Color.BLUE : Color.RED;
        LinkedHashMap<Integer,List<Integer>> moves2 = moveGenerator.generateAllPossibleMoves(color);
        double prob = moves1.size() / (double) (moves2.size() + moves1.size());
        Random generator =  new Random();
        return  generator.nextDouble() >= prob? 1 : 0;
    }

    /**
     * Checks whether the search should continue based on the end time-
     *
     * @param endTime The maximum time allowed for evaluating moves, in milliseconds.
     * @return  {@code true} if the current system time is less or equal than the end time,  {@code false} if the current system time is more than the end time.
     */
    public boolean continueSearch(double endTime) {
        double time = System.currentTimeMillis();
        if (endTime >= time) {
            return true;
        }
        return false;
    }

    /**
     * Performs a tree traversal in the Monte Carlo Tree Search (MCTS) algorithm.
     *
     * @param endtime The time at which the search should stop, in milliseconds.
     * @param node The current {@code MCTSNode} from which to start the traversal.
     * @param moveGenerator The {@code MoveGenerator} object used to generate and execute moves.
     * @return The {@code MCTSNode} reached at the end of the traversal.
     */
    public MCTSNode treeTraversal(double endtime, MCTSNode node, MoveGenerator moveGenerator) {
        while (continueSearch(endtime)) {
            if (node.children.isEmpty() || node.isWinPos || node.isWinMove) {
                return node;
            }

            double maxUCB = Integer.MIN_VALUE;
            MCTSNode bestChild = null;

            for (MCTSNode child : node.children) {

                double nodeUCB = child.getNodeValue();

                if (child.isWinPos || child.isWinMove) {
                     bestChild = child;
                     return bestChild;
                }

                if (nodeUCB >= maxUCB) {
                    maxUCB = nodeUCB;
                    bestChild = child;
                }
            }

            moveGenerator.movePiece(bestChild.move);
            if (bestChild.numberOfVisits <= 1) {
                //check if win
                bestChild.updateNode(moveGenerator);
                if (bestChild.isWinPos || bestChild.isWinMove) {
                    return bestChild;
                }
            }

            node = bestChild;
        }
        return node;
    }

    /**
     * Expands the given MCTS node by creating new child nodes for each possible move
     * and returns one of the created child nodes at random.
     *
     * @param node The {@code MCTSNode} to be expanded.
     * @param moveGenerator The {@code MoveGenerator} object used to generate and execute moves.
     * @param color The color of the children.
     * @param children A list of possible moves (as integers) from the current node.
     * @return A randomly selected {@code MCTSNode} from the newly created children.
     */
    public MCTSNode expandAndReturnRandomNode(MCTSNode node, MoveGenerator moveGenerator, Color color, LinkedList<Integer> children){
        for (int move : children) {
            node.children.add(new MCTSNode(node, move, color));
        }

        MCTSNode selectedChild = node.children.get(random.nextInt(node.children.size()));
        moveGenerator.movePiece(selectedChild.move);
        return selectedChild;
    }

    /**
     * Executes the tree policy for the Monte Carlo Tree Search (MCTS) algorithm.
     * This traverses the search tree, expands nodes, and performs simulations
     * to update the tree with new information.
     *
     * @param moveGenerator The {@code MoveGenerator} object used to generate and execute moves.
     * @param node The current {@code MCTSNode} from which to start the tree policy.
     * @param color The color of the current node making the move.
     */
    public void treePolicy(MoveGenerator moveGenerator, MCTSNode node, Color color) {
        MoveGenerator parentGameState = new MoveGenerator();
        parentGameState.initializeBoard(moveGenerator.getFenFromBoard());
        Color parentColor = color;
        double endtime = System.currentTimeMillis() + timeLimit;

        while(continueSearch(endtime)) {

            MCTSNode selectedNode = treeTraversal(endtime, node, moveGenerator);

            if (selectedNode.isWinMove) {
                propagateDataToRoot(selectedNode,1, selectedNode.color);
                node = this.parentNode;
                moveGenerator.initializeBoard(parentGameState.getFenFromBoard());
                continue;
            }

            if (selectedNode.isWinPos) {
                propagateDataToRoot(selectedNode,1, selectedNode.color);
                node = this.parentNode;
                moveGenerator.initializeBoard(parentGameState.getFenFromBoard());
                continue;
            }

            color = (selectedNode.color == Color.RED) ? Color.BLUE : Color.RED;
            // generate possible moves:
            LinkedList<Integer> moves = Evaluation.convertMovesToList(moveGenerator.generateAllPossibleMoves(selectedNode.color));

            if (moves.size() > 0) {
                MCTSNode nodeToRollout = expandAndReturnRandomNode(selectedNode, moveGenerator, color, moves);
                int reward = simulateToEnd(nodeToRollout.color, moveGenerator, parentColor);
                propagateDataToRoot(nodeToRollout, reward, nodeToRollout.color);
            } else {
                selectedNode.updateNode(moveGenerator);
                propagateDataToRoot(selectedNode,1, selectedNode.color);
            }

            node = this.parentNode;
            moveGenerator.initializeBoard(parentGameState.getFenFromBoard());
        }
    }

    /**
     *  Propagates the results of a simulation back up to the root of the Monte Carlo Tree Search (MCTS) tree.
     *  This method updates the win and visit counts of the nodes.
     *
     * @param node The Leaf {@code MCTSNode} from which to start the propagation.
     * @param reward The reward obtained from the simulation (1 for a win, 0 for a loss).
     * @param colorOfExpandedPlayer The color of the leaf node (last expanded node).
     */
    public void propagateDataToRoot(MCTSNode node, int reward, Color colorOfExpandedPlayer) {
        while (node.parent != null) {
            if (node.color == colorOfExpandedPlayer) {
                node.addWinAndIncrementVisit(reward);
                node = node.parent;
            } else {
                if (reward == 0) {
                    node.addWinAndIncrementVisit(1);
                }
                else {
                    node.addWinAndIncrementVisit(0);
                }
                node = node.parent;
            }
        }

        node.numberOfVisits++;
        if (node.color == colorOfExpandedPlayer) {
            node.numberOfWins += reward;
        }
        numberOfAllSimulations++;
    }
}
