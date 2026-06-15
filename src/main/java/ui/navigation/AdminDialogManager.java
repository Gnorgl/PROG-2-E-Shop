package ui.navigation;

import entities.Benutzer;
import entities.Mitarbeiter;
import exceptions.artikel.MengeUngueltigException;
import exceptions.user.BenutzerExistiertNichtException;
import exceptions.user.EmailBereitsVergebenException;
import logic.Eshop;

import java.util.Scanner;

public class AdminDialogManager {
    private final Eshop eshop;
    private final Scanner scanner;
    private final SessionManager session;

    public AdminDialogManager(Eshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
    }

    public void mitarbeiterKontoErstellen() {
        if (session.getBenutzer() == null || !session.istBenutzerEinMitarbeiter()) {
            System.out.println("Fehler: Zugriff verweigert. Nur Mitarbeiter dürfen Konten erstellen!");
            return;
        }
        System.out.println("----------------");
        System.out.println("Erstellen Sie ein Mitarbeiter-Konto!");
        System.out.println("----------------");

        String email = "";
        String emailMuster = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$";
        boolean emailGueltig = false;

        while (!emailGueltig) {
            System.out.print("E-Mail: ");
            email = scanner.nextLine().trim().toLowerCase();

            if (!email.matches(emailMuster)) {
                System.out.println("Fehler: Ungültiges E-Mail-Format! (Erlaubt ist nur: name@domain.com)");
                continue;
            }

            try {
                eshop.getBenutzerVerwaltung().benutzerCheck(email);
                System.out.println("Fehler: Benutzer existiert bereits!");
            } catch (BenutzerExistiertNichtException e) {
                emailGueltig = true;
            }
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Nachname: ");
        String nachname = scanner.nextLine().trim();

        System.out.print("Vorname: ");
        String vorname = scanner.nextLine().trim();

        try {
            eshop.getBenutzerVerwaltung().getMitarbeiterVerwaltung().createNewMitarbeiter(email, password, nachname, vorname);

            System.out.println("----------------");
            System.out.println("Neuen Mitarbeiter erfolgreich erstellt!");
            System.out.println("----------------");

            Benutzer benutzer = eshop.getBenutzerVerwaltung().benutzerCheck(email);
            session.login(benutzer);

        } catch (EmailBereitsVergebenException e) {
            System.out.println("Fehler: " + e.getMessage());
        } catch (BenutzerExistiertNichtException e) {
            System.out.println("Systemfehler beim automatischen Login.");
        }
    }

    public void produktErstellen() {
        System.out.println("------Neues Produkt------");
        System.out.println("[1] Einzelartikel");
        System.out.println("[2] Massengutartikel");
        System.out.print("> ");
        String typWahl = scanner.nextLine().trim();

        boolean istMassengut = typWahl.equals("2");

        try {
            System.out.print("Artikel-Nummer: ");
            int nr = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Name: ");
            String name = scanner.nextLine().trim();

            int packungsGroesse = 1;
            if (istMassengut) {
                System.out.print("Packungsgröße (Vielfaches für Ein-/Auslagerung): ");
                packungsGroesse = Integer.parseInt(scanner.nextLine().trim());
                if (packungsGroesse <= 0) {
                    System.out.println("Fehler: Packungsgröße muss größer als 0 sein!");
                    return;
                }
            }

            System.out.print("Bestand: ");
            int bestand = Integer.parseInt(scanner.nextLine().trim());

            if (bestand <= 0) {
                System.out.println("Fehler: Bestand muss größer als 0 sein!");
                return;
            }

            if (istMassengut && bestand % packungsGroesse != 0) {
                System.out.println("Fehler: Bestand muss ein Vielfaches der Packungsgröße (" + packungsGroesse + ") sein!");
                return;
            }

            System.out.print("Preis: ");
            double preis = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));

            if (preis < 0) {
                System.out.println("Fehler: Preis kann nicht negativ sein!");
                return;
            }

            if (istMassengut) {
                eshop.getArtikelVerwaltung().legeMassengutartikelAn(nr, name, bestand, preis, packungsGroesse);
                System.out.println("------Massengutartikel erfolgreich erstellt (Packungsgröße: " + packungsGroesse + ")------");
            } else {
                eshop.getArtikelVerwaltung().legeArtikelAn(nr, name, bestand, preis);
                System.out.println("------Einzelartikel erfolgreich erstellt------");
            }
        } catch (NumberFormatException e) {
            System.out.println("Fehler: Ungültige Eingabe!");
        } catch (MengeUngueltigException e) {
            System.out.println("Fehler: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    public void produktLoeschen() {
        System.out.println("------Produkt löschen------");
        try {
            System.out.print("Artikel-Nummer: ");
            int nr = Integer.parseInt(scanner.nextLine().trim());
            eshop.getArtikelVerwaltung().loeschen(nr);
            System.out.println("------Produkt erfolgreich gelöscht------");
        } catch (NumberFormatException e) {
            System.out.println("Fehler: Ungültige Artikel-Nummer!");
        }
    }
    public void orderVerlauf() {
        System.out.println("------Orderverlauf------");
    }




}
