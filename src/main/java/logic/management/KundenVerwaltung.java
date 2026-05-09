package logic.management;

import entities.Kunde;
import logic.moduls.IKV;
import persistence.user.KundenListe;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public class KundenVerwaltung implements IKV {
    private final KundenListe kundenListe = new KundenListe();
    //id counter
    private long idCounter = 0;

    public KundenVerwaltung() {}

    @Override
    public KundenListe getKundenListe() {
        return kundenListe;
    }

    @Override
    public boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse) {
        //nummer wird automatisch generiert, am Ende wird nach username gefragt, welcher für login genutzt werden kann, da er mit nummer auf Liste? verknüpft ist.
        if (this.kundenListe.getKunden().containsKey(email)) {
            return false;
        } else {
            String nummer = generateKundenNummer();
            this.kundenListe.getKunden().put(email, new Kunde(nummer, email, passwort, nachname, vorname, adresse));
            //verknüpfung von nummer und username -> neue persistence class erstellen
            return true;
        }
    }//würde sinn machen mit interface, da sowohl KundenVerwaltung als auch MitarbeiterVerwaltung ähnliche createMethoden benutzen.
    //generell nur ein Interface für KundenVerwaltung und Mitarbeiterverwaltung, da diese sehr ähnlich sind.

    @Override
    public String generateKundenNummer() {
        this.idCounter++;
        return "KI-" + this.idCounter;
    }

    //Kunden ID-Generator andere kennzeichnung als mitarbeiter ID, etwa KD-234324 und Mitarbeiter MB-234324, random generated lange nummer.
    //check if id already contains as key, if true -> generate again.
}
