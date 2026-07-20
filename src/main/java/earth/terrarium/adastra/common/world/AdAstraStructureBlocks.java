package earth.terrarium.adastra.common.world;

import com.google.common.base.Optional;
import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModEntities;
import earth.terrarium.adastra.common.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
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
                state.setString("Name", "minecraft:air");
                state.removeTag("Properties");
                continue;
            }

            state.setString("Name", remappedName);
            remapProperties(state, originalName, remappedName);
        }
    }

    /** Rewrites item, block-entity, and entity IDs stored in structure payloads. */
    public static void remapStructureData(NBTTagCompound tag) {
        NBTTagList blocks = tag.getTagList("blocks", 10);
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound block = blocks.getCompoundTagAt(i);
            if (block.hasKey("nbt", 10)) {
                if (!remapBlockEntity(block.getCompoundTag("nbt"))) {
                    block.removeTag("nbt");
                }
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

    /**
     * Resolves the placeholder loot marker used by the modern Venus and meteor templates.
     * The marker is intentionally changed at load time so the same NBT can still be shared with
     * the modern resource set while each legacy structure receives its own table.
     */
    static void remapContextLootTables(NBTTagCompound tag, ResourceLocation structure) {
        String lootTable = contextLootTable(structure);
        if (lootTable == null) {
            return;
        }

        NBTTagList blocks = tag.getTagList("blocks", 10);
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound block = blocks.getCompoundTagAt(i);
            if (!block.hasKey("nbt", 10)) {
                continue;
            }
            NBTTagCompound blockEntity = block.getCompoundTag("nbt");
            if ("minecraft:loot".equals(blockEntity.getString("LootTable"))) {
                blockEntity.setString("LootTable", lootTable);
            }
        }
    }

    private static String contextLootTable(ResourceLocation structure) {
        if (structure == null || !Reference.MOD_ID.equals(structure.getNamespace())) {
            return null;
        }

        String path = structure.getPath();
        if ("venus_tower".equals(path)) {
            return Reference.MOD_ID + ":chests/tower/venus/venus_tower";
        }
        if ("warped_watch_tower".equals(path)
            || "crimson_room_2".equals(path)
            || "crimson_room_3".equals(path)
            || "crimson_room_4".equals(path)) {
            return Reference.MOD_ID + ":chests/village/venus/pygro_village";
        }
        if (path.startsWith("meteor")) {
            return Reference.MOD_ID + ":chests/meteor";
        }
        return null;
    }

    public static String remapBlockName(String name) {
        String stainedGlass = remapStainedGlassName(name);
        if (stainedGlass != null) {
            return stainedGlass;
        }
        if ("minecraft:barrel".equals(name)) {
            return "minecraft:chest";
        }
        if ("minecraft:spawner".equals(name)) {
            return "minecraft:mob_spawner";
        }
        if ("minecraft:campfire".equals(name) || "minecraft:soul_campfire".equals(name)
            || "minecraft:soul_fire".equals(name)) {
            return "minecraft:fire";
        }
        if ("minecraft:soul_torch".equals(name)) {
            return "minecraft:torch";
        }
        if ("minecraft:soul_wall_torch".equals(name)) {
            return "minecraft:wall_torch";
        }
        if ("minecraft:lightning_rod".equals(name)) {
            return "minecraft:iron_bars";
        }
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
        if ("minecraft:polished_blackstone_brick_stairs".equals(name)) {
            return "minecraft:stone_brick_stairs";
        }
        if ("minecraft:polished_blackstone_brick_slab".equals(name)
            || "minecraft:polished_blackstone_slab".equals(name)
            || "minecraft:blackstone_slab".equals(name)) {
            return "minecraft:stone_slab";
        }
        if ("minecraft:polished_blackstone".equals(name) || "minecraft:polished_blackstone_bricks".equals(name)) {
            return "minecraft:stonebrick";
        }
        if ("minecraft:quartz_bricks".equals(name)) {
            return "minecraft:quartz_block";
        }
        if ("minecraft:quartz_slab".equals(name) || "minecraft:smooth_quartz_slab".equals(name)) {
            return "minecraft:stone_slab";
        }
        if ("minecraft:quartz_stairs".equals(name) || "minecraft:smooth_quartz_stairs".equals(name)) {
            return "minecraft:stone_brick_stairs";
        }
        if ("minecraft:smooth_quartz".equals(name)) {
            return "minecraft:quartz_block";
        }
        if ("minecraft:shroomlight".equals(name)) {
            return "minecraft:sea_lantern";
        }
        if (name.endsWith("_door") && (name.startsWith("minecraft:warped_") || name.startsWith("minecraft:crimson_"))) {
            return "minecraft:wooden_door";
        }
        if (name.endsWith("_trapdoor") && (name.startsWith("minecraft:warped_") || name.startsWith("minecraft:crimson_"))) {
            return "minecraft:trapdoor";
        }
        if (name.endsWith("_fence_gate") && (name.startsWith("minecraft:warped_") || name.startsWith("minecraft:crimson_"))) {
            return "minecraft:fence_gate";
        }
        if (name.endsWith("_fence") && (name.startsWith("minecraft:warped_") || name.startsWith("minecraft:crimson_"))) {
            return "minecraft:fence";
        }
        if (name.endsWith("_button") && (name.startsWith("minecraft:warped_") || name.startsWith("minecraft:crimson_"))) {
            return "minecraft:wooden_button";
        }
        if (name.endsWith("_pressure_plate") && (name.startsWith("minecraft:warped_") || name.startsWith("minecraft:crimson_"))) {
            return "minecraft:wooden_pressure_plate";
        }
        if (name.endsWith("_stairs") && (name.startsWith("minecraft:warped_") || name.startsWith("minecraft:crimson_"))) {
            return "minecraft:oak_stairs";
        }
        if (name.endsWith("_slab") && (name.startsWith("minecraft:warped_") || name.startsWith("minecraft:crimson_"))) {
            return "minecraft:wooden_slab";
        }
        if ("minecraft:warped_planks".equals(name) || "minecraft:crimson_planks".equals(name)) {
            return "minecraft:planks";
        }
        if ("minecraft:warped_wood".equals(name) || "minecraft:crimson_wood".equals(name)
            || "minecraft:stripped_warped_wood".equals(name)
            || "minecraft:stripped_crimson_wood".equals(name)
            || name.endsWith("_stem") || name.endsWith("_hyphae")) {
            return "minecraft:log";
        }
        if ("minecraft:warped_fungus".equals(name) || "minecraft:crimson_fungus".equals(name)) {
            return "minecraft:red_mushroom";
        }
        if ("minecraft:warped_roots".equals(name) || "minecraft:crimson_roots".equals(name)
            || "minecraft:nether_sprouts".equals(name)) {
            return "minecraft:dead_bush";
        }
        if ("minecraft:twisting_vines".equals(name) || "minecraft:weeping_vines".equals(name)) {
            return "minecraft:vine";
        }
        if ("minecraft:warped_nylium".equals(name) || "minecraft:crimson_nylium".equals(name)
            || "minecraft:warped_wart_block".equals(name) || "minecraft:nether_wart_block".equals(name)) {
            return "minecraft:netherrack";
        }
        return remapImportedBlockName(name);
    }

    public static boolean isKnownBlock(String name) {
        ResourceLocation location = parse(name);
        return location != null && Block.REGISTRY.containsKey(location);
    }

    private static void remapProperties(NBTTagCompound state, String originalName, String remappedName) {
        Block target = Block.REGISTRY.getObject(parse(remappedName));
        if (target == null) {
            state.removeTag("Properties");
            return;
        }

        NBTTagCompound source = state.hasKey("Properties", 10)
            ? state.getCompoundTag("Properties") : null;
        NBTTagCompound filtered = new NBTTagCompound();
        if (source != null) {
            for (String key : source.getKeySet()) {
                String value = source.getString(key);
                String targetKey = key;
                if ("type".equals(key) && findProperty(target, "type") == null
                    && findProperty(target, "half") != null) {
                    targetKey = "half";
                    if ("double".equals(value)) {
                        value = "bottom";
                    }
                }

                IProperty<?> property = findProperty(target, targetKey);
                if (property == null || !isValidPropertyValue(property, value)) {
                    continue;
                }
                filtered.setString(targetKey, value);
            }
        }

        String color = stainedGlassColor(originalName);
        if (color != null && findProperty(target, "color") != null
            && isValidPropertyValue(findProperty(target, "color"), color)) {
            filtered.setString("color", color);
        }

        if (filtered.getKeySet().isEmpty()) {
            state.removeTag("Properties");
        } else {
            state.setTag("Properties", filtered);
        }
    }

    private static IProperty<?> findProperty(Block block, String name) {
        for (IProperty<?> property : block.getBlockState().getProperties()) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        return null;
    }

    private static boolean isValidPropertyValue(IProperty<?> property, String value) {
        Optional<?> parsed = property.parseValue(value);
        return parsed.isPresent();
    }

    private static String remapStainedGlassName(String name) {
        if (stainedGlassColor(name) == null) {
            return null;
        }
        return name.endsWith("_pane") ? "minecraft:stained_glass_pane" : "minecraft:stained_glass";
    }

    private static String stainedGlassColor(String name) {
        if (!name.startsWith("minecraft:")) {
            return null;
        }
        String path = name.substring("minecraft:".length());
        String suffix = path.endsWith("_pane") ? "_stained_glass_pane" : "_stained_glass";
        if (!path.endsWith(suffix)) {
            return null;
        }
        String color = path.substring(0, path.length() - suffix.length());
        switch (color) {
            case "white":
            case "orange":
            case "magenta":
            case "light_blue":
            case "yellow":
            case "lime":
            case "pink":
            case "gray":
            case "light_gray":
            case "cyan":
            case "purple":
            case "blue":
            case "brown":
            case "green":
            case "red":
            case "black":
                return color;
            default:
                return null;
        }
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

    private static boolean remapBlockEntity(NBTTagCompound blockEntity) {
        if (blockEntity.hasKey("id", 8)) {
            String id = blockEntity.getString("id");
            if ("minecraft:barrel".equals(id) || "minecraft:spawner".equals(id)) {
                blockEntity.setString("id", "minecraft:" + ("minecraft:barrel".equals(id) ? "chest" : "mob_spawner"));
            } else if ("minecraft:campfire".equals(id) || "minecraft:soul_campfire".equals(id)) {
                return false;
            } else {
                String remappedId = remapModBlockEntityId(id);
                if (!id.equals(remappedId)) {
                    blockEntity.setString("id", remappedId);
                }
            }
        }
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
                if (potential.hasKey("entity", 10)) {
                    remapEntityData(potential.getCompoundTag("entity"));
                }
            }
        }
        return true;
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
        if (entity.hasKey("entity", 10)) {
            remapEntityData(entity.getCompoundTag("entity"));
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

    private static String remapModBlockEntityId(String name) {
        ResourceLocation location = parse(name);
        if (location == null || !Reference.MOD_ID.equals(location.getNamespace())) {
            return name;
        }

        String path = location.getPath();
        if (path.endsWith("_globe")) {
            return Reference.MOD_ID + ":globe";
        }
        if (path.endsWith("_sliding_door") || "airlock".equals(path) || "reinforced_door".equals(path)) {
            return Reference.MOD_ID + ":sliding_door";
        }
        if (path.endsWith("_flag")) {
            return Reference.MOD_ID + ":flag";
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
