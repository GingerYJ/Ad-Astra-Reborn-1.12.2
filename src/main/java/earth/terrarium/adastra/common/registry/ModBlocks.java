package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.AdAstraAttachedMachineBlock;
import earth.terrarium.adastra.common.blocks.AdAstraAxisBlock;
import earth.terrarium.adastra.common.blocks.AdAstraBlock;
import earth.terrarium.adastra.common.blocks.AdAstraButtonBlock;
import earth.terrarium.adastra.common.blocks.AdAstraDoorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraEnergizerBlock;
import earth.terrarium.adastra.common.blocks.AdAstraFlagBlock;
import earth.terrarium.adastra.common.blocks.AdAstraFenceBlock;
import earth.terrarium.adastra.common.blocks.AdAstraFenceGateBlock;
import earth.terrarium.adastra.common.blocks.AdAstraFluidBlock;
import earth.terrarium.adastra.common.blocks.AdAstraGlobeBlock;
import earth.terrarium.adastra.common.blocks.AdAstraHugeMushroomBlock;
import earth.terrarium.adastra.common.blocks.AdAstraIndustrialLampBlock;
import earth.terrarium.adastra.common.blocks.AdAstraLadderBlock;
import earth.terrarium.adastra.common.blocks.AdAstraMachineBlock;
import earth.terrarium.adastra.common.blocks.AdAstraModelBlock;
import earth.terrarium.adastra.common.blocks.AdAstraMushroomBlock;
import earth.terrarium.adastra.common.blocks.AdAstraOreBlock;
import earth.terrarium.adastra.common.blocks.AdAstraPipeBlock;
import earth.terrarium.adastra.common.blocks.AdAstraPressurePlateBlock;
import earth.terrarium.adastra.common.blocks.AdAstraRadioBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSensorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSlabBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSlidingDoorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraStairsBlock;
import earth.terrarium.adastra.common.blocks.AdAstraTransparentBlock;
import earth.terrarium.adastra.common.blocks.AdAstraTrapDoorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraWallBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.init.Items;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModBlocks {

    private static final List<Block> INTERNAL_BLOCKS = new ArrayList<>();
    private static final List<Block> DOUBLE_SLABS = new ArrayList<>();

    public static final List<Block> BLOCKS = Collections.unmodifiableList(INTERNAL_BLOCKS);
    public static final List<Block> HIDDEN_BLOCKS = Collections.unmodifiableList(DOUBLE_SLABS);

    public static final Block LAUNCH_PAD = model("launch_pad", Material.IRON, 5.0f, 12.0f);
    public static final Block STEEL_CABLE = pipe("steel_cable", 0.5f, 12.0f);
    public static final Block DESH_CABLE = pipe("desh_cable", 0.5f, 9.0f);
    public static final Block DESH_FLUID_PIPE = pipe("desh_fluid_pipe", 0.5f, 9.0f);
    public static final Block OSTRUM_FLUID_PIPE = pipe("ostrum_fluid_pipe", 0.5f, 16.0f);
    public static final Block CABLE_DUCT = metal("cable_duct", 0.5f, 12.0f);
    public static final Block FLUID_PIPE_DUCT = metal("fluid_pipe_duct", 0.5f, 9.0f);

    public static final Block COAL_GENERATOR = machine("coal_generator", Material.IRON, 5.0f, 6.0f);
    public static final Block COMPRESSOR = machine("compressor", Material.IRON, 5.0f, 6.0f);
    public static final Block ETRIONIC_BLAST_FURNACE = machine("etrionic_blast_furnace", Material.IRON, 5.0f, 6.0f);
    public static final Block NASA_WORKBENCH = machine("nasa_workbench", Material.IRON, 5.0f, 12.0f);
    public static final Block FUEL_REFINERY = machine("fuel_refinery", Material.IRON, 5.0f, 12.0f);
    public static final Block OXYGEN_LOADER = machine("oxygen_loader", Material.IRON, 5.0f, 12.0f);
    public static final Block SOLAR_PANEL = machine("solar_panel", Material.IRON, 5.0f, 9.0f);
    public static final Block WATER_PUMP = machine("water_pump", Material.IRON, 5.0f, 9.0f);
    public static final Block OXYGEN_DISTRIBUTOR = attachedMachine("oxygen_distributor", Material.IRON, 5.0f, 9.0f);
    public static final Block GRAVITY_NORMALIZER = attachedMachine("gravity_normalizer", Material.IRON, 5.0f, 9.0f);
    public static final Block ENERGIZER = energizer("energizer", Material.IRON, 5.0f, 16.0f);
    public static final Block CRYO_FREEZER = machine("cryo_freezer", Material.IRON, 5.0f, 16.0f);
    public static final Block OXYGEN_SENSOR = sensor("oxygen_sensor", Material.IRON, 5.0f, 16.0f);

    public static final Block OXYGEN = fluid("oxygen", ModFluids.OXYGEN);
    public static final Block HYDROGEN = fluid("hydrogen", ModFluids.HYDROGEN);
    public static final Block OIL = fluid("oil", ModFluids.OIL);
    public static final Block FUEL = fluid("fuel", ModFluids.FUEL);
    public static final Block CRYO_FUEL = fluid("cryo_fuel", ModFluids.CRYO_FUEL);

    public static final Block CHEESE_BLOCK = simple("cheese_block", Material.CAKE, 0.8f, 1.0f);
    public static final Block SKY_STONE = simple("sky_stone", Material.ROCK, 1.5f, 6.0f);
    public static final Block VENT = transparentMetal("vent", 5.0f, 6.0f);
    public static final Block RADIO = radio("radio");

    public static final Block WHITE_FLAG = flag("white_flag");
    public static final Block ORANGE_FLAG = flag("orange_flag");
    public static final Block MAGENTA_FLAG = flag("magenta_flag");
    public static final Block LIGHT_BLUE_FLAG = flag("light_blue_flag");
    public static final Block YELLOW_FLAG = flag("yellow_flag");
    public static final Block LIME_FLAG = flag("lime_flag");
    public static final Block PINK_FLAG = flag("pink_flag");
    public static final Block GRAY_FLAG = flag("gray_flag");
    public static final Block LIGHT_GRAY_FLAG = flag("light_gray_flag");
    public static final Block CYAN_FLAG = flag("cyan_flag");
    public static final Block PURPLE_FLAG = flag("purple_flag");
    public static final Block BLUE_FLAG = flag("blue_flag");
    public static final Block BROWN_FLAG = flag("brown_flag");
    public static final Block GREEN_FLAG = flag("green_flag");
    public static final Block RED_FLAG = flag("red_flag");
    public static final Block BLACK_FLAG = flag("black_flag");

    public static final Block EARTH_GLOBE = globe("earth_globe");
    public static final Block MOON_GLOBE = globe("moon_globe");
    public static final Block MARS_GLOBE = globe("mars_globe");
    public static final Block MERCURY_GLOBE = globe("mercury_globe");
    public static final Block VENUS_GLOBE = globe("venus_globe");
    public static final Block GLACIO_GLOBE = globe("glacio_globe");

    public static final Block IRON_FACTORY_BLOCK = metal("iron_factory_block", 5.0f, 6.0f);
    public static final Block ENCASED_IRON_BLOCK = metal("encased_iron_block", 5.0f, 6.0f);
    public static final Block IRON_PLATEBLOCK = metal("iron_plateblock", 5.0f, 6.0f);
    public static final Block IRON_PANEL = metal("iron_panel", 5.0f, 6.0f);
    public static final Block IRON_PLATING = metal("iron_plating", 5.0f, 6.0f);
    public static final Block IRON_PLATING_STAIRS = stairs("iron_plating_stairs", IRON_PLATING);
    public static final Block IRON_PLATING_SLAB = slab("iron_plating_slab", Material.IRON, 5.0f, 6.0f, SoundType.METAL);
    public static final Block IRON_PILLAR = pillar("iron_pillar", Material.IRON, 5.0f, 6.0f);
    public static final Block GLOWING_IRON_PILLAR = glowingPillar("glowing_iron_pillar", Material.IRON, 5.0f, 6.0f);
    public static final Block MARKED_IRON_PILLAR = pillar("marked_iron_pillar", Material.IRON, 5.0f, 6.0f);
    public static final Block IRON_PLATING_BUTTON = button("iron_plating_button", false);
    public static final Block IRON_PLATING_PRESSURE_PLATE = pressurePlate("iron_plating_pressure_plate", Material.IRON, AdAstraPressurePlateBlock.Sensitivity.MOBS);

    public static final Block STEEL_FACTORY_BLOCK = metal("steel_factory_block", 5.0f, 12.0f);
    public static final Block ENCASED_STEEL_BLOCK = metal("encased_steel_block", 5.0f, 12.0f);
    public static final Block STEEL_PLATEBLOCK = metal("steel_plateblock", 5.0f, 12.0f);
    public static final Block STEEL_PANEL = metal("steel_panel", 5.0f, 12.0f);
    public static final Block STEEL_BLOCK = metal("steel_block", 5.0f, 12.0f);
    public static final Block STEEL_PLATING = metal("steel_plating", 5.0f, 12.0f);
    public static final Block STEEL_PLATING_STAIRS = stairs("steel_plating_stairs", STEEL_PLATING);
    public static final Block STEEL_PLATING_SLAB = slab("steel_plating_slab", Material.IRON, 5.0f, 12.0f, SoundType.METAL);
    public static final Block STEEL_PILLAR = pillar("steel_pillar", Material.IRON, 5.0f, 12.0f);
    public static final Block GLOWING_STEEL_PILLAR = glowingPillar("glowing_steel_pillar", Material.IRON, 5.0f, 12.0f);
    public static final Block STEEL_PLATING_BUTTON = button("steel_plating_button", false);
    public static final Block STEEL_PLATING_PRESSURE_PLATE = pressurePlate("steel_plating_pressure_plate", Material.IRON, AdAstraPressurePlateBlock.Sensitivity.MOBS);
    public static final Block IRON_SLIDING_DOOR = slidingDoor("iron_sliding_door", 5.0f, 6.0f);
    public static final Block STEEL_SLIDING_DOOR = slidingDoor("steel_sliding_door", 5.0f, 12.0f);
    public static final Block STEEL_DOOR = door("steel_door", Material.IRON);
    public static final Block STEEL_TRAPDOOR = trapDoor("steel_trapdoor", Material.IRON);
    public static final Block AIRLOCK = slidingDoor("airlock", 5.0f, 18.0f);
    public static final Block REINFORCED_DOOR = slidingDoor("reinforced_door", 25.0f, 40.0f);

    public static final Block ETRIUM_FACTORY_BLOCK = metal("etrium_factory_block", 5.0f, 5.0f);
    public static final Block ENCASED_ETRIUM_BLOCK = metal("encased_etrium_block", 5.0f, 5.0f);
    public static final Block ETRIUM_PLATEBLOCK = metal("etrium_plateblock", 5.0f, 5.0f);
    public static final Block ETRIUM_PANEL = metal("etrium_panel", 5.0f, 5.0f);
    public static final Block ETRIUM_BLOCK = metal("etrium_block", 5.0f, 5.0f);

    public static final Block DESH_FACTORY_BLOCK = metal("desh_factory_block", 5.0f, 9.0f);
    public static final Block ENCASED_DESH_BLOCK = metal("encased_desh_block", 5.0f, 9.0f);
    public static final Block DESH_PLATEBLOCK = metal("desh_plateblock", 5.0f, 9.0f);
    public static final Block DESH_PANEL = metal("desh_panel", 5.0f, 9.0f);
    public static final Block DESH_BLOCK = metal("desh_block", 5.0f, 9.0f);
    public static final Block RAW_DESH_BLOCK = metal("raw_desh_block", 5.0f, 9.0f);
    public static final Block DESH_PLATING = metal("desh_plating", 5.0f, 9.0f);
    public static final Block DESH_PLATING_STAIRS = stairs("desh_plating_stairs", DESH_PLATING);
    public static final Block DESH_PLATING_SLAB = slab("desh_plating_slab", Material.IRON, 5.0f, 9.0f, SoundType.METAL);
    public static final Block DESH_PILLAR = pillar("desh_pillar", Material.IRON, 5.0f, 9.0f);
    public static final Block GLOWING_DESH_PILLAR = glowingPillar("glowing_desh_pillar", Material.IRON, 5.0f, 9.0f);
    public static final Block DESH_PLATING_BUTTON = button("desh_plating_button", false);
    public static final Block DESH_PLATING_PRESSURE_PLATE = pressurePlate("desh_plating_pressure_plate", Material.IRON, AdAstraPressurePlateBlock.Sensitivity.MOBS);
    public static final Block DESH_SLIDING_DOOR = slidingDoor("desh_sliding_door", 5.0f, 9.0f);

    public static final Block OSTRUM_FACTORY_BLOCK = metal("ostrum_factory_block", 5.0f, 16.0f);
    public static final Block ENCASED_OSTRUM_BLOCK = metal("encased_ostrum_block", 5.0f, 16.0f);
    public static final Block OSTRUM_PLATEBLOCK = metal("ostrum_plateblock", 5.0f, 16.0f);
    public static final Block OSTRUM_PANEL = metal("ostrum_panel", 5.0f, 16.0f);
    public static final Block OSTRUM_BLOCK = metal("ostrum_block", 5.0f, 16.0f);
    public static final Block RAW_OSTRUM_BLOCK = metal("raw_ostrum_block", 5.0f, 16.0f);
    public static final Block OSTRUM_PLATING = metal("ostrum_plating", 5.0f, 16.0f);
    public static final Block OSTRUM_PLATING_STAIRS = stairs("ostrum_plating_stairs", OSTRUM_PLATING);
    public static final Block OSTRUM_PLATING_SLAB = slab("ostrum_plating_slab", Material.IRON, 5.0f, 16.0f, SoundType.METAL);
    public static final Block OSTRUM_PILLAR = pillar("ostrum_pillar", Material.IRON, 5.0f, 16.0f);
    public static final Block GLOWING_OSTRUM_PILLAR = glowingPillar("glowing_ostrum_pillar", Material.IRON, 5.0f, 16.0f);
    public static final Block OSTRUM_PLATING_BUTTON = button("ostrum_plating_button", false);
    public static final Block OSTRUM_PLATING_PRESSURE_PLATE = pressurePlate("ostrum_plating_pressure_plate", Material.IRON, AdAstraPressurePlateBlock.Sensitivity.MOBS);
    public static final Block OSTRUM_SLIDING_DOOR = slidingDoor("ostrum_sliding_door", 5.0f, 16.0f);

    public static final Block CALORITE_FACTORY_BLOCK = metal("calorite_factory_block", 7.0f, 22.0f);
    public static final Block ENCASED_CALORITE_BLOCK = metal("encased_calorite_block", 7.0f, 22.0f);
    public static final Block CALORITE_PLATEBLOCK = metal("calorite_plateblock", 7.0f, 22.0f);
    public static final Block CALORITE_PANEL = metal("calorite_panel", 7.0f, 22.0f);
    public static final Block CALORITE_BLOCK = metal("calorite_block", 7.0f, 22.0f);
    public static final Block RAW_CALORITE_BLOCK = metal("raw_calorite_block", 7.0f, 22.0f);
    public static final Block CALORITE_PLATING = metal("calorite_plating", 7.0f, 22.0f);
    public static final Block CALORITE_PLATING_STAIRS = stairs("calorite_plating_stairs", CALORITE_PLATING);
    public static final Block CALORITE_PLATING_SLAB = slab("calorite_plating_slab", Material.IRON, 7.0f, 22.0f, SoundType.METAL);
    public static final Block CALORITE_PILLAR = pillar("calorite_pillar", Material.IRON, 7.0f, 22.0f);
    public static final Block GLOWING_CALORITE_PILLAR = glowingPillar("glowing_calorite_pillar", Material.IRON, 7.0f, 22.0f);
    public static final Block CALORITE_PLATING_BUTTON = button("calorite_plating_button", false);
    public static final Block CALORITE_PLATING_PRESSURE_PLATE = pressurePlate("calorite_plating_pressure_plate", Material.IRON, AdAstraPressurePlateBlock.Sensitivity.MOBS);
    public static final Block CALORITE_SLIDING_DOOR = slidingDoor("calorite_sliding_door", 7.0f, 22.0f);

    public static final Block BLACK_INDUSTRIAL_LAMP = industrialLamp("black_industrial_lamp", false);
    public static final Block BLUE_INDUSTRIAL_LAMP = industrialLamp("blue_industrial_lamp", false);
    public static final Block BROWN_INDUSTRIAL_LAMP = industrialLamp("brown_industrial_lamp", false);
    public static final Block CYAN_INDUSTRIAL_LAMP = industrialLamp("cyan_industrial_lamp", false);
    public static final Block GRAY_INDUSTRIAL_LAMP = industrialLamp("gray_industrial_lamp", false);
    public static final Block GREEN_INDUSTRIAL_LAMP = industrialLamp("green_industrial_lamp", false);
    public static final Block LIGHT_BLUE_INDUSTRIAL_LAMP = industrialLamp("light_blue_industrial_lamp", false);
    public static final Block LIGHT_GRAY_INDUSTRIAL_LAMP = industrialLamp("light_gray_industrial_lamp", false);
    public static final Block LIME_INDUSTRIAL_LAMP = industrialLamp("lime_industrial_lamp", false);
    public static final Block MAGENTA_INDUSTRIAL_LAMP = industrialLamp("magenta_industrial_lamp", false);
    public static final Block ORANGE_INDUSTRIAL_LAMP = industrialLamp("orange_industrial_lamp", false);
    public static final Block PINK_INDUSTRIAL_LAMP = industrialLamp("pink_industrial_lamp", false);
    public static final Block PURPLE_INDUSTRIAL_LAMP = industrialLamp("purple_industrial_lamp", false);
    public static final Block RED_INDUSTRIAL_LAMP = industrialLamp("red_industrial_lamp", false);
    public static final Block WHITE_INDUSTRIAL_LAMP = industrialLamp("white_industrial_lamp", false);
    public static final Block YELLOW_INDUSTRIAL_LAMP = industrialLamp("yellow_industrial_lamp", false);

    public static final Block SMALL_BLACK_INDUSTRIAL_LAMP = industrialLamp("small_black_industrial_lamp", true);
    public static final Block SMALL_BLUE_INDUSTRIAL_LAMP = industrialLamp("small_blue_industrial_lamp", true);
    public static final Block SMALL_BROWN_INDUSTRIAL_LAMP = industrialLamp("small_brown_industrial_lamp", true);
    public static final Block SMALL_CYAN_INDUSTRIAL_LAMP = industrialLamp("small_cyan_industrial_lamp", true);
    public static final Block SMALL_GRAY_INDUSTRIAL_LAMP = industrialLamp("small_gray_industrial_lamp", true);
    public static final Block SMALL_GREEN_INDUSTRIAL_LAMP = industrialLamp("small_green_industrial_lamp", true);
    public static final Block SMALL_LIGHT_BLUE_INDUSTRIAL_LAMP = industrialLamp("small_light_blue_industrial_lamp", true);
    public static final Block SMALL_LIGHT_GRAY_INDUSTRIAL_LAMP = industrialLamp("small_light_gray_industrial_lamp", true);
    public static final Block SMALL_LIME_INDUSTRIAL_LAMP = industrialLamp("small_lime_industrial_lamp", true);
    public static final Block SMALL_MAGENTA_INDUSTRIAL_LAMP = industrialLamp("small_magenta_industrial_lamp", true);
    public static final Block SMALL_ORANGE_INDUSTRIAL_LAMP = industrialLamp("small_orange_industrial_lamp", true);
    public static final Block SMALL_PINK_INDUSTRIAL_LAMP = industrialLamp("small_pink_industrial_lamp", true);
    public static final Block SMALL_PURPLE_INDUSTRIAL_LAMP = industrialLamp("small_purple_industrial_lamp", true);
    public static final Block SMALL_RED_INDUSTRIAL_LAMP = industrialLamp("small_red_industrial_lamp", true);
    public static final Block SMALL_WHITE_INDUSTRIAL_LAMP = industrialLamp("small_white_industrial_lamp", true);
    public static final Block SMALL_YELLOW_INDUSTRIAL_LAMP = industrialLamp("small_yellow_industrial_lamp", true);

    public static final Block MOON_SAND = sand("moon_sand");
    public static final Block MOON_STONE = stone("moon_stone");
    public static final Block MOON_DEEPSLATE = stone("moon_deepslate");
    public static final Block MOON_STONE_STAIRS = stairs("moon_stone_stairs", MOON_STONE);
    public static final Block MOON_STONE_SLAB = stoneSlab("moon_stone_slab");
    public static final Block MOON_COBBLESTONE = stone("moon_cobblestone");
    public static final Block MOON_COBBLESTONE_STAIRS = stairs("moon_cobblestone_stairs", MOON_COBBLESTONE);
    public static final Block MOON_COBBLESTONE_SLAB = stoneSlab("moon_cobblestone_slab");
    public static final Block MOON_STONE_BRICKS = stone("moon_stone_bricks");
    public static final Block MOON_STONE_BRICK_STAIRS = stairs("moon_stone_brick_stairs", MOON_STONE_BRICKS);
    public static final Block MOON_STONE_BRICK_SLAB = stoneSlab("moon_stone_brick_slab");
    public static final Block CRACKED_MOON_STONE_BRICKS = stone("cracked_moon_stone_bricks");
    public static final Block CHISELED_MOON_STONE_BRICKS = stone("chiseled_moon_stone_bricks");
    public static final Block CHISELED_MOON_STONE_STAIRS = stairs("chiseled_moon_stone_stairs", CHISELED_MOON_STONE_BRICKS);
    public static final Block CHISELED_MOON_STONE_SLAB = stoneSlab("chiseled_moon_stone_slab");
    public static final Block POLISHED_MOON_STONE = stone("polished_moon_stone");
    public static final Block POLISHED_MOON_STONE_STAIRS = stairs("polished_moon_stone_stairs", POLISHED_MOON_STONE);
    public static final Block POLISHED_MOON_STONE_SLAB = stoneSlab("polished_moon_stone_slab");
    public static final Block MOON_PILLAR = pillar("moon_pillar", Material.ROCK, 1.5f, 6.0f);
    public static final Block MOON_STONE_BRICK_WALL = wall("moon_stone_brick_wall", MOON_STONE_BRICKS);
    public static final Block MOON_CHEESE_ORE = ore("moon_cheese_ore", ModItems.CHEESE, 1, 1, 0, 2);
    public static final Block MOON_DESH_ORE = ore("moon_desh_ore", ModItems.RAW_DESH, 1, 1, 0, 0);
    public static final Block DEEPSLATE_DESH_ORE = ore("deepslate_desh_ore", ModItems.RAW_DESH, 1, 1, 0, 0);
    public static final Block MOON_IRON_ORE = ore("moon_iron_ore");
    public static final Block MOON_ICE_SHARD_ORE = ore("moon_ice_shard_ore", ModItems.ICE_SHARD, 1, 1, 2, 5);
    public static final Block DEEPSLATE_ICE_SHARD_ORE = ore("deepslate_ice_shard_ore", ModItems.ICE_SHARD, 1, 1, 3, 6);

    public static final Block MARS_SAND = sand("mars_sand");
    public static final Block MARS_STONE = stone("mars_stone");
    public static final Block MARS_STONE_STAIRS = stairs("mars_stone_stairs", MARS_STONE);
    public static final Block MARS_STONE_SLAB = stoneSlab("mars_stone_slab");
    public static final Block MARS_COBBLESTONE = stone("mars_cobblestone");
    public static final Block MARS_COBBLESTONE_STAIRS = stairs("mars_cobblestone_stairs", MARS_COBBLESTONE);
    public static final Block MARS_COBBLESTONE_SLAB = stoneSlab("mars_cobblestone_slab");
    public static final Block MARS_STONE_BRICKS = stone("mars_stone_bricks");
    public static final Block MARS_STONE_BRICK_STAIRS = stairs("mars_stone_brick_stairs", MARS_STONE_BRICKS);
    public static final Block MARS_STONE_BRICK_SLAB = stoneSlab("mars_stone_brick_slab");
    public static final Block CRACKED_MARS_STONE_BRICKS = stone("cracked_mars_stone_bricks");
    public static final Block CHISELED_MARS_STONE_BRICKS = stone("chiseled_mars_stone_bricks");
    public static final Block CHISELED_MARS_STONE_STAIRS = stairs("chiseled_mars_stone_stairs", CHISELED_MARS_STONE_BRICKS);
    public static final Block CHISELED_MARS_STONE_SLAB = stoneSlab("chiseled_mars_stone_slab");
    public static final Block POLISHED_MARS_STONE = stone("polished_mars_stone");
    public static final Block POLISHED_MARS_STONE_STAIRS = stairs("polished_mars_stone_stairs", POLISHED_MARS_STONE);
    public static final Block POLISHED_MARS_STONE_SLAB = stoneSlab("polished_mars_stone_slab");
    public static final Block MARS_PILLAR = pillar("mars_pillar", Material.ROCK, 1.5f, 6.0f);
    public static final Block MARS_STONE_BRICK_WALL = wall("mars_stone_brick_wall", MARS_STONE_BRICKS);
    public static final Block CONGLOMERATE = stone("conglomerate");
    public static final Block POLISHED_CONGLOMERATE = stone("polished_conglomerate");
    public static final Block MARS_IRON_ORE = ore("mars_iron_ore");
    public static final Block MARS_DIAMOND_ORE = ore("mars_diamond_ore", Items.DIAMOND, 1, 1, 3, 7);
    public static final Block MARS_OSTRUM_ORE = ore("mars_ostrum_ore", ModItems.RAW_OSTRUM, 1, 1, 0, 0);
    public static final Block DEEPSLATE_OSTRUM_ORE = ore("deepslate_ostrum_ore", ModItems.RAW_OSTRUM, 1, 1, 0, 0);
    public static final Block MARS_ICE_SHARD_ORE = ore("mars_ice_shard_ore", ModItems.ICE_SHARD, 1, 1, 2, 5);

    public static final Block MERCURY_STONE = stone("mercury_stone");
    public static final Block MERCURY_STONE_STAIRS = stairs("mercury_stone_stairs", MERCURY_STONE);
    public static final Block MERCURY_STONE_SLAB = stoneSlab("mercury_stone_slab");
    public static final Block MERCURY_COBBLESTONE = stone("mercury_cobblestone");
    public static final Block MERCURY_COBBLESTONE_STAIRS = stairs("mercury_cobblestone_stairs", MERCURY_COBBLESTONE);
    public static final Block MERCURY_COBBLESTONE_SLAB = stoneSlab("mercury_cobblestone_slab");
    public static final Block MERCURY_STONE_BRICKS = stone("mercury_stone_bricks");
    public static final Block MERCURY_STONE_BRICK_STAIRS = stairs("mercury_stone_brick_stairs", MERCURY_STONE_BRICKS);
    public static final Block MERCURY_STONE_BRICK_SLAB = stoneSlab("mercury_stone_brick_slab");
    public static final Block CRACKED_MERCURY_STONE_BRICKS = stone("cracked_mercury_stone_bricks");
    public static final Block CHISELED_MERCURY_STONE_BRICKS = stone("chiseled_mercury_stone_bricks");
    public static final Block CHISELED_MERCURY_STONE_STAIRS = stairs("chiseled_mercury_stone_stairs", CHISELED_MERCURY_STONE_BRICKS);
    public static final Block CHISELED_MERCURY_STONE_SLAB = stoneSlab("chiseled_mercury_stone_slab");
    public static final Block POLISHED_MERCURY_STONE = stone("polished_mercury_stone");
    public static final Block POLISHED_MERCURY_STONE_STAIRS = stairs("polished_mercury_stone_stairs", POLISHED_MERCURY_STONE);
    public static final Block POLISHED_MERCURY_STONE_SLAB = stoneSlab("polished_mercury_stone_slab");
    public static final Block MERCURY_PILLAR = pillar("mercury_pillar", Material.ROCK, 1.5f, 6.0f);
    public static final Block MERCURY_STONE_BRICK_WALL = wall("mercury_stone_brick_wall", MERCURY_STONE_BRICKS);
    public static final Block MERCURY_IRON_ORE = ore("mercury_iron_ore");

    public static final Block VENUS_SAND = sand("venus_sand");
    public static final Block VENUS_STONE = stone("venus_stone");
    public static final Block VENUS_STONE_STAIRS = stairs("venus_stone_stairs", VENUS_STONE);
    public static final Block VENUS_STONE_SLAB = stoneSlab("venus_stone_slab");
    public static final Block VENUS_COBBLESTONE = stone("venus_cobblestone");
    public static final Block VENUS_COBBLESTONE_STAIRS = stairs("venus_cobblestone_stairs", VENUS_COBBLESTONE);
    public static final Block VENUS_COBBLESTONE_SLAB = stoneSlab("venus_cobblestone_slab");
    public static final Block VENUS_STONE_BRICKS = stone("venus_stone_bricks");
    public static final Block VENUS_STONE_BRICK_STAIRS = stairs("venus_stone_brick_stairs", VENUS_STONE_BRICKS);
    public static final Block VENUS_STONE_BRICK_SLAB = stoneSlab("venus_stone_brick_slab");
    public static final Block CRACKED_VENUS_STONE_BRICKS = stone("cracked_venus_stone_bricks");
    public static final Block CHISELED_VENUS_STONE_BRICKS = stone("chiseled_venus_stone_bricks");
    public static final Block CHISELED_VENUS_STONE_STAIRS = stairs("chiseled_venus_stone_stairs", CHISELED_VENUS_STONE_BRICKS);
    public static final Block CHISELED_VENUS_STONE_SLAB = stoneSlab("chiseled_venus_stone_slab");
    public static final Block POLISHED_VENUS_STONE = stone("polished_venus_stone");
    public static final Block POLISHED_VENUS_STONE_STAIRS = stairs("polished_venus_stone_stairs", POLISHED_VENUS_STONE);
    public static final Block POLISHED_VENUS_STONE_SLAB = stoneSlab("polished_venus_stone_slab");
    public static final Block VENUS_PILLAR = pillar("venus_pillar", Material.ROCK, 1.5f, 6.0f);
    public static final Block VENUS_STONE_BRICK_WALL = wall("venus_stone_brick_wall", VENUS_STONE_BRICKS);
    public static final Block VENUS_SANDSTONE = stone("venus_sandstone");
    public static final Block VENUS_SANDSTONE_BRICKS = stone("venus_sandstone_bricks");
    public static final Block VENUS_SANDSTONE_BRICK_STAIRS = stairs("venus_sandstone_brick_stairs", VENUS_SANDSTONE_BRICKS);
    public static final Block VENUS_SANDSTONE_BRICK_SLAB = stoneSlab("venus_sandstone_brick_slab");
    public static final Block CRACKED_VENUS_SANDSTONE_BRICKS = stone("cracked_venus_sandstone_bricks");
    public static final Block INFERNAL_SPIRE_BLOCK = stone("infernal_spire_block");
    public static final Block VENUS_COAL_ORE = ore("venus_coal_ore", Items.COAL, 1, 1, 0, 2);
    public static final Block VENUS_GOLD_ORE = ore("venus_gold_ore");
    public static final Block VENUS_DIAMOND_ORE = ore("venus_diamond_ore", Items.DIAMOND, 1, 1, 3, 7);
    public static final Block VENUS_CALORITE_ORE = ore("venus_calorite_ore", ModItems.RAW_CALORITE, 1, 1, 0, 0);
    public static final Block DEEPSLATE_CALORITE_ORE = ore("deepslate_calorite_ore", ModItems.RAW_CALORITE, 1, 1, 0, 0);

    public static final Block GLACIO_STONE = stone("glacio_stone");
    public static final Block GLACIO_STONE_STAIRS = stairs("glacio_stone_stairs", GLACIO_STONE);
    public static final Block GLACIO_STONE_SLAB = stoneSlab("glacio_stone_slab");
    public static final Block GLACIO_COBBLESTONE = stone("glacio_cobblestone");
    public static final Block GLACIO_COBBLESTONE_STAIRS = stairs("glacio_cobblestone_stairs", GLACIO_COBBLESTONE);
    public static final Block GLACIO_COBBLESTONE_SLAB = stoneSlab("glacio_cobblestone_slab");
    public static final Block GLACIO_STONE_BRICKS = stone("glacio_stone_bricks");
    public static final Block GLACIO_STONE_BRICK_STAIRS = stairs("glacio_stone_brick_stairs", GLACIO_STONE_BRICKS);
    public static final Block GLACIO_STONE_BRICK_SLAB = stoneSlab("glacio_stone_brick_slab");
    public static final Block CRACKED_GLACIO_STONE_BRICKS = stone("cracked_glacio_stone_bricks");
    public static final Block CHISELED_GLACIO_STONE_BRICKS = stone("chiseled_glacio_stone_bricks");
    public static final Block CHISELED_GLACIO_STONE_STAIRS = stairs("chiseled_glacio_stone_stairs", CHISELED_GLACIO_STONE_BRICKS);
    public static final Block CHISELED_GLACIO_STONE_SLAB = stoneSlab("chiseled_glacio_stone_slab");
    public static final Block POLISHED_GLACIO_STONE = stone("polished_glacio_stone");
    public static final Block POLISHED_GLACIO_STONE_STAIRS = stairs("polished_glacio_stone_stairs", POLISHED_GLACIO_STONE);
    public static final Block POLISHED_GLACIO_STONE_SLAB = stoneSlab("polished_glacio_stone_slab");
    public static final Block GLACIO_PILLAR = pillar("glacio_pillar", Material.ROCK, 1.5f, 6.0f);
    public static final Block GLACIO_STONE_BRICK_WALL = wall("glacio_stone_brick_wall", GLACIO_STONE_BRICKS);
    public static final Block PERMAFROST = stone("permafrost");
    public static final Block PERMAFROST_BRICKS = stone("permafrost_bricks");
    public static final Block PERMAFROST_BRICK_STAIRS = stairs("permafrost_brick_stairs", PERMAFROST_BRICKS);
    public static final Block PERMAFROST_BRICK_SLAB = stoneSlab("permafrost_brick_slab");
    public static final Block CRACKED_PERMAFROST_BRICKS = stone("cracked_permafrost_bricks");
    public static final Block PERMAFROST_TILES = stone("permafrost_tiles");
    public static final Block CHISELED_PERMAFROST_BRICKS = stone("chiseled_permafrost_bricks");
    public static final Block CHISELED_PERMAFROST_BRICK_STAIRS = stairs("chiseled_permafrost_brick_stairs", CHISELED_PERMAFROST_BRICKS);
    public static final Block CHISELED_PERMAFROST_BRICK_SLAB = stoneSlab("chiseled_permafrost_brick_slab");
    public static final Block POLISHED_PERMAFROST = stone("polished_permafrost");
    public static final Block POLISHED_PERMAFROST_STAIRS = stairs("polished_permafrost_stairs", POLISHED_PERMAFROST);
    public static final Block POLISHED_PERMAFROST_SLAB = stoneSlab("polished_permafrost_slab");
    public static final Block PERMAFROST_PILLAR = pillar("permafrost_pillar", Material.ROCK, 1.5f, 6.0f);
    public static final Block PERMAFROST_BRICK_WALL = wall("permafrost_brick_wall", PERMAFROST_BRICKS);
    public static final Block GLACIO_ICE_SHARD_ORE = ore("glacio_ice_shard_ore", ModItems.ICE_SHARD, 1, 1, 2, 5);
    public static final Block GLACIO_COAL_ORE = ore("glacio_coal_ore", Items.COAL, 1, 1, 0, 2);
    public static final Block GLACIO_COPPER_ORE = ore("glacio_copper_ore");
    public static final Block GLACIO_IRON_ORE = ore("glacio_iron_ore");
    public static final Block GLACIO_LAPIS_ORE = ore("glacio_lapis_ore", Items.DYE, EnumDyeColor.BLUE.getDyeDamage(), 4, 9, 2, 5);

    public static final Block AERONOS_CAP = wood("aeronos_cap");
    public static final Block AERONOS_STEM = hugeMushroom("aeronos_stem");
    public static final Block AERONOS_MUSHROOM = mushroom("aeronos_mushroom");
    public static final Block AERONOS_PLANKS = wood("aeronos_planks");
    public static final Block AERONOS_STAIRS = stairs("aeronos_stairs", AERONOS_PLANKS);
    public static final Block AERONOS_SLAB = slab("aeronos_slab", Material.WOOD, 2.0f, 3.0f, SoundType.WOOD);
    public static final Block AERONOS_FENCE = fence("aeronos_fence");
    public static final Block AERONOS_FENCE_GATE = fenceGate("aeronos_fence_gate");
    public static final Block AERONOS_LADDER = ladder("aeronos_ladder");
    public static final Block AERONOS_DOOR = door("aeronos_door", Material.WOOD);
    public static final Block AERONOS_TRAPDOOR = trapDoor("aeronos_trapdoor", Material.WOOD);
    public static final Block STROPHAR_CAP = wood("strophar_cap");
    public static final Block STROPHAR_STEM = hugeMushroom("strophar_stem");
    public static final Block STROPHAR_MUSHROOM = mushroom("strophar_mushroom");
    public static final Block STROPHAR_PLANKS = wood("strophar_planks");
    public static final Block STROPHAR_STAIRS = stairs("strophar_stairs", STROPHAR_PLANKS);
    public static final Block STROPHAR_SLAB = slab("strophar_slab", Material.WOOD, 2.0f, 3.0f, SoundType.WOOD);
    public static final Block STROPHAR_FENCE = fence("strophar_fence");
    public static final Block STROPHAR_FENCE_GATE = fenceGate("strophar_fence_gate");
    public static final Block STROPHAR_LADDER = ladder("strophar_ladder");
    public static final Block STROPHAR_DOOR = door("strophar_door", Material.WOOD);
    public static final Block STROPHAR_TRAPDOOR = trapDoor("strophar_trapdoor", Material.WOOD);
    public static final Block GLACIAN_LOG = pillar("glacian_log", Material.WOOD, 2.0f, 3.0f);
    public static final Block STRIPPED_GLACIAN_LOG = pillar("stripped_glacian_log", Material.WOOD, 2.0f, 3.0f);
    public static final Block GLACIAN_LEAVES = simple("glacian_leaves", Material.LEAVES, 0.2f, 1.0f);
    public static final Block GLACIAN_PLANKS = wood("glacian_planks");
    public static final Block GLACIAN_STAIRS = stairs("glacian_stairs", GLACIAN_PLANKS);
    public static final Block GLACIAN_SLAB = slab("glacian_slab", Material.WOOD, 2.0f, 3.0f, SoundType.WOOD);
    public static final Block GLACIAN_DOOR = door("glacian_door", Material.WOOD);
    public static final Block GLACIAN_TRAPDOOR = trapDoor("glacian_trapdoor", Material.WOOD);
    public static final Block GLACIAN_FENCE = fence("glacian_fence");
    public static final Block GLACIAN_FENCE_GATE = fenceGate("glacian_fence_gate");
    public static final Block GLACIAN_BUTTON = button("glacian_button", true);
    public static final Block GLACIAN_PRESSURE_PLATE = pressurePlate("glacian_pressure_plate", Material.WOOD, AdAstraPressurePlateBlock.Sensitivity.EVERYTHING);
    public static final Block GLACIAN_FUR = simple("glacian_fur", Material.CLOTH, 0.8f, 1.0f);

    private ModBlocks() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(INTERNAL_BLOCKS.toArray(new Block[0]));
        event.getRegistry().registerAll(DOUBLE_SLABS.toArray(new Block[0]));
    }

    public static Item createItemBlock(Block block) {
        Item item;
        if (block instanceof AdAstraDoorBlock) {
            item = new ItemDoor(block);
        } else if (block instanceof AdAstraSlabBlock.Single) {
            BlockSlab doubleSlab = findDoubleSlab(block);
            item = new ItemSlab(block, (BlockSlab) block, doubleSlab);
        } else {
            item = new ItemBlock(block);
        }
        item.setRegistryName(block.getRegistryName());
        if (block.getRegistryName() != null) {
            item.setTranslationKey(Reference.MOD_ID + "." + block.getRegistryName().getPath());
        }
        return item;
    }

    public static boolean isHiddenFromItemModels(Block block) {
        return DOUBLE_SLABS.contains(block) || block instanceof AdAstraFluidBlock;
    }

    private static Block simple(String name, Material material, float hardness, float resistance) {
        Block block = new AdAstraBlock(material, hardness, resistance);
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        INTERNAL_BLOCKS.add(block);
        return block;
    }

    private static Block metal(String name, float hardness, float resistance) {
        Block block = simple(name, Material.IRON, hardness, resistance);
        block.setHarvestLevel("pickaxe", 1);
        return block;
    }

    private static Block transparentMetal(String name, float hardness, float resistance) {
        Block block = new AdAstraTransparentBlock(Material.IRON, hardness, resistance);
        block.setHarvestLevel("pickaxe", 1);
        return register(name, block);
    }

    private static Block model(String name, Material material, float hardness, float resistance) {
        Block block = new AdAstraModelBlock(material, hardness, resistance);
        if (material == Material.IRON) {
            block.setHarvestLevel("pickaxe", 1);
        }
        return register(name, block);
    }

    private static Block machine(String name, Material material, float hardness, float resistance) {
        return register(name, new AdAstraMachineBlock(material, hardness, resistance));
    }

    private static Block pipe(String name, float hardness, float resistance) {
        return register(name, new AdAstraPipeBlock(hardness, resistance));
    }

    private static Block fluid(String name, net.minecraftforge.fluids.Fluid fluid) {
        return register(name, new AdAstraFluidBlock(fluid));
    }

    private static Block attachedMachine(String name, Material material, float hardness, float resistance) {
        return register(name, new AdAstraAttachedMachineBlock(material, hardness, resistance));
    }

    private static Block energizer(String name, Material material, float hardness, float resistance) {
        return register(name, new AdAstraEnergizerBlock(material, hardness, resistance));
    }

    private static Block sensor(String name, Material material, float hardness, float resistance) {
        return register(name, new AdAstraSensorBlock(material, hardness, resistance));
    }

    private static Block stone(String name) {
        Block block = simple(name, Material.ROCK, 1.5f, 6.0f);
        block.setHarvestLevel("pickaxe", 1);
        return block;
    }

    private static Block ore(String name) {
        Block block = simple(name, Material.ROCK, 3.0f, 5.0f);
        block.setHarvestLevel("pickaxe", 2);
        return block;
    }

    private static Block ore(String name, Item droppedItem, int minDrops, int maxDrops, int minExperience, int maxExperience) {
        return register(name, new AdAstraOreBlock(droppedItem, minDrops, maxDrops, minExperience, maxExperience));
    }

    private static Block ore(String name, Item droppedItem, int droppedMeta, int minDrops, int maxDrops, int minExperience, int maxExperience) {
        return register(name, new AdAstraOreBlock(droppedItem, droppedMeta, minDrops, maxDrops, minExperience, maxExperience));
    }

    private static Block sand(String name) {
        return simple(name, Material.SAND, 0.5f, 0.5f);
    }

    private static Block wood(String name) {
        return simple(name, Material.WOOD, 2.0f, 3.0f);
    }

    private static Block hugeMushroom(String name) {
        return register(name, new AdAstraHugeMushroomBlock());
    }

    private static Block mushroom(String name) {
        return register(name, new AdAstraMushroomBlock());
    }

    private static Block radio(String name) {
        return register(name, new AdAstraRadioBlock());
    }

    private static Block flag(String name) {
        return register(name, new AdAstraFlagBlock());
    }

    private static Block globe(String name) {
        return register(name, new AdAstraGlobeBlock());
    }

    private static Block stairs(String name, Block baseBlock) {
        Block block = new AdAstraStairsBlock(baseBlock.getDefaultState());
        return register(name, block);
    }

    private static Block wall(String name, Block baseBlock) {
        return register(name, new AdAstraWallBlock(baseBlock));
    }

    private static Block button(String name, boolean wooden) {
        return register(name, new AdAstraButtonBlock(wooden));
    }

    private static Block pressurePlate(String name, Material material, AdAstraPressurePlateBlock.Sensitivity sensitivity) {
        return register(name, new AdAstraPressurePlateBlock(material, sensitivity));
    }

    private static Block fence(String name) {
        return register(name, new AdAstraFenceBlock(Material.WOOD, MapColor.WOOD));
    }

    private static Block fenceGate(String name) {
        return register(name, new AdAstraFenceGateBlock());
    }

    private static Block ladder(String name) {
        return register(name, new AdAstraLadderBlock());
    }

    private static Block door(String name, Material material) {
        return register(name, new AdAstraDoorBlock(material));
    }

    private static Block trapDoor(String name, Material material) {
        return register(name, new AdAstraTrapDoorBlock(material));
    }

    private static Block slidingDoor(String name, float hardness, float resistance) {
        return register(name, new AdAstraSlidingDoorBlock(hardness, resistance));
    }

    private static Block industrialLamp(String name, boolean small) {
        return register(name, new AdAstraIndustrialLampBlock(small, small ? 8 : 12));
    }

    private static Block stoneSlab(String name) {
        return slab(name, Material.ROCK, 1.5f, 6.0f, SoundType.STONE);
    }

    private static Block slab(String name, Material material, float hardness, float resistance, SoundType soundType) {
        AdAstraSlabBlock.Single single = new AdAstraSlabBlock.Single(material, hardness, resistance, soundType);
        AdAstraSlabBlock.Double doubleSlab = new AdAstraSlabBlock.Double(material, single, hardness, resistance, soundType);
        register(name, single);
        registerHidden("double_" + name, doubleSlab);
        return single;
    }

    private static Block pillar(String name, Material material, float hardness, float resistance) {
        Block block = new AdAstraAxisBlock(material, hardness, resistance);
        if (material == Material.ROCK) {
            block.setHarvestLevel("pickaxe", 1);
        } else if (material == Material.WOOD) {
            block.setHarvestLevel("axe", 0);
        } else if (material == Material.IRON) {
            block.setHarvestLevel("pickaxe", 1);
        }
        return register(name, block);
    }

    private static Block glowingPillar(String name, Material material, float hardness, float resistance) {
        Block block = pillar(name, material, hardness, resistance);
        block.setLightLevel(1.0f);
        return block;
    }

    private static Block register(String name, Block block) {
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        INTERNAL_BLOCKS.add(block);
        return block;
    }

    private static Block registerHidden(String name, Block block) {
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        DOUBLE_SLABS.add(block);
        return block;
    }

    private static BlockSlab findDoubleSlab(Block singleSlab) {
        String name = singleSlab.getRegistryName().getPath();
        for (Block block : DOUBLE_SLABS) {
            if (block.getRegistryName() != null && block.getRegistryName().getPath().equals("double_" + name)) {
                return (BlockSlab) block;
            }
        }
        throw new IllegalStateException("Missing double slab for " + name);
    }
}
