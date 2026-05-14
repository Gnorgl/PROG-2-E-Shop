package exceptions.user;

public class BenutzerExistiertNicht extends RuntimeException {
    public BenutzerExistiertNicht(String message) {
        super(message);
    }
}

//Falsche E-Mail oder falscher Benutzername.
