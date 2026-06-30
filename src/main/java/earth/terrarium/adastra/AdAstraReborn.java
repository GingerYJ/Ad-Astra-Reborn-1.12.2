package earth.terrarium.adastra;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModOreDictionary;
import earth.terrarium.adastra.common.registry.ModSmeltingRecipes;
import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class AdAstraReborn {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    @Mod.Instance(Reference.MOD_ID)
    public static AdAstraReborn instance;

    @SidedProxy(
        modId = Reference.MOD_ID,
        clientSide = "earth.terrarium.adastra.proxy.ClientProxy",
        serverSide = "earth.terrarium.adastra.proxy.CommonProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        AdAstraConfig.init(event.getSuggestedConfigurationFile());
        ModFluids.init();
        ModDimensions.register();
        NetworkHandler.init();
        ModTileEntities.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ModGuiHandler());
        proxy.preInit(event);
        LOGGER.info("{} foundation initialized for Minecraft 1.12.2.", Reference.MOD_NAME);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModOreDictionary.register();
        ModSmeltingRecipes.register();
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
