package ui.gui.views;

import entities.Artikel;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;
import ui.gui.scenes.MainLayoutScene;
import ui.gui.components.CustomButton;

public class WarenkorbView extends VBox {
    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;
    private final MainLayoutScene mainLayout;

    private TableView<Artikel> warenkorbTable;
    private ObservableList<Artikel> warenkorbListe;

    public WarenkorbView(Eshop eshop, SessionManager session, EshopGUI guiController, MainLayoutScene mainLayout) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;
        this.mainLayout = mainLayout;

        this.getStyleClass().add("article-container");

        initUI();


        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }

        this.setSpacing(15);
        this.setPadding(new Insets(20)); // Ersetzt die vorherigen Insets(30)
    }

    private void initUI() {
        HBox mainContent = new HBox(30);

        // Links Artikelliste
        VBox linksBox = new VBox(15);

        Label titleLabel = new Label("Mein Warenkorb");
        titleLabel.getStyleClass().add("checkout-title");

        warenkorbTable = new TableView<>();
        TableColumn<Artikel, String> colBez = new TableColumn<>("Artikel");
        colBez.setCellValueFactory(new PropertyValueFactory<>("bezeichnung"));
        colBez.setPrefWidth(250);

        TableColumn<Artikel, Double> colPreis = new TableColumn<>("Preis");
        colPreis.setCellValueFactory(new PropertyValueFactory<>("preis"));

        warenkorbTable.getColumns().addAll(colBez, colPreis);
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
        box.setPadding(new Insets(20));
        box.getStyleClass().add("summary-box");
        box.setPrefWidth(300);

        Label lblTitel = new Label("Bestellübersicht");
        lblTitel.getStyleClass().add("summary-title");

        HBox rowZwischen = new HBox();
        rowZwischen.getChildren().addAll(new Label("Zwischensumme"), new Region(), new Label("€0,00"));
        HBox.setHgrow(rowZwischen.getChildren().get(1), Priority.ALWAYS);

        HBox rowGesamt = new HBox();
        Label lblGesamt = new Label("Gesamt");
        lblGesamt.getStyleClass().add("summary-text-bold");

        Label lblGesamtPreis = new Label("€0,00");
        lblGesamtPreis.getStyleClass().add("summary-price");

        rowGesamt.getChildren().addAll(lblGesamt, new Region(), lblGesamtPreis);
        HBox.setHgrow(rowGesamt.getChildren().get(1), Priority.ALWAYS);

        CustomButton btnZurKasse = new CustomButton("Zur Kasse ➔", CustomButton.ButtonType.PRIMARY);
        btnZurKasse.setMaxWidth(Double.MAX_VALUE);

        btnZurKasse.setOnAction(e -> {
            mainLayout.setCenterView(new CheckoutView(eshop, session, guiController, mainLayout));
        });

        box.getChildren().addAll(lblTitel, new Separator(), rowZwischen, new Separator(), rowGesamt, btnZurKasse);
        return box;
    }
}