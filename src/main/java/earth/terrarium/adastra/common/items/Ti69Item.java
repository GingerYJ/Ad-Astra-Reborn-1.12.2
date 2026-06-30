package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.blocks.AdAstraMachineBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import earth.terrarium.adastra.common.util.EnvironmentUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

public class Ti69Item extends Item {

    private static final float EARTH_GRAVITY = 9.8f;

    public Ti69Item(String name) {
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            EnvironmentReading reading = EnvironmentReading.read(player);
            player.sendStatusMessage(new TextComponentTranslation(
                "message.ad_astra.ti_69.environment",
                localizeBoolean(reading.oxygen),
                localizeTemperature(reading.hasTemperature, reading.temperature),
                localizeGravity(reading.hasGravity, reading.gravity)), false);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(new TextComponentTranslation("info.ad_astra.ti_69").getFormattedText());
    }

    private static TextComponentTranslation localizeBoolean(boolean value) {
        return new TextComponentTranslation(value ? "message.ad_astra.ti_69.available" : "message.ad_astra.ti_69.unavailable");
    }

    private static TextComponentTranslation localizeTemperature(boolean hasTemperature, int temperature) {
        return hasTemperature
            ? new TextComponentTranslation("text.ad_astra.temperature", String.valueOf(temperature))
            : new TextComponentTranslation("hud.ad_astra.none");
    }

    private static TextComponentTranslation localizeGravity(boolean hasGravity, float gravity) {
        return hasGravity
            ? new TextComponentTranslation("text.ad_astra.gravity", String.format(Locale.ROOT, "%.1f", gravity))
            : new TextComponentTranslation("hud.ad_astra.none");
    }

    private static boolean isMachineLit(IBlockState state) {
        return state.getPropertyKeys().contains(AdAstraMachineBlock.LIT) && state.getValue(AdAstraMachineBlock.LIT);
    }

    private static boolean reachesOxygenDistributor(World world, BlockPos distributorPos, BlockPos playerPos) {
        TileEntity tile = world.getTileEntity(distributorPos);
        if (tile instanceof OxygenDistributorTileEntity) {
            int radius = Math.max(1, ((OxygenDistributorTileEntity) tile).getWorkingRadius());
            return distributorPos.distanceSq(playerPos) <= radius * radius;
        }
        return true;
    }

    private static boolean reachesGravityNormalizer(World world, BlockPos normalizerPos, BlockPos playerPos) {
        TileEntity tile = world.getTileEntity(normalizerPos);
        if (tile instanceof GravityNormalizerTileEntity) {
            int radius = Math.max(1, ((GravityNormalizerTileEntity) tile).getWorkingRadius());
            return normalizerPos.distanceSq(playerPos) <= radius * radius;
        }
        return true;
    }

    private static float getTargetGravity(World world, BlockPos normalizerPos) {
        TileEntity tile = world.getTileEntity(normalizerPos);
        if (tile instanceof GravityNormalizerTileEntity) {
            return ((GravityNormalizerTileEntity) tile).getTargetGravity();
        }
        return 1.0f;
    }

    private static Number invokeNumber(Object target, String methodName) {
        Object value = invokeNoArg(target, methodName);
        return value instanceof Number ? (Number) value : null;
    }

    private static Object invokeNoArg(Object target, String methodName) {
        if (target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | SecurityException e) {
            return null;
        }
    }

    private static class EnvironmentReading {

        private final boolean oxygen;
        private final boolean hasTemperature;
        private final int temperature;
        private final boolean hasGravity;
        private final float gravity;

        private EnvironmentReading(boolean oxygen, boolean hasTemperature, int temperature, boolean hasGravity, float gravity) {
            this.oxygen = oxygen;
            this.hasTemperature = hasTemperature;
            this.temperature = temperature;
            this.hasGravity = hasGravity;
            this.gravity = gravity;
        }

        private static EnvironmentReading read(EntityPlayer player) {
            World world = player.world;
            Object provider = world.provider;
            Number temperature = invokeNumber(provider, "getTemperature");
            Number gravity = invokeNumber(provider, "getGravity");

            boolean oxygen = EnvironmentUtils.hasOxygen(player);
            boolean hasGravity = gravity != null;
            float gravityValue = gravity == null ? EARTH_GRAVITY : gravity.floatValue();

            BlockPos playerPos = player.getPosition();
            BlockPos min = playerPos.add(
                -EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS,
                -EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS,
                -EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS);
            BlockPos max = playerPos.add(
                EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS,
                EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS,
                EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS);

            for (BlockPos mutablePos : BlockPos.getAllInBoxMutable(min, max)) {
                if (!world.isBlockLoaded(mutablePos)) {
                    continue;
                }
                IBlockState state = world.getBlockState(mutablePos);
                Block block = state.getBlock();
                if (block == ModBlocks.OXYGEN_DISTRIBUTOR && isMachineLit(state) && reachesOxygenDistributor(world, mutablePos, playerPos)) {
                    oxygen = true;
                } else if (block == ModBlocks.GRAVITY_NORMALIZER && isMachineLit(state) && reachesGravityNormalizer(world, mutablePos, playerPos)) {
                    hasGravity = true;
                    gravityValue = getTargetGravity(world, mutablePos) * EARTH_GRAVITY;
                }

                if (oxygen && hasGravity) {
                    break;
                }
            }

            return new EnvironmentReading(
                oxygen,
                temperature != null,
                temperature == null ? 0 : temperature.intValue(),
                hasGravity,
                gravityValue);
        }
    }
}
