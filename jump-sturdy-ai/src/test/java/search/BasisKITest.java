package search;

import game.MoveGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    private void testMoves(String fen, String expectedMove) {
        init();
        String answer = ki.orchestrator(fen);
        assertEquals(answer, expectedMove);
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
        testMoves("6/4bbb02/b02b01b02/1b02b03/2b01rrrr2/6r01/r01r0r0r03/5r0 r", "E5-D3");
    }

    @Test
    @DisplayName("Gruppe T")
    public void testGruppeT() {
        testMoves("1b0b0b02/8/3b04/3b04/r0r06/2b05/5r0r01/6 b", "C6-C7");
        // testMoves("6/4bb3/8/8/4b0r0b01/8/8/6 b", "E2-F4");
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
        testMoves("6/4b01b01/8/5b01b0/2b04r0/1b04r01/5r01rr/1r04 b", "C5-C6");
        testMoves("3bb2/b02b02b01/3b02bbb0/1b06/1r0r02r01r0/6r01/5r0r0r0/6 b", "B4-C5");
    }

    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard("6/4bb3/8/8/4b0r0b01/8/8/6 b");
        moveGenerator.printBoard(true);
    }
}
