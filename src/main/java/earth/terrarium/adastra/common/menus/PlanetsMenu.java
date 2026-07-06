package earth.terrarium.adastra.common.menus;

import earth.terrarium.adastra.common.capability.AdAstraCapabilities;
import earth.terrarium.adastra.common.capability.IAdAstraPlayer;
import earth.terrarium.adastra.common.capability.SpaceStation;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketConstructSpaceStation;
import earth.terrarium.adastra.common.network.packet.PacketLandPlanet;
import earth.terrarium.adastra.common.network.packet.PacketLandSpaceStation;
import earth.terrarium.adastra.common.recipe.RecipeRegistry;
import earth.terrarium.adastra.common.recipe.SpaceStationRecipe;
import earth.terrarium.adastra.common.util.PlanetTravelHelper;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlanetsMenu extends Container {

    private final InventoryPlayer inventory;
    private final EntityPlayer player;
    private final int rocketTier;
    private final int rocketEntityId;
    private final Set<ResourceLocation> disabledPlanets;

    public PlanetsMenu(InventoryPlayer inventory, int rocketTier, int rocketEntityId, Set<ResourceLocation> disabledPlanets) {
        this.inventory = inventory;
        this.player = inventory.player;
        this.rocketTier = rocketTier;
        this.rocketEntityId = rocketEntityId;
        this.disabledPlanets = Collections.unmodifiableSet(new HashSet<>(disabledPlanets));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn == player;
    }

    public int getRocketTier() {
        return rocketTier;
    }

    public int getRocketEntityId() {
        return rocketEntityId;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public Set<ResourceLocation> getDisabledPlanets() {
        return disabledPlanets;
    }

    public List<PlanetDimensionProperties> getAvailablePlanets() {
        List<PlanetDimensionProperties> planets = new ArrayList<>();
        PlanetDimensionProperties currentOrbitPlanet = getCurrentOrbitPlanet();
        if (currentOrbitPlanet != null && isPlanetEnabled(currentOrbitPlanet)) {
            planets.add(currentOrbitPlanet);
        }
        if (player != null && player.dimension != 0 && isPlanetEnabled(PlanetTravelHelper.EARTH_PROPERTIES)) {
            addIfMissing(planets, PlanetTravelHelper.EARTH_PROPERTIES);
        }
        for (PlanetDimensionProperties planet : PlanetTravelHelper.getPlanets()) {
            if (player != null && player.dimension == planet.getDimensionId()) {
                continue;
            }
            if (isPlanetEnabled(planet)) {
                addIfMissing(planets, planet);
            }
        }
        planets.sort(Comparator.comparingInt((PlanetDimensionProperties planet) -> isSamePlanet(planet, currentOrbitPlanet) ? 0 : 1)
            .thenComparingInt(PlanetTravelHelper::getRequiredRocketTier)
            .thenComparing(PlanetDimensionProperties::getName));
        return planets;
    }

    public PlanetDimensionProperties getCurrentOrbitPlanet() {
        return player == null ? null : PlanetTravelHelper.getPlanetByOrbitDimensionId(player.dimension);
    }

    public boolean canReach(PlanetDimensionProperties planet) {
        return PlanetTravelHelper.canRocketTierReach(rocketTier, planet);
    }

    public boolean canConstructSpaceStation(PlanetDimensionProperties planet) {
        if (planet == null || !canReach(planet) || hasSpaceStation(planet)) {
            return false;
        }
        SpaceStationRecipe recipe = getSpaceStationRecipe(planet);
        return recipe != null && recipe.canCraft(player);
    }

    public boolean hasSpaceStation(PlanetDimensionProperties planet) {
        return !getSpaceStations(planet).isEmpty();
    }

    public List<SpaceStation> getSpaceStations(PlanetDimensionProperties planet) {
        List<SpaceStation> stations = new ArrayList<>();
        if (planet == null || player == null) {
            return stations;
        }
        IAdAstraPlayer capability = AdAstraCapabilities.getPlayer(player);
        if (capability == null) {
            return stations;
        }
        int orbitDimensionId = PlanetTravelHelper.getOrbitDimensionId(planet.getDimensionId());
        for (SpaceStation station : capability.getSpaceStations()) {
            if (station.getDimension() == orbitDimensionId) {
                stations.add(station);
            }
        }
        stations.sort(Comparator.comparing(SpaceStation::getName));
        return stations;
    }

    public SpaceStationRecipe getSpaceStationRecipe(PlanetDimensionProperties planet) {
        if (planet == null) {
            return null;
        }
        int orbitDimensionId = PlanetTravelHelper.getOrbitDimensionId(planet.getDimensionId());
        ResourceLocation orbitLocation = PlanetTravelHelper.getOrbitDimensionLocation(orbitDimensionId);
        return orbitLocation == null ? null : RecipeRegistry.findSpaceStationRecipe(orbitLocation);
    }

    public int getOwnedIngredientCount(SpaceStationRecipe.IngredientRequirement requirement) {
        return requirement == null ? 0 : requirement.countMatching(inventory);
    }

    public boolean isPlayerAtSpaceStation(SpaceStation station) {
        return player != null
            && station != null
            && player.dimension == station.getDimension()
            && Math.abs(player.posX - (station.getPosition().getX() + 0.5D)) <= 40.0D
            && Math.abs(player.posZ - (station.getPosition().getZ() + 0.5D)) <= 40.0D;
    }

    public void landOnPlanet(PlanetDimensionProperties planet) {
        if (planet != null) {
            NetworkHandler.CHANNEL.sendToServer(new PacketLandPlanet(planet.getDimensionId(), rocketTier, rocketEntityId));
        }
    }

    public void constructSpaceStation(PlanetDimensionProperties planet) {
        if (planet != null) {
            NetworkHandler.CHANNEL.sendToServer(new PacketConstructSpaceStation(planet.getDimensionId()));
        }
    }

    public void landOnSpaceStation(PlanetDimensionProperties planet, BlockPos stationPos) {
        if (planet != null && stationPos != null) {
            NetworkHandler.CHANNEL.sendToServer(new PacketLandSpaceStation(PlanetTravelHelper.getOrbitDimensionId(planet.getDimensionId()), stationPos));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }

    private boolean isPlanetEnabled(PlanetDimensionProperties planet) {
        return planet != null && !disabledPlanets.contains(new ResourceLocation(planet.getName()));
    }

    private static void addIfMissing(List<PlanetDimensionProperties> planets, PlanetDimensionProperties planet) {
        for (PlanetDimensionProperties existing : planets) {
            if (isSamePlanet(existing, planet)) {
                return;
            }
        }
        planets.add(planet);
    }

    private static boolean isSamePlanet(PlanetDimensionProperties first, PlanetDimensionProperties second) {
        return first != null && second != null && first.getDimensionId() == second.getDimensionId();
    }
}
