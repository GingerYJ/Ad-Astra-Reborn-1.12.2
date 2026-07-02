package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.common.entities.vehicles.AdAstraVehicleEntity;
import earth.terrarium.adastra.common.entities.vehicles.RocketEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Network packet for vehicle control from client to server.
 * Handles rocket launch, rover steering, and lander descent thrust.
 */
public class PacketVehicleControl implements IMessage {

    private static final double MAX_CONTROL_DISTANCE_SQ = 256.0D; // 16 blocks

    private int vehicleId;
    private ControlType controlType;
    private float steerValue; // -1.0 to 1.0 for steering/rotation
    private float accelerateValue; // 0.0 to 1.0 for thrust/acceleration
    private boolean boostPressed; // For boost or special actions

    public PacketVehicleControl() {
    }

    /**
     * Creates a vehicle control packet.
     *
     * @param vehicleId Entity ID of the vehicle
     * @param controlType Type of control input
     * @param steerValue Steering value (-1.0 to 1.0)
     * @param accelerateValue Acceleration value (0.0 to 1.0)
     * @param boostPressed Boost button state
     */
    public PacketVehicleControl(int vehicleId, ControlType controlType,
                               float steerValue, float accelerateValue,
                               boolean boostPressed) {
        this.vehicleId = vehicleId;
        this.controlType = controlType;
        this.steerValue = steerValue;
        this.accelerateValue = accelerateValue;
        this.boostPressed = boostPressed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        vehicleId = buf.readInt();
        controlType = ControlType.values()[buf.readByte()];
        steerValue = buf.readFloat();
        accelerateValue = buf.readFloat();
        boostPressed = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(vehicleId);
        buf.writeByte(controlType.ordinal());
        buf.writeFloat(steerValue);
        buf.writeFloat(accelerateValue);
        buf.writeBoolean(boostPressed);
    }

    public static class Handler implements IMessageHandler<PacketVehicleControl, IMessage> {

        @Override
        public IMessage onMessage(PacketVehicleControl message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private void handle(PacketVehicleControl message, EntityPlayerMP player) {
            if (player.world == null) {
                return;
            }

            // Get the vehicle entity
            Entity entity = player.world.getEntityByID(message.vehicleId);
            if (!(entity instanceof AdAstraVehicleEntity)) {
                return;
            }

            AdAstraVehicleEntity vehicle = (AdAstraVehicleEntity) entity;

            // Validate player is controlling or near the vehicle
            if (!vehicle.isPassenger(player)) {
                // Allow control if player is close (e.g., remote control, future feature)
                if (player.getDistanceSq(vehicle) > MAX_CONTROL_DISTANCE_SQ) {
                    return;
                }
            }

            // Clamp input values to prevent cheating
            float steer = clamp(message.steerValue, -1.0f, 1.0f);
            float accelerate = clamp(message.accelerateValue, 0.0f, 1.0f);

            // Apply control based on type
            switch (message.controlType) {
                case ROCKET_LAUNCH:
                    if (vehicle instanceof RocketEntity) {
                        ((RocketEntity) vehicle).initiateLaunch();
                    } else {
                        vehicle.startLaunch();
                    }
                    break;

                case ROVER_MOVE:
                    vehicle.setRoverControl(steer, accelerate, message.boostPressed);
                    break;

                case LANDER_THRUST:
                    vehicle.setLanderThrust(accelerate, steer);
                    break;

                case STOP:
                    vehicle.stopVehicle();
                    break;
            }
        }

        private float clamp(float value, float min, float max) {
            return Math.max(min, Math.min(max, value));
        }
    }

    public enum ControlType {
        ROCKET_LAUNCH,
        ROVER_MOVE,
        LANDER_THRUST,
        STOP
    }
}
