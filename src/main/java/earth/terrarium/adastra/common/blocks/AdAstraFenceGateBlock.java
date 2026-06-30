package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.common.AdAstraCreativeTab;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;

public class AdAstraFenceGateBlock extends BlockFenceGate {

    public AdAstraFenceGateBlock() {
        super(BlockPlanks.EnumType.OAK);
        setCreativeTab(AdAstraCreativeTab.INSTANCE);
        setHardness(2.0f);
        setResistance(3.0f);
    }
}
