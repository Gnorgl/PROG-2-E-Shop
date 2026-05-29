package logic.verwaltung;

import entities.Mitarbeiter;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.MitarbeiterNichtGefundenException;
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
    public Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException {
        Mitarbeiter mitarbeiter = this.mitarbeiterListe.getMitarbeiter().get(email);
        if (mitarbeiter == null) {
            throw new MitarbeiterNichtGefundenException(email);
        }
        return mitarbeiter;
    }

    //Methode um einen neuen Mitarbeiter zu erstellen. Gibt einen Boolean Wert wieder.

    @Override
    public boolean createNewMitarbeiter(String email, String passwort, String nachname,String vorname) throws EmailBereitsVergebenException {
        if (this.mitarbeiterListe.getMitarbeiter().containsKey(email)) {
            throw new EmailBereitsVergebenException(email);
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
