package earth.terrarium.adastra.common.network;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.network.packet.PacketClearFluidTank;
import earth.terrarium.adastra.common.network.packet.PacketConstructSpaceStation;
import earth.terrarium.adastra.common.network.packet.PacketLandPlanet;
import earth.terrarium.adastra.common.network.packet.PacketLandSpaceStation;
import earth.terrarium.adastra.common.network.packet.PacketOpenRadioGui;
import earth.terrarium.adastra.common.network.packet.PacketOpenPlanetSelection;
import earth.terrarium.adastra.common.network.packet.PacketOpenVehicleGui;
import earth.terrarium.adastra.common.network.packet.PacketPlayRadioStation;
import earth.terrarium.adastra.common.network.packet.PacketRequestRadioStations;
import earth.terrarium.adastra.common.network.packet.PacketResetSideConfig;
import earth.terrarium.adastra.common.network.packet.PacketSetFlagUrl;
import earth.terrarium.adastra.common.network.packet.PacketSetFurnaceMode;
import earth.terrarium.adastra.common.network.packet.PacketSetGravityNormalizerTarget;
import earth.terrarium.adastra.common.network.packet.PacketSetRadioStation;
import earth.terrarium.adastra.common.network.packet.PacketSetRedstoneControl;
import earth.terrarium.adastra.common.network.packet.PacketSetRoverRadioStation;
import earth.terrarium.adastra.common.network.packet.PacketSetSideConfig;
import earth.terrarium.adastra.common.network.packet.PacketSyncEnvironment;
import earth.terrarium.adastra.common.network.packet.PacketSyncKeybinds;
import earth.terrarium.adastra.common.network.packet.PacketSyncLocalPlanetData;
import earth.terrarium.adastra.common.network.packet.PacketSyncMachine;
import earth.terrarium.adastra.common.network.packet.PacketSyncPlanetDefinitions;
import earth.terrarium.adastra.common.network.packet.PacketSyncPlanetData;
import earth.terrarium.adastra.common.network.packet.PacketSyncPlayerCapability;
import earth.terrarium.adastra.common.network.packet.PacketSyncRadioStations;
import earth.terrarium.adastra.common.network.packet.PacketSyncSpaceStation;
import earth.terrarium.adastra.common.network.packet.PacketSyncVehicle;
import earth.terrarium.adastra.common.network.packet.PacketVehicleControl;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class NetworkHandler {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    private NetworkHandler() {
    }

    public static void init() {
        int discriminator = 0;

        // Client-bound packets (sent to client)
        CHANNEL.registerMessage(PacketOpenPlanetSelection.Handler.class, PacketOpenPlanetSelection.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketOpenRadioGui.Handler.class, PacketOpenRadioGui.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketOpenVehicleGui.Handler.class, PacketOpenVehicleGui.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketSyncPlayerCapability.Handler.class, PacketSyncPlayerCapability.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketSyncSpaceStation.Handler.class, PacketSyncSpaceStation.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketSyncMachine.Handler.class, PacketSyncMachine.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketSyncEnvironment.Handler.class, PacketSyncEnvironment.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketSyncPlanetData.Handler.class, PacketSyncPlanetData.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketSyncVehicle.Handler.class, PacketSyncVehicle.class, discriminator++, Side.CLIENT);

        // Server-bound packets (sent to server)
        CHANNEL.registerMessage(PacketLandPlanet.Handler.class, PacketLandPlanet.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketConstructSpaceStation.Handler.class, PacketConstructSpaceStation.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketLandSpaceStation.Handler.class, PacketLandSpaceStation.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketSetRadioStation.Handler.class, PacketSetRadioStation.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketSyncKeybinds.Handler.class, PacketSyncKeybinds.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketVehicleControl.Handler.class, PacketVehicleControl.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketSetFlagUrl.Handler.class, PacketSetFlagUrl.class, discriminator++, Side.SERVER);

        // Machine configuration packets (server-bound)
        CHANNEL.registerMessage(PacketSetSideConfig.Handler.class, PacketSetSideConfig.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketResetSideConfig.Handler.class, PacketResetSideConfig.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketSetRedstoneControl.Handler.class, PacketSetRedstoneControl.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketSetFurnaceMode.Handler.class, PacketSetFurnaceMode.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketSetGravityNormalizerTarget.Handler.class, PacketSetGravityNormalizerTarget.class, discriminator++, Side.SERVER);
        CHANNEL.registerMessage(PacketClearFluidTank.Handler.class, PacketClearFluidTank.class, discriminator++, Side.SERVER);

        // Radio station list packets.
        CHANNEL.registerMessage(PacketSyncRadioStations.Handler.class, PacketSyncRadioStations.class, discriminator++, Side.CLIENT);
        CHANNEL.registerMessage(PacketRequestRadioStations.Handler.class, PacketRequestRadioStations.class, discriminator++, Side.SERVER);

        // Local player environment data packet.
        CHANNEL.registerMessage(PacketSyncLocalPlanetData.Handler.class, PacketSyncLocalPlanetData.class, discriminator++, Side.CLIENT);

        // Planet API definition sync packet.
        CHANNEL.registerMessage(PacketSyncPlanetDefinitions.Handler.class, PacketSyncPlanetDefinitions.class, discriminator++, Side.CLIENT);

        // Radio playback packet.
        CHANNEL.registerMessage(PacketPlayRadioStation.Handler.class, PacketPlayRadioStation.class, discriminator++, Side.CLIENT);

        // Rover radio packet.
        CHANNEL.registerMessage(PacketSetRoverRadioStation.Handler.class, PacketSetRoverRadioStation.class, discriminator++, Side.SERVER);
    }
}
