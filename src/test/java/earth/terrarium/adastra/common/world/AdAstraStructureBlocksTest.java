package earth.terrarium.adastra.common.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdAstraStructureBlocksTest {

    @BeforeAll
    static void initializeVanillaBlockRegistry() {
        Bootstrap.register();
        Blocks.AIR.getRegistryName();
    }

    @Test
    void mapsModernStructureBlocksToTheirLegacyCounterparts() {
        assertEquals("minecraft:chest", AdAstraStructureBlocks.remapBlockName("minecraft:barrel"));
        assertEquals("minecraft:mob_spawner", AdAstraStructureBlocks.remapBlockName("minecraft:spawner"));
        assertEquals("minecraft:fire", AdAstraStructureBlocks.remapBlockName("minecraft:soul_campfire"));
        assertEquals("minecraft:fire", AdAstraStructureBlocks.remapBlockName("minecraft:soul_fire"));
        assertEquals("minecraft:torch", AdAstraStructureBlocks.remapBlockName("minecraft:soul_torch"));
        assertEquals("minecraft:wall_torch", AdAstraStructureBlocks.remapBlockName("minecraft:soul_wall_torch"));
        assertEquals("minecraft:stained_glass", AdAstraStructureBlocks.remapBlockName("minecraft:blue_stained_glass"));
        assertEquals("minecraft:stained_glass_pane",
            AdAstraStructureBlocks.remapBlockName("minecraft:light_blue_stained_glass_pane"));
        assertEquals("minecraft:wooden_door", AdAstraStructureBlocks.remapBlockName("minecraft:warped_door"));
        assertEquals("minecraft:log", AdAstraStructureBlocks.remapBlockName("minecraft:stripped_warped_wood"));
        assertEquals("minecraft:red_mushroom", AdAstraStructureBlocks.remapBlockName("minecraft:crimson_fungus"));
        assertEquals("minecraft:dead_bush", AdAstraStructureBlocks.remapBlockName("minecraft:warped_roots"));
        assertEquals("minecraft:stone_brick_stairs",
            AdAstraStructureBlocks.remapBlockName("minecraft:polished_blackstone_brick_stairs"));
    }

    @Test
    void preservesSupportedPropertiesAndDropsModernOnlyProperties() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList palette = new NBTTagList();

        NBTTagCompound door = new NBTTagCompound();
        door.setString("Name", "minecraft:warped_door");
        door.setTag("Properties", properties(
            "facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "false",
            "waterlogged", "false"));
        palette.appendTag(door);

        NBTTagCompound stairs = new NBTTagCompound();
        stairs.setString("Name", "minecraft:polished_blackstone_brick_stairs");
        stairs.setTag("Properties", properties(
            "facing", "east", "half", "bottom", "shape", "outer_left", "waterlogged", "false"));
        palette.appendTag(stairs);

        NBTTagCompound glass = new NBTTagCompound();
        glass.setString("Name", "minecraft:blue_stained_glass");
        palette.appendTag(glass);
        tag.setTag("palette", palette);

        AdAstraStructureBlocks.remapPalette(tag);

        assertEquals("minecraft:wooden_door", palette.getCompoundTagAt(0).getString("Name"));
        NBTTagCompound doorProperties = palette.getCompoundTagAt(0).getCompoundTag("Properties");
        assertEquals("south", doorProperties.getString("facing"));
        assertEquals("lower", doorProperties.getString("half"));
        assertEquals("left", doorProperties.getString("hinge"));
        assertFalse(doorProperties.hasKey("waterlogged"));

        assertEquals("minecraft:stone_brick_stairs", palette.getCompoundTagAt(1).getString("Name"));
        assertEquals("outer_left", palette.getCompoundTagAt(1).getCompoundTag("Properties").getString("shape"));
        assertFalse(palette.getCompoundTagAt(1).getCompoundTag("Properties").hasKey("waterlogged"));

        assertEquals("minecraft:stained_glass", palette.getCompoundTagAt(2).getString("Name"));
        assertEquals("blue", palette.getCompoundTagAt(2).getCompoundTag("Properties").getString("color"));
    }

    @Test
    void synchronizesContainerAndSpawnerBlockEntities() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList blocks = new NBTTagList();

        NBTTagCompound barrel = blockWithEntity("minecraft:barrel", "ad_astra:chests/test");
        NBTTagCompound spawner = blockWithEntity("minecraft:spawner", null);
        NBTTagCompound campfire = blockWithEntity("minecraft:soul_campfire", null);
        blocks.appendTag(barrel);
        blocks.appendTag(spawner);
        blocks.appendTag(campfire);
        tag.setTag("blocks", blocks);

        AdAstraStructureBlocks.remapStructureData(tag);

        assertEquals("minecraft:chest", barrel.getCompoundTag("nbt").getString("id"));
        assertEquals("ad_astra:chests/test", barrel.getCompoundTag("nbt").getString("LootTable"));
        assertEquals("minecraft:mob_spawner", spawner.getCompoundTag("nbt").getString("id"));
        assertFalse(campfire.hasKey("nbt"));
    }

    private static NBTTagCompound properties(String... values) {
        NBTTagCompound properties = new NBTTagCompound();
        for (int i = 0; i < values.length; i += 2) {
            properties.setString(values[i], values[i + 1]);
        }
        return properties;
    }

    private static NBTTagCompound blockWithEntity(String id, String lootTable) {
        NBTTagCompound block = new NBTTagCompound();
        NBTTagCompound entity = new NBTTagCompound();
        entity.setString("id", id);
        if (lootTable != null) {
            entity.setString("LootTable", lootTable);
        }
        block.setTag("nbt", entity);
        return block;
    }
}
