package net;

import logic.Eshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

            Thread thread = new Thread(() -> verbindungBehandeln(clientSocket));
            thread.start();
        }
    }

    // Jede neu ankommende Verbindung ist entweder eine normale Kommando-Verbindung
    // oder ein reiner Push-Kanal (erste Zeile "SUBSCRIBE"), über den der Server
    // Clients unaufgefordert über Änderungen informiert (z.B. Bestandsänderungen
    // durch andere Kunden), damit die GUI ohne Refresh-Button aktuell bleibt.
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
                    // Der Push-Kanal wird nur zum Senden benutzt; wir lesen trotzdem
                    // weiter, um zu bemerken, wenn der Client die Verbindung schließt
                    while (in.readLine() != null) {
                        // absichtlich leer
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
