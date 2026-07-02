package earth.terrarium.adastra;

import earth.terrarium.adastra.common.capability.AdAstraCapabilities;
import earth.terrarium.adastra.common.capability.AdAstraCapabilityHandler;
import earth.terrarium.adastra.common.commands.AdAstraCommand;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.recipe.CompressingRecipes;
import earth.terrarium.adastra.common.recipe.CryoFreezingRecipes;
import earth.terrarium.adastra.common.recipe.RecipeLoader;
import earth.terrarium.adastra.common.registry.ModDimensions;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModOreDictionary;
import earth.terrarium.adastra.common.registry.ModSmeltingRecipes;
import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.common.world.AdAstraStructureWorldGenerator;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDimensionRegistrar;
import earth.terrarium.adastra.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:patchouli")
public class AdAstraReborn {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    static {
        FluidRegistry.enableUniversalBucket();
    }

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
        AdAstraCapabilities.register();
        MinecraftForge.EVENT_BUS.register(AdAstraCapabilities.class);
        MinecraftForge.EVENT_BUS.register(new AdAstraCapabilityHandler());
        // 注意：玩家氧�?温度/供氧的权威实现在 CommonEventHandler（由 CommonProxy 注册），
        // 它通过 EnvironmentUtils 处理氧气检测、太空服供氧、窒息与温度伤害�?        // systems.OxygenTickHandler + OxygenSystem 是平行的旧实现，若也注册会与 CommonEventHandler
        // 双重订阅 LivingUpdateEvent，导致玩家在无氧环境被双重扣�?双重伤害，故不再注册�?        ModFluids.init();
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

        // Load all machine recipes from JSON
        RecipeLoader.loadAllRecipes();

        // Legacy recipe registration (for backwards compatibility)
        CompressingRecipes.register();
        CryoFreezingRecipes.register();

        ModSmeltingRecipes.register();
        GameRegistry.registerWorldGenerator(new AdAstraStructureWorldGenerator(), 0);
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        CustomPlanetDimensionRegistrar.registerQueuedDimensions();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new AdAstraCommand());
        LOGGER.info("Ad Astra commands registered.");
    }
}
