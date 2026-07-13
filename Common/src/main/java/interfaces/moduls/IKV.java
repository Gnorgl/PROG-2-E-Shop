package interfaces.moduls;

import entities.Kunde;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;
import persistence.user.KundenListe;

public interface IKV { //Interface KundenVerwaltung

    KundenListe getKundenListe();

    Kunde getKunde(String email) throws KundeNichtGefundenException;

    boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse) throws EmailBereitsVergebenException;

}
