package earth.terrarium.adastra.common.entities.vehicles;

import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

public class AdAstraVehiclePart extends MultiPartEntityPart {

    public interface InteractionHandler {
        boolean interact(EntityPlayer player, EnumHand hand);
    }

    private final VehicleBase vehicle;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final InteractionHandler interactionHandler;

    public AdAstraVehiclePart(VehicleBase vehicle, String name, float width, float height,
                              double offsetX, double offsetY, double offsetZ,
                              InteractionHandler interactionHandler) {
        super(vehicle, name, width, height);
        this.vehicle = vehicle;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.interactionHandler = interactionHandler;
    }

    public void updatePartPosition() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;

        double yaw = Math.toRadians(vehicle.rotationYaw);
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double rotatedX = offsetX * cos - offsetZ * sin;
        double rotatedZ = offsetX * sin + offsetZ * cos;
        setPosition(vehicle.posX + rotatedX, vehicle.posY + offsetY, vehicle.posZ + rotatedZ);
        rotationYaw = vehicle.rotationYaw;
        rotationPitch = vehicle.rotationPitch;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (interactionHandler != null && interactionHandler.interact(player, hand)) {
            return true;
        }
        return vehicle.processInitialInteract(player, hand);
    }

    @Override
    public boolean canBeCollidedWith() {
        return !vehicle.isDead;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return vehicle.attackEntityFrom(source, amount);
    }

    @Override
    public boolean isEntityEqual(net.minecraft.entity.Entity entity) {
        return this == entity || vehicle == entity;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return vehicle.getDropStack();
    }

    @Override
    public boolean writeToNBTOptional(net.minecraft.nbt.NBTTagCompound compound) {
        return false;
    }
}
