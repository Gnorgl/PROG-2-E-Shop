package logic.verwaltung;

import entities.Benutzer;
import logic.moduls.IBV;

public class BenutzerVerwaltung implements IBV {
    private final KundenVerwaltung kundenVerwaltung = new KundenVerwaltung();
    private final MitarbeiterVerwaltung mitarbeiterVerwaltung = new MitarbeiterVerwaltung();

    public BenutzerVerwaltung() {}

    //Getter-Methoden

    public KundenVerwaltung getKundenVerwaltung() {
        return kundenVerwaltung;
    }

    public MitarbeiterVerwaltung getMitarbeiterVerwaltung() {
        return mitarbeiterVerwaltung;
    }

    //Methode um zu überprüfen, ob ein Benutzer existiert. Überprüfung anhand der eingegebenen E-Mail.
    //Gibt ein Benutzer Objekt zurück.

    @Override
    public Benutzer benutzerCheck(String email) {
        Benutzer kunde = kundenVerwaltung.getKunde(email);
        if (kunde != null) {
            return kunde;
        }
        return mitarbeiterVerwaltung.getMitarbeiter(email);
    }

    //Methode um zu überprüfen, ob ein eingegebenes Password mit dem Password des aktuellen Benutzers übereinstimmt.
    //Gibt einen Boolean Wert zurück.

    @Override
    public boolean passwordCheck(Benutzer benutzer, String password) {
        if (benutzer == null || password == null) {
            return false;
        }
        return benutzer.getPasswort().equals(password);
    }

}

