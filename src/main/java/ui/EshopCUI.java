package ui;

import logic.Eshop;


import java.util.Scanner;

public class EshopCUI {

    private final Eshop eshop;
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
        System.out.println("Wählen Sie aus:");
        System.out.println("Warenkatalog: w");
        System.out.println("Bestellverlauf: b");
        System.out.println("Abmelden: a");
        System.out.println("Kundensupport: s");
        System.out.println("Beenden: e");
    }

    private void navigation(String input) {
        //Wenn bestimmer Buchstabe eingegeben wurde, dann Stage
        //bzw. Klasse mit der jeweiligen Funktionalität öffnen.
        switch (input.toLowerCase()) {
            case "w" -> warenkatalog();
            case "b" -> bestellverlauf();
            case "a" -> abmelden();
            case "s" -> kundensupport();
            case "e" -> programmBeenden();
            default -> {
                System.out.println("Unbekannter Befehl!");
            }
        }
    }

    private void warenkatalog() {
        System.out.println("Warenkatalog:");
    }

    private void bestellverlauf() {
        System.out.println("Bestellverlauf:");
    }

    private void abmelden() {
        System.out.println("Sie wurden abgemeldet!");
    }

    private void kundensupport() {
        System.out.println("Kontaktmöglichkeiten:");
    }

    private void programmBeenden() {
        System.out.println("Programm wird beendet!");
        this.running = false;
    }

    //Main-Methode
    public static void main(String[] args) {
        Eshop eshop = new Eshop();
        EshopCUI eShopCUI = new EshopCUI(eshop);
        eShopCUI.start();

        //Nach dem Anmeldungsprozess muss ein
    }
}


