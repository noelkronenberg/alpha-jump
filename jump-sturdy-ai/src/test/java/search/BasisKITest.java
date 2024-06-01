package search;

import game.MoveGenerator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class BasisKITest {

    static BasisKI ki;

    @BeforeAll
    public static void init() {
        ki = new BasisKI();
    }

    @Test
    @DisplayName("Anzahl untersuchter Züge (mit händischen Beweis)")
    public void anzahlSanityCheck() {
        init();
        ki.orchestrator("6/8/8/3r04/4b03/8/8/6 b", 2);
        assertEquals(ki.positionsHM.size(), 13);
    }

    private void testMoves(String fen, String... expectedMoves) {
        init();
        String answer = ki.orchestrator(fen);
        boolean matchFound = false;
        for (String expectedMove : expectedMoves) {
            if (expectedMove.equals(answer)) {
                matchFound = true;
                break;
            }
        }

        System.out.println("Expected: " + Arrays.asList(expectedMoves)+ "\n" + "Actual: " + answer);
        assertTrue(matchFound);
    }

    private void testMoves(String fen, String expectedMove) {
        init();
        String answer = ki.orchestrator(fen);
        assertEquals(expectedMove, answer);
        System.out.println(answer);
    }

    @Test
    @DisplayName("Gruppe H")
    public void testGruppeH() {
        testMoves("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b", "A6-A7");
        testMoves("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 r", "F5-G3");
        testMoves("6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 b", "D5-C7");
        testMoves("6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 r", "D3-E2");
    }

    @Test
    @DisplayName("Gruppe F")
    public void testGruppeF() {
        testMoves("6/7b0/8/8/1r06/4b03/2rr1rrr02/5r0 b", "E6-D6");
        testMoves("6/4bbb02/b02b01b02/1b02b03/2b01rrrr2/6r01/r01r0r0r03/5r0 r", "E5-D3", "E5-F3");
    }

    @Test
    @DisplayName("Gruppe T")
    public void testGruppeT() {
        testMoves("1b0b0b02/8/3b04/3b04/r0r06/2b05/5r0r01/6 b", "C6-C7");
        testMoves("6/4bb3/8/8/4b0r0b01/8/8/6 b", "E2-F4");
    }

    @Test
    @DisplayName("Gruppe AG")
    public void testGruppeAG() {
        testMoves("6/8/8/8/b0b02b0b0/2b05/2r0r0r0r02/6 b", "C6-D7");
        testMoves("3b01b0/3bb1b02/8/8/8/2r0b0r02/8/0r04r0 b", "D6-D7");
    }

    @Test
    @DisplayName("Gruppe C")
    public void testGruppeC() {
        testMoves("6/4b01b01/8/5b01b0/2b04r0/1b04r01/5r01rr/1r04 b", "C5-C6", "C5-B5", "B6-C6");
        testMoves("3bb2/b02b02b01/3b02bbb0/1b06/1r0r02r01r0/6r01/5r0r0r0/6 b", "B4-C5");
    }

    @Test
    @DisplayName("Gruppe S")
    public void testGruppeS() {
        testMoves("2b03/1b0b05/6b01/3bb2r01/3r02r01/2b05/2r03r01/3r02 b", "D4-C6");
        testMoves("2b03/1b0b05/6b01/3b02r01/1b01r02r01/2b05/2r03r01/3r02 b", "B5-C5");
    }

    @Test
    @DisplayName("Gruppe Z")
    public void testGruppeZ() {
        testMoves("6/8/6r01/2b01r0r02/1r03r02/8/8/6 r", "B5-C4"); //G3-G2
        testMoves("1b02b0b0/1r06/1b04b01/8/2r02b02/1r01r01r02/5r0r01/r0r01r011 b", "C1-B2");
        testMoves("3b0b01/8/1b0b01b0b02/2r01b01b01/8/2rr2r02/1r06/2r03 r", "C4-B3");
    }

    @Test
    @DisplayName("Gruppe J")
    public void testGruppeJ() {
        testMoves("6/1bb1b0bbb0b01/r02b04/2b01b0b02/2r02r02/1r02rrr02/6rr1/2r01r01 r", "A3-B2"); //G3-G2
        testMoves("3b02/1bb6/1r0b02r02/2r05/4r03/8/2r03r01/6 r", "B3-A3");
    }

    @Test
    @DisplayName("Gruppe I")
    public void testGruppeI() {
        testMoves("3b02/5b02/8/1b06/4bb3/6r01/1r06/1r0r03 b", "E5-F7"); //G3-G2
        testMoves("6/1b06/5b02/2b05/2b05/4r03/2r05/6 b", "C4-C5");
    }

    @Test
    @DisplayName("Gruppe W")
    public void testGruppeW() {
        testMoves("6/8/2b01b03/6b0r0/4b03/8/r07/6 b", "E5-E6"); //G3-G2
        // testMoves("6/8/8/8/8/1r0b0r0b0r02/4r03/3rr2 b", "E8-C7");
    }

    @Test
    @DisplayName("Gruppe R")
    public void testGruppeR() {
        testMoves("2b03/r07/3r04/6rr1/4bb3/2b04bb/3rr1rr2/5r0 b", "H6-G8"); //G3-G2
        testMoves("6/3b01b02/4bb3/1bb6/3rr1r02/8/4r03/6 r", "D5-B4");
    }

    @Test
    @DisplayName("Gruppe AC")
    public void testGruppeAC() {
        // testMoves("3b01b0/3b04/3bb4/2r05/rbbr5rb/4rr3/br4r02/6 b", "H5-G7"); //G3-G2
        // testMoves("1b01b01b0/bb6bb/3bb4/2rr5/r0r01r03r0/1br3rr2/8/6 r", "D3-C5");
    }

    @Test
    @DisplayName("Gruppe K")
    public void testGruppeK() {
         testMoves("1bbb01b0b0/4b03/4r01b01/2b01r0b02/5r02/1r06/3r02r01/1rrr01r01 r", "E4-E3"); //G3-G2
         // testMoves("b0b02b01/3b01b0b01/2r01r02b0/4r0b02/r07/3b04/5r02/rr3rr1 r", "E3-D2");
    }

    @Test
    @DisplayName("Gruppe AF")
    public void testGruppeAF() {
        testMoves("1b0b0b0b0b0/1b06/8/2bb5/8/3rr4/1r03r0r01/r0r0r0r0r0r0 b", "C4-D6"); //G3-G2
        testMoves("2b01bbb0/2b0r0b03/4b03/2bbb04/3r04/5r02/1r03r02/r0r0r0r0r0r0 r", "D5-C4");
    }

    @Test
    @DisplayName("Gruppe G")
    public void testGruppeG() {
        testMoves("b02b01b0/4r03/1b02r03/1bb6/8/4r0b02/1r03r02/r01r02r0 r", "E3-E2"); //G3-G2
        testMoves("1b01b01b0/1b06/3b04/8/4b0r02/2b03r01/3r0r03/r03r01 b", "C6-D7");
    }

    @Test
    @DisplayName("Gruppe P")
    public void testGruppeP() {
        testMoves("b0b01bb2/6b01/3bb4/4b0b02/3r04/3r04/1r0r05/1r0rrrr2 b", "E4-D5");
        testMoves("b04b0/8/7r0/1b03b02/1rr5r0/4r0b02/b07/4r01 b", "A7-B7"); // fixed from F6-F7
    }

    @Test
    @DisplayName("Gruppe B")
    public void testGruppeB() {
        testMoves("1bb4/1b0b01r03/b01b0bb4/1b01b01b02/3r01r02/b0r0r02rr2/4r01rr1/4r0r0 b", "C1-E2");
        // testMoves("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r", "D8-C8");
    }

    @Test
    @DisplayName("Gruppe Q")
    public void testGruppeQ() {
        testMoves("b03b01/1b0b0b02b01/4b01b01/1b0b05/3b01r0b01/1r02b01rr1/4r0r02/r0r0r03 b", "E6-F7");
        testMoves("6/3b0b03/8/1rrr02bb2/6b01/2b05/3r04/5r0 b", "C6-D7");
    }

    @Test
    @DisplayName("Gruppe V")
    public void testGruppeV() {
        // testMoves("1b01b02/8/3b04/1rbr05/6bb1/1r02r03/2rr5/8 b", "H5-G7");
        testMoves("6/4b03/1b01b01bb2/r02r04/8/br01/2r02r02/6 r", "A4-B3");
    }

    @Test
    @DisplayName("Gruppe L")
    public void testGruppeL() {
        testMoves("2b03/3bb4/1b02b03/4br3/3r02bb1/1rr2rr3/2r03rr1/6 b", "G5-F7");
        testMoves("6/4bb3/5b02/r03b03/1b02r01r0b0/1rr2r02r0/8/6 r", "B6-A4");
    }

    @Test
    @DisplayName("Gruppe AI")
    public void testGruppeAI() {
        testMoves("6/6b01/8/2b02rr2/8/8/6r01/6 r", "F4-G2");
        // testMoves("2b03/8/8/1b03b02/3rr4/8/8/6 b", "B4-B5", "B4-C5", "F4-F5", "F4-E5", "F4-G5"); // added missings
    }

    @Test
    @DisplayName("Gruppe O")
    public void testGruppeO() {
        // testMoves("6/8/5bb2/8/6b01/8/r07/6 b", "F6-F1");
        // testMoves("6/4r03/8/8/8/8/4b03/6 r", "E7-E8");
    }

    @Test
    @DisplayName("Gruppe N")
    public void testGruppeN() {
        // testMoves("b0b01b0b0b0/8/4b0b02/3br4/6b01/2rr3rb1/4rr3/r0r02r0r0 r", "D4-F3", "D4-B3");
        // testMoves("b0b01b0b0b0/8/4b0b02/3br4/6b01/2rr3rb1/4rr3/r0r02r0r0 r", "H6-G8");
    }

    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard("b0b01b0b0b0/8/4b0b02/3br4/6b01/2rr3rb1/4rr3/r0r02r0r0 r");
        moveGenerator.printBoard(true);
    }
}
