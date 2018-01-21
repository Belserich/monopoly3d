package de.btu.monopoly.core;

import de.btu.monopoly.GlobalSettings;
import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.FieldService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.core.service.TradeService;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardAction;
import de.btu.monopoly.data.card.CardManager;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.parser.CardStackParser;
import de.btu.monopoly.data.parser.GameBoardParser;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import de.btu.monopoly.input.InputHandler;
import de.btu.monopoly.ki.HardKi;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.PlayerTradeRequest;
import de.btu.monopoly.net.networkClasses.PlayerTradeResponse;
import de.btu.monopoly.ui.Logger.TextAreaHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * @author Christian Prinz
 */
public class Game {

    private static final Logger LOGGER = Logger.getLogger(Game.class.getCanonicalName());

    /**
     * Spielclient
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
     * Die fachliche Komponente des Spiels als Einheit, bestehend aus einem Spielbrett, den Spielern sowie Zuschauern.
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
        if (!GlobalSettings.isRunAsTest() && !GlobalSettings.isRunInConsole()) {
            TextAreaHandler logHandler = new TextAreaHandler();
            LOGGER.addHandler(logHandler);
        }
    }

    public void init() {

        LOGGER.info("Spiel wird initialisiert.");

        try {
            CardStack stack = CardStackParser.parse("/data/card_data.xml");
            stack.shuffle(getRandom());
            LOGGER.finest(stack.toString());
            GameBoardParser.setCardLoadout0(stack);
            GameBoardParser.setCardLoadout1(stack);
            board = GameBoardParser.parse("/data/field_data.xml");
            AuctionService.initAuction(players, client);
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            LOGGER.warning(String.format("Fehler beim initialisieren des Boards / der Karten.", ex));
        }

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
            if (GlobalSettings.isRunInConsole()) {
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

    private boolean isChoiceFromThisClient(Player player) {
        return player == client.getPlayerOnClient();
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
        if (board.getCardManager().useJailCard(player)) {
            LOGGER.info(String.format("%s hat eine Gefängnis-Frei-Karte benutzt.", player.getName()));
        }
        else {
            LOGGER.info(String.format("%s hat keine Gefängnis-Frei-Karten mehr.", player.getName()));
        }
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
            switch (GameBoard.FIELD_STRUCTURE[player.getPosition()]) {

                case TAX: // Steuerfeld
                    TaxField taxField = (TaxField) board.getFields()[player.getPosition()];
                    LOGGER.fine(String.format("%s steht auf einem Steuer-Zahlen-Feld.", player.getName()));
                    FieldService.payTax(player, taxField);
                    break;

                case CARD: // Kartenfeld
                    CardField cardField = (CardField) board.getFields()[player.getPosition()];
                    Card nextCard = cardField.nextCard();
                    LOGGER.fine(String.format("%s steht auf einem Kartenfeld (%s).", player.getName(), cardField.getName()));
                    board.getCardManager().manageCardActions(player, nextCard);

                    if (nextCard.getActions().contains(CardAction.SET_POSITION)
                            || nextCard.getActions().contains(CardAction.MOVE_PLAYER)
                            || nextCard.getActions().contains(CardAction.NEXT_SUPPLY)) {
                        repeatPhase = true;
                    }
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
        } while (repeatPhase);
    }

    private void processPlayerOnPropertyField(Player player, PropertyField prop, int[] rollResult) {
        Player other = prop.getOwner();
        if (other == null) { // Feld frei
            if (GlobalSettings.isRunInConsole()) {
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
                if (!FieldService.buyPropertyField(player, prop, prop.getPrice())) {
                    LOGGER.warning(String.format("%s hat nicht genug Geld! %s wird zwangsversteigert.",
                            player.getName(), prop.getName()));
                    betPhase(prop);
                }
                break;

            case 2: // Auktion
                LOGGER.log(Level.INFO, "{0} hat sich gegen den Kauf entschieden, die Stra\u00dfe wird nun versteigert.", player.getName());
                betPhase(prop);
                break;

            default:
                LOGGER.warning("Fehler: Wahl außerhalb des gültigen Bereichs!");
                break;
        }
    }

    private void actionPhase(Player player) {

        int choice;
        do {
            if (GlobalSettings.isRunInConsole()) {
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
                if (player.getKiLevel() < 2) {
                    chosenFieldId = ownedFieldIds[InputHandler.askForField(player, fieldNames) - 1];
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

    private void processPlayerTradeOption(Player player) {

        PlayerTradeResponse response = null;

        if (isChoiceFromThisClient(player)) {

            PlayerTradeRequest request = new PlayerTradeRequest();
            Trade trade = new Trade();

            List<Player> activePlayers = board.getActivePlayers();
            StringBuilder builder = new StringBuilder("Waehle einen Spieler:\n");
            for (int i = 0; i < activePlayers.size(); i++) {
                Player p = activePlayers.get(i);
                if (p != player) {
                    builder.append(String.format("[%d] - %s%n", i + 1, p.toString()));
                }
            }
            LOGGER.info(builder.toString());
            Player otherPlayer = activePlayers.get(InputHandler.getUserInput(activePlayers.size()) - 1);

            trade.setSupply(createTradeOfferFor(player));
            trade.setDemand(createTradeOfferFor(otherPlayer));
            request.setTrade(trade);

            LOGGER.info(String.format("Zusammenfassung:%n%n%s%nHandelsanfrage wirklich absenden?%n\t[1] - Ja%n\t[2] - Nein",
                    trade.toString(board)));

            request.setDenied(InputHandler.getUserInput(2) == 2);

            client.sendTCP(request);

            response = (PlayerTradeResponse) client.waitForObjectOfClass(PlayerTradeResponse.class);
        }
        else {

            PlayerTradeRequest request = (PlayerTradeRequest) client.waitForObjectOfClass(PlayerTradeRequest.class);

            if (request.isDenied()) {
                return;
            }

            Trade trade = request.getTrade();
            Player receipt = board.getPlayer(request.getTrade().getDemand().getPlayerId());
            Player thisPlayer = client.getPlayerOnClient();

            if (receipt == thisPlayer) {

                LOGGER.info(String.format("Spieler %s hat dir eine Handelsanfrage gemacht:%n%n%s%n\t[1] - annehmen%n\t[2] - ablehnen",
                        board.getPlayer(trade.getSupply().getPlayerId()).getName(), trade.toString(board)));
                int choice = IOService.getClientChoice(receipt, 2);

                response = new PlayerTradeResponse();
                response.setRequest(request);
                response.setAccepted(choice == 1);

                client.sendTCP(response);
            }
            else {
                response = (PlayerTradeResponse) client.waitForObjectOfClass(PlayerTradeResponse.class);
            }
        }

        if (response.isAccepted()) {
            LOGGER.info("<<< HANDEL ANGENOMMEN >>>");
            TradeService.completeTrade(response.getRequest().getTrade(), board);
        }
        else {
            LOGGER.info("<<< HANDEL ABGELEHNT >>>");
        }
    }

    private TradeOffer createTradeOfferFor(Player player) {

        TradeOffer retObj = new TradeOffer();

        List<Integer> ownedIds;
        ArrayList<Integer> chosenIds = new ArrayList<>();

        boolean runOnce = false,
                doneChoosing = false;

        retObj.setPlayerId(player.getId());

        ownedIds = Arrays.stream(board.getFieldManager().getOwnedPropertyFieldIds(player))
                .boxed().collect(Collectors.toList());
        while (!doneChoosing && (ownedIds.size() - chosenIds.size()) > 0) {
            printPropertyOffer(player, ownedIds, runOnce);
            runOnce = true;
            doneChoosing = handleOfferChoice(ownedIds, chosenIds);
        }

        retObj.setPropertyIds(chosenIds.stream().mapToInt(i -> i).toArray());

        doneChoosing = false;
        runOnce = false;
        chosenIds.clear();

        ownedIds = Arrays.stream(board.getCardManager().getTradeableCardIds(player))
                .boxed().collect(Collectors.toList());
        while (!doneChoosing && (ownedIds.size() - chosenIds.size()) > 0) {
            printCardOffer(player, ownedIds, runOnce);
            runOnce = true;
            doneChoosing = handleOfferChoice(ownedIds, chosenIds);
        }

        retObj.setCardIds(chosenIds.stream().mapToInt(i -> i).toArray());

        if (player.getBank().isLiquid()) {

            LOGGER.info(String.format("Soll %s Geld bieten?%n\t[1] - Ja%n\t[2] - Nein", player.getName()));
            if (InputHandler.getUserInput(2) == 1) {
                LOGGER.info(String.format("Wieviel Geld bietet Spieler %s?%n", player.getName()));
                retObj.setMoney(InputHandler.getUserInput(player.getMoney()));
            }
        }

        return retObj;
    }

    public boolean handleOfferChoice(List<Integer> ownedIds, List<Integer> chosenIds) {

        int choice, chosenId;

        choice = InputHandler.getUserInput(ownedIds.size() + 1) - 1;
        if (choice != ownedIds.size()) {
            chosenId = ownedIds.get(choice);
            chosenIds.add(chosenId);
            ownedIds.remove(ownedIds.indexOf(chosenId));
            return false;
        }
        else {
            return true;
        }
    }

    public void printPropertyOffer(Player player, List<Integer> ownedPropIds, boolean runOnce) {

        FieldManager fm = board.getFieldManager();

        StringBuilder builder;
        int id;

        builder = new StringBuilder(String.format("Welches Gebaeude bietet Spieler %s%s?%n",
                player.getName(), runOnce ? " noch" : ""));
        for (id = 0; id < ownedPropIds.size(); id++) {
            builder.append(String.format("[%d] - %s%n", id + 1, fm.getField(ownedPropIds.get(id)).getName()));
        }

        builder.append(String.format("[%d] - Keins%n", id + 1));
        LOGGER.info(builder.toString());
    }

    public void printCardOffer(Player player, List<Integer> ownedCardIds, boolean runOnce) {

        CardManager cm = board.getCardManager();

        StringBuilder builder;
        int id;

        builder = new StringBuilder(String.format("Welche Karte bietet Spieler %s%s?%n",
                player.getName(), runOnce ? " noch" : ""));
        for (id = 0; id < ownedCardIds.size(); id++) {
            builder.append(String.format("[%d] - %s%n", id + 1, cm.getCard(player, ownedCardIds.get(id)).getName()));
        }

        builder.append(String.format("[%d] - Keine%n", id + 1));
        LOGGER.info(builder.toString());
    }

    public void betPhase(PropertyField property) {
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
}
