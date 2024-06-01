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
        LinkedList<Integer> movesList = evaluator.convertMovesToList(movesMap);

        // order moves
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(color_fen);
        evaluator.orderMoves(movesList, color);

        assertEquals(movesList.getFirst(), expectedFirst);
    }

    private void testPositionComparison(String fen1, Color color1, String fen2, Color color2) {
        init();
        moveGenerator.initializeBoard(fen1);
        double score1 = evaluator.ratePosition(moveGenerator, color1,1);
        moveGenerator.initializeBoard(fen2);
        double score2 = evaluator.ratePosition(moveGenerator, color2,1);
        assertTrue(score1 < score2, "Expected position 2 to be rated higher than position 1");
    }

    private void testRateMoves(String fen, double expectedRating, int startPosition, int endPosition) {
        init();
        moveGenerator.initializeBoard(fen);
        char colorFen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(colorFen);
        double rated = evaluator.rateMove(moveGenerator, color, startPosition, endPosition,1);
        assertEquals(expectedRating, rated);
    }

    private void testRateMoves(String fen, boolean win, int startPosition, int endPosition) {
        init();
        moveGenerator.initializeBoard(fen);
        char colorFen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(colorFen);
        double rated = evaluator.rateMove(moveGenerator, color, startPosition, endPosition,1);
        assertTrue(rated >= 50000);
    }


    private void testPositionRating(String fen, Color color, double expectedScore) {
        init();
        moveGenerator.initializeBoard(fen);
        double score = evaluator.ratePosition(moveGenerator, color,1);
        assertEquals(expectedScore, score);
    }

    private void testPositionRating(String fen, Color color, boolean win) {
        init();
        moveGenerator.initializeBoard(fen);
        double score = evaluator.ratePosition(moveGenerator, color,1);
        assertTrue(score >= 50000);
    }

    private Color getColorFromFen(char colorFen) {
        return colorFen == 'b' ? Color.BLUE : Color.RED;
    }

    private void testMoveRatingComparison(String fen, int startPosition1, int endPosition1, int startPosition2, int endPosition2) {
        init();
        moveGenerator.initializeBoard(fen);
        char colorFen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(colorFen);

        double rating1 = evaluator.rateMove(moveGenerator, color, startPosition1, endPosition1,1);
        double rating2 = evaluator.rateMove(moveGenerator, color, startPosition2, endPosition2,1);

        assertTrue(rating1 > rating2, "Expected move rating (" + rating1 + ") to be higher than (" + rating2 + ")");
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
        testRateMoves("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 1.0, 26, 36);
    }

    @Test
    @DisplayName("Move Rating 2")
    public void testRateMoves2() {
        testRateMoves("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 1.0, 1, 11);
    }

    // end game move
    @Test
    @DisplayName("Move Rating 4")
    public void testRateMoves4() {
        testRateMoves("6/8/4r03/8/8/8/3b04/6 b", true, 63, 73);
    }

    // compare Rating of moving forward as a single player or as a tower
    @Test
    @DisplayName("Move Rating Comparison 1")
    public void testMoveRatingComparison1() {
        String fen = "6/8/4r03/8/8/3b04/8/6 b";
        int startPosition2 = 53;
        int endPosition2 = 52; // normal move- one forward
        int startPosition1 = 53;
        int endPosition1 = 63; // tower move

        testMoveRatingComparison(fen, startPosition1, endPosition1, startPosition2, endPosition2);
    }

    // Compare the rating of different moves
    @Test
    @DisplayName("Move Rating Comparison 2")
    public void testMoveRatingComparison2() {
        String fen = "6/8/4r03/8/8/8/3b04/6 b";
        int startPosition1 = 63;
        int endPosition1 = 73; // end game move
        int startPosition2 = 12;
        int endPosition2 = 22; // sideway move

        testMoveRatingComparison(fen, startPosition1, endPosition1, startPosition2, endPosition2);
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
        testPositionRating("6/8/4r03/8/8/8/8/b05 b", Color.BLUE, true);
    }

    // double player
    @Test
    @DisplayName("Position Rating 4")
    public void testPositionRating4() {
        testPositionRating("6/8/4r03/8/8/8/2bb5/6 b", Color.BLUE, 8);
    }

    // Compare two positions where one should be rated higher - end position vs. possible end
    @Test
    @DisplayName("Position Comparison 2")
    public void testPositionComparison2() {
        String fen1 = "6/8/4r03/8/8/8/2bb5/6 b";
        String fen2 = "6/8/4r03/8/8/8/8/b05 b";
        testPositionComparison(fen1, Color.BLUE, fen2, Color.BLUE);
    }
}

