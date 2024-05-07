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
        double startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            moveGenerator.generateAllPossibleMoves(Color.BLUE);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / 1000) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)

        System.out.println("Time to generate moves: " + duration + " milliseconds");

        // example: max. 1 second
        assertTrue(duration <= 1000);
    }
    
    @Test
    @DisplayName("Memory-Test")
    public void testMoveGeneratorMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        for (int i = 0; i < 1000; i++) {
            moveGenerator.generateAllPossibleMoves(Color.BLUE);
        }

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long usedMemory = (memoryAfter - memoryBefore) / 1000;

        System.out.println("Memory usage: " + usedMemory + " bytes");

        // example: max 10 megabyte
        assertTrue(usedMemory <= 10 * 1024 * 1024);
    }

}