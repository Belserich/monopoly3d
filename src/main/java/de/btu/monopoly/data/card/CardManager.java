package de.btu.monopoly.data.card;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.FieldService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.data.Tradeable;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.util.Assets;

import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardManager {

    private static final Logger LOGGER = Logger.getLogger(CardManager.class.getCanonicalName());

    private GameBoard board;

    private CardStack communityCards;
    private CardStack eventCards;

    public CardManager(GameBoard board) {
        this.board = board;
        
        communityCards = Assets.getCommunityCards();
        eventCards = Assets.getEventCards();
    }
    
    /**
     * @param type Typ des Kartenstapels
     * @param player Spieler
     * @return gezogene Karte
     * @see #pullAndProcess(CardStack.Type, Player)
     */
    public Card pullAndProcess(CardStack.Type type, Player player) {

        CardStack stack = stackFor(type);
        Card card = stack.nextCard();

        LOGGER.info(String.format("%s hat eine Karte gezogen. Kartentyp: %s", player.getName(), card.getAction()));
        
        if (card instanceof Takeable) ((Takeable)card).drawTo(player.getCardStack());
        else applyCardAction(card.getAction(), card.getArg(), player);
        
        return card;
    }

    private CardStack stackFor(CardStack.Type type) {

        switch (type) {
            case COMMUNITY:
                return communityCards;
            case EVENT:
                return eventCards;
            default:
                throw new RuntimeException(String.format("Unknown originalStack type %s", type));
        }
    }
    
    /**
     * @param action Kartenaktion
     * @param arg Kartenargument
     * @param player Spieler
     */
    public void applyCardAction(Card.Action action, int arg, Player player) {

        LOGGER.info(String.format("%s benutzt eine Karte (Aktion: %s)", player.getName(), action));

        switch (action) {

            case JAIL:
                processJailAction(player);
                break;

            case GET_MONEY:
                PlayerService.giveMoney(player, arg);
                break;

            case GO_JAIL:
                FieldService.toJail(player);
                break;

            case PAY_BANK:
                PlayerService.takeMoney(player, arg);
                break;

            case MOVE:
                board.getFieldManager().movePlayer(player, arg);
                break;

            case SET_POSITION:
                board.getFieldManager().movePlayer(player, arg - player.getPosition());
                break;

            case PAY_ALL:
                processPayAllAction(player, arg);
                break;

            case BIRTHDAY:
                processBirthdayAction(player, arg);
                break;

            case RENOVATE:
                processRenovateAction(player, arg);
                break;

            case MOVE_NEXT_SUPPLY:
                PropertyField prop = (PropertyField) board.getFieldManager().movePlayer(player, FieldTypes.SUPPLY);
                FieldService.payRent(player, prop, null, 1);
                break;

            case MOVE_NEXT_STATION_RENT_AMP:
                prop = (PropertyField) board.getFieldManager().movePlayer(player, FieldTypes.STATION);
                FieldService.payRent(player, prop, null, arg);
                break;
        
            default: throw new RuntimeException(String.format("Unknown card action: %s", action));
        }
    }

    public void applyCardAction(Card.Action action, Player player) {
        applyCardAction(action, 0, player);
    }

    private void processJailAction(Player player) {

        CardStack playerStack = player.getCardStack();
        Card jailCard = playerStack.nextCardOfAction(Card.Action.JAIL);
        playerStack.removeCard(jailCard);
        PlayerService.freeFromJail(player);
    }

    private void processPayAllAction(Player player, int arg) {

        board.getActivePlayers().forEach(p -> {
            if (p != player) {
                PlayerService.takeAndGiveMoneyUnchecked(player, p, arg);
            }
        });
    }

    private void processBirthdayAction(Player player, int arg) {

        board.getActivePlayers().forEach(p -> {
            if (p != player) {
                PlayerService.takeAndGiveMoneyUnchecked(p, player, arg);
            }
        });
    }

    private void processRenovateAction(Player player, int arg) {

        FieldManager fm = board.getFieldManager();
        int[] counts = fm.getHouseAndHotelCount(player);

        LOGGER.info(String.format("Rechnung: %d (Häuser) * %d und %d (Hotels) * %d", counts[0], arg, counts[1], arg * 5));

        PlayerService.takeMoneyUnchecked(player, counts[0] * arg);
        PlayerService.takeMoneyUnchecked(player, counts[1] * arg * 5);
    }

    public int[] getTradeableCardIds(Player player) {
        CardStack stack = player.getCardStack();
        IntStream.Builder builder = IntStream.builder();
        for (int id = 0; id < stack.cards.size(); id++) {
            if (stack.cards.get(id) instanceof Tradeable) {
                builder.accept(id);
            }
        }
        return builder.build().toArray();
    }

    public boolean hasJailCards(Player player) {
        return player.getCardStack().countCardsOfAction(Card.Action.JAIL) > 0;
    }

    public void bankrupt(Player player) {
        LOGGER.info(String.format("%s's gesammelte Karten werden wieder zurück auf ihre Stapel gelegt.", player.getName()));
        CardStack stack = player.getCardStack();
        for (int i = 0; i < stack.countCardsOfAction(Card.Action.JAIL); i++) {
            Card card = stack.cardAt(i);
            if (card instanceof Takeable) {
                ((Takeable) card).putBack();
            }
        }
        stack.removeAll();
    }

    public CardStack getStack(CardStack.Type type) {
        
        switch(type) {
            case COMMUNITY: return communityCards;
            case EVENT: return eventCards;
            default: throw new RuntimeException(String.format("Undefined stack type: %s", type));
        }
    }
}
