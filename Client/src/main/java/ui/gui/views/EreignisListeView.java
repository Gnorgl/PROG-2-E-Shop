package ui.gui.views;

import entities.Ereignis;
import interfaces.InterfaceEshop;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.gui.EshopGUI;
import ui.gui.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeSet;

// Allgemeine Ereignisübersicht (alle Lagerereignisse: Ein-/Auslagerungen), sortier- und
// filterbar - siehe Blatt 3, Aufgabe 3: "Liste der (Lager-)Ereignisse ... Möglichkeit bieten,
// die Ereignisse nach Datum zu sortieren und nach Artikel / Person / Ereignistyp zu filtern."
public class EreignisListeView extends VBox {

    private static final String ALLE = "Alle";
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final InterfaceEshop eshop;
    private final SessionManager session;
    private final EshopGUI guiController;

    private TableView<Ereignis> table;
    private ObservableList<Ereignis> alleEreignisse;
    private FilteredList<Ereignis> gefiltert;
    private SortedList<Ereignis> sortiert;

    private ComboBox<String> filterArtikelBox;
    private ComboBox<String> filterPersonBox;
    private ComboBox<String> filterTypBox;

    public EreignisListeView(InterfaceEshop eshop, SessionManager session, EshopGUI guiController) {
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

        // Auch diese Übersicht soll sich automatisch aktualisieren, wenn ein anderer
        // Client Bestand ändert (Kauf, Ein-/Auslagerung) - gleiches Muster wie bei
        // ArtikelVerwaltungView/KatalogView, inkl. sauberem Abmelden beim View-Wechsel.
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
        Label titelLabel = new Label("Ereignisprotokoll");
        titelLabel.getStyleClass().add("view-title");

        HBox filterBar = new HBox(15);
        filterBar.getStyleClass().add("katalog-bar");

        filterArtikelBox = new ComboBox<>();
        filterPersonBox = new ComboBox<>();
        filterTypBox = new ComboBox<>();
        filterArtikelBox.setOnAction(e -> aktualisiereFilter());
        filterPersonBox.setOnAction(e -> aktualisiereFilter());
        filterTypBox.setOnAction(e -> aktualisiereFilter());

        filterBar.getChildren().addAll(
                new Label("Artikel:"), filterArtikelBox,
                new Label("Person:"), filterPersonBox,
                new Label("Ereignistyp:"), filterTypBox
        );

        table = new TableView<>();
        VBox.setVgrow(table, Priority.ALWAYS);

        // Zellwert ist der echte LocalDateTime (sortierbar via Comparable), die Anzeige
        // wird erst in der CellFactory als Text formatiert - so sortiert die Spalte nach
        // dem tatsächlichen Zeitpunkt und nicht lexikografisch nach dem formatierten String.
        TableColumn<Ereignis, java.time.LocalDateTime> colDatum = new TableColumn<>("Zeitpunkt");
        colDatum.setCellValueFactory(new PropertyValueFactory<>("zeitstempel"));
        colDatum.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(java.time.LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(FORMAT));
            }
        });
        colDatum.setPrefWidth(160);

        TableColumn<Ereignis, String> colTyp = new TableColumn<>("Ereignistyp");
        colTyp.setCellValueFactory(new PropertyValueFactory<>("typ"));
        colTyp.setPrefWidth(140);

        TableColumn<Ereignis, String> colArtikel = new TableColumn<>("Artikel");
        colArtikel.setCellValueFactory(cellData -> {
            entities.Artikel a = cellData.getValue().getArtikel();
            return new javafx.beans.property.ReadOnlyStringWrapper(a != null ? a.getBezeichnung() : "-");
        });
        colArtikel.setPrefWidth(200);

        TableColumn<Ereignis, Integer> colAnzahl = new TableColumn<>("Anzahl");
        colAnzahl.setCellValueFactory(new PropertyValueFactory<>("anzahl"));
        colAnzahl.setPrefWidth(80);

        TableColumn<Ereignis, String> colPerson = new TableColumn<>("Person");
        colPerson.setCellValueFactory(cellData -> {
            entities.Benutzer b = cellData.getValue().getBenutzer();
            String text = b != null ? (b.getVorname() + " " + b.getNachname()) : "-";
            return new javafx.beans.property.ReadOnlyStringWrapper(text);
        });
        colPerson.setPrefWidth(180);

        table.getColumns().addAll(colDatum, colTyp, colArtikel, colAnzahl, colPerson);

        // Standard-Sortierung: neuestes Ereignis zuerst
        table.getSortOrder().add(colDatum);
        colDatum.setSortType(TableColumn.SortType.DESCENDING);

        this.getChildren().addAll(titelLabel, filterBar, table);
    }

    private void datenLaden() {
        List<Ereignis> geladen = eshop.getAlleEreignisse();
        alleEreignisse = FXCollections.observableArrayList(geladen);

        befuelleFilterOptionen();

        gefiltert = new FilteredList<>(alleEreignisse, e -> true);
        sortiert = new SortedList<>(gefiltert);
        sortiert.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortiert);

        aktualisiereFilter();
    }

    // Baut die Filter-ComboBoxen aus den tatsächlich vorhandenen Werten (statt fest codierter Listen)
    private void befuelleFilterOptionen() {
        TreeSet<String> artikelNamen = new TreeSet<>();
        TreeSet<String> personen = new TreeSet<>();
        TreeSet<String> typen = new TreeSet<>();

        for (Ereignis e : alleEreignisse) {
            if (e.getArtikel() != null) {
                artikelNamen.add(e.getArtikel().getBezeichnung());
            }
            if (e.getBenutzer() != null) {
                personen.add(e.getBenutzer().getVorname() + " " + e.getBenutzer().getNachname());
            }
            if (e.getTyp() != null) {
                typen.add(e.getTyp());
            }
        }

        String bisherArtikel = filterArtikelBox.getValue();
        String bisherPerson = filterPersonBox.getValue();
        String bisherTyp = filterTypBox.getValue();

        filterArtikelBox.getItems().setAll(ALLE);
        filterArtikelBox.getItems().addAll(artikelNamen);
        filterArtikelBox.setValue(bisherArtikel != null ? bisherArtikel : ALLE);

        filterPersonBox.getItems().setAll(ALLE);
        filterPersonBox.getItems().addAll(personen);
        filterPersonBox.setValue(bisherPerson != null ? bisherPerson : ALLE);

        filterTypBox.getItems().setAll(ALLE);
        filterTypBox.getItems().addAll(typen);
        filterTypBox.setValue(bisherTyp != null ? bisherTyp : ALLE);
    }

    private void aktualisiereFilter() {
        if (gefiltert == null) return;

        String artikelFilter = filterArtikelBox.getValue();
        String personFilter = filterPersonBox.getValue();
        String typFilter = filterTypBox.getValue();

        gefiltert.setPredicate(ereignis -> {
            boolean passtArtikel = ALLE.equals(artikelFilter) || ereignis.getArtikel() == null
                    || ereignis.getArtikel().getBezeichnung().equals(artikelFilter);
            boolean passtPerson = ALLE.equals(personFilter) || ereignis.getBenutzer() == null
                    || (ereignis.getBenutzer().getVorname() + " " + ereignis.getBenutzer().getNachname()).equals(personFilter);
            boolean passtTyp = ALLE.equals(typFilter) || typFilter.equals(ereignis.getTyp());
            return passtArtikel && passtPerson && passtTyp;
        });
    }
}
