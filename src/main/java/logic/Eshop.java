package logic;

public class Eshop {
    private Artikelmanager artikelmanager = new Artikelmanager();
    private KundenVerwaltung kundenVerwaltung = new KundenVerwaltung();
    private MitarbeiterVerwaltung mitarbeiter = new MitarbeiterVerwaltung();
    private ShoppingService shoppingService = new ShoppingService();
    private UserManager userManager = new UserManager();

    public Eshop() {}

    //Verwaltungsklassen können keine Interfaces sein, da ein Interface keine Attribute haben kann.
    //Die Klassen brauchen aber Attribute für die Methoden.
    //Nutzen stattdessen Komposition.



}

//Bündelt alle anderen Logic Komponenten und kommuniziert direkt mit UI.
// Hier werden alle anderen Logik Objekte erstellt. Und Admin Mitarbeiter wird erstellt.
// Konstruktor für alle Interfaces? Attribute werden weiter gegeben, damit man die Methoden nutzen kann.
