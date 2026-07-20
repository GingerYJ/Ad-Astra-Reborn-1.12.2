package earth.terrarium.adastra.common.world.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.world.chunk.ChunkPrimer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CraterCarverTest {

    @BeforeAll
    static void initializeVanillaBlockRegistry() {
        Bootstrap.register();
        Blocks.AIR.getRegistryName();
    }

    @Test
    void limitsPrimerCarvingToConfiguredTerrainHeight() {
        ChunkPrimer primer = new ChunkPrimer();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y <= 100; y++) {
                    primer.setBlockState(x, y, z, Blocks.STONE.getDefaultState());
                }
            }
        }

        CraterCarver crater = new CraterCarver(8, 8, 50, 5, 5, 1);
        crater.carveInPrimer(primer, 0, 0, Blocks.AIR.getDefaultState(), 64);

        assertEquals(Blocks.AIR, primer.getBlockState(8, 45, 8).getBlock());
        assertEquals(Blocks.STONE, primer.getBlockState(8, 65, 8).getBlock());
    }
}
