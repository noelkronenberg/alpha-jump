package search.optimisation;

import game.Color;
import game.MoveGenerator;
import search.SearchConfig;
import search.ab.Minimax_AB;
import search.ab.Evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Implementation of a Genetic Algorithm to optimize weights for evaluating game states in a specific game scenario.
 * References: https://direct.mit.edu/books/book/4675/An-Introduction-to-Genetic-Algorithms, https://stackoverflow.com/a/1575995
 */
public class GeneticAlgorithm {

    // hyperparameter (reference: https://www.baeldung.com/java-genetic-algorithm)
    private static final int POPULATION_SIZE = 20;
    private static final int GENERATIONS = 10;
    private static final double MUTATION_RATE = 0.1;
    private static final double CROSSOVER_RATE = 0.75;
    private static final int ELITISM_COUNT = 2;

    private static Random random = new Random();

    /**
     * Represents an individual in the genetic algorithm population,
     * with specific weights for evaluating game states.
     */
    public static class Individual {
        double possibleMovesWeight;
        double protectedPiecesWeight;
        double doubleWeight;
        double mixedWeight;
        double closenessWeight;
        double fitness;

        /**
         * Constructs an individual with specific weights.
         *
         * @param possibleMovesWeight Weight for possible moves evaluation.
         * @param protectedPiecesWeight Weight for protected pieces evaluation.
         * @param doubleWeight Weight for double pieces evaluation.
         * @param mixedWeight Weight for mixed pieces evaluation.
         * @param closenessWeight Weight for closeness to winning row evaluation.
         */
        Individual(double possibleMovesWeight, double protectedPiecesWeight, double doubleWeight, double mixedWeight, double closenessWeight) {
            this.possibleMovesWeight = possibleMovesWeight;
            this.protectedPiecesWeight = protectedPiecesWeight;
            this.doubleWeight = doubleWeight;
            this.mixedWeight = mixedWeight;
            this.closenessWeight = closenessWeight;
        }

        /**
         * Evaluates the fitness of the individual by playing a simulated full game and calculating a score.
         */
        void evaluateFitness() {
            Evaluation.possibleMovesWeight = this.possibleMovesWeight;
            Evaluation.protectedPiecesWeight = this.protectedPiecesWeight;
            Evaluation.doubleWeight = this.doubleWeight;
            Evaluation.mixedWeight = this.mixedWeight;
            Evaluation.closenessWeight = this.closenessWeight;
            this.fitness = playGame();
        }

        /**
         * Generates a random individual with random weights.
         *
         * @return A new randomly generated individual.
         */
        static Individual generateRandomIndividual() {
            return new Individual(random.nextDouble()*5, random.nextDouble()*5, random.nextDouble()*5, random.nextDouble()*5, random.nextDouble()*5);
        }

        /**
         * Provides a string representation of the individual, including its weights and fitness.
         *
         * @return String representation of the individual.
         */
        @Override
        public String toString() {
            return String.format("possibleMovesWeight: %.5f | protectedPiecesWeight: %.5f | doubleWeight: %.5f | mixedWeight: %.5f | closenessWeight: %.5f | fitness: %.5f",
                    possibleMovesWeight, protectedPiecesWeight, doubleWeight, mixedWeight, closenessWeight, fitness);
        }
    }

    /**
     * Simulates playing a full game using a specific set of weights for evaluation.
     *
     * @return The score achieved in the simulated game.
     */
    public static double playGame() {
        Minimax_AB ai = new Minimax_AB();
        SearchConfig config = Minimax_AB.bestConfig.copy();
        config.timeLimit = 1000; // reduce time for speed
        String bestMove;

        String fen = "2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b";
        Color currentColor = Color.BLUE;
        MoveGenerator gameState = new MoveGenerator();
        gameState.initializeBoard(fen);

        boolean gameOver = false;
        int moveCount = 0;

        while (!gameOver) {
            bestMove = ai.orchestrator(fen, config); // get best move

            // convert move
            int[] bestMoveInts = gameState.convertStringToPosWrapper(bestMove);
            int bestMoveInt = bestMoveInts[0] * 100 + bestMoveInts[1];

            // check for game over
            char currentColorChar = fen.charAt(fen.length() - 1);
            currentColor = (currentColorChar == 'r') ? Color.RED : Color.BLUE;
            gameOver = gameState.isGameOver(bestMove, currentColor);

            if (!gameOver) {
                // move piece
                gameState.movePiece(bestMoveInt);
                moveCount++;
            }

            /*
            // show status
            System.out.println("Color: " + currentColor);
            System.out.println("Move: " + bestMove);
            System.out.println("MoveCount: " + moveCount);
            System.out.println("GameOver: " + gameOver);
            gameState.printBoard(true);
            */

            // get next FEN
            char nextColor = (currentColorChar == 'r') ? 'b' : 'r'; // switch color
            fen = gameState.getFenFromBoard() + " " + nextColor;
        }

        // START: score

        double score = 0;

        // check if won
        if (currentColor != Color.BLUE) {
            score += 1000;
        }
        score -= moveCount; // "get" points for fewer moves

        /*
        System.out.println();
        System.out.println("Score: " + score); // show status
        */

        // END: score

        return score;
    }

    /**
     * Performs crossover operation between two parent individuals.
     *
     * @param parent1 First parent individual.
     * @param parent2 Second parent individual.
     * @return Array of two new individuals resulting from crossover.
     */
    private static Individual[] crossover(Individual parent1, Individual parent2) {
        // crossover with given probability (CROSSOVER_RATE)
        if (random.nextDouble() < CROSSOVER_RATE) {

            // new weights as average of parent weights
            double newPossibleMovesWeight = (parent1.possibleMovesWeight + parent2.possibleMovesWeight) / 2;
            double newProtectedPiecesWeight = (parent1.protectedPiecesWeight + parent2.protectedPiecesWeight) / 2;
            double newDoubleWeight = (parent1.doubleWeight + parent2.doubleWeight) / 2;
            double newMixedWeight = (parent1.mixedWeight + parent2.mixedWeight) / 2;
            double newClosenessWeight = (parent1.closenessWeight + parent2.closenessWeight) / 2;

            // return two new children with crossover weights
            return new Individual[]{
                    new Individual(newPossibleMovesWeight, parent1.protectedPiecesWeight, newDoubleWeight, parent1.mixedWeight, newClosenessWeight),
                    new Individual(parent2.possibleMovesWeight, newProtectedPiecesWeight, parent2.doubleWeight, newMixedWeight, parent2.closenessWeight),
            };
        }

        // return parents if no crossover
        return new Individual[]{parent1, parent2};
    }

    /**
     * Mutates an individual with a given mutation rate.
     *
     * @param individual Individual to be mutated.
     * @return Mutated individual.
     */
    private static Individual mutate(Individual individual) {
        // mutate with given probability (MUTATION_RATE)
        if (random.nextDouble() < MUTATION_RATE) {
            // random change of weights
            individual.possibleMovesWeight += (random.nextDouble() - 0.5);
            individual.protectedPiecesWeight += (random.nextDouble() - 0.5);
            individual.doubleWeight += (random.nextDouble() - 0.5);
            individual.mixedWeight += (random.nextDouble() - 0.5);
            individual.closenessWeight += (random.nextDouble() - 0.5);
        }
        return individual;
    }

    /**
     * Main method that executes the Genetic Algorithm to optimize weights.
     *
     * Outputs the best individual found after a number of generations to a text file.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            PrintStream fileOut = new PrintStream(new File("src/main/java/search/optimisation/GA-Debugger-output.txt"));
            System.setOut(fileOut);

            List<Individual> population = new ArrayList<>();

            for (int i = 0; i < POPULATION_SIZE; i++) {
                Individual individual = Individual.generateRandomIndividual();
                individual.evaluateFitness();
                population.add(individual);
            }

            for (int generation = 1; generation <= GENERATIONS; generation++) {
                Collections.sort(population, Comparator.comparingDouble(individual -> -individual.fitness)); // put fittest individuals first

                List<Individual> newPopulation = new ArrayList<>();

                // keep the top (ELITISM_COUNT) individuals
                for (int i = 0; i < ELITISM_COUNT; i++) {
                    newPopulation.add(population.get(i));
                }

                // use top (after ELITISM_COUNT) half as parents
                for (int i = ELITISM_COUNT; i < POPULATION_SIZE / 2; i++) {
                    Individual parent1 = population.get(i); // get (next) fittest individual
                    Individual parent2 = population.get(random.nextInt(POPULATION_SIZE / 2)); // get random other individual

                    Individual[] children = crossover(parent1, parent2); // crossover selected parents

                    // add new (mutated) children to (new) population
                    newPopulation.add(mutate(children[0]));
                    newPopulation.add(mutate(children[1]));
                }

                // evaluate fitness of new population
                for (Individual individual : newPopulation) {
                    individual.evaluateFitness();
                }

                population = newPopulation;

                // show the fittest individual of current generation
                Individual bestIndividual = population.get(0);
                System.out.println();
                System.out.printf("Generation %d - %s\n", generation, bestIndividual);
            }

            // show the overal fittest individual
            Individual bestIndividual = population.get(0);
            System.out.println();
            System.out.printf("Optimized parameters: %s\n", bestIndividual);

            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}