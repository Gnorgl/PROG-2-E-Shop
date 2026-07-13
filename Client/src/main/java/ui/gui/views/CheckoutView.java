package ui.gui.views;

import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import logic.Eshop;
import logic.SessionManager;
import ui.gui.EshopGUI;
import ui.gui.scenes.MainLayoutScene;
import ui.gui.components.CustomButton;
import ui.gui.components.FormRow;

import java.util.HashMap;

public class CheckoutView extends VBox {
    private final Eshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;
    private final MainLayoutScene mainLayout;

    // Instanzvariablen für die Eingabefelder (damit der Button sie auslesen kann)
    private TextField vornameField;
    private TextField nachnameField;
    private TextField strasseField;
    private TextField plzField;
    private TextField stadtField;
    private Label lblGesamtPreis;
    private Label lblZwischensumme;
    private VBox summaryArtikelBox;

    public CheckoutView(Eshop eshop, SessionManager session, EshopGUI guiController, MainLayoutScene mainLayout) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;
        this.mainLayout = mainLayout;

        // CSS-Klasse für das gesamte Layout setzen (regelt Spacing und Padding)
        this.getStyleClass().add("checkout-view");

        initUI();
        datenLaden(); // Lädt den aktuellen Warenkorb-Preis

        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }
    }

    private void datenLaden() {
        double netto = eshop.getBestellVerwaltungV().berechneNettoSumme(eshop.getWarenkorbVerwaltung().getWarenkorbListe());
        double brutto = eshop.getBestellVerwaltungV().berechneBruttoSumme(eshop.getWarenkorbVerwaltung().getWarenkorbListe());

        summaryArtikelBox.getChildren().clear();
        HashMap<Artikel, Integer> warenkorbMap = eshop.getWarenkorbVerwaltung().getWarenkorbListe().getAlleArtikel();

        for (Artikel a : warenkorbMap.keySet()) {
            int mengeStueck = warenkorbMap.get(a);
            double positionsPreis = a.berechneGesamtpreis(mengeStueck);

            String anzeigeName = a.getBezeichnung();
            int anzeigeMenge = mengeStueck;

            // Umrechnen für die Kassen-Anzeige
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

        lblZwischensumme.setText(String.format("€%.2f", netto));
        lblGesamtPreis.setText(String.format("€%.2f", brutto));
    }

    private void initUI() {
        HBox mainContent = new HBox(30);

        // Links Eingabefelder
        VBox linksBox = new VBox(25);
        Label titleLabel = new Label("Kasse");
        titleLabel.getStyleClass().add("checkout-title");

        Label adresseTitel = new Label("Lieferadresse");
        adresseTitel.getStyleClass().add("section-subtitle");

        vornameField = new TextField();
        nachnameField = new TextField();
        strasseField = new TextField();
        plzField = new TextField();
        stadtField = new TextField();

        // Falls der Nutzer eingeloggt ist, könnten wir hier seine Daten vorausfüllen
        if (session.getBenutzer() instanceof Kunde) {
            Kunde k = (Kunde) session.getBenutzer();
            vornameField.setText(k.getVorname());
            nachnameField.setText(k.getNachname());
        }

        FormRow rowVorname = new FormRow("Vorname:", vornameField);
        FormRow rowNachname = new FormRow("Nachname:", nachnameField);
        FormRow rowStrasse = new FormRow("Straße & Hausnr.:", strasseField);
        FormRow rowPlz = new FormRow("PLZ:", plzField);
        FormRow rowStadt = new FormRow("Stadt:", stadtField);

        VBox adresseBox = new VBox(15);
        adresseBox.getChildren().addAll(rowVorname, rowNachname, rowStrasse, rowPlz, rowStadt);

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

        linksBox.getChildren().addAll(titleLabel, adresseTitel, adresseBox, zahlungTitel, zahlungBox);
        HBox.setHgrow(linksBox, Priority.ALWAYS);

        // Rechts Bestellübersicht
        VBox rechtsBox = createSummaryBox();

        mainContent.getChildren().addAll(linksBox, rechtsBox);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        CustomButton btnBack = new CustomButton("Zurück zum Warenkorb", CustomButton.ButtonType.SECONDARY);
        btnBack.setId("btn-back-kasse");
        btnBack.setOnAction(e -> mainLayout.setCenterView(new WarenkorbView(eshop, session, guiController, mainLayout)));

        this.getChildren().addAll(mainContent, btnBack);
    }

    private VBox createSummaryBox() {
        VBox box = new VBox(15);
        // Breite und Padding
        box.getStyleClass().add("summary-box");

        Label lblTitel = new Label("Bestellung abschließen");
        lblTitel.getStyleClass().add("summary-title");

        // Container für die Artikel
        summaryArtikelBox = new VBox(8);

        // Zwischensumme für den Checkout hinzugefügt
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

        CustomButton btnKaufen = new CustomButton("Kauf abschließen", CustomButton.ButtonType.PRIMARY);
        btnKaufen.setMaxWidth(Double.MAX_VALUE);
        btnKaufen.setOnAction(e -> processCheckout());

        box.getChildren().addAll(lblTitel, new Separator(), summaryArtikelBox, new Separator(), rowZwischen, new Separator(), rowGesamt, btnKaufen);
        return box;
    }

    private void processCheckout() {
        try {
            if (vornameField.getText().trim().isEmpty() || nachnameField.getText().trim().isEmpty() ||
                    strasseField.getText().trim().isEmpty() || plzField.getText().trim().isEmpty() ||
                    stadtField.getText().trim().isEmpty()) {

                showAlert(Alert.AlertType.WARNING, "Fehlende Daten", "Bitte füllen Sie alle Adressfelder aus.");
                return;
            }

            if (!(session.getBenutzer() instanceof Kunde)) {
                showAlert(Alert.AlertType.ERROR, "Fehler", "Nur angemeldete Kunden können einkaufen!");
                return;
            }
            Kunde kunde = (Kunde) session.getBenutzer();

            Rechnung rechnung = eshop.getBestellVerwaltungV().checkOut(
                    kunde,
                    eshop.getWarenkorbVerwaltung().getWarenkorbListe(),
                    eshop.getArtikelVerwaltung()
            );

            if (rechnung == null) {
                showAlert(Alert.AlertType.WARNING, "Warenkorb leer", "Ihr Warenkorb ist leer.");
                return;
            }

            eshop.getWarenkorbVerwaltung().safe();

            // Adresse aus den Feldern für die Rechnung zusammenbauen
            String lieferadresse = vornameField.getText() + " " + nachnameField.getText() + "\n" +
                    strasseField.getText() + "\n" +
                    plzField.getText() + " " + stadtField.getText();

            zeigeRechnung(rechnung, lieferadresse);

            // Nach Kauf zum Katalog leiten
            mainLayout.setCenterView(new KatalogView(eshop, session, guiController));

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Kauf fehlgeschlagen", ex.getMessage());
        }
    }

    private void zeigeRechnung(Rechnung rechnung, String lieferadresse) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Kaufbestätigung");
        alert.setHeaderText("Vielen Dank für Ihren Einkauf!");

        String belegText = eshop.getBestellVerwaltungV().generiereRechnungsText(rechnung, lieferadresse);

        TextArea textArea = new TextArea(belegText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.getStyleClass().add("invoice-text"); // CSS Klasse statt setStyle
        textArea.setPrefHeight(350);
        textArea.setPrefWidth(450);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
