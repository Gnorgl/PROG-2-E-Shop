package ui.navigation;

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
        System.out.println("----------------");
        System.out.println("Erstellen Sie einen Mitarbeiter!");
        System.out.println("----------------");
    }

    public void kundenKontoErstellen() {
        System.out.println("----------------");
        System.out.println("Erstellen Sie ein Benutzer-Konto!");
        System.out.println("----------------");
    }

    public void produktErstellen() {
        System.out.println("------Neues Produkt------");
    }

    public void orderVerlauf() {
        System.out.println("------Orderverlauf------");
    }




}
