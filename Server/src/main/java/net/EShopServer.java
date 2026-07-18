package net;

import logic.Eshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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

            Thread thread = new Thread(() -> verbindungBehandeln(clientSocket));
            thread.start();
        }
    }

    private void verbindungBehandeln(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String ersteZeile = in.readLine();
            if (ersteZeile == null) {
                socket.close();
                return;
            }

            if ("SUBSCRIBE".equals(ersteZeile)) {
                BroadcastManager.getInstanz().registrieren(out);
                try {
                    while (in.readLine() != null) {
                    }
                } finally {
                    BroadcastManager.getInstanz().entfernen(out);
                    socket.close();
                }
            } else {
                new ClientRequestProcessor(socket, eshop, ersteZeile, in, out).run();
            }
        } catch (IOException e) {
            System.out.println("Verbindung beendet: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Eshop eshop = new Eshop();
        EShopServer server = new EShopServer(eshop);
        server.starten();
    }
}
