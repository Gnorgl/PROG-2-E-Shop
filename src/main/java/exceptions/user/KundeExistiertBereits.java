package exceptions.user;

public class KundeExistiertBereits extends RuntimeException {
    public KundeExistiertBereits(String message) {
        super(message);
    }
}
