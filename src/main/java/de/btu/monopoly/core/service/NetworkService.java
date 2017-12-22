package de.btu.monopoly.core.service;

import de.btu.monopoly.net.networkClasses.Lobby.ChangeUsercolorRequest;
import com.esotericsoftware.kryo.Kryo;
import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.net.networkClasses.*;
import de.btu.monopoly.net.networkClasses.Lobby.AddKiRequest;
import de.btu.monopoly.net.networkClasses.Lobby.BroadcastRandomSeedRequest;
import de.btu.monopoly.net.networkClasses.Lobby.ChangeUsernameRequest;
import de.btu.monopoly.net.networkClasses.Lobby.GamestartRequest;
import de.btu.monopoly.net.networkClasses.Lobby.GamestartResponse;
import de.btu.monopoly.net.networkClasses.Lobby.JoinImpossibleResponse;
import de.btu.monopoly.net.networkClasses.Lobby.JoinRequest;
import de.btu.monopoly.net.networkClasses.Lobby.JoinResponse;
import de.btu.monopoly.net.networkClasses.Lobby.RefreshLobbyResponse;
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
        kryo.register(BroadcastAuctionResponse.class);
        kryo.register(BidRequest.class);
        kryo.register(ExitAuctionRequest.class);
        kryo.register(JoinAuctionRequest.class);
        kryo.register(int[][].class);
    }

    public static void logServerReceiveMessage(Object obj) {
        LOGGER.info("S <- " + obj.getClass().getSimpleName());
    }

    public static void logServerSendMessage(Object obj) {
        LOGGER.info("S -> " + obj.getClass().getSimpleName());
    }

    public static void logClientReceiveMessage(Object obj, String name) {
        LOGGER.info("C <- " + obj.getClass().getSimpleName() + "(" + name + ")");
    }

    public static void logClientSendMessage(Object obj, String name) {
        LOGGER.info("C -> " + obj.getClass().getSimpleName() + "(" + name + ")");
    }
}
