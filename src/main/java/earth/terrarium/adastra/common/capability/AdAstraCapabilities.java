package earth.terrarium.adastra.common.capability;

import earth.terrarium.adastra.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public class AdAstraCapabilities {

    @CapabilityInject(IAdAstraPlayer.class)
    public static Capability<IAdAstraPlayer> PLAYER_CAPABILITY = null;

    private static final ResourceLocation PLAYER_CAP_ID = new ResourceLocation(Reference.MOD_ID, "player_data");

    public static void register() {
        CapabilityManager.INSTANCE.register(IAdAstraPlayer.class, new Storage(), AdAstraPlayer::new);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(PLAYER_CAP_ID, new AdAstraPlayerProvider());
        }
    }

    public static IAdAstraPlayer getPlayer(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        return player.getCapability(PLAYER_CAPABILITY, null);
    }

    public static boolean hasCapability(EntityPlayer player) {
        return player != null && player.hasCapability(PLAYER_CAPABILITY, null);
    }

    private static class Storage implements Capability.IStorage<IAdAstraPlayer> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IAdAstraPlayer> capability, IAdAstraPlayer instance, EnumFacing side) {
            if (instance instanceof AdAstraPlayer) {
                return ((AdAstraPlayer) instance).writeToNBT(new NBTTagCompound());
            }
            return new NBTTagCompound();
        }

        @Override
        public void readNBT(Capability<IAdAstraPlayer> capability, IAdAstraPlayer instance, EnumFacing side, NBTBase nbt) {
            if (instance instanceof AdAstraPlayer && nbt instanceof NBTTagCompound) {
                ((AdAstraPlayer) instance).readFromNBT((NBTTagCompound) nbt);
            }
        }
    }
}
