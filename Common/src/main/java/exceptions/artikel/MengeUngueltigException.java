package exceptions.artikel;

import exceptions.VerwaltungsException;

public class MengeUngueltigException extends VerwaltungsException {
    public MengeUngueltigException(String packungsGroesse) {
        super("Ungültige Menge! Muss Vielfaches von " + packungsGroesse + " sein.");
    }

    private MengeUngueltigException(String nachricht, boolean roh) {
        super(nachricht);
    }

    // Für den Client: der Server schickt schon die fertig formatierte Fehlermeldung,
    public static MengeUngueltigException mitFertigerNachricht(String nachricht) {
        return new MengeUngueltigException(nachricht, true);
    }
}