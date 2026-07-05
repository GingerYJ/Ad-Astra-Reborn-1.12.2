package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.items.AdAstraEnergizerItemBlock;
import earth.terrarium.adastra.common.registry.ModTileEntities;
import earth.terrarium.adastra.common.tile.EnergizerTileEntity;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class AdAstraEnergizerBlock extends AdAstraModelBlock implements ITileEntityProvider {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 5);
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public AdAstraEnergizerBlock(Material material, float hardness, float resistance) {
        super(material, hardness, resistance);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(LIT, false)
            .withProperty(POWER, 0)
            .withProperty(POWERED, false));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState()
            .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
            .withProperty(POWERED, world.isBlockPowered(pos));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state
            .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
            .withProperty(POWERED, world.isBlockPowered(pos)), 2);
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof EnergizerTileEntity) {
                ((EnergizerTileEntity) tile).restoreStoredEnergy(AdAstraEnergizerItemBlock.getEnergyStored(stack));
            }
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof EnergizerTileEntity) {
            ItemStack stack = new ItemStack(this);
            AdAstraEnergizerItemBlock.setEnergyStored(stack, ((EnergizerTileEntity) tile).getEnergyStorage().getEnergyStored());
            drops.add(stack);
            return;
        }
        super.getDrops(drops, world, pos, state, fortune);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, net.minecraft.block.Block block, BlockPos fromPos) {
        boolean powered = world.isBlockPowered(pos);
        if (state.getValue(POWERED) != powered) {
            world.setBlockState(pos, state.withProperty(POWERED, powered), 2);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, LIT, POWER, POWERED);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof EnergizerTileEntity)) {
            return false;
        }

        if (world.isRemote) {
            return true;
        }

        EnergizerTileEntity energizer = (EnergizerTileEntity) tile;
        ItemStack held = player.getHeldItem(hand);
        ItemStack stored = energizer.getStackInSlot(0);

        if (player.isSneaking()) {
            sendEnergyStatus(player, energizer, stored);
            return true;
        }

        if (stored.isEmpty() && !held.isEmpty() && held.getCount() == 1) {
            energizer.setInventorySlotContents(0, held.copy());
            player.setHeldItem(hand, ItemStack.EMPTY);
            energizer.markDirty();
            world.notifyBlockUpdate(pos, state, state, 3);
        } else if (held.isEmpty() && !stored.isEmpty()) {
            player.setHeldItem(hand, energizer.removeStackFromSlot(0));
            energizer.markDirty();
            world.notifyBlockUpdate(pos, state, state, 3);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }

    private void sendEnergyStatus(EntityPlayer player, EnergizerTileEntity energizer, ItemStack stored) {
        IEnergyStorage blockEnergy = energizer.getEnergyStorage();
        String message = "充能器: " + formatEnergy(blockEnergy);
        if (!stored.isEmpty() && stored.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage itemEnergy = stored.getCapability(CapabilityEnergy.ENERGY, null);
            if (itemEnergy != null) {
                message += " | 物品: " + formatEnergy(itemEnergy);
            }
        }
        player.sendStatusMessage(new TextComponentString(message), true);
    }

    private String formatEnergy(IEnergyStorage energy) {
        if (energy == null) {
            return "0/0 FE";
        }
        return energy.getEnergyStored() + "/" + energy.getMaxEnergyStored() + " FE";
    }
}
