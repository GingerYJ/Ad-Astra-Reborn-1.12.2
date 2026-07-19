package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.Random;

/** 1.12.2 tree replacement for the configured Ad Astra tree growers. */
public final class AdAstraSaplingBlock extends BlockSapling {

    private final boolean glacian;

    public AdAstraSaplingBlock(boolean glacian) {
        super();
        this.glacian = glacian;
        setCreativeTab(earth.terrarium.adastra.common.AdAstraCreativeTab.INSTANCE);
        setHardness(0.0F);
        setTickRandomly(true);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return canStayOn(world, pos);
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        return canStayOn(world, pos);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
    }

    @Override
    public void generateTree(World world, BlockPos pos, IBlockState state, Random random) {
        int height = glacian ? 5 + random.nextInt(4) : 5 + random.nextInt(3);
        if (!hasRoom(world, pos, height)) {
            return;
        }

        Block log = glacian ? ModBlocks.GLACIAN_LOG : ModBlocks.get("centaurian_oak_log");
        Block leaves = glacian ? ModBlocks.GLACIAN_LEAVES : ModBlocks.get("centaurian_oak_leaves");
        if (log == null || leaves == null) {
            return;
        }

        world.setBlockToAir(pos);
        for (int y = 0; y < height; y++) {
            world.setBlockState(pos.up(y), log.getDefaultState(), 3);
        }

        int foliageBase = glacian ? height - 2 : height - 3;
        int foliageTop = glacian ? height + 2 : height;
        int radius = glacian ? 4 : 2;
        for (int y = foliageBase; y <= foliageTop; y++) {
            int layerRadius = y == foliageTop ? Math.max(1, radius - 1) : radius;
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    if (dx * dx + dz * dz > layerRadius * layerRadius + 1) {
                        continue;
                    }
                    BlockPos leafPos = pos.add(dx, y, dz);
                    if (world.getBlockState(leafPos).getBlock().isReplaceable(world, leafPos)
                        || world.getBlockState(leafPos).getBlock() == this
                        || world.getBlockState(leafPos).getBlock() == leaves) {
                        world.setBlockState(leafPos, leaves.getDefaultState(), 3);
                    }
                }
            }
        }
    }

    private boolean canStayOn(World world, BlockPos pos) {
        IBlockState belowState = world.getBlockState(pos.down());
        Block below = belowState.getBlock();
        return below != Blocks.AIR && below != Blocks.BEDROCK && belowState.getMaterial() != Material.WATER
            && belowState.getMaterial() != Material.LAVA;
    }

    private boolean hasRoom(World world, BlockPos pos, int height) {
        int radius = glacian ? 4 : 2;
        for (int y = 0; y <= height + 2; y++) {
            int layerRadius = y < height - 2 ? 0 : radius;
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    BlockPos check = pos.add(dx, y, dz);
                    Block block = world.getBlockState(check).getBlock();
                    if (!block.isReplaceable(world, check) && block != this) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
