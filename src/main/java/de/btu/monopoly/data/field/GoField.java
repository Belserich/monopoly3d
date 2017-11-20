package de.btu.monopoly.data.field;

import de.btu.monopoly.data.field.Field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GoField extends Field {

    /**
     * Geldbetrag des LOS Feldes
     */
    private final int amount;

    /**
     * Erzeugt eine Instanz des LOS-Feldes. Bewegt sich ein Spieler während des Spiels über dieses Feld,
     * bekommt dieser {@code amount} Einheiten der gewählten Währung auf sein Konto überschrieben.
     */
    public GoField(int id, String name, int amount) {
        super(id, name);
        
        this.amount = amount;
    }

    /**
     * @return Der Betrag, der dem Spieler beim passieren des Feldes gutgeschrieben werden soll.
     */
    public int getAmount() {
        return amount;
    }

}
