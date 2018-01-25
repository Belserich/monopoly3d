package de.btu.monopoly.data.card;

import de.btu.monopoly.GlobalSettings;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.FieldService;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.data.Tradeable;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ui.TextAreaHandler;

import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardManager {

    private static final Logger LOGGER = Logger.getLogger(CardManager.class.getCanonicalName());

    private GameBoard board;

    public CardManager(GameBoard board) {
        this.board = board;
        if (!GlobalSettings.RUN_AS_TEST && !GlobalSettings.RUN_IN_CONSOLE) {
            TextAreaHandler textHandler = new TextAreaHandler();
            LOGGER.addHandler(textHandler);
        }
    }

    public void manageCardActions(Player player, Card card) {

        PropertyField prop;
        LOGGER.info(String.format("%s hat eine Karte gezogen: %s", player.getName(), card));

        for (CardAction action : card.getActions()) {
            int args[] = card.getArgs();
            action.ensureArgs(args);
            switch (action) {
                case JAIL:
                    LOGGER.info("Es ist eine Gefängnis-Frei-Karte.");
                    card.getCardStack().removeCard(card);
                    player.getCardStack().addCard(card);
                    break;

                case GIVE_MONEY:
                    LOGGER.info("Der Spieler bekommt Geld.");
                    PlayerService.giveMoney(player, args[0]);
                    break;

                case GO_JAIL:
                    LOGGER.info("Der Spieler muss ins Gefängnis.");
                    FieldService.toJail(player);
                    break;

                case PAY_MONEY:
                    LOGGER.info("Der Spieler muss Geld zahlen.");
                    PlayerService.takeMoney(player, args[0]);
                    break;

                case MOVE_PLAYER:
                    LOGGER.info("Der Spieler wird bewegt.");
                    IOService.sleep(3000);
                    board.getFieldManager().movePlayer(player, args[0]);
                    break;

                case SET_POSITION:
                    LOGGER.info("Der Spieler bekommt eine neue Position.");
                    board.getFieldManager().movePlayer(player, args[0] - player.getPosition());
                    break;

                case PAY_MONEY_ALL:
                    LOGGER.info("Der Spieler muss Geld an sämtliche Mitspieler zahlen.");
                    board.getActivePlayers().forEach(p -> {
                        if (p != player) {
                            PlayerService.takeAndGiveMoneyUnchecked(player, p, args[0]);
                        }
                    });
                    break;

                case NEXT_SUPPLY:
                    LOGGER.info("Der Spieler rückt bis zum nächsten Werk vor.");
                    prop = (PropertyField) board.getFieldManager().movePlayer(player, GameBoard.FieldType.SUPPLY);
                    FieldService.payRent(player, prop, null, 1);
                    break;

                case NEXT_STATION_RENT_AMP:
                    LOGGER.info("Der Spieler rückt bis zum nächsten Bahnhof vor. Die Miete verdoppelt sich.");
                    prop = (PropertyField) board.getFieldManager().movePlayer(player, GameBoard.FieldType.STATION);
                    FieldService.payRent(player, prop, null, args[0]);
                    break;

                case BIRTHDAY:
                    LOGGER.info("Der Spieler bekommt Geld von seinen Mitspielern.");
                    board.getActivePlayers().forEach(p -> {
                        if (p != player) {
                            PlayerService.takeAndGiveMoneyUnchecked(p, player, args[0]);
                        }
                    });
                    break;

                case RENOVATE:
                    LOGGER.info("Der Spieler muss für jedes Haus und Hotel seinem Besitz Geld zahlen.");
                    processRenovateAction(player, args);
                    break;

                default:
                    throw new RuntimeException("Unknown card action-type!");
            }
        }
    }

    public boolean useJailCard(Player player) {
        LOGGER.info(String.format("%s benutzt eine Gefaengnis-Frei-Karte.", player.getName()));
        CardStack stack = player.getCardStack();
        if (stack.countCardsOfAction(CardAction.JAIL) > 0) {
            Card jailCard = stack.removeCardOfAction(CardAction.JAIL);
            jailCard.getCardStack().addCard(jailCard);
            PlayerService.freeFromJail(player);
            return true;
        }
        else {
            return false;
        }
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

    public Card getCard(Player player, int id) {
        CardStack stack = player.getCardStack();
        return stack.cards.get(id);
    }

    public void processRenovateAction(Player player, int[] args) {
        FieldManager fm = board.getFieldManager();
        int[] counts = fm.getHouseAndHotelCount(player);

        LOGGER.info(String.format("Rechnung: %d (Häuser) * %d und %d (Hotels) * %d", counts[0], args[0], counts[1], args[0] * 5));
        PlayerService.takeMoneyUnchecked(player, counts[0] * args[0]);
        PlayerService.takeMoneyUnchecked(player, counts[1] * args[0] * 5);
    }

    private void putBackOnStack(Card card) {
        card.getCardStack().addCard(card);
    }

    public void bankrupt(Player player) {
        LOGGER.info(String.format("%s's gesammelte Karten werden wieder zurück auf ihre Stapel gelegt.", player.getName()));
        CardStack stack = player.getCardStack();
        for (int i = 0; i < stack.countCardsOfAction(CardAction.JAIL); i++) {
            putBackOnStack(stack.removeCardOfAction(CardAction.JAIL));
        }
        stack.removeAll();
    }
}
