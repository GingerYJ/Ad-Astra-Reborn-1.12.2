package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.util.AdAstraFluidHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Network packet for syncing machine field data to clients.
 * Synchronizes energy, fluid amounts, fluid type, and progress values.
 */
public class PacketSyncMachine implements IMessage {

    private BlockPos pos;
    private int energy;
    private int energyCapacity;
    private FluidStack fluidStack;
    private int fluidCapacity;
    private int progress;
    private int maxProgress;

    public PacketSyncMachine() {
    }

    /**
     * Creates a packet to sync machine data.
     *
     * @param pos Machine position
     * @param energy Current energy stored
     * @param energyCapacity Maximum energy capacity
     * @param fluidStack Current fluid stack (type + amount)
     * @param fluidCapacity Maximum fluid capacity
     * @param progress Current operation progress
     * @param maxProgress Maximum progress value
     */
    public PacketSyncMachine(BlockPos pos, int energy, int energyCapacity,
                            FluidStack fluidStack, int fluidCapacity,
                            int progress, int maxProgress) {
        this.pos = pos;
        this.energy = energy;
        this.energyCapacity = energyCapacity;
        this.fluidStack = fluidStack;
        this.fluidCapacity = fluidCapacity;
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        energy = buf.readInt();
        energyCapacity = buf.readInt();

        // Read fluid stack
        boolean hasFluid = buf.readBoolean();
        if (hasFluid) {
            fluidStack = AdAstraFluidHelper.loadFluidStackFromNBT(ByteBufUtils.readTag(buf));
        } else {
            fluidStack = null;
        }

        fluidCapacity = buf.readInt();
        progress = buf.readInt();
        maxProgress = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(energy);
        buf.writeInt(energyCapacity);

        // Write fluid stack
        buf.writeBoolean(fluidStack != null);
        if (fluidStack != null) {
            ByteBufUtils.writeTag(buf, fluidStack.writeToNBT(new net.minecraft.nbt.NBTTagCompound()));
        }

        buf.writeInt(fluidCapacity);
        buf.writeInt(progress);
        buf.writeInt(maxProgress);
    }

    public static class Handler implements IMessageHandler<PacketSyncMachine, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncMachine message, MessageContext ctx) {
            // Handle on client side
            Minecraft.getMinecraft().addScheduledTask(() -> handleClientSide(message));
            return null;
        }

        @SideOnly(Side.CLIENT)
        private static void handleClientSide(PacketSyncMachine message) {
            if (message.pos == null) {
                return;
            }

            TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(message.pos);
            if (!(tile instanceof AdAstraMachineTileEntity)) {
                return;
            }

            AdAstraMachineTileEntity machine = (AdAstraMachineTileEntity) tile;

            // Update energy if the machine has energy storage
            if (machine.getEnergyStorage() != null) {
                machine.getEnergyStorage().setEnergyStored(message.energy);
            }

            // Update fluid tank if the machine has one
            if (machine.getFluidTank() != null) {
                if (message.fluidStack != null) {
                    machine.getFluidTank().setFluid(message.fluidStack);
                } else {
                    machine.getFluidTank().setFluid(null);
                }
                machine.getFluidTank().setCapacity(message.fluidCapacity);
            }

            // Update progress via field system
            machine.setField(2, message.progress);
            machine.setField(3, message.maxProgress);
        }
    }
}
