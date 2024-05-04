package benchmark;

import main.Color;
import main.MoveGenerator;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class MoveGeneratorBM {

    private MoveGenerator moveGenerator;

    @Setup
    public void init() {
        moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 10, time = 1)
    @Fork(value = 1)
    public void generateAllPossibleMovesBenchmark() {
        moveGenerator.generateAllPossibleMoves(Color.WHITE);
    }

}