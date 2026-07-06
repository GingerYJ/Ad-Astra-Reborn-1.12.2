package earth.terrarium.adastra.integration.jei.category;

import earth.terrarium.adastra.common.util.PlanetTravelHelper;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RocketDestinationRecipe {

    private final ItemStack rocketStack;
    private final int rocketTier;
    private final int page;
    private final int totalPages;
    private final List<Destination> destinations;

    public RocketDestinationRecipe(ItemStack rocketStack, int rocketTier, int page, int totalPages, List<Destination> destinations) {
        this.rocketStack = rocketStack;
        this.rocketTier = rocketTier;
        this.page = page;
        this.totalPages = totalPages;
        this.destinations = Collections.unmodifiableList(new ArrayList<>(destinations));
    }

    public ItemStack getRocketStack() {
        return rocketStack;
    }

    public int getRocketTier() {
        return rocketTier;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public static List<Destination> collectDestinations() {
        List<Destination> destinations = new ArrayList<>();
        for (PlanetDimensionProperties planet : PlanetTravelHelper.getPlanets()) {
            if (planet == null) {
                continue;
            }
            int requiredTier = PlanetTravelHelper.getRequiredRocketTier(planet);
            destinations.add(new Destination(planet.getName(), planet.getDimensionId(), requiredTier));
        }
        destinations.sort(Comparator.comparingInt(Destination::getRequiredTier)
            .thenComparing(Destination::getName));
        return destinations;
    }

    public static class Destination {

        private final String name;
        private final int dimensionId;
        private final int requiredTier;

        public Destination(String name, int dimensionId, int requiredTier) {
            this.name = name;
            this.dimensionId = dimensionId;
            this.requiredTier = requiredTier;
        }

        public String getName() {
            return name;
        }

        public int getDimensionId() {
            return dimensionId;
        }

        public int getRequiredTier() {
            return requiredTier;
        }

        public boolean canReach(int rocketTier) {
            return rocketTier >= requiredTier;
        }
    }
}
