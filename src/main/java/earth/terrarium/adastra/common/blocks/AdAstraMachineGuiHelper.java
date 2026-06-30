package earth.terrarium.adastra.common.blocks;

import earth.terrarium.adastra.AdAstraReborn;
import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class AdAstraMachineGuiHelper {

    private AdAstraMachineGuiHelper() {
    }

    static boolean openMachineGui(World world, BlockPos pos, EntityPlayer player, EnumHand hand) {
        if (hand != EnumHand.MAIN_HAND) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof AdAstraMachineTileEntity)) {
            return false;
        }
        int guiId = AdAstraMachineContainer.idFor((AdAstraMachineTileEntity) tile);
        if (guiId < 0) {
            return false;
        }
        if (!world.isRemote) {
            player.openGui(AdAstraReborn.instance, guiId, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}
