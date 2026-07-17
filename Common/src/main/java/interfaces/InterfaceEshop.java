package interfaces;

import interfaces.moduls.*;

public interface InterfaceEshop extends IAV, IBV, ICV, IKV, IMV, IOV, IWV{
    //Kann leer bleiben. Also hier nichts reinschreiben.
    //
    // Wir müssen nur die ganzen moduls-Interfaces um die wichtigen Methoden aus den Verwaltungsklassen ergänzen.
    //Also alle Logik Methoden, die in den GUI Klassen direkt aufgerufen werden, müssen mit @Override markiert sein und müssen
    //sich in den moduls-Interfaces befinden!
}
