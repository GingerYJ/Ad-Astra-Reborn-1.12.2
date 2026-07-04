package earth.terrarium.adastra.common.tags;

import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 1.12.2 stand-in for 1.20.x block tags used by environment systems.
 */
public final class ModBlockTags {

    public static final Set<Block> PASSES_FLOOD_FILL = new HashSet<>();
    public static final Set<Block> BLOCKS_FLOOD_FILL = new HashSet<>();
    public static final Set<Block> DESTROYED_IN_SPACE = new HashSet<>();
    private static boolean defaultsRegistered;

    private ModBlockTags() {
    }

    public static void addPassesFloodFill(Block... blocks) {
        Collections.addAll(PASSES_FLOOD_FILL, blocks);
    }

    public static void addBlocksFloodFill(Block... blocks) {
        Collections.addAll(BLOCKS_FLOOD_FILL, blocks);
    }

    public static void addPassesFloodFill(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            Block block = ForgeRegistries.BLOCKS.getValue(id);
            if (block != null) {
                PASSES_FLOOD_FILL.add(block);
            }
        }
    }

    public static void addBlocksFloodFill(ResourceLocation... ids) {
        for (ResourceLocation id : ids) {
            Block block = ForgeRegistries.BLOCKS.getValue(id);
            if (block != null) {
                BLOCKS_FLOOD_FILL.add(block);
            }
        }
    }

    public static void registerDefaults() {
        if (defaultsRegistered) {
            return;
        }
        defaultsRegistered = true;

        for (Block block : ForgeRegistries.BLOCKS) {
            if (block instanceof BlockFence
                || block instanceof BlockLadder
                || block instanceof BlockLeaves
                || block.getDefaultState().getMaterial() == Material.LEAVES) {
                PASSES_FLOOD_FILL.add(block);
            }
        }

        addPassesFloodFill(
            Blocks.IRON_BARS,
            Blocks.TNT,
            ModBlocks.VENT,
            ModBlocks.OXYGEN_DISTRIBUTOR,
            ModBlocks.GRAVITY_NORMALIZER
        );

        registerDestroyedInSpaceDefaults();
    }

    private static void registerDestroyedInSpaceDefaults() {
        for (Block block : ForgeRegistries.BLOCKS) {
            if (block instanceof BlockSapling
                || block instanceof BlockLeaves
                || block instanceof BlockCrops
                || block instanceof BlockBush
                || block instanceof BlockVine
                || block instanceof BlockTorch
                || block instanceof BlockMushroom
                || block instanceof BlockCocoa
                || block.getDefaultState().getMaterial() == Material.LEAVES
                || block.getDefaultState().getMaterial() == Material.PLANTS
                || block.getDefaultState().getMaterial() == Material.VINE) {
                DESTROYED_IN_SPACE.add(block);
            }
        }

        Collections.addAll(DESTROYED_IN_SPACE,
            Blocks.FIRE,
            Blocks.TORCH,
            Blocks.LIT_PUMPKIN,
            Blocks.VINE,
            Blocks.BROWN_MUSHROOM_BLOCK,
            Blocks.RED_MUSHROOM_BLOCK,
            Blocks.TALLGRASS,
            Blocks.DOUBLE_PLANT
        );
    }
}
