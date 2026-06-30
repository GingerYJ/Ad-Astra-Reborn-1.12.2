package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModTileEntities;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AdAstraGlobeBlock extends AdAstraModelBlock implements ITileEntityProvider {

    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool WATERLOGGED = PropertyBool.create("waterlogged");

    public AdAstraGlobeBlock() {
        super(Material.IRON, 5.0f, 6.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setDefaultState(blockState.getBaseState()
            .withProperty(POWERED, false)
            .withProperty(WATERLOGGED, false));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
            .withProperty(POWERED, (meta & 1) != 0)
            .withProperty(WATERLOGGED, false);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWERED) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED, WATERLOGGED);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return ModTileEntities.createForBlock(this);
    }
}
