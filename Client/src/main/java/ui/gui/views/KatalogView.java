package ui.gui.views;

import entities.Artikel;
import interfaces.InterfaceEshop;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.gui.EshopGUI;
import ui.gui.SessionManager;
import ui.gui.components.CustomButton;

import java.util.Comparator;
import java.util.List;

public class KatalogView extends VBox {
    private final InterfaceEshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    private TableView<Artikel> table;
    private ComboBox<String> sortBox;
    private ObservableList<Artikel> artikelListe;
    private SortedList<Artikel> sortedListe;
    private TextField mengeField;

    public KatalogView(InterfaceEshop eshop, SessionManager session, EshopGUI guiController) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;

        // CSS-Klasse für das gesamte Layout setzen
        this.getStyleClass().add("katalog-view");

        // CSS laden
        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }

        initUI();
        datenLaden();

        if (eshop instanceof net.EshopClient client) {
            Runnable refreshCallback = this::datenLaden;
            client.aktualisierungAbonnieren(refreshCallback);

            this.sceneProperty().addListener((obs, alteScene, neueScene) -> {
                if (neueScene == null) {
                    client.aktualisierungAbmelden(refreshCallback);
                }
            });
        }
    }

    private void initUI() {
        Label titelLabel = new Label("Artikelkatalog");
        titelLabel.getStyleClass().add("katalog-title"); // CSS statt setStyle

        HBox topBar = new HBox();
        topBar.getStyleClass().add("katalog-bar");

        Label sortLabel = new Label("Sortieren nach:");
        sortBox = new ComboBox<>();
        sortBox.getItems().addAll("Artikelnummer", "Bezeichnung");
        sortBox.setValue("Artikelnummer"); // Standardwert
        sortBox.setOnAction(e -> sortiereTabelle());

        topBar.getChildren().addAll(sortLabel, sortBox);

        // Tabelle zum Artikel Anzeigen
        table = new TableView<>();

        TableColumn<Artikel, Integer> colNr = new TableColumn<>("Nr.");
        colNr.setCellValueFactory(new PropertyValueFactory<>("artikelNummer"));

        TableColumn<Artikel, String> colBez = new TableColumn<>("Bezeichnung");
        colBez.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));

        TableColumn<Artikel, String> colArt = new TableColumn<>("Verpackung");
        colArt.setCellValueFactory(cellData -> {
            Artikel a = cellData.getValue();
            if (a instanceof entities.Massengutartikel) {
                return new javafx.beans.property.ReadOnlyStringWrapper(((entities.Massengutartikel) a).getPackungsGroesse() + "er Pack");
            } else {
                return new javafx.beans.property.ReadOnlyStringWrapper("Einzelstück");
            }
        });

        TableColumn<Artikel, Double> colPreis = new TableColumn<>("Preis (€)");
        colPreis.setCellValueFactory(new PropertyValueFactory<>("preis"));

        TableColumn<Artikel, Integer> colBestand = new TableColumn<>("Bestand");
        colBestand.setCellValueFactory(new PropertyValueFactory<>("bestand"));

        table.getColumns().addAll(colNr, colBez, colArt, colPreis, colBestand);
        VBox.setVgrow(table, Priority.ALWAYS);

        // In Warenkorb legen
        HBox bottomBar = new HBox();
        bottomBar.getStyleClass().add("katalog-bar");

        Label mengeLabel = new Label("Menge:");
        mengeField = new TextField("1");
        mengeField.getStyleClass().add("menge-input");

        CustomButton btnWarenkorb = new CustomButton("In den Warenkorb", CustomButton.ButtonType.PRIMARY);
        btnWarenkorb.setOnAction(e -> artikelInWarenkorbLegen());

        bottomBar.getChildren().addAll(mengeLabel, mengeField, btnWarenkorb);

        this.getChildren().addAll(titelLabel, topBar, table, bottomBar);
    }

    private void datenLaden() {
        List<Artikel> alleArtikel = eshop.getAlleArtikel();
        artikelListe = FXCollections.observableArrayList(alleArtikel);

        sortedListe = new SortedList<>(artikelListe);
        table.setItems(sortedListe);

        sortiereTabelle();
    }

    private void sortiereTabelle() {
        String kriterium = sortBox.getValue();

        if (kriterium.equals("Artikelnummer")) {
            sortedListe.setComparator(Comparator.comparingInt(Artikel::getArtikelNummer));
        } else if (kriterium.equals("Bezeichnung")) {
            sortedListe.setComparator(Comparator.comparing(Artikel::getBezeichnung, String.CASE_INSENSITIVE_ORDER));
        }
    }

    private void artikelInWarenkorbLegen() {
        Artikel ausgewaehlterArtikel = table.getSelectionModel().getSelectedItem();

        if (ausgewaehlterArtikel == null) {
            showAlert(Alert.AlertType.WARNING, "Kein Artikel ausgewählt", "Bitte wähle zuerst einen Artikel aus der Tabelle aus.");
            return;
        }

        try {
            int eingabeMenge = Integer.parseInt(mengeField.getText().trim());
            if (eingabeMenge <= 0) throw new NumberFormatException();

            int finaleMenge = eingabeMenge;
            if (ausgewaehlterArtikel instanceof entities.Massengutartikel) {
                int packGroesse = ((entities.Massengutartikel) ausgewaehlterArtikel).getPackungsGroesse();
                finaleMenge = eingabeMenge * packGroesse;
            }

            eshop.artikelHinzufuegen(ausgewaehlterArtikel, finaleMenge);

            System.out.println(eingabeMenge + "x " + ausgewaehlterArtikel.getBezeichnung() + " in den Warenkorb gelegt.");
            showAlert(Alert.AlertType.INFORMATION, "Erfolg", eingabeMenge + "x " + ausgewaehlterArtikel.getBezeichnung() + " wurde zum Warenkorb hinzugefügt.");

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Eingabefehler", "Bitte eine gültige positive Zahl als Menge eingeben.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Fehler", ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}