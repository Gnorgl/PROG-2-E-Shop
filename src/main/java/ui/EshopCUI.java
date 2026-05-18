package ui;

import entities.*;
import ui.navigation.*;
import logic.Eshop;


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
    private final WindowManager windowManager; //Für die Erstellung und Verwaltung der Anwendungsfenster im GUI.

    //Konstruktor
    public EshopCUI(Eshop eshop) {

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
                    case "p" -> adminDialogManager.produktErstellen();
                    case "d" -> adminDialogManager.produktLoeschen();
                    case "i" -> adminDialogManager.orderVerlauf();
                    case "m" -> adminDialogManager.mitarbeiterKontoErstellen();
                }
            }
            //Für Kunden-Befehle
            case Kunde kunde -> {
                switch (eingabe) {
                    case "b" -> shoppingServiceManager.bestellverlauf();
                    case "s" -> generalServiceManager.kundensupport();
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
            case "w" -> shoppingServiceManager.warenkatalog();
            case "a" -> loginLogoutManager.logout();

            default -> {
                System.out.println("----------------");
                System.out.println("Unbekannter Befehl!");
                System.out.println("----------------");
            }
        }
    }

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

