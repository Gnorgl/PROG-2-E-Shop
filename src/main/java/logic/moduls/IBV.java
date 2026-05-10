package logic.moduls;

import entities.Benutzer;

public interface IBV {

    Benutzer userExists (String input);

    boolean login(Benutzer benutzer);



}
