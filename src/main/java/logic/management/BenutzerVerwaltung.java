package logic.management;

import logic.moduls.IBV;

public class BenutzerVerwaltung implements IBV {

    @Override
    public boolean login(String username, String password) {
        return true;
    }
}

//Funktionen für Passwort Abgleich, möglicherweise Erstellung von Kunden und Mitarbeitern
//Vlt extra für Kunde und Mitarbeiter eine Verwaltungsklasse erstellung und
//PasswordManager Klasse für Abgleich von Passwörtern bei User Input im AnmeldeFeld

//Exception Klassen erstellen
