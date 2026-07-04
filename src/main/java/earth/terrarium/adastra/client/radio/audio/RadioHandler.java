package earth.terrarium.adastra.client.radio.audio;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.tile.RadioTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RadioHandler {

    public static final int RANGE_SQ = 3072;
    public static final int RANGE_DROPOFF_SQ = 1024;
    public static final double RANGE = Math.sqrt(RANGE_SQ);

    private static String playingUrl = "";
    private static BlockPos sourcePos;
    private static RadioAudioPlayer player;

    private RadioHandler() {
    }

    public static void play(String url, BlockPos pos) {
        String normalized = normalize(url);
        if (normalized.isEmpty()) {
            stop();
            return;
        }
        if (!normalized.equals(playingUrl)) {
            stopPlayer();
            player = new RadioAudioPlayer(normalized);
            player.start();
        }
        playingUrl = normalized;
        sourcePos = pos;
        updateVolume();
    }

    public static void play(String url) {
        String normalized = normalize(url);
        if (normalized.isEmpty()) {
            stop();
            return;
        }
        if (!normalized.equals(playingUrl)) {
            stopPlayer();
            player = new RadioAudioPlayer(normalized);
            player.start();
        }
        playingUrl = normalized;
        sourcePos = null;
        updateVolume();
    }

    public static void stop() {
        playingUrl = "";
        sourcePos = null;
        stopPlayer();
    }

    public static void tick() {
        if (playingUrl.isEmpty()) {
            return;
        }

        if (player == null || player.isFinished()) {
            stop();
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null || minecraft.player == null) {
            stop();
            return;
        }

        if (sourcePos == null) {
            return;
        }

        TileEntity tile = minecraft.world.getTileEntity(sourcePos);
        if (!(tile instanceof RadioTileEntity) || !((RadioTileEntity) tile).isPlaying()) {
            stop();
            return;
        }

        double distanceSq = minecraft.player.getDistanceSq(
            sourcePos.getX() + 0.5D,
            sourcePos.getY() + 0.5D,
            sourcePos.getZ() + 0.5D
        );
        if (distanceSq > RANGE_SQ) {
            stop();
            return;
        }
        updateVolume();
    }

    public static String getPlaying() {
        return playingUrl.isEmpty() ? null : playingUrl;
    }

    public static BlockPos getSourcePos() {
        return sourcePos;
    }

    public static float getPositionalVolume(BlockPos source) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || source == null) {
            return 0.0F;
        }
        double distanceSq = minecraft.player.getDistanceSq(source.getX() + 0.5D, source.getY() + 0.5D, source.getZ() + 0.5D);
        if (distanceSq > RANGE_SQ) {
            return 0.0F;
        }
        return 1.0F - (float) Math.max((distanceSq - (RANGE_SQ - RANGE_DROPOFF_SQ)) / RANGE_DROPOFF_SQ, 0.0D);
    }

    public static int getRadioVolume() {
        return AdAstraConfig.radioVolume;
    }

    public static void setRadioVolume(int volume) {
        AdAstraConfig.setRadioVolume(volume);
        updateVolume();
    }

    public static void adjustRadioVolume(int amount) {
        setRadioVolume(getRadioVolume() + amount);
    }

    private static void updateVolume() {
        if (player == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        float masterVolume = minecraft.gameSettings.getSoundLevel(SoundCategory.MASTER);
        float rangeVolume = sourcePos == null ? 1.0F : getPositionalVolume(sourcePos);
        float radioVolume = AdAstraConfig.radioVolume / 100.0F;
        player.setVolume(masterVolume * rangeVolume * radioVolume);
    }

    private static void stopPlayer() {
        if (player != null) {
            player.stop();
            player = null;
        }
    }

    private static String normalize(String url) {
        return RadioTileEntity.normalizeStation(url);
    }
}
