package search;

/**
 * Interface for defining an AI.
 */
public interface KI {

    /**
     * Orchestrates the AI on the given FEN position and search configuration.
     *
     * @param fen The current position in FEN notation
     * @param config The configuration object specifying the AI search parameters
     * @return The best move determined by the search algorithm
     */
    String orchestrator(String fen, SearchConfig config);
}