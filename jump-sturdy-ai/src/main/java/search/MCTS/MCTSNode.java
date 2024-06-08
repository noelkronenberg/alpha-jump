package search.MCTS;
import java.util.ArrayList;
import java.util.List;

import game.Color;

public class MCTSNode {
    int move; // Der Zug, der zu diesem Knoten führt (z.B. [row, col])
    MCTSNode parent;
    List<MCTSNode> children;
    int visits;
    double wins;

    public MCTSNode(int move, MCTSNode parent) {
        this.move = move;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.wins = 0.0;
    }

    public boolean isFullyExpanded() {
        for (MCTSNode child : children) {
            if (child.visits == 0) return false;
        }
        return true;
    }

    public boolean isTerminal(String state) {
        return getWinner(state) != null;
    }

    public static Color getWinner(String state) {
        // Placeholder: Implementiere die Logik zur Überprüfung des Spielgewinners
        return null;  // Beispielhaft kein Gewinner
    }
}
