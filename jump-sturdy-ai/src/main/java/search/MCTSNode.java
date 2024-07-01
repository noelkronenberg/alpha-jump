package search;

import game.Color;
import game.MoveGenerator;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class MCTSNode {
    MCTSNode parent;
    //List<MCTSNode> childrenSearched = new LinkedList<>();
    List<MCTSNode> children = new LinkedList<>();
    //LinkedList<Integer> childrenUnserached;
    double numberOfVisits;
    double numberOfWins;
    int move;
    //boolean isFullySearched = false;
    Color color;
    //LinkedHashMap<Integer,List<Integer>> movesList;
    boolean isWinNext=false;
    boolean isWinPos=false;

//    public MCTSNode(MCTSNode parent, int move, MoveGenerator moveGenerator, Color color) {
//        MoveGenerator throwAwayMG = new MoveGenerator();
//        throwAwayMG.initializeBoard(moveGenerator.getFenFromBoard());
//        throwAwayMG.movePiece(move);
//        this.parent = parent;
//        LinkedHashMap<Integer,List<Integer>> movesList=throwAwayMG.generateAllPossibleMoves(color);
//        this.childrenUnserached = Evaluation.convertMovesToList(movesList);         //Eigentlich gerne das nicht mehr machen :( ---> Erstelle neue methode die einfach nur die moves ungeordnet generiert
//        this.numberOfVisits = 0;
//        this.numberOfWins = 0;
//        this.move = move;
//        this.color = color;
//        this.isWin = throwAwayMG.isGameOver(color);
//    }

    public MCTSNode(MCTSNode parent, int move, MoveGenerator moveGenerator, Color color) {
        this.parent = parent;
        this.children=new ArrayList<>();
        this.numberOfVisits = 0;
        this.numberOfWins = 0;
        this.move = move;
        this.color = color;
        this.isWinNext = isOnBaseLineForColor();
    }


    public MCTSNode(Color color) {
        this.parent = null;
        this.children = new ArrayList<>();;         //Eigentlich gerne das nicht mehr machen :( ---> Erstelle neue methode die einfach nur die moves ungeordnet generiert
        this.numberOfVisits = 0;
        this.numberOfWins = 0;
        this.move = 0;
        this.color = color;
    }



    public void addWinAndIncrementVisit(int winValue) {
            numberOfWins += winValue;             //It works, due to me assuming either win=1 or loss=0
            numberOfVisits++;
    }

    public double getNodeValue(){
            double value = (this.numberOfWins/this.numberOfVisits)+(Math.sqrt(2)*Math.sqrt((Math.log(parent.numberOfVisits))/(this.numberOfVisits)));
            return value;
    }

    public double getNodeValueNew(){
        if (this.numberOfVisits>0){
            double value = (this.numberOfWins/this.numberOfVisits)+(Math.sqrt(2)*Math.sqrt((Math.log(parent.numberOfVisits))/(this.numberOfVisits)));
            return value;
        }
        else {
            return 500000 ;
        }
    }

    public void updateNode(MoveGenerator moveGenerator){
        this.isWinPos = moveGenerator.isWinForMCTS(color);
    }

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
