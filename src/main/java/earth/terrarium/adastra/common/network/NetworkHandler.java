package earth.terrarium.adastra.common.network;

import earth.terrarium.adastra.Reference;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public final class NetworkHandler {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    private NetworkHandler() {
    }

    public static void init() {
        int discriminator = 0;
        // Packet registrations will be added as gameplay systems are ported.
    }
}
