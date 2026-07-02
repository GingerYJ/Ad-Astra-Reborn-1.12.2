package earth.terrarium.adastra.common.entities.ai;

import earth.terrarium.adastra.common.entities.mob.GlacianRamEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

/**
 * AI task for Glacian Ram charging attack with knockback
 */
public class EntityAIRamCharge extends EntityAIBase {

    private final GlacianRamEntity ram;
    private final double speed;
    private final double knockbackStrength;
    private EntityLivingBase target;
    private int chargeCooldown;
    private int chargeTimer;
    private boolean isCharging;

    public EntityAIRamCharge(GlacianRamEntity ram, double speed, double knockbackStrength) {
        this.ram = ram;
        this.speed = speed;
        this.knockbackStrength = knockbackStrength;
        this.setMutexBits(1); // MOVE flag
    }

    @Override
    public boolean shouldExecute() {
        if (chargeCooldown > 0) {
            chargeCooldown--;
            return false;
        }

        this.target = ram.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return false;
        }

        double distance = ram.getDistanceSq(target);
        return distance > 16.0d && distance < 144.0d && ram.onGround; // 4-12 blocks
    }

    @Override
    public boolean shouldContinueExecuting() {
        return isCharging && chargeTimer > 0 && target != null && target.isEntityAlive();
    }

    @Override
    public void startExecuting() {
        isCharging = true;
        chargeTimer = 60; // 3 seconds
    }

    @Override
    public void resetTask() {
        isCharging = false;
        chargeTimer = 0;
        chargeCooldown = 200 + ram.getRNG().nextInt(100); // 10-15 seconds
        ram.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        if (target == null) {
            return;
        }

        chargeTimer--;

        // Look at target
        ram.getLookHelper().setLookPositionWithEntity(target, 30.0f, 30.0f);

        // Charge towards target
        double dx = target.posX - ram.posX;
        double dz = target.posZ - ram.posZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0.1) {
            // Normalize and apply speed
            ram.motionX = (dx / distance) * speed;
            ram.motionZ = (dz / distance) * speed;

            // Check for collision with target
            if (ram.getDistanceSq(target) < 4.0d) { // Within 2 blocks
                // Apply knockback
                Vec3d knockback = new Vec3d(dx, 0, dz).normalize().scale(knockbackStrength);
                target.addVelocity(knockback.x, 0.3, knockback.z);
                target.velocityChanged = true;

                // Deal damage
                ram.attackEntityAsMob(target);

                // End charge
                isCharging = false;
                chargeTimer = 0;
            }
        }
    }
}
