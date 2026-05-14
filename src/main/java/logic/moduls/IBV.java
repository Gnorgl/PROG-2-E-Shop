package logic.moduls;

import entities.Benutzer;

public interface IBV {

    boolean benutzerExistiert(String email);

    boolean login(String email, String password);

    Benutzer dieserBenutzerIstAngemeldet(String email);

}
