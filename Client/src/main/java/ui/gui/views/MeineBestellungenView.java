package ui.gui.views;

import entities.Kunde;
import entities.Rechnung;
import interfaces.InterfaceEshop;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import ui.gui.EshopGUI;
import ui.gui.SessionManager;

import java.util.List;

public class MeineBestellungenView extends VBox {
    private final InterfaceEshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    private TableView<Rechnung> bestellungenTable;
    private ObservableList<Rechnung> bestellungenListe;

    public MeineBestellungenView(InterfaceEshop eshop, SessionManager session, EshopGUI guiController) {
        this.eshop = eshop;
        this.session = session;
        this.guiController = guiController;

        try {
            String cssPath = getClass().getResource("/ui/gui/css/style.css").toExternalForm();
            this.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("CSS Datei nicht gefunden!");
        }
        this.getStyleClass().add("artikel-verwaltung-view");

        initUI();
        datenLaden();
    }

    private void initUI() {
        Label titelLabel = new Label("Meine Bestellungen");
        titelLabel.getStyleClass().add("view-title");

        bestellungenTable = new TableView<>();

        TableColumn<Rechnung, Integer> colNr = new TableColumn<>("Rechnungs-Nr.");
        colNr.setCellValueFactory(new PropertyValueFactory<>("rechnungsNummer"));

        TableColumn<Rechnung, String> colDatum = new TableColumn<>("Datum");
        colDatum.setCellValueFactory(cellData ->
                new javafx.beans.property.ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getDatum())));

        TableColumn<Rechnung, String> colNetto = new TableColumn<>("Netto (€)");
        colNetto.setCellValueFactory(cellData ->
                new javafx.beans.property.ReadOnlyStringWrapper(String.format("%.2f", cellData.getValue().getNettosumme())));

        TableColumn<Rechnung, String> colBrutto = new TableColumn<>("Brutto (€)");
        colBrutto.setCellValueFactory(cellData ->
                new javafx.beans.property.ReadOnlyStringWrapper(String.format("%.2f", cellData.getValue().getBruttoSumme())));

        TableColumn<Rechnung, Void> colAction = new TableColumn<>("Beleg");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Anzeigen");

            {
                btn.setOnAction(event -> {
                    Rechnung rechnung = getTableView().getItems().get(getIndex());
                    zeigeBeleg(rechnung);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        bestellungenTable.getColumns().addAll(colNr, colDatum, colNetto, colBrutto, colAction);

        this.getChildren().addAll(titelLabel, bestellungenTable);
    }

    private void datenLaden() {
        if (!(session.getBenutzer() instanceof Kunde)) {
            return;
        }
        Kunde kunde = (Kunde) session.getBenutzer();
        List<Rechnung> rechnungen = eshop.getRechnungenFuerKunde(kunde);
        bestellungenListe = FXCollections.observableArrayList(rechnungen);
        bestellungenTable.setItems(bestellungenListe);
    }

    private void zeigeBeleg(Rechnung rechnung) {
        String belegText = eshop.generiereRechnungsText(rechnung, "(Lieferadresse nicht gespeichert)");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Beleg");
        alert.setHeaderText("Rechnung Nr. " + rechnung.getRechnungsNummer());

        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(belegText);
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.getStyleClass().add("invoice-text");

        textArea.setStyle("-fx-font-family: 'Courier New', monospace;");
        textArea.setPrefHeight(350);
        textArea.setPrefWidth(450);

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/ui/gui/css/style.css").toExternalForm());
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
}
