package de.btu.monopoly.core;

import de.btu.monopoly.data.Card;
import de.btu.monopoly.data.CardStack;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.CardField;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.Property;
import de.btu.monopoly.data.parser.CardAction;

import java.util.List;

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
            throw new IllegalArgumentException(String.format("%s is not standing on a card field!"));
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
                    board.getFieldManager().toJail(player);
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
                    fm.payRent(player, prop, null, args[0]);
                    break;
        
                case BIRTHDAY:
                    board.getActivePlayers().forEach(p -> {
                        if (p != player) {
                            PlayerService.takeAndGiveMoneyUnchecked(p, player, args[0]);
                        }
                    });
                    break;
                    
                case RENOVATE:
                    renovate(player, args);
                    break;
                    
                default: throw new RuntimeException("Unknown card action-type!");
            }
        }
    }
    
    private void renovate(Player player, int[] args) {
        FieldManager fm = board.getFieldManager();
        PlayerService.takeMoneyUnchecked(player, fm.getHouseCount(player) * args[0]);
        PlayerService.takeMoneyUnchecked(player, fm.getHotelCount(player) * args[1]);
    }
}
