package ui.navigation;


import exceptions.artikel.ArtikelNichtGefunden;
import logic.Eshop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import entities.Artikel;
import entities.Ereignis;
import entities.Kunde;
import entities.Rechnung;
import logic.verwaltung.ArtikelVerwaltung;
import logic.verwaltung.EreignisVerwaltung;
import persistence.shop.WarenkorbListe;
import java.util.HashMap;

public class ShoppingServiceManager {
    private final Eshop eshop;
    private final EreignisVerwaltung ereignisVerwaltung;
    private final Scanner scanner;
    private final SessionManager session;
    private final WarenkorbListe warenkorb = new WarenkorbListe();

    public ShoppingServiceManager(Eshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
        this.ereignisVerwaltung = eshop.getEreignisVerwaltung();
    }

    public void warenkatalog() {
        boolean running = true;
        while (running) {
            System.out.println("\n------Warenkatalog------");
            System.out.println("[A] Artikel anzeigen");
            System.out.println("[H] Zum Warenkorb hinzufügen");
            System.out.println("[W] Warenkorb anzeigen");
            System.out.println("[M] Menge ändern");
            System.out.println("[R] Artikel entfernen");
            System.out.println("[C] Checkout");
            System.out.println("[Z] Zurück");
            System.out.print("> ");

            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a" -> artikelAnzeigen();
                case "h" -> artikelZumWarenkorbHinzufuegen();
                case "w" -> warenkorbAnzeigen();
                case "m" -> mengeAendern();
                case "r" -> artikelAusWarenkorbEntfernen();
                case "c" -> checkout();
                case "z" -> running = false;
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void artikelAnzeigen() {
        System.out.println("\n--- Verfügbare Artikel ---");
        var artikel = eshop.getArtikelVerwaltung().getArtikelListe().getArtikelImLager();

        if (artikel.isEmpty()) {
            System.out.println("Keine Artikel vorhanden!");
            return;
        }

        for (Artikel a : artikel) {
            System.out.printf("[%d] %s - Preis: %.2f€ - Bestand: %d%n",
                    a.getArtikelNummer(), a.getBezeichnung(), a.getPreis(), a.getBestand());
        }
    }

    private void artikelZumWarenkorbHinzufuegen() {
        artikelAnzeigen();
        System.out.print("\nArtikel-Nummer eingeben: ");

        try {
            int artikelNr = Integer.parseInt(scanner.nextLine().trim());
            Artikel artikel = eshop.getArtikelVerwaltung().findeArtikel(artikelNr);

            if (artikel == null) {
                System.out.println("Artikel nicht gefunden!");
                return;
            }

            System.out.print("Menge eingeben: ");
            int menge = Integer.parseInt(scanner.nextLine().trim());

            if (menge <= 0) {
                System.out.println("Menge muss größer als 0 sein!");
                return;
            }

            if (menge > artikel.getBestand()) {
                System.out.println("Nicht genug Bestand! Verfügbar: " + artikel.getBestand());
                return;
            }

            int aktueleMenge = warenkorb.getMenge(artikel);
            warenkorb.speichern(artikel, aktueleMenge + menge);
            System.out.println( menge + "x " + artikel.getBezeichnung() + " zum Warenkorb hinzugefügt!");

        } catch (NumberFormatException | ArtikelNichtGefunden e) {
            System.out.println("Ungültige Eingabe!");
        }
    }

    private void warenkorbAnzeigen() {
        HashMap<Artikel, Integer> items = warenkorb.getAlleArtikel();

        if (items.isEmpty()) {
            System.out.println("\nWarenkorb ist leer!");
            return;
        }

        System.out.println("\n--- Warenkorb ---");

        for (Artikel artikel : items.keySet()) {
            int menge = items.get(artikel);
            double gesamtpreis = artikel.getPreis() * menge;
            System.out.printf("%d x %s - %.2f€ (je %.2f€)%n",
                    menge, artikel.getBezeichnung(), gesamtpreis, artikel.getPreis());
        }


        double summeNetto = eshop.getBestellVerwaltungV().berechneNettoSumme(warenkorb);
        double mwst = summeNetto * 0.19;
        double brutto = summeNetto + mwst;

        System.out.println("---");
        System.out.printf("Netto:  %.2f€%n", summeNetto);
        System.out.printf("MwSt:   %.2f€%n", mwst);
        System.out.printf("Brutto: %.2f€%n", brutto);
    }

    private void mengeAendern() {
        if (warenkorb.istLeer()) {
            System.out.println("\nWarenkorb ist leer!");
            return;
        }

        warenkorbAnzeigen();
        System.out.print("\nArtikel-Nummer eingeben: ");
        try {
            int artikelNr = Integer.parseInt(scanner.nextLine().trim());
            Artikel artikel = eshop.getArtikelVerwaltung().findeArtikel(artikelNr);

            if (artikel == null || warenkorb.getMenge(artikel) == 0) {
                System.out.println("Artikel nicht im Warenkorb!");
                return;
            }

            System.out.print("Neue Menge eingeben: ");
            int neueMenge = Integer.parseInt(scanner.nextLine().trim());

            if (neueMenge <= 0) {
                System.out.println("Menge muss größer als 0 sein!");
                return;
            }

            warenkorb.speichern(artikel, neueMenge);
            System.out.println(" Menge geändert!");

        } catch (NumberFormatException | ArtikelNichtGefunden e) {
            System.out.println("Ungültige Eingabe!");
        }
    }

    private void artikelAusWarenkorbEntfernen() {
        if (warenkorb.istLeer()) {
            System.out.println("\nWarenkorb ist leer!");
            return;
        }

        warenkorbAnzeigen();
        System.out.print("\nArtikel-Nummer eingeben: ");
        try {
            int artikelNr = Integer.parseInt(scanner.nextLine().trim());
            Artikel artikel = eshop.getArtikelVerwaltung().findeArtikel(artikelNr);

            if (artikel == null || warenkorb.getMenge(artikel) == 0) {
                System.out.println("Artikel nicht im Warenkorb!");
                return;
            }

            warenkorb.artikelEntfernen(artikel);
            System.out.println( artikel.getBezeichnung() + " entfernt!");

        } catch (NumberFormatException | ArtikelNichtGefunden e) {
            System.out.println("Ungültige Eingabe!");
        }
    }

    private void checkout() {
        if (warenkorb.istLeer()) {
            System.out.println("\nWarenkorb ist leer!");
            return;
        }

        if (!(session.getBenutzer() instanceof Kunde)) {
            System.out.println("Sie müssen als Kunde angemeldet sein!");
            return;
        }

        Kunde kunde = (Kunde) session.getBenutzer();
        Rechnung rechnung = null;
        // Übergeben die ArtikelVerwaltung damit Bestand reduziert werden kann
        try {
            rechnung = eshop.getBestellVerwaltungV().checkOut(kunde, warenkorb, eshop.getArtikelVerwaltung());
        } catch (ArtikelNichtGefunden e) {
            System.out.println("Artikel nicht gefunden!");
        }
        if (rechnung != null) {
            eshop.getBestellVerwaltungV().rechnungAnzeigen(rechnung);
            System.out.println("Bestellung erfolgreich abgeschlossen!");
        }
    }

    public void bestellverlauf() {
        System.out.println("------Bestellverlauf------");
    } // ----- Bestandshistorie -----
    public Map<LocalDate, Integer> getBestandsHistorie(int artikelNr) throws ArtikelNichtGefunden {
        // Artikel holen über die vorhandene ArtikelVerwaltung im Eshop
        Artikel artikel = eshop.getArtikelVerwaltung().findeArtikel(artikelNr);

        int aktuellerBestand = artikel.getBestand();
        LocalDate heute = LocalDate.now();
        LocalDateTime vor30Tagen = LocalDateTime.now().minusDays(30);

        List<Ereignis> relevantEreignisse = ereignisVerwaltung.getEreignisseFuerArtikel(artikelNr)
                .stream()
                .filter(e -> e.getZeitstempel().isAfter(vor30Tagen))
                .sorted(Comparator.comparing(Ereignis::getZeitstempel))
                .collect(Collectors.toList());

        // 1) Rückwärts rekonstruieren (Startbestand vor ersten relevanten Ereignis)
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

        // 2) Vorwärts: Tagesweise Bestand anwenden und speichern
        Map<LocalDate, Integer> historie = new LinkedHashMap<>();
        LocalDate startDatum = heute.minusDays(29); // 30 Tage inkl. heute
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

            historie.put(datum, bestandRekonstruiert);
        }

        return historie;
    }

    /** Hilfs-Methode: formatiert Ausgabe für CUI, fängt Fehler ab. */
    public void zeigeBestandsHistorie(int artikelNr) {
        try {
            Map<LocalDate, Integer> historie = getBestandsHistorie(artikelNr);
            Artikel artikel = eshop.getArtikelVerwaltung().findeArtikel(artikelNr);

            System.out.println("\n==================== BESTANDSHISTORIE ====================");
            System.out.println("Artikel: " + artikel.getBezeichnung() + " (Nr. " + artikel.getArtikelNummer() + ")");
            System.out.println("========================================================");
            System.out.println("Datum          | Bestand am Tagesende");
            System.out.println("--------");

            historie.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.printf("%s | %d%n", entry.getKey(), entry.getValue()));

            System.out.println("========================================================\n");
        } catch (ArtikelNichtGefunden e) {
            System.out.println("Artikel nicht gefunden (Nr: " + artikelNr + ")");
        }
    }

}