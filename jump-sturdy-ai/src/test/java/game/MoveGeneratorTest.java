package game;

import java.awt.*;
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
    @DisplayName("Gruppe V")
    public void testGruppeV() {
        String fen = "2bb3/5b02/8/2bb5/5rr2/8/3b03r0/7";
        String expectedMoves = "F5-D4,F5-E3,F5-G3,F5-H4,H7-G7,H7-H6";
        moveGenerator.initializeBoard(fen);
        moveGenerator.printBoard();
        List<Map.Entry<Integer, List<Integer>>> actualMovesList = moveGenerator.generateAllPossibleMoves(Color.RED);
        String[] actualMovesArray = moveGenerator.convertAllMoves(actualMovesList).split(", ");
        String[] expectedMovesArray = expectedMoves.split(",");
        Arrays.sort(actualMovesArray);
        Arrays.sort(expectedMovesArray);
        assertArrayEquals(expectedMovesArray, actualMovesArray);
    }

    @Test
    @DisplayName("Gruppe C")
    public void testGruppeC() {
        String fen = "5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04";
        String expectedMoves = "C8-B8,C8-C7,C8-D7,C8-D8,B7-A5,B7-C5,B7-D6,E4-E3,E4-F4,F7-H6,F7-G5,F7-E5,F7-D6,G7-H5,G7-F5,G7-E6,E2-G1,E2-C1";

        moveGenerator.initializeBoard(fen);
        moveGenerator.printBoard();

        List<Map.Entry<Integer, List<Integer>>> actualMovesList = moveGenerator.generateAllPossibleMoves(Color.RED);
        String[] actualMovesArray = moveGenerator.convertAllMoves(actualMovesList).split(", ");
        String[] expectedMovesArray = expectedMoves.trim().split(","); // Trim the expected string before splitting

        Arrays.sort(actualMovesArray);
        Arrays.sort(expectedMovesArray);

        assertArrayEquals(expectedMovesArray, actualMovesArray);
    }

    @Test
    @DisplayName("Gruppe R")
    public void testGruppeRFen2() {
        String fen = "6/7rr/4bb1r01/8/8/b02bb3b0/8/6";
        String expectedMoves = "G3-F3,G3-H3,G3-G2,H2-F1";

        moveGenerator.initializeBoard(fen);
        moveGenerator.printBoard();

        List<Map.Entry<Integer, List<Integer>>> actualMovesList = moveGenerator.generateAllPossibleMoves(Color.RED);
        String[] actualMovesArray = moveGenerator.convertAllMoves(actualMovesList).split(", ");
        String[] expectedMovesArray = expectedMoves.split(",");

        Arrays.sort(actualMovesArray);
        Arrays.sort(expectedMovesArray);

        assertArrayEquals(expectedMovesArray, actualMovesArray);
    }

    @Test
    @DisplayName("Speed-Test")
    public void testZugGeneratorSpeed() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            moveGenerator.generateAllPossibleMoves(Color.BLUE);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Zuggenerierungsdauer: " + duration + " Millisekunden");

        // Annahme: Eine "akzeptable" Zeit wird hier definiert, je nach Komplexität der Stellung
        // Beispiel: Maximal 1 Sekunde
        assertTrue(duration <= 1000);
    }
    
    @Test
    public void testMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Annahme: Eine große Anzahl von Zügen wird generiert, um den Speicherbedarf zu erhöhen
        for (int i = 0; i < 1000; i++) {
            moveGenerator.generateAllPossibleMoves(Color.BLUE);
        }

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long usedMemory = memoryAfter - memoryBefore;

        System.out.println("Speicherbedarf: " + usedMemory + " Bytes");

        // Annahme: Ein "akzeptabler" Speicherbedarf wird hier definiert, je nach Systemressourcen
        // Beispiel: Maximal 10 Megabyte
        assertTrue(usedMemory <= 10 * 1024 * 1024);
    }

}