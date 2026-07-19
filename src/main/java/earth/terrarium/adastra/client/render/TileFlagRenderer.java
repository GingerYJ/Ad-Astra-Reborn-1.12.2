package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.AdAstraEightDirection;
import earth.terrarium.adastra.common.blocks.AdAstraFlagBlock;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.tile.FlagTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class TileFlagRenderer extends TileEntitySpecialRenderer<FlagTileEntity> {

    private static final ResourceLocation WARNING_FLAG = new ResourceLocation(Reference.MOD_ID, "textures/block/flag/warning_flag.png");
    private static final Map<String, ResourceLocation> URL_TEXTURES = new HashMap<>();

    @Override
    public void render(FlagTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || !AdAstraConfig.allowFlagImages || te.getFlagUrl().isEmpty()) {
            return;
        }

        World world = te.getWorld();
        BlockPos pos = te.getPos();
        if (world == null || pos == null) {
            return;
        }

        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof AdAstraFlagBlock) || state.getValue(AdAstraFlagBlock.HALF) != AdAstraFlagBlock.Half.UPPER) {
            return;
        }

        IBlockState lowerState = world.getBlockState(pos.down());
        if (!(lowerState.getBlock() instanceof AdAstraFlagBlock)) {
            return;
        }

        ResourceLocation texture = getUrlTexture(te.getFlagUrl());
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0.5f, -0.5f, 0.5f);
        GlStateManager.rotate(rotationFor(lowerState.getValue(AdAstraFlagBlock.FACING)), 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(-0.5f, 0.5f, -0.5f);

        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        renderFlagImage();
        BlockDestroyStageRenderer.renderDestroyStageQuad(
            destroyStage,
            -15.0f / 16.0f,
            -1.0f / 16.0f,
            0.497f,
            7.0f / 16.0f,
            15.0f / 16.0f,
            0.497f,
            false,
            alpha);
        BlockDestroyStageRenderer.renderDestroyStageQuad(
            destroyStage,
            -15.0f / 16.0f,
            -1.0f / 16.0f,
            0.503f,
            7.0f / 16.0f,
            15.0f / 16.0f,
            0.503f,
            true,
            alpha);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static ResourceLocation getUrlTexture(String url) {
        String key = url.trim();
        ResourceLocation texture = URL_TEXTURES.get(key);
        if (texture == null) {
            texture = new ResourceLocation(Reference.MOD_ID, "flag_url/" + Integer.toHexString(key.hashCode()));
            Minecraft.getMinecraft().getTextureManager().loadTexture(texture, new ThreadDownloadImageData(null, key, WARNING_FLAG, null));
            URL_TEXTURES.put(key, texture);
        }
        return texture;
    }

    private static float rotationFor(AdAstraEightDirection direction) {
        switch (direction) {
            case WEST:
                return 90.0f;
            case SOUTH:
            case SOUTH_EAST:
            case SOUTH_WEST:
                return 180.0f;
            case EAST:
                return 270.0f;
            case NORTH:
            case NORTH_EAST:
            case NORTH_WEST:
            default:
                return 0.0f;
        }
    }

    private static void renderFlagImage() {
        float x1 = -15.0f / 16.0f;
        float x2 = 7.0f / 16.0f;
        float y1 = -1.0f / 16.0f;
        float y2 = 15.0f / 16.0f;

        renderQuad(x1, y1, 0.497f, x2, y2, 0.497f, false);
        renderQuad(x1, y1, 0.503f, x2, y2, 0.503f, true);
    }

    private static void renderQuad(float x1, float y1, float z1, float x2, float y2, float z2, boolean reverse) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        if (reverse) {
            buffer.pos(x1, y1, z1).tex(1.0d, 1.0d).endVertex();
            buffer.pos(x2, y1, z1).tex(0.0d, 1.0d).endVertex();
            buffer.pos(x2, y2, z2).tex(0.0d, 0.0d).endVertex();
            buffer.pos(x1, y2, z2).tex(1.0d, 0.0d).endVertex();
        } else {
            buffer.pos(x1, y1, z1).tex(0.0d, 1.0d).endVertex();
            buffer.pos(x1, y2, z2).tex(0.0d, 0.0d).endVertex();
            buffer.pos(x2, y2, z2).tex(1.0d, 0.0d).endVertex();
            buffer.pos(x2, y1, z1).tex(1.0d, 1.0d).endVertex();
        }
        tessellator.draw();
    }
}
