# PROG-2 eShop

Ein Shop-System (Client/Server) für die Veranstaltung "Programmieren 2" (Int. Studiengang
Medieninformatik, HSB, SoSe 2026). Kunden können Artikel kaufen, Mitarbeiter verwalten den
Artikelbestand. Die Kommunikation zwischen Client und Server läuft über Sockets (siehe
[docs/Kommunikationsprotokoll.md](docs/Kommunikationsprotokoll.md)).

## Projektstruktur

Das Projekt ist ein Maven-Multi-Modul-Projekt mit drei Modulen:

- **Common** – Entities, Interfaces und Exceptions, die sowohl Client als auch Server
  benutzen (z.B. `Artikel`, `Kunde`, `Rechnung`, `InterfaceEshop`).
- **Server** – Anwendungskern, Datenhaltung (Persistenz als JSON-Dateien) und die
  Server-seitige Netzwerkkommunikation (`EShopServer`, `ClientRequestProcessor`,
  `*KommandoHandler`).
- **Client** – JavaFX-GUI (und eine einfache CUI als Kommandozeilen-Alternative) sowie die
  Client-seitige Netzwerkkommunikation (`EshopClient`, `*Fassade`-Klassen).

## Voraussetzungen

- Java 25 (JDK), siehe `maven.compiler.source`/`target` in der `pom.xml`
- Maven (oder die IntelliJ-eigene Maven-Integration)
- JavaFX wird automatisch über Maven-Abhängigkeiten geladen (Version 21.0.2), keine
  gesonderte Installation nötig

## Bauen

Im Projekt-Root:

```bash
mvn clean install
```

Das baut nacheinander `Common`, `Server` und `Client` (in dieser Abhängigkeitsreihenfolge).

## Starten

Der eShop besteht aus **zwei separaten Prozessen**, die beide laufen müssen:

### 1. Server starten

Hauptklasse: `net.EShopServer` (Modul `Server`)

```bash
cd Server
mvn exec:java -Dexec.mainClass="net.EShopServer"
```

Alternativ in IntelliJ: Rechtsklick auf `EShopServer.java` → *Run*.

Der Server lauscht standardmäßig auf Port `8080` (`EShopServer.DEFAULT_PORT`) und legt beim
ersten Start automatisch die Datenhaltungsdateien an (`artikel.json`, `kunden.json`,
`mitarbeiter.json`, `checkout.json`, `ereignisse.json`, `warenkorb.json` im Arbeitsverzeichnis
des Servers).

### 2. Client starten (GUI)

Hauptklasse: `ui.gui.Launcher` (Modul `Client`)

```bash
cd Client
mvn javafx:run
```

Alternativ in IntelliJ: Rechtsklick auf `Launcher.java` → *Run*. Es können beliebig viele
Client-Instanzen parallel gestartet werden (z.B. um mehrere gleichzeitig eingeloggte Kunden zu
simulieren) – dazu in der Run-Konfiguration "Allow multiple instances" aktivieren.

Der Client verbindet sich beim Start mit `localhost:8080`. Beim allerersten Start kann man
sich über "Noch kein Konto? Registrieren" einen Kunden-Account anlegen; ein initialer
Mitarbeiter-Account wird von der `MitarbeiterVerwaltung` beim ersten Serverstart automatisch
angelegt (siehe Server-Log für die Zugangsdaten).

### 3. (Optional) CUI statt GUI

Für Testzwecke gibt es zusätzlich eine textbasierte Bedienung ohne JavaFX:
Hauptklasse `ui.cui.EshopCUI` (Modul `Client`).

## Architektur

Eine vereinfachte Übersicht der Klassen und ihrer Beziehungen befindet sich unter
[docs/Klassendiagramm.md](docs/Klassendiagramm.md). Das Kommunikationsprotokoll zwischen
Client und Server ist in [docs/Kommunikationsprotokoll.md](docs/Kommunikationsprotokoll.md)
als endlicher Automat aus Sicht des Servers dokumentiert.

## Bekannte Einschränkungen

- Die Lieferadresse wird nicht dauerhaft an der `Rechnung` gespeichert (nur zur Laufzeit beim
  Checkout verwendet).
- Zahlungsarten (Kreditkarte/PayPal) sind reine UI-Simulationen ohne echte Zahlungsabwicklung.
