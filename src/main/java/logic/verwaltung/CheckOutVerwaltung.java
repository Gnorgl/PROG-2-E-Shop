package logic.verwaltung;

import java.util.*;

import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import exceptions.artikel.ArtikelNichtGefunden;
import logic.moduls.ICV;
import persistence.shop.WarenkorbListe;

public class CheckOutVerwaltung implements ICV {
    private int rechnungsNummerZaehler = 1;
    private EreignisVerwaltung ereignisVerwaltung = new EreignisVerwaltung();

    public CheckOutVerwaltung() {

    }

    public void setEreignisVerwaltung(EreignisVerwaltung ereignisVerwaltung) {
        this.ereignisVerwaltung = ereignisVerwaltung;
    }

    @Override
    public double berechneNettoSumme(WarenkorbListe warenkorbListe) {
        double netto = 0;
        //durchlaufe alle Artikel im Warenkorb und berechne die Summe (Preis * Menge)
        HashMap<Artikel, Integer> warenkorbItems = warenkorbListe.getAlleArtikel();

        for (Artikel artikel : warenkorbItems.keySet()) {
            int menge = warenkorbItems.get(artikel);
            netto += artikel.getPreis() * menge;
        }
        return netto;
    }

    @Override
    public Rechnung checkOut(Kunde kunde, WarenkorbListe warenkorbListe, logic.moduls.IAV artikelVerwaltung) throws ArtikelNichtGefunden {
        HashMap<Artikel, Integer> warenkorbItems = warenkorbListe.getAlleArtikel();

        if (warenkorbItems.isEmpty()) {
            return null;
        }

        // Netto-Summe berechnen lassen
        double netto = berechneNettoSumme(warenkorbListe);

        List<Artikel> gekaufteArtikel = new ArrayList<>();

        // HIER WIRD ANGEPASST: Artikel entsprechend ihrer Menge oft in die Liste legen
        for (Artikel artikel : warenkorbItems.keySet()) {
            int menge = warenkorbItems.get(artikel);

            for (int i = 0; i < menge; i++) {
                gekaufteArtikel.add(artikel);
            }

            // Artikelbestand im Lager nach dem Kauf reduzieren
            try {
                artikelVerwaltung.bestandReduzieren(artikel.getArtikelNummer(), menge);
            } catch (ArtikelNichtGefunden e) {
                // sollte nicht passieren, da Artikel aus Warenkorb stammt
                throw e;
            } catch (Exception e) {
                // andere Validierungsfehler (Bestand, Menge) werden hier als Laufzeitfehler weitergegeben
                throw new RuntimeException("Fehler beim Reduzieren des Bestands für Artikel " + artikel.getArtikelNummer() + ": " + e.getMessage(), e);
            }

            try {
                ereignisVerwaltung.logEreignis(artikel, menge, kunde, "AUSLAGERUNG_KAUF");
            } catch (exceptions.artikel.ArtikelNullException e) {
                System.err.println("Fehler beim Loggen des Ereignisses: " + e.getMessage());
            }
        }

        // MwSt und Brutto berechnen
        double mwst = netto * 0.19;
        double brutto = netto + mwst;

        // Rechnung erstellen
        Rechnung rechnung = new Rechnung(
                rechnungsNummerZaehler++,
                kunde,
                gekaufteArtikel,
                netto,
                mwst,
                brutto
        );

        // Warenkorb leeren nach erfolgreichem Checkout
        warenkorbListe.leeren();

        return rechnung;
    }

    @Override
    public void rechnungAnzeigen(Rechnung rechnung) {
        if (rechnung == null) {
            System.out.println("Keine Rechnung vorhanden!");
            return;
        }

        /**
         * DATUM für die Rechnung mit Stunde und Minute
         */
        java.time.LocalDate tag = java.time.LocalDate.now();
        java.time.LocalTime zeit = java.time.LocalTime.now();

        String tagText = tag.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String zeitText = zeit.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        String rechnungsDatum = tagText + " um " + zeitText + " Uhr";


        System.out.println("\n========== RECHNUNG ==========");
        System.out.println("Rechnungsnummer: " + rechnung.getRechnungsNummer());
        System.out.println("Datum: " + rechnungsDatum);
        System.out.println("Kunde: " + rechnung.getKunde().getNachname() + ", " + rechnung.getKunde().getVorname());
        System.out.println("Adresse: " + rechnung.getKunde().getAdresse()); //
        System.out.println("\n--- Artikel ---");

        // Hilfsliste um doppelte Konsolenausgaben zu vermeiden
        List<Artikel> schonGedruckt = new ArrayList<>();

        // Stückzahl und Gesamtpreis aus der Liste berechnen
        for (Artikel artikel : rechnung.getArtikel()) {
            if (!schonGedruckt.contains(artikel)) {

                // Collections.frequency zählt, wie oft der Artikel in der Liste vorkommt
                int menge = Collections.frequency(rechnung.getArtikel(), artikel);
                double gesamtPreisPosition = artikel.getPreis() * menge;

                System.out.println("- " + menge + "x " + artikel.getBezeichnung() +
                        " (" + artikel.getArtikelNummer() + "): " +
                        gesamtPreisPosition + "€ (Einzelpreis: " + artikel.getPreis() + "€)");

                schonGedruckt.add(artikel);
            }
        }

        System.out.println("\n--- Summe ---");
        System.out.printf("Netto:  %.2f€%n", rechnung.getNettosumme());
        System.out.printf("MwSt:   %.2f€%n", rechnung.getMwstBetrag());
        System.out.printf("Brutto: %.2f€%n", rechnung.getBruttoSumme());
        System.out.println("==============================\n");
    }
}

//Funktionen für Warenkauf aus Warenkorb und Bestand aus Lager
