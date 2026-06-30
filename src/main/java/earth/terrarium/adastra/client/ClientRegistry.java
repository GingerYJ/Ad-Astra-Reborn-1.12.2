package earth.terrarium.adastra.client;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.blocks.AdAstraFluidBlock;
import earth.terrarium.adastra.common.blocks.AdAstraGlobeBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSlabBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSlidingDoorBlock;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public final class ClientRegistry {

    private ClientRegistry() {
    }

    public static void registerModels() {
        for (Block block : ModBlocks.BLOCKS) {
            registerStateMapper(block);
            if (ModBlocks.isHiddenFromItemModels(block)) {
                continue;
            }
            registerBlockItemModel(block);
        }
        for (Block block : ModBlocks.HIDDEN_BLOCKS) {
            registerStateMapper(block);
        }
        for (Item item : ModItems.ITEMS) {
            registerItemModel(item);
        }
    }

    private static void registerStateMapper(Block block) {
        if (block instanceof AdAstraSlabBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraSlabStateMapper());
        } else if (block instanceof AdAstraFluidBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraFluidStateMapper());
        } else if (block instanceof AdAstraSlidingDoorBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraSlidingDoorStateMapper());
        }
    }

    private static void registerItemModel(Item item) {
        if (item == null || item.getRegistryName() == null) {
            return;
        }
        ModelLoader.setCustomModelResourceLocation(
            item,
            0,
            new ModelResourceLocation(Reference.MOD_ID + ":" + item.getRegistryName().getPath(), "inventory"));
    }

    private static void registerBlockItemModel(Block block) {
        Item item = Item.getItemFromBlock(block);
        if (item == null || item.getRegistryName() == null) {
            return;
        }
        if (block instanceof AdAstraGlobeBlock) {
            ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new ModelResourceLocation(Reference.MOD_ID + ":" + item.getRegistryName().getPath(), "powered=false,waterlogged=false"));
            return;
        }
        registerItemModel(item);
    }

    private static class AdAstraSlabStateMapper extends StateMapperBase {

        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            Block block = state.getBlock();
            String path = block.getRegistryName().getPath();
            if (block instanceof AdAstraSlabBlock.Double) {
                path = path.replaceFirst("^double_", "");
                return new ModelResourceLocation(Reference.MOD_ID + ":" + path, "type=double");
            }
            BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
            String type = half == BlockSlab.EnumBlockHalf.TOP ? "top" : "bottom";
            return new ModelResourceLocation(Reference.MOD_ID + ":" + path, "type=" + type);
        }
    }

    private static class AdAstraFluidStateMapper extends StateMapperBase {

        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return new ModelResourceLocation("forge:fluid", state.getBlock().getRegistryName().getPath());
        }
    }

    private static class AdAstraSlidingDoorStateMapper extends StateMapperBase {

        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            Block block = state.getBlock();
            return new ModelResourceLocation(
                Reference.MOD_ID + ":" + block.getRegistryName().getPath(),
                "facing=north,locked=false,open=false,part=bottom,powered=false");
        }
    }
}
