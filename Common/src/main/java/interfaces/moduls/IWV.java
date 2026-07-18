package interfaces.moduls;

import entities.Artikel;

import java.io.IOException;
import java.util.HashMap;

public interface IWV {//Interface WarenkorbVerwaltung

    HashMap<Artikel, Integer> getAlleWarenkorbArtikel();

    boolean istLeer();

    int getMenge(Artikel artikel);

    void artikelHinzufuegen(Artikel artikel, int menge) throws IOException;

    void artikelEntfernen(Artikel artikel) throws IOException;

    void leeren() throws IOException;
}
