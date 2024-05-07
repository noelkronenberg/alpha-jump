package game;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.*;


public class MoveGeneratorTest {

    static MoveGenerator moveGenerator;

    @BeforeAll
    public static void init() {
        moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();
    }

    @Test
    @DisplayName("example")
    public void example() {
        // TBI
    }

    @Test
    @DisplayName("Speed-Test")
    public void testMoveGeneratorSpeed() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            moveGenerator.generateAllPossibleMoves(Color.BLUE);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Time to generate moves: " + duration + " milliseconds");

        // Annahme: Eine "akzeptable" Zeit wird hier definiert, je nach Komplexität der Stellung
        // Beispiel: Maximal 1 Sekunde
        assertTrue(duration <= 1000);
    }
    
    @Test
    @DisplayName("Memory-Test")
    public void testMoveGeneratorMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Annahme: Eine große Anzahl von Zügen wird generiert, um den Speicherbedarf zu erhöhen
        for (int i = 0; i < 1000; i++) {
            moveGenerator.generateAllPossibleMoves(Color.BLUE);
        }

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long usedMemory = memoryAfter - memoryBefore;

        System.out.println("Memory usage: " + usedMemory + " bytes");

        // Annahme: Ein "akzeptabler" Speicherbedarf wird hier definiert, je nach Systemressourcen
        // Beispiel: Maximal 10 Megabyte
        assertTrue(usedMemory <= 10 * 1024 * 1024);
    }

}