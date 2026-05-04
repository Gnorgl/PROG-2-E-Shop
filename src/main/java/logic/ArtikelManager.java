package logic;

import entities.Artikel;
import java.util.ArrayList;
import java.util.List;

public class ArtikelManager {
    private List<Artikel> artikelbestand = new ArrayList<>();

    public Artikel legeArtikelan(int nr, String name, int bestand, double preis) {
        Artikel neuerArtikel = new Artikel(nr, name, bestand, preis);
        artikelBestand.add(neuerArtikel);
        return neuerArtikel;
    }
}
public void Bestanderhoehen (int nr, int anzahl) {
    for (Artikel a : artikelBestand) {
        if (a.getArtikelNummer() == nr) {
            int neuerBestand = ... +  anzahl;
            break;
        }
    }
}


//Wird genutzt, um neue Artikel zu erstellen