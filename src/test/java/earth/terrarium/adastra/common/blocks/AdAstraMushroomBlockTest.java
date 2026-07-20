package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.block.state.IBlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdAstraMushroomBlockTest {

    @BeforeAll
    static void initializeVanillaBlockRegistry() {
        Bootstrap.register();
        Blocks.AIR.getRegistryName();
    }

    @Test
    void acceptsMoonMyceliumAndVanillaMushroomGround() {
        TestMushroomBlock mushroom = new TestMushroomBlock();

        assertTrue(mushroom.canSustain(ModBlocks.MOON_MYCELIUM.getDefaultState()));
        assertTrue(mushroom.canSustain(Blocks.GRASS.getDefaultState()));
        assertTrue(mushroom.canSustain(Blocks.DIRT.getDefaultState()));
        assertTrue(mushroom.canSustain(Blocks.FARMLAND.getDefaultState()));
        assertFalse(mushroom.canSustain(Blocks.STONE.getDefaultState()));
    }

    private static final class TestMushroomBlock extends AdAstraMushroomBlock {

        private boolean canSustain(IBlockState state) {
            return canSustainBush(state);
        }
    }
}
