package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.registry.ModParticles;
import earth.terrarium.adastra.common.tile.WaterPumpTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileWaterPumpRenderer extends TileEntitySpecialRenderer<WaterPumpTileEntity> {

    private static final double BUBBLE_Y_OFFSET = -0.5D;
    private static final double BUBBLE_VELOCITY = 0.01D;

    @Override
    public void render(WaterPumpTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || te.getWorld() == null || !te.isLit() || !te.consumeBubbleParticleTick()) {
            return;
        }

        Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
            ModParticles.OXYGEN_BUBBLE,
            te.getPos().getX() + 0.5D,
            te.getPos().getY() + BUBBLE_Y_OFFSET,
            te.getPos().getZ() + 0.5D,
            0.0D,
            0.0D,
            BUBBLE_VELOCITY);
    }
}
