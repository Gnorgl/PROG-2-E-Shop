package logic.management;

import logic.moduls.IKV;
import persistence.user.KundenListe;

public class KundenVerwaltung implements IKV {
    private final KundenListe kundenListe = new KundenListe();

    @Override
    public KundenListe getKundenListe() {
        return kundenListe;
    }

    @Override
    public boolean createNewKunden(String nummer, String email, String passwort, String nachname, String vorname, String adresse) {
        if (this.kundenListe.getKunden().containsKey(vorname))
        return true;
    }

    //Kunden ID-Generator andere kennzeichnung als mitarbeiter ID, etwa KD-234324 und Mitarbeiter MB-234324, random generated lange nummer.
    //check if id already contains as key, if true -> generate again.
}
