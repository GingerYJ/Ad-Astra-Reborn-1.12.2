package earth.terrarium.adastra.common.network;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.network.packet.PacketLandPlanet;
import earth.terrarium.adastra.common.network.packet.PacketOpenPlanetSelection;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class NetworkHandler {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    private NetworkHandler() {
    }

    public static void init() {
        int discriminator = 0;
        CHANNEL.registerMessage(PacketOpenPlanetSelection.Handler.class, PacketOpenPlanetSelection.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketLandPlanet.Handler.class, PacketLandPlanet.class, discriminator++, Side.SERVER);
    }
}
