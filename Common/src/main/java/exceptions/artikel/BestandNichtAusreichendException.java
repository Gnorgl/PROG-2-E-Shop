package exceptions.artikel;

import exceptions.VerwaltungsException;

public class BestandNichtAusreichendException extends VerwaltungsException {
    public BestandNichtAusreichendException(int verfuegbar, int angefordert) {
        super("Nicht genügend Bestand! Verfügbar: " + verfuegbar + ", Angefordert: " + angefordert);
    }

    private BestandNichtAusreichendException(String nachricht, boolean roh) {
        super(nachricht);
    }

    // Für den Client: der Server schickt schon die fertig formatierte Fehlermeldung
    public static BestandNichtAusreichendException mitFertigerNachricht(String nachricht) {
        return new BestandNichtAusreichendException(nachricht, true);
    }
}

