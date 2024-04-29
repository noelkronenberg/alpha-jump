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
    public void testLeeresSpielfeld() {
        String notation = "6/8/8/8/8/8/8/6";
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

    @Test
    public void testSpielfeldMitSpielsteinen() {
        String notation = "r0r0r0r0r0r0/1r0r0r0r0r0r01/r07/8/8/7b0/1b0b0b0b0b0b01/b0b0b0b0b0b0";
        String expectedOutput = "- r r r r r r -\n" +
                                ". r r r r r r .\n" +
                                "r . . . . . . .\n" +
                                ". . . . . . . .\n" +
                                ". . . . . . . .\n" +
                                ". . . . . . . b\n" +
                                ". b b b b b b .\n" +
                                "- b b b b b b -\n";
        assertEquals(expectedOutput, printAndGetOutput(notation));
    }

    @Test
    public void testUnvollstaendigeSpielfeldNotation() {
        String notation = "r0r0r0r0r0r0/1r0r0r0r0r0r01/8/8/8/8/1b0b0b0b0b0b01";
        String expectedOutput = "- r r r r r r -\n" +
                                ". r r r r r r .\n" +
                                ". . . . . . . .\n" +
                                ". . . . . . . .\n" +
                                ". . . . . . . .\n" +
                                ". . . . . . . .\n" +
                                ". b b b b b b .\n";
        assertEquals(expectedOutput, printAndGetOutput(notation));
    }

}