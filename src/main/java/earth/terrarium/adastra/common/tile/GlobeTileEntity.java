package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.blocks.AdAstraGlobeBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class GlobeTileEntity extends AdAstraSyncedTileEntity implements ITickable {

    private float torque;
    private float yRot;
    private float lastYRot;

    @Override
    public void update() {
        if (world == null || pos == null) {
            return;
        }

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof AdAstraGlobeBlock && state.getValue(AdAstraGlobeBlock.POWERED) && torque <= 3.0f) {
            torque = 3.0f;
        }

        if (torque > 0.0f) {
            torque -= 0.75f;
            lastYRot = yRot;
            yRot -= torque;
            if (!world.isRemote) {
                markDirty();
            }
        } else if (torque < 0.0f) {
            torque = 0.0f;
            if (!world.isRemote) {
                markDirty();
            }
        }
    }

    public void rotateGlobe() {
        torque = (float) ((Math.PI * 15.0d) / (1.0d + Math.pow(0.00003d, torque)));
        syncToClients();
    }

    public float getTorque() {
        return torque;
    }

    public float getYRot() {
        return yRot;
    }

    public float getLastYRot() {
        return lastYRot;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        torque = compound.getFloat("Torque");
        yRot = compound.getFloat("YRot");
        lastYRot = yRot;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setFloat("Torque", torque);
        compound.setFloat("YRot", yRot);
        return compound;
    }
}
