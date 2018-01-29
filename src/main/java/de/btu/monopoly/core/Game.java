package de.btu.monopoly.core;

import de.btu.monopoly.Global;
import de.btu.monopoly.core.service.*;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ki.HardKi;
import de.btu.monopoly.net.client.GameClient;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Prinz
 */
public class Game {

    public static final int JAIL_ROLL_OPTION = 1;
    public static final int JAIL_PAY_OPTION = 2;
    public static final int JAIL_CARD_OPTION = 3;

    public static final int ACTION_NOTHING = 1;
    public static final int ACTION_BUY_HOUSE = 2;
    public static final int ACTION_SELL_HOUSE = 3;
    public static final int ACTION_TAKE_MORTGAGE = 4;
    public static final int ACTION_PAY_MORTGAGE = 5;
    public static final int ACTION_TRADE = 6;

    /**
     * Zentraler Logger der Spiellogik
     */
    private static final Logger LOGGER = Logger.getLogger(Game.class.getCanonicalName());

    /**
     * Spiel-Client
     */
    protected final GameClient client;

    /**
     * Spielbrett
     */
    protected GameBoard board;

    /**
     * Spieler (Zuschauer und aktive Spieler)
     */
    private final Player[] players;

    /**
     * Die Zufallsinstanz für sämtliche zufällige Spielereignisse
     */
    private final Random random;

    /**
     * Alle registrierten Listener die auf das Ende einer Runde warten.
     */
    private List<GameStateListener> stateListeners;

    protected Player player;

    protected int rollResult[];
    protected int doubletCount;

    /**
     * Die fachliche Komponente des Spiels als Einheit, bestehend aus einem Spielbrett, den Spielern sowie Zuschauern.
     *
     * @param client GameClient
     * @param players Spieler
     * @param seed Seed
     */
    public Game(GameClient client, Player[] players, long seed) {

        this.client = client;
        this.players = players;
        random = new Random(seed);

        stateListeners = new LinkedList<>();

        IOService.setClient(client);

        init();
    }

    public void addGameStateListener(GameStateListener listener) {
        stateListeners.add(listener);
    }

    public void removeGameStateListener(GameStateListener listener) {
        stateListeners.remove(listener);
    }

    protected void init() {

        LOGGER.info("Spiel wird initialisiert.");
        stateListeners.forEach(GameStateListener::onGameInit);

        this.board = new GameBoard();
        AuctionService.initAuction(players, client);

        for (Player player : players) {
            board.addPlayer(player);
        }

        System.err.println("-------------------------");
    }

    public void start() {

        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Spiel beginnt.");
        stateListeners.forEach(l -> l.onGameStart(players));

        List<Player> activePlayers = board.getActivePlayers();

        while (activePlayers.size() > 1) {

            for (int id = 0; id < activePlayers.size(); id++) {

                player = activePlayers.get(id);
                LOGGER.info(String.format("%s ist an der Reihe.", player.getName()));
                stateListeners.forEach(l -> l.onTurnStart(player));

                turn();

                if (!player.getBank().isLiquid()) {
                    PlayerService.bankrupt(player, board);
                }
                board.updateActivePlayers();

                Player nextPlayer = activePlayers.get((id + 1) % activePlayers.size());
                stateListeners.forEach(l -> l.onTurnEnd(player, nextPlayer));
            }
        }

        Player winner = board.getActivePlayers().get(0);
        LOGGER.info(String.format("%s hat das Spiel gewonnen!", winner.getName()));
        stateListeners.forEach(l -> l.onGameEnd(winner));
    }

    protected void turn() {

        if (player.isInJail()) {
            stateListeners.forEach(l -> l.onPlayerStartsTurnInJail(player));
            jailPhase();
        }

        if (!player.isInJail()) {
            do {
                rollPhase();
                if (doubletCount < 3) {
                    fieldPhase();
                    if (player.isInJail()) {
                        break;
                    }
                }
                actionPhase();
            } while (rollResult[0] == rollResult[1]);
        }
    }

    protected void rollPhase() {

        LOGGER.info(String.format("%s ist dran mit würfeln.", player.getName()));
        stateListeners.forEach(l -> l.onRollPhaseStart(player));
        IOService.sleep(2000);

        rollResult = PlayerService.roll(random);
        doubletCount += (rollResult[0] == rollResult[1]) ? 1 : 0;

        stateListeners.forEach(l -> l.onDiceThrow(rollResult, doubletCount));

        if (doubletCount >= 3) {
            LOGGER.info(String.format("%s hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!", player.getName()));
            FieldService.toJail(player);
        }
        else {
            int oldPos = player.getPosition();
            int moveAmount = rollResult[0] + rollResult[1];
            int newPos = (oldPos + moveAmount) % FieldTypes.GAMEBOARD_FIELD_STRUCT.length;

            board.getFieldManager().movePlayer(player, moveAmount);
            boolean passedGo = (oldPos >= newPos);

            for (GameStateListener l : stateListeners) {
                l.onPlayerMove(player, oldPos, newPos, passedGo);
            }

        }
    }

    protected void fieldPhase() {
        stateListeners.forEach(l -> l.onFieldPhaseStart(player));

        boolean repeatPhase;
        FieldTypes type;
        Field field;

        do {
            repeatPhase = false;
            type = FieldTypes.GAMEBOARD_FIELD_STRUCT[player.getPosition()];
            field = board.getFieldManager().getField(player.getPosition());

            LOGGER.fine(String.format("Feldphase begonnen: Spieler %s Feld: %s", player.getName(), type));
            for (GameStateListener l : stateListeners) {
                l.onPlayerOnNewField(player, type);
            }

            if (type.isStation()) {
                PropertyField prop = (PropertyField) board.getFields()[player.getPosition()];
                onPlayerOnProperty(prop);
            }
            else if (type.isTax()) {
                TaxField taxField = (TaxField) field;
                FieldService.payTax(player, taxField);
            }
            else if (type.isCard()) {
                CardField cardField = (CardField) field;
                board.getCardManager().pullAndProcess(cardField.getStackType(), player);
            }
            else if (type == FieldTypes.CORNER_3) /*
             * "Gehen Sie ins Gefängnis"
             */ {
                FieldService.toJail(player);
            }
        } while (repeatPhase);
    }

    protected void onPlayerOnProperty(PropertyField prop) {

        Player other = prop.getOwner();
        if (other == null) { // Feld frei
            if (Global.RUN_IN_CONSOLE) {
                LOGGER.info(String.format("%s steht auf %s. Wähle eine Aktion!%n[1] Kaufen %n[2] Nicht kaufen",
                        player.getName(), prop.getName()));
            }
            onBuyPropertyOption(prop);
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

    protected void onBuyPropertyOption(PropertyField prop) {

        int choice = IOService.buyPropertyChoice(player, prop);
        switch (choice) {
            case 1: // Kaufen
                LOGGER.info(String.format("%s >> %s", player.getName(), prop.getName()));
                if (!FieldService.buyPropertyField(player, prop)) {
                    LOGGER.warning(String.format("%s hat nicht genug Geld! %s wird zwangsversteigert.",
                            player.getName(), prop.getName()));
                    onAuction(prop);
                }
                break;

            case 2: // Auktion
                LOGGER.log(Level.INFO, "{0} hat sich gegen den Kauf entschieden, die Stra\u00dfe wird nun versteigert.", player.getName());
                onAuction(prop);
                break;

            default:
                LOGGER.warning("Fehler: Wahl außerhalb des gültigen Bereichs!");
                break;
        }
    }

    protected void actionPhase() {
        stateListeners.forEach(l -> l.onActionPhaseStart(player));

        int actionChoice;
        do {
            if (Global.RUN_IN_CONSOLE) {
                LOGGER.info(String.format("%s ist an der Reihe! Waehle eine Aktion:%n[1] - Nichts%n[2] - Haus kaufen%n[3] - Haus verkaufen%n[4] - "
                        + "Hypothek aufnehmen%n[5] - Hypothek abbezahlen%n[6] - Handeln", player.getName()));
            }

            actionChoice = IOService.actionSequence(player, board);
            for (GameStateListener l : stateListeners) {
                l.onPlayerActionOption(player, actionChoice);
            }

            if (actionChoice > ACTION_NOTHING && actionChoice < ACTION_TRADE) {

                FieldManager fima = board.getFieldManager();
                if (player.getAiLevel() < 2) {
                    int fieldChoice = getFieldChoice();
                }
                else {
                    int fieldChoice = HardKi.getChosenFieldId();
                }
                if (fieldChoice == -1) {
                    continue;
                }

                PropertyField prop = (PropertyField) board.getFieldManager().getField(fieldChoice);
                switch (actionChoice) {

                    case ACTION_BUY_HOUSE:
                        if (prop instanceof StreetField) {
                            fima.buyHouse((StreetField) prop);
                        }
                        else {
                            LOGGER.info("Gewähltes Feld ist keine Straße!");
                        }
                        break;

                    case ACTION_SELL_HOUSE:
                        if (prop instanceof StreetField) {
                            fima.sellHouse((StreetField) prop);
                        }
                        else {
                            LOGGER.info("Gewähltes Feld ist keine Straße!");
                        }
                        break;

                    case ACTION_TAKE_MORTGAGE:
                        fima.takeMortgage(prop);
                        break;

                    case ACTION_PAY_MORTGAGE:
                        fima.payMortgage(prop);
                        break;
                }
            }
            else if (actionChoice == ACTION_TRADE) {
                onPlayerTradeOption();
            }
        } while (actionChoice != 1);
    }

    private int getFieldChoice() {

        int[] ownedFieldIds = board.getFieldManager().getOwnedPropertyFieldIds(player);
        String[] fieldNames = Arrays.stream(ownedFieldIds)
                .mapToObj(id -> board.getFieldManager().getField(id).getName())
                .toArray(String[]::new);

        if (player.getAiLevel() < 2) {
            int chosenFieldChoice = IOService.askForField(player, fieldNames) - 1;
            if (chosenFieldChoice < 0) {
                LOGGER.info("Straßenauswahl wurde abgebrochen!");
                return -1;
            }
            return ownedFieldIds[chosenFieldChoice];
        }
        else {
            return HardKi.getChosenFieldId();
        }
        // Das hier verwendete Feld wurde vorher in HardKi.processActionSequence() festgelegt.
    }

    protected void jailPhase() {
        stateListeners.forEach(l -> l.onJailPhaseStart(player));

        int choice;
        do {
            if (Global.RUN_IN_CONSOLE) {
                LOGGER.info(String.format(" %s ist im Gefängnis und kann: %n[1] - 3-mal Würfeln, um mit einem Pasch freizukommen "
                        + "%n[2] - Bezahlen (50€) %n[3] - Gefängnis-Frei-Karte benutzen", player.getName()));
            }

            choice = IOService.jailChoice(player);
            for (GameStateListener l : stateListeners) {
                l.onPlayerJailOption(player, choice);
            }

            switch (choice) {

                case JAIL_ROLL_OPTION:
                    onJailRollOption();
                    break;
                case JAIL_PAY_OPTION:
                    onJailPayOption();
                    break;
                case JAIL_CARD_OPTION:
                    onJailCardOption();
                    break;
                default:
                    throw new RuntimeException("Undefined player jail choice!");
            }
        } while (player.isInJail() && choice != JAIL_ROLL_OPTION);
    }

    protected void onJailRollOption() {
        int[] rollResult = PlayerService.roll(random);

        if (rollResult[0] == rollResult[1]) {
            onJailRollSuccess();
        }
        else {
            onJailRollFailure();
        }
    }

    protected void onJailRollSuccess() {
        stateListeners.forEach(l -> l.onJailRollSuccess(player));
        PlayerService.freeFromJail(player);
    }

    protected void onJailRollFailure() {
        stateListeners.forEach(l -> l.onJailRollFailure(player));

        player.addDayInJail();
        if (player.getDaysInJail() >= 3) {
            onForceJailPayOption();
        }
    }

    protected void onForceJailPayOption() {
        stateListeners.forEach(l -> l.onForceJailPayOption(player));

        LOGGER.info("Drei Runden ohne Pasch, Spieler muss zahlen.");
        PlayerService.takeMoneyUnchecked(player, 50);
        PlayerService.freeFromJail(player);
    }

    protected void onJailPayOption() {
        if (PlayerService.takeMoney(player, 50)) {
            onJailPayFailure();
        }
        else {
            onJailPaySuccess();
        }
    }

    protected void onJailPayFailure() {
        stateListeners.forEach(l -> l.onJailPayFailure(player));
        LOGGER.info(String.format("%s hat kein Geld um sich freizukaufen.", player.getName()));
    }

    protected void onJailPaySuccess() {
        stateListeners.forEach(l -> l.onJailPaySuccess(player));
        LOGGER.info(String.format("%s hat 50 gezahlt und ist frei!", player.getName()));
        PlayerService.freeFromJail(player);
    }

    protected void onJailCardOption() {
        if (board.getCardManager().hasJailCards(player)) {
            onJailCardSuccess();
        }
        else {
            onJailCardFailure();
        }
    }

    protected void onJailCardSuccess() {
        stateListeners.forEach(l -> l.onJailCardSuccess(player));
        LOGGER.info(String.format("%s hat eine Gefängnis-Frei-Karte benutzt.", player.getName()));
        board.getCardManager().applyCardAction(Card.Action.JAIL, player);
    }

    protected void onJailCardFailure() {
        stateListeners.forEach(l -> l.onJailCardFailure(player));
        LOGGER.info(String.format("%s hat keine Gefängnis-Frei-Karten mehr.", player.getName()));
    }

    /**
     * Initiiert einen Tausch/Handel ausgehend von einem speziellen Spieler.
     */
    protected void onPlayerTradeOption() {
        stateListeners.forEach(l -> l.onTradeStart(player));
        TradeService.processPlayerTradeOption(player, client, board);
        stateListeners.forEach(l -> l.onTradeEnd(player));
    }

    /**
     * Initiiert die Auktion eines angegebenen Grundstuecks.
     *
     * @param prop Grundstueck
     */
    protected void onAuction(PropertyField prop) {
        stateListeners.forEach(l -> l.onAuctionStart(prop));
        AuctionService.startAuction(prop);
        stateListeners.forEach(l -> l.onAuctionEnd(prop.getOwner(), prop));
    }

    /**
     * @return Die Spielbrett-Instanz
     */
    public GameBoard getBoard() {
        return board;
    }

    /**
     * @return Die Liste aller Spieler
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * @return Zufallsinstanz für sämtliche zufällige Spielereignisse
     */
    public Random getRandom() {
        return random;
    }
}
