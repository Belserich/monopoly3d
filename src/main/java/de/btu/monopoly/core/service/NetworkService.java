package de.btu.monopoly.core.service;

import com.esotericsoftware.kryo.Kryo;
import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.net.networkClasses.*;
import java.util.logging.Logger;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class NetworkService {

    private static final Logger LOGGER = Logger.getLogger(NetworkService.class.getCanonicalName());

    public static void registerKryoClasses(Kryo kryo) {
        kryo.register(BroadcastPlayerChoiceRequest.class);
        kryo.register(BroadcastRandomSeedRequest.class);
        kryo.register(ChangeUsercolorRequest.class);
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
        kryo.register(String[][].class);
        kryo.register(JoinResponse.class);
        kryo.register(AddKiRequest.class);
    }

    public static void logReceiveMessage(Object obj) {
        LOGGER.info("<- " + obj.getClass().getSimpleName());
    }

    public static void logSendMessage(Object obj) {
        LOGGER.info("-> " + obj.getClass().getSimpleName());
    }
}
