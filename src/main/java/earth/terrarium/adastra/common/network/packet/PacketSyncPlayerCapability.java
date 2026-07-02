package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.capability.AdAstraCapabilities;
import earth.terrarium.adastra.common.capability.AdAstraPlayer;
import earth.terrarium.adastra.common.capability.IAdAstraPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncPlayerCapability implements IMessage {

    private NBTTagCompound data;

    public PacketSyncPlayerCapability() {
    }

    public PacketSyncPlayerCapability(AdAstraPlayer capability) {
        this.data = capability.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, data);
    }

    public static class Handler implements IMessageHandler<PacketSyncPlayerCapability, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSyncPlayerCapability message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player != null && message.data != null) {
                    IAdAstraPlayer cap = AdAstraCapabilities.getPlayer(player);
                    if (cap instanceof AdAstraPlayer) {
                        ((AdAstraPlayer) cap).readFromNBT(message.data);
                    }
                }
            });
            return null;
        }
    }
}
