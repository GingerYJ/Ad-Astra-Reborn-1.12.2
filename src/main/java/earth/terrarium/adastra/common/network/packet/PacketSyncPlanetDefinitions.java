package earth.terrarium.adastra.common.network.packet;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.planets.PlanetApiImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PacketSyncPlanetDefinitions implements IMessage {

    private static final int MAX_PLANETS = 256;
    private static final int MAX_ADDITIONAL_LAUNCH_DIMS = 128;

    private final Map<Integer, Planet> planets = new LinkedHashMap<>();

    public PacketSyncPlanetDefinitions() {
    }

    public PacketSyncPlanetDefinitions(Map<Integer, Planet> planets) {
        if (planets != null) {
            this.planets.putAll(planets);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        planets.clear();
        int count = Math.min(buf.readInt(), MAX_PLANETS);
        for (int i = 0; i < count; i++) {
            Planet planet = readPlanet(buf);
            planets.put(planet.getDimensionId(), planet);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        int count = Math.min(planets.size(), MAX_PLANETS);
        buf.writeInt(count);
        int written = 0;
        for (Planet planet : planets.values()) {
            if (written++ >= count) {
                break;
            }
            writePlanet(buf, planet);
        }
    }

    private static void writePlanet(ByteBuf buf, Planet planet) {
        buf.writeInt(planet.getDimensionId());
        buf.writeBoolean(planet.hasOxygen());
        buf.writeShort(planet.getTemperature());
        buf.writeFloat(planet.getGravity());
        buf.writeInt(planet.getSolarPower());
        ByteBufUtils.writeUTF8String(buf, planet.getSolarSystem().toString());
        Integer orbitDimensionId = planet.getOrbitDimensionId();
        buf.writeBoolean(orbitDimensionId != null);
        if (orbitDimensionId != null) {
            buf.writeInt(orbitDimensionId);
        }
        buf.writeInt(planet.getTier());
        List<Integer> launchDimensions = planet.getAdditionalLaunchDimensions();
        int count = Math.min(launchDimensions.size(), MAX_ADDITIONAL_LAUNCH_DIMS);
        buf.writeInt(count);
        for (int i = 0; i < count; i++) {
            buf.writeInt(launchDimensions.get(i));
        }
    }

    private static Planet readPlanet(ByteBuf buf) {
        int dimensionId = buf.readInt();
        boolean oxygen = buf.readBoolean();
        short temperature = buf.readShort();
        float gravity = buf.readFloat();
        int solarPower = buf.readInt();
        ResourceLocation solarSystem = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        Integer orbitDimensionId = buf.readBoolean() ? buf.readInt() : null;
        int tier = buf.readInt();
        int additionalCount = Math.min(buf.readInt(), MAX_ADDITIONAL_LAUNCH_DIMS);
        List<Integer> additionalLaunchDimensions = new ArrayList<>(additionalCount);
        for (int i = 0; i < additionalCount; i++) {
            additionalLaunchDimensions.add(buf.readInt());
        }
        return new Planet(
            dimensionId,
            oxygen,
            temperature,
            gravity,
            solarPower,
            solarSystem,
            orbitDimensionId,
            tier,
            additionalLaunchDimensions);
    }

    public static class Handler implements IMessageHandler<PacketSyncPlanetDefinitions, IMessage> {

        @Override
        public IMessage onMessage(PacketSyncPlanetDefinitions message, MessageContext ctx) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> PlanetApiImpl.replacePlanets(message.planets));
            return null;
        }
    }
}
