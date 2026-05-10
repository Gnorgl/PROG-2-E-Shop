package logic.management;

import entities.Kunde;
import entities.Mitarbeiter;
import logic.moduls.IMV;
import logic.moduls.IUC;
import persistence.user.MitarbeiterListe;

public class MitarbeiterVerwaltung implements IMV, IUC {
    private final MitarbeiterListe mitarbeiterListe = new MitarbeiterListe();

    private long idCounter = 0;

    public MitarbeiterVerwaltung() {}

    @Override
    public MitarbeiterListe getMitarbeiterListe() {
        return mitarbeiterListe;
    }

    @Override
    public boolean createNewMitarbeiter(String benutzerName, String email, String passwort, String nachname,String vorname) {
        if (this.mitarbeiterListe.getMitarbeiter().containsKey(email)) {
            return false;
        } else {
            String nummer = generateBenutzerNummer();
            //benutzernamen generierung
            this.mitarbeiterListe.getMitarbeiter().put(email, new Mitarbeiter(benutzerName, nummer, email, passwort, nachname, vorname));
            this.mitarbeiterListe.getBenutzerNameUndEmail().put(benutzerName, email);
            return true;
        }
    }

    @Override
    public String generateBenutzerNummer() {
        this.idCounter++;
        return "MI-" + this.idCounter;
    }

}
