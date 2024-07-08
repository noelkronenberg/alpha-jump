package search;

import game.MoveGenerator;

import java.util.LinkedHashMap;
import java.util.List;

public class RandomAI extends AI {

    private MoveGenerator moveGenerator = new MoveGenerator();
    private long totalTimeNano = 0;
    private int moveCount = 0;

    @Override
    public String orchestrator(String fen, SearchConfig config) {
        long startTime = System.nanoTime();

        LinkedHashMap<Integer, List<Integer>> moves = moveGenerator.getMovesWrapper(fen);
        String move = moveGenerator.getRandomMove(moves);

        long endTime = System.nanoTime();
        long durationNano = endTime - startTime;

        totalTimeNano += durationNano;
        moveCount++;

        return move;
    }

    @Override
    protected String showConfig() {
        double averageTimeMillis = (moveCount > 0) ? (totalTimeNano / 1000000.0 / moveCount) : 0;
        return String.format("random moves | average time to generate move = %.2f ms", averageTimeMillis);
    }
}
