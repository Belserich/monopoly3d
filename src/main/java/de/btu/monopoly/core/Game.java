package de.btu.monopoly.core;

import de.btu.monopoly.Global;
import de.btu.monopoly.core.service.*;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ki.HardKi;
import de.btu.monopoly.net.chat.GUIChat;
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

    protected Player currPlayer;

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
        if (!Global.RUN_AS_TEST) {
            GUIChat.getInstance().event("Das Spiel beginnt!");
        }
        stateListeners.forEach(l -> l.onGameStart(players));

        List<Player> activePlayers = board.getActivePlayers();

        while (activePlayers.size() > 1) {

            for (int id = 0; id < activePlayers.size(); id++) {

                currPlayer = activePlayers.get(id);
                LOGGER.info(String.format("%s ist an der Reihe.", currPlayer.getName()));
                stateListeners.forEach(l -> l.onTurnStart(currPlayer));

                turn();

                if (!currPlayer.getBank().isLiquid()) {
                    PlayerService.bankrupt(currPlayer, board);
                }
                board.updateActivePlayers();

                Player nextPlayer = activePlayers.get((id + 1) % activePlayers.size());
                stateListeners.forEach(l -> l.onTurnEnd(currPlayer, nextPlayer));
            }
        }

        Player winner = board.getActivePlayers().get(0);
        if (!Global.RUN_AS_TEST) {
            GUIChat.getInstance().event(String.format("%s hat das Spiel gewonnen!", winner.getName()));
        }
        stateListeners.forEach(l -> l.onGameEnd(winner));
    }

    protected void turn() {

        if (currPlayer.isInJail()) {
            stateListeners.forEach(l -> l.onPlayerStartsTurnInJail(currPlayer));
            jailPhase();
        }

        if (!currPlayer.isInJail()) {
            do {
                rollPhase();
                if (doubletCount < 3) {
                    fieldPhase();
                    if (currPlayer.isInJail()) {
                        break;
                    }
                }
                actionPhase();
            } while (rollResult[0] == rollResult[1]);
            doubletCount = 0;
        }
    }

    protected void rollPhase() {

        LOGGER.info(String.format("%s ist dran mit würfeln.", currPlayer.getName()));
        stateListeners.forEach(l -> l.onRollPhaseStart(currPlayer));
        IOService.sleep(2000);

        rollResult = PlayerService.roll(random);
        doubletCount += (rollResult[0] == rollResult[1]) ? 1 : 0;

        stateListeners.forEach(l -> l.onDiceThrow(rollResult, doubletCount));

        if (doubletCount >= 3) {
            if (!Global.RUN_AS_TEST) {
                GUIChat.getInstance().event(String.format("%s hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!",
                        currPlayer.getName()));
            }
            FieldService.toJail(currPlayer);
            LOGGER.info(String.format("%s hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!", currPlayer.getName()));
            FieldService.toJail(currPlayer);
        }
        else {
            int oldPos = currPlayer.getPosition();
            int moveAmount = rollResult[0] + rollResult[1];
            int newPos = (oldPos + moveAmount) % FieldTypes.GAMEBOARD_FIELD_STRUCT.length;

            board.getFieldManager().movePlayer(currPlayer, moveAmount);
            boolean passedGo = (oldPos >= newPos);

            for (GameStateListener l : stateListeners) {
                l.onPlayerMove(currPlayer, oldPos, newPos, passedGo);
            }

        }
    }

    protected void fieldPhase() {
        stateListeners.forEach(l -> l.onFieldPhaseStart(currPlayer));

        boolean repeatPhase;
        FieldTypes type;
        Field field;

        do {
            repeatPhase = false;
            type = FieldTypes.GAMEBOARD_FIELD_STRUCT[currPlayer.getPosition()];
            field = board.getFieldManager().getField(currPlayer.getPosition());

            LOGGER.fine(String.format("Feldphase begonnen: Spieler %s, Feld: %s", currPlayer.getName(), type));
            for (GameStateListener l : stateListeners) {
                l.onPlayerOnNewField(currPlayer, type);
            }

            if (type.isProperty()) {
                PropertyField prop = (PropertyField) board.getFields()[currPlayer.getPosition()];
                onPlayerOnProperty(prop);
            }
            else if (type.isTax()) {
                TaxField taxField = (TaxField) field;
                FieldService.payTax(currPlayer, taxField);
            }
            else if (type.isCard()) {
                CardField cardField = (CardField) field;
                Card card = board.getCardManager().pullAndProcess(cardField.getStackType(), currPlayer);
                repeatPhase = Card.Action.mustRepeatFieldPhase(card.getAction());

                stateListeners.forEach(l -> l.onPlayerOnCardField(currPlayer, cardField, card));
            }
            else if (type == FieldTypes.CORNER_3) /*
             * "Gehen Sie ins Gefängnis"
             */ {
                FieldService.toJail(currPlayer);
            }
        } while (repeatPhase);
    }

    protected void onPlayerOnProperty(PropertyField prop) {

        Player other = prop.getOwner();
        if (other == null) { // Feld frei
            if (Global.RUN_IN_CONSOLE) {
                LOGGER.info(String.format("%s steht auf %s. Wähle eine Aktion!%n[1] Kaufen %n[2] Nicht kaufen",
                        currPlayer.getName(), prop.getName()));
            }
            onBuyPropertyOption(prop);
        }
        else if (other == currPlayer) { // PropertyField im eigenen Besitz
            LOGGER.fine(String.format("%s steht auf seinem eigenen Grundstück.", currPlayer.getName()));
        }
        else { // PropertyField nicht in eigenem Besitz
            LOGGER.info(String.format("%s steht auf %s. Dieses Grundstück gehört von %s.",
                    currPlayer.getName(), prop.getName(), other.getName()));
            PlayerService.takeAndGiveMoneyUnchecked(currPlayer, other, FieldService.getRent(prop, rollResult));
        }
    }

    protected void onBuyPropertyOption(PropertyField prop) {

        int choice = IOService.getBuyPropertyChoice(currPlayer, prop);
        switch (choice) {
            case 1: // Kaufen
                LOGGER.info(String.format("%s >> %s", currPlayer.getName(), prop.getName()));
                if (!FieldService.buyPropertyField(currPlayer, prop)) {
                    LOGGER.warning(String.format("%s hat nicht genug Geld! %s wird zwangsversteigert.",
                            currPlayer.getName(), prop.getName()));
                    onAuction(prop);
                }
                break;

            case 2: // Auktion
                LOGGER.log(Level.INFO, "{0} hat sich gegen den Kauf entschieden, die Stra\u00dfe wird nun versteigert.", currPlayer.getName());
                onAuction(prop);
                break;

            default:
                LOGGER.warning("Fehler: Wahl außerhalb des gültigen Bereichs!");
                break;
        }
    }

    protected void actionPhase() {
        stateListeners.forEach(l -> l.onActionPhaseStart(currPlayer));

        int actionChoice;
        do {
            if (Global.RUN_IN_CONSOLE) {
                LOGGER.info(String.format("%s ist an der Reihe! Waehle eine Aktion:%n[1] - Nichts%n[2] - Haus kaufen%n[3] - Haus verkaufen%n[4] - "
                        + "Hypothek aufnehmen%n[5] - Hypothek abbezahlen%n[6] - Handeln", currPlayer.getName()));
            }

            actionChoice = IOService.getActionChoice(currPlayer, board);
            for (GameStateListener l : stateListeners) {
                l.onPlayerActionOption(currPlayer, actionChoice);
            }

            if (actionChoice > ACTION_NOTHING && actionChoice < ACTION_TRADE) {

                FieldManager fima = board.getFieldManager();
                int fieldChoice;
                if (currPlayer.getAiLevel() < 2) {
                    fieldChoice = getFieldChoice();
                }
                else {
                    fieldChoice = HardKi.getChosenFieldId();
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

        int[] ownedFieldIds = board.getFieldManager().getOwnedPropertyFieldIds(currPlayer);
        String[] fieldNames = Arrays.stream(ownedFieldIds)
                .mapToObj(id -> board.getFieldManager().getField(id).getName())
                .toArray(String[]::new);

        if (currPlayer.getAiLevel() < 2) {
            int chosenFieldChoice = IOService.askForField(currPlayer, fieldNames) - 1;
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
        stateListeners.forEach(l -> l.onJailPhaseStart(currPlayer));

        int choice;
        do {
            if (Global.RUN_IN_CONSOLE) {
                LOGGER.info(String.format(" %s ist im Gefängnis und kann: %n[1] - 3-mal Würfeln, um mit einem Pasch freizukommen "
                        + "%n[2] - Bezahlen (50€) %n[3] - Gefängnis-Frei-Karte benutzen", currPlayer.getName()));
            }

            choice = IOService.getJailChoice(currPlayer);
            for (GameStateListener l : stateListeners) {
                l.onPlayerJailOption(currPlayer, choice);
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
        } while (currPlayer.isInJail() && choice != JAIL_ROLL_OPTION);
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
        stateListeners.forEach(l -> l.onJailRollSuccess(currPlayer));
        PlayerService.freeFromJail(currPlayer);
    }

    protected void onJailRollFailure() {
        stateListeners.forEach(l -> l.onJailRollFailure(currPlayer));

        currPlayer.addDayInJail();
        if (currPlayer.getDaysInJail() >= 3) {
            onForceJailPayOption();
        }
    }

    protected void onForceJailPayOption() {
        stateListeners.forEach(l -> l.onForceJailPayOption(currPlayer));

        LOGGER.info("Drei Runden ohne Pasch, Spieler muss zahlen.");
        PlayerService.takeMoneyUnchecked(currPlayer, 50);
        PlayerService.freeFromJail(currPlayer);
    }

    protected void onJailPayOption() {
        if (PlayerService.takeMoney(currPlayer, 50)) {
            onJailPayFailure();
        }
        else {
            onJailPaySuccess();
        }
    }

    protected void onJailPayFailure() {
        stateListeners.forEach(l -> l.onJailPayFailure(currPlayer));
        LOGGER.info(String.format("%s hat kein Geld um sich freizukaufen.", currPlayer.getName()));
    }

    protected void onJailPaySuccess() {
        stateListeners.forEach(l -> l.onJailPaySuccess(currPlayer));
        LOGGER.info(String.format("%s hat 50 gezahlt und ist frei!", currPlayer.getName()));
        PlayerService.freeFromJail(currPlayer);
    }

    protected void onJailCardOption() {
        if (board.getCardManager().hasJailCards(currPlayer)) {
            onJailCardSuccess();
        }
        else {
            onJailCardFailure();
        }
    }

    protected void onJailCardSuccess() {
        stateListeners.forEach(l -> l.onJailCardSuccess(currPlayer));
        LOGGER.info(String.format("%s hat eine Gefängnis-Frei-Karte benutzt.", currPlayer.getName()));
        board.getCardManager().applyCardAction(Card.Action.JAIL, currPlayer);
    }

    protected void onJailCardFailure() {
        stateListeners.forEach(l -> l.onJailCardFailure(currPlayer));
        LOGGER.info(String.format("%s hat keine Gefängnis-Frei-Karten mehr.", currPlayer.getName()));
    }

    /**
     * Initiiert einen Tausch/Handel ausgehend von einem speziellen Spieler.
     */
    protected void onPlayerTradeOption() {
        stateListeners.forEach(l -> l.onTradeStart(currPlayer));
        TradeService.processPlayerTradeOption(currPlayer, client, board);
        stateListeners.forEach(l -> l.onTradeEnd(currPlayer));
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

    public Player getCurrentPlayer() {
        return currPlayer;
    }
}
