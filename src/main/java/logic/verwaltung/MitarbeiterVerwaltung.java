package logic.verwaltung;

import entities.Mitarbeiter;
import logic.moduls.IMV;
import logic.moduls.IUC;
import persistence.user.MitarbeiterListe;

public class MitarbeiterVerwaltung implements IMV, IUC {
    private final MitarbeiterListe mitarbeiterListe = new MitarbeiterListe();

    private long idCounter = 0;

    public MitarbeiterVerwaltung() {}

    //Getter-Methoden

    @Override
    public MitarbeiterListe getMitarbeiterListe() {
        return mitarbeiterListe;
    }

    @Override
    public Mitarbeiter getMitarbeiter(String email) {
        return this.mitarbeiterListe.getMitarbeiter().get(email);
    }

    //Methode um einen neuen Mitarbeiter zu erstellen. Gibt einen Boolean Wert wieder.

    @Override
    public boolean createNewMitarbeiter(String email, String passwort, String nachname,String vorname) {
        if (this.mitarbeiterListe.getMitarbeiter().containsKey(email)) {
            return false;
        } else {
            String nummer = generateBenutzerNummer();
            this.mitarbeiterListe.getMitarbeiter().put(email, new Mitarbeiter(nummer, email, passwort, nachname, vorname));
            return true;
        }
    }

    //Methode um eine Mitarbeiter-ID-Nummer zu generieren. ID wird gezählt.

    @Override
    public String generateBenutzerNummer() {
        this.idCounter++;
        return "MI-" + this.idCounter;
    }

}
