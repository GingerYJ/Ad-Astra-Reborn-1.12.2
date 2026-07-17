package earth.terrarium.adastra.common.world;

import earth.terrarium.adastra.Reference;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

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

    public static String remapBlockName(String name) {
        if (name != null && name.startsWith("ad_extendra:")) {
            return Reference.MOD_ID + ":" + name.substring("ad_extendra:".length());
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
        return name;
    }

    public static boolean isKnownBlock(String name) {
        return Block.REGISTRY.containsKey(new ResourceLocation(name));
    }
}
