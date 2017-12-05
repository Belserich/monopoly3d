package de.btu.monopoly.data.card;

import de.btu.monopoly.core.FieldManager;
import de.btu.monopoly.core.FieldService;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.PlayerService;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.CardField;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.Property;
import de.btu.monopoly.data.parser.CardAction;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardManager {
    
    private GameBoard board;
    
    public CardManager(GameBoard board) {
        this.board = board;
    }
    
    public void processPlayerOnCardField(Player player) {
        Field field = board.getFields()[player.getPosition()];
        
        if (!(field instanceof CardField)) {
            throw new IllegalArgumentException(String.format("%s is not standing on a card field!", player.getName()));
        }
    
        CardStack stack = ((CardField) field).getCardStack();
        Card card = stack.nextCard();
        
        for (CardAction action : card.getActions()) {
            int args[] = card.getArgs();
            action.ensureArgs(args);
            switch (action) {
                case JAIL:
                    stack.removeCard(card);
                    player.getCardStack().addCard(card);
                    break;
        
                case GIVE_MONEY:
                    PlayerService.giveMoney(player, args[0]);
                    break;
        
                case GO_JAIL:
                    FieldService.toJail(player);
                    break;
        
                case PAY_MONEY:
                    PlayerService.takeMoney(player, args[0]);
                    break;
        
                case MOVE_PLAYER:
                    board.getFieldManager().movePlayer(player, args[0]);
                    break;
        
                case SET_POSITION:
                    board.getFieldManager().movePlayer(player, args[0] - player.getPosition());
                    break;
        
                case PAY_MONEY_ALL:
                    board.getActivePlayers().forEach(p -> {
                        if (p != player) {
                            PlayerService.takeAndGiveMoneyUnchecked(player, p, args[0]);
                        }
                    });
                    break;
        
                case NEXT_SUPPLY:
                    board.getFieldManager().movePlayer(player, GameBoard.FieldType.SUPPLY);
                    break;
        
                case NEXT_STATION_RENT_AMP:
                    FieldManager fm = board.getFieldManager();
                    Property prop = (Property) fm.movePlayer(player, GameBoard.FieldType.STATION);
                    FieldService.payRent(player, prop, null, args[0]);
                    break;
        
                case BIRTHDAY:
                    board.getActivePlayers().forEach(p -> {
                        if (p != player) {
                            PlayerService.takeAndGiveMoneyUnchecked(p, player, args[0]);
                        }
                    });
                    break;
                    
                case RENOVATE:
                    processRenovateAction(player, args);
                    break;
                    
                default: throw new RuntimeException("Unknown card action-type!");
            }
        }
    }
    
    public boolean useJailCard(Player player) {
        CardStack stack = player.getCardStack();
        if (stack.countCardsOfAction(CardAction.JAIL) > 0) {
            Card jailCard = stack.removeCardOfAction(CardAction.JAIL);
            jailCard.getCardStack().addCard(jailCard);
            PlayerService.freeFromJail(player);
            return true;
        }
        else return false;
    }
    
    private void processRenovateAction(Player player, int[] args) {
        FieldManager fm = board.getFieldManager();
        PlayerService.takeMoneyUnchecked(player, fm.getHouseCount(player) * args[0]);
        PlayerService.takeMoneyUnchecked(player, fm.getHotelCount(player) * args[1]);
    }
    
    private void putBackOnStack(Card card) {
        card.getCardStack().addCard(card);
    }
    
    public void bankrupt(Player player) {
        CardStack stack = player.getCardStack();
        for (int i = 0; i < stack.countCardsOfAction(CardAction.JAIL); i++) {
            putBackOnStack(stack.removeCardOfAction(CardAction.JAIL));
        }
        stack.removeAll();
    }
}
