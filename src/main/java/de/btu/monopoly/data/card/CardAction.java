package de.btu.monopoly.data.card;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public enum CardAction {
    
    JAIL (0),
    GO_JAIL (0),
    GIVE_MONEY (1),
    NEXT_STATION_RENT_AMP (1),
    MOVE_PLAYER (1),
    PAY_MONEY (1),
    SET_POSITION (1),
    RENOVATE (2),
    NEXT_SUPPLY (0),
    BIRTHDAY (1),
    PAY_MONEY_ALL (1);
    
    private int argAmount;
    
    private CardAction(int argAmount) {
        this.argAmount = argAmount;
    }
    
    public int getArgAmount() {
        return argAmount;
    }
    
    public void ensureArgs(int[] args) {
        if (args.length < argAmount) {
            throw new IllegalArgumentException(String.format("Given amount of arguments (%d) does not match expected amount (%d)!",
                    args.length, argAmount));
        }
    }
}
