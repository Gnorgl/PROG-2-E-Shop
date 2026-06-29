package ui.gui.views;

import exceptions.artikel.AnzahlUngueltigException;
import exceptions.artikel.ArtikelExistiertBereits;
import exceptions.artikel.ArtikelNichtGefunden;
import exceptions.artikel.BestandNichtAusreichendException;
import exceptions.artikel.MengeUngueltigException;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;


import ui.gui.components.CustomButton;
import ui.gui.components.CustomInputField;
import ui.gui.components.FormRow;

public class ArtikelVerwaltungView extends VBox {
    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    // Artikel anlegen
    private CustomInputField neuBezeichnungField;
    private CustomInputField neuBestandField;
    private CustomInputField neuPreisField;
    private CustomButton anlegenBtn;

    // Bestand ändern
    private CustomInputField bestandNrField;
    private CustomInputField bestandAnzahlField;
    private CustomButton einlagernBtn;
    private CustomButton reduzierenBtn;

    // Artikel löschen
    private CustomInputField loeschNrField;
    private CustomButton loeschenBtn;

    public ArtikelVerwaltungView(Eshop eshop, SessionManager session, EshopGUI guiController) {
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

        this.setSpacing(30);
        this.setPadding(new Insets(25));

        initUI();
    }

    private void initUI() {
        Label titelLabel = new Label("Warenbestand verwalten");
        titelLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox anlegenBox = createAnlegenBereich();
        VBox bestandBox = createBestandBereich();
        VBox loeschBox = createLoeschenBereich();

        this.getChildren().addAll(
                titelLabel,
                anlegenBox,
                new Separator(),
                bestandBox,
                new Separator(),
                loeschBox
        );
    }

    private VBox createAnlegenBereich() {
        VBox box = new VBox(15);
        Label header = new Label("Neuen Artikel anlegen (ID wird automatisch vergeben)");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        neuBezeichnungField = new CustomInputField("z.B. Gaming Maus");
        neuBestandField = new CustomInputField("z.B. 100");
        neuPreisField = new CustomInputField("z.B. 49.99");

        VBox formLayout = new VBox(10);
        formLayout.getChildren().addAll(
                new FormRow("Bezeichnung:", neuBezeichnungField),
                new FormRow("Startbestand:", neuBestandField),
                new FormRow("Preis (€):", neuPreisField)
        );

        anlegenBtn = new CustomButton("Artikel erstellen", CustomButton.ButtonType.PRIMARY);
        anlegenBtn.setOnAction(e -> ArtikelAnlegen());

        box.getChildren().addAll(header, formLayout, anlegenBtn);
        return box;
    }

    private VBox createBestandBereich() {
        VBox box = new VBox(15);
        Label header = new Label("Bestand ändern");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

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

        reduzierenBtn = new CustomButton("Reduzieren (-)", CustomButton.ButtonType.SECONDARY);
        reduzierenBtn.setOnAction(e -> BestandReduzieren());

        btnBox.getChildren().addAll(einlagernBtn, reduzierenBtn); // Hier war der Tippfehler behoben

        box.getChildren().addAll(header, formLayout, btnBox);
        return box;
    }

    private VBox createLoeschenBereich() {
        VBox box = new VBox(15);
        Label header = new Label("Artikel unwiderruflich löschen");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        loeschNrField = new CustomInputField("Artikelnummer");
        FormRow loeschRow = new FormRow("Artikelnummer:", loeschNrField);

        loeschenBtn = new CustomButton("Löschen", CustomButton.ButtonType.PRIMARY);
        loeschenBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        loeschenBtn.setOnAction(e -> ArtikelLoeschen());

        box.getChildren().addAll(header, loeschRow, loeschenBtn);
        return box;
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

            eshop.getArtikelVerwaltung().legeArtikelAn(bezeichnung, bestand, preis);
            showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Artikel '" + bezeichnung + "' wurde erfolgreich angelegt.");

            neuBezeichnungField.clear();
            neuBestandField.clear();
            neuPreisField.clear();

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

            eshop.getArtikelVerwaltung().bestandErhoehen(nr, anzahl);
            showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Bestand erfolgreich erhöht.");

            bestandNrField.clear();
            bestandAnzahlField.clear();

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

            eshop.getArtikelVerwaltung().bestandReduzieren(nr, anzahl);
            showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Bestand erfolgreich reduziert.");

            bestandNrField.clear();
            bestandAnzahlField.clear();

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
                eshop.getArtikelVerwaltung().loeschen(nr);
                showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Artikel wurde aus dem System entfernt.");
                loeschNrField.clear();
            }

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Eingabefehler", "Bitte eine gültige Artikelnummer eingeben.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler beim Löschen: " + ex.getMessage());
        }
    }


    //Pop up zur visuellen Rückmeldung
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}