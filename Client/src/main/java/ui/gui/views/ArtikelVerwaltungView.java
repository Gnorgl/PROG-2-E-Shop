package ui.gui.views;

import entities.Artikel;
import exceptions.artikel.AnzahlUngueltigException;
import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.BestandNichtAusreichendException;
import exceptions.artikel.MengeUngueltigException;
import interfaces.InterfaceEshop;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.gui.EshopGUI;

import ui.gui.SessionManager;
import ui.gui.components.CustomButton;
import ui.gui.components.CustomInputField;
import ui.gui.components.FormRow;

import java.util.List;

public class ArtikelVerwaltungView extends VBox {
    private final InterfaceEshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    // Tabelle für die Übersicht
    private TableView<Artikel> artikelTable;
    private ObservableList<Artikel> artikelListe;

    // Artikel anlegen
    private CheckBox massengutCheckBox;
    private CustomInputField neuPackungField;
    private CustomInputField neuBezeichnungField;
    private CustomInputField neuBestandField;
    private CustomInputField neuPreisField;
    private CustomButton anlegenBtn;

    // Bestand ändern
    private CustomInputField bestandNrField;
    private CustomInputField bestandAnzahlField;
    private CustomButton einlagernBtn;
    private CustomButton reduzierenBtn;

    // Bestandshistorie anzeigen
    private CustomInputField historieNrField;
    private CustomButton historieAnzeigenBtn;
    private BestandsHistorieView historieChart;

    // Artikel löschen
    private CustomInputField loeschNrField;
    private CustomButton loeschenBtn;

    public ArtikelVerwaltungView(InterfaceEshop eshop, SessionManager session, EshopGUI guiController) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;

        // CSS laden
        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }

        // Haupt-Layout CSS-Klasse zuweisen
        this.getStyleClass().add("artikel-verwaltung-view");

        initUI();
        datenLaden();
    }

    private void initUI() {
        Label titelLabel = new Label("Warenbestand verwalten");
        titelLabel.getStyleClass().add("view-title");

        // UI-Bereiche generieren
        VBox tabelleBox = createTabelleBereich();
        VBox anlegenBox = createAnlegenBereich();
        VBox bestandBox = createBestandBereich();
        VBox loeschBox = createLoeschenBereich();
        VBox historieBox = createHistorieBereich();

        // Ein Layout für die Formulare nebeneinander
        HBox formulareBox = new HBox(30);

        // Linke Seite: Anlegen & Löschen
        VBox linksBox = new VBox(20);
        linksBox.getChildren().addAll(anlegenBox, new Separator(), loeschBox);
        HBox.setHgrow(linksBox, Priority.ALWAYS);

        // Rechte Seite: Bestand ändern
        VBox rechtsBox = new VBox(20);
        rechtsBox.getChildren().addAll(bestandBox);
        rechtsBox.getChildren().add(historieBox);
        HBox.setHgrow(rechtsBox, Priority.ALWAYS);

        formulareBox.getChildren().addAll(linksBox, new Separator(), rechtsBox);

        this.getChildren().addAll(
                titelLabel,
                tabelleBox, // Die Tabelle oben
                new Separator(),
                formulareBox
        );
    }

    // Tabellen-Bereich
    private VBox createTabelleBereich() {
        VBox box = new VBox(10);
        Label header = new Label("Aktueller Inventarbestand");
        header.getStyleClass().add("section-header");

        artikelTable = new TableView<>();
        artikelTable.setPrefHeight(200); // Feste Höhe, damit genug Platz für die Formulare bleibt

        // ("artikelNummer" ruft getArtikelNummer() auf)
        TableColumn<Artikel, Integer> colNr = new TableColumn<>("Nr.");
        colNr.setCellValueFactory(new PropertyValueFactory<>("artikelNummer"));
        colNr.setPrefWidth(50);

        TableColumn<Artikel, String> colBez = new TableColumn<>("Bezeichnung");
        colBez.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));
        colBez.setPrefWidth(200);

        TableColumn<Artikel, String> colArt = new TableColumn<>("Typ / Packung");
        colArt.setCellValueFactory(cellData -> {
            Artikel a = cellData.getValue();
            if (a instanceof entities.Massengutartikel) {
                return new javafx.beans.property.ReadOnlyStringWrapper(((entities.Massengutartikel) a).getPackungsGroesse() + "er Pack");
            } else {
                return new javafx.beans.property.ReadOnlyStringWrapper("Einzelartikel");
            }
        });
        colArt.setPrefWidth(100);

        TableColumn<Artikel, Integer> colBestand = new TableColumn<>("Bestand");
        colBestand.setCellValueFactory(new PropertyValueFactory<>("bestand"));

        TableColumn<Artikel, Double> colPreis = new TableColumn<>("Preis (€)");
        colPreis.setCellValueFactory(new PropertyValueFactory<>("preis"));

        artikelTable.getColumns().addAll(colNr, colBez, colArt, colBestand, colPreis);

        box.getChildren().addAll(header, artikelTable);
        return box;
    }


    private void datenLaden() {
        List<Artikel> alleArtikel = eshop.getAlleArtikel();
        //eshop.getArtikelVerwaltung().getArtikelListe().getArtikelImLager();
        //Hier war vorher getArtikelImLager() das Gleiche wie getAlleArtikel() ?
        // Baut eine Liste aus dem aktuellen Lagerbestand
        artikelListe = FXCollections.observableArrayList(alleArtikel);

        artikelTable.setItems(artikelListe);
        artikelTable.refresh();
    }

    private VBox createAnlegenBereich() {
        VBox box = new VBox(15);
        Label header = new Label("Neuen Artikel anlegen");
        header.getStyleClass().add("section-header");

        neuBezeichnungField = new CustomInputField("z.B. Gaming Maus");
        neuBestandField = new CustomInputField("z.B. 100");
        neuPreisField = new CustomInputField("z.B. 49.99");

        massengutCheckBox = new CheckBox("Ist Massengutartikel?");
        neuPackungField = new CustomInputField("z.B. 6");
        neuPackungField.setDisable(true); // Standardmäßig deaktiviert

        // Toggle-Logik für das Textfeld
        massengutCheckBox.setOnAction(e -> {
            neuPackungField.setDisable(!massengutCheckBox.isSelected());
            if(!massengutCheckBox.isSelected()) neuPackungField.clear();
        });

        VBox formLayout = new VBox(10);
        formLayout.getChildren().addAll(
                new FormRow("Bezeichnung:", neuBezeichnungField),
                new FormRow("Startbestand:", neuBestandField),
                new FormRow("Preis (€):", neuPreisField),
                massengutCheckBox,
                new FormRow("Packungsgröße:", neuPackungField)
        );

        anlegenBtn = new CustomButton("Artikel erstellen", CustomButton.ButtonType.PRIMARY);
        anlegenBtn.setOnAction(e -> ArtikelAnlegen());

        box.getChildren().addAll(header, formLayout, anlegenBtn);
        return box;
    }

    private VBox createBestandBereich() {
        VBox box = new VBox(15);
        Label header = new Label("Bestand ändern");
        header.getStyleClass().add("section-header");

        bestandNrField = new CustomInputField("Artikelnummer");
        bestandAnzahlField = new CustomInputField("Menge");

        VBox formLayout = new VBox(10);
        formLayout.getChildren().addAll(
                new FormRow("Artikelnummer:", bestandNrField),
                new FormRow("Anzahl/Menge:", bestandAnzahlField)
        );

        HBox btnBox = new HBox(10);
        einlagernBtn = new CustomButton("Einlagern (+)", CustomButton.ButtonType.PRIMARY);
        einlagernBtn.setOnAction(e -> BestandErhoehen());

        reduzierenBtn = new CustomButton("Reduzieren (-)", CustomButton.ButtonType.PRIMARY);
        reduzierenBtn.setOnAction(e -> BestandReduzieren());

        btnBox.getChildren().addAll(einlagernBtn, reduzierenBtn);

        box.getChildren().addAll(header, formLayout, btnBox);
        return box;
    }

    private VBox createLoeschenBereich() {
        VBox box = new VBox(15);
        Label header = new Label("Artikel unwiderruflich löschen");
        header.getStyleClass().add("section-header");

        loeschNrField = new CustomInputField("Artikelnummer");
        FormRow loeschRow = new FormRow("Artikelnummer:", loeschNrField);

        loeschenBtn = new CustomButton("Löschen", CustomButton.ButtonType.PRIMARY);
        loeschenBtn.getStyleClass().add("delete-btn");
        loeschenBtn.setOnAction(e -> ArtikelLoeschen());

        box.getChildren().addAll(header, loeschRow, loeschenBtn);
        return box;
    }

    private VBox createHistorieBereich() {
        VBox box = new VBox(15);
        Label header = new Label("Bestandshistorie anzeigen");
        header.getStyleClass().add("section-header");

        historieNrField = new CustomInputField("Artikelnummer");
        FormRow historieRow = new FormRow("Artikelnummer:", historieNrField);

        historieAnzeigenBtn = new CustomButton("Historie anzeigen", CustomButton.ButtonType.PRIMARY);
        historieAnzeigenBtn.setOnAction(e -> HistorieAnzeigen());

        historieChart = new BestandsHistorieView(eshop,400, 250);

        box.getChildren().addAll(header, historieRow, historieAnzeigenBtn, historieChart);
        return box;
    }

    private void HistorieAnzeigen() {
        try {
            int nr = Integer.parseInt(historieNrField.getText().trim());
            historieChart.zeichne(nr);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Eingabefehler", "Bitte eine gültige Artikelnummer eingeben.");
        } catch (ArtikelNichtGefunden ex) {
            showAlert(Alert.AlertType.WARNING, "Nicht gefunden", "Artikel-ID existiert nicht.");
        }
    }

    private void ArtikelAnlegen() {
        try {
            String bezeichnung = neuBezeichnungField.getText().trim();
            int bestand = Integer.parseInt(neuBestandField.getText().trim());
            double preis = Double.parseDouble(neuPreisField.getText().trim().replace(",", "."));

            if (bezeichnung.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Eingabefehler", "Bitte eine Bezeichnung angeben.");
                return;
            }

            if (massengutCheckBox.isSelected()) {
                int packung = Integer.parseInt(neuPackungField.getText().trim());
                eshop.legeMassengutartikelAn(bezeichnung, bestand, preis, packung);
            } else {
                eshop.legeArtikelAn(bezeichnung, bestand, preis);
            }

            showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Artikel '" + bezeichnung + "' wurde erfolgreich angelegt.");

            neuBezeichnungField.clear();
            neuBestandField.clear();
            neuPreisField.clear();
            neuPackungField.clear();
            massengutCheckBox.setSelected(false);
            neuPackungField.setDisable(true);

            datenLaden();

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Eingabefehler", "Bitte gültige Zahlenformate nutzen.");
        } catch (ArtikelExistiertBereits ex) {
            showAlert(Alert.AlertType.ERROR, "Fehler", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler beim Anlegen: " + ex.getMessage());
        }
    }

    private void BestandErhoehen() {
        try {
            int nr = Integer.parseInt(bestandNrField.getText().trim());
            int anzahl = Integer.parseInt(bestandAnzahlField.getText().trim());

            eshop.bestandErhoehen(nr, anzahl);
            showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Bestand erfolgreich erhöht.");

            bestandNrField.clear();
            bestandAnzahlField.clear();

            datenLaden();

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Eingabefehler", "Bitte gültige Ganzzahlen eingeben.");
        } catch (ArtikelNichtGefunden ex) {
            showAlert(Alert.AlertType.WARNING, "Nicht gefunden", "Artikel-ID existiert nicht.");
        } catch (AnzahlUngueltigException | MengeUngueltigException ex) {
            showAlert(Alert.AlertType.WARNING, "Aktion ungültig", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler bei Bestandserhöhung: " + ex.getMessage());
        }
    }

    private void BestandReduzieren() {
        try {
            int nr = Integer.parseInt(bestandNrField.getText().trim());
            int anzahl = Integer.parseInt(bestandAnzahlField.getText().trim());

            eshop.bestandReduzieren(nr, anzahl);
            showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Bestand erfolgreich reduziert.");

            bestandNrField.clear();
            bestandAnzahlField.clear();

            datenLaden();

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Eingabefehler", "Bitte gültige Ganzzahlen eingeben.");
        } catch (ArtikelNichtGefunden ex) {
            showAlert(Alert.AlertType.WARNING, "Nicht gefunden", "Artikel-ID existiert nicht.");
        } catch (AnzahlUngueltigException | MengeUngueltigException | BestandNichtAusreichendException ex) {
            showAlert(Alert.AlertType.WARNING, "Aktion ungültig", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler bei Bestandsreduzierung: " + ex.getMessage());
        }
    }

    private void ArtikelLoeschen() {
        try {
            int nr = Integer.parseInt(loeschNrField.getText().trim());

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Möchten Sie den Artikel mit der Nummer " + nr + " wirklich löschen?", javafx.scene.control.ButtonType.YES, javafx.scene.control.ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait();

            if (confirm.getResult() == javafx.scene.control.ButtonType.YES) {
                eshop.loeschen(nr);
                showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Artikel wurde aus dem System entfernt.");
                loeschNrField.clear();

                datenLaden();
            }

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Eingabefehler", "Bitte eine gültige Artikelnummer eingeben.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler beim Löschen: " + ex.getMessage());
        }
    }

    // Pop-up zur visuellen Rückmeldung
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}