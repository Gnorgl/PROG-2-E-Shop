package exceptions.artikel;

import exceptions.VerwaltungsException;

public class BestandNichtAusreichendException extends VerwaltungsException {
    public BestandNichtAusreichendException(int verfuegbar, int angefordert) {
        super("Nicht genügend Bestand! Verfügbar: " + verfuegbar + ", Angefordert: " + angefordert);
    }
}

