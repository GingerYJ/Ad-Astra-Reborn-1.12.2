package earth.terrarium.adastra.common.handler;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.systems.GravitySystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Enhanced gravity event handler.
 * Handles gravity motion override, Gravity Normalizer detection, jump adjustments, and fall damage.
 */
public class GravityEventHandler {

    private static final double VANILLA_GRAVITY = 0.08D;
    private static final float GRAVITY_EPSILON = 1.0e-4F;
    private static final float LOW_GRAVITY_THRESHOLD = 0.3F;
    private static final float LOW_GRAVITY_FALL_DISTANCE_MULTIPLIER = 3.0F;
    private static final double JUMP_BOOST_SCALE = 0.42D;  // Vanilla jump motion

    private static final double DEFAULT_ENTITY_GRAVITY = 0.04D;
    private static final double ARROW_GRAVITY = 0.05D;
    private static final double FISHING_HOOK_GRAVITY = 0.03D;
    private static final double THROWABLE_GRAVITY = 0.03D;
    private static final double POTION_GRAVITY = 0.05D;
    private static final double EXPERIENCE_BOTTLE_GRAVITY = 0.07D;
    private static final double BOAT_GRAVITY = 0.04D;
    private static final double EXPERIENCE_ORB_GRAVITY = 0.03D;

    /**
     * Applies gravity motion override to entities.
     * This event fires for all living entities on both client and server.
     *
     * Priority: HIGHEST to ensure it runs before other gravity modifications.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (AdAstraConfig.disableGravity) {
            return;
        }

        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || entity.world == null) {
            return;
        }

        // Skip if in water, lava, or elytra flying
        if (entity.isInWater() || entity.isInLava() || entity.isElytraFlying()) {
            return;
        }

        // Skip if player is in creative flying mode
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.capabilities.isFlying) {
                return;
            }
        }

        // Get effective gravity at entity's position (includes Gravity Normalizer check)
        float gravityMultiplier = GravitySystem.getGravityForEntity(entity);

        // Skip if Earth gravity
        if (Math.abs(gravityMultiplier - 1.0F) < GRAVITY_EPSILON) {
            return;
        }

        // Apply gravity compensation
        // Formula: compensation = vanilla_gravity * (1 - multiplier)
        // - Low gravity (e.g., Moon 0.166): compensation = +0.067, reduces fall speed, increases jump height
        // - High gravity (e.g., Venus 0.904): compensation = -0.008, increases fall speed, reduces jump height
        double compensation = VANILLA_GRAVITY * (1.0D - gravityMultiplier);
        entity.motionY += compensation;
        entity.velocityChanged = true;
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (AdAstraConfig.disableGravity || event.phase != TickEvent.Phase.END) {
            return;
        }

        World world = event.world;
        if (world == null) {
            return;
        }

        Entity[] entities = world.loadedEntityList.toArray(new Entity[0]);
        for (Entity entity : entities) {
            applyNonLivingEntityGravity(entity);
        }
    }

    private void applyNonLivingEntityGravity(Entity entity) {
        if (entity == null || entity.world == null || entity instanceof EntityLivingBase || entity.hasNoGravity()) {
            return;
        }
        if (entity.isInWater() || entity.isInLava()) {
            return;
        }

        double vanillaGravity = getVanillaGravity(entity);
        if (vanillaGravity <= 0.0D) {
            return;
        }

        float gravityMultiplier = GravitySystem.getGravityForEntity(entity);
        if (Math.abs(gravityMultiplier - 1.0F) < GRAVITY_EPSILON) {
            return;
        }

        entity.motionY += vanillaGravity * (1.0D - gravityMultiplier);
        entity.velocityChanged = true;
    }

    private double getVanillaGravity(Entity entity) {
        if (entity instanceof EntityExpBottle) {
            return EXPERIENCE_BOTTLE_GRAVITY;
        }
        if (entity instanceof EntityPotion) {
            return POTION_GRAVITY;
        }
        if (entity instanceof EntityThrowable) {
            return THROWABLE_GRAVITY;
        }
        if (entity instanceof EntityArrow) {
            return ARROW_GRAVITY;
        }
        if (entity instanceof EntityFishHook) {
            return FISHING_HOOK_GRAVITY;
        }
        if (entity instanceof EntityBoat) {
            return BOAT_GRAVITY;
        }
        if (entity instanceof EntityXPOrb) {
            return EXPERIENCE_ORB_GRAVITY;
        }
        if (entity instanceof EntityItem
            || entity instanceof EntityTNTPrimed
            || entity instanceof EntityMinecart
            || entity instanceof EntityFallingBlock) {
            return DEFAULT_ENTITY_GRAVITY;
        }
        return 0.0D;
    }

    /**
     * Adjusts fall damage based on gravity.
     * In low gravity, entities take proportionally less fall damage.
     * In high gravity, entities take proportionally more fall damage.
     *
     * Priority: HIGH to run before vanilla fall damage calculation.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingFall(LivingFallEvent event) {
        if (AdAstraConfig.disableGravity) {
            return;
        }

        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || entity.world == null) {
            return;
        }

        // Get effective gravity at entity's position
        float gravityMultiplier = GravitySystem.getGravityForEntity(entity);

        // Skip if Earth gravity
        if (Math.abs(gravityMultiplier - 1.0F) < GRAVITY_EPSILON) {
            return;
        }

        // Adjust fall distance by gravity multiplier
        // Low gravity = less damage, high gravity = more damage
        float adjustedDistance = event.getDistance() * gravityMultiplier;

        // In very low gravity, cancel fall damage entirely for short falls
        // This replicates the 1.20.x behavior where falls under a threshold are ignored
        if (gravityMultiplier < LOW_GRAVITY_THRESHOLD) {
            float threshold = LOW_GRAVITY_FALL_DISTANCE_MULTIPLIER / gravityMultiplier;
            if (event.getDistance() <= threshold) {
                event.setCanceled(true);
                return;
            }
        }

        event.setDistance(adjustedDistance);
    }

    /**
     * Adjusts jump height based on gravity.
     * In low gravity, entities jump higher.
     * In high gravity, entities jump lower.
     *
     * Priority: HIGH to modify jump motion early.
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingJump(LivingJumpEvent event) {
        if (AdAstraConfig.disableGravity) {
            return;
        }

        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || entity.world == null) {
            return;
        }

        // Get effective gravity at entity's position
        float gravityMultiplier = GravitySystem.getGravityForEntity(entity);

        // Skip if Earth gravity
        if (Math.abs(gravityMultiplier - 1.0F) < GRAVITY_EPSILON) {
            return;
        }

        // Adjust jump height by gravity multiplier
        // Formula: new_motion = base_motion / sqrt(gravity)
        // This gives more realistic jump heights in different gravity
        // Low gravity (0.166) -> jump ~2.5x higher
        // High gravity (0.9) -> jump slightly lower
        double jumpScale = 1.0D / Math.sqrt(gravityMultiplier);

        // Apply the jump boost
        // In low gravity, increase jump motion
        // In high gravity, decrease jump motion
        double currentMotionY = entity.motionY;
        double adjustedMotionY = currentMotionY * jumpScale;

        // Apply the adjustment
        entity.motionY = adjustedMotionY;
        entity.velocityChanged = true;
    }
}
