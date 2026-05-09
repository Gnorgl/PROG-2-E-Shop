package logic.management;

import entities.Artikel;
import exceptions.ArtikelExistiertBereits;
import logic.moduls.IAV;
import persistence.ArtikelListe;

import java.util.Iterator;


public class ArtikelVerwaltung implements IAV{
    private ArtikelListe artikelListe;


    public ArtikelVerwaltung () {
        this.artikelListe = new ArtikelListe();
    }
    public boolean legeArtikelAn(int nr, String name, int bestand, double preis) throws ArtikelExistiertBereits {
        for (Artikel a : artikelListe.getArtikelImLager()) {
            if (a.getArtikelNummer() == nr) {
                throw new ArtikelExistiertBereits(name);
            }
        }
        Artikel neuerArtikel = new Artikel(nr, name, bestand, preis);
        artikelListe.fuegeArtikelInsLagerHinzu(neuerArtikel);
        return true;
    }

    public void loeschen(int nr) {
        Iterator<Artikel> it = artikelListe.getArtikelImLager().iterator();
        while (it.hasNext()) {
            Artikel artikel = it.next();

            if (artikel.getArtikelNummer() == nr) {
            it.remove();
            break;
            }

        }
    }

    @Override
    public void bestandErhoehen ( int nr, int anzahl ) {
        for (Artikel a : artikelListe.getArtikelImLager()) {
            if (a.getArtikelNummer() == nr) {
                int neuerBestand = anzahl;
                break;
            }
        }
    }
}

