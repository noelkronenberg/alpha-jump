package search;

import game.Color;
import game.MoveGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluationTest {

    static Evaluation evaluator;
    static MoveGenerator moveGenerator;

    @BeforeAll
    public static void init() {
        evaluator = new Evaluation();
        moveGenerator = new MoveGenerator();
    }

    private void testOrderMovesFirst(String fen, int expectedFirst) {
        init();

        // get moves
        LinkedHashMap<Integer, List<Integer>> movesMap = moveGenerator.getMovesWrapper(fen);
        LinkedList<Integer> movesList =  Evaluation.convertMovesToList(movesMap);

        // order moves
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = moveGenerator.getColor(color_fen);
        Evaluation.orderMoves(movesList, color);

        assertEquals(movesList.getFirst(), expectedFirst);
    }

    @Test
    @DisplayName("Move Order 1")
    public void testMoveOrder1() {
        testOrderMovesFirst("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b", 5060);
    }

}
