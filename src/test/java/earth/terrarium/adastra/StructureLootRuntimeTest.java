package earth.terrarium.adastra;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StructureLootRuntimeTest {

    @BeforeAll
    static void initializeVanillaRegistries() {
        Bootstrap.register();
    }

    @Test
    void structureLootTablesAreNonEmptyInTheLegacyLoader() throws Exception {
        Path resources = Path.of("src/main/resources");
        Set<String> tableIds = structureLootTableIds(resources);
        assertFalse(tableIds.isEmpty(), "No structure loot tables were found");

        Set<String> itemIds = lootTableItemIds(resources);
        registerPlaceholderItems(itemIds);

        LootTableManager manager = new LootTableManager(null);
        for (String tableId : tableIds) {
            LootTable table = manager.getLootTableFromLocation(new ResourceLocation(tableId));
            assertTrue(hasPools(table), "Legacy loader returned an empty table for " + tableId);
        }
    }

    private static Set<String> structureLootTableIds(Path resources) throws IOException {
        Set<String> ids = new LinkedHashSet<String>();
        Path structureRoot = resources.resolve("data/ad_astra/structures");
        try (Stream<Path> paths = Files.walk(structureRoot)) {
            for (Path path : paths.filter(file -> file.toString().endsWith(".nbt")).toList()) {
                try (InputStream input = Files.newInputStream(path)) {
                    NBTTagCompound root = CompressedStreamTools.readCompressed(input);
                    NBTTagList blocks = root.getTagList("blocks", 10);
                    for (int i = 0; i < blocks.tagCount(); i++) {
                        NBTTagCompound block = blocks.getCompoundTagAt(i);
                        if (!block.hasKey("nbt", 10)) {
                            continue;
                        }
                        NBTTagCompound entity = block.getCompoundTag("nbt");
                        if (entity.hasKey("LootTable", 8)) {
                            String lootTable = entity.getString("LootTable");
                            ids.add(lootTable);
                            if ("minecraft:loot".equals(lootTable)) {
                                ids.add("ad_astra:chests/meteor");
                            }
                        }
                    }
                }
            }
        }
        return ids;
    }

    private static Set<String> lootTableItemIds(Path resources) throws IOException {
        Set<String> ids = new LinkedHashSet<String>();
        Path lootRoot = resources.resolve("assets/ad_astra/loot_tables");
        try (Stream<Path> paths = Files.walk(lootRoot)) {
            for (Path path : paths.filter(file -> file.toString().endsWith(".json")).toList()) {
                JsonElement root = JsonParser.parseString(Files.readString(path));
                collectItemNames(root, ids);
            }
        }
        return ids;
    }

    private static void collectItemNames(JsonElement element, Set<String> ids) {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement child : array) {
                collectItemNames(child, ids);
            }
        } else if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("type") && "item".equals(object.get("type").getAsString())
                && object.has("name")) {
                ids.add(object.get("name").getAsString());
            }
            for (JsonElement child : object.entrySet().stream().map(java.util.Map.Entry::getValue).toList()) {
                collectItemNames(child, ids);
            }
        }
    }

    private static void registerPlaceholderItems(Set<String> itemIds) throws Exception {
        for (String itemId : itemIds) {
            ResourceLocation location = new ResourceLocation(itemId);
            if (ForgeRegistries.ITEMS.getValue(location) != null) {
                continue;
            }
            Item item = new Item();
            item.setRegistryName(location);
            ForgeRegistries.ITEMS.register(item);
        }
    }

    private static boolean hasPools(LootTable table) throws Exception {
        Field pools = null;
        for (Field field : LootTable.class.getDeclaredFields()) {
            if (List.class.isAssignableFrom(field.getType())) {
                pools = field;
                break;
            }
        }
        assertTrue(pools != null, "Could not inspect legacy LootTable pools");
        pools.setAccessible(true);
        Object value = pools.get(table);
        return value instanceof List && !((List<?>) value).isEmpty();
    }
}
