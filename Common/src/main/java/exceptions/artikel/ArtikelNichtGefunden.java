package exceptions.artikel;

import exceptions.VerwaltungsException;

public class ArtikelNichtGefunden extends VerwaltungsException {
  public ArtikelNichtGefunden(String artikelNummer) {
    super("Ein Artikel mit der Nummer " + artikelNummer + " konnte nicht gefunden werden.");
  }
}