package earth.terrarium.adastra.proxy;

import earth.terrarium.adastra.common.handler.CommonEventHandler;
import earth.terrarium.adastra.common.handler.GravityEventHandler;
import earth.terrarium.adastra.common.handler.PerformanceTickHandler;
import earth.terrarium.adastra.common.handler.SpaceEnvironmentHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new GravityEventHandler());
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
        MinecraftForge.EVENT_BUS.register(new SpaceEnvironmentHandler());
        MinecraftForge.EVENT_BUS.register(new PerformanceTickHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Override
    public void openPlanetSelection(int rocketTier) {
    }

    @Override
    public void openRadio(BlockPos pos, String station, boolean playing) {
    }
}
