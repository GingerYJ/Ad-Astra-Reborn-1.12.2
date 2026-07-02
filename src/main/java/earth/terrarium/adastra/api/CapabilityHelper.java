package earth.terrarium.adastra.api;

import earth.terrarium.adastra.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Helper class for integrating with Ad Astra capabilities.
 * <p>
 * This class provides convenience methods for accessing Ad Astra's capability system,
 * which stores player-specific data such as oxygen levels, space suit status, and more.
 * <p>
 * Third-party mods can use this to check if a player has Ad Astra capabilities
 * or to create their own capability integrations following the same patterns.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Check if player has Ad Astra capability
 * if (CapabilityHelper.hasAdAstraCapability(player)) {
 *     // Access Ad Astra player data
 * }
 * }</pre>
 *
 * @since 1.12.2
 */
public final class CapabilityHelper {

    private static final ResourceLocation PLAYER_CAP_ID = new ResourceLocation(Reference.MOD_ID, "player_data");

    private CapabilityHelper() {
    }

    /**
     * Gets the Ad Astra player capability ID.
     * This is the resource location used to identify Ad Astra's player capability.
     *
     * @return The player capability resource location
     */
    public static ResourceLocation getPlayerCapabilityId() {
        return PLAYER_CAP_ID;
    }

    /**
     * Creates a standard capability provider that implements both ICapabilitySerializable
     * and delegates to a capability instance.
     * <p>
     * This is a helper method for mods creating their own capabilities following
     * Ad Astra's patterns.
     *
     * @param capability The capability to provide
     * @param instance The capability instance
     * @param storage The capability storage
     * @param <T> The capability type
     * @return A new capability provider
     */
    public static <T> ICapabilitySerializable<NBTTagCompound> createProvider(
        Capability<T> capability,
        T instance,
        Capability.IStorage<T> storage
    ) {
        return new ICapabilitySerializable<NBTTagCompound>() {
            @Override
            public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing side) {
                return cap == capability;
            }

            @Nullable
            @Override
            public <C> C getCapability(@Nonnull Capability<C> cap, @Nullable EnumFacing side) {
                if (cap == capability) {
                    return capability.cast(instance);
                }
                return null;
            }

            @Override
            public NBTTagCompound serializeNBT() {
                NBTBase nbt = storage.writeNBT(capability, instance, null);
                if (nbt instanceof NBTTagCompound) {
                    return (NBTTagCompound) nbt;
                }
                NBTTagCompound tag = new NBTTagCompound();
                tag.setTag("data", nbt);
                return tag;
            }

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                if (nbt.hasKey("data")) {
                    storage.readNBT(capability, instance, null, nbt.getTag("data"));
                } else {
                    storage.readNBT(capability, instance, null, nbt);
                }
            }
        };
    }

    /**
     * Safely gets a capability from an entity, returning null if not present.
     * <p>
     * This is a convenience method that checks hasCapability before calling getCapability.
     *
     * @param player The player to get capability from
     * @param capability The capability to get
     * @param side The side (typically null for player capabilities)
     * @param <T> The capability type
     * @return The capability instance, or null if not present
     */
    @Nullable
    public static <T> T getCapability(EntityPlayer player, Capability<T> capability, @Nullable EnumFacing side) {
        if (player == null || capability == null) {
            return null;
        }
        if (player.hasCapability(capability, side)) {
            return player.getCapability(capability, side);
        }
        return null;
    }

    /**
     * Creates a simple capability storage that uses NBTTagCompound for serialization.
     * <p>
     * This assumes the capability instance has writeToNBT and readFromNBT methods.
     * <p>
     * Example usage:
     * <pre>{@code
     * public static class Storage implements Capability.IStorage<IMyCapability> {
     *     @Override
     *     public NBTBase writeNBT(Capability<IMyCapability> capability, IMyCapability instance, EnumFacing side) {
     *         if (instance instanceof MyCapabilityImpl) {
     *             return ((MyCapabilityImpl) instance).writeToNBT(new NBTTagCompound());
     *         }
     *         return new NBTTagCompound();
     *     }
     *
     *     @Override
     *     public void readNBT(Capability<IMyCapability> capability, IMyCapability instance, EnumFacing side, NBTBase nbt) {
     *         if (instance instanceof MyCapabilityImpl && nbt instanceof NBTTagCompound) {
     *             ((MyCapabilityImpl) instance).readFromNBT((NBTTagCompound) nbt);
     *         }
     *     }
     * }
     * }</pre>
     *
     * @param <T> The capability type
     * @return A documentation reference for capability storage implementation
     */
    public static <T> String getStorageImplementationGuide() {
        return "Implement Capability.IStorage<T> with writeNBT and readNBT methods. " +
            "writeNBT should return an NBTBase (typically NBTTagCompound), " +
            "and readNBT should populate the instance from the NBTBase.";
    }
}
