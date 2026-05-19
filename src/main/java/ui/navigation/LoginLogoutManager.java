package ui.navigation;

import entities.Benutzer;
import logic.Eshop;

import java.util.Scanner;

public class LoginLogoutManager {
    private final Eshop eshop;
    private final Scanner scanner;
    private final SessionManager session;

    public LoginLogoutManager(Eshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
    }

    public void login() {
        System.out.println("------Login------");
        System.out.print("E-Mail: ");

        String email = scanner.nextLine().trim().toLowerCase();

        Benutzer benutzer = eshop.getBenutzerVerwaltung().benutzerCheck(email);
        //Technisch gesehen hier ein Loop, falls man sich verschreibt.
        //Aber sonst muss es mehrere Eingaben geben damit man zum Anfang zurückkommt.
        //Dies wird implementiert, wenn wir Buttons im GUI benutzen.
        if (benutzer != null) {
            System.out.print("Passwort: ");
            String passwort = scanner.nextLine(); //Darf nicht empty sein!

            if (eshop.getBenutzerVerwaltung().passwordCheck(benutzer, passwort)) {
                session.login(benutzer);
            } else {
                System.out.println("----------------");
                System.out.println("Falsches Passwort!");
                System.out.println("----------------");
            }
        } else {
            System.out.println("----------------");
            System.out.println("Benutzer nicht gefunden!");
            System.out.println("----------------");
            return;
            //Exception was bei NullPointerException passiert
        }
        System.out.format("Willkommen im Eshop %s (%s)\n",
                session.getBenutzer().getVorname(),
                session.istBenutzerEinMitarbeiter() ? "Mitarbeiter" : "Kunde");
    }

    public void logout() {
        session.logout();
        System.out.println("----------------");
        System.out.println("Sie wurden abgemeldet!");
        System.out.println("----------------");
    }

}
