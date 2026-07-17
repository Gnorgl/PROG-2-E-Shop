package net;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Eine gemeinsame Socket-Verbindung zum Server, die von allen Fassaden
// (Artikel, Checkout, Warenkorb, ...) benutzt wird. Ein Kommando besteht
// aus mehreren Zeilen (Kommandowort + Parameter), die Antwort beginnt
// immer mit "OK" oder "ERROR".
public class ServerVerbindung {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public ServerVerbindung(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    // Für Kommandos, deren "OK"-Antwort KEINE Daten-Zeile hat (z.B. BESTAND_ERHOEHEN).
    // Wirft bei "ERROR" eine ServerFehlerException mit der Server-Fehlermeldung.
    public synchronized void sendeKommando(String... zeilen) throws IOException, ServerFehlerException {
        for (String zeile : zeilen) {
            out.println(zeile);
        }
        String status = liesZeile();
        if (!"OK".equals(status)) {
            throw new ServerFehlerException(liesZeile());
        }
    }

    // Für Kommandos, deren "OK"-Antwort zusätzlich eine Daten-Zeile hat
    // (z.B. ALLE_ARTIKEL, BESTANDSHISTORIE, CHECKOUT, ...).
    // Gibt im Erfolgsfall die Daten-Zeile zurück, wirft sonst eine ServerFehlerException
    // mit der vom Server gesendeten Fehlermeldung.
    public synchronized String sendeKommandoMitAntwort(String... zeilen) throws IOException, ServerFehlerException {
        for (String zeile : zeilen) {
            out.println(zeile);
        }
        String status = liesZeile();
        String zweiteZeile = liesZeile();
        if ("OK".equals(status)) {
            return zweiteZeile;
        }
        throw new ServerFehlerException(zweiteZeile);
    }

    private String liesZeile() throws IOException {
        String zeile = in.readLine();
        if (zeile == null) {
            throw new IOException("Verbindung zum Server wurde beendet.");
        }
        return zeile;
    }

    public void schliessen() {
        try {
            out.println("QUIT");
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
