package net;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Verwaltet alle Push-Kanäle (eine zweite, separate Verbindung pro Client, die nur
// zum Zuhören da ist). Threadsicher, weil mehrere Client-Threads gleichzeitig
// broadcasten und sich gleichzeitig neue/alte Push-Verbindungen an-/abmelden können.
// CopyOnWriteArrayList passt hier gut: viele Lese-/Broadcast-Zugriffe, aber nur
// selten ein neuer Client, der sich an-/abmeldet.
public class BroadcastManager {
    private static final BroadcastManager INSTANZ = new BroadcastManager();
    private final List<PrintWriter> abonnenten = new CopyOnWriteArrayList<>();

    private BroadcastManager() {
    }

    public static BroadcastManager getInstanz() {
        return INSTANZ;
    }

    public void registrieren(PrintWriter out) {
        abonnenten.add(out);
    }

    public void entfernen(PrintWriter out) {
        abonnenten.remove(out);
    }

    // Schickt die Nachricht an alle registrierten Push-Kanäle (also an alle
    // aktuell verbundenen Clients), damit diese sich unaufgefordert aktualisieren können.
    public void broadcast(String nachricht) {
        for (PrintWriter out : abonnenten) {
            out.println(nachricht);
        }
    }
}
