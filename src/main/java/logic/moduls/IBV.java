package logic.moduls;

import entities.Benutzer;

public interface IBV {

    Benutzer benutzerExistiert(String email);

    boolean login(String email, String password);

}
