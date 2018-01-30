package de.btu.monopoly.core.service;

import de.btu.monopoly.Global;
import de.btu.monopoly.GlobalSettings;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardManager;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.data.PlayerTradeRequest;
import de.btu.monopoly.net.data.PlayerTradeResponse;
import de.btu.monopoly.ui.GuiTrade;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class TradeService {

    private static final Logger LOGGER = Logger.getLogger(TradeService.class.getCanonicalName());
    private static boolean isRunningOnce = false;

//    public static void startTradeOption(Player player, GameClient, GameBoard board) {
//
//    }
    /**
     * Initiiert und koordiniert den Handel ausgehend von einem speziellen Spieler.
     *
     * @param player Initiator
     * @param client Spielclient
     * @param board Spielbrett
     */
    public static void processPlayerTradeOption(Player player, GameClient client, GameBoard board) {

        PlayerTradeResponse response;
        GuiTrade tradeGui = new GuiTrade();

        if (PlayerService.isMainPlayer(player, client)) {
            TradeService.createAndSendTradeRequest(client, board, player, tradeGui);
            response = (PlayerTradeResponse) client.waitForObjectOfClass(PlayerTradeResponse.class);
        }
        else {
            response = TradeService.waitAndTryProcessTradeRequest(client, board, tradeGui);
        }

        if (response == null) {
            response = (PlayerTradeResponse) client.waitForObjectOfClass(PlayerTradeResponse.class);
        }

        if (GlobalSettings.RUN_IN_CONSOLE) {
            if (response.isAccepted()) {
                LOGGER.info("<<< HANDEL ANGENOMMEN >>>");
                TradeService.completeTrade(response.getRequest().getTrade(), board);
            }
            else {
                LOGGER.info("<<< HANDEL ABGELEHNT >>>");
            }
        }
    }

    /**
     * Wartet auf das nächste {@link PlayerTradeRequest}-Objekt und versucht es zu verarbeiten.
     *
     * @see TradeService#tryProcessTradeRequest(GameClient, GameBoard, PlayerTradeRequest)
     */
    private static PlayerTradeResponse waitAndTryProcessTradeRequest(GameClient client, GameBoard board, GuiTrade tradeGui) {

        PlayerTradeRequest request = (PlayerTradeRequest) client.waitForObjectOfClass(PlayerTradeRequest.class);
        return tryProcessTradeRequest(client, board, request, tradeGui);
    }

    /**
     * Versucht, ein gegebenes {@link PlayerTradeRequest}-Objekt zu verarbeiten.
     *
     * @param client Spielclient
     * @param board Spielbrett
     * @param request Handelsanfrage
     * @return Das gesendete {@link PlayerTradeResponse}-Objekt, falls die empfangene Request an diesen Clienten gerichtet war,
     * sonst null.
     */
    private static PlayerTradeResponse tryProcessTradeRequest(GameClient client, GameBoard board, PlayerTradeRequest request, GuiTrade tradeGui) {

        if (request.isDenied()) {
            return null;
        }

        PlayerTradeResponse response = null;
        Trade trade = request.getTrade();
        Player receipt = board.getPlayer(request.getTrade().getDemand().getPlayerId());
        Player thisPlayer = client.getPlayerOnClient();

        if (receipt == thisPlayer) {

            if (GlobalSettings.RUN_IN_CONSOLE) {

                LOGGER.info(String.format("Spieler %s hat dir eine Handelsanfrage gemacht:%n%n%s%n\t[1] - annehmen%n\t[2] - ablehnen",
                        board.getPlayer(trade.getSupply().getPlayerId()).getName(), trade.toString(board)));
                int choice = IOService.getUserInput(2);

                response = new PlayerTradeResponse();
                response.setRequest(request);
                response.setAccepted(choice == 1);

                client.sendTCP(response);
            }
            else {
                response = new PlayerTradeResponse();
                response.setRequest(request);

                Global.ref().getGameSceneManager().showOfferPopup(tradeGui);
                while (!Global.ref().getGameSceneManager().getTradeAnswerIsGiven()) {
                    IOService.sleep(500);
                }
                Global.ref().getGameSceneManager().resteTradeAnswerIsGiven();
                response.setAccepted(Global.ref().getGameSceneManager().getTradeAnswer());

                client.sendTCP(response);

                if (response.isAccepted()) {
                    TradeService.completeTrade(response.getRequest().getTrade(), board);
                }
            }
        }
        return response;
    }

    private static void createAndSendTradeRequest(GameClient client, GameBoard board, Player supplier, GuiTrade tradeGui) {
        client.sendTCP(createTradeRequest(supplier, board, tradeGui));
    }

    private static PlayerTradeRequest createTradeRequest(Player supplier, GameBoard board, GuiTrade tradeGui) {

        PlayerTradeRequest request = new PlayerTradeRequest();
        request.setTrade(createTrade(supplier, board, tradeGui));
        if (GlobalSettings.RUN_IN_CONSOLE) {
            request.setDenied(IOService.getUserInput(2) == 2);
        }
        else {
            request.setDenied(false);
        }
        return request;
    }

    private static Trade createTrade(Player supplier, GameBoard board, GuiTrade tradeGui) {

        Trade trade = new Trade();
        if (GlobalSettings.RUN_IN_CONSOLE) {
            List<Player> activePlayers = board.getActivePlayers();
            StringBuilder builder = new StringBuilder("Waehle einen Spieler:\n");
            for (int i = 0; i < activePlayers.size(); i++) {
                Player p = activePlayers.get(i);
                if (p != supplier) {
                    builder.append(String.format("[%d] - %s%n", i + 1, p.toString()));
                }
            }
            LOGGER.info(builder.toString());
            Player receipt = activePlayers.get(IOService.getUserInput(activePlayers.size()) - 1);

            trade.setSupply(TradeService.createTradeOffer(supplier, board));
            trade.setDemand(TradeService.createTradeOffer(receipt, board));
        }
        else {

            Global.ref().getGameSceneManager().initTradePopup(supplier, tradeGui);
            while (!Global.ref().getGameSceneManager().getTradeOfferIsCreated()) {
                IOService.sleep(500);
            }
            trade.setSupply(TradeService.createTradeOfferGui(supplier, tradeGui));
            trade.setDemand(TradeService.createTradeOfferGui(Global.ref().getGameSceneManager().getTradePartner(tradeGui), tradeGui));
            Global.ref().getGameSceneManager().resetTradeOfferIsCreated();
        }
        if (GlobalSettings.RUN_IN_CONSOLE) {
            LOGGER.info(String.format("Zusammenfassung:%n%n%s%nHandelsanfrage wirklich absenden?%n\t[1] - Ja%n\t[2] - Nein",
                    trade.toString(board)));
        }
        return trade;
    }

    /**
     * Erstellt eine TradeOffer-Instanz, die alle gebotenen handelbaren Objekt-IDs eines Spielers zusammenfasst.
     *
     * @param player Spieler
     * @param board Board
     * @return Angebots-Instanz
     */
    private static TradeOffer createTradeOffer(Player player, GameBoard board) {

        TradeOffer retObj = new TradeOffer();

        List<Integer> ownedIds;
        ArrayList<Integer> chosenIds = new ArrayList<>();

        boolean runOnce = false,
                doneChoosing = false;

        retObj.setPlayerId(player.getId());

        ownedIds = Arrays.stream(board.getFieldManager().getOwnedPropertyFieldIds(player))
                .boxed().collect(Collectors.toList());
        while (!doneChoosing && (ownedIds.size() - chosenIds.size()) > 0) {
            printPropertyOffer(player, board.getFieldManager(), ownedIds, runOnce);
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
            printCardOffer(player, board.getCardManager(), ownedIds, runOnce);
            runOnce = true;
            doneChoosing = handleOfferChoice(ownedIds, chosenIds);
        }

        retObj.setCardIds(chosenIds.stream().mapToInt(i -> i).toArray());

        if (player.getBank().isLiquid()) {

            LOGGER.info(String.format("Soll %s Geld bieten?%n\t[1] - Ja%n\t[2] - Nein", player.getName()));
            if (IOService.getUserInput(2) == 1) {
                LOGGER.info(String.format("Wieviel Geld bietet Spieler %s?%n", player.getName()));
                retObj.setMoney(IOService.getUserInput(player.getMoney()));
            }
        }

        return retObj;
    }

    /**
     * Erstellt eine TradeOffer-Instanz, die sich alle gebotenen Objekte aus der GUI holt
     *
     * @param player Spieler
     * @return Angebots-Instanz
     */
    public static TradeOffer createTradeOfferGui(Player player, GuiTrade tradeGui) {

        TradeOffer offer = new TradeOffer();

        offer.setPlayerId(player.getId());
        offer.setPropertyIds(Global.ref().getGameSceneManager().getPropertyIdsForTrade(player, tradeGui));
        offer.setCardIds(Global.ref().getGameSceneManager().getCardIdsForTrade(player, tradeGui));
        offer.setMoney(Global.ref().getGameSceneManager().getMoneyForTrade(player, tradeGui));

        System.out.println("Bin hier mit Spieler " + player.getName());
        return offer;
    }

    /**
     * Hilfsmethode
     *
     * @param ownedIds Die IDs der handelbaren Objekte in Spielerbesitz
     * @param chosenIds Die IDs der ausgewählten handelbaren Objekte in Spielerbesitz
     * @return ob der Spieler mit der momentanen ID-Auswahl fertig ist
     */
    private static boolean handleOfferChoice(List<Integer> ownedIds, List<Integer> chosenIds) {

        int choice, chosenId;

        choice = IOService.getUserInput(ownedIds.size() + 1) - 1;
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

    /**
     * Fragt nach dem Gebäude-Angebot.
     *
     * @param player Spieler
     * @param fm FieldManager-Instanz
     * @param ownedPropIds IDs der Gebäude im Besitz des Spielers
     * @param runOnce ob diese Methode schon einmal ausgeführt wurde
     */
    private static void printPropertyOffer(Player player, FieldManager fm, List<Integer> ownedPropIds, boolean runOnce) {

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

    /**
     * Fragt nach dem Karten-Angebot.
     *
     * @param player Spieler
     * @param cm CardManager-Instanz
     * @param ownedCardIds IDs der Karten im Besitz des Spielers
     * @param runOnce ob diese Methode schon einmal ausgeführt wurde
     */
    private static void printCardOffer(Player player, CardManager cm, List<Integer> ownedCardIds, boolean runOnce) {

        StringBuilder builder;
        int id;

        builder = new StringBuilder(String.format("Welche Karte bietet Spieler %s%s?%n",
                player.getName(), runOnce ? " noch" : ""));

        CardStack playerStack = player.getCardStack();
        for (id = 0; id < ownedCardIds.size(); id++) {
            builder.append(String.format("[%d] - %s%n", id + 1, playerStack.cardAt(id).getName()));
        }

        builder.append(String.format("[%d] - Keine%n", id + 1));
        LOGGER.info(builder.toString());
    }

    /**
     * Verarbeitet einen Tauschhandel.
     *
     * @param trade Tauschobjekt
     * @param board Spielbrett-Instanz
     */
    private static void completeTrade(Trade trade, GameBoard board) {

        TradeOffer supply = trade.getSupply();
        Player supplier = board.getPlayer(supply.getPlayerId());

        TradeOffer demand = trade.getDemand();
        Player receipt = board.getPlayer(demand.getPlayerId());

        FieldManager fm = board.getFieldManager();

        completeTradeOffer(supply, supplier, receipt, fm);
        completeTradeOffer(demand, receipt, supplier, fm);
    }

    /**
     * Verarbeitet ein Tauschangebot.
     *
     * @param offer Angebot
     * @param supplier bietender Spieler
     * @param receipt empfangender Spieler
     * @param fm Feldmanager
     */
    private static void completeTradeOffer(TradeOffer offer, Player supplier, Player receipt, FieldManager fm) {

        CardStack suppStack = supplier.getCardStack();
        CardStack recStack = receipt.getCardStack();
        int suppMoney = offer.getMoney();

        for (int fieldId : offer.getPropertyIds()) {
            PropertyField field = (PropertyField) fm.getField(fieldId);
            field.setOwner(receipt);
        }

        for (int cardId : offer.getCardIds()) {
            Card card = suppStack.cardAt(cardId);
            suppStack.removeCard(card);
            recStack.addCard(card);
        }

        if (suppMoney != 0) {
            PlayerService.takeAndGiveMoneyUnchecked(supplier, receipt, suppMoney);
        }
    }
}
