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
    int startLongSearch;
    int endLongSearch;
    double longSearchDuration;

    /**
     * Constructs a new {@code ConnectionSimulationConfig} with the specified parameters.
     *
     * @param weightParameterNormal the weight parameter for normal moves
     * @param weightParameterStart the weight parameter at the start of the simulation
     * @param weightParameterEndTime the weight parameter at the end time of the simulation
     * @param numberOfMovesNormal the number of normal moves in the simulation
     * @param numberOfMovesStart the number of moves at the start of the simulation
     * @param weightParameterFinal the final weight parameter
     * @param activateLongSearch the boolean that activates a search with more time
     * @param startLongSearch the number of moves where the long search begins
     * @param endLongSearch the number of moves where the long search ends
     * @param longSearchDuration the time for each move in the long search
     */
    public ConnectionSimulationConfig(double weightParameterNormal, double weightParameterStart, double weightParameterEndTime, int numberOfMovesNormal, int numberOfMovesStart, double weightParameterFinal,boolean activateLongSearch, int startLongSearch, int endLongSearch, double longSearchDuration) {
        this.weightParameterNormal = weightParameterNormal;
        this.weightParameterStart = weightParameterStart;
        this.weightParameterEndTime = weightParameterEndTime;
        this.weightParameterFinal = weightParameterFinal;
        this.numberOfMovesNormal = numberOfMovesNormal;
        this.numberOfMovesStart = numberOfMovesStart;
        this.activateLongSearch = activateLongSearch;
        this.startLongSearch = startLongSearch;
        this.endLongSearch = endLongSearch;
        this.longSearchDuration = longSearchDuration;
    }

    @Override
    public String toString() {
        return "weightParameterNormal = " + weightParameterNormal +
                " | weightParameterStart = " + weightParameterStart +
                " | weightParameterEndTime = " + weightParameterEndTime +
                " | weightParameterFinal = " + weightParameterFinal +
                " | numberOfMovesNormal = " + numberOfMovesNormal +
                " | numberOfMovesStart = " + numberOfMovesStart +
                " | activateLongSearch = " + activateLongSearch;
    }
}