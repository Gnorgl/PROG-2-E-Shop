package exceptions.user;

public class MitarbeiterExistiertBereits extends RuntimeException {
    public MitarbeiterExistiertBereits(String message) {
        super(message);
    }
}
