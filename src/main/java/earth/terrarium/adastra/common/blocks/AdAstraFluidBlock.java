package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModFluids;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class AdAstraFluidBlock extends BlockFluidClassic {

    private static final Material AD_ASTRA_FLUID_MATERIAL = new MaterialLiquid(MapColor.WATER);

    public AdAstraFluidBlock(Fluid fluid) {
        super(fluid, AD_ASTRA_FLUID_MATERIAL);
        setQuantaPerBlock(8);
        setLightOpacity(3);
    }

    @Override
    public boolean canDrain(World world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollision(world, pos, state, entity);
        if (!world.isRemote || definedFluid != ModFluids.CRYO_FUEL || world.rand.nextInt(10) != 0) {
            return;
        }

        double x = entity.posX + (world.rand.nextDouble() - 0.5D) * Math.max(entity.width, 0.25F);
        double y = entity.posY + world.rand.nextDouble() * Math.max(entity.height, 0.25F);
        double z = entity.posZ + (world.rand.nextDouble() - 0.5D) * Math.max(entity.width, 0.25F);
        world.spawnParticle(EnumParticleTypes.SNOWBALL, x, y, z, 0.0D, 0.01D, 0.0D);
    }
}
