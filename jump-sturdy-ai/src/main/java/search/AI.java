package search;

/**
 * Interface for defining an AI.
 */
public abstract class AI {

    /**
     * Orchestrates the AI on the given FEN position and search configuration.
     *
     * @param fen The current position in FEN notation
     * @param config The configuration object specifying the AI search parameters
     * @return The best move determined by the search algorithm
     */
    public abstract String orchestrator(String fen, SearchConfig config);

    /**
     * Returns the configuration of the AI.
     *
     * @return The configuration object
     */
    protected abstract String showConfig();

    @Override
    public String toString() {
        return "Class: " + this.getClass().getSimpleName() +
                "\nConfig: " +
                showConfig();
    }
}