package exceptions;

public class ArtikelExistiertBereits extends RuntimeException {
    public ArtikelExistiertBereits(String artikelBezeichnung) {
        super("Dieser Artikel: " + artikelBezeichnung + "existiert bereits!");
    }
}
