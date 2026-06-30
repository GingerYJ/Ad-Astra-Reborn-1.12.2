package earth.terrarium.adastra.common.world;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;

import java.util.Collections;
import java.util.List;

public final class PlanetMobSpawns {

    private PlanetMobSpawns() {
    }

    public static List<Biome.SpawnListEntry> getPossibleCreatures(
        PlanetDimensionProperties properties,
        EnumCreatureType creatureType) {
        Biome biome = properties.getBiome();
        return biome == null ? Collections.<Biome.SpawnListEntry>emptyList() : biome.getSpawnableList(creatureType);
    }
}
