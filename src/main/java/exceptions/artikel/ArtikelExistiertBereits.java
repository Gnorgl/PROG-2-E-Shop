package exceptions.artikel;

import exceptions.VerwaltungsException;

public class ArtikelExistiertBereits extends VerwaltungsException {
    public ArtikelExistiertBereits(String artikelBezeichnung) {
        super("Dieser Artikel: " + artikelBezeichnung + " existiert bereits!");
    }
}
