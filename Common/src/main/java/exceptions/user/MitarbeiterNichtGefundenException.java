package exceptions.user;

import exceptions.VerwaltungsException;

public class MitarbeiterNichtGefundenException extends VerwaltungsException {
    public MitarbeiterNichtGefundenException(String email) {
        super("Es konnte kein Mitarbeiter mit der E-Mail '" + email + "' ermittelt werden.");
    }
}
