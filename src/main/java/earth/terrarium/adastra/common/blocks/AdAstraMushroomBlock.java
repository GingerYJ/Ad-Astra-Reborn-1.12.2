package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;

public class AdAstraMushroomBlock extends BlockBush {

    public AdAstraMushroomBlock() {
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(0.0f);
        setLightLevel(0.375f);
        setSoundType(SoundType.PLANT);
    }
}
