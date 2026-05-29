package logic.verwaltung;

import entities.Kunde;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;
import logic.moduls.IKV;
import logic.moduls.IUC;
import persistence.user.KundenListe;

public class KundenVerwaltung implements IKV, IUC {
    private final KundenListe kundenListe = new KundenListe();
    //id counter
    private long idCounter = 0;

    public KundenVerwaltung() {}

    //Getter-Methoden

    @Override
    public KundenListe getKundenListe() {
        return kundenListe;
    }

    @Override
    public Kunde getKunde(String email) throws KundeNichtGefundenException {
        Kunde kunde = this.kundenListe.getKunden().get(email);
        if (kunde == null) {
            throw new KundeNichtGefundenException(email);
        }
        return kunde;
    }

    //Methode um einen neuen Kunden zu erstellen. Gibt einen Boolean Wert wieder.

    @Override
    public boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse) throws EmailBereitsVergebenException {
        if (this.kundenListe.getKunden().containsKey(email)) {
            throw new EmailBereitsVergebenException(email);
        } else {
            String nummer = generateBenutzerNummer();
            this.kundenListe.getKunden().put(email, new Kunde(nummer, email, passwort, nachname, vorname, adresse));
            return true;
        }
    }

    //Methode um eine Kunden-ID-Nummer zu generieren. ID wird gezählt.

    @Override
    public String generateBenutzerNummer() {
        this.idCounter++;
        return "KI-" + this.idCounter;
    }

    //Kunden ID-Generator andere kennzeichnung als mitarbeiter ID, etwa KD-234324 und Mitarbeiter MB-234324, random generated lange nummer.
}
