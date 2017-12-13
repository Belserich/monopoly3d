package de.btu.monopoly.core.service;

import com.esotericsoftware.kryo.Kryo;
import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.net.networkClasses.*;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class NetworkService {
    
    public static void registerKryoClasses(Kryo kryo) {
        kryo.register(BroadcastPlayerChoiceRequest.class);
        kryo.register(BroadcastRandomSeedRequest.class);
        kryo.register(ChangeUsernameRequest.class);
        kryo.register(GamestartResponse.class);
        kryo.register(GamestartRequest.class);
        kryo.register(JoinImpossibleResponse.class);
        kryo.register(JoinRequest.class);
        kryo.register(PlayerTradeRequest.class);
        kryo.register(PlayerTradeResponse.class);
        kryo.register(RefreshLobbyResponse.class);
        kryo.register(String[].class);
        kryo.register(int[].class);
        kryo.register(TradeOffer.class);
        kryo.register(Trade.class);
    }
}
