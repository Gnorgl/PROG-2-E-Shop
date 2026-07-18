package net;

import logic.Eshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

// Hält NUR die Verbindung zu genau einem Client (Socket, Streams, die
// Lese-Schleife) und verteilt jedes eingehende Kommando an den passenden
// Handler. Enthält selbst keine Fachlogik mehr.
//
// in/out und das ggf. schon gelesene erste Kommando werden von EShopServer
// übergeben: der Server muss die erste Zeile selbst lesen, um zu entscheiden,
// ob es sich um eine normale Kommando-Verbindung oder einen reinen Push-Kanal
// (SUBSCRIBE) handelt, bevor er diese Klasse hier überhaupt aufruft.
public class ClientRequestProcessor implements Runnable {

    private final Socket socket;
    private final Eshop eshop;
    private final BufferedReader in;
    private final PrintWriter out;
    private final String erstesKommando;

    public ClientRequestProcessor(Socket socket, Eshop eshop, String erstesKommando, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        this.eshop = eshop;
        this.erstesKommando = erstesKommando;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            // Unser Bereich: Artikel, Checkout/Order, Warenkorb.
            List<KommandoHandler> handler = List.of(
                    new ArtikelKommandoHandler(eshop, in, out),
                    new CheckoutKommandoHandler(eshop, in, out),
                    new WarenkorbKommandoHandler(eshop, in, out),
                    new BenutzerKommandoHandler(eshop, in, out)
            );

            String kommando = erstesKommando;
            while (kommando != null) {
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

                kommando = in.readLine();
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
