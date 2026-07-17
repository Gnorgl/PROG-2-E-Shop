package ui.gui.views;

import entities.Artikel;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;
import ui.gui.scenes.MainLayoutScene;
import ui.gui.components.CustomButton;

import java.util.HashMap;

public class WarenkorbView extends VBox {
    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;
    private final MainLayoutScene mainLayout;

    private TableView<Artikel> warenkorbTable;
    private ObservableList<Artikel> warenkorbListe;
    private Label lblZwischensumme;
    private Label lblGesamtPreis;
    private VBox summaryArtikelBox;

    public WarenkorbView(Eshop eshop, SessionManager session, EshopGUI guiController, MainLayoutScene mainLayout) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;
        this.mainLayout = mainLayout;

        // CSS-Klasse für das gesamte Layout setzen
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

        // Links Artikelliste
        VBox linksBox = new VBox(15);

        Label titleLabel = new Label("Mein Warenkorb");
        titleLabel.getStyleClass().add("checkout-title");

        warenkorbTable = new TableView<>();

        // 1. Spalte: Menge
        TableColumn<Artikel, Integer> colMenge = new TableColumn<>("Menge");
        colMenge.setCellValueFactory(cellData -> {
            Artikel a = cellData.getValue();
            int mengeStueck = eshop.getWarenkorbVerwaltung().getMenge(a);

            if (a instanceof entities.Massengutartikel) {
                int packGroesse = ((entities.Massengutartikel) a).getPackungsGroesse();
                return new ReadOnlyObjectWrapper<>(mengeStueck / packGroesse);
            }
            return new ReadOnlyObjectWrapper<>(mengeStueck);
        });

        // 2. Spalte: Bezeichnung (Packungsgröße anhängen)
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

        // 3. Spalte: Einzelpreis
        TableColumn<Artikel, Double> colPreis = new TableColumn<>("Einzelpreis (€)");
        colPreis.setCellValueFactory(new PropertyValueFactory<>("preis"));

        // 4. Spalte: Entfernen-Button
        TableColumn<Artikel, Void> colAction = new TableColumn<>("Aktion");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("X");

            {

                btn.getStyleClass().add("delete-table-btn");
                btn.setOnAction(event -> {
                    Artikel artikel = getTableView().getItems().get(getIndex());
                    // Artikel aus der Logik entfernen
                    eshop.getWarenkorbVerwaltung().artikelEntfernen(artikel);
                    // View sofort neu laden, damit die Summen und die Tabelle aktualisiert werden
                    datenLaden();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn); // Zeigt den Button in der Zelle an
                }
            }
        });

        warenkorbTable.getColumns().addAll(colMenge, colBez, colPreis, colAction);
        VBox.setVgrow(warenkorbTable, Priority.ALWAYS);

        linksBox.getChildren().addAll(titleLabel, warenkorbTable);
        HBox.setHgrow(linksBox, Priority.ALWAYS);

        // Rechts Zusammenfassung Bestellung
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
        rowZwischen.getChildren().addAll(new Label("Zwischensumme"), new Region(), lblZwischensumme);
        HBox.setHgrow(rowZwischen.getChildren().get(1), Priority.ALWAYS);

        HBox rowGesamt = new HBox();
        Label lblGesamt = new Label("Gesamt");
        lblGesamt.getStyleClass().add("summary-text-bold");

        lblGesamtPreis = new Label("€0,00");
        lblGesamtPreis.getStyleClass().add("summary-price");

        rowGesamt.getChildren().addAll(lblGesamt, new Region(), lblGesamtPreis);
        HBox.setHgrow(rowGesamt.getChildren().get(1), Priority.ALWAYS);

        CustomButton btnZurKasse = new CustomButton("Zur Kasse ➔", CustomButton.ButtonType.PRIMARY);
        btnZurKasse.setMaxWidth(Double.MAX_VALUE);
        btnZurKasse.setOnAction(e -> {
            // Verhindert das Navigieren zur Kasse, wenn der Korb leer ist
            if (eshop.getWarenkorbVerwaltung().istLeer()) {
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

    private void datenLaden() {
        HashMap<Artikel, Integer> warenkorbMap = eshop.getWarenkorbVerwaltung().getAlleArtikel();

        warenkorbListe = javafx.collections.FXCollections.observableArrayList(warenkorbMap.keySet());
        warenkorbTable.setItems(warenkorbListe);

        summaryArtikelBox.getChildren().clear();

        for (Artikel a : warenkorbMap.keySet()) {
            int mengeStueck = warenkorbMap.get(a);
            double positionsPreis = a.berechneGesamtpreis(mengeStueck);

            String anzeigeName = a.getBezeichnung();
            int anzeigeMenge = mengeStueck;

            // NEU: AnzeigeMenge berechnen
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

        double gesamt = eshop.getBestellVerwaltungV().berechneNettoSumme(eshop.getWarenkorbVerwaltung().getAlleArtikel());

        String preisString = String.format("€%.2f", gesamt);
        lblZwischensumme.setText(preisString);
        lblGesamtPreis.setText(preisString);
    }
}