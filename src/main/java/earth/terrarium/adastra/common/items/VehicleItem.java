package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

public class VehicleItem extends Item {

    private final Function<World, ? extends AdAstraVehicleEntity> factory;

    public VehicleItem(String name, Function<World, ? extends AdAstraVehicleEntity> factory) {
        this.factory = factory;
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(
        EntityPlayer player,
        World world,
        BlockPos pos,
        EnumHand hand,
        EnumFacing facing,
        float hitX,
        float hitY,
        float hitZ) {
        IBlockState state = world.getBlockState(pos);
        BlockPos spawnPos = state.getBlock().isReplaceable(world, pos) ? pos : pos.offset(facing);
        if (!world.isAirBlock(spawnPos) && !world.getBlockState(spawnPos).getBlock().isReplaceable(world, spawnPos)) {
            return EnumActionResult.FAIL;
        }

        if (!world.isRemote) {
            AdAstraVehicleEntity vehicle = factory.apply(world);
            vehicle.setLocationAndAngles(
                spawnPos.getX() + 0.5D,
                spawnPos.getY() + 0.05D,
                spawnPos.getZ() + 0.5D,
                player.rotationYaw,
                0.0F);
            world.spawnEntity(vehicle);
            ItemStack stack = player.getHeldItem(hand);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        return EnumActionResult.SUCCESS;
    }
}
