package earth.terrarium.adastra.common.entities.vehicles;

import earth.terrarium.adastra.common.items.ConfigurableRocketItem;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketRegistry;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketSpec;
import earth.terrarium.adastra.common.util.RocketFuelHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

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
        updateFuelFilter();
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
        int legacyTier = legacyBuiltInTier(getRocketId());
        if (legacyTier > 0) {
            return legacySpec(legacyTier);
        }
        ConfigurableRocketSpec spec = ConfigurableRocketRegistry.get(getRocketId());
        return spec == null ? ConfigurableRocketRegistry.fallback() : spec;
    }

    @Override
    public int getRocketTier() {
        int legacyTier = legacyBuiltInTier(getRocketId());
        return legacyTier > 0 ? legacyTier : getRocketSpec().getTier();
    }

    public int getModelTier() {
        return getRocketSpec().getModelTier();
    }

    @Override
    public ItemStack getDropStack() {
        ItemStack legacyDrop = legacyBuiltInDrop(getRocketId());
        if (!legacyDrop.isEmpty()) {
            return legacyDrop;
        }
        ConfigurableRocketItem item = ConfigurableRocketRegistry.getItem(getRocketId());
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        rocketId = compound.getString("ConfigurableRocketId");
        if (rocketId.isEmpty()) {
            rocketId = compound.getString("RocketId");
        }
        if (!world.isRemote) {
            dataManager.set(DATA_ROCKET_ID, rocketId == null ? "" : rocketId);
        }
        int modelTier = getRocketSpec().getModelTier();
        setSize(1.1f, modelTier >= 4 ? 7.0f : modelTier == 3 ? 5.5f : modelTier == 2 ? 4.8f : 4.6f);
        updateFuelFilter();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setString("ConfigurableRocketId", getRocketId());
    }

    private static int legacyBuiltInTier(String id) {
        if (id == null || !id.startsWith("tier_") || !id.endsWith("_rocket")) {
            return 0;
        }
        try {
            int tier = Integer.parseInt(id.substring("tier_".length(), id.length() - "_rocket".length()));
            return tier >= 8 && tier <= 15 ? tier : 0;
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private void updateFuelFilter() {
        if (fuelTank == null) {
            return;
        }
        ConfigurableRocketSpec spec = getRocketSpec();
        fuelTank.setFluidFilter(stack -> spec.acceptsAnyFuel()
            ? RocketFuelHelper.isRocketFuel(stack)
            : RocketFuelHelper.canFuelRocket(stack, spec.getTier()));
    }

    private static ConfigurableRocketSpec legacySpec(int tier) {
        ResourceLocation texture = ConfigurableRocketSpec.builtInTextureForModelTier(tier);
        return new ConfigurableRocketSpec(
            "tier_" + tier + "_rocket",
            "Tier " + tier + " Rocket",
            tier,
            18000 + (tier - 8) * 1000,
            tier,
            texture);
    }

    private static ItemStack legacyBuiltInDrop(String id) {
        switch (legacyBuiltInTier(id)) {
            case 8:
                return new ItemStack(ModItems.TIER_8_ROCKET);
            case 9:
                return new ItemStack(ModItems.TIER_9_ROCKET);
            case 10:
                return new ItemStack(ModItems.TIER_10_ROCKET);
            case 11:
                return new ItemStack(ModItems.TIER_11_ROCKET);
            case 12:
                return new ItemStack(ModItems.TIER_12_ROCKET);
            case 13:
                return new ItemStack(ModItems.TIER_13_ROCKET);
            case 14:
                return new ItemStack(ModItems.TIER_14_ROCKET);
            case 15:
                return new ItemStack(ModItems.TIER_15_ROCKET);
            default:
                return ItemStack.EMPTY;
        }
    }
}


