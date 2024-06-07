package communication;

import game.MoveGenerator;
import search.BasisKI;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Connection {

    int player;
    String lastBoard = "";

    public void connect() {
        BasisKI ki = new BasisKI();
        String serverAddress = "localhost";
        int port = 5555;

        try (Socket server = new Socket(serverAddress, port)) {

            PrintWriter outputStream = new PrintWriter(server.getOutputStream(), true);
            InputStream inputStream = server.getInputStream();

            int temp = (int) inputStream.read();
            if (temp == 48){
                this.player = 1;
                System.out.println("\n" + "You are Player 1");
            }
            else {
                this.player = 2;
                System.out.println("\n" +  "You are Player 2");
            }

            //System.out.println("\n" + "Player "+ this.player + " | " + "Connected to the server.");
            
            Gson gson = new Gson();

            while (true) {
                // send request to get game status
                outputStream.println("\"get\"");

                // wait for server response
                Thread.sleep(500);

                // get server response
                byte[] data = new byte[9999];
                int bytesRead = inputStream.read(data);
                JSONObject response;

                if (bytesRead != -1) {
                    // convert response
                    String jsonString;
                    if (data[0] == 49 || data[0] == 48){ // check player turn (0 or 1)
                         jsonString = new String(data, 1, bytesRead);
                    }
                    else {
                         jsonString = new String(data, 0, bytesRead);
                    }
                    
                    try {
                        response = new JSONObject(jsonString);
                    } catch (JSONException e) {
                        System.out.println("\n" + "Player "+ this.player + " | " + "Error parsing JSON: " + jsonString);
                        continue;
                    }

                    // System.out.println("\n" + "Player "+ this.player + " | " + "Server response:  " + response);

                    String fen = response.getString("board");

                    // process server response
                    if (response.getBoolean("bothConnected") && !fen.equals(this.lastBoard)) {

                        this.lastBoard = fen;
                        //System.out.println("\n" + "Player "+ this.player + " | " + "Current board: ");
                        //System.out.println(fen);

                        // player turns
                        if (response.getBoolean("player1") && this.player == 1) {
                            String move = ki.orchestrator(fen, BasisKI.bestConfig);
                            outputStream.println(gson.toJson(move));
                            MoveGenerator moveGenerator = new MoveGenerator();
                            moveGenerator.initializeBoard(fen);
                            moveGenerator.printBoard(true);
                            System.out.print("\n" + "Player 1 | Move: " + move);
                            System.out.println();
                        } else if (response.getBoolean("player2") && this.player == 2) {
                            String move = ki.orchestrator(fen, BasisKI.bestConfig);
                            outputStream.println(gson.toJson(move));
                            MoveGenerator moveGenerator = new MoveGenerator();
                            moveGenerator.initializeBoard(fen);
                            moveGenerator.printBoard(true);
                            System.out.println("\n" + "Player 2 | Move: " + move);
                            System.out.println();
                        }
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("\n" + "Player "+ this.player + " | " +  "Error occurred while connecting to the server: " + e.getMessage());
        }
    }

    public void connectPlayer(){
        BasisKI ki = new BasisKI();
        String serverAddress = "localhost";
        int port = 5555;

        try (Socket server = new Socket(serverAddress, port)) {

            PrintWriter outputStream = new PrintWriter(server.getOutputStream(), true);
            InputStream inputStream = server.getInputStream();

            int temp = (int) inputStream.read();
            if (temp == 48){
                this.player = 1;
                System.out.println("\n" + "You are Player 1");
            }
            else {
                this.player = 2;
                System.out.println("\n" +  "You are Player 2");
            }

            //System.out.println("\n" + "Player "+ this.player + " | " + "Connected to the server.");

            Gson gson = new Gson();

            while (true) {
                // send request to get game status
                outputStream.println("\"get\"");

                // wait for server response
                Thread.sleep(500);

                // get server response
                byte[] data = new byte[9999];
                int bytesRead = inputStream.read(data);
                JSONObject response;

                if (bytesRead != -1) {
                    // convert response
                    String jsonString;
                    if (data[0] == 49 || data[0] == 48){ // check player turn (0 or 1)
                        jsonString = new String(data, 1, bytesRead);
                    }
                    else {
                        jsonString = new String(data, 0, bytesRead);
                    }

                    try {
                        response = new JSONObject(jsonString);
                    } catch (JSONException e) {
                        System.out.println("\n" + "Player "+ this.player + " | " + "Error parsing JSON: " + jsonString);
                        continue;
                    }

                    // System.out.println("\n" + "Player "+ this.player + " | " + "Server response:  " + response);

                    String fen = response.getString("board");

                    // process server response
                    if (response.getBoolean("bothConnected") && !fen.equals(this.lastBoard)) {

                        this.lastBoard = fen;
                        //System.out.println("\n" + "Player "+ this.player + " | " + "Current board: ");
                        //System.out.println(fen);

                        // player turns
                        if (response.getBoolean("player1") && this.player == 1) {
                            MoveGenerator moveGenerator = new MoveGenerator();
                            moveGenerator.initializeBoard(fen);
                            moveGenerator.printBoard(true);
                            System.out.println("Please Give Move:");
                            Scanner scanner = new Scanner(System.in);
                            String move = scanner.nextLine();
                            outputStream.println(gson.toJson(move));
                            System.out.print("\n" + "Player 1 | Move: " + move);
                            System.out.println();
                        } else if (response.getBoolean("player2") && this.player == 2) {
                            MoveGenerator moveGenerator = new MoveGenerator();
                            moveGenerator.initializeBoard(fen);
                            moveGenerator.printBoard(true);
                            System.out.println("Please Give Move:");
                            Scanner scanner = new Scanner(System.in);
                            String move = scanner.nextLine();
                            outputStream.println(gson.toJson(move));
                            System.out.print("\n" + "Player 1 | Move: " + move);
                            System.out.println();
                        }
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("\n" + "Player "+ this.player + " | " +  "Error occurred while connecting to the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Connection player1 = new Connection();
        player1.connect(); // only for single player
//        Connection player2 = new Connection();
//
//        Thread thread1 = new Thread(() -> player1.connect());
//        Thread thread2 = new Thread(() -> player2.connect());
//
//        thread1.start();
//        Thread.sleep(100);
//        thread2.start();
//
//        try {
//            thread1.join();
//            thread2.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        // END: two player game
    }
}
