package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardField extends Field {

    private CardQueue cards;

    /**
     *
     * @param name Name des Kartenfeldes
     */
    public CardField(int id, String name) {
        super(id, name);
    }

}
