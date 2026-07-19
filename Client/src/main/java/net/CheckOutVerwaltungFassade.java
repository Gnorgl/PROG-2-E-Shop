package net;

import com.fasterxml.jackson.core.type.TypeReference;
import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import exceptions.artikel.BestandNichtAusreichendException;
import exceptions.artikel.MengeUngueltigException;
import interfaces.moduls.ICV;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/** Kapselt die Socket-Kommunikation für den Checkout-Vorgang. */
public class CheckOutVerwaltungFassade implements ICV {

    private final ServerVerbindung verbindung;

    public CheckOutVerwaltungFassade(ServerVerbindung verbindung) {
        this.verbindung = verbindung;
    }

    @Override
    public Rechnung checkOut(Kunde kunde, Map<Artikel, Integer> warenkorbInhalt, interfaces.moduls.IAV artikelVerwaltung) throws ArtikelNichtGefunden, ArtikelNullException, BestandNichtAusreichendException, MengeUngueltigException, IOException {
        try {
            String kundeJson = verbindung.mapper.writeValueAsString(kunde);
            String warenkorbJson = verbindung.mapper.writeValueAsString(zuNrMap(warenkorbInhalt));

            String json = verbindung.sendeKommandoMitAntwort("CHECKOUT", kundeJson, warenkorbJson);
            return verbindung.mapper.readValue(json, Rechnung.class);
        } catch (exceptions.ServerFehlerException e) {
            switch (e.getExceptionName()) {
                case "ArtikelNichtGefunden" -> throw ArtikelNichtGefunden.mitFertigerNachricht(e.getNachricht());
                case "ArtikelNullException" -> throw new ArtikelNullException();
                case "BestandNichtAusreichendException" -> throw BestandNichtAusreichendException.mitFertigerNachricht(e.getNachricht());
                case "MengeUngueltigException" -> throw MengeUngueltigException.mitFertigerNachricht(e.getNachricht());
                default -> throw new IOException("Serverfehler: " + e.getMessage());
            }
        }
    }

    @Override
    public double berechneNettoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        try {
            String warenkorbJson = verbindung.mapper.writeValueAsString(zuNrMap(warenkorbInhalt));
            String antwort = verbindung.sendeKommandoMitAntwort("NETTOSUMME", warenkorbJson);
            return Double.parseDouble(antwort);
        } catch (IOException | exceptions.ServerFehlerException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
    }

    @Override
    public double berechneBruttoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        return berechneNettoSumme(warenkorbInhalt) * 1.19;
    }

    @Override
    public String generiereRechnungsText(Rechnung rechnung, String lieferadresse) {
        StringBuilder beleg = new StringBuilder();
        beleg.append("==================================================\n");
        beleg.append("                    RECHNUNG                      \n");
        beleg.append("==================================================\n");
        beleg.append("Rechnungsnummer: ").append(rechnung.getRechnungsNummer()).append("\n");
        beleg.append("Datum: ").append(java.time.LocalDate.now()).append("\n\n");
        beleg.append("Lieferadresse:\n");
        beleg.append(lieferadresse).append("\n");
        beleg.append("--------------------------------------------------\n");

        List<Artikel> schonGedruckt = new java.util.ArrayList<>();
        for (Artikel artikel : rechnung.getArtikel()) {
            if (!schonGedruckt.contains(artikel)) {
                int menge = java.util.Collections.frequency(rechnung.getArtikel(), artikel);
                double gesamt = artikel.berechneGesamtpreis(menge);

                String anzeigeName = artikel.getBezeichnung();
                if (artikel instanceof entities.Massengutartikel) {
                    anzeigeName += " (" + ((entities.Massengutartikel) artikel).getPackungsGroesse() + "er Pack)";
                }

                beleg.append(String.format("%dx %-25s %10.2f €\n", menge, anzeigeName, gesamt));
                schonGedruckt.add(artikel);
            }
        }

        beleg.append("--------------------------------------------------\n");
        beleg.append(String.format("%-30s %10.2f €\n", "Netto:", rechnung.getNettosumme()));
        beleg.append(String.format("%-30s %10.2f €\n", "MwSt (19%):", rechnung.getMwstBetrag()));
        beleg.append(String.format("%-30s %10.2f €\n", "BRUTTO GESAMT:", rechnung.getBruttoSumme()));
        beleg.append("==================================================");

        return beleg.toString();
    }

    // Bestellverlauf
    @Override
    public List<Rechnung> getRechnungenFuerKunde(Kunde kunde) {
        try {
            String kundeJson = verbindung.mapper.writeValueAsString(kunde);
            String json = verbindung.sendeKommandoMitAntwort("BESTELLVERLAUF", kundeJson);
            return verbindung.mapper.readValue(json, new TypeReference<List<Rechnung>>() {
            });
        } catch (IOException | exceptions.ServerFehlerException e) {
            throw new RuntimeException("Fehler bei der Kommunikation mit dem Server: " + e.getMessage(), e);
        }
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
        System.out.printf("Netto:  %.2f€%n", rechnung.getNettosumme());
        System.out.printf("MwSt:   %.2f€%n", rechnung.getMwstBetrag());
        System.out.printf("Brutto: %.2f€%n", rechnung.getBruttoSumme());
        System.out.println("==============================\n");
    }

    private Map<Integer, Integer> zuNrMap(Map<Artikel, Integer> warenkorbInhalt) {
        Map<Integer, Integer> nrMap = new LinkedHashMap<>();
        for (Map.Entry<Artikel, Integer> eintrag : warenkorbInhalt.entrySet()) {
            nrMap.put(eintrag.getKey().getArtikelNummer(), eintrag.getValue());
        }
        return nrMap;
    }
}
