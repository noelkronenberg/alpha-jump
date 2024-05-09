package search;

import game.Color;

public class Evaluation {

    // NOTE: Wird noch an 2D-Array angepasst

    public int SumWeightedPositions(String givenString, char searchedChar) {
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
            rating = SumWeightedPositions(currBoard, 'b') - SumWeightedPositions(currBoard, 'r');
        } else if (player == Color.RED) {
            rating = SumWeightedPositions(currBoard, 'r') - SumWeightedPositions(currBoard, 'b');
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
}
