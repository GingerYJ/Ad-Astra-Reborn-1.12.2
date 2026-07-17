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
import net.minecraft.util.ResourceLocation;
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
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AdAstraChunkGenerator implements IChunkGenerator {

    public static final int SURFACE_Y = 63;
    public static final int SPAWN_Y = SURFACE_Y + 1;

    private static final int MIN_GENERATION_Y = 1;
    private static final Map<String, List<PlanetOreSpec>> ORE_SPECS_CACHE = new HashMap<>();

    private final World world;
    private final PlanetDimensionProperties properties;
    private final PlanetTerrainConfig terrainConfig;
    private final CraterConfig craterConfig;
    private final AdAstraNoiseGenerator heightNoise;
    private final AdAstraNoiseGenerator caveNoise;

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
                for (int y = 1; y <= 255; y++) {
                    if (y > terrainHeight) {
                        // Air above terrain
                        continue;
                    }

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
        Random random = createChunkRandom(chunkX, chunkZ);
        List<PlanetOreSpec> specs = getOreSpecs(properties.getName());
        BlockPos chunkOrigin = new BlockPos(chunkX * 16, 0, chunkZ * 16);
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

        int oreCount = 0;
        for (PlanetOreSpec spec : specs) {
            int generated = generateOre(spec, chunkOrigin, random);
            oreCount += generated;

            if (OreGenConfig.debugWorldgen && generated > 0) {
                AdAstraReborn.LOGGER.info("Generated {} {} ore veins in chunk [{}, {}] at Y:{}-{}",
                    generated, spec.getOreName(), chunkPos.x, chunkPos.z, spec.minY, spec.maxY);
            }
        }

        if (OreGenConfig.debugWorldgen && oreCount > 0) {
            AdAstraReborn.LOGGER.debug("Total {} ore veins generated in chunk [{}, {}] on {}",
                oreCount, chunkPos.x, chunkPos.z, properties.getName());
        }

        generateSimpleFeatures(chunkOrigin, random);
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

    private int generateOre(PlanetOreSpec spec, BlockPos chunkOrigin, Random random) {
        int generated = 0;
        int maxGenerationY = terrainConfig.getMaxHeight() - 3; // Stay below surface

        for (int i = 0; i < spec.countPerChunk; i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int y = spec.minY + random.nextInt(Math.min(spec.maxY, maxGenerationY) - spec.minY + 1);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            if (generateVein(spec, random, new BlockPos(x, y, z))) {
                generated++;
            }
        }
        return generated;
    }

    private boolean generateVein(PlanetOreSpec spec, Random random, BlockPos origin) {
        float angle = random.nextFloat() * (float) Math.PI;
        double startX = (double) origin.getX() + 8.0D + (double) (MathHelper.sin(angle) * (float) spec.veinSize) / 8.0D;
        double endX = (double) origin.getX() + 8.0D - (double) (MathHelper.sin(angle) * (float) spec.veinSize) / 8.0D;
        double startZ = (double) origin.getZ() + 8.0D + (double) (MathHelper.cos(angle) * (float) spec.veinSize) / 8.0D;
        double endZ = (double) origin.getZ() + 8.0D - (double) (MathHelper.cos(angle) * (float) spec.veinSize) / 8.0D;
        double startY = (double) origin.getY() + random.nextInt(3) - 2;
        double endY = (double) origin.getY() + random.nextInt(3) - 2;

        boolean generated = false;
        int maxGenerationY = terrainConfig.getMaxHeight() - 2;

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
                            if (replaceOreBlock(spec, new BlockPos(x, y, z))) {
                                generated = true;
                            }
                        }
                    }
                }
            }
        }

        return generated;
    }

    private boolean replaceOreBlock(PlanetOreSpec spec, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) {
            return false;
        }
        IBlockState current = world.getBlockState(pos);
        if (spec.canReplace(current)) {
            world.setBlockState(pos, spec.oreState, 2);
            return true;
        }
        return false;
    }

    private void generateSimpleFeatures(BlockPos chunkOrigin, Random random) {
        String planetName = properties.getName();
        if ("moon".equals(planetName)) {
            generateMoonSoulSand(chunkOrigin, random);
        } else if ("mars".equals(planetName)) {
            generateMarsRockBlobs(chunkOrigin, random);
        } else if ("venus".equals(planetName)) {
            generateInfernalSpireColumns(chunkOrigin, random);
        }
    }

    private void generateMoonSoulSand(BlockPos chunkOrigin, Random random) {
        PlanetOreSpec soulSand = new PlanetOreSpec(
            "moon",
            Blocks.SOUL_SAND.getDefaultState(),
            60,
            20,
            MIN_GENERATION_Y,
            terrainConfig.getMaxHeight() - 5,
            new Block[]{ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE},
            null
        );
        generateOre(soulSand, chunkOrigin, random);
    }

    private void generateMarsRockBlobs(BlockPos chunkOrigin, Random random) {
        for (int i = 0; i < 2; i++) {
            int x = chunkOrigin.getX() + random.nextInt(16);
            int z = chunkOrigin.getZ() + random.nextInt(16);
            int searchStartY = terrainConfig.getMaxHeight() + 8;
            BlockPos surface = findSurface(new BlockPos(x, searchStartY, z), ModBlocks.MARS_SAND);
            if (surface != null) {
                generateMarsRockBlob(surface.up(), random);
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

    private void generateMarsRockBlob(BlockPos origin, Random random) {
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
                        if (pos.distanceSq(center) <= (double) (radius * radius)) {
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
                        crater.carveInPrimer(primer, chunkX, chunkZ, airState);
                    }
                }
            }
        }
    }

    /**
     * Generate list of craters for a specific chunk using deterministic random.
     */
    private List<CraterCarver> generateCratersForChunk(int chunkX, int chunkZ) {
        List<CraterCarver> craters = new ArrayList<>();
        Random random = createChunkRandom(chunkX, chunkZ);

        // Use separate seed for craters
        random.setSeed(random.nextLong() + 12345);

        // Check if this chunk should have craters
        if (random.nextDouble() > craterConfig.getCraterChance()) {
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

        return craters;
    }

    private List<PlanetOreSpec> getOreSpecs(String planetName) {
        // Cache ore specs per planet for performance
        if (!ORE_SPECS_CACHE.containsKey(planetName)) {
            List<PlanetOreSpec> specs = createOreSpecs(planetName);
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

    private List<PlanetOreSpec> createOreSpecs(String planetName) {
        List<PlanetOreSpec> specs = new ArrayList<>();

        switch (planetName) {
            case "moon":
                addConfiguredOre(specs, "moon", ModBlocks.MOON_CHEESE_ORE,
                    OreGenConfig.moonCheeseVeinSize, OreGenConfig.moonCheeseCount,
                    OreGenConfig.moonCheeseMinY, OreGenConfig.moonCheeseMaxY,
                    ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE);
                addConfiguredOre(specs, "moon", ModBlocks.MOON_DESH_ORE,
                    OreGenConfig.moonDeshVeinSize, OreGenConfig.moonDeshCount,
                    OreGenConfig.moonDeshMinY, OreGenConfig.moonDeshMaxY,
                    ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE);
                addConfiguredOre(specs, "moon", ModBlocks.MOON_ICE_SHARD_ORE,
                    OreGenConfig.moonIceShardVeinSize, OreGenConfig.moonIceShardCount,
                    OreGenConfig.moonIceShardMinY, OreGenConfig.moonIceShardMaxY,
                    ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE);
                addConfiguredOre(specs, "moon", ModBlocks.MOON_IRON_ORE,
                    OreGenConfig.moonIronVeinSize, OreGenConfig.moonIronCount,
                    OreGenConfig.moonIronMinY, OreGenConfig.moonIronMaxY,
                    ModBlocks.MOON_STONE, ModBlocks.MOON_DEEPSLATE);
                break;

            case "mars":
                addConfiguredOre(specs, "mars", ModBlocks.MARS_DIAMOND_ORE,
                    OreGenConfig.marsDiamondVeinSize, OreGenConfig.marsDiamondCount,
                    OreGenConfig.marsDiamondMinY, OreGenConfig.marsDiamondMaxY,
                    ModBlocks.MARS_STONE);
                addConfiguredOre(specs, "mars", ModBlocks.MARS_ICE_SHARD_ORE,
                    OreGenConfig.marsIceShardVeinSize, OreGenConfig.marsIceShardCount,
                    OreGenConfig.marsIceShardMinY, OreGenConfig.marsIceShardMaxY,
                    ModBlocks.MARS_STONE);
                addConfiguredOre(specs, "mars", ModBlocks.MARS_IRON_ORE,
                    OreGenConfig.marsIronVeinSize, OreGenConfig.marsIronCount,
                    OreGenConfig.marsIronMinY, OreGenConfig.marsIronMaxY,
                    ModBlocks.MARS_STONE);
                addConfiguredOre(specs, "mars", ModBlocks.MARS_OSTRUM_ORE,
                    OreGenConfig.marsOstrumVeinSize, OreGenConfig.marsOstrumCount,
                    OreGenConfig.marsOstrumMinY, OreGenConfig.marsOstrumMaxY,
                    ModBlocks.MARS_STONE);
                break;

            case "mercury":
                addConfiguredOre(specs, "mercury", ModBlocks.MERCURY_IRON_ORE,
                    OreGenConfig.mercuryIronVeinSize, OreGenConfig.mercuryIronCount,
                    OreGenConfig.mercuryIronMinY, OreGenConfig.mercuryIronMaxY,
                    ModBlocks.MERCURY_STONE);
                break;

            case "venus":
                addConfiguredOre(specs, "venus", ModBlocks.VENUS_CALORITE_ORE,
                    OreGenConfig.venusCaloriteVeinSize, OreGenConfig.venusCaloriteCount,
                    OreGenConfig.venusCaloriteMinY, OreGenConfig.venusCaloriteMaxY,
                    ModBlocks.VENUS_STONE);
                addConfiguredOre(specs, "venus", ModBlocks.VENUS_COAL_ORE,
                    OreGenConfig.venusCoalVeinSize, OreGenConfig.venusCoalCount,
                    OreGenConfig.venusCoalMinY, OreGenConfig.venusCoalMaxY,
                    ModBlocks.VENUS_STONE);
                addConfiguredOre(specs, "venus", ModBlocks.VENUS_DIAMOND_ORE,
                    OreGenConfig.venusDiamondVeinSize, OreGenConfig.venusDiamondCount,
                    OreGenConfig.venusDiamondMinY, OreGenConfig.venusDiamondMaxY,
                    ModBlocks.VENUS_STONE);
                addConfiguredOre(specs, "venus", ModBlocks.VENUS_GOLD_ORE,
                    OreGenConfig.venusGoldVeinSize, OreGenConfig.venusGoldCount,
                    OreGenConfig.venusGoldMinY, OreGenConfig.venusGoldMaxY,
                    ModBlocks.VENUS_STONE);
                break;

            case "glacio":
                addConfiguredOre(specs, "glacio", ModBlocks.GLACIO_COAL_ORE,
                    OreGenConfig.glacioCoalVeinSize, OreGenConfig.glacioCoalCount,
                    OreGenConfig.glacioCoalMinY, OreGenConfig.glacioCoalMaxY,
                    ModBlocks.GLACIO_STONE);
                addConfiguredOre(specs, "glacio", ModBlocks.GLACIO_ICE_SHARD_ORE,
                    OreGenConfig.glacioIceShardVeinSize, OreGenConfig.glacioIceShardCount,
                    OreGenConfig.glacioIceShardMinY, OreGenConfig.glacioIceShardMaxY,
                    ModBlocks.GLACIO_STONE);
                addConfiguredOre(specs, "glacio", ModBlocks.GLACIO_IRON_ORE,
                    OreGenConfig.glacioIronVeinSize, OreGenConfig.glacioIronCount,
                    OreGenConfig.glacioIronMinY, OreGenConfig.glacioIronMaxY,
                    ModBlocks.GLACIO_STONE);
                addConfiguredOre(specs, "glacio", ModBlocks.GLACIO_LAPIS_ORE,
                    OreGenConfig.glacioLapisVeinSize, OreGenConfig.glacioLapisCount,
                    OreGenConfig.glacioLapisMinY, OreGenConfig.glacioLapisMaxY,
                    ModBlocks.GLACIO_STONE);
                break;

            default:
                break;
        }

        addCustomConfiguredBlocks(specs, planetName);
        return specs;
    }


    private void addCustomConfiguredBlocks(List<PlanetOreSpec> specs, String planetName) {
        for (OreGenConfig.CustomBlockSettings settings : OreGenConfig.getCustomBlockSettings(planetName)) {
            IBlockState targetState = parseConfiguredBlockState(settings.blockState);
            if (targetState == null) {
                if (OreGenConfig.debugWorldgen) {
                    AdAstraReborn.LOGGER.warn("Ignored custom planet block generator on {}: invalid target block '{}'",
                        planetName, settings.blockState);
                }
                continue;
            }

            IBlockState[] replaceableStates = parseReplaceableStates(settings.replaceTargets);
            if (replaceableStates.length == 0) {
                replaceableStates = defaultCustomReplaceableStates();
            }

            addConfiguredOre(specs, settings.planetName, targetState,
                settings.veinSize, settings.countPerChunk, settings.minY, settings.maxY, replaceableStates);
        }
    }

    private IBlockState[] parseReplaceableStates(String replaceTargets) {
        if (replaceTargets == null || replaceTargets.trim().isEmpty() || "default".equalsIgnoreCase(replaceTargets.trim())) {
            return defaultCustomReplaceableStates();
        }
        String[] parts = replaceTargets.split(",");
        List<IBlockState> states = new ArrayList<>();
        for (String part : parts) {
            IBlockState state = parseConfiguredBlockState(part.trim());
            if (state != null) {
                states.add(state);
            }
        }
        return states.toArray(new IBlockState[0]);
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

    private static IBlockState parseConfiguredBlockState(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || "default".equalsIgnoreCase(trimmed)) {
            return null;
        }
        int meta = 0;
        int metaSeparator = trimmed.lastIndexOf('@');
        if (metaSeparator >= 0) {
            String metaText = trimmed.substring(metaSeparator + 1).trim();
            trimmed = trimmed.substring(0, metaSeparator).trim();
            try {
                meta = Integer.parseInt(metaText);
            } catch (NumberFormatException ignored) {
                meta = 0;
            }
        }
        if (trimmed.indexOf(':') < 0) {
            trimmed = "minecraft:" + trimmed;
        }
        try {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(trimmed));
            if (block == null || block == Blocks.AIR) {
                return null;
            }
            return block.getStateFromMeta(meta);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static IBlockState state(Block block, int meta) {
        return block.getStateFromMeta(meta);
    }

    private void addConfiguredOre(List<PlanetOreSpec> specs, String planetName, Block oreBlock,
                                  OreGenConfig.OreSettings settings,
                                  Block... replaceableBlocks) {
        addConfiguredOre(specs, planetName, oreBlock, settings.veinSize, settings.countPerChunk, settings.minY, settings.maxY, replaceableBlocks);
    }

    private void addConfiguredOre(List<PlanetOreSpec> specs, String planetName, IBlockState oreState,
                                  OreGenConfig.OreSettings settings,
                                  IBlockState... replaceableStates) {
        addConfiguredOre(specs, planetName, oreState, settings.veinSize, settings.countPerChunk, settings.minY, settings.maxY, replaceableStates);
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

