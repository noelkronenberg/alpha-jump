package search;

import game.Color;
import game.MoveGenerator;
import game.Piece;

public class Evaluation {

    public int sumWeightedPositions(MoveGenerator mgSP, Color player) {
        //Aktuell wird nach Anzahl der Steine bewertet
        //Außerdem haben die Steine ein Rating von 1-8, je nachdem wie nah sie an der gegnerischen Endreihe stehen
        //Türme werden mit x2 bewertet, bisher egal ob MIXED oder einfarbig
        int counter = 0;
        int weight = 0;
        if (player == Color.BLUE) {
            weight = 1;
            for (int row=0; row < mgSP.getPieceBoard().length; row++) {
                for (int col = 0; col < mgSP.getPieceBoard()[row].length; col++) {
                    if (mgSP.getColorBoard()[row][col] == Color.BLUE) {
                        if (mgSP.getPieceBoard()[row][col] == Piece.SINGLE) {
                            counter += weight; 
                        } else if (mgSP.getPieceBoard()[row][col] == Piece.DOUBLE) {
                            counter += weight*2;
                        } else if (mgSP.getPieceBoard()[row][col] == Piece.MIXED) {
                            counter += weight*2;
                        }
                    }
                }
                weight += 1;
            }
        } else if (player == Color.RED) {
            weight = 8;
            for (int row=0; row < mgSP.getPieceBoard().length; row++) {
                for (int col = 0; col < mgSP.getPieceBoard()[row].length; col++) {
                    if (mgSP.getColorBoard()[row][col] == Color.RED) {
                        if (mgSP.getPieceBoard()[row][col] == Piece.SINGLE) {
                            counter += weight; 
                        } else if (mgSP.getPieceBoard()[row][col] == Piece.DOUBLE) {
                            counter += weight*2;
                        } else if (mgSP.getPieceBoard()[row][col] == Piece.MIXED) {
                            counter += weight*2;
                        }
                    }
                }
                weight -= 1;
            }
        }
        return counter;
    }

    public int ratePosition(MoveGenerator mgRP, Color player) {
        //Bewertet vorhandene Position für den angegebenen Spieler
        //Summe der Steine des Spielers - Summe der Steine des Gegners
        int rating = 0;
        if (player == Color.BLUE) {
            rating = sumWeightedPositions(mgRP, Color.BLUE) - sumWeightedPositions(mgRP, Color.RED);
        } else if (player == Color.RED) {
            rating = sumWeightedPositions(mgRP, Color.RED) - sumWeightedPositions(mgRP, Color.BLUE);
        } else {
            System.out.println("Leider wurde kein gültiger Spieler angegeben.");
        }
        return rating;
    }

    public int rateMove(MoveGenerator mgRT, Color player, int startPos, int endPos) {
        //Bewertung des Endboards - Bewertung des Startboards
        int result = 0;
        MoveGenerator mgComp = new MoveGenerator();
        mgComp.initializeBoard();
        mgComp.setColorBoard(mgRT.getColorBoard());
        mgComp.setPieceBoard(mgRT.getPieceBoard());
        result -= ratePosition(mgComp, player);
        mgComp.movePiece(startPos, endPos);
        result += ratePosition(mgComp, player);
        return result;
    }
}
