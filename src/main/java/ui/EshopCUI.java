package ui;

import entities.*;
import ui.navigation.*;
import logic.Eshop;


import java.util.Scanner;

public class EshopCUI {

    private final Eshop eshop;
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    //SessionManager
    private final SessionManager session = new SessionManager();

    //Management Klassen:
    private final GeneralServiceManager generalServiceManager; //Alle service Methoden, wie Support.
    private final RegistrationManager registrationManager; //Für Erstellung von Benutzern.
    private final LoginLogoutManager loginLogoutManager; //Für den An- und Abmeldeprozess.
    private final ShoppingServiceManager shoppingServiceManager; //Alle shopping Methoden.
    private final AdminDialogManager adminDialogManager; //Alle Admin Fähigkeiten-Methoden.
    private final WindowManager windowManager; //Für die Erstellung und Verwaltung der Anwendungsfenster im GUI.

    //Konstruktor
    public EshopCUI(Eshop eshop) {
        this.eshop = eshop;

        //Management Klassen:
        this.generalServiceManager = new GeneralServiceManager(eshop, scanner, session);
        this.registrationManager = new RegistrationManager(eshop, scanner, session);
        this.loginLogoutManager = new LoginLogoutManager(eshop, scanner, session);
        this.shoppingServiceManager = new ShoppingServiceManager(eshop, scanner, session);
        this.adminDialogManager = new AdminDialogManager(eshop, scanner, session);
        this.windowManager = new WindowManager(eshop, scanner, session);
    }

    public void start() {

        System.out.println("Willkommen im E-Shop!");

        while (running) {
            menu();
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return;
            }
            navigation(input);
        }
        scanner.close();
    }

    private void menu() {
        //Nicht angemeldete Benutzer:
        if (session.getBenutzer() == null) {
            System.out.println("[L] Login");
            System.out.println("[R] Registrieren");
            System.out.println("[E] Beenden");
        } else {
            //Angemeldete Benutzer:
            System.out.println("-----------------");
            System.out.println("------Eshop------");
            System.out.println("-----------------");
            System.out.println("[W] Warenkatalog");
            //Benutzer ist Mitarbeiter:
            if (session.istBenutzerEinMitarbeiter()) {
                System.out.println("[P] Produkt hinzufügen");
                System.out.println("[I] Orderverlauf einsehen");
                System.out.println("[M] Neuen Mitarbeiter erstellen");
            } else {
                //Benutzer ist Kunde:
                System.out.println("[B] Bestellverlauf");
                System.out.println("[S] Support");
            }
            System.out.println("[A] Abmelden");
        }
        System.out.print("> ");
    }

    private void navigation(String input) {
        //Navigation absichern

        String eingabe = input.toLowerCase();

        //Für nicht angemeldete Benutzer
        switch (eingabe) {
            case "l" -> {
                if (session.getBenutzer() == null) login(); return;
            }
            case "e" -> {
                programmBeenden(); return;
            }
            case "r" -> {
                kundenKontoErstellen(); return;
            }
        }

        //Für angemeldete Benutzer

        switch (session.getBenutzer()) {
            case null -> {
                System.out.println("----------------");
                System.out.println("Für diese Funktion müssen Sie angemeldet sein!");
                System.out.println("----------------");
                return;
            }
            //Für Mitarbeiter-Befehle
            case Mitarbeiter mitarbeiter -> {
                switch (eingabe) {
                    case "p" -> produktErstellen();
                    case "i" -> orderVerlauf();
                    case "m" -> mitarbeiterKontoErstellen();
                }
            }
            //Für Kunden-Befehle
            case entities.Kunde kunde -> {
                switch (eingabe) {
                    case "b" -> bestellverlauf();
                    case "s" -> kundensupport();
                }
            }
            default -> {
                System.out.println("----------------");
                System.out.println("Unbekannter Befehl!");
                System.out.println("----------------");
            }
        }
        //Für Benutzer-Befehle
        switch (eingabe) {
            case "w" -> warenkatalog();
            case "a" -> logout();

            default -> {
                System.out.println("----------------");
                System.out.println("Unbekannter Befehl!");
                System.out.println("----------------");
            }
        }
    }

    private void mitarbeiterKontoErstellen() {
        System.out.println("----------------");
        System.out.println("Erstellen Sie einen Mitarbeiter!");
        System.out.println("----------------");
    }

    private void kundenKontoErstellen() {
        System.out.println("----------------");
        System.out.println("Erstellen Sie ein Benutzer-Konto!");
        System.out.println("----------------");
    }

    private void produktErstellen() {
        System.out.println("------Neues Produkt------");
    }

    private void orderVerlauf() {
        System.out.println("------Orderverlauf------");
    }

    private void login() {
        System.out.println("------Login------");
        System.out.println("E-Mail:");

        String email = scanner.nextLine();

        Benutzer benutzer = eshop.getBenutzerVerwaltung().benutzerCheck(email);
        //Technisch gesehen hier ein Loop, falls man sich verschreibt.
        //Aber sonst muss es mehrere Eingaben geben damit man zum Anfang zurückkommt.
        //Dies wird implementiert, wenn wir Buttons im GUI benutzen.
        if (benutzer != null) {
            System.out.println("Passwort:");
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
        }
        System.out.format("Willkommen im Eshop %s (%s)\n",
                session.getBenutzer().getVorname(),
                session.istBenutzerEinMitarbeiter() ? "Mitarbeiter" : "Kunde");
    }

    private void logout() {
        session.logout();
        System.out.println("----------------");
        System.out.println("Sie wurden abgemeldet!");
        System.out.println("----------------");
    }

    private void warenkatalog() {
        System.out.println("------Warenkatalog------");
    }

    private void bestellverlauf() {
        System.out.println("------Bestellverlauf------");
    }

    private void kundensupport() {
        System.out.println("------Kontaktmöglichkeiten------");
    }

    //In CUI Klasse lassen
    private void programmBeenden() {
        System.out.println("----------------");
        System.out.println("Programm wird beendet!");
        System.out.println("----------------");
        this.running = false;
    }

    //Main-Methode
    public static void main(String[] args) {
        Eshop eshop = new Eshop();
        EshopCUI eShopCUI = new EshopCUI(eshop);
        eshop.getBenutzerVerwaltung().getMitarbeiterVerwaltung().createNewMitarbeiter("admin@email.com", "123", "AI", "Admin");
        eshop.getBenutzerVerwaltung().getKundenVerwaltung().createNewKunden("kunde@email.com", "123", "Mustermann", "Max", "Straße 123");
        eShopCUI.start();
    }
}

//Methoden in CUI: start(), menu(), navigation(), programmBeenden(), main().

