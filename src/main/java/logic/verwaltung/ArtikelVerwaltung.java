package logic.verwaltung;
import entities.*;
import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
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


    public ArtikelVerwaltung () {
    this.artikelListe = new ArtikelListe();
    }

    public void setEreignisVerwaltung(EreignisVerwaltung ereignisVerwaltung) {
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

    public boolean legeMassengutartikelAn(int nr, String bezeichnung, int bestand, double preis, int packungsGroesse)
            throws ArtikelExistiertBereits {
        if (findeArtikel(nr) != null) {
            throw new ArtikelExistiertBereits(bezeichnung);
        }

        if (bestand % packungsGroesse != 0) {
            throw new IllegalArgumentException("Bestand muss Vielfaches der Packungsgröße sein!");
        }

        Massengutartikel neuerArtikel = new Massengutartikel(nr, bezeichnung, bestand, preis, packungsGroesse);
        artikelListe.getArtikelImLager().add(neuerArtikel);

        Benutzer aktuellerBenutzer = getCurrentBenutzer();
        ereignisVerwaltung.logEreignis(neuerArtikel, bestand, aktuellerBenutzer, "EINLAGERUNG");
        return true;
    }

    @Override
    public void bestandErhoehen(int nr, int anzahl) throws ArtikelNichtGefunden {
        Artikel a = findeArtikel(nr);
        if (a != null) {

            a.setBestand(a.getBestand() + anzahl);
            Mitarbeiter aktuellerMitarbeiter = getCurrentMitarbeiter();
            ereignisVerwaltung.logEreignis(a, anzahl, aktuellerMitarbeiter, "EINLAGERUNG_M");
        }
    }

    //Reduziert Bestand extra beim Checkout
    public void bestandReduzieren(int nr, int anzahl) throws ArtikelNichtGefunden {
        Artikel a = findeArtikel(nr);
        if (a != null && a.getBestand() >= anzahl) {
            a.setBestand(a.getBestand() - anzahl);
            Mitarbeiter akutellerMitarbeiter = getCurrentMitarbeiter();
            ereignisVerwaltung.logEreignis(a, anzahl, akutellerMitarbeiter, "AUSLAGERUNG_M");
        }
    }

    public Map<LocalDate, Integer> getBestandsHistorie(int artikelNr) {
        Artikel artikel = findeArtikel(artikelNr);
        if (artikel != null) {
            return new HashMap<>();
        }
        Map<LocalDate, Integer> historie = new LinkedHashMap<>();
        int aktuellerBestand = artikel.getBestand();
        LocalDate heute =  LocalDate.now();

        LocalDateTime vor30Tagen = LocalDateTime.now().minusDays(30);
        List<Ereignis> relevantEreignis = ereignisVerwaltung
                .getEreignisseFuerArtikel(artikelNr)
                .stream()
                .filter(e -> e.getZeitstempel().isAfter(vor30Tagen))
                .sorted(Comparator.comparing(Ereignis::getZeitstempel).reversed())
                .collect(Collectors.toList());

        int bestandRekonstuiert = aktuellerBestand;
        for (int tag = 0; tag < 30; tag++) {
            LocalDate datum = heute.minusDays(tag);

            List<Ereignis> tagesEreignisse = relevantEreignis
                    .stream()
                    .filter(e -> e.getZeitstempel().toLocalDate().equals(datum))
                    .collect(Collectors.toList());

            historie.put(datum, bestandRekonstuiert);

            for (Ereignis e : tagesEreignisse) {
                if (e.getTyp().contains("EINLAGERUNG")) {
                    bestandRekonstuiert -= e.getAnzahl();
                } else if (e.getTyp().contains("AUSLAGERUNG")) {
                    bestandRekonstuiert += e.getAnzahl();
                }
            }
        }
        return historie;
    }

    public void zeigeBestandsHistorie(int artikelNr) {
        Artikel artikel = findeArtikel(artikelNr);
        if (artikel == null) {
            System.out.println("Artikel nicht gefunden");
            return;
        }
        Map<LocalDate, Integer> historie = getBestandsHistorie(artikelNr);
        System.out.println("\n==================== BESTANDSHISTORIE ====================");
        System.out.println("Artikel: " + artikel.getBezeichnung() + " (Nr. " + artikel.getArtikelNummer() + ")");
        System.out.println("========================================================");
        System.out.println("Datum          | Bestand am Tagesende");
        System.out.println("--------");

        historie.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("%s | %d%n", entry.getKey(), entry.getValue()));

        System.out.println("========================================================\n");
    }



    //Implementierung um den eingeloggten Mitarbeiter zu erhalten
    private Mitarbeiter getCurrentMitarbeiter() {
        return null;
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
        throw new ArtikelNichtGefunden(nr); // Nicht gefunden
    }
}

