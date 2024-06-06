package communication;

import com.google.gson.Gson;

import org.json.JSONException;
import search.BasisKI;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class Connection {

    int player;

    public static JSONObject toJSON(String move) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("move", move);
        return jsonObject;
    }

    public void connect() {
        this.player = player;
        BasisKI ki = new BasisKI();
        String serverAddress = "localhost";
        int port = 5555;

        try (Socket server = new Socket(serverAddress, port)) {

            PrintWriter output = new PrintWriter(server.getOutputStream(), true);

            InputStream inputStream = server.getInputStream();

            int zwischen = (int) inputStream.read();
            if (zwischen==48){
                this.player=1;
                System.out.println("You are Player 1");
            }
            else {
                this.player=2;
                System.out.println("You are Player 2");
            }

            //System.out.println("Player "+ this.player + " | " + "Connected to the server.");
            
            Gson gson = new Gson();

            while (true) {
                // send request to get game status
                output.println("\"get\"");

                // wait for server response
                //Thread.currentThread().sleep(500);

                // get server response
                byte[] data = new byte[9999];
                int bytesRead = inputStream.read(data);
                JSONObject response;

                if (bytesRead != -1) {
                    // convert response
                    String jsonString;
                    if (data[0]==49||data[0]==48){          //check whos turn it is (0 or 1)
                         jsonString = new String(data, 1, bytesRead);
                    }
                    else {
                         jsonString = new String(data, 0, bytesRead);
                    }
                    
                    try {
                        response = new JSONObject(jsonString);
                    } catch (JSONException e) {
                        //System.out.println("\n" + "Player "+ this.player + " | " + "Error parsing JSON: " + jsonString);
                        continue;
                    }

                    //System.out.println("\n" + "Player "+ this.player + " | " + "Server response:  " + response);

                    // process server response
                    if (response.getBoolean("bothConnected")) {

                        String fen = response.getString("board");
                        //System.out.println("\n" + "Player "+ this.player + " | " + "Current board: ");
                        //System.out.println(fen);

                        // player turns
                        if (response.getBoolean("player1") && this.player == 1) {
                            String move = ki.orchestrator(fen, 20000.0, 0.25);
                            output.println(gson.toJson(move));
                            System.out.print("\n" + "Player 1 move: " + move);

                        } else if (response.getBoolean("player2") && this.player == 2) {
                            String move = ki.orchestrator(fen, 20000.0, 0.25);
                            output.println(gson.toJson(move));
                            System.out.println("\n" + "Player 2 move: " + move);
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Player "+ this.player + " | " +  "Error occurred while connecting to the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Connection player1 = new Connection();
        player1.connect();                              //add this
        //Connection player2 = new Connection();          //Delete this

        //Thread thread1 = new Thread(() -> player1.connect()); //Delete this
        //Thread thread2 = new Thread(() -> player2.connect()); //Delete this

//        thread1.start();                    //Delete this
//        Thread.sleep(100);            //Delete this
//        thread2.start();                    //Delete this
//
//        try {
//            thread1.join();                 //Delete this
//            thread2.join();                 //Delete this
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
