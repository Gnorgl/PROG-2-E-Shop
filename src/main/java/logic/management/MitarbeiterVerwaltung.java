package logic.management;

import entities.Kunde;
import entities.Mitarbeiter;
import logic.moduls.IMV;
import persistence.user.MitarbeiterListe;

public class MitarbeiterVerwaltung implements IMV {
    private final MitarbeiterListe mitarbeiterListe = new MitarbeiterListe();

    private long idCounter = 0;

    public MitarbeiterVerwaltung() {}

    @Override
    public MitarbeiterListe getMitarbeiterListe() {
        return mitarbeiterListe;
    }

    @Override
    public boolean createNewMitarbeiter(String email, String passwort, String nachname,String vorname) {
        if (this.mitarbeiterListe.getMitarbeiter().containsKey(email)) {
            return false;
        } else {
            String nummer = generateMitarbeiterNummer();
            this.mitarbeiterListe.getMitarbeiter().put(email, new Mitarbeiter(nummer, email, passwort, nachname, vorname));
            return true;
        }
    }

    @Override
    public String generateMitarbeiterNummer() {
        this.idCounter++;
        return "MI-" + this.idCounter;
    }

}
