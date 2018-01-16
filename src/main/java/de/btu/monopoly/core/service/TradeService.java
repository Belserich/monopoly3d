package de.btu.monopoly.core.service;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardManager;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

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
    
    /**
     * Erstellt eine TradeOffer-Instanz, die alle gebotenen handelbaren
     * Objekt-IDs eines Spielers zusammenfasst.
     *
     * @param player Spieler
     * @return Angebots-Instanz
     */
    public static TradeOffer createTradeOfferFor(Player player) {
        
        TradeOffer retObj = new TradeOffer();
        
        GameBoard board = player.getBoard();
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
        else return true;
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
    private static void printCardOffer(Player player, CardManager cm,  List<Integer> ownedCardIds, boolean runOnce) {
        
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
    
    /**
     * Verarbeitet einen Tauschhandel.
     *
     * @param trade Tauschobjekt
     * @param board Spielbrett-Instanz
     */
    public static void completeTrade(Trade trade, GameBoard board) {
        
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
