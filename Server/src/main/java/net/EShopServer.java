package net;

import logic.Eshop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Nimmt Verbindungswünsche von Clients entgegen und startet pro Client
// einen eigenen ClientRequestProcessor in einem eigenen Thread.
public class EShopServer {

    public static final int DEFAULT_PORT = 8080;

    private final int port;
    private final Eshop eshop;
    private ServerSocket serverSocket;

    public EShopServer(Eshop eshop) {
        this(eshop, DEFAULT_PORT);
    }

    public EShopServer(Eshop eshop, int port) {
        this.eshop = eshop;
        this.port = port;
    }

    public void starten() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("EShopServer bereit auf Port " + port + " ...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client verbunden: " + clientSocket.getInetAddress());

            ClientRequestProcessor processor = new ClientRequestProcessor(clientSocket, eshop);
            Thread thread = new Thread(processor);
            thread.start();
        }
    }

    public static void main(String[] args) throws IOException {
        Eshop eshop = new Eshop();
        EShopServer server = new EShopServer(eshop);
        server.starten();
    }
}
