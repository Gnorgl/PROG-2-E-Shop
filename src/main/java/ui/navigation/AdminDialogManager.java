package ui.navigation;

import entities.Benutzer;
import entities.Mitarbeiter;
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
            System.out.println("Fehler: Zugriff verweigert. Nur Mitarbeiter duerfen Konten erstellen!");
            return;
        }
        System.out.println("----------------");
        System.out.println("Erstellen Sie ein Mitarbeiter-Konto!");
        System.out.println("----------------");

        System.out.print("E-Mail: ");
        String email = scanner.nextLine().trim().toLowerCase();

        String emailMuster = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$";

        while(!email.matches(emailMuster) || eshop.getBenutzerVerwaltung().benutzerCheck(email) != null) {
            if (!email.matches(emailMuster)) {
                System.out.println("Fehler: Ungueltiges E-Mail-Format! (Erlaubt ist nur: name@domain.com)");
            } else {
                System.out.println("Fehler: Benutzer existiert bereits!");
            }
            System.out.print("E-Mail: ");
            email = scanner.nextLine().trim().toLowerCase();
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Nachname: ");
        String nachname = scanner.nextLine().trim();

        System.out.print("Vorname: ");
        String vorname = scanner.nextLine().trim();

        eshop.getBenutzerVerwaltung().getMitarbeiterVerwaltung().createNewMitarbeiter(email, password, nachname, vorname);

        System.out.println("----------------");
        System.out.println("Neuen Mitarbeiter erfolgreich erstellt!");
        System.out.println("----------------");

        Benutzer benutzer = eshop.getBenutzerVerwaltung().benutzerCheck(email);
        session.login(benutzer);
    }

    public void produktErstellen() {
        System.out.println("------Neues Produkt------");
    }

    public void produktLoeschen() {
        System.out.println("------Produkt löschen------");
    }

    public void orderVerlauf() {
        System.out.println("------Orderverlauf------");
    }




}
