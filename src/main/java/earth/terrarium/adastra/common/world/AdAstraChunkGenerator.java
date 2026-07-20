package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.common.config.OreGenConfig;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.world.feature.CraterCarver;
import earth.terrarium.adastra.common.world.feature.CraterConfig;
import earth.terrarium.adastra.common.world.noise.AdAstraNoiseGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AdAstraChunkGenerator implements IChunkGenerator {

    public static final int SURFACE_Y = 63;
    public static final int SPAWN_Y = SURFACE_Y + 1;

    private static final int MIN_GENERATION_Y = 1;
    private static final int MAX_CACHED_CRATER_CHUNKS = 256;
    private static final Map<String, List<PlanetOreSpec>> ORE_SPECS_CACHE = new HashMap<>();

    private final World world;
    private final PlanetDimensionProperties properties;
    private final PlanetTerrainConfig terrainConfig;
    private final CraterConfig craterConfig;
    private final AdAstraNoiseGenerator heightNoise;
    private final AdAstraNoiseGenerator caveNoise;
    private final Map<Long, List<CraterCarver>> craterCache =
        new LinkedHashMap<Long, List<CraterCarver>>(MAX_CACHED_CRATER_CHUNKS, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, List<CraterCarver>> eldest) {
                return size() > MAX_CACHED_CRATER_CHUNKS;
            }
        };

    public AdAstraChunkGenerator(World world, PlanetDimensionProperties properties) {
        this.world = world;
        this.properties = properties;
        this.terrainConfig = PlanetTerrainConfig.forPlanet(properties.getName());
        this.craterConfig = CraterConfig.forPlanet(properties.getName());

        Random random = new Random(world.getSeed());
        this.heightNoise = new AdAstraNoiseGenerator(
            random,
            terrainConfig.getOctaves(),
            terrainConfig.getPersistence(),
            terrainConfig.getScale()
        );

        this.caveNoise = new AdAstraNoiseGenerator(
            new Random(world.getSeed() + 1),
            3,
            0.5,
            30.0
        );
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        ChunkPrimer primer = new ChunkPrimer();
        IBlockState bedrock = Blocks.BEDROCK.getDefaultState();
        IBlockState surface = properties.getSurfaceBlock();
        IBlockState filler = properties.getFillerBlock();

        // Generate terrain with noise
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                // Calculate terrain height using noise
                int terrainHeight = heightNoise.sampleHeight(
                    worldX,
                    worldZ,
                    terrainConfig.getMinHeight(),
                    terrainConfig.getMaxHeight()
                );

                // Place bedrock at bottom
                primer.setBlockState(x, 0, z, bedrock);

                // Generate terrain column
                int maxTerrainY = Math.min(255, terrainHeight);
                for (int y = 1; y <= maxTerrainY; y++) {
                    // Check for cave carving
                    if (terrainConfig.shouldGenerateCaves() &&
                        y >= terrainConfig.getCaveMinY() &&
                        y <= terrainConfig.getCaveMaxY() &&
                        y < terrainHeight - 3) {
                        double caveValue = caveNoise.sample3D(worldX, y, worldZ);
                        if (caveValue > terrainConfig.getCaveThreshold()) {
                            continue; // Air pocket (cave)
                        }
                    }

                    // Surface and subsurface layers
                    if (y == terrainHeight) {
                        primer.setBlockState(x, y, z, surface);
                    } else if (y >= terrainHeight - terrainConfig.getSurfaceDepth()) {
                        primer.setBlockState(x, y, z, surface);
                    } else {
                        primer.setBlockState(x, y, z, filler);
                    }
                }
            }
        }

        // Carve craters into terrain
        if (craterConfig.isEnabled()) {
            carveCraters(primer, chunkX, chunkZ);
        }

        // Ore is part of the chunk's terrain. Writing it into the primer avoids per-block
        // world updates during populate for every planet and keeps generation local to this chunk.
        generateConfiguredOres(primer, chunkX, chunkZ);

        Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);
        byte biomeId = (byte) Biome.getIdForBiome(properties.getBiome());
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = biomeId;
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        BlockPos chunkOrigin = new BlockPos(chunkX * 16, 0, chunkZ * 16);
        // Keep population-only features on their own deterministic stream so ore generation
        // changes do not affect their placement relative to one another.
        generateSimpleFeatures(chunkOrigin, createChunkRandom(chunkX, chunkZ));
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return PlanetMobSpawns.getPossibleCreatures(properties, creatureType);
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }

    private Random createChunkRandom(int chunkX, int chunkZ) {
        Random random = new Random(world.getSeed());
        long xSeed = random.nextLong() / 2L * 2L + 1L;
        long zSeed = random.nextLong() / 2L * 2L + 1L;
        random.setSeed((long) chunkX * xSeed + (long) chunkZ * zSeed ^ world.getSeed());
        return random;
    }

    private void generateConfiguredOres(ChunkPrimer primer, int chunkX, int chunkZ) {
        Random random = createChunkRandom(chunkX, chunkZ);
        BlockPos chunkOrigin = new BlockPos(chunkX * 16, 0, chunkZ * 16);
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        List<PlanetOreSpec> specs = getOreSpecs(properties.getName());

        int oreCount = 0;
        for (PlanetOreSpec spec : specs) {
            int generated = generateOre(spec, primer, chunkOrigin, random, chunkX, chunkZ);
            oreCount += generated;

            if (OreGenConfig.debugWorldgen && generated > 0) {
                AdAstraReborn.LOGGER.info("Generated {} {} ore veins in chunk [{}, {}] at Y:{}-{}",
                    generated, spec.getOreName(), chunkPos.x, chunkPos.z, spec.minY, spec.maxY);
            }
        }

        if ("moon".equals(properties.getName())) {
            oreCount += generateMoonSoulSand(primer, chunkOrigin, random, chunkX, chunkZ);
        }

        if (OreGenConfig.debugWorldgen && oreCount > 0) {
            AdAstraReborn.LOGGER.debug("Total {} ore veins generated in chunk [{}, {}] on {}",
                oreCount, chunkPos.x, chunkPos.z, properties.getName());
        }
    }

    private int generateOre(PlanetOreSpec spec, ChunkPrimer primer, BlockPos chunkOrigin, Random random,
                             int chunkX, int chunkZ) {
        int generated = 0;
        int maxGenerationY = terrainConfig.getMaxHeight() - 3; // Stay below surface
        int maxY = Math.min(spec.maxY, maxGenerationY);
        if (spec.minY > maxY) {
            return 0;
        }

        for (int i = 0; i < spec.countPerChunk; i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int y = spec.minY + random.nextInt(maxY - spec.minY + 1);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            if (generateVein(spec, primer, random, new BlockPos(x, y, z), chunkX, chunkZ)) {
                generated++;
            }
        }
        return generated;
    }

    private boolean generateVein(PlanetOreSpec spec, ChunkPrimer primer, Random random, BlockPos origin,
                                 int chunkX, int chunkZ) {
        float angle = random.nextFloat() * (float) Math.PI;
        double startX = (double) origin.getX() + 8.0D + (double) (MathHelper.sin(angle) * (float) spec.veinSize) / 8.0D;
        double endX = (double) origin.getX() + 8.0D - (double) (MathHelper.sin(angle) * (float) spec.veinSize) / 8.0D;
        double startZ = (double) origin.getZ() + 8.0D + (double) (MathHelper.cos(angle) * (float) spec.veinSize) / 8.0D;
        double endZ = (double) origin.getZ() + 8.0D - (double) (MathHelper.cos(angle) * (float) spec.veinSize) / 8.0D;
        double startY = (double) origin.getY() + random.nextInt(3) - 2;
        double endY = (double) origin.getY() + random.nextInt(3) - 2;

        boolean generated = false;
        int maxGenerationY = terrainConfig.getMaxHeight() - 2;
        int chunkMinX = chunkX << 4;
        int chunkMinZ = chunkZ << 4;
        int chunkMaxX = chunkMinX + 15;
        int chunkMaxZ = chunkMinZ + 15;

        for (int step = 0; step < spec.veinSize; step++) {
            float progress = (float) step / (float) spec.veinSize;
            double centerX = startX + (endX - startX) * (double) progress;
            double centerY = startY + (endY - startY) * (double) progress;
            double centerZ = startZ + (endZ - startZ) * (double) progress;
            double diameter = random.nextDouble() * (double) spec.veinSize / 16.0D;
            double horizontalRadius = (double) (MathHelper.sin((float) Math.PI * progress) + 1.0F) * diameter + 1.0D;
            double verticalRadius = (double) (MathHelper.sin((float) Math.PI * progress) + 1.0F) * diameter + 1.0D;

            int minX = MathHelper.floor(centerX - horizontalRadius / 2.0D);
            int minY = Math.max(MIN_GENERATION_Y, MathHelper.floor(centerY - verticalRadius / 2.0D));
            int minZ = MathHelper.floor(centerZ - horizontalRadius / 2.0D);
            int maxX = MathHelper.floor(centerX + horizontalRadius / 2.0D);
            int maxY = Math.min(maxGenerationY, MathHelper.floor(centerY + verticalRadius / 2.0D));
            int maxZ = MathHelper.floor(centerZ + horizontalRadius / 2.0D);

            minX = Math.max(minX, chunkMinX);
            maxX = Math.min(maxX, chunkMaxX);
            minZ = Math.max(minZ, chunkMinZ);
            maxZ = Math.min(maxZ, chunkMaxZ);
            if (minX > maxX || minZ > maxZ || minY > maxY) {
                continue;
            }

            for (int x = minX; x <= maxX; x++) {
                double xDistance = ((double) x + 0.5D - centerX) / (horizontalRadius / 2.0D);
                if (xDistance * xDistance >= 1.0D) {
                    continue;
                }

                for (int y = minY; y <= maxY; y++) {
                    double yDistance = ((double) y + 0.5D - centerY) / (verticalRadius / 2.0D);
                    if (xDistance * xDistance + yDistance * yDistance >= 1.0D) {
                        continue;
                    }

                    for (int z = minZ; z <= maxZ; z++) {
                        double zDistance = ((double) z + 0.5D - centerZ) / (horizontalRadius / 2.0D);
                        if (xDistance * xDistance + yDistance * yDistance + zDistance * zDistance < 1.0D) {
                            if (replaceOreBlock(spec, primer, x, y, z, chunkX, chunkZ)) {
                                generated = true;
                            }
                        }
                    }
                }
            }
        }

        return generated;
    }

    private boolean replaceOreBlock(PlanetOreSpec spec, ChunkPrimer primer, int x, int y, int z,
                                    int chunkX, int chunkZ) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!isInChunk(pos, chunkX, chunkZ)) {
            return false;
        }
        int localX = x & 15;
        int localZ = z & 15;
        IBlockState current = primer.getBlockState(localX, y, localZ);
        if (spec.canReplace(current)) {
            primer.setBlockState(localX, y, localZ, spec.oreState);
            return true;
        }
        return false;
    }

    private void generateSimpleFeatures(BlockPos chunkOrigin, Random random) {
        String planetName = properties.getName();
        if ("mars".equals(planetName)) {
            generateMarsRockBlobs(chunkOrigin, random);
        } else if ("venus".equals(planetName)) {
            generateInfernalSpireColumns(chunkOrigin, random);
        }
    }

    private int generateMoonSoulSand(ChunkPrimer primer, BlockPos chunkOrigin, Random random,
                                     int chunkX, int chunkZ) {
        PlanetOreSpec soulSand = new PlanetOreSpec(
            "moon",
            Blocks.SOUL_SAND.getDefaultState(),
            8,
            8,
            MIN_GENERATION_Y,
            terrainConfig.getMaxHeight() - 5,
            new Block[]{ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE},
            null
        );
        return generateOre(soulSand, primer, chunkOrigin, random, chunkX, chunkZ);
    }

    private void generateMarsRockBlobs(BlockPos chunkOrigin, Random random) {
        int chunkX = chunkOrigin.getX() >> 4;
        int chunkZ = chunkOrigin.getZ() >> 4;
        for (int i = 0; i < 2; i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            int searchStartY = terrainConfig.getMaxHeight() + 8;
            BlockPos surface = findSurface(new BlockPos(x, searchStartY, z), ModBlocks.MARS_SAND);
            if (surface != null) {
                generateMarsRockBlob(surface.up(), random, chunkX, chunkZ);
            }
        }
    }

    @Nullable
    private BlockPos findSurface(BlockPos start, Block groundBlock) {
        BlockPos pos = start;
        while (pos.getY() > MIN_GENERATION_Y + 3) {
            if (!world.isAirBlock(pos.down()) && world.getBlockState(pos.down()).getBlock() == groundBlock) {
                return pos.down();
            }
            pos = pos.down();
        }
        return null;
    }

    private void generateMarsRockBlob(BlockPos origin, Random random, int chunkX, int chunkZ) {
        BlockPos center = origin;
        for (int i = 0; i < 3; i++) {
            int radiusX = random.nextInt(2);
            int radiusY = random.nextInt(2);
            int radiusZ = random.nextInt(2);
            float radius = (float) (radiusX + radiusY + radiusZ) * 0.333F + 0.5F;

            for (int x = center.getX() - radiusX; x <= center.getX() + radiusX; x++) {
                for (int y = center.getY() - radiusY; y <= center.getY() + radiusY; y++) {
                    for (int z = center.getZ() - radiusZ; z <= center.getZ() + radiusZ; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (isInChunk(pos, chunkX, chunkZ)
                            && pos.distanceSq(center) <= (double) (radius * radius)) {
                            world.setBlockState(pos, ModBlocks.CONGLOMERATE.getDefaultState(), 2);
                        }
                    }
                }
            }

            center = center.add(-1 + random.nextInt(2), -random.nextInt(2), -1 + random.nextInt(2));
        }
    }

    private void generateInfernalSpireColumns(BlockPos chunkOrigin, Random random) {
        for (int i = 0; i < 4; i++) {
            generateInfernalSpireColumn(chunkOrigin, random, 1, 4, 1);
        }
        for (int i = 0; i < 2; i++) {
            generateInfernalSpireColumn(chunkOrigin, random, 5, 10, 2 + random.nextInt(2));
        }
    }

    private void generateInfernalSpireColumn(BlockPos chunkOrigin, Random random, int minHeight, int maxHeight, int reach) {
        int x = chunkOrigin.getX() + random.nextInt(16);
        int z = chunkOrigin.getZ() + random.nextInt(16);
        int searchStartY = terrainConfig.getMaxHeight() + 8;
        BlockPos surface = findSurface(new BlockPos(x, searchStartY, z), ModBlocks.VENUS_SAND);
        if (surface == null) {
            return;
        }

        int height = minHeight + random.nextInt(maxHeight - minHeight + 1);
        placeInfernalSpireCluster(surface.up(), random, height, reach, chunkOrigin);
    }

    private void placeInfernalSpireCluster(BlockPos origin, Random random, int height, int reach, BlockPos chunkOrigin) {
        boolean dense = random.nextFloat() < 0.9F;
        int spread = Math.min(height, dense ? 5 : 8);
        int attempts = dense ? 50 : 15;

        for (int i = 0; i < attempts; i++) {
            BlockPos columnOrigin = origin.add(randomRange(random, spread), 0, randomRange(random, spread));
            int columnHeight = height - Math.abs(columnOrigin.getX() - origin.getX()) - Math.abs(columnOrigin.getZ() - origin.getZ());
            if (columnHeight >= 0) {
                placeInfernalSpireColumnAt(columnOrigin, columnHeight, reach, chunkOrigin);
            }
        }
    }

    private int randomRange(Random random, int radius) {
        return random.nextInt(radius * 2 + 1) - radius;
    }

    private void placeInfernalSpireColumnAt(BlockPos origin, int height, int reach, BlockPos chunkOrigin) {
        int minChunkX = chunkOrigin.getX();
        int minChunkZ = chunkOrigin.getZ();
        int maxChunkX = minChunkX + 15;
        int maxChunkZ = minChunkZ + 15;

        for (int x = Math.max(origin.getX() - reach, minChunkX); x <= Math.min(origin.getX() + reach, maxChunkX); x++) {
            for (int z = Math.max(origin.getZ() - reach, minChunkZ); z <= Math.min(origin.getZ() + reach, maxChunkZ); z++) {
                BlockPos base = new BlockPos(x, origin.getY(), z);
                int distance = Math.abs(x - origin.getX()) + Math.abs(z - origin.getZ());
                int columnHeight = height - distance / 2;
                if (columnHeight < 0 || !canPlaceSpireAt(base)) {
                    continue;
                }

                for (int y = 0; y <= columnHeight && origin.getY() + y < world.getHeight(); y++) {
                    BlockPos pos = base.up(y);
                    if (world.isAirBlock(pos) || world.getBlockState(pos).getBlock() == ModBlocks.INFERNAL_SPIRE_BLOCK) {
                        world.setBlockState(pos, ModBlocks.INFERNAL_SPIRE_BLOCK.getDefaultState(), 2);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private boolean canPlaceSpireAt(BlockPos pos) {
        if (!world.isBlockLoaded(pos) || !world.isBlockLoaded(pos.down())) {
            return false;
        }
        Block below = world.getBlockState(pos.down()).getBlock();
        Block current = world.getBlockState(pos).getBlock();
        return (world.isAirBlock(pos) || current == ModBlocks.INFERNAL_SPIRE_BLOCK)
            && below != Blocks.AIR
            && below != Blocks.BEDROCK
            && below != Blocks.LAVA;
    }

    /**
     * Carve craters into the ChunkPrimer.
     * Checks surrounding chunks for craters that might affect this chunk.
     */
    private void carveCraters(ChunkPrimer primer, int chunkX, int chunkZ) {
        IBlockState airState = Blocks.AIR.getDefaultState();

        // Check current chunk and surrounding chunks for craters
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                int checkX = chunkX + dx;
                int checkZ = chunkZ + dz;

                List<CraterCarver> craters = generateCratersForChunk(checkX, checkZ);
                for (CraterCarver crater : craters) {
                    if (crater.affectsChunk(chunkX, chunkZ)) {
                        crater.carveInPrimer(primer, chunkX, chunkZ, airState, terrainConfig.getMaxHeight());
                    }
                }
            }
        }
    }

    /**
     * Generate list of craters for a specific chunk using deterministic random.
     */
    private List<CraterCarver> generateCratersForChunk(int chunkX, int chunkZ) {
        long cacheKey = chunkKey(chunkX, chunkZ);
        List<CraterCarver> cached = craterCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<CraterCarver> craters = new ArrayList<>();
        Random random = createChunkRandom(chunkX, chunkZ);

        // Use separate seed for craters
        random.setSeed(random.nextLong() + 12345);

        // Check if this chunk should have craters
        if (random.nextDouble() > craterConfig.getCraterChance()) {
            craterCache.put(cacheKey, craters);
            return craters; // No craters in this chunk
        }

        // Determine number of craters
        int count = craterConfig.getMinCratersPerChunk();
        if (craterConfig.getMaxCratersPerChunk() > craterConfig.getMinCratersPerChunk()) {
            count += random.nextInt(craterConfig.getMaxCratersPerChunk() - craterConfig.getMinCratersPerChunk() + 1);
        }

        // Generate craters
        for (int i = 0; i < count; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);

            // Sample terrain height at crater center
            int surfaceY = heightNoise.sampleHeight(
                x, z,
                terrainConfig.getMinHeight(),
                terrainConfig.getMaxHeight()
            );

            // Create crater (large or normal)
            CraterCarver crater;
            if (craterConfig.allowLargeCraters() && random.nextFloat() < 0.15f) {
                crater = CraterCarver.createLarge(random, x, z, surfaceY);
            } else {
                crater = CraterCarver.createRandom(random, x, z, surfaceY);
            }

            craters.add(crater);
        }

        craterCache.put(cacheKey, craters);
        return craters;
    }

    private long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) ^ (chunkZ & 0xffffffffL);
    }

    protected static boolean isInChunk(BlockPos pos, int chunkX, int chunkZ) {
        return (pos.getX() >> 4) == chunkX && (pos.getZ() >> 4) == chunkZ;
    }

    private List<PlanetOreSpec> getOreSpecs(String planetName) {
        // Cache ore specs per planet for performance
        if (!ORE_SPECS_CACHE.containsKey(planetName)) {
            List<PlanetOreSpec> specs = createUnifiedOreSpecs(planetName);
            ORE_SPECS_CACHE.put(planetName, specs);
        }
        return ORE_SPECS_CACHE.get(planetName);
    }

    /**
     * Clears the ore spec cache. Call this when config is reloaded.
     */
    public static void clearCache() {
        ORE_SPECS_CACHE.clear();
    }

    private List<PlanetOreSpec> createUnifiedOreSpecs(String planetName) {
        List<PlanetOreSpec> specs = new ArrayList<>();
        for (OreGenConfig.OreEntry entry : OreGenConfig.getOreEntries(planetName)) {
            IBlockState oreState = OreGenConfig.resolveBlockState(entry.blockState);
            if (oreState == null) {
                AdAstraReborn.LOGGER.warn("Ignored ore row on {}: invalid target block '{}'.",
                    planetName, entry.blockState);
                continue;
            }
            IBlockState[] replaceableStates = parseReplaceableStates(entry.replaceTargets);
            if (replaceableStates == null) {
                AdAstraReborn.LOGGER.warn("Ignored ore row on {}: invalid replace target '{}'.",
                    planetName, entry.replaceTargets);
                continue;
            }
            addConfiguredOre(specs, planetName, oreState, entry.veinSize, entry.countPerChunk,
                entry.minY, entry.maxY, replaceableStates);
        }
        return specs;
    }

    private IBlockState[] parseReplaceableStates(String replaceTargets) {
        if (replaceTargets == null || replaceTargets.trim().isEmpty() || "default".equalsIgnoreCase(replaceTargets.trim())) {
            return defaultCustomReplaceableStates();
        }
        String[] parts = replaceTargets.split(",");
        List<IBlockState> states = new ArrayList<>();
        for (String part : parts) {
            IBlockState state = OreGenConfig.resolveBlockState(part.trim());
            if (state == null) {
                return null;
            }
            states.add(state);
        }
        return states.isEmpty() ? null : states.toArray(new IBlockState[0]);
    }

    private IBlockState[] defaultCustomReplaceableStates() {
        List<IBlockState> states = new ArrayList<>();
        addUniqueState(states, properties.getSurfaceBlock());
        addUniqueState(states, properties.getFillerBlock());
        addUniqueState(states, properties.getCaveTopBlock());
        addUniqueState(states, properties.getCaveFloorBlock());
        return states.toArray(new IBlockState[0]);
    }

    private static void addUniqueState(List<IBlockState> states, IBlockState state) {
        if (state == null) {
            return;
        }
        for (IBlockState existing : states) {
            if (existing.getBlock() == state.getBlock()
                && existing.getBlock().getMetaFromState(existing) == state.getBlock().getMetaFromState(state)) {
                return;
            }
        }
        states.add(state);
    }

    private static IBlockState state(Block block, int meta) {
        return block.getStateFromMeta(meta);
    }

    private void addConfiguredOre(List<PlanetOreSpec> specs, String planetName, Block oreBlock,
                                   int veinSize, int countPerChunk, int sourceMinY, int sourceMaxY,
                                   Block... replaceableBlocks) {
        int maxGenerationY = terrainConfig.getMaxHeight() - 3;
        int minY = Math.max(MIN_GENERATION_Y, sourceMinY);
        int maxY = Math.min(maxGenerationY, sourceMaxY);
        int adjustedCount = OreGenConfig.getModifiedOreCount(countPerChunk);
        if (minY <= maxY && adjustedCount > 0) {
            specs.add(new PlanetOreSpec(planetName, oreBlock.getDefaultState(), veinSize, adjustedCount, minY, maxY, replaceableBlocks, null));
        }
    }

    private void addConfiguredOre(List<PlanetOreSpec> specs, String planetName, IBlockState oreState,
                                  int veinSize, int countPerChunk, int sourceMinY, int sourceMaxY,
                                  IBlockState... replaceableStates) {
        int maxGenerationY = terrainConfig.getMaxHeight() - 3;
        int minY = Math.max(MIN_GENERATION_Y, sourceMinY);
        int maxY = Math.min(maxGenerationY, sourceMaxY);
        int adjustedCount = OreGenConfig.getModifiedOreCount(countPerChunk);
        if (minY <= maxY && adjustedCount > 0) {
            specs.add(new PlanetOreSpec(planetName, oreState, veinSize, adjustedCount, minY, maxY, new Block[0], replaceableStates));
        }
    }

    private static final class PlanetOreSpec {

        private final String planetName;
        private final IBlockState oreState;
        private final int veinSize;
        private final int countPerChunk;
        private final int minY;
        private final int maxY;
        private final Block[] replaceableBlocks;
        private final IBlockState[] replaceableStates;
        private String oreName; // Cached ore name for logging

        private PlanetOreSpec(String planetName, IBlockState oreState, int veinSize, int countPerChunk, int minY, int maxY,
                              Block[] replaceableBlocks, IBlockState[] replaceableStates) {
            this.planetName = planetName;
            this.oreState = oreState;
            this.veinSize = veinSize;
            this.countPerChunk = countPerChunk;
            this.minY = minY;
            this.maxY = maxY;
            this.replaceableBlocks = replaceableBlocks;
            this.replaceableStates = replaceableStates;
        }

        private boolean canReplace(IBlockState state) {
            if (replaceableStates != null) {
                for (IBlockState replaceableState : replaceableStates) {
                    if (isSameState(state, replaceableState)) {
                        return true;
                    }
                }
                return false;
            }

            Block block = state.getBlock();
            for (Block replaceableBlock : replaceableBlocks) {
                if (block == replaceableBlock) {
                    return true;
                }
            }
            return false;
        }

        private boolean isSameState(IBlockState first, IBlockState second) {
            return first.getBlock() == second.getBlock()
                && first.getBlock().getMetaFromState(first) == second.getBlock().getMetaFromState(second);
        }

        private String getOreName() {
            if (oreName == null) {
                oreName = oreState.getBlock().getRegistryName().getPath();
            }
            return oreName;
        }
    }
}

