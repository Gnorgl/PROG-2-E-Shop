package ui.cui;

import entities.*;
import logic.SessionManager;
import ui.cui.navigation.*;
import logic.Eshop;


import java.io.IOException;
import java.util.Scanner;

public class EshopCUI {

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

    //Konstruktor
    public EshopCUI(Eshop eshop) throws IOException {

        //Management Klassen:
        this.generalServiceManager = new GeneralServiceManager(eshop, scanner, session);
        this.registrationManager = new RegistrationManager(eshop, scanner, session);
        this.loginLogoutManager = new LoginLogoutManager(eshop, scanner, session);
        this.shoppingServiceManager = new ShoppingServiceManager(eshop, scanner, session);
        this.adminDialogManager = new AdminDialogManager(eshop, scanner, session);
    }

    public void start() {

        System.out.println("Willkommen im E-Shop!");

        while (running) {
            menu();
            String input = scanner.nextLine().trim();

            while (input.isEmpty() && running) {
                System.out.print("> ");
                input = scanner.nextLine().trim();
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
                System.out.println("[D] Produkt löschen");
                System.out.println("[I] Orderverlauf einsehen");
                System.out.println("[H] Bestandshistorie (letzte 30 Tage)");
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
                if (session.getBenutzer() == null) {
                    loginLogoutManager.login();
                    return; //Sonst läuft es weiter zum nächsten Switch.
                }
            }
            case "e" -> {
                programmBeenden();
                return;
            }
            case "r" -> {
                registrationManager.kundenKontoErstellen();
                return;
            }
        }

        //Für angemeldete Benutzer
        //Sicherheitscheck hier
        if (session.getBenutzer() == null) {
            System.out.println("----------------");
            System.out.println("Für diese Funktion müssen Sie angemeldet sein!");
            System.out.println("----------------");
            return;
        }

        switch (session.getBenutzer()) {
            //Für Mitarbeiter-Befehle
            case Mitarbeiter mitarbeiter -> {
                switch (eingabe) {
                    case "p" -> adminDialogManager.produktErstellen();
                    case "d" -> adminDialogManager.produktLoeschen();
                    case "i" -> adminDialogManager.orderVerlauf();
                    case "h" -> bestandshistorieAbfragen();
                    //case "m" -> adminDialogManager.mitarbeiterKontoErstellen();
                    case "w" -> shoppingServiceManager.warenkatalog();
                    case "a" -> loginLogoutManager.logout();
                    default -> unbekannterBefehl();
                }
            }
            //Für Kunden-Befehle
            case Kunde kunde -> {
                switch (eingabe) {
                    case "b" -> shoppingServiceManager.bestellverlauf();
                    case "s" -> generalServiceManager.kundensupport();
                    case "w" -> shoppingServiceManager.warenkatalog();
                    case "a" -> loginLogoutManager.logout();
                    default -> unbekannterBefehl();
                }
            }
            default -> unbekannterBefehl();
        }
    }

    private void bestandshistorieAbfragen() {
        System.out.print("Artikel-Nummer: ");
        try {
            int artikelNr = Integer.parseInt(scanner.nextLine().trim());
            shoppingServiceManager.zeigeBestandsHistorie(artikelNr);
        } catch (NumberFormatException e) {
            System.out.println("Fehler: Ungültige Artikel-Nummer!");
        }
    }

    private void unbekannterBefehl() {
        System.out.println("----------------");
        System.out.println("Unbekannter Befehl!");
        System.out.println("----------------");
    }

    private void programmBeenden() {
        System.out.println("----------------");
        System.out.println("Programm wird beendet!");
        System.out.println("----------------");
        this.running = false;
    }

    //Main-Methode
    public static void main(String[] args) {
        try {
            Eshop eshop = new Eshop();
            EshopCUI eShopCUI = new EshopCUI(eshop);
            eShopCUI.start();
        } catch (IOException e) {
            System.out.println("----------------");
            System.out.println("Der Eshop konnte nicht gestartet werden.");
            System.out.println("Die gespeicherten Daten konnten nicht geladen werden. Bitte prüfe die JSON-Dateien.");
            System.out.println("----------------");
        }
    }
}

//Methoden in CUI: start(), menu(), navigation(), programmBeenden(), main().

