package org.chanaka;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final String username;
    static List<ClientHandler> clients = new ArrayList<>();

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        username = in.readLine();
        clients.add(this);
    }

    @Override
    public void run() {
        while (!clientSocket.isClosed()) {
            try {
                String message = in.readLine();
                logger.info("Received message from " + username);
                broadcast(message);
            } catch (IOException e) {
                close();
                break;
            }
        }
    }

    public void broadcast(String message) throws IOException {
        for (ClientHandler client : clients) {
            if (!client.username.contentEquals(username)) {
               client.out.write(message);
               client.out.newLine();
               client.out.flush();
            }
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            clients.remove(this);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing client connection", e);
        }
    }
}
