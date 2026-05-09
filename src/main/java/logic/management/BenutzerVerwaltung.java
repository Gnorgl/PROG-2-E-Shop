package logic.management;

import entities.Benutzer;
import logic.moduls.IBV;

public class BenutzerVerwaltung implements IBV {
    private KundenVerwaltung kundenVerwaltung = new KundenVerwaltung();
    private MitarbeiterVerwaltung mitarbeiterVerwaltung = new MitarbeiterVerwaltung();

    public BenutzerVerwaltung() {}

    public KundenVerwaltung getKundenVerwaltung() {
        return kundenVerwaltung;
    }

    public MitarbeiterVerwaltung getMitarbeiterVerwaltung() {
        return mitarbeiterVerwaltung;
    }

    //Methode wird Benutzer übergeben, ruft dann getter Methoden für username und password auf
    @Override
    public boolean login(Benutzer benutzer) {
        if (this.kundenVerwaltung.getKundenListe().getKunden().containsKey(benutzer.getVorname())) { //gut verschachtelt
            return this.kundenVerwaltung.getKundenListe().getKunden().get(benutzer.getVorname()).getPasswort().equals(benutzer.getPasswort());
        } else if (this.mitarbeiterVerwaltung.getMitarbeiterListe().getMitarbeiter().containsKey(benutzer.getVorname())) {
            return this.mitarbeiterVerwaltung.getMitarbeiterListe().getMitarbeiter().get(benutzer.getVorname()).getPasswort().equals(benutzer.getPasswort());
        }
        return false;
    }
}

//Button im UI für mitarbeiter erstellung oder kundenerstellung
//Kunden können selber konto erstellen, nur angemeldete mitarbeiter können andere mitarbeiter erstellen
//Methoden für user creation jeweils in kundenverwaltung und mitarbeiterverwaltung!

//Funktionen für Passwort Abgleich, möglicherweise Erstellung von Kunden und Mitarbeitern
//Vlt extra für Kunde und Mitarbeiter eine Verwaltungsklasse erstellung und
//PasswordManager Klasse für Abgleich von Passwörtern bei User Input im AnmeldeFeld

//Exception Klassen erstellen
