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

//    public void MCTS_UCB(String fen){
//        MoveGenerator gameState = new MoveGenerator();
//        char color_fen = fen.charAt(fen.length() - 1);
//        Color ourColor = gameState.getColor(color_fen);
//
//        this.ourColor=ourColor;
//
//        LinkedHashMap<Integer, List<Integer>> moves = gameState.getMovesWrapper(fen);
//        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);
//        MCTSNode parentNode = new MCTSNode(movesList,moves,ourColor);
//        this.parentNode = parentNode;
//        gameState.printBoard(true);
//
//        treePolicy(gameState,parentNode, ourColor);
//
//        for (MCTSNode m : parentNode.children){
//            System.out.println("Node For Move "+m.move+", Value: "+m.getNodeValue()+" Visits: "+m.numberOfVisits+" Wins: "+m.numberOfWins);
//        }
//        System.out.println("Number Of all: "+numberOfAllSimulations);
//        System.out.println("Number Of all: "+parentNode.numberOfVisits);
//
//        System.out.println(MoveGenerator.convertMoveToFEN(getBestMove(parentNode)));
//    }

    public String MCTS_Orchestrator(String fen){
        MoveGenerator gameState = new MoveGenerator();
        char color_fen = fen.charAt(fen.length() - 1);
        Color ourColor = gameState.getColor(color_fen);

        this.ourColor=ourColor;

        LinkedHashMap<Integer, List<Integer>> moves = gameState.getMovesWrapper(fen);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(moves);
        MCTSNode parentNode = new MCTSNode(ourColor);

        this.parentNode = parentNode;
        gameState.printBoard(true);

        MoveGenerator parentGameState = new MoveGenerator();
        parentGameState.initializeBoard(gameState.getFenFromBoard());

        Color color = (ourColor == Color.RED) ? Color.BLUE : Color.RED;

        MCTSNode node = expandAndReturnRandomNodeNew(parentNode,gameState,color,movesList);

        //MCTSNode node =expandAndReturnRandomNode(parentNode,gameState,color);
        int reward = simulateToEnd(color,gameState,ourColor);
        propagateDataToRoot(node,reward,node.color);    //TODO check if node.color and color is always equal

        newTreePolicy(parentGameState,parentNode, ourColor);

        for (MCTSNode m : parentNode.children){
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
        for (MCTSNode child:node.children){
            double value = child.numberOfWins/ child.numberOfVisits;
            if (value>max){
                max=value;
                maxChild = child;
            }
        }
        return maxChild.move;
    }


    public int simulateToEnd(Color color, MoveGenerator moveGenerator, Color parentColor){
        while (true){           //TODO CHANGE FOR NEW PROPAGATION
            //generate and Pick random mov
            color = (color == Color.RED) ? Color.BLUE : Color.RED;          //TODO: Check where to do color change
            LinkedHashMap<Integer,List<Integer>> moves = moveGenerator.generateAllPossibleMoves(color);

            int move = moveGenerator.getRandomMoveInt(moves);                                      //IDEA: maybe change randomness based on position in order
            int res = moveGenerator.isGameOverMCTS(moves,color);
            if (parentColor==Color.RED && res==-1||parentColor==Color.BLUE && res==1){
                return 1;
            } else if (parentColor==Color.BLUE && res==-1||parentColor==Color.RED && res==1){
                return 0;
            }
            if (moves.size()!=0) {
                moveGenerator.movePiece(move);
            }
        }
//        LinkedHashMap<Integer,List<Integer>> moves1 = moveGenerator.generateAllPossibleMoves(color);
//        color  = (color == Color.RED) ? Color.BLUE : Color.RED;
//        LinkedHashMap<Integer,List<Integer>> moves2 = moveGenerator.generateAllPossibleMoves(color);
//        double prob = moves1.size()/(double) (moves2.size()+moves1.size());
//        Random generator =  new Random();
//        return  generator.nextDouble() >= prob? 1 : 0;
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
            if(node.children.isEmpty()||node.isWin){
                return node;
            }
            if (node.numberOfVisits>20000){
                int i =1;
            }

            double maxUCB = Integer.MIN_VALUE;
            MCTSNode bestChild = null;
            for (MCTSNode child:node.children){
                if (child.move==7372&&numberOfAllSimulations>=200000){
                    int i = 0;
                }
                double nodeUCB= child.getNodeValueNew();
                if (child.isWin){
                     bestChild=child;
                     continue;
                }
                if (nodeUCB>=maxUCB){
                    maxUCB=nodeUCB;
                    bestChild=child;
                }
            }
            if(bestChild.move==6171){
                int i =0;
            }
            moveGenerator.movePiece(bestChild.move);
            if (bestChild.numberOfVisits<=1){
                //check if win
                bestChild.updateNode(moveGenerator);
                if (bestChild.isWin){
                    return bestChild;
                }
            }
            node=bestChild;
        }
    }

//    public MCTSNode expandAndReturnRandomNode(MCTSNode node, MoveGenerator moveGenerator, Color color){
//        for (int move:node.childrenUnserached){
//            node.children.add(new MCTSNode(node,move,moveGenerator, color));
//        }
//        node.childrenUnserached.clear();
//
//        MCTSNode selectedChild=node.children.get(random.nextInt(node.children.size()));
//        return selectedChild;
//    }

    public MCTSNode expandAndReturnRandomNodeNew(MCTSNode node, MoveGenerator moveGenerator, Color color, LinkedList<Integer> children){
        for (int move:children){
            node.children.add(new MCTSNode(node,move,moveGenerator, color));        //TODO Switch to new MCTSNode
        }
        if (node.children.size()<=0){
            int i =0;
        }
        MCTSNode selectedChild=node.children.get(random.nextInt(node.children.size()));
        moveGenerator.movePiece(selectedChild.move);
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
            if (selectedNode.move==6171){
                int i =1;
            }
            if (selectedNode.isWin){
              //TODO Change for new propagation
                //color = (selectedNode.color == Color.RED) ? Color.BLUE : Color.RED;
                propagateDataToRoot(selectedNode,1,selectedNode.color);
                node = this.parentNode;
                moveGenerator.initializeBoard(parentGameState.getFenFromBoard());
                continue;
            }
            color = (selectedNode.color == Color.RED) ? Color.BLUE : Color.RED;
            //generate possible moves:
            LinkedList<Integer> moves = Evaluation.convertMovesToList(moveGenerator.generateAllPossibleMoves(selectedNode.color));
            MCTSNode nodeToRollout = expandAndReturnRandomNodeNew(selectedNode,moveGenerator,color,moves);
            int reward = simulateToEnd(nodeToRollout.color,moveGenerator,parentColor);               //maybe random reward?

            propagateDataToRoot(nodeToRollout,reward,nodeToRollout.color);

            node = this.parentNode;
            moveGenerator.initializeBoard(parentGameState.getFenFromBoard());
        }
    }


    public void propagateDataToRoot(MCTSNode node, int reward, Color colorOfExpandedPlayer){
        while (node.parent != null){
            if (node.color==colorOfExpandedPlayer){
                node.addWinAndIncrementVisit(reward);
                node=node.parent;
            }
            else  {
                if (reward==0){
                    node.addWinAndIncrementVisit(1);
                }
                else {
                    node.addWinAndIncrementVisit(0);
                }
                node=node.parent;
            }
        }
        node.numberOfVisits++;
        if (node.color==colorOfExpandedPlayer){
            node.numberOfWins+=reward;
        }
        numberOfAllSimulations++;
    }

    public static void main(String[] args) {
        MCTSKI ki = new MCTSKI();
        String fen = "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r"; //1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 r    +      6/1bb1b0bbb0b01/r02b04/2b01b0b02/2r02r02/1r02rrr02/6rr1/2r01r01 r
        ki.MCTS_Orchestrator(fen);
    }
}
