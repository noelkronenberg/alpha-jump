package communication;

import game.Color;
import game.MoveGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {

        int port = 9999;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server started and listening on port " + port);

            // connect to clients
            Socket client_01 = server.accept();
            System.out.println("Client 1 connected.");
            Socket client_02 = server.accept();
            System.out.println("Client 2 connected.");

            // input and output streams for each client
            BufferedReader inFromClient_01 = new BufferedReader(new InputStreamReader(client_01.getInputStream()));
            PrintWriter outToClient_01 = new PrintWriter(client_01.getOutputStream(), true);
            BufferedReader inFromClient_02 = new BufferedReader(new InputStreamReader(client_02.getInputStream()));
            PrintWriter outToClient_02 = new PrintWriter(client_02.getOutputStream(), true);

            // start
            MoveGenerator moveGenerator = new MoveGenerator();
            String fen = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
            moveGenerator.initializeBoard(fen);
            outToClient_01.println(fen);
            boolean gameEnded = false;

            // read from one client and send to the other (continuously)
            while (!gameEnded) {
                String messageFromClient1 = inFromClient_01.readLine();
                if (messageFromClient1 != null) {
                    System.out.println("Player 1 (BLUE) says: " + messageFromClient1);

                    int[] moves = moveGenerator.convertStringToPosWrapper(messageFromClient1); // convert to internal representation
                    moveGenerator.movePiece(moves[0], moves[1]);

                    moveGenerator.printBoard(true);

                    if (moveGenerator.isGameOver(messageFromClient1, Color.RED)) {
                        gameEnded = true;
                        System.out.println("BLUE won!");
                        outToClient_01.println("GAME OVER");
                        outToClient_02.println("GAME OVER");
                        continue;
                    }

                    String new_fen = moveGenerator.getFenFromBoard(); // convert to FEN
                    String new_fen_with_color = new_fen + " r";

                    outToClient_02.println(new_fen_with_color);
                }

                String messageFromClient2 = inFromClient_02.readLine();
                if (messageFromClient2 != null) {
                    System.out.println("Player 2 (RED) says: " + messageFromClient2);

                    int[] moves = moveGenerator.convertStringToPosWrapper(messageFromClient2); // convert to internal representation
                    moveGenerator.movePiece(moves[0], moves[1]);

                    moveGenerator.printBoard(true);

                    if (moveGenerator.isGameOver(messageFromClient2, Color.BLUE)) {
                        gameEnded = true;
                        System.out.println("RED won!");
                        outToClient_01.println("GAME OVER");
                        outToClient_02.println("GAME OVER");
                        continue;
                    }

                    String new_fen = moveGenerator.getFenFromBoard(); // convert to FEN
                    String new_fen_with_color = new_fen + " b";

                    outToClient_01.println(new_fen_with_color);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
