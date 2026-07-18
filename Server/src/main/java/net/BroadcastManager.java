package net;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


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

    public void broadcast(String nachricht) {
        for (PrintWriter out : abonnenten) {
            out.println(nachricht);
        }
    }
}
