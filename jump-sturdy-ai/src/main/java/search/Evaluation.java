package search;

import game.Color;
import game.MoveGenerator;

import java.util.*;

public class Evaluation {

    // NOTE: Wird noch an 2D-Array angepasst

    public int sumWeightedPositions(String givenString, char searchedChar) {
        int counter = 0;
        int weight = 0;
        if (searchedChar == 'b') {
            weight = 1;
            for (int i=0; i < givenString.length(); i++) {
                if (givenString.charAt(i) == searchedChar) {
                    counter += weight;
                } else if(givenString.charAt(i) == '/') {
                    weight += 1;
                }
            }
        } else {
            weight = 8;
            for (int i=0; i < givenString.length(); i++) {
                if (givenString.charAt(i) == searchedChar) {
                    counter += weight;
                } else if(givenString.charAt(i) == '/') {
                weight -= 1;
                }
            }
        }
        return counter;
    }

    public int ratePosition(String currBoard, Color player) {
        //Bewertet vorhandene Position für den angegebenen Spieler
        //Aktuell wird nach Anzahl der Steine bewertet
        //Außerdem haben die Steine ein Rating von 1-8, je nachdem wie nah sie an der gegnerischen Endreihe stehen
        int rating = 0;
        if (player == Color.BLUE) {
            rating = sumWeightedPositions(currBoard, 'b') - sumWeightedPositions(currBoard, 'r');
        } else if (player == Color.RED) {
            rating = sumWeightedPositions(currBoard, 'r') - sumWeightedPositions(currBoard, 'b');
        } else {
            System.out.println("Leider wurde kein gültiger Spieler angegeben.");
        }
        return rating;
    }

    public int rateMove(String startBoard, String endBoard, Color player) {
        //Bewertung des Endboards - Bewertung des Startboards
        int result = 0;
        result = ratePosition(endBoard, player) - ratePosition(startBoard, player);
        return result;
    }

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
            moves.sort(Comparator.comparingInt(move -> (move % 100) % 10));
        } else {
            moves.sort(Comparator.comparingInt(move -> ((int) move % 100) % 10).reversed());
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
        orderMoves(movesList, Color.RED);
        System.out.println(movesList);

        System.out.println();
        System.out.println("Moves as map again: ");
        System.out.println(convertMovesToMap(movesList));
    }
}
