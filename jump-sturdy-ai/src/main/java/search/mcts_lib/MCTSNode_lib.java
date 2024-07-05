package search.mcts_lib;
import java.util.ArrayList;
import java.util.List;

/**
 * class of nodes for the MCTS-algorithm for creating an opening-library-document
 */
public class MCTSNode_lib {
    int move; // move leading to node
    MCTSNode_lib parent;
    List<MCTSNode_lib> children;
    int visits;
    double wins;
    int depth;

    /**
     * 
     * @param move move which the node represants
     * @param parent parent node of this node
     * @param depth int which indicates the depth of this node in the mcts-tree
     */
    public MCTSNode_lib(int move, MCTSNode_lib parent, int depth) {
        this.move = move;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.wins = 0.0;
        this.depth = depth;
    }

    /**
     * 
     * @return boolean which indicates if every child node of this one has been visited
     */
    public boolean isFullyExpanded() {
        for (MCTSNode_lib child : children) {
            if (child.visits == 0) return false;
        }
        return true;
    }
}
