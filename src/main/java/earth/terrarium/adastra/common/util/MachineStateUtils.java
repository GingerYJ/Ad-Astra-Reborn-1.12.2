package earth.terrarium.adastra.common.util;

import earth.terrarium.adastra.common.blocks.AdAstraAttachedMachineBlock;
import earth.terrarium.adastra.common.blocks.AdAstraMachineBlock;
import net.minecraft.block.state.IBlockState;

public final class MachineStateUtils {

    private MachineStateUtils() {
    }

    public static boolean hasLitProperty(IBlockState state) {
        return state != null
            && (state.getPropertyKeys().contains(AdAstraMachineBlock.LIT)
            || state.getPropertyKeys().contains(AdAstraAttachedMachineBlock.LIT));
    }

    public static boolean isLit(IBlockState state) {
        if (state == null) {
            return false;
        }
        if (state.getPropertyKeys().contains(AdAstraMachineBlock.LIT)) {
            return state.getValue(AdAstraMachineBlock.LIT);
        }
        if (state.getPropertyKeys().contains(AdAstraAttachedMachineBlock.LIT)) {
            return state.getValue(AdAstraAttachedMachineBlock.LIT);
        }
        return false;
    }

    public static IBlockState withLit(IBlockState state, boolean lit) {
        if (state.getPropertyKeys().contains(AdAstraMachineBlock.LIT)) {
            return state.withProperty(AdAstraMachineBlock.LIT, lit);
        }
        if (state.getPropertyKeys().contains(AdAstraAttachedMachineBlock.LIT)) {
            return state.withProperty(AdAstraAttachedMachineBlock.LIT, lit);
        }
        return state;
    }
}
