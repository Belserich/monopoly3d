package de.btu.monopoly.core.service;

import com.esotericsoftware.kryo.Kryo;
import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.net.networkClasses.*;
import de.btu.monopoly.net.networkClasses.Lobby.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class NetworkService {

    private static final Logger LOGGER = Logger.getLogger(NetworkService.class.getCanonicalName());

    public static void registerKryoClasses(Kryo kryo) {
    
        kryo.register(String[].class);
        kryo.register(String[][].class);
        kryo.register(int[].class);
        kryo.register(int[][].class);
        
        kryo.register(ChangeUsercolorRequest.class);
        kryo.register(ChangeUsernameRequest.class);
        kryo.register(AddKiRequest.class);
        kryo.register(RefreshLobbyResponse.class);
        kryo.register(DeleteUserRequest.class);
        kryo.register(BroadcastRandomSeedRequest.class);
    
        kryo.register(JoinRequest.class);
        kryo.register(JoinImpossibleResponse.class);
        kryo.register(JoinResponse.class);
        kryo.register(GamestartRequest.class);
        kryo.register(GamestartResponse.class);
        
        kryo.register(PlayerTradeRequest.class);
        kryo.register(PlayerTradeResponse.class);
    
        kryo.register(BroadcastPlayerChoiceRequest.class);
    
        kryo.register(Trade.class);
        kryo.register(TradeOffer.class);
        
        kryo.register(JoinAuctionRequest.class);
        kryo.register(BidRequest.class);
        kryo.register(BroadcastAuctionResponse.class);
        kryo.register(ExitAuctionRequest.class);
    }

    public static void logServerReceiveMessage(Object obj) {
        LOGGER.log(Level.INFO, "{Server} <- {0}", obj.getClass().getSimpleName());
    }

    public static void logServerSendMessage(Object obj) {
        LOGGER.log(Level.INFO, "{Server} -> {0}", obj.getClass().getSimpleName());
    }

    public static void logClientReceiveMessage(Object obj, String name) {
        LOGGER.log(Level.INFO, "{Client} <- {0}({1})", new Object[]{obj.getClass().getSimpleName(), name});
    }

    public static void logClientSendMessage(Object obj, String name) {
        LOGGER.log(Level.INFO, "{Client} -> {0}({1})", new Object[]{obj.getClass().getSimpleName(), name});
    }
}
