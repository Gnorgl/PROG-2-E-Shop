package exceptions.artikel;

import exceptions.VerwaltungsException;

public class MengeUngueltigException extends VerwaltungsException {
    public MengeUngueltigException(String packungsGroesse) {
        super("Ungültige Menge! Muss Vielfaches von " + packungsGroesse + " sein.");
    }
}