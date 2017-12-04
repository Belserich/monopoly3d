//
//
//import de.btu.monopoly.core.FieldManager;
//import de.btu.monopoly.core.Game;
//import de.btu.monopoly.core.PlayerService;
//import de.btu.monopoly.data.*;
//import de.btu.monopoly.data.field.*;
//import java.util.logging.Level;
//
///**
// * In dieser Klasse werden saemtliche User-Storys einzeln getestet. Ist eine User-Story erfolgreich gestestet worden, wird ein
// * <code> System.out.println("passed : User-Story")</code> ausgegeben. Für jeden Testparameter wird im Falle eines
// * fehlgeschlagenen Tests ein <code> assert [TEST] : "failed : Fehlerbeschreibung";</code> ausgegeben.
// *
// * @author Christian Prinz
// */
//public class MainTest {
//
//    private static Game gc;
//    private static GameBoard board;
//    private static Player[] players;
//    private static FieldManager fm;
//
//    public static void main(String[] args) throws Exception {
//
//        // Variablen und Initialisierung:
//        gc = new Game(2);
//        Game.LOGGER.setLevel(Level.WARNING);  // Logger des GameControllers ausschalten
//        gc.init();
//        board = gc.board;
//        players = gc.players;
//        fm = gc.getFieldManager();
//
//        // Testmethoden:
//        testGameBoard();
//        testRollMethod();
//        testGoField();
//        testCornerFields();
//        // Steuer auf TaxFeld bezahlen
//        testBuyStreet();
////        testBuyBuilding();
////        testSellBuilding();
////        testPayRent();
////        testTakeMortgage();
////        testPayMortgage();
////        testBankrupt();
////        testGoToJail();
////        testDoubletToJail();
////        // aus dem Gefaengnis mit Freikarte
////        // aus dem Gefaengnis freiwuerfeln
////        testPayPrisonDeposit();
//
//    }
//
//    /**
//     * @author Christian Prinz
//     * @param gc
//     * @param player
//     */
//    private static void testBankrupt() {
//        Player player = players[0];
//        Player notSpectator = players[1];
//
//        // Attribute setzen
//        notSpectator.setBankrupt(false);
//        player.setBankrupt(false);
//        StreetField street1 = (StreetField) gc.board.getFields()[1];
//        street1.setOwner(player);                                   // Eine Strasse geben
//        StreetField street2 = (StreetField) gc.board.getFields()[3];
//        street2.setOwner(player);                                   // Zweite Strasse geben
//        Property station = (Property) gc.board.getFields()[5];
//        station.setOwner(player);                                   // Eine Property geben
//
//        street1.setHouseCount(3);                                   // Strasse 1 bebauen
//        street2.setMortgageTaken(true);                             // Strasse 2 Hypothek aufnehmen
//
//        // Methode aufrufen
//        PlayerService.bankrupt(player, board, players);
//
//        //Attribute prüfen
//        assert player.isBankrupt() : "failed : Spieler nicht als Zuschauer gesetzt.";
//        assert station.getOwner() == null : "failed : Bahnhof nicht zur Bank zurückgesetzt.";
//        assert street1.getOwner() == null : "failed : Strasse nicht zur Bank zurückgesetzt.";
//        assert street2.getOwner() == null : "failed : Strasse nicht zur Bank zurückgesetzt.";
//        boolean isMortgageReset = !street2.isMortgageTaken();
//        assert isMortgageReset : "failed : Hypothek nicht zurückgesetzt.";
//        assert street1.getHouseCount() == 0 : "failed : Häuser nicht zurückgesetzt.";
//
//        System.out.println("passed : Spieler geht bankrupt");
//
//    }
//
//    /**
//     * @author Patrick Kalweit
//     * @param gc
//     * @param player
//     */
//    private static void testGoField() {
//        //Spieler bestimmen
//        Player player1 = players[0];
//        Player player2 = players[1];
//
//        // Spielern 1000 Geld geben:
//        PlayerService.giveMoney(player1, 1000 - player1.getMoney());
//        PlayerService.giveMoney(player2, 1000 - player2.getMoney());
//
//        //Spieler auf Start setzen
//        player1.setPosition(0);
//        player2.setPosition(0);
//
//        //Spieler bewegen (1 direkt auf Los, 2 ueber Los hinweg)
//        fm.movePlayer(player1, 40, ((GoField) board.getFields()[0]).getAmount());
//        fm.movePlayer(player2, 50, ((GoField) board.getFields()[0]).getAmount());
//
//        //Test auf korrekte Ueberweisung des Los - Geldes
//        assert player1.getMoney() == 1000 + ((GoField) board.getFields()[0]).getAmount() : "failed : Los Geld falsch oder garnicht ueberwiesen!";
//        assert player2.getMoney() == 1000 + ((GoField) board.getFields()[0]).getAmount() : "failed : Los Geld falsch oder garnicht ueberwiesen!";
//
//        System.out.println("passed : Spieler bekommt Geld durch Los Feld");
//
//    }
//
//    /**
//     * @author Patrick Kalweit
//     * @param gc
//     * @param players
//     */
//    private static void testDoubletToJail() {
//        //Spieler und Variablen initialisieren
//        Player prisoner = players[0];
//
//        // WurfPhase mit 3 Paeschen starten
//        gc.rollPhase(prisoner, 3);
//
//        assert prisoner.isInJail() == true : "failed : Spieler ist nicht im Gefaengnis!";
//        if (prisoner.isInJail() == true) {
//            System.out.println("passed : durch 3 Päsche ins Gefaengnis");
//        }
//
//        prisoner.setInJail(false);
//
//    }
//
//    /**
//     * @author Patrick Kalweit
//     * @param gc
//     * @param players
//     */
//    private static void testPayPrisonDeposit() {
//        //Spieler und Variablen initialisieren
//        Player prisoner1 = players[0];
////        Player prisoner2 = players[1];
//        prisoner1.setMoney(1500);
//        prisoner1.setInJail(true);
////        prisoner2.setMoney(49);
////        prisoner2.setInJail(true);
//
//        //Frei kaufen
//        System.out.println("Gebe eine 2 ein!"); //TODO Userinput automatisieren
//        gc.jailPhase(prisoner1);
//
//        //kommt mit 2 in endlos Schleife, also richtig!
////        System.out.println("Gebe eine 2 ein!"); //TODO Userinput automatisieren
////        gc.jailPhase(prisoner2);
//        assert prisoner1.isInJail() == false : "failed : Spieler ist noch im Gefaengnis!";
//        if (prisoner1.isInJail() == false) {
//            System.out.println("passed : freigekauft aus Gefaengnis");
//        }
//
//    }
//
//    /**
//     * @author Patrick Kalweit
//     * @param gc
//     * @param players
//     */
//    private static void testCornerFields() {
//        //Spieler und Variablen initialisieren
//        Player cornerPlayer = players[0];
//        cornerPlayer.setInJail(false);
//
//        //Gefaengnis Feld (zu Besuch)
//        cornerPlayer.setPosition(10);
//        assert cornerPlayer.isInJail() == false : "failed : Spieler ist im Gefaengnis!";
//        //Freiparken
//        cornerPlayer.setPosition(20);
//        assert cornerPlayer.getPosition() == 20 : "failed : Spieler ist nicht auf Frei Parken!";
//        //Los-Feld
//        cornerPlayer.setPosition(0);
//        assert cornerPlayer.getPosition() == 0 : "failed : Spieler ist nicht auf Los";
//        if (cornerPlayer.getPosition() == 0) {
//            System.out.println("passed : Eckfelder betreten");
//        }
//    }
//
//    /**
//     * @author Patrick Kalweit
//     * @param gc
//     * @param players
//     */
//    private static void testGoToJail() {
//        //Spieler und Variablen initialisieren
//        Player prePrisoner = players[0];
//        prePrisoner.setPosition(30);
//        prePrisoner.setInJail(false);
//        int[] rollResult = {14, 16};
//
//        gc.fieldPhase(prePrisoner, rollResult);
//
//        assert prePrisoner.getPosition() == 10 : "failed : Spieler nicht auf Gefaengnis Feld!";
//        assert prePrisoner.isInJail() == true : "failed : Spieler ist nicht IM Gefaengnis!";
//        assert prePrisoner.getDaysInJail() == 0 : "failed : daysInJail sind nicht 0!";
//        if (prePrisoner.getPosition() == 10 && prePrisoner.isInJail() == true && prePrisoner.getDaysInJail() == 0) {
//            System.out.println("passed : durch GoToJail ins Gefaengnis!");
//        }
//
//        prePrisoner.setInJail(false);
//
//    }
//
//    /**
//     * @author Christian Prinz
//     * @param gc
//     * @param player
//     */
//    private static void testBuyStreet() {
//        Player player = players[0];
//        Property street = (Property) gc.board.getFields()[1];
//        PlayerService.giveMoney(player, 1000 - player.getMoney());
//        fm.buyProperty(player, street, street.getPrice());
//
//        assert player.getMoney() == 1000 - street.getPrice() : "failed : Geld bei Straßenkauf nicht Abgebucht";
//        assert street.getOwner() == player : "failed : Straßenbesitz wurde nicht geaendert";
//
//        System.out.println("passed : Straße kaufen");
//    }
//
//    /**
//     * @author Christian Prinz
//     * @param gc
//     * @param players
//     */
//    private static void testPayRent() {
//        // Spieler bestimmen
//        Player player = players[0];
//        Player owner = players[1];
//
//        // Spielern Geld geben
//        owner.setMoney(1000);
//        player.setMoney(1000);
//
//        // Owner  alle Strassen geben (5, 6, 12)
//        for (Field field : gc.board.getFields()) {
//            if (field instanceof Property) {
//                Property actual = (Property) field;
//                actual.setOwner(owner);
//            }
//        }
//
//        // Wuerfelergebnis setzen (6 + 5 = 11)
//        int[] rollResult = {6, 5};
//
//        // Test fuer Miete zahlen auf Bahnhof (4fache Bahnhofsmiete abziehen)
//        player.setPosition(5);
//        gc.fieldPhase(player, rollResult);                          // -200 = 800
//        Property bahnhof2 = (Property) gc.board.getFields()[15];    // owner einen Bahnhof wegnehmen
//        bahnhof2.setOwner(null);                                    // jetzt mit 3facher Miete
//        gc.fieldPhase(player, rollResult);                          // -100 = 700
//        assert player.getMoney() == 700 : "failed : falsche Bahnhofsmiete abgezogen!";
//        assert owner.getMoney() == 1300 : "failed : falsche Bahnhofsmiete überwiesen!";
//
//        // Test fuer Miete zahlen auf Werk (10 x Augenzahl)
//        player.setPosition(12);
//        gc.fieldPhase(player, rollResult);                          // -10*11 = 590
//        Property werk2 = (Property) gc.board.getFields()[28];       // owner ein Werk wegnehmen
//        werk2.setOwner(null);                                       // jetzt mit 4*Augenzahl
//        gc.fieldPhase(player, rollResult);                          // -4*11 = 546
//        assert player.getMoney() == 546 : "failed : falsche Werksmiete abgezogen!";
//        assert owner.getMoney() == 1454 : "failed : falsche Werksmiete überwiesen!";
//
//        // Test fuer Miete zahlen auf Strasse (alle Strassen im Besitz)
//        player.setPosition(1);
//        gc.fieldPhase(player, rollResult);                          // -4 = 542
//
//        assert player.getMoney() == 542 : "failed : falsche Strassenmiete abgezogen!";
//        assert owner.getMoney() == 1458 : "failed : falsche Strassenmiete überwiesen!";
//
//        StreetField strasse1 = (StreetField) gc.board.getFields()[1];
//        strasse1.setHouseCount(5);
//        gc.fieldPhase(player, rollResult);                          // - 250 = 292
//
//        assert player.getMoney() == 292 : "failed : falsche Strassenmiete abgezogen!";
//        assert owner.getMoney() == 1708 : "failed : falsche Strassenmiete überwiesen!";
//
//        // Owner  alle Strassen wegnehmen (5, 6, 8, 9, 12)
//        for (Field field : gc.board.getFields()) {
//            if (field instanceof Property) {
//                Property actual = (Property) field;
//                actual.setOwner(null);
//            }
//        }
//
//        // Spielergeld zurücksetzen
//        owner.setMoney(1500);
//        player.setMoney(1500);
//
//        System.out.println("passed : Miete zahlen auf allen Grundstuecken!");
//    }
//
//    /**
//     * @author Christian Prinz
//     * @param gc
//     * @param arg
//     */
//    private static void testRollMethod() {
//        Player arg = players[0];
//        for (int j = 0; j < 1000; j++) {
//            int[] result = PlayerService.roll(arg);
//            for (int i : result) {
//                assert i > 0 && i < 7 : "failed : Wuerfelergebnis ungueltig!";
//            }
//        }
//        System.out.println("passed : Wuerfeln");
//    }
//
//    /**
//     * @author Christian Prinz
//     * @param board
//     * @param players
//     */
//    private static void testGameBoard() {
//        //existiert das Board
//        assert board != null : "failed : Gamboard nicht initialisiert!";
//
//        //existieren die Felder
//        for (Field field : board.getFields()) {
//            assert field != null : "failed : Felder nicht initialisiert!";
//        }
//
//        //existieren die Spieler
//        for (Player player : players) {
//            assert player != null : "failed : Spieler nicht initialisiert!";
//        }
//        System.out.println("passed : Initialisierung");
//    }
//
//    /**
//     * @author Eleonora Kostova
//     * @param gc
//     * @param players
//     */
//    private static void testBuyBuilding() {
//        //Variablen initialisieren
//        StreetField seeStrasse = (StreetField) gc.board.getFields()[11];
//        StreetField hafenStrasse = (StreetField) gc.board.getFields()[13];
//        StreetField neueStrasse = (StreetField) gc.board.getFields()[14];
//        Player player = players[0];
//
//        seeStrasse.setOwner(player);
//        hafenStrasse.setOwner(player);
//        neueStrasse.setOwner(player);
//        player.setMoney(800);
//        hafenStrasse.setHouseCount(1);
//        neueStrasse.setHouseCount(0);
//        seeStrasse.setHouseCount(0);
//
//        gc.buyBuilding(player, seeStrasse);
//
//        //checkLiquidity()
//        assert gc.checkLiquidity(player, seeStrasse.getHousePrice()) == true : "failed: Spieler hat nicht genug Geld";
//
//        //complete()
//        assert seeStrasse.complete() == true : "failed: der Strassenzug ist nicht komplett";
//
//        //checkBalance()
//        assert gc.checkBalance(seeStrasse, false) == true : "failed: das Gewicht der Haeuser ist geglichen";
//
//        //getHousePrice()
//        assert seeStrasse.getHousePrice() == 100 : "failed: ";
//
//        assert player.getMoney() == 700 : "failed: Geld nicht abgebucht";
//
//        //getRent()
//        assert seeStrasse.getRent() == 50 : "failed: Haus wurde trotzdem gebaut";
//
//        System.out.println("passed : Haus wurde gebaut");
//    }
//
//    /**
//     * @author Eleonora Kostova
//     * @param gc
//     * @param players
//     */
//    private static void testSellBuilding() {
//        //Variablen initialisieren
//        StreetField parkStrasse = (StreetField) gc.board.getFields()[37];
//        StreetField schlossAllee = (StreetField) gc.board.getFields()[39];
//        Player player = players[0];
//
//        player.setMoney(300);
//        parkStrasse.setOwner(player);
//        schlossAllee.setOwner(player);
//        parkStrasse.setHouseCount(3);
//        schlossAllee.setHouseCount(4);
//
//        gc.sellBuilding(player, schlossAllee);
//
//        //checkBalance()
//        assert gc.checkBalance(schlossAllee, true) == true : "failed: Hause konnte nicht verkauft werden";
//
//        //getHousePrice()
//        assert schlossAllee.getHousePrice() == 200 : "failed: Haus kostet nicht 200";
//
//        //giveMoney() + getMoney()
//        assert player.getMoney() == 500 : "failed:  Geld nicht ueberwiesen";
//
//        //getRent()
//        assert schlossAllee.getRent() == 1400 : "failed: Rent bei 3 Haeuser nicht richtig gerechnen";
//
//        System.out.println("passed : Haus verkauft");
//    }
//
//    /**
//     * @author Eleonora Kostova
//     * @param gc
//     * @param players
//     */
//    private static void testTakeMortgage() {
//        //Variablen initialisieren
//        Player player = players[0];
//        StationField suedbahnhof = (StationField) gc.board.getFields()[5];
//        StationField nordbahnhof = (StationField) gc.board.getFields()[25];
//
//        StreetField theaterStrasse = (StreetField) gc.board.getFields()[21];
//        StreetField badStrasse = (StreetField) gc.board.getFields()[1];
//        StreetField turmStrasse = (StreetField) gc.board.getFields()[3];
//
//        SupplyField elWerk = (SupplyField) gc.board.getFields()[12];
//        SupplyField wasserWerk = (SupplyField) gc.board.getFields()[28];
//
//        player.setMoney(600);
//        suedbahnhof.setOwner(player);
//        nordbahnhof.setOwner(player);
//        theaterStrasse.setOwner(player);
//        badStrasse.setOwner(player);
//        turmStrasse.setOwner(player);
//        elWerk.setOwner(player);
//        wasserWerk.setOwner(player);
//        badStrasse.setHouseCount(1);
//        turmStrasse.setHouseCount(1);
//
//        //BAHNHOF UND WERKE======================================================
//        gc.takeMortgage(player, suedbahnhof);
//        gc.takeMortgage(player, elWerk);
//
//        //getMortgageValue + giveMoney() + getMoney()
//        assert player.getMoney() == 775 : "failed: Hypothek nicht aufgenommen"; // 600 + 100 +75
//
//        //setMortgageTaken() + getRent()
//        assert suedbahnhof.getRent() == 0 : "failed: Grundstueck hat noch Rent";
//        assert nordbahnhof.getRent() == 25 : "failed: Rent ist nicht doppelt so wenig";
//        assert elWerk.getRent() == 0 : "failed: Werk hat noch Rent";
//
//        //NICHT KOMPLETTE STRASSE=======================================
//        gc.takeMortgage(player, theaterStrasse);
//
//        //getMortgageValue() + getMoney()+giveMoney()
//        assert player.getMoney() == 885 : "failed: Hypothek nicht aufgenommen"; // 775 + 110
//
//        //getRent()+setMortgagaeTaken()
//        assert theaterStrasse.getRent() == 0 : "failed: Grundstueck hat noch Rent";
//
//        //KOMPLETTE STRASSE=============================================
//        gc.takeMortgage(player, turmStrasse);
//
//        //getHousCount()
//        assert badStrasse.getHouseCount() == 0 : "failed: Haus nicht verkauft";
//        assert turmStrasse.getHouseCount() == 0 : "failed: Haus nicht verkauft";
//
//        //getMortgageValue()
//        assert turmStrasse.getMortgageValue() == 30 : "failed: HypothekValue falsch";
//
//        assert turmStrasse.getHousePrice() == 50 : "failed: HausPrice falsch";
//
//        //getMoney() +giveMoney()
//        assert player.getMoney() == 1015 : "failed: Problem bei Verkauf von Haeuser"; //885 + 2x50(Haus)+30(Hypothek)
//        assert turmStrasse.getRent() == 0 : "failed: Grundstueck hat kein Rent";
//        //setMortgageTaken()
//        turmStrasse.setMortgageTaken(true);
//
//        //getRent()
//        assert turmStrasse.getRent() == 0 : "failed: Grundstueck hat nocht Rent";
//
//        System.out.println("passed : Hypothek aufgenommen");
//    }
//
//    /**
//     * @author Eleonora Kostova
//     * @param gc
//     * @param players
//     */
//    private static void testPayMortgage() {
//        //Variablen initialisieren
//        Property suedbahnhof = (Property) gc.board.getFields()[5];
//        Property nordbahnhof = (Property) gc.board.getFields()[25];
//        Player player = players[0];
//
//        player.setMoney(800);
//        suedbahnhof.setOwner(player);
//        nordbahnhof.setOwner(player);
//
//        //setMortgageTaken()
//        suedbahnhof.setMortgageTaken(true);
//
//        //getRent()
//        assert nordbahnhof.getRent() == 25 : "failed: Rent ist falsch";
//        gc.payMortgage(player, suedbahnhof);
//
//        //checkLiquidity()
//        assert gc.checkLiquidity(player, suedbahnhof.getMortgageValue()) == true : "faild: Spieler hat kein Geld";
//
//        //getMortgageValue()
//        assert suedbahnhof.getMortgageBack() == 110 : "failed: HypothekValueBack falsch";
//
//        //takeMoneyUnchecked() + getMoney()
//        assert player.getMoney() == 690 : "failed: Hypothek nicht zurueck gezahlt"; //800-110
//
//        //setMortgagetaken()
//        suedbahnhof.setMortgageTaken(false);
//
//        //getRent()
//        assert suedbahnhof.getRent() == 50 : "failed: grundstueck hat kein Rent";
//
//        System.out.println("passed : Hypothek zurueck gezahlt");
//
//    }
//}
