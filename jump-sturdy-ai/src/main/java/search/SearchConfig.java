package search;

/**
 * Configuration class for defining search algorithm parameters.
 */
public class SearchConfig {
    public boolean timeCriterion;
    public double timeLimit;
    public boolean aspirationWindow;
    public double aspirationWindowSize;
    public boolean transpositionTables;
    public int maxAllowedDepth;
    public boolean dynamicTime;
    public boolean useQuiescenceSearch;

    /**
     * Constructor to initialize all configuration parameters.
     *
     * @param timeCriterion Whether time-based criteria are used
     * @param timeLimit Maximum time limit for the search
     * @param aspirationWindow Whether aspiration window is used
     * @param aspirationWindowSize Size of the aspiration window
     * @param transpositionTables Whether transposition tables are used
     * @param maxAllowedDepth Maximum allowed depth for search (in case timeCriterion is not used)
     * @param dynamicTime Whether dynamic time management is used
     * @param useQuiescenceSearch Whether quiescence search is used
     */
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

    /**
     * Method to create a copy of this configuration.
     *
     * @return A new SearchConfig instance with the same values as this one.
     */
    public SearchConfig copy() {
        return new SearchConfig(timeCriterion, timeLimit, aspirationWindow, aspirationWindowSize, transpositionTables, maxAllowedDepth, dynamicTime, useQuiescenceSearch);
    }
}