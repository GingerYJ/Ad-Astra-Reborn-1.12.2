package earth.terrarium.adastra.proxy;

import earth.terrarium.adastra.client.handler.ClientEventHandler;
import earth.terrarium.adastra.client.ClientRegistry;
import earth.terrarium.adastra.client.gui.FlagUrlGui;
import earth.terrarium.adastra.client.gui.PlanetSelectionGui;
import earth.terrarium.adastra.client.gui.RadioStationGui;
import earth.terrarium.adastra.client.radio.audio.RadioHandler;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(this);
        ModParticles.register();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ClientRegistry.registerParticles();
        ClientRegistry.registerTileEntityRenderers();
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event) {
        ClientRegistry.registerModels();
    }

    @SubscribeEvent
    public void onItemColors(ColorHandlerEvent.Item event) {
        ClientRegistry.registerItemColors(event.getItemColors());
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!"textures".equals(event.getMap().getBasePath())) {
            return;
        }
        registerFluidSprites(event, ModFluids.OXYGEN);
        registerFluidSprites(event, ModFluids.HYDROGEN);
        registerFluidSprites(event, ModFluids.OIL);
        registerFluidSprites(event, ModFluids.FUEL);
        registerFluidSprites(event, ModFluids.CRYO_FUEL);
    }

    private static void registerFluidSprites(TextureStitchEvent.Pre event, Fluid fluid) {
        event.getMap().registerSprite(fluid.getStill());
        event.getMap().registerSprite(fluid.getFlowing());
    }

    @Override
    public void openPlanetSelection(int rocketTier, int rocketEntityId) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.addScheduledTask(() -> minecraft.displayGuiScreen(new PlanetSelectionGui(rocketTier, rocketEntityId)));
    }

    @Override
    public void openRadio(BlockPos pos, String station, boolean playing) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.addScheduledTask(() -> minecraft.displayGuiScreen(new RadioStationGui(pos, station, playing)));
    }

    @Override
    public void openFlagUrl(BlockPos pos) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.addScheduledTask(() -> minecraft.displayGuiScreen(new FlagUrlGui(pos)));
    }

    @Override
    public void syncRadioPlayback(BlockPos pos, String station, boolean playing) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.addScheduledTask(() -> {
            if (playing) {
                RadioHandler.play(station, pos);
            } else if (pos == null || pos.equals(RadioHandler.getSourcePos())) {
                RadioHandler.stop();
            }
        });
    }
}
