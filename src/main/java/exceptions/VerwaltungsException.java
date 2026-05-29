package exceptions;

public abstract class VerwaltungsException extends Exception {

    public VerwaltungsException(String nachricht) {
        super(nachricht);
    }
}
