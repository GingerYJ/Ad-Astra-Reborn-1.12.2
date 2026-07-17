package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.tile.CableTileEntity;
import earth.terrarium.adastra.common.tile.CoalGeneratorTileEntity;
import earth.terrarium.adastra.common.tile.CompressorTileEntity;
import earth.terrarium.adastra.common.tile.CryoFreezerTileEntity;
import earth.terrarium.adastra.common.tile.DetectorTileEntity;
import earth.terrarium.adastra.common.tile.EnergizerTileEntity;
import earth.terrarium.adastra.common.tile.EtrionicBlastFurnaceTileEntity;
import earth.terrarium.adastra.common.tile.FlagTileEntity;
import earth.terrarium.adastra.common.tile.FluidPipeTileEntity;
import earth.terrarium.adastra.common.tile.FuelRefineryTileEntity;
import earth.terrarium.adastra.common.tile.GlobeTileEntity;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import earth.terrarium.adastra.common.tile.NasaWorkbenchTileEntity;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import earth.terrarium.adastra.common.tile.OxygenLoaderTileEntity;
import earth.terrarium.adastra.common.tile.RadioTileEntity;
import earth.terrarium.adastra.common.tile.SlidingDoorTileEntity;
import earth.terrarium.adastra.common.tile.SolarPanelTileEntity;
import earth.terrarium.adastra.common.tile.WaterPumpTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModTileEntities {

    private ModTileEntities() {
    }

    public static void register() {
        register("coal_generator", CoalGeneratorTileEntity.class);
        register("compressor", CompressorTileEntity.class);
        register("etreonic_blast_furnace", EtrionicBlastFurnaceTileEntity.class);
        register("oxygen_loader", OxygenLoaderTileEntity.class);
        register("fuel_refinery", FuelRefineryTileEntity.class);
        register("water_pump", WaterPumpTileEntity.class);
        register("solar_panel", SolarPanelTileEntity.class);
        register("oxygen_distributor", OxygenDistributorTileEntity.class);
        register("gravity_normalizer", GravityNormalizerTileEntity.class);
        register("energizer", EnergizerTileEntity.class);
        register("cryo_freezer", CryoFreezerTileEntity.class);
        register("detector", DetectorTileEntity.class);
        register("nasa_workbench", NasaWorkbenchTileEntity.class);
        register("globe", GlobeTileEntity.class);
        register("flag", FlagTileEntity.class);
        register("sliding_door", SlidingDoorTileEntity.class);
        register("cable", CableTileEntity.class);
        register("fluid_pipe", FluidPipeTileEntity.class);
        register("radio", RadioTileEntity.class);
    }

    public static TileEntity createForBlock(Block block) {
        if (block == ModBlocks.COAL_GENERATOR) return new CoalGeneratorTileEntity();
        if (block == ModBlocks.COMPRESSOR) return new CompressorTileEntity();
        if (block == ModBlocks.ETRIONIC_BLAST_FURNACE) return new EtrionicBlastFurnaceTileEntity();
        if (block == ModBlocks.OXYGEN_LOADER) return new OxygenLoaderTileEntity();
        if (block == ModBlocks.FUEL_REFINERY) return new FuelRefineryTileEntity();
        if (block == ModBlocks.WATER_PUMP) return new WaterPumpTileEntity();
        if (block == ModBlocks.SOLAR_PANEL) return new SolarPanelTileEntity();
        if (block == ModBlocks.OXYGEN_DISTRIBUTOR) return new OxygenDistributorTileEntity();
        if (block == ModBlocks.GRAVITY_NORMALIZER) return new GravityNormalizerTileEntity();
        if (block == ModBlocks.ENERGIZER) return new EnergizerTileEntity();
        if (block == ModBlocks.CRYO_FREEZER) return new CryoFreezerTileEntity();
        if (block == ModBlocks.OXYGEN_SENSOR) return new DetectorTileEntity();
        if (block == ModBlocks.NASA_WORKBENCH) return new NasaWorkbenchTileEntity();
        if (block instanceof earth.terrarium.adastra.common.blocks.AdAstraGlobeBlock) return new GlobeTileEntity();
        if (block instanceof earth.terrarium.adastra.common.blocks.AdAstraFlagBlock) return new FlagTileEntity();
        if (block instanceof earth.terrarium.adastra.common.blocks.AdAstraSlidingDoorBlock) return new SlidingDoorTileEntity();
        if (block == ModBlocks.STEEL_CABLE || block == ModBlocks.DESH_CABLE || block == ModBlocks.CABLE_DUCT) return new CableTileEntity();
        if (block == ModBlocks.DESH_FLUID_PIPE || block == ModBlocks.OSTRUM_FLUID_PIPE || block == ModBlocks.FLUID_PIPE_DUCT) return new FluidPipeTileEntity();
        if (block == ModBlocks.RADIO) return new RadioTileEntity();
        return null;
    }

    private static void register(String name, Class<? extends TileEntity> tileEntityClass) {
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(Reference.MOD_ID, name));
    }
}

