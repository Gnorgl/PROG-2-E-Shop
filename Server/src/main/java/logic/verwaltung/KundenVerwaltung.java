package logic.verwaltung;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Kunde;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;
import interfaces.moduls.IKV;
import persistence.user.KundenListe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KundenVerwaltung implements IKV {

    private final File datei = new File("kunden.json");
    private final ObjectMapper mapper = new ObjectMapper();
    private KundenListe kundenListe = new KundenListe();
    private long idCounter = 0;

    public KundenVerwaltung() {
        datenLaden();
    }

    //Getter-Methoden

    public KundenListe getKundenListe() {
        return kundenListe;
    }

    // synchronized: kundenListe/idCounter werden von allen Client-Threads gemeinsam
    // benutzt (Login, Registrierung usw. können von mehreren Clients gleichzeitig kommen)
    @Override
    public synchronized List<Kunde> getAlleKunden() {
        return new ArrayList<>(this.kundenListe.getKunden().values());
    }

    @Override
    public synchronized Kunde getKunde(String email) throws KundeNichtGefundenException {
        Kunde kunde = this.kundenListe.getKunden().get(email);
        if (kunde == null) {
            throw new KundeNichtGefundenException(email);
        }
        return kunde;
    }

    //Methode um einen neuen Kunden zu erstellen. Gibt einen Boolean Wert wieder.

    @Override
    public synchronized boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse) throws EmailBereitsVergebenException {
        if (this.kundenListe.getKunden().containsKey(email)) {
            throw new EmailBereitsVergebenException(email);
        } else {
            String nummer = generateBenutzerNummer();
            this.kundenListe.getKunden().put(email, new Kunde(nummer, email, passwort, nachname, vorname, adresse));
            safe();
            return true;
        }
    }

    //Methode um eine Kunden-ID-Nummer zu generieren. ID wird gezählt.
    private String generateBenutzerNummer() {
        this.idCounter++;
        return "-ki-" + this.idCounter;
    }

    //Kunden ID-Generator andere kennzeichnung als mitarbeiter ID, etwa KD-234324 und Mitarbeiter MB-234324, random generated lange nummer.

    //Persistenz Methoden
    private void safe() {
        try {
            Object[] speicherContainer = new Object[]{ this.kundenListe, this.idCounter };

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

            this.kundenListe = mapper.convertValue(speicherContainer[0], KundenListe.class);
            this.idCounter = mapper.convertValue(speicherContainer[1], Long.class);

            if (this.kundenListe.getKunden().isEmpty()) {
                this.idCounter = 0;
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Fehler beim Laden der Kunden: " + e.getMessage());
        }
    }



}
