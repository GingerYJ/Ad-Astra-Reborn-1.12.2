package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModEntities;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;

/**
 * Shared block-name remapping for Ad Astra structure NBTs authored in 1.16+ (Mojang's newer
 * palette format references blocks that do not exist in 1.12.2). Both the single-structure
 * generator and the jigsaw generator route their palettes through here so ruins render with
 * sensible 1.12 equivalents instead of collapsing to air.
 */
public final class AdAstraStructureBlocks {

    private AdAstraStructureBlocks() {
    }

    /** Rewrites every palette entry in-place to a known 1.12 block, falling back to air. */
    public static void remapPalette(NBTTagCompound tag) {
        NBTTagList palette = tag.getTagList("palette", 10);
        for (int i = 0; i < palette.tagCount(); i++) {
            NBTTagCompound state = palette.getCompoundTagAt(i);
            if (!state.hasKey("Name", 8)) {
                continue;
            }

            String originalName = state.getString("Name");
            String remappedName = remapBlockName(originalName);
            if (!isKnownBlock(remappedName)) {
                remappedName = "minecraft:air";
            }

            if (!originalName.equals(remappedName)) {
                state.setString("Name", remappedName);
                state.removeTag("Properties");
            }
        }
    }

    /** Rewrites item and entity IDs stored in structure block entities and entity payloads. */
    public static void remapStructureData(NBTTagCompound tag) {
        NBTTagList blocks = tag.getTagList("blocks", 10);
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound block = blocks.getCompoundTagAt(i);
            if (block.hasKey("nbt", 10)) {
                remapBlockEntity(block.getCompoundTag("nbt"));
            }
        }

        NBTTagList entities = tag.getTagList("entities", 10);
        for (int i = 0; i < entities.tagCount(); i++) {
            NBTTagCompound entity = entities.getCompoundTagAt(i);
            if (entity.hasKey("nbt", 10)) {
                remapEntityData(entity.getCompoundTag("nbt"));
            }
        }
    }

    public static String remapBlockName(String name) {
        if ("minecraft:soul_soil".equals(name)) {
            return "minecraft:soul_sand";
        }
        if ("minecraft:basalt".equals(name) || "minecraft:polished_basalt".equals(name)) {
            return Reference.MOD_ID + ":sky_stone";
        }
        if ("minecraft:blackstone".equals(name) || "minecraft:crying_obsidian".equals(name)) {
            return "minecraft:obsidian";
        }
        if ("minecraft:gilded_blackstone".equals(name)) {
            return "minecraft:coal_ore";
        }
        if ("minecraft:magma_block".equals(name)) {
            return "minecraft:magma";
        }
        if ("minecraft:soul_fire".equals(name)) {
            return "minecraft:air";
        }
        // 1.16+ decorative blocks -> closest 1.12 equivalents
        if ("minecraft:chain".equals(name)) {
            return "minecraft:iron_bars";
        }
        if ("minecraft:soul_lantern".equals(name) || "minecraft:lantern".equals(name)) {
            return "minecraft:redstone_lamp";
        }
        if ("minecraft:blackstone_wall".equals(name) || "minecraft:polished_blackstone_wall".equals(name)) {
            return "minecraft:cobblestone_wall";
        }
        if ("minecraft:polished_blackstone_stairs".equals(name) || "minecraft:blackstone_stairs".equals(name)) {
            return "minecraft:stone_brick_stairs";
        }
        if ("minecraft:polished_blackstone".equals(name) || "minecraft:polished_blackstone_bricks".equals(name)) {
            return "minecraft:stonebrick";
        }
        if ("minecraft:quartz_bricks".equals(name)) {
            return "minecraft:quartz_block";
        }
        if ("minecraft:quartz_slab".equals(name) || "minecraft:smooth_quartz_slab".equals(name)) {
            return "minecraft:quartz_block";
        }
        if ("minecraft:quartz_stairs".equals(name) || "minecraft:smooth_quartz_stairs".equals(name)) {
            return "minecraft:quartz_block";
        }
        if ("minecraft:smooth_quartz".equals(name)) {
            return "minecraft:quartz_block";
        }
        if ("minecraft:shroomlight".equals(name)) {
            return "minecraft:sea_lantern";
        }
        if ("minecraft:orange_stained_glass".equals(name)) {
            return "minecraft:stained_glass";
        }
        if ("minecraft:black_stained_glass_pane".equals(name)) {
            return "minecraft:stained_glass_pane";
        }
        return remapImportedBlockName(name);
    }

    public static boolean isKnownBlock(String name) {
        return Block.REGISTRY.containsKey(new ResourceLocation(name));
    }

    private static String remapImportedBlockName(String name) {
        ResourceLocation location = parse(name);
        if (location == null || !Reference.MOD_ID.equals(location.getNamespace())
            || location.getPath().startsWith("block_")) {
            return name;
        }

        Block block = ModBlocks.get(location.getPath());
        if (block == null || block.getRegistryName() == null
            || !block.getRegistryName().getPath().startsWith("block_")) {
            return name;
        }
        return block.getRegistryName().toString();
    }

    private static void remapBlockEntity(NBTTagCompound blockEntity) {
        if (blockEntity.hasKey("Items", 9)) {
            remapItemList(blockEntity.getTagList("Items", 10));
        }
        if (blockEntity.hasKey("item", 10)) {
            remapItemCompound(blockEntity.getCompoundTag("item"));
        }
        if (blockEntity.hasKey("SpawnData", 10)) {
            remapEntityData(blockEntity.getCompoundTag("SpawnData"));
        }
        if (blockEntity.hasKey("SpawnPotentials", 9)) {
            NBTTagList potentials = blockEntity.getTagList("SpawnPotentials", 10);
            for (int i = 0; i < potentials.tagCount(); i++) {
                NBTTagCompound potential = potentials.getCompoundTagAt(i);
                if (potential.hasKey("Entity", 10)) {
                    remapEntityData(potential.getCompoundTag("Entity"));
                }
            }
        }
    }

    private static void remapItemList(NBTTagList items) {
        for (int i = 0; i < items.tagCount(); i++) {
            remapItemCompound(items.getCompoundTagAt(i));
        }
    }

    private static void remapItemCompound(NBTTagCompound item) {
        if (item.hasKey("id", 8)) {
            item.setString("id", remapItemId(item.getString("id")));
        }
    }

    private static void remapEntityData(NBTTagCompound entity) {
        if (entity.hasKey("id", 8)) {
            entity.setString("id", remapEntityId(entity.getString("id")));
        }
        if (entity.hasKey("Entity", 10)) {
            remapEntityData(entity.getCompoundTag("Entity"));
        }
        if (entity.hasKey("SpawnData", 10)) {
            remapEntityData(entity.getCompoundTag("SpawnData"));
        }
    }

    private static String remapItemId(String name) {
        ResourceLocation location = parse(name);
        if (location == null || !Reference.MOD_ID.equals(location.getNamespace())
            || location.getPath().startsWith("item_")) {
            return name;
        }

        net.minecraft.item.Item item = ModItems.get(location.getPath());
        if (item != null && item.getRegistryName() != null
            && item.getRegistryName().getPath().startsWith("item_")) {
            return item.getRegistryName().toString();
        }

        Block block = ModBlocks.get(location.getPath());
        if (block != null && block.getRegistryName() != null
            && block.getRegistryName().getPath().startsWith("block_")) {
            return Reference.MOD_ID + ":item_" + location.getPath();
        }
        return name;
    }

    private static String remapEntityId(String name) {
        ResourceLocation location = parse(name);
        if (location == null || !Reference.MOD_ID.equals(location.getNamespace())
            || location.getPath().startsWith("entity_")) {
            return name;
        }

        for (EntityEntry entry : ModEntities.ENTITIES) {
            ResourceLocation registryName = entry.getRegistryName();
            if (registryName != null && registryName.getPath().startsWith("entity_")
                && registryName.getPath().substring("entity_".length()).equals(location.getPath())) {
                return registryName.toString();
            }
        }
        return name;
    }

    private static ResourceLocation parse(String name) {
        try {
            return new ResourceLocation(name);
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
