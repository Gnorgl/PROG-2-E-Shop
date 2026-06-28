package ui.cui.navigation;


import exceptions.artikel.ArtikelNichtGefunden;
import logic.Eshop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import entities.Artikel;
import entities.Ereignis;
import entities.Kunde;
import entities.Massengutartikel;
import entities.Rechnung;
import logic.SessionManager;
import logic.verwaltung.EreignisVerwaltung;
import logic.verwaltung.ArtikelVerwaltung;
import persistence.shop.WarenkorbListe;
import java.util.HashMap;
import logic.verwaltung.WarenkorbVerwaltung;

public class ShoppingServiceManager {
    private final Eshop eshop;
    private final EreignisVerwaltung ereignisVerwaltung;
    private final Scanner scanner;
    private final SessionManager session;
    private final WarenkorbVerwaltung warenkorbVerwaltung;
    private final WarenkorbListe warenkorb;

    public ShoppingServiceManager(Eshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
        this.ereignisVerwaltung = eshop.getEreignisVerwaltung();
        this.warenkorbVerwaltung = new WarenkorbVerwaltung(eshop.getArtikelVerwaltung());
        this.warenkorb = warenkorbVerwaltung.getWarenkorbListe();
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
            if (a instanceof Massengutartikel ma) {
                System.out.printf("[%d] %s - %.2f€ pro %der-Pack - Bestand: %d Stück%n",
                        a.getArtikelNummer(), a.getBezeichnung(), a.getPreis(), ma.getPackungsGroesse(), a.getBestand());
            } else {
                System.out.printf("[%d] %s - Preis: %.2f€ - Bestand: %d%n",
                        a.getArtikelNummer(), a.getBezeichnung(), a.getPreis(), a.getBestand());
            }
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

            if (artikel instanceof Massengutartikel ma) {
                System.out.printf("Menge eingeben (Vielfaches von %d): ", ma.getPackungsGroesse());
            } else {
                System.out.print("Menge eingeben: ");
            }
            int menge = Integer.parseInt(scanner.nextLine().trim());

            if (menge <= 0) {
                System.out.println("Menge muss größer als 0 sein!");
                return;
            }

            if (!artikel.istMengeGueltig(menge)) {
                int pg = ((Massengutartikel) artikel).getPackungsGroesse();
                System.out.println("Ungültige Menge! Bei Massengutartikeln nur Vielfache von " + pg + " erlaubt.");
                return;
            }

            if (menge > artikel.getBestand()) {
                System.out.println("Nicht genug Bestand! Verfügbar: " + artikel.getBestand());
                return;
            }

            int aktueleMenge = warenkorb.getMenge(artikel);
            warenkorbVerwaltung.artikelHinzufuegen(artikel, aktueleMenge + menge);
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
            double gesamtpreis = artikel.berechneGesamtpreis(menge);
            if (artikel instanceof Massengutartikel ma) {
                System.out.printf("%d x %s - %.2f€ (%.2f€ pro %der-Pack)%n",
                        menge, artikel.getBezeichnung(), gesamtpreis, artikel.getPreis(), ma.getPackungsGroesse());
            } else {
                System.out.printf("%d x %s - %.2f€ (je %.2f€)%n",
                        menge, artikel.getBezeichnung(), gesamtpreis, artikel.getPreis());
            }
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

            if (artikel instanceof Massengutartikel ma) {
                System.out.printf("Neue Menge eingeben (Vielfaches von %d): ", ma.getPackungsGroesse());
            } else {
                System.out.print("Neue Menge eingeben: ");
            }
            int neueMenge = Integer.parseInt(scanner.nextLine().trim());

            if (neueMenge <= 0) {
                System.out.println("Menge muss größer als 0 sein!");
                return;
            }

            if (!artikel.istMengeGueltig(neueMenge)) {
                int pg = ((Massengutartikel) artikel).getPackungsGroesse();
                System.out.println("Ungültige Menge! Bei Massengutartikeln nur Vielfache von " + pg + " erlaubt.");
                return;
            }

            warenkorbVerwaltung.artikelHinzufuegen(artikel, neueMenge);
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

            warenkorbVerwaltung.artikelEntfernen(artikel);
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
            warenkorbVerwaltung.safe();
            System.out.println("Bestellung erfolgreich abgeschlossen!");
        }
    }

    // eventuell raus damit
    private void rechnungAnzeigen(Rechnung rechnung) {
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
    }


    // Zeigt alle vergangenen Bestellungen des eingeloggten Kunden an
    public void bestellverlauf() {
        // Sicherheitscheck: nur Kunden dürfen diese Funktion nutzen
        if (!(session.getBenutzer() instanceof Kunde kunde)) {
            System.out.println("Fehler: Nur Kunden haben einen Bestellverlauf!");
            return;
        }

        // Alle gespeicherten Rechnungen dieses Kunden aus der CheckOutVerwaltung holen
        List<Rechnung> rechnungen = eshop.getBestellVerwaltungV().getRechnungenFuerKunde(kunde);

        if (rechnungen.isEmpty()) {
            System.out.println("\nKeine Bestellungen vorhanden.");
            return;
        }

        System.out.println("\n========== BESTELLVERLAUF ==========");
        System.out.println("Kunde: " + kunde.getNachname() + ", " + kunde.getVorname());
        System.out.println("====================================");

        // Jede Rechnung einzeln ausgeben
        for (Rechnung r : rechnungen) {
            System.out.println("\nRechnung #" + r.getRechnungsNummer() + " vom " + r.getDatum());
            System.out.println("---");

            // Hilfsliste verhindert, dass derselbe Artikel mehrfach gedruckt wird
            List<Artikel> schonGedruckt = new ArrayList<>();
            for (Artikel a : r.getArtikel()) {
                if (!schonGedruckt.contains(a)) {
                    // frequency zählt wie oft dieser Artikel in der Rechnungsliste vorkommt = gekaufte Menge
                    int menge = java.util.Collections.frequency(r.getArtikel(), a);
                    // berechneGesamtpreis berücksichtigt bei Massengutartikeln den Packungspreis
                    System.out.printf("  %dx %s - %.2f€%n", menge, a.getBezeichnung(), a.berechneGesamtpreis(menge));
                    schonGedruckt.add(a);
                }
            }

            System.out.printf("  Gesamt (Brutto): %.2f€%n", r.getBruttoSumme());
        }

        System.out.println("====================================\n");
    }

    // ----- Bestandshistorie -----

    // Gibt die Bestandshistorie formatiert auf der Konsole aus (fängt Fehler ab)
    public void zeigeBestandsHistorie(int artikelNr) {
        try {
            // Berechnung liegt in ArtikelVerwaltung, hier nur noch Ausgabe
            Map<LocalDate, Integer> historie = eshop.getArtikelVerwaltung().getBestandsHistorie(artikelNr);
            Artikel artikel = eshop.getArtikelVerwaltung().findeArtikel(artikelNr);

            System.out.println("\n==================== BESTANDSHISTORIE ====================");
            System.out.println("Artikel: " + artikel.getBezeichnung() + " (Nr. " + artikel.getArtikelNummer() + ")");
            System.out.println("========================================================");
            System.out.println("Datum          | Bestand am Tagesende");
            System.out.println("--------");

            // Einträge nach Datum sortiert ausgeben
            historie.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.printf("%s | %d%n", entry.getKey(), entry.getValue()));

            System.out.println("========================================================\n");
        } catch (ArtikelNichtGefunden e) {
            System.out.println("Artikel nicht gefunden (Nr: " + artikelNr + ")");
        }
    }

}