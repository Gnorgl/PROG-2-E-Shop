package exceptions.user;

import exceptions.VerwaltungsException;

public class KundeNichtGefundenException extends VerwaltungsException {
    public KundeNichtGefundenException(String email) {
        super("Es konnte kein Kunde mit der E-Mail '" + email + "' ermittelt werden.");
    }
}
