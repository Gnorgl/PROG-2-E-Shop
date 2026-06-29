package ui.gui.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;
import ui.gui.scenes.MainLayoutScene;
import ui.gui.components.CustomButton;
import ui.gui.components.FormRow;

public class CheckoutView extends VBox {
    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;
    private final MainLayoutScene mainLayout;

    public CheckoutView(Eshop eshop, SessionManager session, EshopGUI guiController, MainLayoutScene mainLayout) {
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
        this.setPadding(new Insets(20));
    }

    private void initUI() {
        HBox mainContent = new HBox(30);

        // Links Eingabefelder
        VBox linksBox = new VBox(25);

        // Titel
        Label titleLabel = new Label("Kasse");
        titleLabel.getStyleClass().add("checkout-title");

        // Adresse mit FormRow
        Label adresseTitel = new Label("Lieferadresse");
        adresseTitel.getStyleClass().add("section-subtitle");

        TextField vornameField = new TextField();
        TextField nachnameField = new TextField();
        TextField strasseField = new TextField();
        TextField plzField = new TextField();
        TextField stadtField = new TextField();

        FormRow rowVorname = new FormRow("Vorname:", vornameField);
        FormRow rowNachname = new FormRow("Nachname:", nachnameField);
        FormRow rowStrasse = new FormRow("Straße & Hausnr.:", strasseField);
        FormRow rowPlz = new FormRow("PLZ:", plzField);
        FormRow rowStadt = new FormRow("Stadt:", stadtField);

        VBox adresseBox = new VBox(15);
        adresseBox.getChildren().addAll(rowVorname, rowNachname, rowStrasse, rowPlz, rowStadt);

        // Zahlungsmethoden
        Label zahlungTitel = new Label("Zahlungsmethode");
        zahlungTitel.getStyleClass().add("section-subtitle");

        VBox zahlungBox = new VBox(10);
        ToggleGroup paymentGroup = new ToggleGroup();

        RadioButton rbKredit = new RadioButton("Kreditkarte / EC-Karte");
        rbKredit.setToggleGroup(paymentGroup);
        rbKredit.setSelected(true);

        RadioButton rbPayPal = new RadioButton("PayPal");
        rbPayPal.setToggleGroup(paymentGroup);

        RadioButton rbRechnung = new RadioButton("Rechnung");
        rbRechnung.setToggleGroup(paymentGroup);

        zahlungBox.getChildren().addAll(rbKredit, rbPayPal, rbRechnung);

        // Alle linken Elemente in die linksBox packen
        linksBox.getChildren().addAll(titleLabel, adresseTitel, adresseBox, zahlungTitel, zahlungBox);
        HBox.setHgrow(linksBox, Priority.ALWAYS);

        // Rechts Bestellübersicht
        VBox rechtsBox = createSummaryBox();

        // Hauptinhalt zusammensetzen
        mainContent.getChildren().addAll(linksBox, rechtsBox);


        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // Back-Button
        CustomButton btnBack = new CustomButton("Zurück zum Warenkorb", CustomButton.ButtonType.SECONDARY);
        btnBack.setId("btn-back-kasse");
        btnBack.setOnAction(e -> mainLayout.setCenterView(new WarenkorbView(eshop, session, guiController, mainLayout)));


        this.getChildren().addAll(mainContent, btnBack);
    }

    private VBox createSummaryBox() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.getStyleClass().add("summary-box");
        box.setPrefWidth(300);

        Label lblTitel = new Label("Bestellung abschließen");
        lblTitel.getStyleClass().add("summary-title");

        HBox rowGesamt = new HBox();
        Label lblGesamt = new Label("Gesamt");
        lblGesamt.getStyleClass().add("summary-text-bold");

        Label lblGesamtPreis = new Label("€0,00");
        lblGesamtPreis.getStyleClass().add("summary-price");

        rowGesamt.getChildren().addAll(lblGesamt, new Region(), lblGesamtPreis);
        HBox.setHgrow(rowGesamt.getChildren().get(1), Priority.ALWAYS);

        CustomButton btnKaufen = new CustomButton("Kauf abschließen", CustomButton.ButtonType.PRIMARY);
        btnKaufen.setMaxWidth(Double.MAX_VALUE);

        btnKaufen.setOnAction(e -> {
            System.out.println("Kauf-Logik wird ausgeführt!");
        });

        box.getChildren().addAll(lblTitel, new Separator(), rowGesamt, btnKaufen);
        return box;
    }
}