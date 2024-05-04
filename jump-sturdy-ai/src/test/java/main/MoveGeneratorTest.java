package main;
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

}