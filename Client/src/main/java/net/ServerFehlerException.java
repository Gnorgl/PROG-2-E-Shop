package net;



public class ServerFehlerException extends Exception {

    private final String rohtext;

    public ServerFehlerException(String rohtext) {
        super(rohtext);
        this.rohtext = rohtext == null ? "" : rohtext;
    }

    public String getExceptionName() {
        int index = rohtext.indexOf(':');
        return index == -1 ? rohtext : rohtext.substring(0, index);
    }

    public String getNachricht() {
        int index = rohtext.indexOf(':');
        return index == -1 ? rohtext : rohtext.substring(index + 1).trim();
    }
}
