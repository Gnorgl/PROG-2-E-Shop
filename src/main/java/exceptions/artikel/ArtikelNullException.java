package exceptions.artikel;

import exceptions.VerwaltungsException;

public class ArtikelNullException extends VerwaltungsException {
    public ArtikelNullException() {
        super("Der Artikel darf nicht null sein.");
    }
}