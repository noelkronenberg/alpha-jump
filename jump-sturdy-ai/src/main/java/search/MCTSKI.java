package search;

import game.Color;
import game.MoveGenerator;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


//NOTES:
//https://stackoverflow.com/questions/42302142/monte-carlo-tree-search-tree-policy-for-two-player-games
// viele Besuche und wins wird minimiert... ?! Sehr komisch


public class MCTSKI {
    static double numberOfAllSimulations;
    Color ourColor = Color.BLUE;
    Random random = new Random();
    MCTSNode parentNode = null;
    //double endTime = 0;

    double timeLimit = 20000;
    //TODO: 1 add a Tree system(Done)     2 add the value function: UCB(done)       3 add a way to simulate randomly the game end(done)      4 traversal Function (done)   5 backpropagation (done)

    public void MCTS_UCB(String fen){
        MoveGenerator gameState = new MoveGenerator();
        char color_fen = fen.charAt(fen.length() - 1);
        Color ourColor = gameState.getColor(color_fen);

        this.ourColor=ourColor;

        LinkedHashMap<Integer, List<Integer>> moves = gameState.getMovesWrapper(fen);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);
        MCTSNode parentNode = new MCTSNode(movesList,moves,ourColor);
        this.parentNode = parentNode;
        gameState.printBoard(true);

        treePolicy(gameState,parentNode, ourColor);

        for (MCTSNode m : parentNode.childrenSearched){
            System.out.println("Node For Move "+m.move+", Value: "+m.getNodeValue()+" Visits: "+m.numberOfVisits+" Wins: "+m.numberOfWins);
        }
        System.out.println("Number Of all: "+numberOfAllSimulations);
        System.out.println("Number Of all: "+parentNode.numberOfVisits);

        System.out.println(MoveGenerator.convertMoveToFEN(getBestMove(parentNode)));
    }

    public String MCTS_Orchestrator(String fen){
        MoveGenerator gameState = new MoveGenerator();
        char color_fen = fen.charAt(fen.length() - 1);
        Color ourColor = gameState.getColor(color_fen);

        this.ourColor=ourColor;

        LinkedHashMap<Integer, List<Integer>> moves = gameState.getMovesWrapper(fen);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);
        MCTSNode parentNode = new MCTSNode(movesList,moves,ourColor);
        this.parentNode = parentNode;
        gameState.printBoard(true);

        Color color = (ourColor == Color.RED) ? Color.BLUE : Color.RED;

        MoveGenerator parentGameState = new MoveGenerator();
        parentGameState.initializeBoard(gameState.getFenFromBoard());

        MCTSNode node =expandAndReturnRandomNode(parentNode,gameState,color);
        int reward = simulateToEnd(color,gameState,ourColor);
        propagateDataToRoot(node,reward);
//        for (int move:movesList){
//            MCTSNode childNode = new MCTSNode(parentNode,move,gameState,gameState.getFenFromBoard(), color);
//            parentNode.addSearchedChildNoRemove(childNode);
//
//            int reward = simulateToEnd(color,gameState,ourColor);
//
//            propagateDataToRoot(childNode,reward);
//        }
        parentNode.childrenUnserached.clear();

        newTreePolicy(parentGameState,parentNode, ourColor);

        for (MCTSNode m : parentNode.childrenSearched){
            System.out.println("Node For Move "+m.move+", Value: "+m.getNodeValue()+" Visits: "+m.numberOfVisits+" Wins: "+m.numberOfWins);
        }
        System.out.println("Number Of all: "+numberOfAllSimulations);
        System.out.println("Number Of all: "+parentNode.numberOfVisits);

        //System.out.println(MoveGenerator.convertMoveToFEN(getBestMove(parentNode)));
        String s =MoveGenerator.convertMoveToFEN(getBestMove(parentNode));
        System.out.println(s);
        return s;
    }


    public int getBestMove(MCTSNode node){
        double max = Integer.MIN_VALUE;
        MCTSNode maxChild = null;
        for (MCTSNode child:node.childrenSearched){
            double value = child.numberOfWins/ child.numberOfVisits;
            if (value>max){
                max=value;
                maxChild = child;
            }
        }
        return maxChild.move;
    }

    public void setOurColor(Color ourColor) {
        this.ourColor=ourColor;
    }

    public int simulateToEnd(Color color, MoveGenerator moveGenerator, Color parentColor){
        while (true){
            //generate and Pick random mov
            color = (color == Color.RED) ? Color.BLUE : Color.RED;          //TODO: Check where to do color change
            LinkedHashMap<Integer,List<Integer>> moves = moveGenerator.generateAllPossibleMoves(color,moveGenerator.getFenFromBoard());

            int move = moveGenerator.getRandomMoveInt(moves);                                      //IDEA: maybe change randomness based on position in order
            int res = moveGenerator.isGameOverMCTS(moves,color);
            if (parentColor==Color.RED && res==-1||parentColor==Color.BLUE && res==1){
                return 1;
            } else if (parentColor==Color.BLUE && res==-1||parentColor==Color.RED && res==1){
                return -1;
            }
            if (moves.size()!=0) {
                moveGenerator.movePiece(move);
            }
        }
    }

    public MCTSNode getBestChild(MCTSNode node){
        double maxUCB = Integer.MIN_VALUE;
        MCTSNode maxChild = null;
        for (MCTSNode child : node.childrenSearched){
            double value = child.getNodeValue();       //TODO: Maybe edit this
            if (value>maxUCB){
                maxUCB=value;
                maxChild = child;
            }
        }
        return maxChild;
    }

    public boolean continueSearch(double endTime){
        double time = System.currentTimeMillis();
        if (endTime>=time){
            return true;
        }
        return false;
    }

    public MCTSNode treeTraversal(double endtime, MCTSNode node, MoveGenerator moveGenerator){
        while(/*continueSearch(endtime))*/true){        //TODO: check here for tree traversal: Color is somtimes min, sometimes max (weird)
            if(node.childrenSearched.isEmpty()||node.isWin){
                return node;
            }
            if (node.color==ourColor){      //TODO: hier
                if (node.parent.numberOfVisits>=20000){
                    int i=1;
                    //System.out.println("WTF");
                }
                double maxUCB = Integer.MIN_VALUE;
                MCTSNode bestChild = null;
                for (MCTSNode child:node.childrenSearched){
                    double nodeUCB= child.getNodeValueNew(1);
                    if (nodeUCB>=maxUCB){
                        maxUCB=nodeUCB;
                        bestChild=child;
                    }
                }
                moveGenerator.movePiece(bestChild.move);
                node=bestChild;
            }
            else {
                if (node.move==4526&&node.parent.numberOfVisits>=20000){
                    int i=1;
                }

                MCTSNode bestChild = null;
                double minUCB = Integer.MAX_VALUE;
                for (MCTSNode child:node.childrenSearched){
                    double nodeUCB= child.getNodeValueNew(-1);
                    if (nodeUCB<minUCB){
                        minUCB=nodeUCB;
                        bestChild=child;
                    }
                }
                moveGenerator.movePiece(bestChild.move);
                node=bestChild;
            }
        }
        //return null;
    }

    public MCTSNode expandAndReturnRandomNode(MCTSNode node, MoveGenerator moveGenerator, Color color){
        for (int move:node.childrenUnserached){
            node.childrenSearched.add(new MCTSNode(node,move,moveGenerator,moveGenerator.getFenFromBoard(), color));
        }
        node.childrenUnserached.clear();

        MCTSNode selectedChild=node.childrenSearched.get(random.nextInt(node.childrenSearched.size()));
        return selectedChild;
    }

    public void newTreePolicy(MoveGenerator moveGenerator, MCTSNode node, Color color){
        //TODO: IMPL Time cutoff und solange es nicht ein win ist
        //TODO: mach UCB, falls noch nicht alle children explored wurden, mach das (done)
        //TODO: Change node to have searched child nodes and unsearched child nodes (maybe play around with a bool to see if unsearched is empty) (done)
        //TODO: update color after each move (done)
        MoveGenerator parentGameState = new MoveGenerator();
        parentGameState.initializeBoard(moveGenerator.getFenFromBoard());
        Color parentColor = color;
        double endtime = System.currentTimeMillis() + timeLimit;

        while(continueSearch(endtime)){

            MCTSNode selectedNode=treeTraversal(endtime,node,moveGenerator);
            if (selectedNode.isWin){
                if(selectedNode.move==4526){
                    int i = 0;
                    System.out.println("WTF");
                }
                if (selectedNode.color==ourColor){
                    propagateDataToRoot(selectedNode,-1);
                    node = this.parentNode;
                    moveGenerator.initializeBoard(parentGameState.getFenFromBoard());
                    continue;
                }
                else {
                    propagateDataToRoot(selectedNode,1);

                    node = this.parentNode;
                    moveGenerator.initializeBoard(parentGameState.getFenFromBoard());

                    continue;
                }
            }
            color = (selectedNode.color == Color.RED) ? Color.BLUE : Color.RED;
            MCTSNode nodeToRollout = expandAndReturnRandomNode(selectedNode,moveGenerator,color);
            int reward = simulateToEnd(selectedNode.color,moveGenerator,parentColor);
            if (nodeToRollout==null){
                //System.out.println("WTF");
            }
            propagateDataToRoot(nodeToRollout,reward);

            node = this.parentNode;
            moveGenerator.initializeBoard(parentGameState.getFenFromBoard());
        }
    }

    public void treePolicy(MoveGenerator moveGenerator, MCTSNode node, Color color){
        //TODO: IMPL Time cutoff und solange es nicht ein win ist
        //TODO: mach UCB, falls noch nicht alle children explored wurden, mach das (done)
        //TODO: Change node to have searched child nodes and unsearched child nodes (maybe play around with a bool to see if unsearched is empty) (done)
        //TODO: update color after each move (done)
        MoveGenerator parentGameState = new MoveGenerator();
        parentGameState.initializeBoard(moveGenerator.getFenFromBoard());
        Color parentColor = color;
        double endtime = System.currentTimeMillis() + timeLimit;

        while(continueSearch(endtime)){
            if (!node.isFullySearched){
                color = (color == Color.RED) ? Color.BLUE : Color.RED;

                LinkedList<Integer> moves =node.getChildrenUnserached();
                int move =moves.get(random.nextInt(moves.size()));

                MCTSNode childNode = new MCTSNode(node,move,moveGenerator, moveGenerator.getFenFromBoard(), color);
                node.addSearchedChild(childNode,move);

                int reward = simulateToEnd(color,moveGenerator,parentColor);

                propagateDataToRoot(childNode,reward);

                node = this.parentNode;

                moveGenerator.initializeBoard(parentGameState.getFenFromBoard());
                color = parentColor;
            }
            else {
                color = (color == Color.RED) ? Color.BLUE : Color.RED;
                node = getBestChild(node);
            }
        }
    }

    public void propagateDataToRoot(MCTSNode node, int reward){
        while (node.parent != null){
            node.addWinAndIncrementVisit(reward);
            node=node.parent;
        }
        node.numberOfVisits++;
        node.numberOfWins+=reward;
        numberOfAllSimulations++;
    }

    public static void main(String[] args) {
        MCTSKI ki = new MCTSKI();
        String fen = "1bb4/8/8/8/5rr2/8/b07/2r03 r"; //1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 r    +      6/1bb1b0bbb0b01/r02b04/2b01b0b02/2r02r02/1r02rrr02/6rr1/2r01r01 r
        ki.MCTS_Orchestrator(fen);
    }
}
