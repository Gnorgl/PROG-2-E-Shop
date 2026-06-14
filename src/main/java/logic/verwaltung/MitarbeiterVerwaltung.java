package logic.verwaltung;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Mitarbeiter;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.MitarbeiterNichtGefundenException;
import logic.moduls.IMV;
import logic.moduls.IUC;
import persistence.user.KundenListe;
import persistence.user.MitarbeiterListe;

import java.io.File;
import java.io.IOException;

public class MitarbeiterVerwaltung implements IMV, IUC {
    private final File datei = new File("mitarbeiter.json");
    private final ObjectMapper mapper = new ObjectMapper();

    private MitarbeiterListe mitarbeiterListe = new MitarbeiterListe();

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

    //Persistenz Methoden
    public void safe() {
        try {
            Object[] speicherContainer = new Object[]{ this.mitarbeiterListe, this.idCounter };

            mapper.writerWithDefaultPrettyPrinter().writeValue(datei, speicherContainer);
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Kunden: " + e.getMessage());
        }
    }

    private void datenLaden() {
        if (!datei.exists()) {
            return;
        }
        try {
            Object[] speicherContainer = mapper.readValue(datei, Object[].class);

            this.mitarbeiterListe = mapper.convertValue(speicherContainer[0], MitarbeiterListe.class);
            this.idCounter = mapper.convertValue(speicherContainer[1], Long.class);

            if (this.mitarbeiterListe.getMitarbeiter().isEmpty()) {
                this.idCounter = 0;
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Fehler beim Laden der Kunden: " + e.getMessage());
        }
    }

}
