package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdAstraStructureWorldGenerator implements IWorldGenerator {

    private static final ResourceLocation OIL_WELL_TEMPLATE = new ResourceLocation(Reference.MOD_ID, "oil");
    private static final int OIL_WELL_SPACING = 20;
    private static final int OIL_WELL_SEPARATION = 14;
    private static final int OIL_WELL_SALT = 1922601463;
    private static final ResourceLocation[] METEOR_TEMPLATES = new ResourceLocation[]{
        new ResourceLocation(Reference.MOD_ID, "meteor1"),
        new ResourceLocation(Reference.MOD_ID, "meteor2"),
        new ResourceLocation(Reference.MOD_ID, "meteor3"),
        new ResourceLocation(Reference.MOD_ID, "meteor4"),
        new ResourceLocation(Reference.MOD_ID, "meteor5"),
        new ResourceLocation(Reference.MOD_ID, "meteor6")
    };
    private static final int METEOR_SPACING = 48;
    private static final int METEOR_SEPARATION = 32;
    private static final int METEOR_SALT = 734154559;
    private static final int METEOR_ORIGIN_Y = AdAstraChunkGenerator.SURFACE_Y - 12;
    private static final String LEGACY_METEOR_LOOT_TABLE = "minecraft:loot";
    private static final String METEOR_LOOT_TABLE = Reference.MOD_ID + ":chests/meteor";
    private static final ITemplateProcessor SKIP_AIR_PROCESSOR = new ITemplateProcessor() {
        @Override
        public Template.BlockInfo processBlock(World world, BlockPos pos, Template.BlockInfo blockInfo) {
            return blockInfo.blockState.getBlock() == Blocks.AIR ? null : blockInfo;
        }
    };

    private final Map<ResourceLocation, Template> templateCache = new HashMap<ResourceLocation, Template>();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
                         IChunkProvider chunkProvider) {
        if (!(world instanceof WorldServer)) {
            return;
        }

        WorldServer serverWorld = (WorldServer) world;
        if (world.provider.getDimension() == 0) {
            generateOilWell(serverWorld, chunkX, chunkZ);
        } else if (world.provider instanceof AdAstraWorldProvider) {
            generateMeteor(serverWorld, chunkX, chunkZ);
        }
    }

    private void generateOilWell(WorldServer world, int chunkX, int chunkZ) {
        ChunkPos candidate = getOilWellCandidate(world.getSeed(), chunkX, chunkZ);
        if (candidate.x != chunkX || candidate.z != chunkZ) {
            return;
        }

        BlockPos center = new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);
        if (!isOilWellBiome(world, center)) {
            return;
        }

        Template template = getTemplate(world, OIL_WELL_TEMPLATE);
        if (template == null) {
            return;
        }

        BlockPos size = template.getSize();
        BlockPos surface = world.getHeight(center);
        BlockPos origin = new BlockPos(
            surface.getX() - size.getX() / 2,
            Math.max(1, surface.getY() - 10),
            surface.getZ() - size.getZ() / 2
        );

        if (!canPlaceOilWell(world, origin, size)) {
            return;
        }

        PlacementSettings settings = new PlacementSettings()
            .setMirror(Mirror.NONE)
            .setRotation(Rotation.NONE)
            .setIgnoreEntities(false)
            .setIgnoreStructureBlock(true)
            .setReplacedBlock(Blocks.STRUCTURE_VOID)
            .setChunk(new ChunkPos(chunkX, chunkZ))
            .setBoundingBox(new StructureBoundingBox(chunkX * 16, 1, chunkZ * 16, chunkX * 16 + 15, world.getHeight(), chunkZ * 16 + 15));
        template.addBlocksToWorld(world, origin, settings, 2);
    }

    private void generateMeteor(WorldServer world, int chunkX, int chunkZ) {
        int regionX = floorDiv(chunkX, METEOR_SPACING);
        int regionZ = floorDiv(chunkZ, METEOR_SPACING);
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                generateMeteorSlice(world, chunkX, chunkZ, regionX + offsetX, regionZ + offsetZ);
            }
        }
    }

    private void generateMeteorSlice(WorldServer world, int chunkX, int chunkZ, int regionX, int regionZ) {
        Random random = getMeteorRandom(world.getSeed(), world.provider.getDimension(), regionX, regionZ);
        int range = METEOR_SPACING - METEOR_SEPARATION;
        int meteorChunkX = regionX * METEOR_SPACING + random.nextInt(range);
        int meteorChunkZ = regionZ * METEOR_SPACING + random.nextInt(range);
        ResourceLocation location = METEOR_TEMPLATES[random.nextInt(METEOR_TEMPLATES.length)];
        Template template = getTemplate(world, location);
        if (template == null) {
            return;
        }

        BlockPos size = template.getSize();
        int centerX = meteorChunkX * 16 + 8;
        int centerZ = meteorChunkZ * 16 + 8;
        BlockPos origin = new BlockPos(centerX - size.getX() / 2, METEOR_ORIGIN_Y, centerZ - size.getZ() / 2);
        StructureBoundingBox structureBox = new StructureBoundingBox(
            origin.getX(),
            origin.getY(),
            origin.getZ(),
            origin.getX() + size.getX() - 1,
            origin.getY() + size.getY() - 1,
            origin.getZ() + size.getZ() - 1);
        StructureBoundingBox chunkBox = getChunkBoundingBox(world, chunkX, chunkZ);
        if (!structureBox.intersectsWith(chunkBox)) {
            return;
        }

        PlacementSettings settings = new PlacementSettings()
            .setMirror(Mirror.NONE)
            .setRotation(Rotation.NONE)
            .setIgnoreEntities(false)
            .setIgnoreStructureBlock(true)
            .setReplacedBlock(Blocks.STRUCTURE_VOID)
            .setChunk(new ChunkPos(chunkX, chunkZ))
            .setBoundingBox(chunkBox);
        template.addBlocksToWorld(world, origin, SKIP_AIR_PROCESSOR, settings, 2);
    }

    private Template getTemplate(WorldServer world, ResourceLocation location) {
        Template cached = templateCache.get(location);
        if (cached != null) {
            return cached;
        }

        Template template;
        if (isMeteorTemplate(location)) {
            template = readDataTemplate(world, location);
        } else {
            TemplateManager manager = world.getStructureTemplateManager();
            template = manager.get(null, location);
            if (template == null) {
                template = readDataTemplate(world, location);
            }
        }

        if (template != null) {
            templateCache.put(location, template);
        }
        return template;
    }

    private boolean isMeteorTemplate(ResourceLocation location) {
        return Reference.MOD_ID.equals(location.getNamespace()) && location.getPath().startsWith("meteor");
    }

    private StructureBoundingBox getChunkBoundingBox(World world, int chunkX, int chunkZ) {
        return new StructureBoundingBox(chunkX * 16, 1, chunkZ * 16, chunkX * 16 + 15, world.getHeight(), chunkZ * 16 + 15);
    }

    private Random getMeteorRandom(long worldSeed, int dimension, int regionX, int regionZ) {
        return new Random(worldSeed
            + (long) regionX * 341873128712L
            + (long) regionZ * 132897987541L
            + (long) dimension * 1442695040888963407L
            + METEOR_SALT);
    }

    private Template readDataTemplate(WorldServer world, ResourceLocation location) {
        String path = "/data/" + location.getNamespace() + "/structures/" + location.getPath() + ".nbt";
        try (InputStream stream = AdAstraStructureWorldGenerator.class.getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }

            NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
            tag = world.getMinecraftServer().getDataFixer().process(FixTypes.STRUCTURE, tag);
            if (isMeteorTemplate(location)) {
                remapMeteorPalette(tag);
                remapMeteorLootTables(tag);
            }
            Template template = new Template();
            template.read(tag);
            return template;
        } catch (IOException | RuntimeException exception) {
            return null;
        }
    }

    private void remapMeteorPalette(NBTTagCompound tag) {
        NBTTagList palette = tag.getTagList("palette", 10);
        for (int i = 0; i < palette.tagCount(); i++) {
            NBTTagCompound state = palette.getCompoundTagAt(i);
            if (!state.hasKey("Name", 8)) {
                continue;
            }

            String originalName = state.getString("Name");
            String remappedName = remapMeteorBlockName(originalName);
            if (!isKnownBlock(remappedName)) {
                remappedName = "minecraft:air";
            }

            if (!originalName.equals(remappedName)) {
                state.setString("Name", remappedName);
                state.removeTag("Properties");
            }
        }
    }

    private void remapMeteorLootTables(NBTTagCompound tag) {
        NBTTagList blocks = tag.getTagList("blocks", 10);
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound block = blocks.getCompoundTagAt(i);
            if (!block.hasKey("nbt", 10)) {
                continue;
            }

            NBTTagCompound blockEntity = block.getCompoundTag("nbt");
            if (LEGACY_METEOR_LOOT_TABLE.equals(blockEntity.getString("LootTable"))) {
                blockEntity.setString("LootTable", METEOR_LOOT_TABLE);
            }
        }
    }

    private String remapMeteorBlockName(String name) {
        if ("minecraft:soul_soil".equals(name)) {
            return "minecraft:soul_sand";
        }
        if ("minecraft:basalt".equals(name) || "minecraft:polished_basalt".equals(name)) {
            return Reference.MOD_ID + ":sky_stone";
        }
        if ("minecraft:blackstone".equals(name) || "minecraft:crying_obsidian".equals(name)) {
            return "minecraft:obsidian";
        }
        if ("minecraft:gilded_blackstone".equals(name)) {
            return "minecraft:coal_ore";
        }
        if ("minecraft:magma_block".equals(name)) {
            return "minecraft:magma";
        }
        if ("minecraft:soul_fire".equals(name)) {
            return "minecraft:air";
        }
        return name;
    }

    private boolean isKnownBlock(String name) {
        return Block.REGISTRY.containsKey(new ResourceLocation(name));
    }

    private ChunkPos getOilWellCandidate(long worldSeed, int chunkX, int chunkZ) {
        int regionX = floorDiv(chunkX, OIL_WELL_SPACING);
        int regionZ = floorDiv(chunkZ, OIL_WELL_SPACING);
        Random random = new Random(worldSeed + (long) regionX * 341873128712L + (long) regionZ * 132897987541L + OIL_WELL_SALT);
        int range = OIL_WELL_SPACING - OIL_WELL_SEPARATION;
        int candidateX = regionX * OIL_WELL_SPACING + random.nextInt(range);
        int candidateZ = regionZ * OIL_WELL_SPACING + random.nextInt(range);
        return new ChunkPos(candidateX, candidateZ);
    }

    private int floorDiv(int value, int divisor) {
        int result = value / divisor;
        if ((value ^ divisor) < 0 && result * divisor != value) {
            result--;
        }
        return result;
    }

    private boolean isOilWellBiome(World world, BlockPos center) {
        Biome biome = world.getBiome(center);
        return biome instanceof BiomeOcean;
    }

    private boolean canPlaceOilWell(World world, BlockPos origin, BlockPos size) {
        if (origin.getY() < 1 || origin.getY() + size.getY() >= world.getHeight()) {
            return false;
        }

        BlockPos center = origin.add(size.getX() / 2, size.getY(), size.getZ() / 2);
        IBlockState surface = world.getBlockState(world.getHeight(center).down());
        return surface.getMaterial().isLiquid();
    }
}
