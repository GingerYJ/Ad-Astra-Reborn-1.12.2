package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class AdAstraStructureWorldGenerator implements IWorldGenerator {

    private static final ResourceLocation OIL_WELL_TEMPLATE = new ResourceLocation(Reference.MOD_ID, "oil");
    private static final int OIL_WELL_SPACING = 20;
    private static final int OIL_WELL_SEPARATION = 14;
    private static final int OIL_WELL_SALT = 1922601463;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
                         IChunkProvider chunkProvider) {
        if (!(world instanceof WorldServer) || world.provider.getDimension() != 0) {
            return;
        }
        generateOilWell((WorldServer) world, chunkX, chunkZ);
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

    private Template getTemplate(WorldServer world, ResourceLocation location) {
        TemplateManager manager = world.getStructureTemplateManager();
        Template template = manager.get(null, location);
        if (template != null) {
            return template;
        }
        return readDataTemplate(world, location);
    }

    private Template readDataTemplate(WorldServer world, ResourceLocation location) {
        String path = "/data/" + location.getNamespace() + "/structures/" + location.getPath() + ".nbt";
        try (InputStream stream = AdAstraStructureWorldGenerator.class.getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }

            NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
            tag = world.getMinecraftServer().getDataFixer().process(FixTypes.STRUCTURE, tag);
            Template template = new Template();
            template.read(tag);
            return template;
        } catch (IOException exception) {
            return null;
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
