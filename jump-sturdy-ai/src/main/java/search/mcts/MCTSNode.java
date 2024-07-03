package search.mcts;

import game.Color;
import game.MoveGenerator;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Represents a node in the Monte Carlo Tree Search (MCTS) tree.
 * Each node holds information about the move that lead to the current situation, its parent, children,
 * the color of the player that moves next, the number of visits and wins, and whether there is a win
 * in this Move or in this position.
 *
 */
public class MCTSNode {
    MCTSNode parent;
    List<MCTSNode> children = new LinkedList<>();

    double numberOfVisits;
    double numberOfWins;

    int move;
    Color color;

    boolean isWinMove=false;
    boolean isWinPos=false;

    /**
     * Constructs any MCTSNode with a specified parent, move, move generator, and color.
     *
     * @param parent The parent node of this node.
     * @param move The move that led to this node.
     * @param color The color of the player making the next move.
     */
    public MCTSNode(MCTSNode parent, int move, Color color) {
        this.parent = parent;
        this.children=new ArrayList<>();
        this.numberOfVisits = 0;
        this.numberOfWins = 0;
        this.move = move;
        this.color = color;
        this.isWinMove = isOnBaseLineForColor();
    }

    /**
     * Constructs an MCTSNode Root node only needing the Color as a parameter
     *
     * @param color The color of the player making the next move.
     */
    public MCTSNode(Color color) {
        this.parent = null;
        this.children = new ArrayList<>();;         //Eigentlich gerne das nicht mehr machen :( ---> Erstelle neue methode die einfach nur die moves ungeordnet generiert
        this.numberOfVisits = 0;
        this.numberOfWins = 0;
        this.move = 0;
        this.color = color;
    }

    /**
     * Increments the number of visits and adds a win to the node.
     *
     * @param winValue The value of the win to add (1 for win, 0 for loss).
     */
    public void addWinAndIncrementVisit(int winValue) {
            numberOfWins += winValue;             //It works, due to me assuming either win=1 or loss=0
            numberOfVisits++;
    }

    /**
     * Calculates the node's value using the UCB1 formula.
     * Returns a high value if the node has not been visited.
     *
     * @return The value of the node.
     */
    public double getNodeValue(){
        if (this.numberOfVisits>0){
            double value = (this.numberOfWins/this.numberOfVisits)+(Math.sqrt(2)*Math.sqrt((Math.log(parent.numberOfVisits))/(this.numberOfVisits)));
            return value;
        }
        else {
            return 500000 ;
        }
    }

    /**
     * Updates the node to determine if it has won in its position.
     *
     * @param moveGenerator The move generator used to check for win conditions.
     */
    public void updateNode(MoveGenerator moveGenerator){
        this.isWinPos = moveGenerator.isWinForMCTS(color);
    }

    /**
     * Checks if the node's move is on the baseline of the enemy.
     *
     * @return {@code true} if the move is on the baseline for the color, {@code false} otherwise.
     */
    public boolean isOnBaseLineForColor(){
        if ((move%100)/10==0&&color==Color.BLUE){
            return true;
        }
        if ((move%100)/10==7&&color==Color.RED){
            return true;
        }
        return false;
    }
}
