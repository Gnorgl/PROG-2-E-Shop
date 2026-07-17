package logic.verwaltung;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entities.Artikel;
import entities.Kunde;
import entities.Massengutartikel; // <-- NEU: Import hinzugefügt
import entities.Rechnung;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.ArtikelNullException;
import interfaces.moduls.IAV;
import interfaces.moduls.ICV;
import persistence.shop.OrderListe;

public class CheckOutVerwaltung implements ICV {
    private int rechnungsNummerZaehler = 1;
    private EreignisVerwaltung ereignisVerwaltung = new EreignisVerwaltung();
    private OrderListe orderListe = new OrderListe();

    private final File datei = new File("checkout.json");
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public CheckOutVerwaltung() throws IOException {
        datenLaden();
    }

    private void safe() throws IOException {
        Object[] speicherContainer = new Object[]{ this.rechnungsNummerZaehler, this.orderListe };
        mapper.writerWithDefaultPrettyPrinter().writeValue(datei, speicherContainer);
    }

    private void datenLaden() throws IOException, IllegalArgumentException {
        if (!datei.exists()) return;
        Object[] speicherContainer = mapper.readValue(datei, Object[].class);
        this.rechnungsNummerZaehler = mapper.convertValue(speicherContainer[0], Integer.class);
        this.orderListe = mapper.convertValue(speicherContainer[1], OrderListe.class);
    }

    public List<Rechnung> getRechnungenFuerKunde(Kunde kunde) {
        List<Rechnung> ergebnis = new ArrayList<>();
        for (Rechnung r : orderListe.getAlleRechnungen()) {
            if (r.getKunde().getEmail().equals(kunde.getEmail())) {
                ergebnis.add(r);
            }
        }
        return ergebnis;
    }

    public void setEreignisVerwaltung(EreignisVerwaltung ereignisVerwaltung) {
        this.ereignisVerwaltung = ereignisVerwaltung;
    }

    @Override
    public double berechneNettoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        double netto = 0;
        //durchlaufe alle Artikel im Warenkorb und berechne die Summe
        for (Artikel artikel : warenkorbInhalt.keySet()) {
            int menge = warenkorbInhalt.get(artikel);
            netto += artikel.berechneGesamtpreis(menge); // Polymorphie greift hier perfekt!
        }
        return netto;
    }

    public double berechneBruttoSumme(Map<Artikel, Integer> warenkorbInhalt) {
        return berechneNettoSumme(warenkorbInhalt) * 1.19;
    }

    @Override
    public Rechnung checkOut(Kunde kunde, Map<Artikel, Integer> warenkorbInhalt, IAV artikelVerwaltung) throws ArtikelNichtGefunden, ArtikelNullException, IOException {
        if (warenkorbInhalt.isEmpty()) {
            return null;
        }

        // Netto-Summe berechnen lassen
        double netto = berechneNettoSumme(warenkorbInhalt);

        List<Artikel> gekaufteArtikel = new ArrayList<>();

        for (Artikel artikel : warenkorbInhalt.keySet()) {
            int menge = warenkorbInhalt.get(artikel);

            for (int i = 0; i < menge; i++) {
                gekaufteArtikel.add(artikel);
            }

            // Artikelbestand im Lager nach dem Kauf reduzieren
            try {
                artikelVerwaltung.bestandReduzieren(artikel.getArtikelNummer(), menge);
            } catch (ArtikelNichtGefunden e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Fehler beim Reduzieren des Bestands für Artikel " + artikel.getArtikelNummer() + ": " + e.getMessage(), e);
            }

            ereignisVerwaltung.logEreignis(artikel, menge, kunde, "AUSLAGERUNG_KAUF");
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

        orderListe.addRechnung(rechnung);
        safe();

        return rechnung;
    }


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

        List<Artikel> schonGedruckt = new ArrayList<>();
        for (Artikel artikel : rechnung.getArtikel()) {
            if (!schonGedruckt.contains(artikel)) {
                int menge = Collections.frequency(rechnung.getArtikel(), artikel);
                double gesamt = artikel.berechneGesamtpreis(menge);

                // Hier passen wir den Namen für den Bon an
                String anzeigeName = artikel.getBezeichnung();
                if (artikel instanceof Massengutartikel) {
                    anzeigeName += " (" + ((Massengutartikel) artikel).getPackungsGroesse() + "er Pack)";
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
        System.out.println("Adresse: " + rechnung.getKunde().getAdresse());
        System.out.println("\n--- Artikel ---");

        List<Artikel> schonGedruckt = new ArrayList<>();

        for (Artikel artikel : rechnung.getArtikel()) {
            if (!schonGedruckt.contains(artikel)) {

                int menge = Collections.frequency(rechnung.getArtikel(), artikel);
                double gesamtPreisPosition = artikel.berechneGesamtpreis(menge);

                String preisHinweis;
                if (artikel instanceof Massengutartikel ma) {
                    preisHinweis = String.format("%.2f€ pro %der-Pack", artikel.getPreis(), ma.getPackungsGroesse());
                } else {
                    preisHinweis = String.format("%.2f€ pro Stück", artikel.getPreis());
                }

                System.out.printf("- %dx %s (%d): %.2f€ (%s)%n",
                        menge, artikel.getBezeichnung(), artikel.getArtikelNummer(),
                        gesamtPreisPosition, preisHinweis);

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
