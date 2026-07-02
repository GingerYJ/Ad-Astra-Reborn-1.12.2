package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.items.AdAstraArmorItem;
import earth.terrarium.adastra.common.registry.ModDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Collection;

/**
 * Core temperature system managing temperature values at block positions.
 * Provides temperature calculation, protection values, and damage application.
 *
 * Ported from Ad Astra 1.20.x TemperatureApiImpl
 */
public class TemperatureSystem {

    // Temperature constants (Celsius) - aligned with 1.20.x PlanetConstants
    public static final short MIN_LIVEABLE_TEMPERATURE = -50;
    public static final short MAX_LIVEABLE_TEMPERATURE = 70;
    public static final short FREEZE_TEMPERATURE = -20;
    public static final short EARTH_TEMPERATURE = 15;

    // Damage constants - aligned with 1.20.x TemperatureApiImpl
    private static final float FREEZE_DAMAGE = 3.0f;
    private static final float BURN_DAMAGE = 6.0f;
    private static final int BURN_FIRE_SECONDS = 10;

    // Temperature modifiers
    private static final float ALTITUDE_TEMP_CHANGE_PER_BLOCK = 0.5f; // Temperature change per block above/below sea level
    private static final int SEA_LEVEL = 64;
    private static final float DAY_NIGHT_TEMP_VARIATION = 10.0f; // Max temperature variation between day/night

    // Custom damage sources
    private static final DamageSource FREEZE_DAMAGE_SOURCE = new DamageSource("freeze").setDamageBypassesArmor();
    private static final DamageSource HEAT_DAMAGE_SOURCE = new DamageSource("heat").setDamageBypassesArmor();

    /**
     * Gets the base temperature of a dimension from its WorldProvider.
     * Uses ModDimensions for planet dimension IDs to maintain consistency.
     */
    public static short getTemperatureInDimension(World world) {
        int dimension = world.provider.getDimension();

        // Vanilla dimensions
        if (dimension == -1) { // Nether
            return 800;
        } else if (dimension == 1) { // End
            return -100;
        } else if (dimension == 0) { // Overworld
            return EARTH_TEMPERATURE;
        }

        // Ad Astra planet dimensions - using ModDimensions IDs
        if (dimension == ModDimensions.MOON_ID) {
            return ModDimensions.MOON_PROPERTIES.getTemperature();
        } else if (dimension == ModDimensions.MARS_ID) {
            return ModDimensions.MARS_PROPERTIES.getTemperature();
        } else if (dimension == ModDimensions.VENUS_ID) {
            return ModDimensions.VENUS_PROPERTIES.getTemperature();
        } else if (dimension == ModDimensions.MERCURY_ID) {
            return ModDimensions.MERCURY_PROPERTIES.getTemperature();
        } else if (dimension == ModDimensions.GLACIO_ID) {
            return ModDimensions.GLACIO_PROPERTIES.getTemperature();
        }

        // Default to Earth temperature for unknown dimensions
        return EARTH_TEMPERATURE;
    }

    /**
     * Gets the temperature at a specific position, accounting for altitude and time of day.
     * @param world The world
     * @param pos The block position
     * @return Temperature in Celsius
     */
    public static short getTemperatureAtPos(World world, BlockPos pos) {
        if (world.isRemote) {
            return getEnvironmentTemperature(world, pos);
        }

        if (!(world instanceof WorldServer)) {
            return getEnvironmentTemperature(world, pos);
        }

        // Check for custom temperature data first
        PlanetDataStorage storage = PlanetDataStorage.get(world);
        PlanetData data = storage.getData(pos);

        if (data != null) {
            return data.temperature();
        }

        return getEnvironmentTemperature(world, pos);
    }

    /**
     * Calculates the environmental temperature at a position.
     * Accounts for base dimension temperature, altitude, and time of day.
     */
    public static short getEnvironmentTemperature(World world, BlockPos pos) {
        // Get base dimension temperature
        float baseTemp = getTemperatureInDimension(world);

        // Apply altitude adjustment (higher = colder, lower = hotter)
        int altitudeDiff = pos.getY() - SEA_LEVEL;
        float altitudeModifier = -altitudeDiff * ALTITUDE_TEMP_CHANGE_PER_BLOCK;

        // Apply time-of-day adjustment (only for overworld-like dimensions with day/night cycle)
        float timeModifier = 0.0f;
        if (world.provider.hasSkyLight()) {
            float celestialAngle = world.getCelestialAngle(1.0f);
            // Peak at noon (0.25), minimum at midnight (0.75)
            // sin gives smooth transition, scaled to ±DAY_NIGHT_TEMP_VARIATION
            timeModifier = DAY_NIGHT_TEMP_VARIATION * MathHelper.sin(celestialAngle * (float) Math.PI * 2.0f);
        }

        // Combine all factors
        float finalTemp = baseTemp + altitudeModifier + timeModifier;

        return (short) MathHelper.clamp(Math.round(finalTemp), Short.MIN_VALUE, Short.MAX_VALUE);
    }

    /**
     * Gets the temperature protection value provided by an entity's armor.
     * Aligned with 1.20.x logic: full space suit provides complete protection.
     *
     * @param entity The entity to check
     * @return Temperature protection in degrees (positive = protects from heat AND cold)
     */
    public static float getTemperatureProtection(EntityLivingBase entity) {
        // Count space suit pieces
        int spaceSuitPieces = 0;
        boolean hasJetSuitPiece = false;
        boolean hasNetheritePiece = false;

        for (ItemStack armorPiece : entity.getArmorInventoryList()) {
            if (!armorPiece.isEmpty() && armorPiece.getItem() instanceof AdAstraArmorItem) {
                AdAstraArmorItem armorItem = (AdAstraArmorItem) armorPiece.getItem();
                spaceSuitPieces++;

                // Detect suit type by checking if it's a jet suit or has oxygen capacity
                if (armorItem.isJetSuitChestPiece()) {
                    hasJetSuitPiece = true;
                } else if (armorItem.isOxygenChestPiece()) {
                    // Check oxygen capacity via NBT or assume netherite if capacity > 1000
                    // For simplicity, we'll treat all oxygen suits as providing protection
                    hasNetheritePiece = true;
                }
            }
        }

        // Full set provides complete protection (4 pieces)
        // In 1.20.x, any full space-resistant armor set provides full protection
        if (spaceSuitPieces >= 4) {
            if (hasJetSuitPiece) {
                // Jet suit provides maximum protection (can handle all extremes)
                return Float.MAX_VALUE;
            } else if (hasNetheritePiece) {
                // Netherite provides excellent protection
                return Float.MAX_VALUE;
            } else {
                // Basic space suit provides standard protection
                return Float.MAX_VALUE;
            }
        }

        // Partial sets provide no protection (aligned with 1.20.x behavior)
        // In 1.20.x, only full sets provide protection via hasFullSet check
        return 0.0f;
    }

    /**
     * Checks if the temperature at a position is within liveable range.
     */
    public static boolean isLiveable(World world, BlockPos pos) {
        short temperature = getTemperatureAtPos(world, pos);
        return temperature >= MIN_LIVEABLE_TEMPERATURE && temperature <= MAX_LIVEABLE_TEMPERATURE;
    }

    /**
     * Checks if the temperature at a position is too hot to be liveable.
     */
    public static boolean isHot(World world, BlockPos pos) {
        return getTemperatureAtPos(world, pos) > MAX_LIVEABLE_TEMPERATURE;
    }

    /**
     * Checks if the temperature at a position is too cold to be liveable.
     */
    public static boolean isCold(World world, BlockPos pos) {
        return getTemperatureAtPos(world, pos) < MIN_LIVEABLE_TEMPERATURE;
    }

    /**
     * Applies temperature damage to an entity based on current environmental temperature.
     * Should be called periodically (e.g., every 40 ticks) for living entities.
     *
     * Aligned with 1.20.x TemperatureApiImpl.entityTick behavior.
     */
    public static void applyTemperatureDamage(EntityLivingBase entity) {
        // Check if temperature damage is disabled
        if (AdAstraConfig.disableTemperature) {
            return;
        }

        World world = entity.world;
        if (world.isRemote) {
            return;
        }

        // Creative/spectator players are immune
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.isCreative() || player.isSpectator()) {
                return;
            }
        }

        BlockPos pos = entity.getPosition();
        short temperature = getTemperatureAtPos(world, pos);

        // Check if wearing full space suit (provides complete protection)
        float protection = getTemperatureProtection(entity);
        if (protection >= Float.MAX_VALUE / 2) {
            // Full suit provides complete protection
            return;
        }

        // Apply heat damage (>70°C)
        if (temperature > MAX_LIVEABLE_TEMPERATURE) {
            // Check for fire resistance potion effect
            if (!entity.isImmuneToFire() && !entity.isPotionActive(net.minecraft.init.MobEffects.FIRE_RESISTANCE)) {
                entity.attackEntityFrom(HEAT_DAMAGE_SOURCE, BURN_DAMAGE);
                entity.setFire(BURN_FIRE_SECONDS);
            }
        }
        // Apply freeze damage (<-50°C)
        else if (temperature < MIN_LIVEABLE_TEMPERATURE) {
            entity.attackEntityFrom(FREEZE_DAMAGE_SOURCE, FREEZE_DAMAGE);
            // Note: 1.12.2 doesn't have freeze mechanics like 1.20 (setTicksFrozen)
            // We just apply damage instead
        }
    }

    /**
     * Gets the temperature of an entity based on its current position.
     */
    public static short getTemperature(Entity entity) {
        return getTemperatureAtPos(entity.world, entity.getPosition());
    }

    // Original methods for setting/removing temperature data

    public static void setTemperature(World world, BlockPos pos, short temperature) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        PlanetDataStorage storage = PlanetDataStorage.get(world);
        PlanetData data = storage.getData(pos);

        if (data == null) {
            boolean oxygen = OxygenSystemExtended.hasOxygenAtPos(world, pos);
            float gravity = GravitySystem.getGravityAtPos(world, pos);
            data = new PlanetData(oxygen, temperature, gravity);
        } else {
            data.setTemperature(temperature);
        }

        storage.setData(pos, data);
    }

    public static void setTemperature(World world, Collection<BlockPos> positions, short temperature) {
        if (world.isRemote || !(world instanceof WorldServer)) {
            return;
        }

        for (BlockPos pos : positions) {
            setTemperature(world, pos, temperature);
        }
    }

    public static void removeTemperature(World world, BlockPos pos) {
        setTemperature(world, pos, getTemperatureInDimension(world));
    }

    public static void removeTemperature(World world, Collection<BlockPos> positions) {
        setTemperature(world, positions, getTemperatureInDimension(world));
    }
}
