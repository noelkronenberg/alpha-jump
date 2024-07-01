package search.MCTS_BIB;
import java.util.ArrayList;
import java.util.List;

import game.Color;
import game.MoveGenerator;

public class MCTSNode_BIB {
    int move; // Der Zug, der zu diesem Knoten führt (z.B. [row, col])
    MCTSNode_BIB parent;
    List<MCTSNode_BIB> children;
    int visits;
    double wins;
    int depth;

    public MCTSNode_BIB(int move, MCTSNode_BIB parent, int depth) {
        this.move = move;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.wins = 0.0;
        this.depth = depth;
    }

    public boolean isFullyExpanded() {
        for (MCTSNode_BIB child : children) {
            if (child.visits == 0) return false;
        }
        return true;
    }

    /*public static Color getWinner(MoveGenerator moveGenerator) {
        String state = moveGenerator.getFenFromBoard();
        String[] splittedState = state.split("/");
        String firstRow = splittedState[0];
        String lastRow = splittedState[7].split(" ")[0];
        System.out.println(lastRow);
        if (!state.contains("r") || lastRow.contains("b")) {
            return Color.BLUE;
        } else if (!state.contains("b") || firstRow.contains("r")) {
            return Color.RED;
        } else {
            return null;
        }
    }*/
}
