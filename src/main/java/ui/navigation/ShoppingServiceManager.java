package ui.navigation;

import logic.Eshop;

import java.util.Scanner;
import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import logic.Eshop;
import persistence.shop.WarenkorbListe;
import logic.verwaltung.CheckOutVerwaltung;
import java.util.HashMap;

public class ShoppingServiceManager {
    private final Eshop eshop;
    private final Scanner scanner;
    private final SessionManager session;
    private final WarenkorbListe warenkorb = new WarenkorbListe();




    public ShoppingServiceManager(Eshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
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
            // Nutzt jetzt die Logik-Klasse zum Finden
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

        } catch (NumberFormatException e) {
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

        // Nutzt jetzt die Logik-Klasse für die Mathematik
        // RICHTIG: Wir fragen die Logik-Klasse (CheckOutVerwaltung) nach der Berechnung
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

        } catch (NumberFormatException e) {
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

            // Nutzt jetzt die saubere Methode zum Löschen
            warenkorb.artikelEntfernen(artikel);
            System.out.println( artikel.getBezeichnung() + " entfernt!");

        } catch (NumberFormatException e) {
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

        // Übergeben die ArtikelVerwaltung damit Bestand reduziert werden kann
        Rechnung rechnung = eshop.getBestellVerwaltungV().checkOut(kunde, warenkorb, eshop.getArtikelVerwaltung());

        if (rechnung != null) {
            eshop.getBestellVerwaltungV().rechnungAnzeigen(rechnung);
            System.out.println("Bestellung erfolgreich abgeschlossen!");
        }
    }

    public void bestellverlauf() {
        System.out.println("------Bestellverlauf------");
    }
}
