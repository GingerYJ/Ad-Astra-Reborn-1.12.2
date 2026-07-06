package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.client.model.ModelEnceladusCrystal;
import earth.terrarium.adastra.common.blocks.celestial.EnceladusCrystalBlock;
import earth.terrarium.adastra.common.tile.TileEntityEnceladusCrystal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEnceladusCrystalRenderer extends TileEntitySpecialRenderer<TileEntityEnceladusCrystal> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/model/enceladus_crystal.png");
    private final ModelEnceladusCrystal model = new ModelEnceladusCrystal();

    @Override
    public void render(TileEntityEnceladusCrystal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220F, 0F);

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);

        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.8F, (float) z + 0.5F);
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        GL11.glScalef(1.2F, 1.2F, 1.2F);

        if (te.getWorld() != null && te.getWorld().getBlockState(te.getPos().up()).isNormalCube()) {
            GL11.glTranslatef(0.0F, -2.15F, 0.0F);
        } else {
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        }

        GL11.glRotatef(getHorizontalRotation(te), 0.0F, 1.0F, 0.0F);
        model.render(null, 0.0625F);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private float getHorizontalRotation(TileEntityEnceladusCrystal te) {
        if (te.getWorld() == null) {
            return 0F;
        }
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (!state.getPropertyKeys().contains(EnceladusCrystalBlock.FACING)) {
            return 0F;
        }
        EnumFacing facing = state.getValue(EnceladusCrystalBlock.FACING);
        if (facing.getAxis().isVertical()) {
            return 0F;
        }
        return facing.getHorizontalIndex() * 90F;
    }
}
