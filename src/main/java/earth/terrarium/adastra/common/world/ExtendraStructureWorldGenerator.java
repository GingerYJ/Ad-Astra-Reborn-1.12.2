package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/** Places the single-element jigsaw structures shipped by Ad Extendra. */
public final class ExtendraStructureWorldGenerator implements IWorldGenerator {

    private static final String NAMESPACE = "ad_extendra";
    private static final int MAX_CHUNK_REACH = 5; // 80 blocks in the source structure definitions.

    private static final StructureSpec[] STRUCTURES = new StructureSpec[] {
        new StructureSpec("b", "b_hut", 20, 6, 1642136474, 0),
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

    private final JigsawStructureGenerator jigsawGenerator = new JigsawStructureGenerator();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!(world instanceof WorldServer) || !(world.provider instanceof AdAstraWorldProvider)) {
            return;
        }

        String planet = planetName(world.provider.getDimension());
        if (planet == null) {
            return;
        }

        WorldServer serverWorld = (WorldServer) world;
        for (StructureSpec structure : STRUCTURES) {
            if (!structure.planet.equals(planet)) {
                continue;
            }
            generateStructure(serverWorld, chunkX, chunkZ, structure);
        }
    }

    private void generateStructure(WorldServer world, int chunkX, int chunkZ, StructureSpec structure) {
        int regionX = floorDiv(chunkX, structure.spacing);
        int regionZ = floorDiv(chunkZ, structure.spacing);
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                generateRegion(world, chunkX, chunkZ, structure, regionX + offsetX, regionZ + offsetZ);
            }
        }
    }

    private void generateRegion(WorldServer world, int chunkX, int chunkZ, StructureSpec structure,
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
        if (Math.abs(chunkX - candidateX) > MAX_CHUNK_REACH
            || Math.abs(chunkZ - candidateZ) > MAX_CHUNK_REACH) {
            return;
        }

        jigsawGenerator.generateChunkSlice(
            world,
            new ChunkPos(candidateX, candidateZ),
            new ChunkPos(chunkX, chunkZ),
            NAMESPACE + ":" + structure.name + "/start_pool",
            2,
            structure.startYOffset,
            MAX_CHUNK_REACH,
            structureRandom);
    }

    private String planetName(int dimension) {
        if (dimension == 1201) {
            return "moon";
        }
        if (dimension == 1203) {
            return "mercury";
        }
        if (dimension == 1204) {
            return "venus";
        }
        if (dimension == 1205) {
            return "glacio";
        }

        CustomPlanetDefinition definition = CustomPlanetRegistry.getByDimensionId(dimension);
        if (definition != null && definition.getId().getNamespace().equals("ad_astra")
            && definition.getId().getPath().startsWith("extendra_")) {
            return definition.getPlanetName();
        }
        return null;
    }

    private int floorDiv(int value, int divisor) {
        int result = value / divisor;
        if ((value ^ divisor) < 0 && result * divisor != value) {
            result--;
        }
        return result;
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
