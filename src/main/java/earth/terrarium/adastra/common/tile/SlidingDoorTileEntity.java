package earth.terrarium.adastra.common.tile;

import earth.terrarium.adastra.common.blocks.AdAstraSlidingDoorPart;
import earth.terrarium.adastra.common.registry.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class SlidingDoorTileEntity extends AdAstraTileEntity implements ITickable {

    private AdAstraSlidingDoorPart part = AdAstraSlidingDoorPart.BOTTOM;
    private boolean locked;
    private boolean open;
    private boolean powered;
    private int slideTicks;
    private int lastSlideTicks;

    @Override
    public void update() {
        lastSlideTicks = slideTicks;
        boolean shouldOpen = open || powered;

        if (world != null && !world.isRemote && part.isController()) {
            if (!shouldOpen && slideTicks == 97) {
                world.playSound(null, pos, ModSounds.SLIDING_DOOR_CLOSE, SoundCategory.BLOCKS, 0.25f, 1.0f);
            } else if (shouldOpen && slideTicks == 3) {
                world.playSound(null, pos, ModSounds.SLIDING_DOOR_OPEN, SoundCategory.BLOCKS, 0.25f, 1.0f);
            }
        }

        slideTicks = MathHelper.clamp(slideTicks + (shouldOpen ? 3 : -3), 0, 100);
    }

    public AdAstraSlidingDoorPart getPart() {
        return part;
    }

    public boolean isController() {
        return part.isController();
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isPowered() {
        return powered;
    }

    public int getSlideTicks() {
        return slideTicks;
    }

    public int getLastSlideTicks() {
        return lastSlideTicks;
    }

    public boolean isPassable() {
        return open || powered || slideTicks > 50;
    }

    public void setAnimationTicks(int lastSlideTicks, int slideTicks) {
        this.lastSlideTicks = MathHelper.clamp(lastSlideTicks, 0, 100);
        this.slideTicks = MathHelper.clamp(slideTicks, 0, 100);
    }

    public void configure(AdAstraSlidingDoorPart part, boolean locked, boolean open, boolean powered) {
        configure(part, locked, open, powered, true);
    }

    public void configure(AdAstraSlidingDoorPart part, boolean locked, boolean open, boolean powered, boolean resetSlide) {
        this.part = part;
        this.locked = locked;
        this.open = open;
        this.powered = powered;
        if (resetSlide) {
            this.slideTicks = open || powered ? 100 : 0;
            this.lastSlideTicks = this.slideTicks;
        }
        markAndSync();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        part = AdAstraSlidingDoorPart.byName(compound.getString("Part"));
        locked = compound.getBoolean("Locked");
        open = compound.getBoolean("Open");
        powered = compound.getBoolean("Powered");
        slideTicks = MathHelper.clamp(compound.getInteger("SlideTicks"), 0, 100);
        lastSlideTicks = slideTicks;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("Part", part.getName());
        compound.setBoolean("Locked", locked);
        compound.setBoolean("Open", open);
        compound.setBoolean("Powered", powered);
        compound.setInteger("SlideTicks", slideTicks);
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    private void markAndSync() {
        markDirty();
        if (world != null && pos != null) {
            IBlockState state = world.getBlockState(pos);
            world.markBlockRangeForRenderUpdate(pos, pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }
}
