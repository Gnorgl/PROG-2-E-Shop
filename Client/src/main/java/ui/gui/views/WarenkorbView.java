package ui.gui.views;

import entities.Artikel;
import interfaces.InterfaceEshop;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import ui.gui.EshopGUI;
import ui.gui.SessionManager;
import ui.gui.scenes.MainLayoutScene;
import ui.gui.components.CustomButton;

import java.util.HashMap;

public class WarenkorbView extends VBox {
    private final InterfaceEshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;
    private final MainLayoutScene mainLayout;

    private TableView<Artikel> warenkorbTable;
    private ObservableList<Artikel> warenkorbListe;
    private Label lblZwischensumme;
    private Label lblGesamtPreis;
    private VBox summaryArtikelBox;

    public WarenkorbView(InterfaceEshop eshop, SessionManager session, EshopGUI guiController, MainLayoutScene mainLayout) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;
        this.mainLayout = mainLayout;

        this.getStyleClass().add("warenkorb-view");

        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }

        initUI();
        datenLaden();
    }

    private void initUI() {
        HBox mainContent = new HBox(30);

        VBox linksBox = new VBox(15);

        Label titleLabel = new Label("Mein Warenkorb");
        titleLabel.getStyleClass().add("checkout-title");

        warenkorbTable = new TableView<>();

        TableColumn<Artikel, Void> colMenge = new TableColumn<>("Menge");
        colMenge.setCellFactory(param -> new TableCell<>() {
            private final Button btnMinus = new Button("-");
            private final Button btnPlus = new Button("+");
            private final Label lblMenge = new Label();
            private final HBox box = new HBox(6, btnMinus, lblMenge, btnPlus);

            {
                box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                btnMinus.getStyleClass().add("menge-btn");
                btnPlus.getStyleClass().add("menge-btn");
                btnMinus.setOnAction(event -> mengeAendern(getTableView().getItems().get(getIndex()), -1));
                btnPlus.setOnAction(event -> mengeAendern(getTableView().getItems().get(getIndex()), +1));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Artikel a = getTableView().getItems().get(getIndex());
                    lblMenge.setText(String.valueOf(anzeigeMenge(a)));
                    setGraphic(box);
                }
            }
        });

        // Bezeichnung
        TableColumn<Artikel, String> colBez = new TableColumn<>("Artikel");
        colBez.setCellValueFactory(cellData -> {
            Artikel a = cellData.getValue();
            if (a instanceof entities.Massengutartikel) {
                int packGroesse = ((entities.Massengutartikel) a).getPackungsGroesse();
                return new javafx.beans.property.ReadOnlyStringWrapper(a.getBezeichnung() + " (" + packGroesse + "er Pack)");
            }
            return new javafx.beans.property.ReadOnlyStringWrapper(a.getBezeichnung());
        });
        colBez.setPrefWidth(200);

        // Einzelpreis
        TableColumn<Artikel, Double> colPreis = new TableColumn<>("Einzelpreis (€)");
        colPreis.setCellValueFactory(new PropertyValueFactory<>("preis"));

        // Entfernen-Button
        TableColumn<Artikel, Void> colAction = new TableColumn<>("Aktion");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("X");

            {

                btn.getStyleClass().add("delete-table-btn");
                btn.setOnAction(event -> {
                    Artikel artikel = getTableView().getItems().get(getIndex());
                    // Artikel aus der Logik entfernen
                    try {
                        eshop.artikelEntfernen(artikel);
                    } catch (java.io.IOException ex) {
                        System.err.println("Fehler beim Entfernen aus dem Warenkorb: " + ex.getMessage());
                    }
                    datenLaden();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        warenkorbTable.getColumns().addAll(colMenge, colBez, colPreis, colAction);
        VBox.setVgrow(warenkorbTable, Priority.ALWAYS);

        linksBox.getChildren().addAll(titleLabel, warenkorbTable);
        HBox.setHgrow(linksBox, Priority.ALWAYS);

        VBox rechtsBox = createSummaryBox();

        mainContent.getChildren().addAll(linksBox, rechtsBox);
        this.getChildren().add(mainContent);
    }

    private VBox createSummaryBox() {
        VBox box = new VBox(15);

        box.getStyleClass().add("summary-box");

        Label lblTitel = new Label("Bestellübersicht");
        lblTitel.getStyleClass().add("summary-title");

        summaryArtikelBox = new VBox(8);

        HBox rowZwischen = new HBox();
        lblZwischensumme = new Label("€0,00");
        rowZwischen.getChildren().addAll(new Label("Netto-Zwischensumme"), new Region(), lblZwischensumme);
        HBox.setHgrow(rowZwischen.getChildren().get(1), Priority.ALWAYS);

        HBox rowGesamt = new HBox();
        Label lblGesamt = new Label("Brutto Gesamt");
        lblGesamt.getStyleClass().add("summary-text-bold");

        lblGesamtPreis = new Label("€0,00");
        lblGesamtPreis.getStyleClass().add("summary-price");

        rowGesamt.getChildren().addAll(lblGesamt, new Region(), lblGesamtPreis);
        HBox.setHgrow(rowGesamt.getChildren().get(1), Priority.ALWAYS);

        CustomButton btnZurKasse = new CustomButton("Zur Kasse ➔", CustomButton.ButtonType.PRIMARY);
        btnZurKasse.setMaxWidth(Double.MAX_VALUE);
        btnZurKasse.setOnAction(e -> {
            if (eshop.istLeer()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Dein Warenkorb ist leer!");
                alert.setHeaderText(null);
                alert.showAndWait();
            } else {
                mainLayout.setCenterView(new CheckoutView(eshop, session, guiController, mainLayout));
            }
        });

        box.getChildren().addAll(lblTitel, new Separator(), summaryArtikelBox, new Separator(), rowZwischen, new Separator(), rowGesamt, btnZurKasse);
        return box;
    }

    // Zeigt bei Massengutartikeln die Anzahl Packungen, sonst die Stückzahl
    private int anzeigeMenge(Artikel a) {
        int mengeStueck = eshop.getMenge(a);
        if (a instanceof entities.Massengutartikel) {
            int packGroesse = ((entities.Massengutartikel) a).getPackungsGroesse();
            return mengeStueck / packGroesse;
        }
        return mengeStueck;
    }

    // Ändert die Stückzahl eines Artikels im Warenkorb um +/-1 Einheit (bzw. +/- 1 Packung
    // bei Massengutartikeln). Erreicht die Menge 0, wird der Artikel komplett entfernt.
    private void mengeAendern(Artikel a, int deltaPackungen) {
        int schrittStueck = 1;
        if (a instanceof entities.Massengutartikel) {
            schrittStueck = ((entities.Massengutartikel) a).getPackungsGroesse();
        }

        int aktuelleMengeStueck = eshop.getMenge(a);
        int neueMengeStueck = aktuelleMengeStueck + (deltaPackungen * schrittStueck);

        try {
            if (neueMengeStueck <= 0) {
                eshop.artikelEntfernen(a);
            } else {
                eshop.artikelMengeAendern(a, neueMengeStueck);
            }
            datenLaden();
        } catch (exceptions.artikel.BestandNichtAusreichendException ex) {
            showAlert(Alert.AlertType.WARNING, "Nicht genug auf Lager", ex.getMessage());
            datenLaden(); // Tabelle neu laden, falls sich der Bestand zwischenzeitlich geändert hat
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Menge konnte nicht geändert werden: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void datenLaden() {
        HashMap<Artikel, Integer> warenkorbMap = eshop.getAlleWarenkorbArtikel();

        warenkorbListe = javafx.collections.FXCollections.observableArrayList(warenkorbMap.keySet());
        warenkorbTable.setItems(warenkorbListe);
        warenkorbTable.refresh();

        summaryArtikelBox.getChildren().clear();

        for (Artikel a : warenkorbMap.keySet()) {
            int mengeStueck = warenkorbMap.get(a);
            double positionsPreis = a.berechneGesamtpreis(mengeStueck);

            String anzeigeName = a.getBezeichnung();
            int anzeigeMenge = mengeStueck;

            if (a instanceof entities.Massengutartikel) {
                int packGroesse = ((entities.Massengutartikel) a).getPackungsGroesse();
                anzeigeMenge = mengeStueck / packGroesse;
                anzeigeName += " (" + packGroesse + "er Pack)";
            }

            HBox itemRow = new HBox();
            Label lblItemName = new Label(anzeigeMenge + "x " + anzeigeName);
            Label lblItemPreis = new Label(String.format("€%.2f", positionsPreis));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            itemRow.getChildren().addAll(lblItemName, spacer, lblItemPreis);
            summaryArtikelBox.getChildren().add(itemRow);
        }

        double netto = eshop.berechneNettoSumme(eshop.getAlleWarenkorbArtikel());
        double brutto = eshop.berechneBruttoSumme(eshop.getAlleWarenkorbArtikel());

        lblZwischensumme.setText(String.format("€%.2f", netto));
        lblGesamtPreis.setText(String.format("€%.2f", brutto));
    }
}