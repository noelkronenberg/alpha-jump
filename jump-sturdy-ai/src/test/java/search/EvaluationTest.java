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

    // Test to ensure the first move in the ordered list matches the expected move
    private void testOrderMovesFirst(String fen, int expectedFirst) {
        init();

        // Get moves from FEN
        LinkedHashMap<Integer, List<Integer>> movesMap = moveGenerator.getMovesWrapper(fen);
        LinkedList<Integer> movesList = Evaluation.convertMovesToList(movesMap);

        // Order moves based on the current player's color
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(color_fen);
        Evaluation.orderMoves(movesList, color);

        assertEquals(movesList.getFirst(), expectedFirst);
    }

    // Test to compare the ratings of two positions
    private void testPositionComparison(String fen1, Color color1, String fen2, Color color2) {
        init();
        moveGenerator.initializeBoard(fen1);
        double score1 = Evaluation.ratePosition(moveGenerator, color1);
        moveGenerator.initializeBoard(fen2);
        double score2 = Evaluation.ratePosition(moveGenerator, color2);
        assertTrue(score1 < score2, "Expected position 2 to be rated higher than position 1");
    }

    // Test to check if a move rating matches the expected rating
    private void testRateMoves(String fen, double expectedRating, int startPosition, int endPosition) {
        init();
        moveGenerator.initializeBoard(fen);
        char colorFen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(colorFen);
        double rated = Evaluation.rateMove(moveGenerator, color, startPosition, endPosition);
        assertEquals(expectedRating, rated);
    }

    // Test to check if the position rating matches the expected score
    private void testPositionRating(String fen, Color color, double expectedScore) {
        init();
        moveGenerator.initializeBoard(fen);
        double score = Evaluation.ratePosition(moveGenerator, color);
        assertEquals(expectedScore, score);
    }

    // Helper function to get color from FEN character
    private Color getColorFromFen(char colorFen) {
        return colorFen == 'b' ? Color.BLUE : Color.RED;
    }

    // Test to compare ratings of two moves
    private void testMoveRatingComparison(String fen, int startPosition1, int endPosition1, int startPosition2, int endPosition2) {
        init();
        moveGenerator.initializeBoard(fen);
        char colorFen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(colorFen);

        double rating1 = Evaluation.rateMove(moveGenerator, color, startPosition1, endPosition1);
        double rating2 = Evaluation.rateMove(moveGenerator, color, startPosition2, endPosition2);

        assertTrue(rating1 > rating2, "Expected move rating (" + rating1 + ") to be higher than (" + rating2 + ")");
    }

    // Test case to ensure correct move ordering for a specific FEN
    @Test
    @DisplayName("Move Order 1")
    public void testMoveOrder1() {
        testOrderMovesFirst("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b", 5060);
    }

    // Another test case for move ordering
    @Test
    @DisplayName("Move Order 2")
    public void testMoveOrder2() {
        testOrderMovesFirst("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 2636);
    }

    // Test case for end game move ordering
    @Test
    @DisplayName("Move Order 3")
    public void testMoveOrder3() {
        testOrderMovesFirst("6/8/4r03/8/8/8/3b04/6 b", 6373);
    }

    // Test move rating for a specific move
    @Test
    @DisplayName("Move Rating 1")
    public void testRateMoves1() {
        testRateMoves("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 1.0, 26, 36);
    }

    // Another test case for move rating
    @Test
    @DisplayName("Move Rating 2")
    public void testRateMoves2() {
        testRateMoves("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 1.0, 1, 11);
    }

    // Test case for rating an end game move
    @Test
    @DisplayName("Move Rating 4")
    public void testRateMoves4() {
        testRateMoves("6/8/4r03/8/8/8/3b04/6 b", 99999.0, 63, 73);
    }

    // Compare rating of moving forward as a single player or as a tower
    @Test
    @DisplayName("Move Rating Comparison 1")
    public void testMoveRatingComparison1() {
        String fen = "6/8/4r03/8/8/3b04/8/6 b";
        int startPosition2 = 53;
        int endPosition2 = 52; // normal move - one forward
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
        int endPosition2 = 22; // sideways move

        testMoveRatingComparison(fen, startPosition1, endPosition1, startPosition2, endPosition2);
    }

    // Test position rating for a specific FEN and color
    @Test
    @DisplayName("Position Rating 1")
    public void testPositionRating1() {
        testPositionRating("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", Color.BLUE, 0.0);
    }

    // Another test case for position rating
    @Test
    @DisplayName("Position Rating 2")
    public void testPositionRating2() {
        testPositionRating("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 r", Color.RED, 0.0);
    }

    // Test case for rating an end position
    @Test
    @DisplayName("Position Rating 3")
    public void testPositionRating3() {
        testPositionRating("6/8/4r03/8/8/8/8/b05 b", Color.BLUE, 100000);
    }

    // Test case for rating a double player position
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

