package benchmark.simulation;

public class ConnectionSimulationConfig {
    double gewichtungsParameterNormal;
    double gewichtungsParameterStart;
    double gewichtungsParameterEndZeit;
    double gewichtungsParameterFinal;
    int numberOfMovesNormal;
    int numberOfMovesStart;
    public ConnectionSimulationConfig(double gewichtungsParameterNormal,double gewichtungsParameterStart, double gewichtungsParameterEndZeit, int numberOfMovesNormal, int numberOfMovesStart, double gewichtungsParameterFinal){
        this.gewichtungsParameterNormal=gewichtungsParameterNormal;
        this.gewichtungsParameterStart=gewichtungsParameterStart;
        this.gewichtungsParameterEndZeit=gewichtungsParameterEndZeit;
        this.gewichtungsParameterFinal=gewichtungsParameterFinal;
        this.numberOfMovesNormal=numberOfMovesNormal;
        this.numberOfMovesStart=numberOfMovesStart;
    }
}
