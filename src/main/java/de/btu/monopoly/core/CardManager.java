package de.btu.monopoly.core;

import de.btu.monopoly.data.Card;
import de.btu.monopoly.data.CardStack;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.CardField;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.Property;

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
        
        for (Card.Action action : card.getActions()) {
            int args[] = card.getArgs();
            switch (action) {
                case JAIL:
                    stack.removeCard(card);
                    player.getCardStack().addCard(card);
                    break;
        
                case GIVE_MONEY:
                    ensureArg(args);
                    PlayerService.giveMoney(player, args[0]);
                    break;
        
                case GO_JAIL:
                    board.getFieldManager().toJail(player);
                    break;
        
                case PAY_MONEY:
                    ensureArg(args);
                    PlayerService.takeMoney(player, args[0]);
                    break;
        
                case MOVE_PLAYER:
                    ensureArg(args);
                    board.getFieldManager().movePlayer(player, args[0]);
                    break;
        
                case SET_POSITION:
                    ensureArg(args);
                    board.getFieldManager().movePlayer(player, args[0] - player.getPosition());
                    break;
        
                case PAY_MONEY_ALL:
                    ensureArg(args);
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
                    ensureArg(args);
                    FieldManager fm = board.getFieldManager();
                    Property prop = (Property) fm.movePlayer(player, GameBoard.FieldType.STATION);
                    fm.payRent(player, prop, null, args[0]);
                    break;
        
                case BIRTHDAY:
                    ensureArg(args);
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
        ensureArgs(args, 2);
        FieldManager fm = board.getFieldManager();
        PlayerService.takeMoneyUnchecked(player, fm.getHouseCount(player) * args[0]);
        PlayerService.takeMoneyUnchecked(player, fm.getHotelCount(player) * args[1]);
    }
    
    private void ensureArgs(int[] args, int amount) {
        if (args.length != amount) {
            throw new IllegalArgumentException(String.format("Given amount of arguments (%d) does not match expected amount (%d)!",
                    args.length, amount));
        }
    }
    
    private void ensureArg(int[] args) {
        ensureArgs(args, 1);
    }
}
