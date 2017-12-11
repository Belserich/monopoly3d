package de.btu.monopoly.core.service;

import com.esotericsoftware.kryo.Kryo;
import de.btu.monopoly.net.networkClasses.*;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class NetworkService {
    
    public static void registerKryoClasses(Kryo kryo) {
        kryo.register(BroadcastPlayerChoiceRequest.class);
        kryo.register(JoinRequest.class);
        kryo.register(JoinResponse.class);
        kryo.register(GamestartRequest.class);
        kryo.register(GamestartResponse.class);
        kryo.register(BroadcastUsersRequest.class);
        kryo.register(BroadcastUsersResponse.class);
        kryo.register(IamHostRequest.class);
        kryo.register(String[].class);
        kryo.register(int[].class);
    }
}
