package search;

import game.Color;
import game.MoveGenerator;
import java.util.*;
import game.Piece;

public class Evaluation {

    // START: evaluation

    public int sumWeightedPositions(MoveGenerator mgSP, Color player) {
        // aktuell wird nach Anzahl der Steine bewertet
        // außerdem haben die Steine ein Rating von 1-8, je nachdem wie nah sie an der gegnerischen Endreihe stehen
        // Türme werden mit x2 bewertet, bisher egal ob MIXED oder einfarbig

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
        }

        else if (player == Color.RED) {
            weight = 8;
            for (int row=0; row < mgSP.getPieceBoard().length; row++) {
                for (int col = 0; col < mgSP.getPieceBoard()[row].length; col++) {
                    if (mgSP.getColorBoard()[row][col] == Color.RED) {
                        if (mgSP.getPieceBoard()[row][col] == Piece.SINGLE) {
                            counter += weight; 
                        } else if (mgSP.getPieceBoard()[row][col] == Piece.DOUBLE) {
                            counter += weight * 2;
                        } else if (mgSP.getPieceBoard()[row][col] == Piece.MIXED) {
                            counter += weight * 2;
                        }
                    }
                }

                weight -= 1;
            }
        }

        return counter;
    }

    public int ratePosition(MoveGenerator mgRP, Color player) {
        // bewertet vorhandene Position für den angegebenen Spieler
        // Summe der Steine des Spielers - Summe der Steine des Gegners

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
        // Bewertung des Endboards - Bewertung des Startboards

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

    // END: evaluation

    // START: move ordering

    public static LinkedList<Integer> convertMovesToList(LinkedHashMap<Integer, List<Integer>> moves) {
        LinkedList<Integer> movesList = new LinkedList<>();

        for (int startPosition : moves.keySet()) {
            List<Integer> possibleEndPositions = moves.get(startPosition);
            for (int endPosition : possibleEndPositions) {
                int fullMove = startPosition * 100 + endPosition;
                movesList.add(fullMove);
            }
        }

        return movesList;
    }

    public static LinkedHashMap<Integer, List<Integer>> convertMovesToMap(List<Integer> movesList) {
        LinkedHashMap<Integer, List<Integer>> movesMap = new LinkedHashMap<>();

        for (int fullMove : movesList) {
            int startPosition = fullMove / 100;
            int endPosition = fullMove % 100;

            if (!movesMap.containsKey(startPosition)) {
                movesMap.put(startPosition, new ArrayList<>());
            }

            movesMap.get(startPosition).add(endPosition);
        }

        return movesMap;
    }

    public static void orderMoves(LinkedList<Integer> moves, Color color) {
        if (color == Color.RED) {
            moves.sort(Comparator.comparingInt(move -> (move % 100) % 100));
        } else {
            moves.sort(Comparator.comparingInt(move -> ((int) move % 100) % 100).reversed());
        }
    }

    // END: move ordering

    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        LinkedHashMap<Integer, List<Integer>> moves = moveGenerator.getMovesWrapper("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b");
        System.out.println(moves);

        System.out.println();
        System.out.println("Moves as list: ");
        LinkedList<Integer> movesList = convertMovesToList(moves);
        System.out.println(movesList);

        System.out.println();
        System.out.println("Sorted moves: ");
        orderMoves(movesList, Color.BLUE);
        System.out.println(movesList);

        System.out.println();
        System.out.println("Moves as map again: ");
        System.out.println(convertMovesToMap(movesList));
    }
}
