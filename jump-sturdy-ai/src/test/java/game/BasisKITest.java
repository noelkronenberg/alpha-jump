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

    private void testMoves(String fen, String expectedMove) {
        init();
        assertEquals(ki.orchestrator(fen), expectedMove);
    }

    @Test
    @DisplayName("Gruppe H")
    public void testGruppeH() {
        testMoves("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b", "A6-A7");
    }

}
