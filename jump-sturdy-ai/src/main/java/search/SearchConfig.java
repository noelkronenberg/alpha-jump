package search;

public class SearchConfig {
    public boolean timeCriterion;
    public double timeLimit;
    public boolean aspirationWindow;
    public double aspirationWindowSize;
    public boolean transpositionTables;
    public int maxAllowedDepth;
    public boolean dynamicTime;
    public boolean useQuiescenceSearch;

    public SearchConfig(boolean timeCriterion, double timeLimit, boolean aspirationWindow, double aspirationWindowSize, boolean transpositionTables, int maxAllowedDepth, boolean dynamicTime, boolean useQuiescenceSearch) {
        this.timeCriterion = timeCriterion;
        this.timeLimit = timeLimit;
        this.aspirationWindow = aspirationWindow;
        this.aspirationWindowSize = aspirationWindowSize;
        this.transpositionTables = transpositionTables;
        this.maxAllowedDepth = maxAllowedDepth;
        this.dynamicTime = dynamicTime;
        this.useQuiescenceSearch = useQuiescenceSearch;
    }
}