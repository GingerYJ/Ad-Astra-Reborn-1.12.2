package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.common.tile.FlagTileEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileFlagRenderer extends TileEntitySpecialRenderer<FlagTileEntity> {

    @Override
    public void render(FlagTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // The regular block model now renders placed flags. The previous TESR drew
        // an extra white flag plane over both halves, causing texture overlap.
    }
}
