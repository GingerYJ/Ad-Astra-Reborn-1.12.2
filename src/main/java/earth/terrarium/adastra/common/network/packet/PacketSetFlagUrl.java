package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.tile.FlagTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.regex.Pattern;

/**
 * Network packet for setting flag custom URL from GUI.
 * Includes server-side validation and broadcasts to nearby clients.
 */
public class PacketSetFlagUrl implements IMessage {

    private static final Pattern URL_REGEX = Pattern.compile("^https://i\\.imgur\\.com/(\\w+)\\.(png|jpeg|jpg|webp)$");
    private static final double MAX_DISTANCE_SQ = 64.0D;
    private static final int MAX_URL_LENGTH = 512;

    private BlockPos pos;
    private String url;

    public PacketSetFlagUrl() {
    }

    /**
     * Creates a packet to set flag URL.
     *
     * @param pos Flag position
     * @param url URL string (will be validated on server)
     */
    public PacketSetFlagUrl(BlockPos pos, String url) {
        this.pos = pos;
        this.url = url == null ? "" : url;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        url = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeUTF8String(buf, url);
    }

    public static class Handler implements IMessageHandler<PacketSetFlagUrl, IMessage> {

        @Override
        public IMessage onMessage(PacketSetFlagUrl message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketSetFlagUrl message, EntityPlayerMP player) {
            World world = player.world;
            if (message.pos == null || world == null || !world.isBlockLoaded(message.pos)) {
                return;
            }

            // Validate distance to prevent cheating
            if (player.getDistanceSq(message.pos.getX() + 0.5D, message.pos.getY() + 0.5D, message.pos.getZ() + 0.5D) > MAX_DISTANCE_SQ) {
                return;
            }

            TileEntity tile = world.getTileEntity(message.pos);
            if (!(tile instanceof FlagTileEntity)) {
                return;
            }

            FlagTileEntity flag = (FlagTileEntity) tile;
            if (flag.getOwnerId() == null || !player.getUniqueID().equals(flag.getOwnerId())) {
                player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.flag.not_owner"), true);
                return;
            }

            // Validate URL
            String validatedUrl = validateUrl(message.url);
            if (validatedUrl == null) {
                return;
            }

            // Set the URL (this will trigger syncToClients in FlagTileEntity)
            flag.setFlagUrl(validatedUrl);
        }

        /**
         * Validates and sanitizes the URL.
         *
         * @param url Input URL
         * @return Validated URL or null if invalid
         */
        private String validateUrl(String url) {
            if (url == null) {
                return "";
            }

            String trimmed = url.trim();

            // Allow empty URLs to clear the flag
            if (trimmed.isEmpty()) {
                return "";
            }

            // Check length
            if (trimmed.length() > MAX_URL_LENGTH) {
                return null;
            }

            // Basic URL format validation
            String lower = trimmed.toLowerCase();
            if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
                return null;
            }

            if (!URL_REGEX.matcher(trimmed).matches()) {
                return null;
            }

            // Prevent common malicious patterns
            if (lower.contains("javascript:") || lower.contains("<script") || lower.contains("onclick")) {
                return null;
            }

            return trimmed;
        }
    }
}
