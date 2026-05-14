package logic.moduls;

import entities.Benutzer;

public interface IBV {

    Benutzer benutzerCheck(String email);
    boolean passwordCheck(Benutzer benutzer, String password);
}
