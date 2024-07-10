package benchmark.simulation;

public class ConnectionSimulationConfig {
    double weightParameterNormal;
    double weightParameterStart;
    double weightParameterEndTime;
    double weightParameterFinal;
    int numberOfMovesNormal;
    int numberOfMovesStart;

    public ConnectionSimulationConfig(double weightParameterNormal, double weightParameterStart, double weightParameterEndTime, int numberOfMovesNormal, int numberOfMovesStart, double weightParameterFinal) {
        this.weightParameterNormal = weightParameterNormal;
        this.weightParameterStart = weightParameterStart;
        this.weightParameterEndTime = weightParameterEndTime;
        this.weightParameterFinal = weightParameterFinal;
        this.numberOfMovesNormal = numberOfMovesNormal;
        this.numberOfMovesStart = numberOfMovesStart;
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