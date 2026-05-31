package exceptions.artikel;

import exceptions.VerwaltungsException;

public class ArtikelNichtGefunden extends VerwaltungsException {
  public ArtikelNichtGefunden(int artikelNummer) {
    super("Ein Artikel mit der Nummer " + artikelNummer + " konnte nicht gefunden werden.");
  }
}