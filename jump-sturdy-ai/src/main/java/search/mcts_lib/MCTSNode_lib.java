package search.mcts_lib;
import java.util.ArrayList;
import java.util.List;

/**
 * Class of nodes for the MCTS-algorithm for creating an opening-library-document.
 */
public class MCTSNode_lib {
    int move; // move leading to node
    MCTSNode_lib parent;
    List<MCTSNode_lib> children;
    int visits;
    double wins;
    int depth;

    /**
     * Constructs a new MCTSNode_lib object.
     *
     * @param move The move associated with this node.
     * @param parent The parent node of this node.
     * @param depth The depth of this node in the MCTS tree.
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
     * Checks if every child node of this node has been visited at least once.
     *
     * @return true if every child node has been visited; false otherwise.
     */
    public boolean isFullyExpanded() {
        for (MCTSNode_lib child : children) {
            if (child.visits == 0) return false;
        }
        return true;
    }
}
