package interfaces.moduls;

import entities.Kunde;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;

public interface IKV { //Interface KundenVerwaltung

    Kunde getKunde(String email) throws KundeNichtGefundenException;

    boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse) throws EmailBereitsVergebenException;

}
