package logic.moduls;

import entities.Benutzer;
import exceptions.user.BenutzerExistiertNichtException;

public interface IBV {

    Benutzer benutzerCheck(String email) throws BenutzerExistiertNichtException;
    boolean passwordCheck(Benutzer benutzer, String password);
}
