package net;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class PushListener {
    private final List<Runnable> listener = new CopyOnWriteArrayList<>();
    private Socket socket;
    private volatile boolean laeuft = true;

    public PushListener(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("SUBSCRIBE");

            Thread thread = new Thread(this::hoerSchleife, "push-listener");
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            System.err.println("Push-Kanal konnte nicht aufgebaut werden: " + e.getMessage());
        }
    }

    private void hoerSchleife() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (laeuft && in.readLine() != null) {
                Platform.runLater(this::benachrichtigeAlle);
            }
        } catch (IOException e) {
            if (laeuft) {
                System.err.println("Push-Kanal getrennt: " + e.getMessage());
            }
        }
    }

    private void benachrichtigeAlle() {
        for (Runnable r : listener) {
            r.run();
        }
    }

    public void aktualisierungAbonnieren(Runnable r) {
        listener.add(r);
    }

    public void abmelden(Runnable r) {
        listener.remove(r);
    }

    public void schliessen() {
        laeuft = false;
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
