package logic.management;

import entities.Kunde;
import logic.moduls.IKV;
import logic.moduls.IUC;
import persistence.user.KundenListe;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public class KundenVerwaltung implements IKV, IUC {
    private final KundenListe kundenListe = new KundenListe();
    //id counter
    private long idCounter = 0;

    public KundenVerwaltung() {}

    @Override
    public KundenListe getKundenListe() {
        return kundenListe;
    }

    @Override
    public Kunde getKunde(String email) {
        return this.kundenListe.getKunden().get(email);
    }

    @Override
    public boolean createNewKunden(String benutzerName, String email, String passwort, String nachname, String vorname, String adresse) {
        if (this.kundenListe.getKunden().containsKey(email)) {
            return false; //Exception Benutzer existiert bereits!
        } else {
            String nummer = generateBenutzerNummer();
            this.kundenListe.getKunden().put(email, new Kunde(benutzerName, nummer, email, passwort, nachname, vorname, adresse));
            return true;
        }
    }

    @Override
    public String generateBenutzerNummer() {
        this.idCounter++;
        return "KI-" + this.idCounter;
    }

    //Kunden ID-Generator andere kennzeichnung als mitarbeiter ID, etwa KD-234324 und Mitarbeiter MB-234324, random generated lange nummer.
    //check if id already contains as key, if true -> generate again.
}
