package ui;

import logic.ArtikelVerwaltung;

import java.util.Scanner;

public class EShopCUI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //Hier Login oder User Creation Bildschirm erstellen

        System.out.println("Willkommen im E-Shop!");
        while (true) {
            System.out.println("Wählen Sie aus:");
            System.out.println("Warenkatalog: w");
            System.out.println("Bestellverlauf: b");
            System.out.println("Abmelden: a");
            System.out.println("Kundensupport: s");

            String input = scanner.nextLine();
            navigation(input);
        }
    }
    public static void navigation(String input) {
        //Wenn bestimmer Buchstabe eingegeben wurde, dann Stage
        //bzw. Klasse mit der jeweiligen Funktionalität öffnen.
        switch (input.toLowerCase()) {
            case "w" -> warenkatalog();
            case "b" -> bestellverlauf();
            case "a" -> abmelden();
            case "s" -> kundensupport();
            default -> {
                System.out.println("Unbekannter Befehl!");
            }
        }
    }

    public static void warenkatalog() {
        ArtikelVerwaltung mng = new ArtikelVerwaltung();
        mng.legeArtikelan(1, "Cola", 10, 2.00);
        mng.legeArtikelan(2, "Fanta", 3, 2.50);
        mng.legeArtikelan(3, "Sprite", 21, 1.99);
        mng.legeArtikelan(4, "Mexomix", 30, 1.59);
        mng.legeArtikelan(5, "Spezi", 40, 2.99);
        mng.legeArtikelan(1, "Cola", 10, 2.00);
    }
    public static void bestellverlauf() {
    }
    public static void abmelden() {
    }
    public static void kundensupport() {
    }
}
