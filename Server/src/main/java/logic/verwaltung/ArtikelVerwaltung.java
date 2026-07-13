package logic.verwaltung;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.*;
import exceptions.artikel.AnzahlUngueltigException;
import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.MengeUngueltigException;
import exceptions.artikel.BestandNichtAusreichendException;
import interfaces.moduls.IAV;
import persistence.shop.ArtikelListe;
import entities.Ereignis;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class ArtikelVerwaltung implements IAV {
    private ArtikelListe artikelListe = new ArtikelListe();
    private EreignisVerwaltung ereignisVerwaltung = new EreignisVerwaltung();
    private Mitarbeiter currentMitarbeiter;


    private final File datei = new File("artikel.json");
    private final ObjectMapper mapper = new ObjectMapper();

    private long idCounter = 0;


    public ArtikelVerwaltung() {
        this.artikelListe = new ArtikelListe();
        datenLaden();
    }

    public void setEreignisVerwaltung(EreignisVerwaltung ereignisVerwaltung) {
        this.ereignisVerwaltung = ereignisVerwaltung;
    }

    public void setCurrentMitarbeiter(Mitarbeiter mitarbeiter) {
        this.currentMitarbeiter = mitarbeiter;
    }



    private void datenLaden() {
        if (!datei.exists()) {
            return;
        }
        try {
            // Liest das allgemeine Object-Array ein
            Object[] speicherContainer = mapper.readValue(datei, Object[].class);

            // Konvertiert die einzelnen Elemente im Array in die korrekten Zielklassen
            this.artikelListe = mapper.convertValue(speicherContainer[0], ArtikelListe.class);
            this.idCounter = mapper.convertValue(speicherContainer[1], Long.class);

            // Falls die Liste leer ist, wird der Counter zurückgesetzt
            if (this.artikelListe.getArtikelImLager().isEmpty()) {
                this.idCounter = 0;
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Fehler beim Laden der Artikel: " + e.getMessage());
        }
    }



    public void safe() {
        try {
            // Speichert sowohl die Liste als auch den Counter im Container-Array
            Object[] speicherContainer = new Object[]{ this.artikelListe, this.idCounter };

            mapper.writerWithDefaultPrettyPrinter().writeValue(datei, speicherContainer);
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Artikel: " + e.getMessage());
        }
    }




    @Override
    public boolean legeArtikelAn(String name, int bestand, double preis) throws ArtikelExistiertBereits {

        int neueNr = (int) ++idCounter;

        for (Artikel a : artikelListe.getArtikelImLager()) {
            if (a.getBezeichnung().equalsIgnoreCase(name)) {
                throw new ArtikelExistiertBereits(name);
            }
        }

        Artikel neuerArtikel = new Artikel(neueNr, name, bestand, preis);
        artikelListe.getArtikelImLager().add(neuerArtikel);


        safe();

        Mitarbeiter aktuellerMitarbeiter = getCurrentMitarbeiter();
        try {
            ereignisVerwaltung.logEreignis(neuerArtikel, bestand, aktuellerMitarbeiter, "EINLAGERUNG");
        } catch (Exception ex) {
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
        safe();
    }

    public boolean legeMassengutartikelAn(String bezeichnung, int bestand, double preis, int packungsGroesse)
            throws ArtikelExistiertBereits, MengeUngueltigException {

        int neueNr = (int) ++idCounter;

        for (Artikel a : artikelListe.getArtikelImLager()) {
            if (a.getBezeichnung().equalsIgnoreCase(bezeichnung)) {
                throw new ArtikelExistiertBereits(bezeichnung);
            }
        }

        if (packungsGroesse <= 0) {
            throw new IllegalArgumentException("Packungsgröße muss > 0 sein");
        }
        if (bestand % packungsGroesse != 0) {
            throw new MengeUngueltigException(String.valueOf(packungsGroesse));
        }

        Massengutartikel neuerArtikel = new Massengutartikel(neueNr, bezeichnung, bestand, preis, packungsGroesse);
        artikelListe.getArtikelImLager().add(neuerArtikel);

        safe();

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

        safe();

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

        safe();

        Mitarbeiter aktuellerMitarbeiter = getCurrentMitarbeiter();
        try {
            ereignisVerwaltung.logEreignis(a, anzahl, aktuellerMitarbeiter, "AUSLAGERUNG_M");
        } catch (Exception ex) {
            System.err.println("Fehler beim Loggen des Ereignisses: " + ex.getMessage());
        }
    }

    // Berechnet den täglichen Bestandsverlauf eines Artikels über die letzten 30 Tage
    public Map<LocalDate, Integer> getBestandsHistorie(int artikelNr) throws ArtikelNichtGefunden {
        Artikel artikel = findeArtikel(artikelNr);

        int aktuellerBestand = artikel.getBestand();
        LocalDateTime vor30Tagen = LocalDateTime.now().minusDays(30);

        // Nur Ereignisse der letzten 30 Tage, chronologisch sortiert
        List<Ereignis> relevantEreignisse = ereignisVerwaltung.getEreignisseFuerArtikel(artikelNr)
                .stream()
                .filter(e -> e.getZeitstempel().isAfter(vor30Tagen))
                .sorted(Comparator.comparing(Ereignis::getZeitstempel))
                .collect(Collectors.toList());

        // 1) Rückwärts rekonstruieren: Wir kennen nur den heutigen Bestand, nicht den von vor 30 Tagen.
        //    Wir drehen deshalb jedes Ereignis von neu→alt rückgängig (Einlagerung subtrahieren, Auslagerung addieren),
        //    bis wir beim Zustand vor 30 Tagen ankommen  das ist unser Startpunkt für Schritt 2.
        int bestandRekonstruiert = aktuellerBestand;
        ListIterator<Ereignis> revIt = relevantEreignisse.listIterator(relevantEreignisse.size());
        while (revIt.hasPrevious()) {
            Ereignis e = revIt.previous();
            if (e.getTyp() != null && e.getTyp().contains("EINLAGERUNG")) {
                bestandRekonstruiert -= e.getAnzahl();
            } else if (e.getTyp() != null && e.getTyp().contains("AUSLAGERUNG")) {
                bestandRekonstruiert += e.getAnzahl();
            }
        }

        // 2) Vorwärts: Tag für Tag die Ereignisse anwenden und den Tagesendbestand speichern
        Map<LocalDate, Integer> historie = new LinkedHashMap<>();
        LocalDate startDatum = LocalDate.now().minusDays(29);
        for (int i = 0; i < 30; i++) {
            LocalDate datum = startDatum.plusDays(i);

            List<Ereignis> tagesEreignisse = relevantEreignisse.stream()
                    .filter(e -> e.getZeitstempel().toLocalDate().equals(datum))
                    .sorted(Comparator.comparing(Ereignis::getZeitstempel))
                    .collect(Collectors.toList());

            for (Ereignis e : tagesEreignisse) {
                if (e.getTyp() != null && e.getTyp().contains("EINLAGERUNG")) {
                    bestandRekonstruiert += e.getAnzahl();
                } else if (e.getTyp() != null && e.getTyp().contains("AUSLAGERUNG")) {
                    bestandRekonstruiert -= e.getAnzahl();
                }
            }

            // Tagesendbestand in die Map eintragen
            historie.put(datum, bestandRekonstruiert);
        }

        return historie;
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



