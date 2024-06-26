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
    public void testGruppeH() {
        //testPosition("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b", "A6-A7");
        testPosition("6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 b", "D5-C7");
    }
    @Test
    public void winningMoveTest() {
        testPosition("6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r0b012r0/5r0 b", "D7-D8");
    }
}
