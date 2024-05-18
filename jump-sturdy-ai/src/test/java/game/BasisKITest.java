package game;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import search.BasisKI;

import static org.junit.jupiter.api.Assertions.*;

public class BasisKITest {

    static BasisKI ki;

    @BeforeAll
    public static void init() {
        ki = new BasisKI();
    }

    @Test
    @DisplayName("Gruppe H")
    public void testGruppeH() {
        String bestMoveKI = ki.orchestrator("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b");
        String actualBestMove = "A6-A7";
        assertEquals(bestMoveKI, actualBestMove);
    }

}
