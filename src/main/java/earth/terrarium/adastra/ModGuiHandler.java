package earth.terrarium.adastra;

import earth.terrarium.adastra.client.gui.AdAstraMachineGui;
import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        AdAstraMachineTileEntity machine = getMachine(world, x, y, z);
        if (machine == null) {
            return null;
        }
        AdAstraMachineContainer.Layout layout = AdAstraMachineContainer.layoutFor(id, machine);
        return layout == null ? null : new AdAstraMachineContainer(player.inventory, machine, layout);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        AdAstraMachineTileEntity machine = getMachine(world, x, y, z);
        if (machine == null) {
            return null;
        }
        AdAstraMachineContainer.Layout layout = AdAstraMachineContainer.layoutFor(id, machine);
        return layout == null ? null : new AdAstraMachineGui(player.inventory, new AdAstraMachineContainer(player.inventory, machine, layout));
    }

    private AdAstraMachineTileEntity getMachine(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return tile instanceof AdAstraMachineTileEntity ? (AdAstraMachineTileEntity) tile : null;
    }
}
