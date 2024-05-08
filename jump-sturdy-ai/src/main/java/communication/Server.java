package communication;

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

            // read from one client and send to the other (continuously)
            while (true) {
                String messageFromClient1 = inFromClient_01.readLine();
                if (messageFromClient1 != null) {
                    System.out.println("Client 1 says: " + messageFromClient1);
                    outToClient_02.println(messageFromClient1);
                }

                String messageFromClient2 = inFromClient_02.readLine();
                if (messageFromClient2 != null) {
                    System.out.println("Client 2 says: " + messageFromClient2);
                    outToClient_01.println(messageFromClient2);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
