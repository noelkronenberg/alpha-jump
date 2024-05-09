package game;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class MoveGeneratorTest {

    static game.MoveGenerator moveGenerator;

    @BeforeAll
    public static void init() {
        moveGenerator = new game.MoveGenerator();
        moveGenerator.initializeBoard();
    }

    @Test
    @DisplayName("Gruppe A")
    public void testGruppeA() {
        testMoves("6/1b06/1r03bb2/2r02b02/8/5r0r0/2r0r04/6 r",
                "B3-A3,B3-C3,C4-B4,C4-D4,C4-C3,F6-E6,F6-G6,F6-F5,G6-F6,G6-H6,G6-G5,C7-B7,C7-D7,C7-C6,D7-C7,D7-E7,D7-D6");
        testMoves("6/1b0b0b0b0b0b01/1b0b0b0b0b0b01/8/8/1r0r0r0r0r0r01/1r0r0r0r0r0r01/6 b",
                "B2-C2,B2-A2,B2-B3,C2-D2,C2-B2,C2-C3,D2-E2,D2-C2,D2-D3,E2-F2,E2-D2,E2-E3,F2-G2,F2-E2,F2-F3,G2-H2,G2-F2,G2-G3,B3-C3,B3-A3,B3-B4,C3-D3,C3-B3,C3-C4,D3-E3,D3-C3,D3-D4,E3-F3,E3-D3,E3-E4,F3-G3,F3-E3,F3-F4,G3-H3,G3-F3,G3-G4");
    }

    @Test
    @DisplayName("Gruppe O")
    public void testGruppeO() {
         testMoves("b0b01b0b0b0/1b0b02b0b01/3b0b03/2b05/3r04/2r05/1r01rr1r0r01/r0r02r0r0 b",
                 "B1-B2,B1-C1,C1-B1,C1-C2,C1-D1,E1-D1,E1-E2,E1-F1,F1-E1,F1-F2,F1-G1,G1-F1,G1-G2,B2-A2,B2-B3,B2-C2,C2-B2,C2-C3,C2-D2,D3-C3,D3-D4,D3-E3,E3-D3,E3-E4,E3-F3,F2-E2,F2-F3,F2-G2,G2-F2,G2-G3,G2-H2,C4-B4,C4-C5,C4-D5,C4-D4");
        testMoves("6/2bb1b03/4b0b02/3b01r02/2b05/8/1rr1r02r01/6 r",
                "B7-A5,B7-C5,B7-D6,D7-C7,D7-D6,D7-E7,G7-F7,G7-G6,G7-H7,F4-E4,F4-G4,F4-E3");
    }

    @Test
    @DisplayName("Gruppe I")
    public void testGruppeI() {
        testMoves("b0b01b01b0/2b0bbb0bb1b0/8/1b06/5r02/2r05/1r01r0rr1r01/r0r0r02rr b",
                "B1-B2,B1-C1,C1-B1,C1-C2,C1-D1,E1-D1,E1-E2,E1-F1,G1-F1,G1-G2,C2-B2,C2-C3,D2-B3,D2-C4,D2-E4,D2-F3,E2-E3,F2-D3,F2-E4,F2-G4,F2-H3,H2-G2,H2-H3,B4-A4,B4-B5,B4-C4");
        testMoves("1b01b02/2b01b01b0b0/2r02b02/4r03/2b02b02/b07/3r02r01/rr4rr b",
                "C1-B1,C1-C2,C1-D1,E1-D1,E1-E2,E1-F1,C2-B2,C2-D2,E2-D2,E2-E3,E2-F2,G2-F2,G2-G3,G2-H2,H2-G2,H2-H3,F3-E3,F3-E4,F3-F4,F3-G3,C5-B5,C5-C6,C5-D5,F5-E5,F5-F6,F5-G5,A6-A7,A6-B6");
    }

    @Test
    @DisplayName("Gruppe AL")
    public void testGruppeAL() {
        testMoves("6/2b02b02/2r02r02/8/8/2b02b02/2r02r02/6 b", // changed 8 to 6 in the first and last line
                "C6-B6,C6-D6,F6-E6,F6-G6,C2-B2,C2-D2,F2-E2,F2-G2");
        testMoves("6/1bbbbbbbbbbbb1/8/8/8/1r0r0r0r0r0r01/8/r0r0r0r0r0r0 b",
                "B2-A4,B2-C4,B2-D3,C2-B4,C2-D4,C2-A3,C2-E3,D2-C4,D2-E4,D2-B3,D2-F3,E2-D4,E2-F4,E2-C3,E2-G3,F2-E4,F2-G4,F2-D3,F2-H3,G2-F4,G2-H4,G2-E3");
    }

    @Test
    @DisplayName("Gruppe X")
    public void testGruppeX() {
        //testMoves("b0b0b0b0b0b0/2b01b0b02/1b01b04/4r03/2b01r03/6r01/1r0r0r01r02/r0r0r0r01r0 b",
        //"B6-B5,E5-D6,E7-D6,D2-D3,B8-C8,D3-C4,B5-C4,B2-B3,C8-D6,D1-C1,E8-D8,E4-E5,D6-E4,B3-C4,D6-E5,C4-C5,C7-B7,C1-D3,D8-B7,C5-C6,B7-C5,G3-G4,B7-C6,D3-E4,C6-C5,C1-C2,C5-E4,C2-E3,C5-D5,C2-D2,D5-E5,G4-F4,E5-F3,F4-E5,D8-D7,B1-C1,F8-F7,E5-F5,F7-E5"); // feld falschrum gezählt
        //testMoves("1b03b0/3b01b02/8/4b0r02/4b03/4r0b02/3r01r02/1r01r01r0 r",
                //"C1-D1,E5-E4,D1-D2,E4-D2,E1-D2,F7-F6,D2-E4,F6-E6,F5-F6,D7-D6,E3-E4,D6-E6,E4-F6,E6-F4,F6-G8");
    }

    @Test
    @DisplayName("Gruppe E")
    public void testGruppeE() {
        testMoves("1b0b0b0b01/1b0b0b0b0b0b01/8/4r0b02/2b05/3r04/1r0rr1r0r0r01/r01r0r01r0 b",
                "C1-B1,C1-C2,C1-D1,D1-C1,D1-D2,D1-E1,E1-D1,E1-E2,E1-F1,F1-E1,F1-F2,F1-G1,B2-A2,B2-B3,B2-C2,C2-B2,C2-C3,C2-D2,D2-C2,D2-D3,D2-E2,E2-D2,E2-E3,E2-F2,F2-E2,F2-F3,F2-G2,G2-F2,G2-G3,G2-H2,F4-F5,F4-G4,C5-B5,C5-C6,C5-D6,C5-D5");
        testMoves("3b02/1b0b03r01/5b02/8/1b0bb3r01/1r06/2r05/3r02 r",
                "B6-A6,B6-C6,C7-B7,C7-C6,C7-D7,E8-D8,E8-E7,E8-F8,G2-F2,G2-G1,G2-H2,G5-F5,G5-G4,G5-H5,B6-C5");
    }

    @Test
    @DisplayName("Gruppe M")
    public void testGruppeM() {
        testMoves("6/1b06/8/2b01bbb0rb1/1rbr0rr1r0r01/8/b07/6 b",
                "B2-A2,B2-B3,B2-C2,C4-B4,C4-D4,C4-D5,E4-C5,E4-D6,E4-F6,E4-G5,F4-G5,G4-E5,G4-F6,G4-H6,B5-A7,B5-C7,B5-D6,A7-B7");
        testMoves("6/8/6rr1/8/8/8/b0b0b05/6 r",
                "G3-F1,G3-E2");
    }

    @Test
    @DisplayName("Gruppe L")
    public void testGruppeL() {
        testMoves("3b02/2bb2b02/5b0bb1/2r0b04/2rb3b01/1rr1rr2r0r0/5r02/2rr3 b",
                "C2-A3,C2-B4,C2-D4,C2-E3,C5-A6,C5-B7,C5-D7,C5-E6,D4-D5,D4-E4,E1-D1,E1-E2,E1-F1,F2-E2,F2-F3,F2-G2,F3-E3,F3-F4,G3-E4,G3-F5,G3-H5,G5-F5,G5-H5,G5-H6");
        testMoves("1b01b0b01/b01bbb01bbb01/1b06/8/7b0/1r02r01rr1/2rr2rr2/r0r01r0r01 b", // corrected second line from b01bb0b01bb0b01 to b01bbb01bbb01
                "A2-A3,A2-B2,B3-A3,B3-B4,B3-C3,C1-B1,C1-D1,C2-A3,C2-B4,C2-D4,C2-E3,D2-D3,D2-E2,E1-D1,E1-E2,E1-F1,F1-E1,F1-G1,F2-D3,F2-E4,F2-G4,F2-H3,G2-G3,G2-H2,H5-G5,H5-G6,H5-H6");
    }

    @Test
    @DisplayName("Gruppe AF")
    public void testGruppeAF() {
        testMoves("b02b01b0/3b01b02/b02b02b01/b01b05/5r02/1r02r02r0/2rrr02r01/r03r01 b",
                "B1-B2,B1-C1,E1-E2,E1-D1,E1-F1,G1-G2,G1-F1,D2-D3,D2-C2,D2-E2,F2-F3,F2-E2,F2-G2,A3-A4,A3-B3,D3-D4,D3-C3,D3-E3,G3-G4,G3-F3,G3-H3,A4-A5,A4-B4,C4-C5,C4-B4,C4-D4");
        testMoves("6/1b03b02/3b01r0b01/bb2b04/1b01r02r0r0/1r0r02rbr01/1r06/6 r",
                "F3-E3,D5-C5,D5-E5,G5-G4,G5-F5,G5-H5,H5-H4,H5-G5,B6-A6,B6-C6,C6-C5,C6-B6,C6-B5,C6-D6,G6-G5,G6-H6,B7-B6,B7-A7,B7-C7");
    }

    @Test
    @DisplayName("Gruppe AI")
    public void testGruppeAI() {
        testMoves("2b03/8/8/3b0b03/2b03b01/2r03r01/2r05/6 r",
                "C6-B6,C6-D6,C7-B7,C7-C6,C7-D7,G6-F6,G6-H6");
        testMoves("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b",
                "B2-A2,B2-B3,B2-C2,B3-A3,B3-B4,B3-C3,D1-B2,D1-C3,D1-E3,D1-F2,D3-C3,D3-D4,D3-E3,E1-C2,E1-D3,E1-F3,E1-G2,E4-D4,E4-F4,G1-F1,G1-G2,G6-F6,G6-F7,G6-G7,G6-H6");
    }

    @Test
    @DisplayName("Gruppe AJ")
    public void testGruppeAJ() {
        testMoves("b0b01b02/3bbb0bb2/2b03bb1/8/2b01r03/5r02/1rr1r0rr1rr1/1rr4 b",
                "B1-B2,B1-C1,C1-B1,C1-C2,C1-D1,E1-D1,E1-E2,E1-F1,D2-B3,D2-C4,D2-E4,D2-F3,E2-E3,F2-D3,F2-E4,F2-G4,F2-H3,C3-B3,C3-C4,C3-D3,G3-E4,G3-F5,G3-H5,C5-B5,C5-C6,C5-D5");
        testMoves("6/1b02br3/6bb1/2b0b04/2r04r0/8/1rr1r0rr1r01/6 b",
                "B2-A2,B2-B3,B2-C2,G3-E4,G3-F5,G3-H5,C4-B4,C4-D4,D4-C4,D4-C5,D4-D5,D4-E4");
    }

    @Test
    @DisplayName("Gruppe U")
    public void testGruppeU() {
        testMoves("1b01b0b0b0/8/1b02r03/1r06/b02r01r02/r01bb5/2r03r01/1r04 b",
                "C1-B1,C1-C2,C1-D1,E1-D1,E1-E2,E1-F1,F1-E1,F1-F2,F1-G1,G1-F1,G1-G2,B3-A3,B3-C3,A5-B5,C6-A7,C6-B8,C6-D8,C6-E7");
        testMoves("1b0b0b0b0b0/1bb2b0b01b0/2b05/3b04/3r04/8/r01r0r0r01r01/rr1r0r0r0r0 r",
                "D5-C5,D5-E5,A7-A6,A7-B7,C7-B7,C7-C6,C7-D7,D7-C7,D7-D6,D7-E7,E7-D7,E7-E6,E7-F7,G7-F7,G7-G6,G7-H7,B8-A6,B8-C6,B8-D7,D8-C8,D8-D7,D8-E8,E8-D8,E8-E7,E8-F8,F8-E8,F8-F7,F8-G8,G8-F8,G8-G7");
    }

    @Test
    @DisplayName("Gruppe AC/DC")
    public void testGruppeAC() {
        testMoves("2bbb0b0b0/1bbb0b0b0b0b01/8/8/8/1r01r04/2r01r0r0r01/r0r0r0r0r0r0 b",
                "C2-C3,D2-D3,E2-E3,F2-F3,G2-G3,G2-H2,F1-E1,G1-F1,D2-C2,E2-D2,F2-E2,G2-F2,E1-E2,F1-F2,G1-G2,E1-F1,F1-G1,C2-D2,D2-E2,E2-F2,F2-G2,D1-C3,B2-A4,D1-E3,B2-C4,B2-D3,D1-F2");
        testMoves("bb5/1bb6/bb6b0/b06r0/r0b06/6rb1/4rr3/6 b", // added 0's to single stones
                "A4-B4,B1-C3,A3-B5,B1-D2,B2-D3,H3-G3,B2-A4,B2-C4,A3-C4,B5-B6,B5-C5,G6-E7,G6-F8"); // corrected possible moves
    }

    @Test
    @DisplayName("Blutgruppe AB")
    public void testGruppeAB() {
        testMoves("1b0b0b0b0b0/1b01bb2b01/8/3bb1b02/5rr2/2r01r03/2rr5/r0r0r0r0r0r0 b",
                "C1-B1,C1-C2,C1-D1,D1-C1,D1-E1,E1-D1,E1-E2,E1-F1,F1-E1,F1-F2,F1-G1,G1-F1,G1-G2,B2-A2,B2-B3,B2-C2,D2-B3,D2-C4,D2-E4,D2-F3,G2-F2,G2-G3,G2-H2,D4-B5,D4-C6,D4-E6,D4-F5,F4-E4,F4-G4");
        testMoves("b0b0b01b01/2b03b01/8/3b01b02/1b01r01r02/2br1r03/b01r02r02/2r0r0r01 r",
            "F8-G8,F8-F7,F8-E8,E8-F8,E8-E7,E8-D8,D8-E8,D8-D7,D8-C8,F7-G7,F7-F6,F7-E7,C7-D7,C7-B7,E6-F6,E6-E5,E6-D6,C6-E5,C6-D4,C6-B4,C6-A5,F5-G5,F5-E5,D5-E5,D5-C5");
    }

    @Test
    @DisplayName("Gruppe G")
    public void testGruppeG() {
        testMoves("b01b0b0b0b0/1b0b01b01b01/3b01b02/2b05/8/2r0r01rr2/1r04r01/r0r0r0r0r0r0 r",
                "B8-B7,B8-C8,C8-B8,C8-C7,C8-D8,D8-C8,D8-D7,D8-E8,E8-D8,E8-E7,E8-F8,F8-E8,F8-F7,F8-G8,G8-F8,G8-G7,B7-A7,B7-B6,B7-C7,C6-B6,C6-C5,C6-D6,D6-C6,D6-D5,D6-E6,G7-F7,G7-G6,G7-H7,F6-D5,F6-E4,F6-G4,F6-H5");
        testMoves("b01b01b01/8/2b03b01/1b06/1r01b01b02/3r04/2r03r01/4r01 r",
                "F8-E8,F8-F7,F8-G8,C7-B7,C7-C6,C7-D7,G7-F7,G7-G6,G7-H7,D6-C6,D6-E6,B5-A5,B5-C5");
    }

    @Test
    @DisplayName("Gruppe Anthony Davis")
    public void testGruppeAD() {
        testMoves("b0b0b01bb1/2b0b0bbb02/5r02/3b04/4r0b02/8/2rrr01r02/r0r0r0r01r0 r",
                "B8-B7,B8-C8,C8-B8,C8-D8,D8-C8,D8-E8,D8-D7,E8-D8,E8-F8,E8-E7,G8-F8,G8-G7,C7-A6,C7-B5,C7-D5,C7-E6,D7-D6,D7-E7,F7-E7,F7-G7,F7-F6,E5-D5,E5-D4,E5-E4,F3-E3,F3-E2,F3-G3"); // - was missing, added afterwards
        testMoves("3b02/5r02/3r04/8/8/2b02b02/2r05/6 b",
                "C6-B6,C6-D6,F6-E6,F6-G6,F6-F7,E1-D1,E1-F1,E1-E2,E1-F2");
    }

    @Test
    @DisplayName("Gruppe Z")
    public void testGruppeZ() {
        testMoves("3b01b0/1bb1r0b03/4bb3/2b03b01/2r03r01/3rb1r02/2r0r04/r02r01r0 b",
                "D6-B7,D6-C8,D6-E8,D6-F7,C4-B4,C4-D4,G4-F4,G4-H4,E3-C4,E3-D5,E3-F5,E3-G4,B2-A4,B2-C4,B2-D3,E2-F2,E1-D1,E1-D2,E1-E2,E1-F1,G1-F1,G1-G2");
        testMoves("2b01b0b0/2b0bb4/8/1r06/3b0rbr0b01/3r02r01/2rr5/2r01r01 b",
                "D5-C5,E5-C6,E5-D7,E5-F7,E5-G6,G5-H5,C2-B2,C2-C3,D2-B3,D2-C4,D2-E4,D2-F3,D1-C1,D1-E1,F1-E1,F1-F2,F1-G1,G1-G2,G1-F1");
    }

    @Test
    @DisplayName("Gruppe B")
    public void testGruppeB() {
        testMoves("2b02bb/1bb2b03/5bb2/8/1r03r02/6r01/8/r01r01rrr0 b",
                "D1-C1,D1-D2,D1-E1,G1-E2,G1-H3,B2-A4,B2-C4,B2-D3,E2-D2,E2-E3,E2-F2,F3-D4,F3-E5,F3-G5,F3-H4");
        testMoves("b01bbb01b0/1b02b03/3bbr01b01/8/3rr1b0b01/8/2r01r01rr1/r0r0r01r01 r",
                "B8-B7,B8-C8,C8-C7,C8-B8,C8-D8,D8-D7,D8-C8,D8-E8,F8-F7,F8-E8,F8-G8,C7-C6,C7-B7,C7-D7,E7-E6,E7-D7,E7-F7,G7-E6,G7-F5,G7-H5,D5-B4,D5-C3,D5-E3,D5-F4,E3-F3");
    }

    @Test
    @DisplayName("Gruppe K")
    public void testGruppeK() {
        testMoves("b0b0b0b0b0b0/1bb3b0b01/3b04/4b03/5r02/1r02r03/2r0r02r01/r0r0r0r0r0r0 b",
                "B1-C1,C1-B1,C1-C2,C1-D1,D1-C1,D1-D2,D1-E1,E1-D1,E1-E2,E1-F1,F1-E1,F1-F2,F1-G1,G1-F1,G1-G2,B2-A4,B2-C4,B2-D3,F2-E2,F2-F3,F2-G2,G2-F2,G2-G3,G2-H2,D3-C3,D3-D4,D3-E3,E4-D4,E4-E5,E4-F5,E4-F4");
        testMoves("1bbb01b0b0/4b03/4rr1b01/2b02b02/5r02/1r06/3r02r01/1rrr01r01 r",
                "C8-A7,C8-B6,C8-D6,C8-E7,D8-D7,D8-E8,F8-E8,F8-F7,F8-G8,D7-C7,D7-D6,D7-E7,G7-F7,G7-G6,G7-H7,B6-A6,B6-B5,B6-C6,F5-E5,F5-G5,E3-C2,E3-D1,E3-F1,E3-G2");
    }

    @Test
    @DisplayName("Gruppe N")
    public void testGruppeN() {
        testMoves("b0b01b0b0b0/8/4b0b02/3br4/6b01/2rr3rb1/4rr3/r0r02r0r0 r",
               "B8-B7,B8-C8,C8-D8,C8-B8,C8-C7,F8-E8,C6-E5,C6-B4,C6-A5,E7-D5,E7-G6,E7-F5,G8-F8,G8-G7,D4-B3,F8-F7,F8-G8,D4-C2,D4-F3,D4-E2");
        testMoves("b0b0b0b0b0b0/8/5b01b0/5r0b01/3b04/4r0rr1rb/3r04/r0r0r0r01r0 b",
                "H3-H4,C1-B1,C1-D1,E1-E2,G4-G5,G1-G2,B1-C1,D5-D6,D1-D2,F3-E3,F1-G1,F3-G3,F1-E1,H6-G8,H3-G3,C1-C2,G4-H4,E1-D1,E1-F1,G1-F1,B1-B2,D1-E1,D5-C5,D5-E6,D1-C1,D5-E5,F1-F2,H6-F7");
    }

    @Test
    @DisplayName("Gruppe T")
    public void testGruppeT() {
        testMoves("6/1b06/2bb1b0b02/6bb1/1r0br5/3r0rr3/8/4r0r0 b",
                "B2-A2,B2-C2,B2-B3,C3-A4,C3-E4,C3-B5,C3-D5,E3-D3,E3-F3,E3-E4,F3-E3,F3-G3,F3-F4,G4-E5,G4-F6,G4-H6");
        testMoves("6/8/6b01/4bb3/r0r0rr4b0/3b02r01/1rr3r02/6 r", // added r0 instead of r in the 7th row
                "A5-B5,A5-A4,B5-A5,B5-B4,C5-E4,C5-A4,C5-D3,C5-B3,G6-F6,G6-H6,G6-H5,G6-G5,B7-D6,B7-A5,F7-E7,F7-G7,F7-F6"); // 3 possible moves missing
    }

    @Test
    @DisplayName("Gruppe J")
    public void testGruppeJ() {
        testMoves("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b",
                "B2-A2,B2-C2,C3-D3,F4-E4,F4-G4,F4-F5,F3-D4,F3-E5,F3-G5,F3-H4");
        testMoves("b0b04/b02bb2b01/2b05/4rb3/6b01/2r04r0/1r01r0r01r01/r0r04 r",
                "B8-B7,B8-C8,C8-B8,C8-C7,C8-D8,B7-A7,B7-B6,B7-C7,D7-C7,D7-D6,D7-E7,E7-D7,E7-E6,E7-F7,G7-F7,G7-G6,G7-H7,C6-B6,C6-C5,C6-D6,H6-G6,H6-H5,H6-G5");
    }

    @Test
    @DisplayName("Gruppe F")
    public void testGruppeF() {
        testMoves("b0b04/r0r0b02b0b0b0/2r02r0r0r0/8/8/b0b0b02b02/r0r0r02r0b0b0/4r0r0 b",
                "B1-A2,B1-C1,C1-B1,C1-B2,C1-D1,C1-C2,C2-D2,F2-E2,F2-G2,F2-G3,G2-F2,G2-F3,G2-H2,G2-H3,H2-G2,H2-G3,A6-B6,A6-B7,B6-A6,B6-A7,B6-C6,B6-C7,C6-B6,C6-B7,C6-D6,F6-E6,F6-G6,G7-F8,G7-H7,H7-G7,H7-G8");
        testMoves("6/3bb4/1br6/r01b0b02bb1/1rr2r0r01b0/6rb1/4rr3/6 b",
                "D2-C4,D2-B3,D2-E4,D2-F3,C4-B4,C4-B5,C4-D4,C4-C5,D4-C4,D4-E4,D4-E5,D4-D5,G4-F6,G4-E5,G4-H6,H5-G5,H5-H6,G6-F8,G6-E7");
    }

    @Test
    @DisplayName("Gruppe R")
    public void testGruppeR() {
        testMoves("1b0b01b0b0/3bb4/8/1r03b02/3b0rrr0b01/6r01/1r0r0r04/2r01r01 r",
                "D8-C8,D8-E8,D8-D7,F8-E8,F8-G8,F8-F7,B7-A7,B7-C7,B7-B6,C7-B7,C7-D7,C7-C6,D7-C7,D7-E7,D7-D6,G6-F6,G6-H6,E5-C4,E5-G4,E5-D3,E5-F3,B4-A4,B4-C4,B4-B3");
        testMoves("6/7rr/4bb1r01/8/8/b02bb3b0/8/6 r",
                "G3-F3,G3-H3,G3-G2,H2-F1");
    }

    @Test
    @DisplayName("Gruppe 4-1")
    public void testGruppeAG() {
        testMoves("b03b01/3bb2bb1/2bb1br3/1b06/5r02/2rr5/1r02rr3/r0r02rr1 b",
                "B1-C1,B1-B2,F1-E1,F1-G1,F1-F2,D2-B3,D2-F3,D2-C4,D2-E4,G2-E3,G2-F4,G2-H4,C3-A4,C3-E4,C3-B5,C3-D5,B4-A4,B4-C4,B4-B5");
        testMoves("b03b01/3b02b01/r01b05/8/8/4rb3/1r06/r03r01 b", // 3rbb03 corrected to 4rb3
                "B1-C1,B1-B2,F1-E1,F1-G1,F1-F2,D2-C2,D2-E2,D2-D3,G2-F2,G2-H2,G2-G3,C3-B3,C3-D3,C3-C4,E6-C7,E6-G7,E6-D8,E6-F8");
    }

    @Test
    @DisplayName("Gruppe S")
    public void testGruppeS() {
        testMoves("b0b0b0b0b0b0/2bbb02bb1/4b03/8/3r04/8/1r0r01r0r0r01/r0r0r0r0r0r0 r",
                "B8-C8,B8-B7,C8-B8,C8-C7,C8-D8,D8-C8,D8-D7,D8-E8,E8-D8,E8-E7,E8-F8,F8-E8,F8-F7,F8-G8,G8-F8,G8-G7,B7-A7,B7-B6,B7-C7,C7-B7,C7-C6,C7-D7,E7-D7,E7-E6,E7-F7,F7-E7,F7-F6,F7-G7,G7-F7,G7-G6,G7-H7,D5-C5,D5-D4,D5-E5");
        testMoves("2b03/1b0b05/6b01/3bb2r01/3r02r01/2b05/2r03r01/3r02 b",
            "D1-C1,D1-D2,D1-E1,B2-A2,B2-B3,B2-C2,C2-B2,C2-C3,C2-D2,G3-F3,G3-H3,D4-B5,D4-C6,D4-E6,D4-F5,C6-B6,C6-D6");
    }

    @Test
    @DisplayName("Gruppe AA")
    public void testGruppeAA() {
       // testMoves("1r0r0r0r01/8/1r01rb2rr1/5bb2/2b05/8/4bb3/1b02b01 b",
                //"F5-G7,F5-E7,F5-H6,F5-H4,F5-D4,F5-E3,F5-G3,C4-C5,C4-C3,C4-B4,C4-D4,E2-F4,E2-D4,E2-C3,E2-C1,E2-G3,E2-G1,C1-C2,C1-B1,C1-D1,F1-F2,F1-G1,F1-E1");
        testMoves("b0b0b01b0b0/4b01b01/2bb2b02/8/1r01br4/4r01r01/2r02rr2/r01r01r0r0 r",
                "B8-C8,B8-B7,D8-C8,D8-E8,D8-D7,F8-E8,F8-G8,G8-F8,G8-G7,C7-B7,C7-D7,C7-C6,F7-D6,F7-E5,F7-G5,F7-H6,E6-D6,E6-E5,E6-F6,G6-F6,G6-G5,G6-H6,B5-A5,B5-B4,B5-C5,D5-B4,D5-C3,D5-E3,D5-F4");
    }

    @Test
    @DisplayName("Gruppe Q")
    public void testGruppeQ() {
        testMoves("6/8/4b03/1r01b0r03/2r02r0b01/1b02b01r01/1r06/6 b",
                "B6-A6,B6-C6,D4-D5,D4-C5,D4-C4,E3-D3,E3-F3,E6-D6,E6-F6,E6-E7,G5-H5"); // D4-C4 was missing
        testMoves("b0b0b0b0b0b0/8/8/4b03/1r01b01r02/8/3r02r01/6 r",
                "B5-B4,B5-A5,B5-C5,D7-C7,D7-E7,D7-D6,F5-E4,F5-E5,F5-G5,F5-F4,G7-G6,G7-H7,G7-F7"); // corrected red forward moves
    }

    @Test
    @DisplayName("Gruppe W")
    public void testGruppeW() {
        //testMoves("r03r01/8/2r05/3b04/8/8/5b02/1b04 b",
              //"B1-B2,B1-C1,F1-E1,F1-F2,F1-G1,C3-B3,C3-C4,C3-D3,C3-D4,D4-C4,D4-D3,D4-E4,D4-C3,F7-E7,F7-F6,F7-G7,C8-B8,C8-C7,C8-D8");
       // testMoves("r05/8/8/3rr4/5b02/8/1b06/6 b",
         //   "B1-B2,B1-C1,D4-F5,D4-E6,D4-C6,D4-B5,F5-E5,F5-F4,F5-G5,B7-A7,B7-B6,B7-C7");
    }

    @Test
    @DisplayName("Gruppe Alpha Jump")
    public void testGruppeC() {
        testMoves("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 r",
                "C8-B8,C8-C7,C8-D7,C8-D8,B7-A5,B7-C5,B7-D6,E4-E3,E4-F4,F7-H6,F7-G5,F7-E5,F7-D6,G7-H5,G7-F5,G7-E6,E2-G1,E2-C1");
        testMoves("6/1bbbbbbbbbbbb1/8/8/8/8/1rrrrrrrrrrrr1/6 b",
                "B2-A4,B2-C4,B2-D3,C2-A3,C2-B4,C2-D4,C2-E3,D2-B3,D2-C4,D2-E4,D2-F3,E2-C3,E2-D4,E2-F4,E2-G3,F2-D3,F2-E4,F2-G4,F2-H3,G2-E3,G2-F4,G2-H4");
    }

    @Test
    @DisplayName("Gruppe V")
    public void testGruppeV() {
        testMoves("b0b0b0b01b0/2bb1b03/1b01b01bb2/3r04/2r05/2r01r0r0r01/3r04/r0r0r0r01r0 b",
                "B1-B2,B1-C1,C1-B1,C1-D1,D1-C1,D1-D2,D1-E1,E1-D1,E1-E2,E1-F1,G1-F1,G1-G2,C2-A3,C2-E3,C2-B4,C2-D4,E2-E3,E2-D2,E2-F2,B3-B4,B3-A3,B3-C3,D3-C3,D3-E3,F3-D4,F3-H4,F3-E5,F3-G5"); //5 moves zu viel, first blue row was correct, rest has been corrected
        testMoves("2bb3/5b02/8/2bb5/5rr2/8/3b03r0/7 r", // used r instead of b
                "F5-D4,F5-E3,F5-G3,F5-H4,H7-G7,H7-H6");
    }

    @Test
    @DisplayName("Gruppe P")
    public void testGruppeP() {
        testMoves("1b0b01b0b0/3b0b03/1b03b02/2b01b03/4r0r0b01/4r01r01/1rr1rr4/1r0r01r01 b",
            "C1-B1,C1-D1,C1-C2,D1-C1,D1-E1,D1-D2,F1-G1,F1-E1,F1-F2,G1-F1,G1-G2,D2-E2,D2-C2,D2-D3,E2-D2,E2-E3,E2-F2,B3-A3,B3-B4,B3-C3,F3-E3,F3-F4,F3-G3,C4-B4,C4-C5,C4-D4,E4-D4,E4-F4,E4-F5,G5-H5"); // ein zug war doppelt
        testMoves("1b03b0/r02bb1b02/3b04/1r06/4r0r0b01/2b03r01/2r05/2r01r01 r", // changed b to r
                "A2-B2,B4-A4,B4-C4,B4-B3,E5-F5,E5-D5,E5-E4,F5-E5,F5-F4,G6-F6,G6-H6,C7-B7,C7-D7,D8-D7,D8-E8,D8-C8,F8-E8,F8-G8,F8-F7");
    }

    private void testMoves(String fen, String expectedMoves) {
        Color color = fen.charAt(fen.length() - 1) == 'r' ? Color.RED : Color.BLUE;
        fen = fen.substring(0, fen.length() - 2);
        moveGenerator.initializeBoard(fen);
        moveGenerator.printBoard(false);

        // Determine color based on the last character of FEN
        Map<Integer, List<Integer>> actualMovesMap = moveGenerator.generateAllPossibleMoves(color);

        String actualMoves = moveGenerator.convertAllMoves(actualMovesMap);
        String[] actualMovesArray = actualMoves.split(", ");
        String[] expectedMovesArray = expectedMoves.trim().split(",");

        Arrays.sort(actualMovesArray);
        Arrays.sort(expectedMovesArray);

        assertArrayEquals(expectedMovesArray, actualMovesArray);
    }

}