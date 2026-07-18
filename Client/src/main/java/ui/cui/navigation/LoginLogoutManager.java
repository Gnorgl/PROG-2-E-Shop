package ui.cui.navigation;

import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;
import interfaces.InterfaceEshop;
import ui.gui.SessionManager;

import java.util.Scanner;

public class LoginLogoutManager {
    private final InterfaceEshop eshop;
    private final Scanner scanner;
    private final SessionManager session;

    public LoginLogoutManager(InterfaceEshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
    }

    public void login() {
        System.out.println("------Login------");
        System.out.print("E-Mail: ");
        String email = scanner.nextLine().trim().toLowerCase();

        try {
            // Wenn der Benutzer nicht existiert
            Benutzer benutzer = eshop.benutzerCheck(email);

            System.out.print("Passwort: ");
            String passwort = scanner.nextLine();

            //Mit Loop gegebenenfalls damit man neu eingeben kann → GUI

            if (eshop.passwordCheck(benutzer, passwort)) {
                session.login(benutzer);

                System.out.format("Willkommen im Eshop %s (%s)\n",
                        session.getBenutzer().getVorname(),
                        session.istBenutzerEinMitarbeiter() ? "Mitarbeiter" : "Kunde");
            } else {
                System.out.println("----------------");
                System.out.println("Falsches Passwort!");
                System.out.println("----------------");
            }

        } catch (BenutzerExistiertNichtException e) {
            System.out.println("----------------");
            System.out.println("Fehler: " + e.getMessage());
            System.out.println("----------------");
        } catch (IllegalArgumentException e) {
            // Ungültiges Password hier dann
            System.out.println("Fehler bei der Eingabe: " + e.getMessage());
        }
    }

    public void logout() {
        session.logout();
        System.out.println("----------------");
        System.out.println("Sie wurden abgemeldet!");
        System.out.println("----------------");
    }

}
