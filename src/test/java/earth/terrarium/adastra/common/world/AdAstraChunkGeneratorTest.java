package earth.terrarium.adastra.common.world;

import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdAstraChunkGeneratorTest {

    @Test
    void recognizesChunkBoundariesIncludingNegativeCoordinates() {
        assertTrue(AdAstraChunkGenerator.isInChunk(new BlockPos(0, 40, 0), 0, 0));
        assertTrue(AdAstraChunkGenerator.isInChunk(new BlockPos(15, 40, 15), 0, 0));
        assertFalse(AdAstraChunkGenerator.isInChunk(new BlockPos(16, 40, 0), 0, 0));
        assertFalse(AdAstraChunkGenerator.isInChunk(new BlockPos(0, 40, 16), 0, 0));

        assertTrue(AdAstraChunkGenerator.isInChunk(new BlockPos(-1, 40, -1), -1, -1));
        assertTrue(AdAstraChunkGenerator.isInChunk(new BlockPos(-16, 40, -16), -1, -1));
        assertFalse(AdAstraChunkGenerator.isInChunk(new BlockPos(-17, 40, -1), -1, -1));
    }
}
