package logic;

import entities.Artikel;
import exceptions.ArtikelExistiertBereits;

import java.util.ArrayList;
import java.util.List;

public class Artikelmanager {
    private List<Artikel> artikelBestand;

    public Artikelmanager () {
        this.artikelBestand = new ArrayList<>();
    }

    public boolean legeArtikelan(int nr, String name, int bestand, double preis) throws ArtikelExistiertBereits {
        // Liste durchsuchen ob Artikel bereit exisistiert.
        for (Artikel a : artikelBestand) {
            if (a.getArtikelNummer() == nr) {
                throw new ArtikelExistiertBereits(name);
            }
        }
        Artikel neuerArtikel = new Artikel(nr, name, bestand, preis);
        artikelBestand.add(neuerArtikel);
        return true;

    }

    public void loeschen()

    public void bestandErhoehen ( int nr, int anzahl) {
        for (Artikel a : artikelBestand) {
            if (a.getArtikelNummer() == nr) {
                int neuerBestand = anzahl;
                break;
            }
        }
    }
}

