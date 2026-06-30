package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.AdAstraCreativeTab;
import earth.terrarium.adastra.common.blocks.AdAstraPipeConnection;
import earth.terrarium.adastra.common.registry.ModSounds;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity.SideConfigType;
import earth.terrarium.adastra.common.tile.AdAstraPipeTileEntity;
import earth.terrarium.adastra.common.tile.AdAstraSideMode;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class AdAstraWrenchItem extends Item {

    private static final String TAG_CONFIG_TYPE = "ConfigType";

    public AdAstraWrenchItem(String name) {
        setRegistryName(Reference.MOD_ID, name);
        setTranslationKey(Reference.MOD_ID + "." + name);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof AdAstraPipeTileEntity) {
            if (!world.isRemote) {
                configurePipeSide((AdAstraPipeTileEntity) tile, player, world, pos, facing);
            }
            return EnumActionResult.SUCCESS;
        }

        if (!(tile instanceof AdAstraMachineTileEntity)) {
            return EnumActionResult.PASS;
        }
        if (!world.isRemote) {
            configureMachineSide((AdAstraMachineTileEntity) tile, player, world, pos, facing, player.getHeldItem(hand));
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            SideConfigType next = cycleSelectedConfigType(stack, player.isSneaking());
            player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.wrench.config_type", localizeType(next)), true);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(new TextComponentTranslation("info.ad_astra.wrench").getFormattedText());
        tooltip.add(new TextComponentTranslation("side_config.ad_astra.type.type", localizeType(getSelectedConfigType(stack))).getFormattedText());
    }

    private void configureMachineSide(AdAstraMachineTileEntity machine, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, ItemStack stack) {
        SideConfigType type = getSelectedConfigType(stack);
        AdAstraSideMode current = machine.getSideMode(facing, type);
        AdAstraSideMode next = player.isSneaking() ? current.previous() : current.next();
        machine.setSideMode(facing, type, next);

        world.playSound(null, pos, ModSounds.WRENCH, SoundCategory.BLOCKS, 1.0f, world.rand.nextFloat() * 0.2f + 0.9f);
        player.sendStatusMessage(new TextComponentTranslation(
            "message.ad_astra.wrench.side_config",
            localizeType(type),
            facing.getName(),
            localizeMode(next)), true);
    }

    private SideConfigType cycleSelectedConfigType(ItemStack stack, boolean reverse) {
        SideConfigType current = getSelectedConfigType(stack);
        SideConfigType[] values = SideConfigType.values();
        int next = reverse ? current.ordinal() - 1 : current.ordinal() + 1;
        if (next < 0) {
            next = values.length - 1;
        } else if (next >= values.length) {
            next = 0;
        }
        SideConfigType type = values[next];
        setSelectedConfigType(stack, type);
        return type;
    }

    private SideConfigType getSelectedConfigType(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) {
            return SideConfigType.ENERGY;
        }
        return SideConfigType.byOrdinal(stack.getTagCompound().getByte(TAG_CONFIG_TYPE));
    }

    private void setSelectedConfigType(ItemStack stack, SideConfigType type) {
        if (stack.isEmpty()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setByte(TAG_CONFIG_TYPE, (byte) type.ordinal());
    }

    private TextComponentTranslation localizeType(SideConfigType type) {
        return new TextComponentTranslation("side_config.ad_astra.type." + type.name().toLowerCase(Locale.ROOT));
    }

    private TextComponentTranslation localizeMode(AdAstraSideMode mode) {
        return new TextComponentTranslation("side_config.ad_astra.type." + mode.name().toLowerCase(Locale.ROOT));
    }

    private void configurePipeSide(AdAstraPipeTileEntity pipe, EntityPlayer player, World world, BlockPos pos, EnumFacing facing) {
        AdAstraPipeConnection connection = pipe.cycleConnection(facing, player.isSneaking());
        world.playSound(null, pos, ModSounds.WRENCH, SoundCategory.BLOCKS, 1.0f, world.rand.nextFloat() * 0.2f + 0.9f);
        player.sendStatusMessage(new TextComponentTranslation(pipeTooltipKey(connection)), true);
    }

    private String pipeTooltipKey(AdAstraPipeConnection connection) {
        return "tooltip.ad_astra.pipe." + connection.getName();
    }
}
