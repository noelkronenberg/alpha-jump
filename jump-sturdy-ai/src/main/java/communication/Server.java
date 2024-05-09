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
                    System.out.println("Client 1 says: " + messageFromClient1);

                    // convert to internal representation
                    String[] positions_string = messageFromClient1.split("-");
                    String start_string = positions_string[0];
                    String end_string = positions_string[1];
                    int start = moveGenerator.revertPosRowColToIntForServer(start_string);
                    int end =  moveGenerator.revertPosRowColToIntForServer(end_string);

                    moveGenerator.movePiece(start, end, moveGenerator.getPieceAtPosition(start), Color.BLUE);

                    moveGenerator.printBoard();

                    if (moveGenerator.isGameOver(messageFromClient1, Color.BLUE)) {
                        gameEnded = true;
                        System.out.println("BLUE won!");
                        continue;
                    }

                    // TBI: board to FEN

                    outToClient_02.println(messageFromClient1);
                }

                String messageFromClient2 = inFromClient_02.readLine();
                if (messageFromClient2 != null) {
                    System.out.println("Client 2 says: " + messageFromClient2);

                    // convert to internal representation
                    String[] positions_string = messageFromClient2.split("-");
                    String start_string = positions_string[0];
                    String end_string = positions_string[1];
                    int start = moveGenerator.revertPosRowColToIntForServer(start_string);
                    int end =  moveGenerator.revertPosRowColToIntForServer(end_string);

                    moveGenerator.movePiece(start, end, moveGenerator.getPieceAtPosition(start), Color.RED);

                    moveGenerator.printBoard();

                    if (moveGenerator.isGameOver(messageFromClient2, Color.RED)) {
                        gameEnded = true;
                        System.out.println("RED won!");
                        continue;
                    }

                    // TBI: board to FEN

                    outToClient_01.println(messageFromClient2);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
