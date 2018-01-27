package de.btu.monopoly.core;

import de.btu.monopoly.GlobalSettings;
import de.btu.monopoly.core.service.*;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ki.HardKi;
import de.btu.monopoly.net.client.GameClient;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Prinz
 */
public class Game {

    /**
     * Zentraler Logger der Spiellogik
     */
    private static final Logger LOGGER = Logger.getLogger(Game.class.getCanonicalName());

    /**
     * Spiel-Client
     */
    private final GameClient client;

    /**
     * Spielbrett
     */
    private GameBoard board;

    /**
     * Spieler (Zuschauer und aktive Spieler)
     */
    private final Player[] players;

    /**
     * Die Zufallsinstanz für sämtliche zufällige Spielereignisse
     */
    private final Random random;
    
    /**
     * momentaner Spieler
     */
    private Player currPlayer;

    /**
     * Die fachliche Komponente des Spiels als Einheit, bestehend aus einem
     * Spielbrett, den Spielern sowie Zuschauern.
     *
     * @param client GameClient
     * @param players Spieler
     * @param seed RandomSeed
     */
    public Game(GameClient client, Player[] players, long seed) {
        
        this.client = client;
        this.players = players;
        random = new Random(seed);
        
        IOService.setClient(client);
        
        init();
    }

    private void init() {

        LOGGER.info("Spiel wird initialisiert.");

        this.board = new GameBoard();
        AuctionService.initAuction(players, client);

        for (Player player : players) {
            board.addPlayer(player);
        }

        System.err.println("-------------------------");
    }

    public void start() throws InterruptedException {
        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Spiel beginnt.");

        while (board.updateActivePlayers().getActivePlayers().size() > 1) {
            for (Player activePlayer : board.getActivePlayers()) {
                currPlayer = activePlayer;
                turn(activePlayer);
                if (!activePlayer.getBank().isLiquid()) {
                    PlayerService.bankrupt(activePlayer, board);
                }
            }
        }

        LOGGER.info(String.format("%s hat das Spiel gewonnen!", board.getActivePlayers().get(0).getName()));
    }

    private void turn(Player player) {
        int[] rollResult;
        int doubletCounter = 0;

        LOGGER.info(String.format("%s ist an der Reihe.", player.getName()));
        if (player.isInJail()) {
            jailPhase(player);
        }

        if (!player.isInJail()) {
            do {
                rollResult = rollPhase(player, doubletCounter);
                doubletCounter += (rollResult[0] == rollResult[1]) ? 1 : 0;

                if (doubletCounter < 3) {
                    fieldPhase(player, rollResult);
                    if (player.isInJail()) {
                        // Spieler gerät durch die fieldPhase, also durch eine Karte oder durch das Feld ins Gefängnis
                        break; // keine Aktionsphase unter dieser Voraussetzung
                    }
                }
                actionPhase(player);
            } while (rollResult[0] == rollResult[1]);
        }
    }

    public void jailPhase(Player player) {
        int choice;
        do {
            if (GlobalSettings.RUN_IN_CONSOLE) {
                LOGGER.info(String.format(" %s ist im Gefängnis und kann: %n[1] - 3-mal Würfeln, um mit einem Pasch freizukommen "
                        + "%n[2] - Bezahlen (50€) %n[3] - Gefängnis-Frei-Karte benutzen", player.getName()));
            }
            choice = IOService.jailChoice(player);
            switch (choice) {
                case 1:
                    processJailRollOption(player);
                    break;

                case 2: // Freikaufen
                    processJailPayOption(player);
                    break;

                case 3: // Freikarte ausspielen
                    processJailCardOption(player);
                    break;

                default:
                    LOGGER.log(Level.WARNING, "Fehler: Die Wahl des Spielers ist außerhalb des erlaubten Bereichs!");
                    break;
            }
        } while (player.isInJail() && choice != 1);
    }

    public void processJailRollOption(Player player) {
        int[] result = PlayerService.roll(getRandom());
        if (result[0] == result[1]) {
            PlayerService.freeFromJail(player);
        }
        else {
            player.addDayInJail();
            if (player.getDaysInJail() >= 3) {
                LOGGER.info("Drei Runden ohne Pasch, Spieler muss zahlen.");
                PlayerService.takeMoneyUnchecked(player, 50);
                PlayerService.freeFromJail(player);
            }
        }
    }

    public void processJailPayOption(Player player) {
        if (PlayerService.takeMoney(player, 50)) {
            LOGGER.info(String.format("%s hat 50 gezahlt und ist frei!", player.getName()));
            PlayerService.freeFromJail(player);
        }
        else {
            LOGGER.info(String.format("%s hat kein Geld um sich freizukaufen.", player.getName()));
        }
    }

    public void processJailCardOption(Player player) {
        if (board.getCardManager().hasJailCards(player)) {
            
            board.getCardManager().applyCardAction(Card.Action.JAIL, player);
            LOGGER.info(String.format("%s hat eine Gefängnis-Frei-Karte benutzt.", player.getName()));
        }
        else LOGGER.info(String.format("%s hat keine Gefängnis-Frei-Karten mehr.", player.getName()));
    }

    private int[] rollPhase(Player player, int doubletCounter) {
        int[] rollResult;
        int doubletCount = doubletCounter;

        LOGGER.info(String.format("%s ist dran mit würfeln.", player.getName()));
        IOService.sleep(2000);
        rollResult = PlayerService.roll(getRandom());
        doubletCount += (rollResult[0] == rollResult[1]) ? 1 : 0;

        if (doubletCount >= 3) {
            LOGGER.info(String.format("%s hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!", player.getName()));
            FieldService.toJail(player);
        }
        else {
            board.getFieldManager().movePlayer(player, rollResult[0] + rollResult[1]);
        }
        return rollResult;
    }

    public void fieldPhase(Player player, int[] rollResult) {

        boolean repeatPhase;
        do {
            repeatPhase = false;
            GameBoard.FieldType type = GameBoard.FIELD_STRUCTURE[player.getPosition()];
            LOGGER.fine(String.format("Feldphase begonnen: Spieler %s Feld: %s", player.getName(), type));
            
            switch (type) {
                
                case TAX: // Steuerfeld
                    TaxField taxField = (TaxField) board.getFields()[player.getPosition()];
                    
                    FieldService.payTax(player, taxField);
                    break;

                case CARD: // Kartenfeld
                    CardField cardField = (CardField) board.getFields()[player.getPosition()];
                    
                    LOGGER.fine(String.format("%s steht auf einem Kartenfeld (%s).", player.getName(), cardField.getName()));
                    board.getCardManager().pullAndProcess(cardField.getStackType(), player);
                    break;

                case GO_JAIL: // "Gehen Sie Ins Gefaengnis"-Feld
                    LOGGER.info(String.format("%s muss ins Gefaengnis!", player.getName()));
                    FieldService.toJail(player);
                    break;

                case CORNER: // Eckfeld
                    LOGGER.fine(String.format("%s steht auf einem Eckfeld.", player.getName()));
                    break;

                case GO: // "LOS"-Feld
                    LOGGER.fine(String.format("%s steht auf LOS.", player.getName()));
                    break;

                default:
                    PropertyField prop = (PropertyField) board.getFields()[player.getPosition()];
                    processPlayerOnPropertyField(player, prop, rollResult);
                    break;
            }
        }
        while (repeatPhase);
    }

    private void processPlayerOnPropertyField(Player player, PropertyField prop, int[] rollResult) {
        Player other = prop.getOwner();
        if (other == null) { // Feld frei
            if (GlobalSettings.RUN_IN_CONSOLE) {
                LOGGER.info(String.format("%s steht auf %s. Wähle eine Aktion!%n[1] Kaufen %n[2] Nicht kaufen",
                        player.getName(), prop.getName()));
            }
            processBuyPropertyFieldOption(player, prop);
        }
        else if (other == player) { // PropertyField im eigenen Besitz
            LOGGER.fine(String.format("%s steht auf seinem eigenen Grundstück.", player.getName()));
        }
        else { // PropertyField nicht in eigenem Besitz
            LOGGER.info(String.format("%s steht auf %s. Dieses Grundstück gehört von %s.",
                    player.getName(), prop.getName(), other.getName()));
            PlayerService.takeAndGiveMoneyUnchecked(player, other, FieldService.getRent(prop, rollResult));
        }
    }

    private void processBuyPropertyFieldOption(Player player, PropertyField prop) {
        int choice = IOService.buyPropertyChoice(player, prop);

        switch (choice) {
            case 1: // Kaufen
                LOGGER.info(String.format("%s >> %s", player.getName(), prop.getName()));
                if (!FieldService.buyPropertyField(player, prop)) {
                    LOGGER.warning(String.format("%s hat nicht genug Geld! %s wird zwangsversteigert.",
                            player.getName(), prop.getName()));
                    processAuction(prop);
                }
                break;

            case 2: // Auktion
                LOGGER.log(Level.INFO, "{0} hat sich gegen den Kauf entschieden, die Stra\u00dfe wird nun versteigert.", player.getName());
                processAuction(prop);
                break;

            default:
                LOGGER.warning("Fehler: Wahl außerhalb des gültigen Bereichs!");
                break;
        }
    }

    private void actionPhase(Player player) {

        int choice;
        do {
            if (GlobalSettings.RUN_IN_CONSOLE) {
                LOGGER.info(String.format("%s ist an der Reihe! Waehle eine Aktion:%n[1] - Nichts%n[2] - Haus kaufen%n[3] - Haus verkaufen%n[4] - "
                        + "Hypothek aufnehmen%n[5] - Hypothek abbezahlen%n[6] - Handeln", player.getName()));
            }
            choice = IOService.actionSequence(player, board);
            if (choice == 6) {
                processPlayerTradeOption(player);
            }
            else if (choice > 1 && choice < 6) {

                int[] ownedFieldIds = board.getFieldManager().getOwnedPropertyFieldIds(player);
                String[] fieldNames = Arrays.stream(ownedFieldIds)
                        .mapToObj(id -> board.getFieldManager().getField(id).getName())
                        .toArray(String[]::new);
                int chosenFieldId;
                if (player.getAiLevel() < 2) {
                    int chosenFieldChoice = IOService.askForField(player, fieldNames) - 1;
                    if (chosenFieldChoice < 0) {
                        LOGGER.info("Straßenauswahl wurde abgebrochen!");
                        continue;
                    }
                    chosenFieldId = ownedFieldIds[chosenFieldChoice];
                }
                else {
                    // Das hier verwendete Feld wurde vorher in HardKi.processActionSequence() festgelegt.
                    chosenFieldId = HardKi.getChosenFieldId();
                }
                Field currField = board.getFieldManager().getField(chosenFieldId); // Wahl der Strasse

                PropertyField property = (PropertyField) currField;
                switch (choice) {
                    case 2: // Haus kaufen
                        if (!(currField instanceof StreetField)) {
                            LOGGER.info("Gewähltes Feld ist keine Straße!");
                            break;
                        }
                        StreetField streetField = (StreetField) property;
                        board.getFieldManager().buyHouse(streetField);
                        break;

                    case 3: //Haus verkaufen
                        if (!(currField instanceof StreetField)) {
                            LOGGER.info("Gewähltes Feld ist keine Straße!");
                            break;
                        }
                        streetField = (StreetField) property;
                        board.getFieldManager().sellHouse(streetField);
                        break;

                    case 4: // Hypothek aufnehmen
                        board.getFieldManager().takeMortgage(property);
                        break;

                    case 5: // Hypothek zurückzahlen
                        board.getFieldManager().payMortgage(property);
                        break;
                }
            }
        } while (choice != 1);
    }

    /**
     * Initiiert einen Tausch/Handel ausgehend von einem speziellen Spieler.
     *
     * @param player Spieler
     */
    private void processPlayerTradeOption(Player player) {
        TradeService.processPlayerTradeOption(player, client, board);
    }

    /**
     * Initiiert die Auktion eines angegebenen Grundstuecks.
     *
     * @param property Grundstueck
     */
    public void processAuction(PropertyField property) {
        AuctionService.startAuction(property);
    }

    public GameBoard getBoard() {
        return board;
    }

    public Player[] getPlayers() {
        return players;
    }

    /**
     * Die Zufallsinstanz für sämtliche zufällige Spielereignisse
     *
     * @return the random
     */
    public Random getRandom() {
        return random;
    }
    
    /**
     * @return momentaner Spieler
     */
    public Player getCurrentPlayer() {
        return currPlayer;
    }
}
