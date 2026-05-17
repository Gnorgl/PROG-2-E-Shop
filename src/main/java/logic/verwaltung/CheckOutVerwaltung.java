package logic.verwaltung;

import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import logic.moduls.ICV;
import persistence.shop.WarenkorbListe;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;



import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import logic.moduls.ICV;
import persistence.shop.WarenkorbListe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckOutVerwaltung implements ICV {

    private int rechnungsNummerZaehler = 1;

    @Override
    public double berechneNettoSumme(WarenkorbListe warenkorbListe) {
        double netto = 0;
        HashMap<Artikel, Integer> warenkorbItems = warenkorbListe.getAlleArtikel();

        for (Artikel artikel : warenkorbItems.keySet()) {
            int menge = warenkorbItems.get(artikel);
            netto += artikel.getPreis() * menge;
        }
        return netto;
    }

    @Override
    public Rechnung checkOut(Kunde kunde, WarenkorbListe warenkorbListe, ArtikelVerwaltung artikelVerwaltung) {
        HashMap<Artikel, Integer> warenkorbItems = warenkorbListe.getAlleArtikel();

        if (warenkorbItems.isEmpty()) {
            return null;
        }

        // Netto-Summe berechnen lassen
        double netto = berechneNettoSumme(warenkorbListe);

        List<Artikel> gekaufteArtikel = new ArrayList<>();

        for (Artikel artikel : warenkorbItems.keySet()) {
            int menge = warenkorbItems.get(artikel);
            gekaufteArtikel.add(artikel);

            // Artikelbestand im Lager nach dem Kauf reduzieren!!

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

        System.out.println("\n========== RECHNUNG ==========");
        System.out.println("Rechnungsnummer: " + rechnung.getRechnungsNummer());
        System.out.println("Datum: " + rechnung.getDatum());
        System.out.println("Kunde: " + rechnung.getKunde().getNachname() + ", " + rechnung.getKunde().getVorname());
        System.out.println("\n--- Artikel ---");

        for (Artikel artikel : rechnung.getArtikel()) {
            System.out.println("- " + artikel.getBezeichnung() +
                    " (" + artikel.getArtikelNummer() + "): " +
                    artikel.getPreis() + "€");
        }

        System.out.println("\n--- Summe ---");
        System.out.printf("Netto:  %.2f€%n", rechnung.getNettosumme());
        System.out.printf("MwSt:   %.2f€%n", rechnung.getMwstBetrag());
        System.out.printf("Brutto: %.2f€%n", rechnung.getBruttoSumme());
        System.out.println("==============================\n");
    }
}

//Funktionen für Warenkauf aus Warenkorb und Bestand aus Lager
