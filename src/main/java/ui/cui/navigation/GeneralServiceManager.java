package ui.cui.navigation;

import logic.Eshop;
import logic.SessionManager;

import java.util.Scanner;

public class GeneralServiceManager {
    private final Eshop eshop;
    private final Scanner scanner;
    private final SessionManager session;

    public GeneralServiceManager(Eshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
    }

    public void kundensupport() {
        System.out.println("------Kontaktmöglichkeiten------");
        System.out.println("Live-Chat");
        System.out.println("Telefon-Nummer");
    }
}
