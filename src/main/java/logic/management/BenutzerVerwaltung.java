package logic.management;

import entities.Benutzer;
import logic.moduls.IBV;

public class BenutzerVerwaltung implements IBV {
    private final KundenVerwaltung kundenVerwaltung = new KundenVerwaltung();
    private final MitarbeiterVerwaltung mitarbeiterVerwaltung = new MitarbeiterVerwaltung();

    public BenutzerVerwaltung() {}

    public KundenVerwaltung getKundenVerwaltung() {
        return kundenVerwaltung;
    }

    public MitarbeiterVerwaltung getMitarbeiterVerwaltung() {
        return mitarbeiterVerwaltung;
    }

    @Override
    public Benutzer userExists(String input) { //input ist entweder E-Mail oder benutzerName.
        if (this.kundenVerwaltung.getKundenListe().getKunden().containsKey(input)) {
            return this.kundenVerwaltung.getKundenListe().getKunden().get(input);
        }
        return null;
    }

    @Override
    public boolean login(Benutzer benutzer) {
        if (this.kundenVerwaltung.getKundenListe().getKunden().containsKey(benutzer.getEmail())) { //gut verschachtelt ||containsKey.benutzer.getBenutzerName()
            return this.kundenVerwaltung.getKundenListe().getKunden().get(benutzer.getEmail()).getPasswort().equals(benutzer.getPasswort());
        } else if (this.mitarbeiterVerwaltung.getMitarbeiterListe().getMitarbeiter().containsKey(benutzer.getEmail())) {
            return this.mitarbeiterVerwaltung.getMitarbeiterListe().getMitarbeiter().get(benutzer.getEmail()).getPasswort().equals(benutzer.getPasswort());
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

//Eigene Liste, damit man sich mit username anmelden kann, hat username als key und benutzerNummer als value

//Kommentare clean machen.

//man soll sich über einen benutzernamen einloggen können, abgleich über benutzerNummer-Liste!
//wenn ich einen benutzernamen eingebe, wird dieser automatisch mit der abgespeicherten E-Mail gleichgesetzt
//die E-Mail wird dann als key genutzt, um den user tatsächlich anzumelden. Über Benutzernamen wird in anderer Methode der user gefunden und
//dann als Benutzer der Methode login(Benutzer benutzer) übergeben.

//da man aus dem use input den user namen und die E-Mail zwischengespeichert hat, kann man als key value paar
//benutzernamen = key und E-Mail = value machen. benutzername || E-Mail login möglich beide checken hashmap. Einzigartiger Benutzername muss!

//man muss vom benutzerNamen auf die E-Mail schließen können.
//Liste die für jeden benutzerNamen eine dazugehörige E-Mail eingespeichert hat.
