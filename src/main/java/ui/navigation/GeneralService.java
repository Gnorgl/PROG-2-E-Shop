package ui.navigation;

import logic.Eshop;

import java.util.Scanner;

public class GeneralService {
    private final Eshop eshop;
    private final Scanner scanner;
    private final SessionManager session;

    public GeneralService(Eshop eshop, Scanner scanner, SessionManager session) {
        this.eshop = eshop;
        this.scanner = scanner;
        this.session = session;
    }
}
