package net;

import logic.Eshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

// Hält NUR die Verbindung zu genau einem Client (Socket, Streams, die
// Lese-Schleife) und verteilt jedes eingehende Kommando an den passenden
// Handler. Enthält selbst keine Fachlogik mehr.
public class ClientRequestProcessor implements Runnable {

    private final Socket socket;
    private final Eshop eshop;

    public ClientRequestProcessor(Socket socket, Eshop eshop) {
        this.socket = socket;
        this.eshop = eshop;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Unser Bereich: Artikel, Checkout/Order, Warenkorb.
            List<KommandoHandler> handler = List.of(
                    new ArtikelKommandoHandler(eshop, in, out),
                    new CheckoutKommandoHandler(eshop, in, out),
                    new WarenkorbKommandoHandler(eshop, in, out)
                    // Sobald es ihn gibt, kommt hier z.B. dazu:
                    // new BenutzerKommandoHandler(eshop, in, out)
            );

            String kommando;
            while ((kommando = in.readLine()) != null) {
                if ("QUIT".equals(kommando)) {
                    socket.close();
                    return;
                }

                final String aktuellesKommando = kommando;

                KommandoHandler zustaendiger = handler.stream()
                        .filter(h -> h.istZustaendig(aktuellesKommando))
                        .findFirst()
                        .orElse(null);

                if (zustaendiger != null) {
                    zustaendiger.verarbeite(aktuellesKommando);
                } else {
                    out.println("ERROR");
                    out.println("Unbekanntes Kommando: " + aktuellesKommando);
                }
            }
        } catch (IOException e) {
            System.out.println("Verbindung zu Client beendet: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
