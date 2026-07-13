package exceptions.artikel;

public class AnzahlUngueltigException extends RuntimeException {
    public AnzahlUngueltigException(int anzahl) {
        super("Die Anzahl " + anzahl + " ist ungültig. Sie muss größer als 0 sein.");
    }

    public AnzahlUngueltigException() {
        super("Die Anzahl ist ungültig. Sie muss größer als 0 sein.");
    }
}
