package earth.terrarium.adastra.client;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.client.particle.ParticleAcidRain;
import earth.terrarium.adastra.client.particle.ParticleCryoFreeze;
import earth.terrarium.adastra.client.particle.ParticleLargeFlame;
import earth.terrarium.adastra.client.particle.ParticleLargeSmoke;
import earth.terrarium.adastra.client.particle.ParticleOxygenBubble;
import earth.terrarium.adastra.client.particle.ParticleOxygenVent;
import earth.terrarium.adastra.client.render.AdAstraEntityRenderers;
import earth.terrarium.adastra.client.render.TileEnergizerRenderer;
import earth.terrarium.adastra.client.render.TileFlagRenderer;
import earth.terrarium.adastra.client.render.TileGlobeRenderer;
import earth.terrarium.adastra.client.render.TileGravityNormalizerRenderer;
import earth.terrarium.adastra.client.render.TileOxygenDistributorRenderer;
import earth.terrarium.adastra.client.render.TileSlidingDoorRenderer;
import earth.terrarium.adastra.client.render.TileWaterPumpRenderer;
import earth.terrarium.adastra.client.render.VehicleItemStackRenderer;
import earth.terrarium.adastra.common.tile.FlagTileEntity;
import earth.terrarium.adastra.common.tile.GlobeTileEntity;
import earth.terrarium.adastra.common.tile.EnergizerTileEntity;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import earth.terrarium.adastra.common.tile.SlidingDoorTileEntity;
import earth.terrarium.adastra.common.tile.WaterPumpTileEntity;
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
import earth.terrarium.adastra.common.registry.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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

    public static void registerTileEntityRenderers() {
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(GlobeTileEntity.class, new TileGlobeRenderer());
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(FlagTileEntity.class, new TileFlagRenderer());
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(SlidingDoorTileEntity.class, new TileSlidingDoorRenderer());
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(EnergizerTileEntity.class, new TileEnergizerRenderer());
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(OxygenDistributorTileEntity.class, new TileOxygenDistributorRenderer());
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(GravityNormalizerTileEntity.class, new TileGravityNormalizerRenderer());
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(WaterPumpTileEntity.class, new TileWaterPumpRenderer());
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
        registerGravityNormalizerModels();
        registerOxygenDistributorTopModel();
    }

    public static void registerParticles() {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.effectRenderer.registerParticle(ModParticles.ACID_RAIN, new ParticleAcidRain.Factory());
        minecraft.effectRenderer.registerParticle(ModParticles.LARGE_FLAME, new ParticleLargeFlame.Factory());
        minecraft.effectRenderer.registerParticle(ModParticles.LARGE_SMOKE, new ParticleLargeSmoke.Factory());
        minecraft.effectRenderer.registerParticle(ModParticles.OXYGEN_BUBBLE, new ParticleOxygenBubble.Factory());
        minecraft.effectRenderer.registerParticle(ModParticles.OXYGEN_VENT, new ParticleOxygenVent.Factory());
        minecraft.effectRenderer.registerParticle(ModParticles.CRYO_FREEZE, new ParticleCryoFreeze.Factory());
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
        // For 1.12.2, just use the item's registry name as the model location
        // The model loader will automatically look for models/item/{name}.json
        ModelLoader.setCustomModelResourceLocation(
            item,
            0,
            new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void registerBlockItemModel(Block block) {
        Item item = getRegisteredBlockItem(block);
        if (item == null || item == Items.AIR || item.getRegistryName() == null) {
            return;
        }
        if (block instanceof AdAstraGlobeBlock) {
            item.setTileEntityItemStackRenderer(TileGlobeRenderer.ITEM_RENDERER);
            registerItemModel(item);
            return;
        }
        if (block == ModBlocks.OXYGEN_DISTRIBUTOR) {
            item.setTileEntityItemStackRenderer(TileOxygenDistributorRenderer.ITEM_RENDERER);
            registerItemModel(item);
            return;
        }
        if (block == ModBlocks.GRAVITY_NORMALIZER) {
            item.setTileEntityItemStackRenderer(TileGravityNormalizerRenderer.ITEM_RENDERER);
            registerItemModel(item);
            return;
        }
        if (block instanceof AdAstraSlidingDoorBlock) {
            registerSlidingDoorRenderVariants(item, block);
            registerItemModel(item);
            return;
        }
        if (block instanceof AdAstraWallBlock) {
            ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, model);
            ModelLoader.setCustomModelResourceLocation(item, 1, model);
            return;
        }
        registerItemModel(item);
    }

    private static void registerSlidingDoorRenderVariants(Item item, Block block) {
        if (block.getRegistryName() == null) {
            return;
        }

        if (block == ModBlocks.AIRLOCK || block == ModBlocks.REINFORCED_DOOR) {
            net.minecraft.client.renderer.block.model.ModelBakery.registerItemVariants(
                item,
                new ModelResourceLocation(block.getRegistryName(), "normal"),
                new ModelResourceLocation(block.getRegistryName(), "flipped"));
            return;
        }

        net.minecraft.client.renderer.block.model.ModelBakery.registerItemVariants(
            item,
            new ModelResourceLocation(block.getRegistryName(), "normal"));
    }

    private static void registerOxygenDistributorTopModel() {
        Item item = getRegisteredBlockItem(ModBlocks.OXYGEN_DISTRIBUTOR);
        if (item == null || item == Items.AIR) {
            return;
        }
        net.minecraft.client.renderer.block.model.ModelBakery.registerItemVariants(
            item,
            TileOxygenDistributorRenderer.TOP_MODEL);
    }

    private static void registerGravityNormalizerModels() {
        Item item = getRegisteredBlockItem(ModBlocks.GRAVITY_NORMALIZER);
        if (item == null || item == Items.AIR) {
            return;
        }
        net.minecraft.client.renderer.block.model.ModelBakery.registerItemVariants(
            item,
            TileGravityNormalizerRenderer.TOP_MODEL,
            TileGravityNormalizerRenderer.TOE_MODEL);
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
            return new ModelResourceLocation(state.getBlock().getRegistryName(), "normal");
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
