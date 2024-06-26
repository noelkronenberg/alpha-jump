package search;

import game.MoveGenerator;
import search.MCTS.MCTS;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import game.Color;

public class MCTSTest {

    static MoveGenerator moveGenerator;
    static MCTS mcts;
    static Color color = Color.BLUE;
    static int iterations = 100000;

    @BeforeAll
    public static void init() {
        moveGenerator = new MoveGenerator();
        mcts = new MCTS();
    }

    public void testPosition(String fen, String expectedAnswer) {
        moveGenerator.initializeBoard(fen);
        moveGenerator.printBoard(false);
        assertEquals(expectedAnswer, MoveGenerator.convertMoveToFEN(mcts.runMCTS(moveGenerator, color, iterations)));
    }

    @Test
    public void winningMoveTest() {
        testPosition("6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r0b012r0/5r0 b", "D7-D8");
    }
    @Test
    public void nearlyWinningMoveTest() {
        testPosition("6/3b0b03/3r02bb1/b0b03b02/rrrr1b02rr1/1b04r01/3r03r0/5r0 b", "B6-B7");
    }
}
