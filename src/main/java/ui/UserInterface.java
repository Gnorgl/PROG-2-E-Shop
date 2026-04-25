package ui;

import java.util.Scanner;

public class UserInterface {
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
        switch (input) {
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
    }
    public static void bestellverlauf() {
    }
    public static void abmelden() {
    }
    public static void kundensupport() {
    }
}
