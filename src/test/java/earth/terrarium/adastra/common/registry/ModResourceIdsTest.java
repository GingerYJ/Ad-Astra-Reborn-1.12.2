package earth.terrarium.adastra.common.registry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModResourceIdsTest {

    @Test
    void createsCanonicalCategoryIds() {
        assertEquals("ad_astra", ModResourceIds.NAMESPACE);
        assertEquals("block_juperium_block", ModResourceIds.blockPath("juperium_block"));
        assertEquals("item_ice_charge", ModResourceIds.itemPath("ice_charge"));
        assertEquals("entity_freeze", ModResourceIds.entityPath("freeze"));
        assertEquals("biome_centaurian_plains", ModResourceIds.biomePath("centaurian_plains"));
        assertEquals("planet_proxima_centauri_b", ModResourceIds.planetPath("proxima_centauri_b"));
        assertEquals("structure_glacio_hut", ModResourceIds.structurePath("glacio_hut"));
        assertEquals("recipe_ice_charge", ModResourceIds.recipePath("ice_charge"));
        assertEquals("particle_freeze", ModResourceIds.particlePath("freeze"));
    }

    @Test
    void doesNotDuplicateCategoryPrefixes() {
        assertEquals("block_juperium_block", ModResourceIds.blockPath("block_juperium_block"));
        assertEquals("item_ice_charge", ModResourceIds.itemPath("item_ice_charge"));
        assertEquals("planet_ceres", ModResourceIds.planetPath("planet_ceres"));
    }

    @Test
    void normalizesCaseAndExposesPaths() {
        assertEquals("block_ceres_stone", ModResourceIds.blockPath("Ceres_Stone"));
        assertEquals("item_freeze_shard", ModResourceIds.itemPath("FREEZE_SHARD"));
        assertEquals("planet_ceres", ModResourceIds.planetPath("Ceres"));
        assertEquals("planet_ceres", ModResourceIds.planetPath("Ceres"));
    }

    @Test
    void rejectsInvalidResourceNames() {
        assertThrows(IllegalArgumentException.class, () -> ModResourceIds.blockPath(null));
        assertThrows(IllegalArgumentException.class, () -> ModResourceIds.blockPath(""));
        assertThrows(IllegalArgumentException.class, () -> ModResourceIds.blockPath(" leading_space"));
        assertThrows(IllegalArgumentException.class, () -> ModResourceIds.blockPath("has whitespace"));
        assertThrows(IllegalArgumentException.class, () -> ModResourceIds.blockPath("other:namespace"));
    }
}
