package org.chanaka;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final int PORT = 3000;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);


    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        logger.info("Server started on port: " + PORT);

        while (!serverSocket.isClosed()) {
            logger.info("Waiting for client connection...");
            Socket clientSocket = serverSocket.accept();
            logger.info("New client has Connected..");
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            executorService.submit(clientHandler);
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }
}
