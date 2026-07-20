package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class VehicleItem extends Item {

    private final Function<World, ? extends AdAstraVehicleEntity> factory;
    private final int rocketTier;

    public VehicleItem(String name, Function<World, ? extends AdAstraVehicleEntity> factory) {
        this(name, factory, true);
    }

    /** Creates a vehicle with a canonical prefixed registry ID while retaining its tiered item name. */
    public VehicleItem(String name, ResourceLocation registryName, String translationKey,
                       Function<World, ? extends AdAstraVehicleEntity> factory) {
        this.factory = factory;
        this.rocketTier = rocketTierOf(name);
        if (registryName != null) {
            setRegistryName(registryName);
        }
        if (translationKey != null) {
            setTranslationKey(translationKey);
        }
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(1);
    }

    protected VehicleItem(String name, Function<World, ? extends AdAstraVehicleEntity> factory,
                          boolean registerName) {
        this(name,
            registerName ? new ResourceLocation(Reference.MOD_ID, name) : null,
            registerName ? Reference.MOD_ID + "." + name : null,
            factory);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        addRocketTierTooltip(tooltip, rocketTier);
    }

    protected final void addRocketTierTooltip(List<String> tooltip, int tier) {
        if (tier > 0) {
            tooltip.add(I18n.translateToLocalFormatted("tooltip.ad_astra.configurable_rocket.tier", tier));
        }
    }

    private static int rocketTierOf(String name) {
        if (!name.startsWith("tier_") || !name.endsWith("_rocket")) {
            return 0;
        }

        try {
            return Integer.parseInt(name.substring("tier_".length(), name.length() - "_rocket".length()));
        } catch (NumberFormatException ignored) {
            return 0;
        }
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
        if (!world.isRemote) {
            AdAstraVehicleEntity vehicle = factory.apply(world);
            BlockPos spawnPos;
            double spawnY;
            if (vehicle instanceof RocketEntity) {
                BlockPos center = LaunchPadBlock.findLaunchPadCenter(world, pos);
                if (center == null) {
                    return EnumActionResult.FAIL;
                }
                ((RocketEntity) vehicle).loadFromItemStack(player.getHeldItem(hand));
                spawnPos = center;
                spawnY = center.getY() + 0.125D;
            } else {
                IBlockState state = world.getBlockState(pos);
                spawnPos = state.getBlock().isReplaceable(world, pos) ? pos : pos.offset(facing);
                if (!world.isAirBlock(spawnPos) && !world.getBlockState(spawnPos).getBlock().isReplaceable(world, spawnPos)) {
                    return EnumActionResult.FAIL;
                }
                spawnY = spawnPos.getY() + 0.05D;
            }

            if (!world.getCollisionBoxes(vehicle, vehicle.getEntityBoundingBox().offset(
                spawnPos.getX() + 0.5D - vehicle.posX,
                spawnY - vehicle.posY,
                spawnPos.getZ() + 0.5D - vehicle.posZ)).isEmpty()) {
                return EnumActionResult.FAIL;
            }

            vehicle.setLocationAndAngles(
                spawnPos.getX() + 0.5D,
                spawnY,
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
