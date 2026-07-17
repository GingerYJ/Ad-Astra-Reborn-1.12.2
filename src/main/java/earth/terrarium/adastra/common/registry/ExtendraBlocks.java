package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.AdAstraBlock;
import earth.terrarium.adastra.common.blocks.AdAstraAxisBlock;
import earth.terrarium.adastra.common.blocks.AdAstraButtonBlock;
import earth.terrarium.adastra.common.blocks.AdAstraDoorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraOreBlock;
import earth.terrarium.adastra.common.blocks.AdAstraFluidBlock;
import earth.terrarium.adastra.common.blocks.AdAstraFenceBlock;
import earth.terrarium.adastra.common.blocks.AdAstraFenceGateBlock;
import earth.terrarium.adastra.common.blocks.AdAstraGlobeBlock;
import earth.terrarium.adastra.common.blocks.AdAstraPressurePlateBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSlabBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSlidingDoorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraStairsBlock;
import earth.terrarium.adastra.common.blocks.AdAstraTrapDoorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraWallBlock;
import earth.terrarium.adastra.common.blocks.ExtendraFlowerPotBlock;
import earth.terrarium.adastra.common.blocks.ExtendraIcicleBlock;
import earth.terrarium.adastra.common.blocks.ExtendraMoonMyceliumBlock;
import earth.terrarium.adastra.common.blocks.ExtendraSaplingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemSlab;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The content ported from Ad Extendra.
 *
 * <p>The 1.20 project has many specialised block classes.  The 1.12.2 port
 * keeps the complete registry names and gameplay data while using the stable
 * Ad Astra block wrappers already used by this project.</p>
 */
public final class ExtendraBlocks {

    private static final List<Block> INTERNAL_BLOCKS = new ArrayList<>();
    private static final Map<String, Block> BY_NAME = new LinkedHashMap<>();
    private static final Map<String, Block> PLANET_STONE = new LinkedHashMap<>();
    private static final Map<String, Block> PLANET_SURFACE = new LinkedHashMap<>();
    private static final Map<String, Block> ORES = new LinkedHashMap<>();
    private static final Map<String, Block> MATERIAL_BLOCKS = new LinkedHashMap<>();
    private static final List<Block> DOUBLE_SLABS = new ArrayList<>();

    public static final List<Block> BLOCKS = Collections.unmodifiableList(INTERNAL_BLOCKS);
    public static final List<Block> HIDDEN_BLOCKS = Collections.unmodifiableList(DOUBLE_SLABS);
    public static final List<String> PLANETS = Collections.unmodifiableList(java.util.Arrays.asList(
        "ceres", "jupiter", "saturn", "uranus", "neptune", "orcus", "pluto",
        "haumea", "quaoar", "makemake", "gonggong", "eris", "sedna", "b"));

    public static final Block ICICLE;
    public static final Block SLUSHY_ICE;
    public static final Block PACKED_SLUSHY_ICE;
    public static final Block BLUE_SLUSHY_ICE;
    public static final Block SATURN_ICE;
    public static final Block MOON_MYCELIUM;
    public static final Block AERONOS_BUTTON;
    public static final Block AERONOS_PRESSURE_PLATE;
    public static final Block STROPHAR_BUTTON;
    public static final Block STROPHAR_PRESSURE_PLATE;
    public static final Block AERONOS_POTTED_MUSHROOM;
    public static final Block STROPHAR_POTTED_MUSHROOM;
    public static Block CENTAURIAN_OAK_POTTED_SAPLING;
    public static Block GLACIAN_POTTED_SAPLING;

    public static final Block VICINUS_AURORITE_ORE;
    public static final Block VICINUS_GLOBE;

    static {
        for (String planet : PLANETS) {
            registerPlanetBlocks(planet);
        }

        registerOre("ceres_copper_ore", ModItems.COPPER_INGOT);
        registerOre("ceres_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("jupiter_juperium_ore", ExtendraItems.RAW_JUPERIUM);
        registerOre("jupiter_coal_ore", net.minecraft.init.Items.COAL);
        registerOre("jupiter_diamond_ore", net.minecraft.init.Items.DIAMOND);
        registerOre("jupiter_gold_ore", net.minecraft.init.Items.GOLD_INGOT);
        registerOre("saturn_saturlyte_ore", ExtendraItems.RAW_SATURLYTE);
        registerOre("saturn_coal_ore", net.minecraft.init.Items.COAL);
        registerOre("saturn_diamond_ore", net.minecraft.init.Items.DIAMOND);
        registerOre("saturn_gold_ore", net.minecraft.init.Items.GOLD_INGOT);
        registerOre("uranus_uranium_ore", ExtendraItems.RAW_URANIUM);
        registerOre("uranus_diamond_ore", net.minecraft.init.Items.DIAMOND);
        registerOre("uranus_ice_shard_ore", ExtendraItems.FREEZE_SHARD);
        registerOre("uranus_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("uranus_lapis_ore", net.minecraft.init.Items.DYE, 4);
        registerOre("neptune_neptunium_ore", ExtendraItems.RAW_NEPTUNIUM);
        registerOre("neptune_coal_ore", net.minecraft.init.Items.COAL);
        registerOre("neptune_copper_ore", ModItems.COPPER_INGOT);
        registerOre("neptune_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("neptune_ice_shard_ore", ExtendraItems.FREEZE_SHARD);
        registerOre("orcus_radium_ore", ExtendraItems.RAW_RADIUM);
        registerOre("orcus_copper_ore", ModItems.COPPER_INGOT);
        registerOre("orcus_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("pluto_plutonium_ore", ExtendraItems.RAW_PLUTONIUM);
        registerOre("pluto_diamond_ore", net.minecraft.init.Items.DIAMOND);
        registerOre("pluto_gold_ore", net.minecraft.init.Items.GOLD_INGOT);
        registerOre("pluto_ice_shard_ore", ExtendraItems.FREEZE_SHARD);
        registerOre("haumea_copper_ore", ModItems.COPPER_INGOT);
        registerOre("haumea_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("quaoar_copper_ore", ModItems.COPPER_INGOT);
        registerOre("quaoar_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("makemake_copper_ore", ModItems.COPPER_INGOT);
        registerOre("makemake_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("gonggong_copper_ore", ModItems.COPPER_INGOT);
        registerOre("gonggong_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("eris_copper_ore", ModItems.COPPER_INGOT);
        registerOre("eris_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("sedna_electrolyte_ore", ExtendraItems.RAW_ELECTROLYTE);
        registerOre("sedna_copper_ore", ModItems.COPPER_INGOT);
        registerOre("sedna_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("b_diamond_ore", net.minecraft.init.Items.DIAMOND);
        registerOre("b_emerald_ore", net.minecraft.init.Items.EMERALD);
        registerOre("b_iron_ore", net.minecraft.init.Items.IRON_INGOT);
        registerOre("b_redstone_ore", net.minecraft.init.Items.REDSTONE);

        for (String material : new String[] {
            "juperium", "saturlyte", "uranium", "neptunium", "radium", "plutonium", "electrolyte", "aurorite"
        }) {
            registerMaterialBlocks(material);
        }

        VICINUS_AURORITE_ORE = registerOre("vicinus_aurorite_ore", ExtendraItems.RAW_AURORITE);
        registerVicinusBlocks();

        ICICLE = register("icicle", new ExtendraIcicleBlock());
        SLUSHY_ICE = registerSimple("slushy_ice", Material.ICE, 0.5F, 0.5F);
        PACKED_SLUSHY_ICE = registerSimple("packed_slushy_ice", Material.ICE, 0.8F, 0.8F);
        BLUE_SLUSHY_ICE = registerSimple("blue_slushy_ice", Material.ICE, 0.5F, 0.5F);
        SATURN_ICE = registerSimple("saturn_ice", Material.ICE, 0.5F, 0.5F);
        MOON_MYCELIUM = register("moon_mycelium", new ExtendraMoonMyceliumBlock());
        AERONOS_BUTTON = register("aeronos_button", new AdAstraButtonBlock(true));
        AERONOS_PRESSURE_PLATE = register("aeronos_pressure_plate",
            new AdAstraPressurePlateBlock(Material.WOOD, AdAstraPressurePlateBlock.Sensitivity.EVERYTHING));
        STROPHAR_BUTTON = register("strophar_button", new AdAstraButtonBlock(true));
        STROPHAR_PRESSURE_PLATE = register("strophar_pressure_plate",
            new AdAstraPressurePlateBlock(Material.WOOD, AdAstraPressurePlateBlock.Sensitivity.EVERYTHING));

        AERONOS_POTTED_MUSHROOM = register("potted_aeronos_mushroom",
            new ExtendraFlowerPotBlock(ModBlocks.AERONOS_MUSHROOM));
        STROPHAR_POTTED_MUSHROOM = register("potted_strophar_mushroom",
            new ExtendraFlowerPotBlock(ModBlocks.STROPHAR_MUSHROOM));

        registerWoodBlocks();
        for (String planet : PLANETS) {
            registerGlobe(planet + "_globe");
        }
        VICINUS_GLOBE = registerGlobe("vicinus_globe");
    }

    private ExtendraBlocks() {
    }

    public static Block get(String name) {
        return BY_NAME.get(name);
    }

    public static Block getPlanetStone(String planet) {
        return PLANET_STONE.get(planet);
    }

    public static Block getPlanetSurface(String planet) {
        return PLANET_SURFACE.get(planet);
    }

    public static Block getOre(String name) {
        return ORES.get(name);
    }

    public static Block getMaterialBlock(String name) {
        return MATERIAL_BLOCKS.get(name);
    }

    public static void register(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(INTERNAL_BLOCKS.toArray(new Block[0]));
        event.getRegistry().registerAll(DOUBLE_SLABS.toArray(new Block[0]));
    }

    public static List<Item> createItemBlocks() {
        List<Item> items = new ArrayList<>();
        for (Block block : INTERNAL_BLOCKS) {
            if (block instanceof AdAstraFluidBlock) {
                continue;
            }
            // Flower-pot blocks are placed through the pot block and do not have
            // a standalone BlockItem in the source project.
            if (block.getRegistryName() != null
                && block.getRegistryName().getPath().startsWith("potted_")) {
                continue;
            }
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
            item.setTranslationKey(Reference.MOD_ID + "." + block.getRegistryName().getPath());
            item.setCreativeTab(earth.terrarium.adastra.common.AdAstraCreativeTab.INSTANCE);
            items.add(item);
        }
        return items;
    }

    private static void registerPlanetBlocks(String planet) {
        Material material = Material.ROCK;
        String[] variants = {
            "stone", "stone_stairs", "stone_slab", "cobblestone", "cobblestone_stairs", "cobblestone_slab",
            "stone_bricks", "stone_brick_stairs", "stone_brick_slab", "stone_brick_wall", "pillar"
        };
        for (String variant : variants) {
            String name = planet + "_" + variant;
            if ("stone_brick_wall".equals(variant)) {
                registerWall(name, BY_NAME.get(planet + "_stone_bricks"));
            } else if (variant.endsWith("_stairs")) {
                String baseVariant = variant.substring(0, variant.length() - "_stairs".length());
                if ("stone_brick".equals(baseVariant)) {
                    baseVariant = "stone_bricks";
                }
                registerStairs(name, BY_NAME.get(planet + "_" + baseVariant));
            } else if (variant.endsWith("_slab")) {
                registerSlab(name, material, 1.5F, 6.0F, SoundType.STONE);
            } else if ("pillar".equals(variant)) {
                registerAxis(name, material, 1.5F, 6.0F);
            } else {
                registerSimple(name, material, 1.5F, 6.0F);
            }
        }
        // These names follow the attached project's registry IDs.  The material
        // prefix is part of the source texture/model name, not the planet prefix.
        registerSimple("cracked_" + planet + "_stone_bricks", material, 1.5F, 6.0F);
        Block chiseledStoneBricks = registerSimple("chiseled_" + planet + "_stone_bricks", material, 1.5F, 6.0F);
        registerStairs("chiseled_" + planet + "_stone_stairs", chiseledStoneBricks);
        registerSlab("chiseled_" + planet + "_stone_slab", material, 1.5F, 6.0F, SoundType.STONE);
        Block polishedStone = registerSimple("polished_" + planet + "_stone", material, 1.5F, 6.0F);
        registerStairs("polished_" + planet + "_stone_stairs", polishedStone);
        registerSlab("polished_" + planet + "_stone_slab", material, 1.5F, 6.0F, SoundType.STONE);
        Block stone = BY_NAME.get(planet + "_stone");
        PLANET_STONE.put(planet, stone);

        if ("ceres".equals(planet) || "jupiter".equals(planet) || "saturn".equals(planet)
            || "pluto".equals(planet) || "b".equals(planet)) {
            Block sand = registerSimple(planet + "_sand", Material.SAND, 0.5F, 0.5F);
            PLANET_SURFACE.put(planet, sand);
        } else {
            PLANET_SURFACE.put(planet, stone);
        }
        if ("b".equals(planet)) {
            registerSimple("b_sandstone", Material.ROCK, 0.8F, 3.0F);
            Block bSandstoneBricks = registerSimple("b_sandstone_bricks", Material.ROCK, 1.5F, 6.0F);
            registerStairs("b_sandstone_brick_stairs", bSandstoneBricks);
            registerSlab("b_sandstone_brick_slab", Material.ROCK, 1.5F, 6.0F, SoundType.STONE);
            registerSimple("cracked_b_sandstone_bricks", Material.ROCK, 1.5F, 6.0F);
        }
    }

    private static void registerMaterialBlocks(String material) {
        String[] variants = {
            "_block", "_factory_block", "_plateblock", "_panel", "_plating",
            "_plating_stairs", "_plating_slab", "_pillar", "_plating_button", "_plating_pressure_plate", "_sliding_door"
        };
        for (String variant : variants) {
            Block block;
            if ("_sliding_door".equals(variant)) {
                block = registerSlidingDoor(material + variant, 5.0F, 12.0F);
            } else if ("_plating_button".equals(variant)) {
                block = register(material + variant, new AdAstraButtonBlock(false));
            } else if ("_plating_pressure_plate".equals(variant)) {
                block = register(material + variant,
                    new AdAstraPressurePlateBlock(Material.IRON, AdAstraPressurePlateBlock.Sensitivity.MOBS));
            } else if ("_plating_stairs".equals(variant)) {
                block = registerStairs(material + variant, BY_NAME.get(material + "_plating"));
            } else if ("_plating_slab".equals(variant)) {
                block = registerSlab(material + variant, Material.IRON, 5.0F, 12.0F, SoundType.METAL);
            } else if ("_pillar".equals(variant)) {
                block = registerAxis(material + variant, Material.IRON, 5.0F, 12.0F);
            } else {
                block = registerSimple(material + variant, Material.IRON, 5.0F, 12.0F);
            }
            if ("_block".equals(variant)) {
                MATERIAL_BLOCKS.put(material, block);
            }
        }
        registerSimple("raw_" + material + "_block", Material.IRON, 5.0F, 12.0F);
        registerSimple("encased_" + material + "_block", Material.IRON, 5.0F, 12.0F);
        registerAxis("glowing_" + material + "_pillar", Material.IRON, 5.0F, 12.0F).setLightLevel(1.0F);
    }

    private static void registerWoodBlocks() {
        String[] variants = {
            "centaurian_oak_log", "centaurian_oak_wood", "stripped_centaurian_oak_log", "stripped_centaurian_oak_wood",
            "centaurian_oak_leaves", "centaurian_oak_planks", "centaurian_oak_stairs", "centaurian_oak_slab",
            "centaurian_oak_fence", "centaurian_oak_fence_gate", "centaurian_oak_door", "centaurian_oak_trapdoor",
            "centaurian_oak_button", "centaurian_oak_pressure_plate", "centaurian_oak_sapling", "potted_centaurian_oak_sapling",
            "glacian_wood", "stripped_glacian_wood", "glacian_sapling", "potted_glacian_sapling"
        };
        for (String variant : variants) {
            if ("centaurian_oak_log".equals(variant)
                || "centaurian_oak_wood".equals(variant)
                || "stripped_centaurian_oak_log".equals(variant)
                || "stripped_centaurian_oak_wood".equals(variant)) {
                registerAxis(variant, Material.WOOD, 2.0F, 3.0F);
            } else if ("centaurian_oak_stairs".equals(variant)) {
                registerStairs(variant, BY_NAME.get("centaurian_oak_planks"));
            } else if ("centaurian_oak_slab".equals(variant)) {
                registerSlab(variant, Material.WOOD, 2.0F, 3.0F, SoundType.WOOD);
            } else if ("centaurian_oak_fence".equals(variant)) {
                registerFence(variant);
            } else if ("centaurian_oak_fence_gate".equals(variant)) {
                registerFenceGate(variant);
            } else if ("centaurian_oak_door".equals(variant)) {
                registerDoor(variant, Material.WOOD);
            } else if ("centaurian_oak_trapdoor".equals(variant)) {
                registerTrapDoor(variant, Material.WOOD);
            } else if ("centaurian_oak_button".equals(variant)) {
                register(variant, new AdAstraButtonBlock(true));
            } else if ("centaurian_oak_pressure_plate".equals(variant)) {
                register(variant, new AdAstraPressurePlateBlock(Material.WOOD,
                    AdAstraPressurePlateBlock.Sensitivity.EVERYTHING));
            } else if ("centaurian_oak_sapling".equals(variant)) {
                register(variant, new ExtendraSaplingBlock(false));
            } else if ("potted_centaurian_oak_sapling".equals(variant)) {
                CENTAURIAN_OAK_POTTED_SAPLING = register(variant,
                    new ExtendraFlowerPotBlock(BY_NAME.get("centaurian_oak_sapling")));
            } else if ("glacian_sapling".equals(variant)) {
                register(variant, new ExtendraSaplingBlock(true));
            } else if ("potted_glacian_sapling".equals(variant)) {
                GLACIAN_POTTED_SAPLING = register(variant,
                    new ExtendraFlowerPotBlock(BY_NAME.get("glacian_sapling")));
            } else if ("centaurian_oak_button".equals(variant)) {
                register(variant, new AdAstraButtonBlock(true));
            } else if ("centaurian_oak_pressure_plate".equals(variant)) {
                register(variant, new AdAstraPressurePlateBlock(Material.WOOD,
                    AdAstraPressurePlateBlock.Sensitivity.EVERYTHING));
            } else {
                registerSimple(variant, Material.WOOD, 2.0F, 3.0F);
            }
        }
    }

    private static void registerVicinusBlocks() {
        String[] variants = {
            "stone", "stone_stairs", "stone_slab", "cobblestone", "cobblestone_stairs", "cobblestone_slab",
            "stone_bricks", "stone_brick_stairs", "stone_brick_slab", "stone_brick_wall",
            "cracked_stone_bricks", "chiseled_stone_bricks", "chiseled_stone_stairs", "chiseled_stone_slab",
            "polished_stone", "polished_stone_stairs", "polished_stone_slab", "pillar"
        };
        for (String variant : variants) {
            String name = variant.startsWith("cracked_") || variant.startsWith("chiseled_")
                || variant.startsWith("polished_")
                ? variant.replace("_stone", "_vicinus_stone")
                : "vicinus_" + variant;
            if ("stone_brick_wall".equals(variant)) {
                registerWall(name, BY_NAME.get("vicinus_stone_bricks"));
            } else if (variant.endsWith("_stairs")) {
                String baseName;
                if (variant.startsWith("chiseled_")) {
                    baseName = "chiseled_vicinus_stone_bricks";
                } else if (variant.startsWith("polished_")) {
                    baseName = "polished_vicinus_stone";
                } else {
                    String baseVariant = variant.substring(0, variant.length() - "_stairs".length());
                    if ("stone_brick".equals(baseVariant)) {
                        baseVariant = "stone_bricks";
                    }
                    baseName = "vicinus_" + baseVariant;
                }
                registerStairs(name, BY_NAME.get(baseName));
            } else if (variant.endsWith("_slab")) {
                registerSlab(name, Material.ROCK, 1.5F, 6.0F, SoundType.STONE);
            } else if ("pillar".equals(variant)) {
                registerAxis(name, Material.ROCK, 1.5F, 6.0F);
            } else {
                registerSimple(name, Material.ROCK, 1.5F, 6.0F);
            }
        }
    }

    private static Block registerOre(String name, Item droppedItem) {
        return registerOre(name, droppedItem, 0);
    }

    private static Block registerOre(String name, Item droppedItem, int droppedMeta) {
        Block block = new AdAstraOreBlock(droppedItem, droppedMeta, 1, 1, droppedItem == null ? 0 : 2, droppedItem == null ? 0 : 5);
        block.setHarvestLevel("pickaxe", 2);
        register(name, block);
        ORES.put(name, block);
        return block;
    }

    private static Block registerSimple(String name, Material material, float hardness, float resistance) {
        return register(name, new AdAstraBlock(material, hardness, resistance));
    }

    private static Block registerStairs(String name, Block modelBlock) {
        if (modelBlock == null) {
            throw new IllegalStateException("Missing stairs model block for " + name);
        }
        return register(name, new AdAstraStairsBlock(modelBlock.getDefaultState()));
    }

    private static Block registerSlab(String name, Material material, float hardness, float resistance,
                                      SoundType soundType) {
        AdAstraSlabBlock.Single single = new AdAstraSlabBlock.Single(material, hardness, resistance, soundType);
        AdAstraSlabBlock.Double doubleSlab = new AdAstraSlabBlock.Double(
            material, single, hardness, resistance, soundType);
        register(name, single);
        registerHidden("double_" + name, doubleSlab);
        return single;
    }

    private static Block registerAxis(String name, Material material, float hardness, float resistance) {
        return register(name, new AdAstraAxisBlock(material, hardness, resistance));
    }

    private static Block registerFence(String name) {
        return register(name, new AdAstraFenceBlock(Material.WOOD, MapColor.WOOD));
    }

    private static Block registerFenceGate(String name) {
        return register(name, new AdAstraFenceGateBlock());
    }

    private static Block registerDoor(String name, Material material) {
        return register(name, new AdAstraDoorBlock(material));
    }

    private static Block registerTrapDoor(String name, Material material) {
        return register(name, new AdAstraTrapDoorBlock(material));
    }

    private static Block registerWall(String name, Block modelBlock) {
        return register(name, new AdAstraWallBlock(modelBlock));
    }

    private static Block registerHidden(String name, Block block) {
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        DOUBLE_SLABS.add(block);
        return block;
    }

    private static Block registerGlobe(String name) {
        return register(name, new AdAstraGlobeBlock());
    }

    private static Block registerSlidingDoor(String name, float hardness, float resistance) {
        return register(name, new AdAstraSlidingDoorBlock(hardness, resistance));
    }

    private static Block register(String name, Block block) {
        block.setRegistryName(Reference.MOD_ID, name);
        block.setTranslationKey(Reference.MOD_ID + "." + name);
        INTERNAL_BLOCKS.add(block);
        BY_NAME.put(name, block);
        return block;
    }

    private static BlockSlab findDoubleSlab(Block singleSlab) {
        String name = singleSlab.getRegistryName().getPath();
        for (Block block : DOUBLE_SLABS) {
            if (block.getRegistryName() != null
                && ("double_" + name).equals(block.getRegistryName().getPath())) {
                return (BlockSlab) block;
            }
        }
        throw new IllegalStateException("Missing double slab for " + name);
    }
}
