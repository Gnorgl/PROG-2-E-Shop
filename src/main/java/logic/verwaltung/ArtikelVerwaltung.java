package logic.verwaltung;
import entities.*;
import exceptions.artikel.AnzahlUngueltigException;
import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.MengeUngueltigException;
import exceptions.artikel.BestandNichtAusreichendException;
import logic.moduls.IAV;
import logic.verwaltung.EreignisVerwaltung;
import persistence.shop.ArtikelListe;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.Comparator;

public class ArtikelVerwaltung implements IAV {
    private ArtikelListe artikelListe = new ArtikelListe();
    private EreignisVerwaltung ereignisVerwaltung = new EreignisVerwaltung();
    private Mitarbeiter currentMitarbeiter;

    public ArtikelVerwaltung() {
        this.artikelListe = new ArtikelListe();
    }

    public void setEreignisVerwaltung(EreignisVerwaltung ereignisVerwaltung) {
        this.ereignisVerwaltung = ereignisVerwaltung;
    }

    public void setCurrentMitarbeiter(Mitarbeiter mitarbeiter) {
        this.currentMitarbeiter = mitarbeiter;
    }

    @Override
    public boolean legeArtikelAn(int nr, String name, int bestand, double preis) throws ArtikelExistiertBereits {
        try {
            findeArtikel(nr);
            throw new ArtikelExistiertBereits(name);
        } catch (ArtikelNichtGefunden e) {
            // Artikel existiert nicht, daher kann er angelegt werden
        }

        Artikel neuerArtikel = new Artikel(nr, name, bestand, preis);
        artikelListe.getArtikelImLager().add(neuerArtikel);

        Mitarbeiter aktuellerMitarbeiter = getCurrentMitarbeiter();
        try {
            ereignisVerwaltung.logEreignis(neuerArtikel, bestand, aktuellerMitarbeiter, "EINLAGERUNG");
        } catch (Exception ex) {
            // Logging-Fehler sollten nicht den Geschäftsprozess blockieren
            System.err.println("Fehler beim Loggen des Ereignisses: " + ex.getMessage());
        }
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

    public boolean legeMassengutartikelAn(int nr, String bezeichnung, int bestand, double preis, int packungsGroesse)
            throws ArtikelExistiertBereits, MengeUngueltigException {
        try {
            findeArtikel(nr);
            throw new ArtikelExistiertBereits(bezeichnung);
        } catch (ArtikelNichtGefunden e) {
            // nicht vorhanden
        }

        if (packungsGroesse <= 0) {
            throw new IllegalArgumentException("Packungsgröße muss > 0 sein");
        }
        if (bestand % packungsGroesse != 0) {
            throw new MengeUngueltigException(String.valueOf(packungsGroesse));
        }

        Massengutartikel neuerArtikel = new Massengutartikel(nr, bezeichnung, bestand, preis, packungsGroesse);
        artikelListe.getArtikelImLager().add(neuerArtikel);

        Mitarbeiter aktuellerMitarbeiter = getCurrentMitarbeiter();
        try {
            ereignisVerwaltung.logEreignis(neuerArtikel, bestand, aktuellerMitarbeiter, "EINLAGERUNG");
        } catch (Exception ex) {
            System.err.println("Fehler beim Loggen des Ereignisses: " + ex.getMessage());
        }
        return true;
    }

    @Override
    public void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden, AnzahlUngueltigException, MengeUngueltigException {
        Artikel a = findeArtikel(nr);

        if (anzahl <= 0) {
            throw new AnzahlUngueltigException();
        }

        if (!a.istMengeGueltig(anzahl)) {
            if (a instanceof Massengutartikel) {
                throw new MengeUngueltigException(String.valueOf(((Massengutartikel) a).getPackungsGroesse()));
            } else {
                throw new AnzahlUngueltigException();
            }
        }

        a.setBestand(a.getBestand() + anzahl);
        Mitarbeiter aktuellerMitarbeiter = getCurrentMitarbeiter();
        try {
            ereignisVerwaltung.logEreignis(a, anzahl, aktuellerMitarbeiter, "EINLAGERUNG_M");
        } catch (Exception ex) {
            System.err.println("Fehler beim Loggen des Ereignisses: " + ex.getMessage());
        }
    }

    // Reduziert Bestand (z. B. beim Checkout)
    public void bestandReduzieren(int nr, int anzahl) throws ArtikelNichtGefunden, AnzahlUngueltigException, BestandNichtAusreichendException, MengeUngueltigException {
        Artikel a = findeArtikel(nr);

        if (anzahl <= 0) {
            throw new AnzahlUngueltigException();
        }

        if (a.getBestand() < anzahl) {
            throw new BestandNichtAusreichendException(a.getBestand(), anzahl);
        }

        if (!a.istMengeGueltig(anzahl)) {
            if (a instanceof Massengutartikel) {
                throw new MengeUngueltigException(String.valueOf(((Massengutartikel) a).getPackungsGroesse()));
            } else {
                throw new AnzahlUngueltigException();
            }
        }

        a.setBestand(a.getBestand() - anzahl);
        Mitarbeiter aktuellerMitarbeiter = getCurrentMitarbeiter();
        try {
            ereignisVerwaltung.logEreignis(a, anzahl, aktuellerMitarbeiter, "AUSLAGERUNG_M");
        } catch (Exception ex) {
            System.err.println("Fehler beim Loggen des Ereignisses: " + ex.getMessage());
        }
    }



    // Implementierung um den eingeloggten Mitarbeiter zu erhalten
    private Mitarbeiter getCurrentMitarbeiter() {
        return this.currentMitarbeiter;
    }

    public ArtikelListe getArtikelListe() {
        return this.artikelListe;
    }

    public Artikel findeArtikel(int nr) throws ArtikelNichtGefunden {
        for (Artikel a : artikelListe.getArtikelImLager()) {
            if (a.getArtikelNummer() == nr) {
                return a;
            }
        }
        throw new ArtikelNichtGefunden(String.valueOf(nr));
    }
}

