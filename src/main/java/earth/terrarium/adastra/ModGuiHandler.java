package earth.terrarium.adastra;

import earth.terrarium.adastra.client.gui.AdAstraMachineGui;
import earth.terrarium.adastra.client.gui.LanderGui;
import earth.terrarium.adastra.client.gui.RocketGui;
import earth.terrarium.adastra.client.gui.RoverGui;
import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.container.LanderContainer;
import earth.terrarium.adastra.common.container.RocketContainer;
import earth.terrarium.adastra.common.container.RoverContainer;
import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.RoverEntity;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {

    // Vehicle GUI IDs (separate from machine IDs)
    public static final int ROCKET_GUI = 100;
    public static final int ROVER_GUI = 101;
    public static final int LANDER_GUI = 102;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        // Handle vehicle GUIs (x parameter is entity ID for vehicles)
        if (id == ROCKET_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof RocketEntity) {
                return new RocketContainer(player.inventory, (RocketEntity) entity);
            }
        } else if (id == ROVER_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof RoverEntity) {
                return new RoverContainer(player.inventory, (RoverEntity) entity);
            }
        } else if (id == LANDER_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof LanderEntity) {
                return new LanderContainer(player.inventory, (LanderEntity) entity);
            }
        }

        // Handle machine GUIs (original logic)
        AdAstraMachineTileEntity machine = getMachine(world, x, y, z);
        if (machine == null) {
            return null;
        }
        AdAstraMachineContainer.Layout layout = AdAstraMachineContainer.layoutFor(id, machine);
        return layout == null ? null : new AdAstraMachineContainer(player.inventory, machine, layout);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        // Handle vehicle GUIs (x parameter is entity ID for vehicles)
        if (id == ROCKET_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof RocketEntity) {
                RocketContainer container = new RocketContainer(player.inventory, (RocketEntity) entity);
                return new RocketGui(player.inventory, container);
            }
        } else if (id == ROVER_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof RoverEntity) {
                RoverContainer container = new RoverContainer(player.inventory, (RoverEntity) entity);
                return new RoverGui(player.inventory, container);
            }
        } else if (id == LANDER_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof LanderEntity) {
                LanderContainer container = new LanderContainer(player.inventory, (LanderEntity) entity);
                return new LanderGui(player.inventory, container);
            }
        }

        // Handle machine GUIs (original logic)
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
