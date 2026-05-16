package ui.navigation;

import entities.Benutzer;
import entities.Mitarbeiter;

//Speichert ab, wer aktuell angemeldet ist.
public class SessionManager {
    private Benutzer angemeldeterBenutzer = null;

    public SessionManager() {}

    //Getter-Methode
    public Benutzer getBenutzer() {
        return this.angemeldeterBenutzer;
    }

    //Aktueller Benutzer Check, ob ein Benutzer angemeldet ist
    public boolean benutzerCheck() {
        return this.angemeldeterBenutzer != null;
    }

    //Benutzer bei login wird festgelegt
    public void login(Benutzer benutzer) {
        this.angemeldeterBenutzer = benutzer;
    }

    //Setzt Benutzer beim logout auf null
    public void logout() {
        this.angemeldeterBenutzer = null;
    }

    //Check ob Benutzer ein Mitarbeiter ist oder nicht
    public boolean istBenutzerEinMitarbeiter() {
        return benutzerCheck() && (this.angemeldeterBenutzer instanceof Mitarbeiter);
    }






}
