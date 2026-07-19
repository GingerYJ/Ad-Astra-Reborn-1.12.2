package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModResourceIds;
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

    private static final int MARS_DIMENSION_ID = 1202;
    private static final int VENUS_DIMENSION_ID = 1204;
    private static final int MOON_DIMENSION_ID = 1201;
    private static final ResourceLocation MARS_TEMPLE_TEMPLATE =
        new ResourceLocation(Reference.MOD_ID, "temple/mars/mars_temple");
    private static final ResourceLocation VENUS_TOWER_TEMPLATE =
        new ResourceLocation(Reference.MOD_ID, "venus_tower");
    private static final int SURFACE_STRUCTURE_SPACING = 32;
    private static final int SURFACE_STRUCTURE_SEPARATION = 20;
    private static final int MARS_TEMPLE_SALT = 486209812;
    private static final int VENUS_TOWER_SALT = 771903547;

    // Matches data/ad_astra/worldgen/structure(_set)/moon_dungeon.json (spacing 32 / separation 14
    // / salt 1361819893, start_height absolute -40, jigsaw size 35).
    private static final String MOON_DUNGEON_START_POOL = "dungeon/moon/entrance";
    private static final int MOON_DUNGEON_MAX_PIECES = 35;
    private static final int MOON_DUNGEON_Y_OFFSET = -40;
    private static final int MOON_DUNGEON_SPACING = 32;
    private static final int MOON_DUNGEON_SEPARATION = 14;
    private static final int MOON_DUNGEON_SALT = 1361819893;

    // Matches data/ad_astra/worldgen/structure(_set)/lunarian_village.json (spacing 30 / separation
    // 14 / salt 2118452159, start_height absolute -20, jigsaw size 20).
    private static final String LUNARIAN_VILLAGE_START_POOL = "lunarian_village/entrance";
    private static final int LUNARIAN_VILLAGE_MAX_PIECES = 20;
    private static final int LUNARIAN_VILLAGE_Y_OFFSET = -20;
    private static final int LUNARIAN_VILLAGE_SPACING = 30;
    private static final int LUNARIAN_VILLAGE_SEPARATION = 14;
    private static final int LUNARIAN_VILLAGE_SALT = 2118452159;

    // Matches data/ad_astra/worldgen/structure(_set)/lunar_tower.json (spacing 34 / separation
    // 13 / salt 1460175004, start_height absolute 0, jigsaw size 1 - single structure).
    private static final String LUNAR_TOWER_START_POOL = "lunar_tower";
    private static final int LUNAR_TOWER_MAX_PIECES = 1;
    private static final int LUNAR_TOWER_Y_OFFSET = 0;
    private static final int LUNAR_TOWER_SPACING = 34;
    private static final int LUNAR_TOWER_SEPARATION = 13;
    private static final int LUNAR_TOWER_SALT = 1460175004;
    private static final int MOON_DUNGEON_CHUNK_REACH = 24;
    private static final int LUNARIAN_VILLAGE_CHUNK_REACH = 12;
    private static final int LUNAR_TOWER_CHUNK_REACH = 2;

    private static final int ADDITIONAL_STRUCTURE_CHUNK_REACH = 5;
    private static final StructureSpec[] ADDITIONAL_STRUCTURES = new StructureSpec[] {
        new StructureSpec("ceres", "ceres_meteor", 40, 5, 1471040600, -5),
        new StructureSpec("eris", "eris_building", 40, 5, 1471040600, 0),
        new StructureSpec("glacio", "glacian_tree", 7, 1, 951165700, 0),
        new StructureSpec("glacio", "glacio_hut", 20, 6, 1642136474, 0),
        new StructureSpec("gonggong", "gonggong_temple", 40, 5, 1471040600, -4),
        new StructureSpec("haumea", "haumea_building", 40, 5, 1471040600, 0),
        new StructureSpec("jupiter", "jupiter_temple", 20, 6, 1642136474, 0),
        new StructureSpec("makemake", "makemake_building", 40, 5, 1471040600, 0),
        new StructureSpec("mercury", "mercury_volcano", 40, 5, 1471040600, 0),
        new StructureSpec("moon", "moon_mushroom_spot", 80, 1, 951165700, 1),
        new StructureSpec("neptune", "neptune_maze", 40, 5, 1471040600, 0),
        new StructureSpec("orcus", "orcus_temple", 40, 5, 1471040600, 0),
        new StructureSpec("pluto", "pluto_temple", 40, 5, 1471040600, 0),
        new StructureSpec("quaoar", "quaoar_temple", 40, 5, 1471040600, -4),
        new StructureSpec("saturn", "saturn_tower", 40, 5, 1471040600, 0),
        new StructureSpec("sedna", "sedna_fallen_ship", 40, 5, 1471040600, -2),
        new StructureSpec("uranus", "uranus_dungeon", 40, 5, 1471040600, -40),
        new StructureSpec("uranus", "uranus_tower", 20, 6, 1642136474, 0),
        new StructureSpec("venus", "venus_volcano", 40, 5, 1471040600, 0)
    };
    private static final ITemplateProcessor SKIP_AIR_PROCESSOR = new ITemplateProcessor() {
        @Override
        public Template.BlockInfo processBlock(World world, BlockPos pos, Template.BlockInfo blockInfo) {
            return blockInfo.blockState.getBlock() == Blocks.AIR ? null : blockInfo;
        }
    };

    private final Map<ResourceLocation, Template> templateCache = new HashMap<ResourceLocation, Template>();
    private final JigsawStructureGenerator jigsawGenerator = new JigsawStructureGenerator();

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
            int dimension = world.provider.getDimension();
            if (dimension == MARS_DIMENSION_ID) {
                generateSurfaceStructure(serverWorld, chunkX, chunkZ, MARS_TEMPLE_TEMPLATE, MARS_TEMPLE_SALT);
            } else if (dimension == VENUS_DIMENSION_ID) {
                generateSurfaceStructure(serverWorld, chunkX, chunkZ, VENUS_TOWER_TEMPLATE, VENUS_TOWER_SALT);
            } else if (dimension == MOON_DIMENSION_ID) {
                generateMoonDungeon(serverWorld, chunkX, chunkZ);
                generateLunarianVillage(serverWorld, chunkX, chunkZ);
                generateLunarTower(serverWorld, chunkX, chunkZ);
            }
            generateAdditionalStructures(serverWorld, chunkX, chunkZ);
        }
    }

    private void generateAdditionalStructures(WorldServer world, int chunkX, int chunkZ) {
        if (world.provider instanceof AdAstraOrbitWorldProvider) {
            return;
        }

        String planet = ((AdAstraWorldProvider) world.provider).getPlanetName();
        if (planet == null) {
            return;
        }

        for (StructureSpec structure : ADDITIONAL_STRUCTURES) {
            if (!structure.planet.equals(planet)) {
                continue;
            }
            generateAdditionalStructure(world, chunkX, chunkZ, structure);
        }
    }

    private void generateAdditionalStructure(WorldServer world, int chunkX, int chunkZ, StructureSpec structure) {
        int regionX = floorDiv(chunkX, structure.spacing);
        int regionZ = floorDiv(chunkZ, structure.spacing);
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                generateAdditionalRegion(
                    world, chunkX, chunkZ, structure, regionX + offsetX, regionZ + offsetZ);
            }
        }
    }

    private void generateAdditionalRegion(WorldServer world, int chunkX, int chunkZ, StructureSpec structure,
                                        int regionX, int regionZ) {
        Random structureRandom = new Random(world.getSeed()
            + (long) regionX * 341873128712L
            + (long) regionZ * 132897987541L
            + (long) world.provider.getDimension() * 1442695040888963407L
            + structure.salt);
        int range = structure.spacing - structure.separation;
        if (range <= 0) {
            return;
        }
        int candidateX = regionX * structure.spacing + structureRandom.nextInt(range);
        int candidateZ = regionZ * structure.spacing + structureRandom.nextInt(range);
        if (!isWithinChunkReach(chunkX, chunkZ, candidateX, candidateZ, ADDITIONAL_STRUCTURE_CHUNK_REACH)) {
            return;
        }

        String startPool = ModResourceIds.structure(structure.name).toString() + "/start_pool";
        jigsawGenerator.generateChunkSlice(
            world,
            new ChunkPos(candidateX, candidateZ),
            new ChunkPos(chunkX, chunkZ),
            startPool,
            2,
            structure.startYOffset,
            ADDITIONAL_STRUCTURE_CHUNK_REACH,
            structureRandom);
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

    private void generateMoonDungeon(WorldServer world, int chunkX, int chunkZ) {
        int regionX = floorDiv(chunkX, MOON_DUNGEON_SPACING);
        int regionZ = floorDiv(chunkZ, MOON_DUNGEON_SPACING);
        int regionReach = 1;
        for (int offsetX = -regionReach; offsetX <= regionReach; offsetX++) {
            for (int offsetZ = -regionReach; offsetZ <= regionReach; offsetZ++) {
                generateMoonDungeonSlice(world, chunkX, chunkZ, regionX + offsetX, regionZ + offsetZ);
            }
        }
    }

    private void generateMoonDungeonSlice(WorldServer world, int chunkX, int chunkZ, int regionX, int regionZ) {
        Random random = new Random(world.getSeed()
            + (long) regionX * 341873128712L
            + (long) regionZ * 132897987541L
            + (long) world.provider.getDimension() * 1442695040888963407L
            + MOON_DUNGEON_SALT);
        int range = MOON_DUNGEON_SPACING - MOON_DUNGEON_SEPARATION;
        int candidateX = regionX * MOON_DUNGEON_SPACING + random.nextInt(range);
        int candidateZ = regionZ * MOON_DUNGEON_SPACING + random.nextInt(range);
        if (!isWithinChunkReach(chunkX, chunkZ, candidateX, candidateZ, MOON_DUNGEON_CHUNK_REACH)) {
            return;
        }
        jigsawGenerator.generateChunkSlice(
            world, new ChunkPos(candidateX, candidateZ), new ChunkPos(chunkX, chunkZ),
            MOON_DUNGEON_START_POOL, MOON_DUNGEON_MAX_PIECES, MOON_DUNGEON_Y_OFFSET,
            MOON_DUNGEON_CHUNK_REACH, random);
    }

    private void generateLunarianVillage(WorldServer world, int chunkX, int chunkZ) {
        int regionX = floorDiv(chunkX, LUNARIAN_VILLAGE_SPACING);
        int regionZ = floorDiv(chunkZ, LUNARIAN_VILLAGE_SPACING);
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                generateLunarianVillageSlice(world, chunkX, chunkZ, regionX + offsetX, regionZ + offsetZ);
            }
        }
    }

    private void generateLunarianVillageSlice(WorldServer world, int chunkX, int chunkZ, int regionX, int regionZ) {
        Random random = new Random(world.getSeed()
            + (long) regionX * 341873128712L
            + (long) regionZ * 132897987541L
            + (long) world.provider.getDimension() * 1442695040888963407L
            + LUNARIAN_VILLAGE_SALT);
        int range = LUNARIAN_VILLAGE_SPACING - LUNARIAN_VILLAGE_SEPARATION;
        int candidateX = regionX * LUNARIAN_VILLAGE_SPACING + random.nextInt(range);
        int candidateZ = regionZ * LUNARIAN_VILLAGE_SPACING + random.nextInt(range);
        if (!isWithinChunkReach(chunkX, chunkZ, candidateX, candidateZ, LUNARIAN_VILLAGE_CHUNK_REACH)) {
            return;
        }
        jigsawGenerator.generateChunkSlice(
            world, new ChunkPos(candidateX, candidateZ), new ChunkPos(chunkX, chunkZ),
            LUNARIAN_VILLAGE_START_POOL, LUNARIAN_VILLAGE_MAX_PIECES, LUNARIAN_VILLAGE_Y_OFFSET,
            LUNARIAN_VILLAGE_CHUNK_REACH, random);
    }

    private void generateLunarTower(WorldServer world, int chunkX, int chunkZ) {
        int regionX = floorDiv(chunkX, LUNAR_TOWER_SPACING);
        int regionZ = floorDiv(chunkZ, LUNAR_TOWER_SPACING);
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                generateLunarTowerSlice(world, chunkX, chunkZ, regionX + offsetX, regionZ + offsetZ);
            }
        }
    }

    private void generateLunarTowerSlice(WorldServer world, int chunkX, int chunkZ, int regionX, int regionZ) {
        Random random = new Random(world.getSeed()
            + (long) regionX * 341873128712L
            + (long) regionZ * 132897987541L
            + (long) world.provider.getDimension() * 1442695040888963407L
            + LUNAR_TOWER_SALT);
        int range = LUNAR_TOWER_SPACING - LUNAR_TOWER_SEPARATION;
        int candidateX = regionX * LUNAR_TOWER_SPACING + random.nextInt(range);
        int candidateZ = regionZ * LUNAR_TOWER_SPACING + random.nextInt(range);
        if (!isWithinChunkReach(chunkX, chunkZ, candidateX, candidateZ, LUNAR_TOWER_CHUNK_REACH)) {
            return;
        }
        jigsawGenerator.generateChunkSlice(
            world, new ChunkPos(candidateX, candidateZ), new ChunkPos(chunkX, chunkZ),
            LUNAR_TOWER_START_POOL, LUNAR_TOWER_MAX_PIECES, LUNAR_TOWER_Y_OFFSET,
            LUNAR_TOWER_CHUNK_REACH, random);
    }

    private void generateSurfaceStructure(WorldServer world, int chunkX, int chunkZ, ResourceLocation location, int salt) {
        ChunkPos candidate = getSurfaceStructureCandidate(world.getSeed(), world.provider.getDimension(), chunkX, chunkZ, salt);
        if (candidate.x != chunkX || candidate.z != chunkZ) {
            return;
        }

        Template template = getTemplate(world, location);
        if (template == null) {
            return;
        }

        BlockPos size = template.getSize();
        int centerX = chunkX * 16 + 8;
        int centerZ = chunkZ * 16 + 8;
        BlockPos surface = world.getHeight(new BlockPos(centerX, 0, centerZ));
        int groundY = surface.getY() - 1;
        if (groundY < 1 || groundY + size.getY() >= world.getHeight()) {
            return;
        }

        BlockPos origin = new BlockPos(centerX - size.getX() / 2, groundY, centerZ - size.getZ() / 2);
        PlacementSettings settings = new PlacementSettings()
            .setMirror(Mirror.NONE)
            .setRotation(Rotation.NONE)
            .setIgnoreEntities(false)
            .setIgnoreStructureBlock(true)
            .setReplacedBlock(Blocks.STRUCTURE_VOID)
            .setChunk(new ChunkPos(chunkX, chunkZ))
            .setBoundingBox(getChunkBoundingBox(world, chunkX, chunkZ));
        template.addBlocksToWorld(world, origin, SKIP_AIR_PROCESSOR, settings, 2);
    }

    private ChunkPos getSurfaceStructureCandidate(long worldSeed, int dimension, int chunkX, int chunkZ, int salt) {
        int regionX = floorDiv(chunkX, SURFACE_STRUCTURE_SPACING);
        int regionZ = floorDiv(chunkZ, SURFACE_STRUCTURE_SPACING);
        Random random = new Random(worldSeed
            + (long) regionX * 341873128712L
            + (long) regionZ * 132897987541L
            + (long) dimension * 1442695040888963407L
            + salt);
        int range = SURFACE_STRUCTURE_SPACING - SURFACE_STRUCTURE_SEPARATION;
        int candidateX = regionX * SURFACE_STRUCTURE_SPACING + random.nextInt(range);
        int candidateZ = regionZ * SURFACE_STRUCTURE_SPACING + random.nextInt(range);
        return new ChunkPos(candidateX, candidateZ);
    }

    private Template getTemplate(WorldServer world, ResourceLocation location) {
        Template cached = templateCache.get(location);
        if (cached != null) {
            return cached;
        }

        Template template = readDataTemplate(world, location);
        if (template == null) {
            TemplateManager manager = world.getStructureTemplateManager();
            template = manager.get(null, location);
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
            if (Reference.MOD_ID.equals(location.getNamespace())) {
                AdAstraStructureBlocks.remapPalette(tag);
                AdAstraStructureBlocks.remapStructureData(tag);
            }
            if (isMeteorTemplate(location)) {
                remapMeteorLootTables(tag);
            }
            Template template = new Template();
            template.read(tag);
            return template;
        } catch (IOException | RuntimeException exception) {
            return null;
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

    private boolean isWithinChunkReach(int chunkX, int chunkZ, int candidateX, int candidateZ, int reach) {
        return Math.abs(chunkX - candidateX) <= reach && Math.abs(chunkZ - candidateZ) <= reach;
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

    private static final class StructureSpec {
        private final String planet;
        private final String name;
        private final int spacing;
        private final int separation;
        private final int salt;
        private final int startYOffset;

        private StructureSpec(String planet, String name, int spacing, int separation, int salt, int startYOffset) {
            this.planet = planet;
            this.name = name;
            this.spacing = spacing;
            this.separation = separation;
            this.salt = salt;
            this.startYOffset = startYOffset;
        }
    }
}
