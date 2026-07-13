package interfaces.moduls;

import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;

public interface IBV { //Interface BenutzerVerwaltung

    Benutzer benutzerCheck(String email) throws BenutzerExistiertNichtException;
    boolean passwordCheck(Benutzer benutzer, String password);
    boolean istEmailVergeben(String email);
}
