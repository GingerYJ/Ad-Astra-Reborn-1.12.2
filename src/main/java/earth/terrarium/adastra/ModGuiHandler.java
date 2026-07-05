package earth.terrarium.adastra;

import earth.terrarium.adastra.client.gui.AdAstraMachineGui;
import earth.terrarium.adastra.client.gui.LanderGui;
import earth.terrarium.adastra.client.gui.RocketGui;
import earth.terrarium.adastra.client.gui.RoverGui;
import earth.terrarium.adastra.common.container.AdAstraMachineContainer;
import earth.terrarium.adastra.common.entities.vehicles.LanderEntity;
import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import earth.terrarium.adastra.common.entities.vehicles.RoverEntity;
import earth.terrarium.adastra.common.menus.base.MachineMenu;
import earth.terrarium.adastra.common.menus.machines.CoalGeneratorMenu;
import earth.terrarium.adastra.common.menus.machines.CompressorMenu;
import earth.terrarium.adastra.common.menus.machines.CryoFreezerMenu;
import earth.terrarium.adastra.common.menus.machines.EtrionicBlastFurnaceMenu;
import earth.terrarium.adastra.common.menus.machines.FuelRefineryMenu;
import earth.terrarium.adastra.common.menus.machines.GravityNormalizerMenu;
import earth.terrarium.adastra.common.menus.machines.NasaWorkbenchMenu;
import earth.terrarium.adastra.common.menus.machines.OxygenDistributorMenu;
import earth.terrarium.adastra.common.menus.machines.OxygenLoaderMenu;
import earth.terrarium.adastra.common.menus.machines.SolarPanelMenu;
import earth.terrarium.adastra.common.menus.machines.WaterPumpMenu;
import earth.terrarium.adastra.common.menus.vehicles.LanderMenu;
import earth.terrarium.adastra.common.menus.vehicles.RocketMenu;
import earth.terrarium.adastra.common.menus.vehicles.RoverMenu;
import earth.terrarium.adastra.common.registry.ModGuiIds;
import earth.terrarium.adastra.common.tile.AdAstraMachineTileEntity;
import earth.terrarium.adastra.common.tile.CoalGeneratorTileEntity;
import earth.terrarium.adastra.common.tile.CompressorTileEntity;
import earth.terrarium.adastra.common.tile.CryoFreezerTileEntity;
import earth.terrarium.adastra.common.tile.EtrionicBlastFurnaceTileEntity;
import earth.terrarium.adastra.common.tile.FuelRefineryTileEntity;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import earth.terrarium.adastra.common.tile.NasaWorkbenchTileEntity;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import earth.terrarium.adastra.common.tile.OxygenLoaderTileEntity;
import earth.terrarium.adastra.common.tile.SolarPanelTileEntity;
import earth.terrarium.adastra.common.tile.WaterPumpTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
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
                return new RocketMenu(player.inventory, (RocketEntity) entity);
            }
        } else if (id == ROVER_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof RoverEntity) {
                return new RoverMenu(player.inventory, (RoverEntity) entity);
            }
        } else if (id == LANDER_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof LanderEntity) {
                return new LanderMenu(player.inventory, (LanderEntity) entity);
            }
        }

        // Handle machine GUIs (original logic)
        AdAstraMachineTileEntity machine = getMachine(world, x, y, z);
        if (machine == null) {
            return null;
        }
        return createMachineContainer(id, player.inventory, machine);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        // Handle vehicle GUIs (x parameter is entity ID for vehicles)
        if (id == ROCKET_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof RocketEntity) {
                RocketMenu container = new RocketMenu(player.inventory, (RocketEntity) entity);
                return new RocketGui(player.inventory, container);
            }
        } else if (id == ROVER_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof RoverEntity) {
                RoverMenu container = new RoverMenu(player.inventory, (RoverEntity) entity);
                return new RoverGui(player.inventory, container);
            }
        } else if (id == LANDER_GUI) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof LanderEntity) {
                LanderMenu container = new LanderMenu(player.inventory, (LanderEntity) entity);
                return new LanderGui(player.inventory, container);
            }
        }

        // Handle machine GUIs (original logic)
        AdAstraMachineTileEntity machine = getMachine(world, x, y, z);
        if (machine == null) {
            return null;
        }
        MachineMenu<?> container = createMachineContainer(id, player.inventory, machine);
        if (container == null) {
            return null;
        }
        return AdAstraMachineGui.create(player.inventory, container);
    }

    private AdAstraMachineTileEntity getMachine(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        return tile instanceof AdAstraMachineTileEntity ? (AdAstraMachineTileEntity) tile : null;
    }

    private MachineMenu<?> createMachineContainer(int id, InventoryPlayer inventory, AdAstraMachineTileEntity machine) {
        switch (id) {
            case ModGuiIds.COAL_GENERATOR:
                return machine instanceof CoalGeneratorTileEntity ? new CoalGeneratorMenu(inventory, (CoalGeneratorTileEntity) machine) : null;
            case ModGuiIds.COMPRESSOR:
                return machine instanceof CompressorTileEntity ? new CompressorMenu(inventory, (CompressorTileEntity) machine) : null;
            case ModGuiIds.ETRIONIC_BLAST_FURNACE:
                return machine instanceof EtrionicBlastFurnaceTileEntity ? new EtrionicBlastFurnaceMenu(inventory, (EtrionicBlastFurnaceTileEntity) machine) : null;
            case ModGuiIds.FUEL_REFINERY:
                return machine instanceof FuelRefineryTileEntity ? new FuelRefineryMenu(inventory, (FuelRefineryTileEntity) machine) : null;
            case ModGuiIds.OXYGEN_LOADER:
                return machine instanceof OxygenLoaderTileEntity ? new OxygenLoaderMenu(inventory, (OxygenLoaderTileEntity) machine) : null;
            case ModGuiIds.SOLAR_PANEL:
                return machine instanceof SolarPanelTileEntity ? new SolarPanelMenu(inventory, (SolarPanelTileEntity) machine) : null;
            case ModGuiIds.WATER_PUMP:
                return machine instanceof WaterPumpTileEntity ? new WaterPumpMenu(inventory, (WaterPumpTileEntity) machine) : null;
            case ModGuiIds.CRYO_FREEZER:
                return machine instanceof CryoFreezerTileEntity ? new CryoFreezerMenu(inventory, (CryoFreezerTileEntity) machine) : null;
            case ModGuiIds.NASA_WORKBENCH:
                return machine instanceof NasaWorkbenchTileEntity ? new NasaWorkbenchMenu(inventory, (NasaWorkbenchTileEntity) machine) : null;
            case ModGuiIds.GRAVITY_NORMALIZER:
                return machine instanceof GravityNormalizerTileEntity ? new GravityNormalizerMenu(inventory, (GravityNormalizerTileEntity) machine) : null;
            case ModGuiIds.OXYGEN_DISTRIBUTOR:
                return machine instanceof OxygenDistributorTileEntity ? new OxygenDistributorMenu(inventory, (OxygenDistributorTileEntity) machine) : null;
            default:
                AdAstraMachineContainer.Layout layout = AdAstraMachineContainer.layoutFor(id, machine);
                return layout == null ? null : new AdAstraMachineContainer(inventory, machine, layout);
        }
    }
}
