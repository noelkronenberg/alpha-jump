package search;

import game.Color;
import game.MoveGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

// references: https://direct.mit.edu/books/book/4675/An-Introduction-to-Genetic-Algorithms, https://stackoverflow.com/a/1575995
public class GeneticAlgorithm {

    // hyperparameter (reference: https://www.baeldung.com/java-genetic-algorithm)
    private static final int POPULATION_SIZE = 20;
    private static final int GENERATIONS = 50;
    private static final double MUTATION_RATE = 0.1;
    private static final double CROSSOVER_RATE = 0.75;

    private static Random random = new Random();

    public static class Individual {
        double possibleMovesWeight;
        double protectedPiecesWeight;
        double doubleWeight;
        double mixedWeight;
        double closenessWeight;
        double fitness;

        Individual(double possibleMovesWeight, double protectedPiecesWeight, double doubleWeight, double mixedWeight, double closenessWeight) {
            this.possibleMovesWeight = possibleMovesWeight;
            this.protectedPiecesWeight = protectedPiecesWeight;
            this.doubleWeight = doubleWeight;
            this.mixedWeight = mixedWeight;
            this.closenessWeight = closenessWeight;
        }

        void evaluateFitness() {
            Evaluation.possibleMovesWeight = this.possibleMovesWeight;
            Evaluation.protectedPiecesWeight = this.protectedPiecesWeight;
            Evaluation.doubleWeight = this.doubleWeight;
            Evaluation.mixedWeight = this.mixedWeight;
            Evaluation.closenessWeight = this.closenessWeight;
            this.fitness = playGame();
        }

        static Individual generateRandomIndividual() {
            return new Individual(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
        }

        @Override
        public String toString() {
            return String.format("possibleMovesWeight: %.5f | protectedPiecesWeight: %.5f | doubleWeight: %.5f | mixedWeight: %.5f | closenessWeight: %.5f | fitness: %.5f",
                    possibleMovesWeight, protectedPiecesWeight, doubleWeight, mixedWeight, closenessWeight, fitness);
        }
    }

    public static double playGame() {
        BasisKI ki = new BasisKI();
        BasisKI.bestConfig.timeLimit = 1000; // reduce time for speed
        String bestMove;

        String fen = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
        Color currentColor = Color.BLUE;
        MoveGenerator gameState = new MoveGenerator();
        gameState.initializeBoard(fen);

        boolean gameOver = false;
        int moveCount = 0;

        while (!gameOver) {
            bestMove = ki.orchestrator(fen,  BasisKI.bestConfig); // get best move

            // convert move
            int[] bestMoveInts = gameState.convertStringToPosWrapper(bestMove);
            int bestMoveInt = bestMoveInts[0] * 100 + bestMoveInts[1];

            // move piece
            gameState.movePiece(bestMoveInt);
            moveCount++;

            // check for game over
            char currentColorChar = fen.charAt(fen.length() - 1);
            currentColor = (currentColorChar == 'r') ? Color.RED : Color.BLUE;
            gameOver = gameState.isGameOver(bestMove, currentColor);

            // show status
            System.out.println("Color: " + currentColor);
            System.out.println("Move: " + bestMove);
            System.out.println("MoveCount: " + moveCount);
            System.out.println("GameOver: " + gameOver);
            gameState.printBoard(true);

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

        System.out.println();
        System.out.println("Score: " + score); // show status

        // END: score

        return score;
    }

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

    public static void main(String[] args) {
        List<Individual> population = new ArrayList<>();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            Individual individual = Individual.generateRandomIndividual();
            individual.evaluateFitness();
            population.add(individual);
        }

        for (int generation = 1; generation <= GENERATIONS; generation++) {
            Collections.sort(population, Comparator.comparingDouble(individual -> -individual.fitness)); // put fittest individuals first

            List<Individual> newPopulation = new ArrayList<>();

            // use top half as parents
            for (int i = 0; i < POPULATION_SIZE / 2; i++) {
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
            System.out.printf("Generation %d - %s\n", generation, bestIndividual);
        }

        // show the overal fittest individual
        Individual bestIndividual = population.get(0);
        System.out.println();
        System.out.printf("Optimized parameters: %s\n", bestIndividual);
    }
}