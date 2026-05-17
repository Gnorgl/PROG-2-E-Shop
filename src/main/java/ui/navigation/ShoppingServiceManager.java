package ui.navigation;

import logic.Eshop;

import java.util.Scanner;

public class ShoppingServiceManager {
    private final Eshop eshop;
    private final Scanner scanner;
    private final SessionManager session;

    public ShoppingServiceManager(Eshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
    }

    public void warenkatalog() {
        System.out.println("------Warenkatalog------");
    }

    public void bestellverlauf() {
        System.out.println("------Bestellverlauf------");
    }
}
