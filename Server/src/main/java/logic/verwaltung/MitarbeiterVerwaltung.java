package logic.verwaltung;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Mitarbeiter;
import exceptions.user.MitarbeiterNichtGefundenException;
import interfaces.moduls.IMV;
import persistence.user.MitarbeiterListe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MitarbeiterVerwaltung implements IMV {
    private final File datei = new File("mitarbeiter.json");
    private final ObjectMapper mapper = new ObjectMapper();

    private MitarbeiterListe mitarbeiterListe = new MitarbeiterListe();
    private long idCounter = 0;

    public MitarbeiterVerwaltung() {
        datenLaden();
        if (this.mitarbeiterListe.getMitarbeiter().isEmpty()) {
            initialisiereStandardAdmin();
        }
    }

    public MitarbeiterListe getMitarbeiterListe() {
        return mitarbeiterListe;
    }

    @Override
    public List<Mitarbeiter> getAlleMitarbeiter() {
        return new ArrayList<>(this.mitarbeiterListe.getMitarbeiter().values());
    }

    @Override
    public Mitarbeiter getMitarbeiter(String email) throws MitarbeiterNichtGefundenException {
        // immer kleinbuchstabe
        if (email != null) {
            email = email.toLowerCase().trim();
        }

        Mitarbeiter mitarbeiter = this.mitarbeiterListe.getMitarbeiter().get(email);
        if (mitarbeiter == null) {
            throw new MitarbeiterNichtGefundenException(email);
        }
        return mitarbeiter;
    }

    @Override
    public Mitarbeiter createNewMitarbeiter(String passwort, String nachname, String vorname) {
        String nummer = generateBenutzerNummer();

        String email = (vorname.trim() + nummer + "@shop.com").toLowerCase();

        Mitarbeiter neuerMitarbeiter = new Mitarbeiter(nummer, email, passwort, nachname, vorname);

        this.mitarbeiterListe.getMitarbeiter().put(email, neuerMitarbeiter);
        safe();

        return neuerMitarbeiter;
    }

    private String generateBenutzerNummer() {
        this.idCounter++;
        return "-mi-" + this.idCounter;
    }

    private void initialisiereStandardAdmin() {
        String adminNummer = "-mi-0";
        // E-Mail explizit komplett klein schreiben
        String adminEmail = "admin@shop.com";
        Mitarbeiter admin = new Mitarbeiter(adminNummer, adminEmail, "123", "Modus", "Admin");

        this.mitarbeiterListe.getMitarbeiter().put(adminEmail, admin);
        safe();
    }

    private void safe() {
        try {
            Object[] speicherContainer = new Object[]{ this.mitarbeiterListe, this.idCounter };
            mapper.writerWithDefaultPrettyPrinter().writeValue(datei, speicherContainer);
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Mitarbeiter: " + e.getMessage());
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
            System.err.println("Fehler beim Laden der Mitarbeiter: " + e.getMessage());
        }
    }
}