package ui.navigation;

import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;
import exceptions.user.EmailBereitsVergebenException;
import logic.Eshop;

import java.util.Scanner;

public class RegistrationManager {
    private final Eshop eshop;
    private final Scanner scanner;
    private final SessionManager session;

    public RegistrationManager(Eshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
    }

    public void kundenKontoErstellen() {
        System.out.println("----------------");
        System.out.println("Erstellen Sie ein Benutzer-Konto!");
        System.out.println("----------------");

        String email = "";
        String emailMuster = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$";
        boolean emailGueltig = false;

        // Schleife läuft so lange, bis das Format stimmt UND die E-Mail frei ist
        while (!emailGueltig) {
            System.out.print("E-Mail: ");
            email = scanner.nextLine().trim().toLowerCase();

            if (!email.matches(emailMuster)) {
                System.out.println("Fehler: Ungültiges E-Mail-Format! (Erlaubt ist nur: name@domain.com)");
                continue;
            }

            try {
                // Wenn benutzerCheck KEINE Exception wirft, existiert der Benutzer bereits!
                eshop.getBenutzerVerwaltung().benutzerCheck(email);
                System.out.println("Fehler: Benutzer existiert bereits!");
            } catch (BenutzerExistiertNichtException e) {
                //E-Mail ist noch frei!
                emailGueltig = true;
            }
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Nachname: ");
        String nachname = scanner.nextLine().trim();

        System.out.print("Vorname: ");
        String vorname = scanner.nextLine().trim();

        System.out.print("Adresse: ");
        String adresse = scanner.nextLine().trim();

        try {
            eshop.getBenutzerVerwaltung().getKundenVerwaltung().createNewKunden(email, password, nachname, vorname, adresse);

            System.out.println("----------------");
            System.out.println("Neues Kundenkonto erfolgreich erstellt!");
            System.out.println("----------------");


            Benutzer benutzer = eshop.getBenutzerVerwaltung().benutzerCheck(email);
            session.login(benutzer);

        } catch (EmailBereitsVergebenException e) {
            System.out.println("Fehler bei der Registrierung: " + e.getMessage());
        } catch (BenutzerExistiertNichtException e) {
            // Dieser Fall ist rein theoretisch, da wir den Benutzer gerade erstellt haben
            System.out.println("Systemfehler.");
        }
    }

}
