package interfaces.moduls;

import entities.Kunde;
import exceptions.user.EmailBereitsVergebenException;
import exceptions.user.KundeNichtGefundenException;

import java.util.List;

public interface IKV { //Interface KundenVerwaltung

    List<Kunde> getAlleKunden();

    Kunde getKunde(String email) throws KundeNichtGefundenException;

    boolean createNewKunden(String email, String passwort, String nachname, String vorname, String adresse) throws EmailBereitsVergebenException;

}
