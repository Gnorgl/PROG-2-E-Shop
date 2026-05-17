package logic.verwaltung;

import exceptions.artikel.ArtikelExistiertBereits;
import logic.moduls.IAV;
import logic.verwaltung.EreignisVerwaltung;
import entities.Mitarbeiter;
import entities.Artikel;
import persistence.shop.ArtikelListe;


import java.util.Iterator;


public class ArtikelVerwaltung implements IAV {
    private ArtikelListe artikelListe = new ArtikelListe();
    private EreignisVerwaltung ereignisVerwaltung;


    public ArtikelVerwaltung () {
    this.artikelListe = new ArtikelListe();
    this.ereignisVerwaltung = ereignisVerwaltung;
    }

    @Override
    public boolean legeArtikelAn(int nr, String name, int bestand, double preis) throws ArtikelExistiertBereits {
        if (findeArtikel(nr) != null) {
            throw new ArtikelExistiertBereits(name);
        }
        Artikel neuerArtikel = new Artikel(nr, name, bestand, preis);
        artikelListe.getArtikelImLager().add(neuerArtikel);
        Mitarbeiter aktuellerMitarbeiter = getCurrentMitarbeiter();
        ereignisVerwaltung.logEreignis(neuerArtikel, bestand, aktuellerMitarbeiter, "EINLAGERUNG");
        return true;
    }


    @Override
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
    public void bestandErhoehen(int nr, int anzahl) {
        Artikel a = findeArtikel(nr);
        if (a != null) {

            a.setBestand(a.getBestand() + anzahl);
            Mitarbeiter aktuellerMitarbeiter = getCurrentMitarbeiter();
            ereignisVerwaltung.logEreignis(a, anzahl, aktuellerMitarbeiter, "EINLAGERUNG_M");
        }
    }

    //Reduziert Bestand extra beim Checkout
    public void bestandReduzieren(int nr, int anzahl) {
        Artikel a = findeArtikel(nr);
        if (a != null && a.getBestand() >= anzahl) {
            a.setBestand(a.getBestand() - anzahl);
            Mitarbeiter akutellerMitarbeiter = getCurrentMitarbeiter();
            ereignisVerwaltung.logEreignis(a, anzahl, akutellerMitarbeiter, "AUSLAGERUNG_M");
        }
    }

    //Implementierung um den eingeloggten Mitarbeiter zu erhalten
    private Mitarbeiter getCurrentMitarbeiter() {
        return null;
    }

    public ArtikelListe getArtikelListe() {
        return this.artikelListe;
    }

    public Artikel findeArtikel(int nr) {
        for (Artikel a : artikelListe.getArtikelImLager()) {
            if (a.getArtikelNummer() == nr) {
                return a;
            }
        }
        return null; // Nicht gefunden
    }
}

