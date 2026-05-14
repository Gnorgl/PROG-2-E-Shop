package logic.moduls;

import entities.Kunde;
import persistence.user.KundenListe;

public interface IKV {

    KundenListe getKundenListe();

    Kunde getKunde(String email);

    boolean createNewKunden(String benutzerName, String email, String passwort, String nachname, String vorname, String adresse);

}
