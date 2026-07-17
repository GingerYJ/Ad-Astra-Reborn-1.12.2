package earth.terrarium.adastra.common.registry;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.entities.mob.ExtendraFreezeEntity;
import earth.terrarium.adastra.common.world.PlanetBiome;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;

/** The fifteen biome entries used by the imported celestial bodies. */
public final class ExtendraBiomes {

    public static final PlanetBiome INTRIGUING_WASTELANDS = biome("intriguing_wastelands", "Intriguing Wastelands", "ceres", 0.6F, 0.0F);
    public static final PlanetBiome COSMIC_GASLANDS = biome("cosmic_gaslands", "Cosmic Gaslands", "jupiter", 0.6F, 0.0F);
    public static final PlanetBiome CRONIAN_GASLANDS = biome("cronian_gaslands", "Cronian Gaslands", "saturn", 0.2F, 0.0F);
    public static final PlanetBiome URANUS_ICE_PEAKS = biome("uranus_ice_peaks", "Uranus Ice Peaks", "uranus", -0.7F, 0.0F);
    public static final PlanetBiome WINDY_WASTELANDS = biome("windy_wastelands", "Windy Wastelands", "neptune", -0.4F, 0.0F);
    public static final PlanetBiome ORCEAN_WASTELANDS = biome("orcean_wastelands", "Orcean Wastelands", "orcus", 0.6F, 0.0F);
    public static final PlanetBiome PLUTONIAN_BARRENS = biome("plutonian_barrens", "Plutonian Barrens", "pluto", -0.5F, 0.0F);
    public static final PlanetBiome HAUMEAN_WASTELANDS = biome("haumean_wastelands", "Haumean Wastelands", "haumea", 0.6F, 0.0F);
    public static final PlanetBiome QUAOARIAN_BARRENS = biome("quaoarian_barrens", "Quaoarian Barrens", "quaoar", 0.6F, 0.0F);
    public static final PlanetBiome MAKEMAKEAN_BARRENS = biome("makemakean_barrens", "Makemakean Barrens", "makemake", 0.6F, 0.0F);
    public static final PlanetBiome CRYOVOLCANIC_WASTELANDS = biome("cryovolcanic_wastelands", "Cryovolcanic Wastelands", "gonggong", 0.4F, 0.0F);
    public static final PlanetBiome ERIDIAN_DELTAS = biome("eridian_deltas", "Eridian Deltas", "eris", -0.6F, 0.0F);
    public static final PlanetBiome SEDNIAN_BARRENS = biome("sednian_barrens", "Sednian Barrens", "sedna", 0.2F, 0.0F);
    public static final PlanetBiome CENTAURIAN_PLAINS = biome("centaurian_plains", "Centaurian Plains", "b", 0.4F, 0.2F);
    public static final PlanetBiome CENTAURIAN_BEACH = biome("centaurian_beach", "Centaurian Beach", "b", 0.4F, 0.0F);

    static {
        URANUS_ICE_PEAKS.addMonsterSpawn(ExtendraFreezeEntity.class, 100, 1, 3);
        CENTAURIAN_PLAINS.addCreatureSpawn(EntitySheep.class, 12, 2, 4);
    }

    private ExtendraBiomes() {
    }

    public static void register(RegistryEvent.Register<Biome> event) {
        event.getRegistry().registerAll(
            INTRIGUING_WASTELANDS, COSMIC_GASLANDS, CRONIAN_GASLANDS, URANUS_ICE_PEAKS,
            WINDY_WASTELANDS, ORCEAN_WASTELANDS, PLUTONIAN_BARRENS, HAUMEAN_WASTELANDS,
            QUAOARIAN_BARRENS, MAKEMAKEAN_BARRENS, CRYOVOLCANIC_WASTELANDS, ERIDIAN_DELTAS,
            SEDNIAN_BARRENS, CENTAURIAN_PLAINS, CENTAURIAN_BEACH);
        BiomeDictionary.addTypes(INTRIGUING_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(COSMIC_GASLANDS, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(CRONIAN_GASLANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(URANUS_ICE_PEAKS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.SNOWY);
        BiomeDictionary.addTypes(WINDY_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(ORCEAN_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(PLUTONIAN_BARRENS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(HAUMEAN_WASTELANDS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(QUAOARIAN_BARRENS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(MAKEMAKEAN_BARRENS, BiomeDictionary.Type.DRY, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(CRYOVOLCANIC_WASTELANDS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(ERIDIAN_DELTAS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(SEDNIAN_BARRENS, BiomeDictionary.Type.COLD, BiomeDictionary.Type.WASTELAND);
        BiomeDictionary.addTypes(CENTAURIAN_PLAINS, BiomeDictionary.Type.PLAINS);
        BiomeDictionary.addTypes(CENTAURIAN_BEACH, BiomeDictionary.Type.BEACH);
    }

    private static PlanetBiome biome(String name, String displayName, String planet, float temperature, float rainfall) {
        IBlockState surface = ExtendraBlocks.getPlanetSurface(planet).getDefaultState();
        IBlockState stone = ExtendraBlocks.getPlanetStone(planet).getDefaultState();
        PlanetBiome biome = new PlanetBiome(displayName, surface, stone, temperature, rainfall);
        biome.setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        return biome;
    }
}
