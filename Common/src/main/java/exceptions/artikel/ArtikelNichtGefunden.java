package exceptions.artikel;

import exceptions.VerwaltungsException;

public class ArtikelNichtGefunden extends VerwaltungsException {
  public ArtikelNichtGefunden(String artikelNummer) {
    super("Ein Artikel mit der Nummer " + artikelNummer + " konnte nicht gefunden werden.");
  }

  private ArtikelNichtGefunden(String nachricht, boolean roh) {
    super(nachricht);
  }

  // Für den Client: der Server schickt schon die fertig formatierte Fehlermeldung
  public static ArtikelNichtGefunden mitFertigerNachricht(String nachricht) {
    return new ArtikelNichtGefunden(nachricht, true);
  }
}