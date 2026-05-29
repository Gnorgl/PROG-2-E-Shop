package exceptions.user;

import exceptions.VerwaltungsException;

public class EmailBereitsVergebenException extends VerwaltungsException {
    public EmailBereitsVergebenException(String email) {
        super("Die E-Mail '" + email + "' wird bereits von einem anderen Account verwendet.");
    }
}