package debug;

import game.MoveGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;

/**
 * Reads a log file containing game logs and outputs a visual representation to a text file.
 */
public class GameLog {
    /**
     * Main method to execute the game log processing.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            PrintStream fileOut = new PrintStream(new File("src/main/java/debug/GameLog-output.txt"));
            System.setOut(fileOut);

            String fileName = "C2-AD-C.txt"; // CHANGE THIS

            MoveGenerator moveGenerator = new MoveGenerator();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            DefaultCategoryDataset dataset = new DefaultCategoryDataset(); // Dataset for plotting

            try (InputStream inputStream = classloader.getResourceAsStream(fileName);
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                String redPlayer = "";
                String bluePlayer = "";

                long prevRedTime = 120000;
                long prevBlueTime = 120000;

                int moveCountRed = 0;
                int moveCountBlue = 0;

                String line;
                while ((line = br.readLine()) != null) {

                    if (line.startsWith("Spieler Rot:")) {
                        redPlayer = line.substring("Spieler Rot: ".length());
                    } else if (line.startsWith("Spieler Blau:")) {
                        bluePlayer = line.substring("Spieler Blau: ".length());
                    }

                        boolean redMove = line.startsWith("Red move:");
                        boolean blueMove = line.startsWith("Blue move:");

                        if (redMove || blueMove) {
                            String[] moveParts = line.split("\\s+");
                            if (moveParts.length >= 3) {
                                String indicator;
                                long time = Integer.parseInt(moveParts[6]);
                                long moveTime;

                                if (redMove) {
                                    moveTime = prevRedTime - time;
                                    prevRedTime = time;
                                    indicator = moveParts[0] + " " + moveParts[1] + " (" + redPlayer + ") " + moveParts[2];
                                    dataset.addValue(moveTime, "Red", String.valueOf(moveCountRed));
                                    moveCountRed++;
                                } else {
                                    moveTime = prevBlueTime - time;
                                    prevBlueTime = time;
                                    indicator = moveParts[0] + " " + moveParts[1] + " (" + bluePlayer + ") " + moveParts[2];
                                    dataset.addValue(moveTime, "Blue", String.valueOf(moveCountBlue));
                                    moveCountBlue++;
                                }

                                System.out.println(indicator + " (" + time + " | " + moveTime + ")");
                                System.out.println();
                            }
                        }

                        if (line.contains("/")) {
                            String fen = line.trim();
                            moveGenerator.initializeBoard(fen.substring(0, fen.length() - 1));
                            moveGenerator.printBoard(true);
                        }

                }

                // plotting (reference: https://www.javatpoint.com/jfreechart-line-chart)
                SwingUtilities.invokeLater(() -> {
                    JFreeChart lineChart = ChartFactory.createLineChart(
                            "Time Used / Move", "Move Number", "Time (ms)",
                            dataset, PlotOrientation.VERTICAL, false, true, false);
                    ChartPanel chartPanel = new ChartPanel(lineChart);
                    chartPanel.setPreferredSize(new Dimension(1000, 500));
                    JFrame frame = new JFrame("Game Log Analysis");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setContentPane(chartPanel);
                    frame.pack();
                    frame.setVisible(true);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
