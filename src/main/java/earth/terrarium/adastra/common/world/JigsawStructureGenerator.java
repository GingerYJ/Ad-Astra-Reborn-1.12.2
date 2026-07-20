package earth.terrarium.adastra.common.world;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

/**
 * Greedy jigsaw structure assembler for 1.12.2, porting the essentials of Ad Astra 1.20's
 * data-driven jigsaw placement (moon dungeon). Start from the entrance piece, then breadth-first
 * connect pieces at matching jigsaw connectors (opposite facings) picked from weighted
 * {@link JigsawPools}, until a piece budget is exhausted. Bounding boxes prevent overlaps.
 *
 * 1.12.2 has no {@code minecraft:jigsaw} block, so connectors are parsed straight from the raw
 * structure NBT (palette orientation + block tile-entity {@code pool}) before the palette is
 * remapped to 1.12 blocks for actual placement.
 */
public final class JigsawStructureGenerator {

    private static final int MAX_CACHED_LAYOUTS = 128;
    private final Map<ResourceLocation, ParsedTemplate> cache = new HashMap<ResourceLocation, ParsedTemplate>();
    private final Map<LayoutKey, List<PlacedPiece>> layoutCache =
        new LinkedHashMap<LayoutKey, List<PlacedPiece>>(MAX_CACHED_LAYOUTS, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<LayoutKey, List<PlacedPiece>> eldest) {
                return size() > MAX_CACHED_LAYOUTS;
            }
        };

    private static final class LayoutKey {
        final long seed;
        final int dimension;
        final int chunkX;
        final int chunkZ;
        final String startPool;
        final int maxPieces;
        final int startYOffset;
        final int maxChunkReach;

        LayoutKey(long seed, int dimension, ChunkPos chunkPos, String startPool,
                  int maxPieces, int startYOffset, int maxChunkReach) {
            this.seed = seed;
            this.dimension = dimension;
            this.chunkX = chunkPos.x;
            this.chunkZ = chunkPos.z;
            this.startPool = startPool;
            this.maxPieces = maxPieces;
            this.startYOffset = startYOffset;
            this.maxChunkReach = maxChunkReach;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof LayoutKey)) {
                return false;
            }
            LayoutKey other = (LayoutKey) obj;
            return seed == other.seed
                && dimension == other.dimension
                && chunkX == other.chunkX
                && chunkZ == other.chunkZ
                && maxPieces == other.maxPieces
                && startYOffset == other.startYOffset
                && maxChunkReach == other.maxChunkReach
                && startPool.equals(other.startPool);
        }

        @Override
        public int hashCode() {
            return Objects.hash(seed, dimension, chunkX, chunkZ, startPool, maxPieces, startYOffset, maxChunkReach);
        }
    }

    /** A structure template plus the jigsaw connectors extracted from its raw NBT. */
    private static final class ParsedTemplate {
        final Template template;
        final BlockPos size;
        final List<Jigsaw> jigsaws;

        ParsedTemplate(Template template, BlockPos size, List<Jigsaw> jigsaws) {
            this.template = template;
            this.size = size;
            this.jigsaws = jigsaws;
        }
    }

    /** A jigsaw connector in template-local coordinates. */
    private static final class Jigsaw {
        final BlockPos pos;
        final EnumFacing facing;
        final String poolName;

        Jigsaw(BlockPos pos, EnumFacing facing, String poolName) {
            this.pos = pos;
            this.facing = facing;
            this.poolName = poolName;
        }
    }

    /** A placed piece with its world transform, kept on the BFS frontier. */
    private static final class PlacedPiece {
        final ParsedTemplate parsed;
        final BlockPos origin;
        final Rotation rotation;
        final int depth;

        PlacedPiece(ParsedTemplate parsed, BlockPos origin, Rotation rotation, int depth) {
            this.parsed = parsed;
            this.origin = origin;
            this.rotation = rotation;
            this.depth = depth;
        }
    }

    public void generateChunkSlice(WorldServer world, ChunkPos structureChunk, ChunkPos targetChunk,
                                   String startPool, int maxPieces, int startYOffset,
                                   int maxChunkReach, Random random) {
        LayoutKey key = new LayoutKey(
            world.getSeed(), world.provider.getDimension(), structureChunk,
            startPool, maxPieces, startYOffset, maxChunkReach);
        List<PlacedPiece> layout = layoutCache.get(key);
        if (layout == null) {
            layout = assembleLayout(
                world, structureChunk, startPool, maxPieces, startYOffset, maxChunkReach, random);
            layoutCache.put(key, layout);
        }

        StructureBoundingBox chunkBox = new StructureBoundingBox(
            targetChunk.x * 16, 1, targetChunk.z * 16,
            targetChunk.x * 16 + 15, world.getHeight(), targetChunk.z * 16 + 15);
        for (PlacedPiece piece : layout) {
            if (boundingBoxOf(piece).intersectsWith(chunkBox)) {
                placeTemplateSlice(world, piece, targetChunk, chunkBox);
            }
        }
    }

    private List<PlacedPiece> assembleLayout(WorldServer world, ChunkPos chunkPos, String startPool,
                                             int maxPieces, int startYOffset,
                                             int maxChunkReach, Random random) {
        ParsedTemplate startTemplate = pickTemplate(world, startPool, random);
        if (startTemplate == null) {
            return java.util.Collections.emptyList();
        }

        int centerX = chunkPos.x * 16 + 8;
        int centerZ = chunkPos.z * 16 + 8;
        int baseY = MathHelper.clamp(
            AdAstraChunkGenerator.SURFACE_Y + 1 + startYOffset,
            5,
            world.getHeight() - 48);
        BlockPos startOrigin = new BlockPos(
            centerX - startTemplate.size.getX() / 2, baseY, centerZ - startTemplate.size.getZ() / 2);

        List<StructureBoundingBox> occupied = new ArrayList<StructureBoundingBox>();
        Queue<PlacedPiece> frontier = new ArrayDeque<PlacedPiece>();
        List<PlacedPiece> layout = new ArrayList<PlacedPiece>();

        PlacedPiece startPiece = new PlacedPiece(startTemplate, startOrigin, Rotation.NONE, 0);
        occupied.add(boundingBoxOf(startPiece));
        frontier.add(startPiece);
        layout.add(startPiece);

        int placed = 1;
        while (!frontier.isEmpty() && placed < maxPieces) {
            PlacedPiece piece = frontier.poll();
            for (Jigsaw jigsaw : piece.parsed.jigsaws) {
                if (placed >= maxPieces) {
                    break;
                }
                PlacedPiece next = tryConnect(world, piece, jigsaw, occupied, random);
                if (next != null && isWithinStructureReach(next, chunkPos, maxChunkReach)) {
                    occupied.add(boundingBoxOf(next));
                    frontier.add(next);
                    layout.add(next);
                    placed++;
                }
            }
        }
        return layout;
    }

    private boolean isWithinStructureReach(PlacedPiece piece, ChunkPos startChunk, int maxChunkReach) {
        StructureBoundingBox box = boundingBoxOf(piece);
        int minX = (startChunk.x - maxChunkReach) * 16;
        int maxX = (startChunk.x + maxChunkReach + 1) * 16 - 1;
        int minZ = (startChunk.z - maxChunkReach) * 16;
        int maxZ = (startChunk.z + maxChunkReach + 1) * 16 - 1;
        return box.minX >= minX && box.maxX <= maxX && box.minZ >= minZ && box.maxZ <= maxZ;
    }

    // placeholder-connect

    /**
     * Attempt to attach a new piece at the given source connector. Picks a candidate from the
     * connector's target pool, finds one of its jigsaws facing the opposite way, computes the
     * rotation and origin that mate the two connectors, and rejects the placement on overlap.
     */
    private PlacedPiece tryConnect(WorldServer world, PlacedPiece source, Jigsaw sourceJigsaw,
                                   List<StructureBoundingBox> occupied, Random random) {
        // World-space position/direction of the source connector.
        EnumFacing sourceWorldFacing = source.rotation.rotate(sourceJigsaw.facing);
        BlockPos sourceWorldPos = transformedPos(sourceJigsaw.pos, source.parsed.size, source.rotation, source.origin);
        // The mating piece must connect on the block just outside the source connector.
        BlockPos attachAt = sourceWorldPos.offset(sourceWorldFacing);
        EnumFacing requiredFacing = sourceWorldFacing.getOpposite();

        JigsawPools.Pool pool = JigsawPools.get(sourceJigsaw.poolName);
        if (pool == null) {
            return null;
        }

        // Try a few weighted candidates before giving up on this connector.
        for (int attempt = 0; attempt < 6; attempt++) {
            JigsawPools.Element element = pool.pick(random);
            if (element == null) {
                break;
            }
            ParsedTemplate candidate = getTemplate(world, element.location);
            if (candidate == null || candidate.jigsaws.isEmpty()) {
                continue;
            }

            // Collect candidate connectors, shuffled, and try to align one to requiredFacing.
            List<Jigsaw> shuffled = new ArrayList<Jigsaw>(candidate.jigsaws);
            java.util.Collections.shuffle(shuffled, random);
            for (Jigsaw candidateJigsaw : shuffled) {
                Rotation rotation = rotationToFace(candidateJigsaw.facing, requiredFacing);
                if (rotation == null) {
                    continue;
                }
                // Origin so that the transformed candidate connector lands on attachAt.
                BlockPos localTransformed = rotateLocal(candidateJigsaw.pos, candidate.size, rotation);
                BlockPos origin = attachAt.subtract(localTransformed);
                PlacedPiece next = new PlacedPiece(candidate, origin, rotation, source.depth + 1);
                StructureBoundingBox box = boundingBoxOf(next);
                if (!intersectsAny(box, occupied)) {
                    return next;
                }
            }
        }
        return null;
    }

    /** Rotation (about Y) that turns {@code from} into {@code to}, or null if not on the horizontal plane. */
    private Rotation rotationToFace(EnumFacing from, EnumFacing to) {
        if (from.getAxis() == EnumFacing.Axis.Y || to.getAxis() == EnumFacing.Axis.Y) {
            return null;
        }
        for (Rotation rotation : Rotation.values()) {
            if (rotation.rotate(from) == to) {
                return rotation;
            }
        }
        return null;
    }

    /**
     * Rotate a local position exactly as {@code Template.transformedBlockPos(pos, NONE, rotation)}
     * does in 1.12.2 (rotation about the origin, mirror none), so our transforms line up with what
     * {@link Template#addBlocksToWorld} produces.
     */
    private BlockPos rotateLocal(BlockPos pos, BlockPos size, Rotation rotation) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
                return new BlockPos(z, y, -x);
            case CLOCKWISE_90:
                return new BlockPos(-z, y, x);
            case CLOCKWISE_180:
                return new BlockPos(-x, y, -z);
            case NONE:
            default:
                return new BlockPos(x, y, z);
        }
    }

    private BlockPos transformedPos(BlockPos local, BlockPos size, Rotation rotation, BlockPos origin) {
        return rotateLocal(local, size, rotation).add(origin);
    }

    // placeholder-place

    private void placeTemplateSlice(WorldServer world, PlacedPiece piece, ChunkPos targetChunk,
                                    StructureBoundingBox chunkBox) {
        PlacementSettings settings = new PlacementSettings()
            .setMirror(Mirror.NONE)
            .setRotation(piece.rotation)
            .setIgnoreEntities(false)
            .setIgnoreStructureBlock(true)
            .setReplacedBlock(Blocks.STRUCTURE_VOID)
            .setChunk(targetChunk)
            .setBoundingBox(chunkBox);
        // Forge invokes IWorldGenerator during chunk population. Template placement must never
        // touch a neighboring chunk here, or that chunk is synchronously loaded and cascades
        // the population pass across the whole structure.
        piece.parsed.template.addBlocksToWorld(
            world, piece.origin, currentChunkProcessor(targetChunk), settings, 2);
    }

    private ITemplateProcessor currentChunkProcessor(final ChunkPos targetChunk) {
        return new ITemplateProcessor() {
            @Override
            public Template.BlockInfo processBlock(
                net.minecraft.world.World world, BlockPos pos, Template.BlockInfo blockInfo) {
                return (pos.getX() >> 4) == targetChunk.x && (pos.getZ() >> 4) == targetChunk.z
                    ? blockInfo : null;
            }
        };
    }

    private StructureBoundingBox boundingBoxOf(PlacedPiece piece) {
        // Template.getBoundingBox honours rotation; build the equivalent AABB from transformed corners.
        BlockPos size = piece.parsed.size;
        BlockPos c1 = rotateLocal(new BlockPos(0, 0, 0), size, piece.rotation).add(piece.origin);
        BlockPos c2 = rotateLocal(new BlockPos(size.getX() - 1, size.getY() - 1, size.getZ() - 1), size, piece.rotation)
            .add(piece.origin);
        return new StructureBoundingBox(
            Math.min(c1.getX(), c2.getX()), Math.min(c1.getY(), c2.getY()), Math.min(c1.getZ(), c2.getZ()),
            Math.max(c1.getX(), c2.getX()), Math.max(c1.getY(), c2.getY()), Math.max(c1.getZ(), c2.getZ()));
    }

    private boolean intersectsAny(StructureBoundingBox box, List<StructureBoundingBox> occupied) {
        // Shrink by 1 so pieces sharing a connector wall are not treated as overlapping.
        StructureBoundingBox shrunk = new StructureBoundingBox(
            box.minX + 1, box.minY + 1, box.minZ + 1, box.maxX - 1, box.maxY - 1, box.maxZ - 1);
        for (StructureBoundingBox other : occupied) {
            if (shrunk.intersectsWith(other)) {
                return true;
            }
        }
        return false;
    }

    private ParsedTemplate pickTemplate(WorldServer world, String poolName, Random random) {
        JigsawPools.Pool pool = JigsawPools.get(poolName);
        if (pool == null) {
            return null;
        }
        JigsawPools.Element element = pool.pick(random);
        return element == null ? null : getTemplate(world, element.location);
    }

    private ParsedTemplate getTemplate(WorldServer world, ResourceLocation location) {
        ParsedTemplate cached = cache.get(location);
        if (cached != null) {
            return cached;
        }

        String path = "/data/" + location.getNamespace() + "/structures/" + location.getPath() + ".nbt";
        try (InputStream stream = JigsawStructureGenerator.class.getResourceAsStream(path)) {
            if (stream == null) {
                cache.put(location, null);
                return null;
            }

            NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
            tag = world.getMinecraftServer().getDataFixer().process(FixTypes.STRUCTURE, tag);

            List<Jigsaw> jigsaws = extractJigsaws(tag);
            AdAstraStructureBlocks.remapPalette(tag);
            AdAstraStructureBlocks.remapStructureData(tag);
            AdAstraStructureBlocks.remapContextLootTables(tag, location);

            Template template = new Template();
            template.read(tag);
            BlockPos size = template.getSize();

            ParsedTemplate parsed = new ParsedTemplate(template, size, jigsaws);
            cache.put(location, parsed);
            return parsed;
        } catch (Exception exception) {
            cache.put(location, null);
            return null;
        }
    }

    /** Parse jigsaw connectors from raw structure NBT before the palette is remapped. */
    private List<Jigsaw> extractJigsaws(NBTTagCompound tag) {
        List<Jigsaw> jigsaws = new ArrayList<Jigsaw>();
        NBTTagList palette = tag.getTagList("palette", 10);
        boolean[] isJigsaw = new boolean[palette.tagCount()];
        EnumFacing[] facings = new EnumFacing[palette.tagCount()];
        for (int i = 0; i < palette.tagCount(); i++) {
            NBTTagCompound state = palette.getCompoundTagAt(i);
            if ("minecraft:jigsaw".equals(state.getString("Name"))) {
                isJigsaw[i] = true;
                String orientation = state.getCompoundTag("Properties").getString("orientation");
                facings[i] = facingFromOrientation(orientation);
            }
        }

        NBTTagList blocks = tag.getTagList("blocks", 10);
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound block = blocks.getCompoundTagAt(i);
            int stateIndex = block.getInteger("state");
            if (stateIndex < 0 || stateIndex >= isJigsaw.length || !isJigsaw[stateIndex]) {
                continue;
            }
            NBTTagList posList = block.getTagList("pos", 3);
            if (posList.tagCount() < 3) {
                continue;
            }
            BlockPos pos = new BlockPos(posList.getIntAt(0), posList.getIntAt(1), posList.getIntAt(2));
            EnumFacing facing = facings[stateIndex];
            if (facing == null) {
                continue;
            }
            String pool = block.getCompoundTag("nbt").getString("pool");
            if (pool.isEmpty()) {
                continue;
            }
            jigsaws.add(new Jigsaw(pos, facing, pool));
        }
        return jigsaws;
    }

    /** jigsaw orientation is "<facing>_<front>"; the first token is the pointing direction. */
    private EnumFacing facingFromOrientation(String orientation) {
        if (orientation == null || orientation.isEmpty()) {
            return null;
        }
        String facing = orientation.contains("_") ? orientation.substring(0, orientation.indexOf('_')) : orientation;
        switch (facing) {
            case "north":
                return EnumFacing.NORTH;
            case "south":
                return EnumFacing.SOUTH;
            case "west":
                return EnumFacing.WEST;
            case "east":
                return EnumFacing.EAST;
            case "up":
                return EnumFacing.UP;
            case "down":
                return EnumFacing.DOWN;
            default:
                return null;
        }
    }
}
