package de.btu.monopoly.data.field;

import de.btu.monopoly.data.CardQueue;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardField extends Field {

    private CardQueue cards;

    /**
     *
     * @param name Name des Kartenfeldes
     */
    public CardField(String name) {
        super(name);
    }

}
