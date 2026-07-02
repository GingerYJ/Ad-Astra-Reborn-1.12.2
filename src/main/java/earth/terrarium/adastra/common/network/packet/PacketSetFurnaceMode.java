package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.tile.EtrionicBlastFurnaceTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Network packet for setting etrionic blast furnace mode.
 * Allows players to toggle between alloying and blasting modes.
 */
public class PacketSetFurnaceMode implements IMessage {

    private static final double MAX_DISTANCE_SQ = 64.0D;

    private BlockPos pos;
    private boolean blastingMode;

    public PacketSetFurnaceMode() {
    }

    /**
     * Creates a packet to set furnace mode.
     *
     * @param pos Machine position
     * @param blastingMode True for blasting mode, false for alloying mode
     */
    public PacketSetFurnaceMode(BlockPos pos, boolean blastingMode) {
        this.pos = pos;
        this.blastingMode = blastingMode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        blastingMode = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeBoolean(blastingMode);
    }

    public static class Handler implements IMessageHandler<PacketSetFurnaceMode, IMessage> {

        @Override
        public IMessage onMessage(PacketSetFurnaceMode message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketSetFurnaceMode message, EntityPlayerMP player) {
            World world = player.world;
            if (message.pos == null || world == null || !world.isBlockLoaded(message.pos)) {
                return;
            }

            // Validate distance to prevent cheating
            if (player.getDistanceSq(message.pos.getX() + 0.5D, message.pos.getY() + 0.5D, message.pos.getZ() + 0.5D) > MAX_DISTANCE_SQ) {
                return;
            }

            TileEntity tile = world.getTileEntity(message.pos);
            if (!(tile instanceof EtrionicBlastFurnaceTileEntity)) {
                return;
            }

            EtrionicBlastFurnaceTileEntity furnace = (EtrionicBlastFurnaceTileEntity) tile;

            // Set furnace mode based on blasting mode flag
            EtrionicBlastFurnaceTileEntity.FurnaceMode newMode = message.blastingMode ?
                EtrionicBlastFurnaceTileEntity.FurnaceMode.BLASTING :
                EtrionicBlastFurnaceTileEntity.FurnaceMode.ALLOYING;
            furnace.setMode(newMode);
            furnace.markDirty();
        }
    }
}
