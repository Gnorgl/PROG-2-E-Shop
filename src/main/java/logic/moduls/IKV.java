package logic.moduls;

import persistence.user.KundenListe;

public interface IKV {

    KundenListe getKundenListe();

    boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse);

    String generateKundenNummer();
}
