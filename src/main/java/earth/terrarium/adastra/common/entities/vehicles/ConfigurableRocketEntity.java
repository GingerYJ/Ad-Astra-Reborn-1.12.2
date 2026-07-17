package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.items.ConfigurableRocketItem;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketRegistry;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketSpec;
import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class ConfigurableRocketEntity extends RocketEntity {

    private static final DataParameter<String> DATA_ROCKET_ID = EntityDataManager.createKey(ConfigurableRocketEntity.class, DataSerializers.STRING);

    private String rocketId;

    public ConfigurableRocketEntity(World world) {
        this(world, ConfigurableRocketRegistry.fallback());
    }

    public ConfigurableRocketEntity(World world, ConfigurableRocketSpec spec) {
        super(world, spec.getTier(), spec.getFuelCapacity(), stack -> spec.acceptsAnyFuel()
            ? RocketFuelHelper.isRocketFuel(stack)
            : RocketFuelHelper.canFuelRocket(stack, spec.getTier()));
        this.rocketId = spec.getId();
        try {
            dataManager.set(DATA_ROCKET_ID, this.rocketId);
        } catch (RuntimeException ignored) {
            // entityInit may not have registered the data parameter yet on some construction paths.
        }
        setSize(1.1f, spec.getModelTier() >= 4 ? 7.0f : spec.getModelTier() == 3 ? 5.5f : spec.getModelTier() == 2 ? 4.8f : 4.6f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(DATA_ROCKET_ID, rocketId == null ? "" : rocketId);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (DATA_ROCKET_ID.equals(key)) {
            rocketId = dataManager.get(DATA_ROCKET_ID);
        }
    }

    public String getRocketId() {
        if (world.isRemote) {
            String synced = dataManager.get(DATA_ROCKET_ID);
            if (synced != null && !synced.isEmpty()) {
                return synced;
            }
        }
        return rocketId == null ? "" : rocketId;
    }

    public ConfigurableRocketSpec getRocketSpec() {
        ConfigurableRocketSpec spec = ConfigurableRocketRegistry.get(getRocketId());
        return spec == null ? ConfigurableRocketRegistry.fallback() : spec;
    }

    @Override
    public int getRocketTier() {
        return getRocketSpec().getTier();
    }

    public int getModelTier() {
        return getRocketSpec().getModelTier();
    }

    @Override
    public ItemStack getDropStack() {
        ConfigurableRocketItem item = ConfigurableRocketRegistry.getItem(getRocketId());
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        rocketId = compound.getString("ConfigurableRocketId");
        if (!world.isRemote) {
            dataManager.set(DATA_ROCKET_ID, rocketId == null ? "" : rocketId);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setString("ConfigurableRocketId", getRocketId());
    }
}


