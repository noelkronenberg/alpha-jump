package benchmark.simulation;

/**
 * Configuration for a connection simulation.
 */
public class ConnectionSimulationConfig {
    double weightParameterNormal;
    double weightParameterStart;
    double weightParameterEndTime;
    double weightParameterFinal;
    int numberOfMovesNormal;
    int numberOfMovesStart;
    boolean activateLongSearch;

    /**
     * Constructs a new {@code ConnectionSimulationConfig} with the specified parameters.
     *
     * @param weightParameterNormal the weight parameter for normal moves
     * @param weightParameterStart the weight parameter at the start of the simulation
     * @param weightParameterEndTime the weight parameter at the end time of the simulation
     * @param numberOfMovesNormal the number of normal moves in the simulation
     * @param numberOfMovesStart the number of moves at the start of the simulation
     * @param weightParameterFinal the final weight parameter
     */
    public ConnectionSimulationConfig(double weightParameterNormal, double weightParameterStart, double weightParameterEndTime, int numberOfMovesNormal, int numberOfMovesStart, double weightParameterFinal,boolean activateLongSearch) {
        this.weightParameterNormal = weightParameterNormal;
        this.weightParameterStart = weightParameterStart;
        this.weightParameterEndTime = weightParameterEndTime;
        this.weightParameterFinal = weightParameterFinal;
        this.numberOfMovesNormal = numberOfMovesNormal;
        this.numberOfMovesStart = numberOfMovesStart;
        this.activateLongSearch = activateLongSearch;
    }

    @Override
    public String toString() {
        return "weightParameterNormal = " + weightParameterNormal +
                " | weightParameterStart = " + weightParameterStart +
                " | weightParameterEndTime = " + weightParameterEndTime +
                " | weightParameterFinal = " + weightParameterFinal +
                " | numberOfMovesNormal = " + numberOfMovesNormal +
                " | numberOfMovesStart = " + numberOfMovesStart;
    }
}