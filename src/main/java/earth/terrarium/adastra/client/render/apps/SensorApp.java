package earth.terrarium.adastra.client.render.apps;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.api.systems.PlanetData;
import earth.terrarium.adastra.client.render.Ti69App;
import earth.terrarium.adastra.client.render.Ti69Renderer;
import earth.terrarium.adastra.client.systems.ClientData;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import earth.terrarium.adastra.common.util.EnvironmentUtils;
import earth.terrarium.adastra.common.util.MachineStateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;

public class SensorApp implements Ti69App {

    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "sensor");
    private static final int ICON_X = 6;
    private static final int TEXT_X = 18;
    private static final int ENVIRONMENT_SCAN_RADIUS = 16;

    @Override
    public void render(FontRenderer font, WorldClient world) {
        PlanetData data = ClientData.getLocalData();
        EntityPlayer player = Minecraft.getMinecraft().player;

        boolean hasOxygen = player != null
            ? EnvironmentUtils.hasOxygen(world, player.getPosition(), EnvironmentUtils.DEFAULT_ENVIRONMENT_SCAN_RADIUS)
            : data != null && data.oxygen();
        Float localGravity = player == null ? null : getLocalGravity(world, player.getPosition());
        float gravityValue = localGravity != null
            ? localGravity
            : data == null ? Float.NaN : data.gravity();

        String oxygen = hasOxygen ? "O2 YES" : "O2 NO";
        String temperature = data == null ? "TEMP --" : "TEMP " + data.temperature() + "C";
        String gravity = Float.isNaN(gravityValue) ? "G --" : "G " + (Math.round(gravityValue * 9.8F * 10.0F) / 10.0F);

        renderTime(font, world, TEXT_X);

        font.drawString(oxygen, TEXT_X, 17, 0xFFFFFF);
        font.drawString(temperature, TEXT_X, 30, 0xFFFFFF);
        font.drawString(gravity, TEXT_X, 43, 0xFFFFFF);

        renderIcon(Ti69Renderer.ICONS, ICON_X, 17, 24, 0, 8, 8, 32, 32);
        renderIcon(Ti69Renderer.ICONS, ICON_X, 30, 24, 8, 8, 8, 32, 32);
        renderIcon(Ti69Renderer.ICONS, ICON_X, 43, 24, 16, 8, 8, 32, 32);
    }

    private static Float getLocalGravity(WorldClient world, BlockPos playerPos) {
        if (world == null || playerPos == null) {
            return null;
        }

        BlockPos min = playerPos.add(-ENVIRONMENT_SCAN_RADIUS, -ENVIRONMENT_SCAN_RADIUS, -ENVIRONMENT_SCAN_RADIUS);
        BlockPos max = playerPos.add(ENVIRONMENT_SCAN_RADIUS, ENVIRONMENT_SCAN_RADIUS, ENVIRONMENT_SCAN_RADIUS);
        for (BlockPos mutablePos : BlockPos.getAllInBoxMutable(min, max)) {
            IBlockState state = world.getBlockState(mutablePos);
            Block block = state.getBlock();
            if (block != ModBlocks.GRAVITY_NORMALIZER || !MachineStateUtils.isLit(state)) {
                continue;
            }

            TileEntity tile = world.getTileEntity(mutablePos);
            if (tile instanceof GravityNormalizerTileEntity
                && reachesGravityNormalizer((GravityNormalizerTileEntity) tile, mutablePos, playerPos)) {
                return ((GravityNormalizerTileEntity) tile).getTargetGravity();
            }
        }
        return null;
    }

    private static boolean reachesGravityNormalizer(GravityNormalizerTileEntity normalizer, BlockPos normalizerPos, BlockPos playerPos) {
        int radius = Math.max(1, normalizer.getWorkingRadius());
        return normalizerPos.distanceSq(playerPos) <= radius * radius;
    }

    @Override
    public int color() {
        return 0xff3aabc3;
    }
}
