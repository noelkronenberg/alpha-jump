package main;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.*;

public class JumpSturdyTest {

    // Hilfsmethode, um die Ausgabe der Funktion zu erhalten
    private String printAndGetOutput(String notation) {
        // Umleitung des System.out, um die Ausgabe zu erfassen
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        JumpSturdy testGame = new JumpSturdy();
        System.setOut(new PrintStream(outContent));
        
        // Aufruf der Methode mit der Notation
        testGame.printSpielfeld(notation);
        
        // Rückgabe der erfassten Ausgabe
        return outContent.toString();
    }

    @Test
    @DisplayName("example")
    public void testLeeresSpielfeld() {
        String notation = "8/8/8/8/8/8/8/8";
        String expectedOutput = "- . . . . . . -\n" +
                                 ". . . . . . . .\n" +
                                 ". . . . . . . .\n" +
                                 ". . . . . . . .\n" +
                                 ". . . . . . . .\n" +
                                 ". . . . . . . .\n" +
                                 ". . . . . . . .\n" +
                                 "- . . . . . . -\n";
        assertEquals(expectedOutput, printAndGetOutput(notation));
    }

}