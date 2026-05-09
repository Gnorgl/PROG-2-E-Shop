package logic.management;

import logic.moduls.IMV;
import persistence.user.MitarbeiterListe;

public class MitarbeiterVerwaltung implements IMV {
    private final MitarbeiterListe mitarbeiterListe = new MitarbeiterListe();

    public MitarbeiterVerwaltung() {}

    @Override
    public MitarbeiterListe getMitarbeiterListe() {
        return mitarbeiterListe;
    }

    @Override
    public boolean createNewMitarbeiter(String nummer, String email, String passwort, String nachname,String vorname) {
        return true;
    }

}
