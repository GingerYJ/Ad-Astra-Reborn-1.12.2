package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.function.Function;

public class AdAstraSpawnEggItem extends Item {

    private final Function<World, ? extends EntityLivingBase> factory;

    public AdAstraSpawnEggItem(String name, Function<World, ? extends EntityLivingBase> factory) {
        this.factory = factory;
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(64);
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

        if (!world.isBlockLoaded(spawnPos) || (!world.isAirBlock(spawnPos) && !world.getBlockState(spawnPos).getBlock().isReplaceable(world, spawnPos))) {
            return EnumActionResult.FAIL;
        }

        if (!world.isRemote) {
            EntityLivingBase entity = factory.apply(world);
            entity.setLocationAndAngles(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                player.rotationYaw,
                0.0F);

            if (entity instanceof EntityLiving) {
                EntityLiving living = (EntityLiving) entity;
                DifficultyInstance difficulty = world.getDifficultyForLocation(spawnPos);
                living.onInitialSpawn(difficulty, (IEntityLivingData) null);
            }

            world.spawnEntity(entity);
            if (!player.capabilities.isCreativeMode) {
                player.getHeldItem(hand).shrink(1);
            }
        }

        return EnumActionResult.SUCCESS;
    }
}
