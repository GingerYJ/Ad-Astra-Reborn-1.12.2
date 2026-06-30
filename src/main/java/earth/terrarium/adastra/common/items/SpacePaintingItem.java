package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.entities.misc.SpacePaintingEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SpacePaintingItem extends Item {

    public SpacePaintingItem(String name) {
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(64);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
            return EnumActionResult.FAIL;
        }

        IBlockState state = world.getBlockState(pos);
        BlockPos placePos = state.getBlock().isReplaceable(world, pos) ? pos : pos.offset(facing);
        ItemStack stack = player.getHeldItem(hand);
        if (!player.canPlayerEdit(placePos, facing, stack)) {
            return EnumActionResult.FAIL;
        }

        SpacePaintingEntity painting = createPainting(world, placePos, facing);
        if (painting == null) {
            return EnumActionResult.FAIL;
        }

        if (!world.isRemote) {
            painting.playPlaceSound();
            world.spawnEntity(painting);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        return EnumActionResult.SUCCESS;
    }

    private SpacePaintingEntity createPainting(World world, BlockPos pos, EnumFacing facing) {
        List<SpacePaintingEntity> validPaintings = new ArrayList<>();
        int maxArea = 0;
        for (SpacePaintingEntity.Variant variant : SpacePaintingEntity.Variant.values()) {
            SpacePaintingEntity painting = new SpacePaintingEntity(world, pos, facing, variant);
            if (painting.onValidSurface()) {
                int area = variant.getArea();
                if (area > maxArea) {
                    validPaintings.clear();
                    maxArea = area;
                }
                if (area == maxArea) {
                    validPaintings.add(painting);
                }
            }
        }
        return validPaintings.isEmpty() ? null : validPaintings.get(world.rand.nextInt(validPaintings.size()));
    }
}
