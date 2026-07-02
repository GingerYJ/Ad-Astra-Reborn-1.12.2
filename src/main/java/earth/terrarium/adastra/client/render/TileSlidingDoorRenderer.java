package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.AdAstraSlidingDoorBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.tile.SlidingDoorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileSlidingDoorRenderer extends TileEntitySpecialRenderer<SlidingDoorTileEntity> {

    @Override
    public void render(SlidingDoorTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || te.getWorld() == null || !te.isController()) {
            return;
        }

        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (!(state.getBlock() instanceof AdAstraSlidingDoorBlock) || state.getBlock().getRegistryName() == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        IBakedModel model = getModel(minecraft, state.getBlock(), "normal");
        if (model == minecraft.getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel()) {
            return;
        }

        float slide = interpolate(te.getLastSlideTicks(), te.getSlideTicks(), partialTicks) / 81.0f;
        boolean flipSecondDoor = needsFlippedSecondDoor(state.getBlock());

        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);

        GlStateManager.translate(0.5f, 1.0f, 0.5f);
        GlStateManager.rotate(state.getValue(AdAstraSlidingDoorBlock.FACING).getHorizontalAngle(), 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(-0.5f, 0.0f, -0.5f);

        GlStateManager.translate(slide, 0.0f, 0.0625f);
        if (state.getValue(AdAstraSlidingDoorBlock.FACING).getAxis() == EnumFacing.Axis.Z) {
            GlStateManager.translate(0.0f, 0.0f, 0.6875f);
            if (state.getBlock() == ModBlocks.REINFORCED_DOOR) {
                GlStateManager.translate(0.0f, 0.0f, -0.3125f);
            }
        }

        renderModel(minecraft, state, model);

        GlStateManager.translate(-slide - slide, 0.0f, 0.0f);
        if (flipSecondDoor) {
            GlStateManager.translate(-1.25f, 0.0f, 0.0f);
            renderModel(minecraft, state, getModel(minecraft, state.getBlock(), "flipped"));
        } else {
            GlStateManager.translate(0.5f, 0.0f, 0.5f);
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-0.5f, 0.0f, -0.5f);
            GlStateManager.translate(0.0f, 0.0f, 0.8125f);
            renderModel(minecraft, state, model);
        }

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private IBakedModel getModel(Minecraft minecraft, Block block, String variant) {
        ResourceLocation registryName = block.getRegistryName();
        return minecraft.getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getModelManager()
            .getModel(new ModelResourceLocation(registryName, variant));
    }

    private void renderModel(Minecraft minecraft, IBlockState state, IBakedModel model) {
        minecraft.getBlockRendererDispatcher()
            .getBlockModelRenderer()
            .renderModelBrightnessColor(state, model, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    private boolean needsFlippedSecondDoor(Block block) {
        return block == ModBlocks.AIRLOCK || block == ModBlocks.REINFORCED_DOOR;
    }

    private float interpolate(int last, int current, float partialTicks) {
        return last + (current - last) * partialTicks;
    }
}
