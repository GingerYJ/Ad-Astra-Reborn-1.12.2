package earth.terrarium.adastra.client;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.client.render.AdAstraEntityRenderers;
import earth.terrarium.adastra.client.render.VehicleItemStackRenderer;
import earth.terrarium.adastra.common.blocks.AdAstraDoorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraFenceGateBlock;
import earth.terrarium.adastra.common.blocks.AdAstraFluidBlock;
import earth.terrarium.adastra.common.blocks.AdAstraGlobeBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSlabBlock;
import earth.terrarium.adastra.common.blocks.AdAstraSlidingDoorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraTrapDoorBlock;
import earth.terrarium.adastra.common.blocks.AdAstraWallBlock;
import earth.terrarium.adastra.common.items.AdAstraSpawnEggItem;
import earth.terrarium.adastra.common.items.VehicleItem;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ClientRegistry {

    private ClientRegistry() {
    }

    public static void registerModels() {
        AdAstraEntityRenderers.register();

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

    public static void registerItemColors(ItemColors itemColors) {
        itemColors.registerItemColorHandler(
            (stack, tintIndex) -> ((AdAstraSpawnEggItem) stack.getItem()).getColor(tintIndex),
            ModItems.ITEMS.stream()
                .filter(item -> item instanceof AdAstraSpawnEggItem)
                .toArray(Item[]::new));
    }

    private static void registerStateMapper(Block block) {
        if (block instanceof AdAstraSlabBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraSlabStateMapper());
        } else if (block instanceof AdAstraFluidBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraFluidStateMapper());
        } else if (block instanceof AdAstraSlidingDoorBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraSlidingDoorStateMapper());
        } else if (block instanceof AdAstraDoorBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraDoorStateMapper());
        } else if (block instanceof AdAstraTrapDoorBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraTrapDoorStateMapper());
        } else if (block instanceof AdAstraFenceGateBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraFenceGateStateMapper());
        } else if (block instanceof AdAstraWallBlock) {
            ModelLoader.setCustomStateMapper(block, new AdAstraWallStateMapper());
        }
    }

    private static void registerItemModel(Item item) {
        if (item == null || item == Items.AIR || item.getRegistryName() == null) {
            return;
        }
        if (item instanceof VehicleItem) {
            item.setTileEntityItemStackRenderer(VehicleItemStackRenderer.INSTANCE);
        }
        ModelLoader.setCustomModelResourceLocation(
            item,
            0,
            new ModelResourceLocation(Reference.MOD_ID + ":" + item.getRegistryName().getPath(), "inventory"));
    }

    private static void registerBlockItemModel(Block block) {
        Item item = getRegisteredBlockItem(block);
        if (item == null || item == Items.AIR || item.getRegistryName() == null) {
            return;
        }
        if (block instanceof AdAstraGlobeBlock) {
            ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new ModelResourceLocation(Reference.MOD_ID + ":" + item.getRegistryName().getPath(), "powered=false,waterlogged=false"));
            return;
        }
        if (block instanceof AdAstraWallBlock) {
            ModelResourceLocation model = new ModelResourceLocation(
                Reference.MOD_ID + ":" + item.getRegistryName().getPath(), "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, model);
            ModelLoader.setCustomModelResourceLocation(item, 1, model);
            return;
        }
        registerItemModel(item);
    }

    private static Item getRegisteredBlockItem(Block block) {
        if (block.getRegistryName() != null) {
            Item registryItem = ForgeRegistries.ITEMS.getValue(block.getRegistryName());
            if (registryItem != null && registryItem != Items.AIR) {
                return registryItem;
            }
        }
        return Item.getItemFromBlock(block);
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

    private static class AdAstraDoorStateMapper extends StateMapperBase {

        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            Block block = state.getBlock();
            return new ModelResourceLocation(
                Reference.MOD_ID + ":" + block.getRegistryName().getPath(),
                "facing=" + state.getValue(BlockDoor.FACING).getName()
                    + ",half=" + state.getValue(BlockDoor.HALF).getName()
                    + ",hinge=" + state.getValue(BlockDoor.HINGE).getName()
                    + ",open=" + state.getValue(BlockDoor.OPEN));
        }
    }

    private static class AdAstraTrapDoorStateMapper extends StateMapperBase {

        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            Block block = state.getBlock();
            return new ModelResourceLocation(
                Reference.MOD_ID + ":" + block.getRegistryName().getPath(),
                "facing=" + state.getValue(BlockTrapDoor.FACING).getName()
                    + ",half=" + state.getValue(BlockTrapDoor.HALF).getName()
                    + ",open=" + state.getValue(BlockTrapDoor.OPEN));
        }
    }

    private static class AdAstraFenceGateStateMapper extends StateMapperBase {

        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            Block block = state.getBlock();
            return new ModelResourceLocation(
                Reference.MOD_ID + ":" + block.getRegistryName().getPath(),
                "facing=" + state.getValue(BlockFenceGate.FACING).getName()
                    + ",in_wall=" + state.getValue(BlockFenceGate.IN_WALL)
                    + ",open=" + state.getValue(BlockFenceGate.OPEN));
        }
    }

    private static class AdAstraWallStateMapper extends StateMapperBase {

        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            Block block = state.getBlock();
            return new ModelResourceLocation(
                Reference.MOD_ID + ":" + block.getRegistryName().getPath(),
                "east=" + state.getValue(BlockWall.EAST)
                    + ",north=" + state.getValue(BlockWall.NORTH)
                    + ",south=" + state.getValue(BlockWall.SOUTH)
                    + ",up=" + state.getValue(BlockWall.UP)
                    + ",west=" + state.getValue(BlockWall.WEST));
        }
    }
}
