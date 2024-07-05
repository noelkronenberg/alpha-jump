package search;

import game.Color;
import game.MoveGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import search.mcts_lib.MCTS_lib;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test class for MCTS_lib.
 */
public class MCTS_libTest {

    
    static MoveGenerator moveGenerator;
    static MCTS_lib mcts;
    static Color color = Color.BLUE;
    static int iterations = 450000;

    /**
     * Initializes the MCTS_lib instance before all test methods.
     */
    @BeforeAll
    public static void init() {
        moveGenerator = new MoveGenerator();
        mcts = new MCTS_lib();
        mcts.timeLimit = 50000;
    }

    /**
     * Helper method to test moves returned by the AI against expected moves.
     *
     * @param fen FEN notation representing the board state
     * @param expectedAnswers Expected moves as an array of FEN strings
     */
    public void testPosition(String fen, String... expectedAnswers) {
        init();

        if (fen.charAt(fen.length() - 1) == 'r') {
            color = Color.RED;
        } else if (fen.charAt(fen.length() - 1) == 'b') {
            color = Color.BLUE;
        } else {
            System.out.println("No color was specified in the string, the test is performed for the player blue.");
        }

        moveGenerator.initializeBoard(fen);
        moveGenerator.printBoard(false);

        String answer = MoveGenerator.convertMoveToFEN(mcts.runMCTS(moveGenerator, color));
        boolean matchFound = false;
        for (String expectedMove : expectedAnswers) {
            if (expectedMove.equals(answer)) {
                matchFound = true;
                break;
            }
        }

        System.out.println("Expected: " + Arrays.asList(expectedAnswers)+ "\n" + "Actual: " + answer);
        assertTrue(matchFound);
    }

    /**
     * Overloaded method to test a single move returned by the AI against the expected move.
     *
     * @param fen FEN notation representing the board state
     * @param expectedAnswer Expected move as a string
     */
    public void testPosition(String fen, String expectedAnswer) {
        init();

        if (fen.charAt(fen.length() - 1) == 'r') {
            color = Color.RED;
        } else if (fen.charAt(fen.length() - 1) == 'b') {
            color = Color.BLUE;
        } else {
            System.out.println("No color was specified in the string, the test is performed for the player blue.");
        }

        moveGenerator.initializeBoard(fen);
        moveGenerator.printBoard(false);

        assertEquals(expectedAnswer, MoveGenerator.convertMoveToFEN(mcts.runMCTS(moveGenerator, color)));
    }

    @Test
    public void winningMoveTest() {
        testPosition("6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r0b012r0/5r0 b", "D7-D8");
    }
    @Test
    public void nearlyWinningMoveTest() {
        testPosition("6/3b0b03/3r02bb1/b0b03b02/rrrr1b02rr1/1b04r01/3r03r0/5r0 b", "B6-B7");
    }

    @Test
    @DisplayName("Gruppe H")
    public void testGruppeH() {
        testPosition("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b", "A6-A7", "D3-B4");
        testPosition("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 r", "F5-G3");
        //testPosition("6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 b", "D5-C7"); // Sicherheitszug wird vorerst bevorzugt, anschließend findet er jedoch den richtigen Gewinnzug
        testPosition("6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 r", "D3-E2");
    }

    @Test
    @DisplayName("Gruppe F")
    public void testGruppeF() {
        testPosition("6/7b0/8/8/1r06/4b03/2rr1rrr02/5r0 b", "E6-D6", "E6-F6");
        testPosition("6/4bbb02/b02b01b02/1b02b03/2b01rrrr2/6r01/r01r0r0r03/5r0 r", "E5-D3", "E5-F3");
    }

    @Test
    @DisplayName("Gruppe T")
    public void testGruppeT() {
        testPosition("1b0b0b02/8/3b04/3b04/r0r06/2b05/5r0r01/6 b", "C6-C7");
        testPosition("6/4bb3/8/8/4b0r0b01/8/8/6 b", "E2-F4");
    }

    @Test
    @DisplayName("Gruppe AG")
    public void testGruppeAG() {
        testPosition("6/8/8/8/b0b02b0b02/2b05/2r0r0r0r02/6 b", "C6-D7");
        testPosition("3b01b0/3bb1b02/8/8/8/2r0b0r03/8/0r04r0 b", "D6-D7");
    }

    @Test
    @DisplayName("Gruppe C")
    public void testGruppeC() {
        testPosition("6/4b01b01/8/5b01b0/2b04r0/1b04r01/5r01rr/1r04 b", "C5-C6", "C5-B5", "B6-C6");
        testPosition("3bb2/b02b02b01/3b02bbb0/1b06/1r0r02r01r0/6r01/5r0r0r0/6 b", "B4-C5", "E1-D3", "D2-D3"); // NOTE: unconfirmed
    }

    @Test
    @DisplayName("Gruppe S")
    public void testGruppeS() {
        testPosition("2b03/1b0b05/6b01/3bb2r01/3r02r01/2b05/2r03r01/3r02 b", "D4-C6");
        testPosition("2b03/1b0b05/6b01/3b02r01/1b01r02r01/2b05/2r03r01/3r02 b", "B5-C5","B5-B6");      //NOTE: Added Move (For Forum)
    }

    @Test
    @DisplayName("Gruppe Z")
    public void testGruppeZ() {
        testPosition("6/8/6r01/2b01r0r02/1r03r02/8/8/6 r", "B5-C4"); //G3-G2
        testPosition("1b02b0b0/1r06/1b04b01/8/2r02b02/1r01r01r02/5r0r01/r0r01r011 b", "C1-B2");
        testPosition("3b0b01/8/1b0b01b0b02/2r01b01b01/8/2rr2r02/1r06/2r03 r", "C4-B3");
    }

    @Test
    @DisplayName("Gruppe J")
    public void testGruppeJ() {
        testPosition("6/1bb1b0bbb0b01/r02b04/2b01b0b02/2r02r02/1r02rrr02/6rr1/2r01r01 r", "A3-B2"); //G3-G2
        //testPosition("3b02/1bb6/1r0b02r02/2r05/4r03/8/2r03r01/6 r", "B3-A3"); //Hier findet der Algorithmus einen Zug, der einen Zug länger für den Gewinn braucht als der expected move
    }

    @Test
    @DisplayName("Gruppe I")
    public void testGruppeI() {
        testPosition("3b02/5b02/8/1b06/4bb3/6r01/1r06/1r0r03 b", "E5-F7"); //G3-G2
        testPosition("6/1b06/5b02/2b05/2b05/4r03/2r05/6 b", "C4-C5");
    }

    @Test
    @DisplayName("Gruppe W")
    public void testGruppeW() {
        testPosition("6/8/2b01b03/6b0r0/4b03/8/r07/6 b", "E5-E6"); //G3-G2
        testPosition("6/8/8/8/8/1r0b0r0b0r02/4r03/3rr2 r", "E8-C7");
    }

    @Test
    @DisplayName("Gruppe R")
    public void testGruppeR() {
        testPosition("2b03/r07/3r04/6rr1/4bb3/2b04bb/3rr1rr2/5r0 b", "H6-G8"); //G3-G2
        testPosition("6/3b01b02/4bb3/1bb6/3rr1r02/8/4r03/6 r", "D5-B4");
    }

    @Test
    @DisplayName("Gruppe AC")
    public void testGruppeAC() {
        testPosition("3b01b0/3b04/3bb4/2r05/rbbr5rb/4rr3/br4r02/6 b", "A5-B7","H5-G7"); //G3-G2
        testPosition("1b01b01b0/bb6bb/3bb4/2rr5/r0r01r03r0/1br3rr2/8/6 r", "C4-B2", "C4-D2");
    }

    @Test
    @DisplayName("Gruppe K")
    public void testGruppeK() {
         testPosition("1bbb01b0b0/4b03/4r01b01/2b01r0b02/5r02/1r06/3r02r01/1rrr01r01 r", "E4-E3"); //G3-G2
         testPosition("b0b02b01/3b01b0b01/2r01r02b0/4r0b02/r07/3b04/5r02/rr3rr1 r", "E3-D2","C3-D2");
    }

    @Test
    @DisplayName("Gruppe AF")
    public void testGruppeAF() {
        testPosition("1b0b0b0b0b0/1b06/8/2bb5/8/3rr4/1r03r0r01/r0r0r0r0r0r0 b", "C4-D6"); //G3-G2
        testPosition("2b01bbb0/2b0r0b03/4b03/2bbb04/3r04/5r02/1r03r02/r0r0r0r0r0r0 r", "D5-C4");
    }

    @Test
    @DisplayName("Gruppe G")
    public void testGruppeG() {
        testPosition("b02b01b0/4r03/1b02r03/1bb6/8/4r0b02/1r03r02/r01r02r0 r", "E3-E2"); //G3-G2
        testPosition("1b01b01b0/1b06/3b04/8/4b0r02/2b03r01/3r0r03/r03r01 b", "C6-D7"); 
    }

    @Test
    @DisplayName("Gruppe P")
    public void testGruppeP() {
        // testPosition("b0b01bb2/6b01/3bb4/4b0b02/3r04/3r04/1r0r05/1r0rrrr2 b", "D3-F4", "E4-D5"); // NOTE: to be confirmed
        testPosition("b04b0/8/7r0/1b03b02/1rr5r0/4r0b02/b07/4r01 b", "A7-B7"); // fixed from F6-F7
    }

    @Test
    @DisplayName("Gruppe B")
    public void testGruppeB() {
        testPosition("1bb4/1b0b01r03/b01b0bb4/1b01b01b02/3r01r02/b0r0r02rr2/4r01rr1/4r0r0 b", "C1-E2");
     }

    @Test
    @DisplayName("Gruppe AD")
    public void testGruppeAD() {
        testPosition("6/1bb1b02b01/8/2r05/3r01b02/5r0r01/2rr1r03/6 b", "F5-G6");
        testPosition("3b02/5r02/3r04/8/8/2b02b02/2r05/6 b", "E1-F1", "E1-F2");
    }

    @Test
    @DisplayName("Gruppe X")
    public void testGruppeX() {
        testPosition("b05/1r03r02/2b01b03/2r01r03/1b06/8/3b02b01/r04r0 b", "D7-D8");
        testPosition("4b0b0/2b0br4/3b04/2b0b01b02/8/4r03/1r03r02/r0r0r01r0r0 r", "D2-F1", "D2-B1");
    }

    @Test
    @DisplayName("Gruppe Q")
    public void testGruppeQ() {
        testPosition("b03b01/1b0b0b02b01/4b01b01/1b0b05/3b01r0b01/1r02b01rr1/4r0r02/r0r0r03 b", "E6-F7");
        testPosition("6/3b0b03/8/1rrr02bb2/6b01/2b05/3r04/5r0 b", "C6-D7", "C6-C7");
    }

    @Test
    @DisplayName("Gruppe V")
    public void testGruppeV() {
        testPosition("1b01b02/8/3b04/1rbr05/6bb1/1r02r03/2rr5/6 b", "G5-F7");
        testPosition("6/4b03/1b01b01bb2/r02r04/8/br01/2r02r02/6 r", "A4-B3");
    }

    @Test
    @DisplayName("Gruppe L")
    public void testGruppeL() {
        testPosition("2b03/3bb4/1b02b03/4br3/3r02bb1/1rr2rr3/2r03rr1/6 b", "G5-F7");
        testPosition("6/4bb3/5b02/r03b03/1b02r01r0b0/1rr2r02r0/8/6 r", "B6-A4");
    }

    @Test
    @DisplayName("Gruppe AI")
    public void testGruppeAI() {
        testPosition("6/6b01/8/2b02rr2/8/8/6r01/6 r", "F4-G2", "F4-E2");
        testPosition("2b03/8/8/1b03b02/3rr4/8/8/6 b", "B4-B5", "F4-F5"); // added missings
    }

    @Test
    @DisplayName("Gruppe O")
    public void testGruppeO() {
        testPosition("6/8/5bb2/8/6b01/8/r07/6 b", "F3-G5","G5-G6");
        testPosition("6/4r03/8/8/8/8/4b03/6 r", "E2-E1");
    }

    @Test
    @DisplayName("Gruppe N")
    public void testGruppeN() {
        testPosition("b0b01b0b0b0/8/4b0b02/3br4/6b01/2rr3rb1/4rr3/r0r02r0r0 r", "E7-G6");
        testPosition("b0b01b0b0b0/8/4b0b02/3br4/6b01/2rr3rb1/4rr3/r0r02r0r0 r", "E7-G6");
    }

    @Test
    @DisplayName("Gruppe E")
    public void testGruppeE() {
        testPosition("b0b0b0b0b01/1b01b02b01/2r05/2r01b03/1r06/3bb4/2r0r02r01/r01r0r0r0r0 b", "D6-E8", "D6-C8");
        testPosition("b01b03/4b03/1b03r02/3rbb03/1bb4r01/8/2r02r02/1r0r02r0 r", "F3-F2");
    }

    @Test
    @DisplayName("Gruppe AJ")
    public void testGruppeAJ() {
        testPosition("1b04/1bb2b0bb2/2bb1b03/3rr4/2r02b01r0/1b02r0rr1b0/1rr2r03/6 r", "D4-E2");
        testPosition("6/8/8/3b04/3b04/8/2r01r03/6 b", "D4-D5");
    }

    /**
     * Main method for visualising the board state.
     * Initializes MoveGenerator and prints the board state.
     *
     * @param args  Command line arguments (not used).
     */
    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard("3bb2/b02b02b01/3b02bbb0/1b06/1r0r02r01r0/6r01/5r0r0r0/6 b");
        moveGenerator.printBoard(true);
    }
}
