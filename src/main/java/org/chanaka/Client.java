package org.chanaka;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private static final int PORT = 3000;
    private static final String HOST = "localhost";
    private final String username;
    private final Socket clientSocket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public Client(Socket clientSocket, String username) throws IOException {
        this.username = username;
        this.clientSocket = clientSocket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    public void listenForMessage() {
        new Thread(() -> {
            try {
                while (!clientSocket.isClosed()) {
                    String message = in.readLine();
                    System.out.println(message);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error reading from server", e);
            } finally {
                close();
            }
        }).start();
    }

    private void sendMessage() {
        try {
            out.write(username);
            out.newLine();
            out.flush();

            Scanner scn = new Scanner(System.in);
            while (!clientSocket.isClosed()) {
                String message = scn.nextLine();
                out.write(username + ": " + message);
                out.newLine();
                out.flush();
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending the message", e);
        }

    }

    private void close() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing client connection", e);
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username to join the chat:");
        String username = scanner.nextLine();

        Socket clientSocket = new Socket(InetAddress.getByName(HOST), PORT);
        Client client = new Client(clientSocket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
