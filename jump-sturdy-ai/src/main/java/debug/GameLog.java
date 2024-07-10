package debug;

import game.MoveGenerator;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
            ArrayList<String> fileNames = new ArrayList<>();
            fileNames.add("C2-AD-C.txt");
            fileNames.add("C2-C-AD.txt");

            int fileIndex = 0; // CHANGE THIS

            String exportName = fileNames.get(fileIndex).substring(0, fileNames.get(fileIndex).length() - 4);
            PrintStream fileOut = new PrintStream(new File("src/main/java/debug/output/" + exportName + "_GameLog-output.txt"));
            System.setOut(fileOut);

            MoveGenerator moveGenerator = new MoveGenerator();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            DefaultCategoryDataset datasetTimeMove = new DefaultCategoryDataset();
            DefaultCategoryDataset datasetTimeLeft = new DefaultCategoryDataset();

            try (InputStream inputStream = classloader.getResourceAsStream(fileNames.get(fileIndex));
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
                                    datasetTimeMove.addValue(moveTime, redPlayer, String.valueOf(moveCountRed));
                                    datasetTimeLeft.addValue(time, redPlayer, String.valueOf(moveCountRed));
                                    moveCountRed++;
                                } else {
                                    moveTime = prevBlueTime - time;
                                    prevBlueTime = time;
                                    indicator = moveParts[0] + " " + moveParts[1] + " (" + bluePlayer + ") " + moveParts[2];
                                    datasetTimeMove.addValue(moveTime, bluePlayer, String.valueOf(moveCountBlue));
                                    datasetTimeLeft.addValue(time, bluePlayer, String.valueOf(moveCountRed));
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

                plotChart("Time Used / Move", "Move Number", "Time (ms)", datasetTimeMove,  exportName + "_time-used");
                plotChart("Time Left / Move", "Move Number", "Time Left (ms)", datasetTimeLeft, exportName + "_time-left");

            } catch (IOException e) {
                e.printStackTrace();
            }
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a line chart with specified parameters and displays it in a JFrame.
     * It also exports the results.
     * Reference: https://www.javatpoint.com/jfreechart-line-chart
     *
     * @param chartTitle The title of the chart.
     * @param xAxisLabel The label for the x-axis.
     * @param yAxisLabel The label for the y-axis.
     * @param dataset The dataset containing the chart data.
     * @param fileName The name of the output file.
     */
    private static void plotChart(String chartTitle, String xAxisLabel, String yAxisLabel,
                                           CategoryDataset dataset, String fileName) {

        Color backgroundPaint =  Color.WHITE;
        Color gridLinePaint = Color.GRAY;

        SwingUtilities.invokeLater(() -> {
            JFreeChart lineChart = ChartFactory.createLineChart(
                    chartTitle, xAxisLabel, yAxisLabel,
                    dataset, PlotOrientation.VERTICAL, true, true, false);

            CategoryPlot plot = lineChart.getCategoryPlot();
            plot.setBackgroundPaint(backgroundPaint);
            plot.setRangeGridlinePaint(gridLinePaint);

            ChartPanel chartPanel = new ChartPanel(lineChart);
            chartPanel.setPreferredSize(new Dimension(1000, 500));

            JFrame frame = new JFrame("Game Log Analysis");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(chartPanel);
            frame.pack();
            frame.setVisible(true);

            // export
            File outputFile = new File("src/main/java/debug/output/", fileName + "-output.png");
            try {
                BufferedImage image = lineChart.createBufferedImage(1000, 500);
                ImageIO.write(image, "png", outputFile) ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
