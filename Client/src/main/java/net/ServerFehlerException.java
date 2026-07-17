package net;

// Transportiert eine Fehlermeldung vom Server (Format "ExceptionName: Nachricht")
// zur aufrufenden Fassade-Methode, die daraus die passende, im Common-Modul
// bekannte Exception (z.B. ArtikelNichtGefunden) wieder herstellt.
public class ServerFehlerException extends Exception {

    private final String rohtext;

    public ServerFehlerException(String rohtext) {
        super(rohtext);
        this.rohtext = rohtext == null ? "" : rohtext;
    }

    // z.B. "ArtikelNichtGefunden" aus "ArtikelNichtGefunden: Ein Artikel..."
    public String getExceptionName() {
        int index = rohtext.indexOf(':');
        return index == -1 ? rohtext : rohtext.substring(0, index);
    }

    public String getNachricht() {
        int index = rohtext.indexOf(':');
        return index == -1 ? rohtext : rohtext.substring(index + 1).trim();
    }
}
