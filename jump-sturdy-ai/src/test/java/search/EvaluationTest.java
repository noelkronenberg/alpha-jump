package search;

import game.Color;
import game.MoveGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluationTest {

    static Evaluation evaluator;
    static MoveGenerator moveGenerator;

    @BeforeAll
    public static void init() {
        evaluator = new Evaluation();
        moveGenerator = new MoveGenerator();
    }

    private void testOrderMovesFirst(String fen, int expectedFirst) {
        init();

        // get moves
        LinkedHashMap<Integer, List<Integer>> movesMap = moveGenerator.getMovesWrapper(fen);
        LinkedList<Integer> movesList =  Evaluation.convertMovesToList(movesMap);

        // order moves
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = moveGenerator.getColor(color_fen);
        Evaluation.orderMoves(movesList, color);

        assertEquals(movesList.getFirst(), expectedFirst);
    }

    @Test
    @DisplayName("Move Order 1")
    public void testMoveOrder1() {
        testOrderMovesFirst("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b", 5060);
    }

    @Test
    @DisplayName("Move Order 2")
    public void testMoveOrder2() {
        testOrderMovesFirst("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 2636);
    }

    // end game move at first
    @Test
    @DisplayName("Move Order 3")
    public void testMoveOrder3() {
        testOrderMovesFirst("6/8/4r03/8/8/8/3b04/6 b", 6373);
    }


    @Test
    @DisplayName("Move Rating 1")
    public void testRateMoves1() {
        testRateMoves("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 1.0,26,36);
    }

    @Test
    @DisplayName("Move Rating 2")
    public void testRateMoves2() {
        testRateMoves("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 1.0,1,11);
    }
/*
    // illegal move
    @Test
    @DisplayName("Move Rating 3")
    public void testRateMoves() {
        testRateMoves("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 0.0,1,12);
    }*/

    // end game move
    @Test
    @DisplayName("Move Rating 4")
    public void testRateMoves4() {
        testRateMoves("6/8/4r03/8/8/8/3b04/6 b", 99999.0,63,73);
    }

    @Test
    @DisplayName("Position Rating 1")
    public void testPositionRating1() {
        testPositionRating("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", Color.BLUE, 0.0);
    }

    @Test
    @DisplayName("Position Rating 2")
    public void testPositionRating2() {
        testPositionRating("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 r", Color.RED, 0.0);
    }

    // end position
    @Test
    @DisplayName("Position Rating 3")
    public void testPositionRating3() {
        testPositionRating("6/8/4r03/8/8/8/8/b05 b", Color.BLUE, 100000);
    }

    // double player
    @Test
    @DisplayName("Position Rating 4")
    public void testPositionRating4() {
        testPositionRating("6/8/4r03/8/8/8/2bb5/6 b", Color.BLUE, 8);
    }

    private void testRateMoves(String fen, double expectedRating, int startPosition, int endPosition) {
        init();
        moveGenerator.initializeBoard(fen);
        char colorFen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(colorFen);
        double rated = Evaluation.rateMove(moveGenerator, color, startPosition, endPosition);
        assertEquals(expectedRating, rated);
    }

    private void testPositionRating(String fen, Color color, double expectedScore) {
        init();
        moveGenerator.initializeBoard(fen);
        double score = Evaluation.ratePosition(moveGenerator, color);
        assertEquals(expectedScore, score);
    }

    private Color getColorFromFen(char colorFen) {
        return colorFen == 'b' ? Color.BLUE : Color.RED;
    }



}
