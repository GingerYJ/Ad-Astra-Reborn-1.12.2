package earth.terrarium.adastra.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.util.math.BlockPos;

public interface IProxy {

    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);

    void openPlanetSelection(int rocketTier, int rocketEntityId);

    void openRadio(BlockPos pos, String station, boolean playing);

    void openFlagUrl(BlockPos pos);

    void syncRadioPlayback(BlockPos pos, String station, boolean playing);
}
