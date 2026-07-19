package earth.terrarium.adastra.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class BlockDestroyStageRenderer {

    private static final int STAGE_COUNT = 10;
    private static final String STAGE_SPRITE_PREFIX = "minecraft:blocks/destroy_stage_";

    private BlockDestroyStageRenderer() {
    }

    public static boolean isDestroying(int destroyStage) {
        return destroyStage >= 0 && destroyStage < STAGE_COUNT;
    }

    /**
     * Renders a damage-model overlay while the caller is already translated to the block position.
     * The block renderer emits world-positioned vertices, so the position is removed in the buffer.
     */
    public static void renderDamageModel(
        IBlockAccess world,
        BlockPos pos,
        IBlockState state,
        IBakedModel model,
        int destroyStage,
        float alpha) {
        if (!isDestroying(destroyStage) || world == null || pos == null || state == null || model == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        IBakedModel missingModel = minecraft.getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getModelManager()
            .getMissingModel();
        if (model == missingModel) {
            return;
        }

        TextureAtlasSprite sprite = getDestroyStageSprite(destroyStage);
        if (sprite == null) {
            return;
        }

        IBakedModel damageModel = ForgeHooksClient.getDamageModel(model, sprite, state, world, pos);
        if (damageModel == null) {
            return;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        prepareOverlay(alpha);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        minecraft.getBlockRendererDispatcher()
            .getBlockModelRenderer()
            .renderModel(world, damageModel, state, pos, buffer, true);
        buffer.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
        finishOverlay();
        GlStateManager.popMatrix();
    }

    public static void renderDestroyStageQuad(
        int destroyStage,
        float x1,
        float y1,
        float z1,
        float x2,
        float y2,
        float z2,
        boolean reverse,
        float alpha) {
        if (!isDestroying(destroyStage)) {
            return;
        }

        TextureAtlasSprite sprite = getDestroyStageSprite(destroyStage);
        if (sprite == null) {
            return;
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        prepareOverlay(alpha);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        if (reverse) {
            buffer.pos(x1, y1, z1).tex(maxU, maxV).endVertex();
            buffer.pos(x2, y1, z1).tex(minU, maxV).endVertex();
            buffer.pos(x2, y2, z2).tex(minU, minV).endVertex();
            buffer.pos(x1, y2, z2).tex(maxU, minV).endVertex();
        } else {
            buffer.pos(x1, y1, z1).tex(minU, maxV).endVertex();
            buffer.pos(x1, y2, z2).tex(minU, minV).endVertex();
            buffer.pos(x2, y2, z2).tex(maxU, minV).endVertex();
            buffer.pos(x2, y1, z1).tex(maxU, maxV).endVertex();
        }
        tessellator.draw();
        finishOverlay();
        GlStateManager.popMatrix();
    }

    private static TextureAtlasSprite getDestroyStageSprite(int destroyStage) {
        return Minecraft.getMinecraft()
            .getTextureMapBlocks()
            .getAtlasSprite(STAGE_SPRITE_PREFIX + destroyStage);
    }

    private static void prepareOverlay(float alpha) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f * alpha);
        GlStateManager.doPolygonOffset(-3.0f, -3.0f);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
    }

    private static void finishOverlay() {
        GlStateManager.disablePolygonOffset();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
