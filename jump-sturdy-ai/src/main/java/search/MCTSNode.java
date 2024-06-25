package search;

import game.Color;
import game.MoveGenerator;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class MCTSNode {
    MCTSNode parent;
    List<MCTSNode> childrenSearched = new ArrayList<>();
    LinkedList<Integer> childrenUnserached;
    double numberOfVisits;
    double numberOfWins;
    double explorationParameter = Math.sqrt(2);
    int move;
    boolean isFullySearched = false;
    Color color;
    LinkedHashMap<Integer,List<Integer>> movesList;
    boolean isWin;

    public MCTSNode(MCTSNode parent, int move, MoveGenerator moveGenerator, String fen, Color color) {
        this.parent = parent;
        this.movesList=moveGenerator.generateAllPossibleMoves(color,fen);
        this.childrenUnserached = Evaluation.convertMovesToList(movesList);         //Eigentlich gerne das nicht mehr machen :( ---> Erstelle neue methode die einfach nur die moves ungeordnet generiert
        this.numberOfVisits = 0;
        this.numberOfWins = 0;
        this.move = move;
        this.color = color;

        MoveGenerator throwAwayMG = new MoveGenerator();
        throwAwayMG.initializeBoard(moveGenerator.getFenFromBoard());
        throwAwayMG.movePiece(move);

        this.isWin = throwAwayMG.isGameOver(color);
    }



    public MCTSNode(LinkedList<Integer> childrenUnserached,LinkedHashMap<Integer,List<Integer>> movesList, Color color) {
        this.parent = null;
        this.childrenUnserached = childrenUnserached;         //Eigentlich gerne das nicht mehr machen :( ---> Erstelle neue methode die einfach nur die moves ungeordnet generiert
        this.movesList=movesList;
        this.numberOfVisits = 0;
        this.numberOfWins = 0;
        this.move = 0;
        this.color = color;
    }

    public void addSearchedChild(MCTSNode child, int move) {
        childrenUnserached.remove(Integer.valueOf(move));
        childrenSearched.add(child);
        if (childrenUnserached.size()==0){
            this.isFullySearched = true;
        }
    }

    public void addSearchedChildNoRemove(MCTSNode child) {
        childrenSearched.add(child);
        if (childrenUnserached.size()==0){
            this.isFullySearched = true;
        }
    }


    public void addWinAndIncrementVisit(int winValue) {
            numberOfWins += winValue;             //It works, due to me assuming either win=1 or loss=0
            numberOfVisits++;
    }

    public double getNodeValue(){
            double value = (this.numberOfWins/this.numberOfVisits)+(this.explorationParameter*Math.sqrt((Math.log(parent.numberOfVisits))/(this.numberOfVisits)));
            return value;
    }

    public double getNodeValueNew(int ourOrEnemy){
        if (this.numberOfVisits>0){
            double value = (this.numberOfWins/this.numberOfVisits)+(this.explorationParameter*Math.sqrt((Math.log(parent.numberOfVisits))/(this.numberOfVisits)));
            return value;
        }
        else {
            return 500000 * ourOrEnemy;
        }
    }

    public LinkedList<Integer> getChildrenUnserached() {
        return childrenUnserached;
    }

    public MCTSNode getParent() {
        return parent;
    }
}
