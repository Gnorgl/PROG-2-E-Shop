package ui.gui.views;

import entities.Artikel;
import entities.Kunde;
import entities.Rechnung;
import interfaces.InterfaceEshop;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.gui.EshopGUI;
import ui.gui.SessionManager;
import ui.gui.scenes.MainLayoutScene;
import ui.gui.components.CustomButton;
import ui.gui.components.FormRow;

import java.util.HashMap;

public class CheckoutView extends VBox {
    private final InterfaceEshop eshop;
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

    // Zahlungsmethode
    private RadioButton rbKredit;
    private RadioButton rbPayPal;
    private RadioButton rbRechnung;

    public CheckoutView(InterfaceEshop eshop, SessionManager session, EshopGUI guiController, MainLayoutScene mainLayout) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;
        this.mainLayout = mainLayout;

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
        HashMap<Artikel, Integer> warenkorbMap = eshop.getAlleWarenkorbArtikel();
        double netto = eshop.berechneNettoSumme(warenkorbMap);
        double brutto = eshop.berechneBruttoSumme(warenkorbMap);

        summaryArtikelBox.getChildren().clear();

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

        rbKredit = new RadioButton("Kreditkarte / EC-Karte");
        rbKredit.setToggleGroup(paymentGroup);
        rbKredit.setSelected(true);

        rbPayPal = new RadioButton("PayPal");
        rbPayPal.setToggleGroup(paymentGroup);

        rbRechnung = new RadioButton("Rechnung");
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
        box.getStyleClass().add("summary-box");

        Label lblTitel = new Label("Bestellung abschließen");
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

            if (!zahlungAbwickeln()) {
                return;
            }

            Kunde kunde = (Kunde) session.getBenutzer();

            Rechnung rechnung = eshop.checkOut(
                    kunde,
                    eshop.getAlleWarenkorbArtikel(),
                    eshop
            );

            if (rechnung == null) {
                showAlert(Alert.AlertType.WARNING, "Warenkorb leer", "Ihr Warenkorb ist leer.");
                return;
            }

            eshop.leeren();

            String lieferadresse = vornameField.getText() + " " + nachnameField.getText() + "\n" +
                    strasseField.getText() + "\n" +
                    plzField.getText() + " " + stadtField.getText();

            zeigeRechnung(rechnung, lieferadresse);

            mainLayout.setCenterView(new KatalogView(eshop, session, guiController));

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Kauf fehlgeschlagen", ex.getMessage());
        }
    }

    private boolean zahlungAbwickeln() {
        if (rbKredit.isSelected()) {
            return zeigeKreditkartenDialog();
        } else if (rbPayPal.isSelected()) {
            return zeigePayPalDialog();
        } else {
            // Rechnung: kein Popup nötig, direkt weiter
            return true;
        }
    }

    private boolean zeigeKreditkartenDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Kreditkarte / EC-Karte");
        dialog.setHeaderText("Bitte Kartendaten eingeben (Fake, es wird nichts wirklich belastet)");

        ButtonType bezahlenBtn = new ButtonType("Bezahlen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bezahlenBtn, ButtonType.CANCEL);

        TextField kartennummerField = new TextField();
        kartennummerField.setPromptText("1234 5678 9012 3456");
        TextField ablaufField = new TextField();
        ablaufField.setPromptText("MM/JJ");
        TextField cvcField = new TextField();
        cvcField.setPromptText("CVC");

        VBox content = new VBox(10,
                new FormRow("Kartennummer:", kartennummerField),
                new FormRow("Ablaufdatum:", ablaufField),
                new FormRow("CVC:", cvcField)
        );
        content.setPrefWidth(320);
        dialog.getDialogPane().setContent(content);

        // Verhindert das Schließen, solange Pflichtfelder leer sind
        Button okButton = (Button) dialog.getDialogPane().lookupButton(bezahlenBtn);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (kartennummerField.getText().trim().isEmpty() || ablaufField.getText().trim().isEmpty()
                    || cvcField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Fehlende Angaben", "Bitte alle Kartenfelder ausfüllen.");
                event.consume();
            }
        });

        java.util.Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == bezahlenBtn;
    }

    private boolean zeigePayPalDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("PayPal");
        alert.setHeaderText("Weiterleitung zu PayPal (Fake)");
        alert.setContentText("Sie werden jetzt zu PayPal weitergeleitet, um die Zahlung abzuschließen.");

        ButtonType weiterBtn = new ButtonType("Bei PayPal bezahlen", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(weiterBtn, ButtonType.CANCEL);

        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == weiterBtn;
    }

    private void zeigeRechnung(Rechnung rechnung, String lieferadresse) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Kaufbestätigung");
        alert.setHeaderText("Vielen Dank für Ihren Einkauf!");

        String belegText = eshop.generiereRechnungsText(rechnung, lieferadresse);

        TextArea textArea = new TextArea(belegText);
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.getStyleClass().add("invoice-text");
        // Alerts laden das Stylesheet der View nicht automatisch mit (eigenes Fenster),
        // daher zusätzlich direkt setzen, damit die feste Zeichenbreite der Rechnung
        // (siehe CheckOutVerwaltung.generiereRechnungsText) auch wirklich monospace
        // dargestellt wird und Netto/MwSt/Brutto sauber untereinanderstehen.
        textArea.setStyle("-fx-font-family: 'Courier New', monospace;");
        textArea.setPrefHeight(350);
        textArea.setPrefWidth(450);

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/ui/gui/css/style.css").toExternalForm());
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
