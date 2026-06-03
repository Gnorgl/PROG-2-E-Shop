package logic.verwaltung;

import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;
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
    //Gibt ein Benutzer Objekt zurück oder null wenn nicht gefunden.

    @Override
    public Benutzer benutzerCheck(String email) throws BenutzerExistiertNichtException {
        try {
            Benutzer kunde = kundenVerwaltung.getKunde(email);
            if (kunde != null) {
                return kunde;
            }
        } catch (Exception e) {
            // Kunde nicht gefunden, weitersuchen in Mitarbeiter
        }

        try {
            return mitarbeiterVerwaltung.getMitarbeiter(email);
        } catch (Exception e) {
            // Mitarbeiter auch nicht gefunden
            return null;
        }
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

