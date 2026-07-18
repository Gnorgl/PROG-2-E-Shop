package exceptions.artikel;

import exceptions.VerwaltungsException;

public class ArtikelExistiertBereits extends VerwaltungsException {
    public ArtikelExistiertBereits(String artikelBezeichnung) {
        super("Dieser Artikel: " + artikelBezeichnung + " existiert bereits!");
    }

    private ArtikelExistiertBereits(String nachricht, boolean roh) {
        super(nachricht);
    }

    // Für den Client: der Server schickt schon die fertig formatierte Fehlermeldung
    public static ArtikelExistiertBereits mitFertigerNachricht(String nachricht) {
        return new ArtikelExistiertBereits(nachricht, true);
    }
}
