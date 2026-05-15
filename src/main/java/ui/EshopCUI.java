package ui;

import entities.Benutzer;
import entities.Mitarbeiter;
import logic.Eshop;


import java.util.Scanner;

public class EshopCUI {

    private final Eshop eshop;
    private Benutzer angemeldeterBenutzer = null; //Später festlegen, wenn Benutzer angemeldet ist.
    private boolean running = true;
    private final Scanner scanner = new Scanner(System.in);

    public EshopCUI(Eshop eshop) {
        this.eshop = eshop;
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
        if (this.angemeldeterBenutzer == null) {
            System.out.println("[L] Login");
            System.out.println("[R] Registrieren");
            System.out.println("[E] Beenden");
        } else {
            //Angemeldete Benutzer:
            System.out.println("----------------");
            System.out.format("Willkommen im Eshop %s (%s)\n",
                    angemeldeterBenutzer.getVorname(),
                    angemeldeterBenutzer instanceof entities.Mitarbeiter ? "Mitarbeiter" : "Kunde");
            System.out.println("----------------");
            System.out.println("[W] Warenkatalog");
            //Benutzer ist Mitarbeiter:
            if (angemeldeterBenutzer instanceof entities.Mitarbeiter) {
                System.out.println("[P] Produkt hinzufügen");
                System.out.println("[I] Orderverlauf einsehen");
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
        //Wenn bestimmer Buchstabe eingegeben wurde, dann Stage
        //bzw. Klasse mit der jeweiligen Funktionalität öffnen.
        switch (input.toLowerCase()) {
            case "l" -> login();
            case "r" -> benutzerErstellen();
            case "w" -> warenkatalog();
            case "b" -> bestellverlauf();
            case "a" -> logout();
            case "s" -> kundensupport();
            case "e" -> programmBeenden();
            default -> {
                System.out.println("----------------");
                System.out.println("Unbekannter Befehl!");
                System.out.println("----------------");
            }
        }
    }

    private void benutzerErstellen() {
        System.out.println("----------------");
        System.out.println("Erstellen Sie einen Benutzer!");
        System.out.println("----------------");
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
                this.angemeldeterBenutzer = benutzer;
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
    }

    private void logout() {
        this.angemeldeterBenutzer = null;
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
        eShopCUI.start();

    }
}


