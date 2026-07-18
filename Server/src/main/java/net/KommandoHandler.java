package net;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import logic.Eshop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class KommandoHandler {

    protected final Eshop eshop;
    protected final BufferedReader in;
    protected final PrintWriter out;
    protected final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected KommandoHandler(Eshop eshop, BufferedReader in, PrintWriter out) {
        this.eshop = eshop;
        this.in = in;
        this.out = out;
    }

    // true, wenn dieser Handler für das übergebene Kommandowort zuständig ist.
    public abstract boolean istZustaendig(String kommando);

    public abstract void verarbeite(String kommando) throws IOException;

    protected void ok() {
        out.println("OK");
    }

    protected void ok(String daten) {
        out.println("OK");
        out.println(daten);
    }

    protected void fehler(Exception e) {
        out.println("ERROR");
        out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    protected void fehler(String nachricht) {
        out.println("ERROR");
        out.println(nachricht);
    }
}
