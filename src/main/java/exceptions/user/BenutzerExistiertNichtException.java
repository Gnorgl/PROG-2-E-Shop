package exceptions.user;

import exceptions.VerwaltungsException;

public class BenutzerExistiertNichtException extends VerwaltungsException {
    public BenutzerExistiertNichtException(String email) {
        super("Der Benutzer mit der E-Mail '" + email + "' wurde nicht gefunden.");
    }
}
