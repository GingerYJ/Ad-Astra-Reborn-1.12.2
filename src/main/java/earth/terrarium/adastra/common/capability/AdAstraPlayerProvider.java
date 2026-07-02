package earth.terrarium.adastra.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AdAstraPlayerProvider implements ICapabilitySerializable<NBTTagCompound> {

    private final AdAstraPlayer instance;

    public AdAstraPlayerProvider() {
        this.instance = new AdAstraPlayer();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == AdAstraCapabilities.PLAYER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == AdAstraCapabilities.PLAYER_CAPABILITY) {
            return AdAstraCapabilities.PLAYER_CAPABILITY.cast(instance);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return instance.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        instance.readFromNBT(nbt);
    }
}
