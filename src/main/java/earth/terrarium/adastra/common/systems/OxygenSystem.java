package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.api.events.AdAstraEvents;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.performance.PerformanceTracker;
import earth.terrarium.adastra.common.registry.ModSounds;
import earth.terrarium.adastra.common.tags.ModBlockTags;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main oxygen system logic for Ad Astra.
 * Handles oxygen checking, space suit consumption, and suffocation damage.
 *
 * Based on 1.20.x OxygenApi implementation:
 * - Players in no-oxygen dimensions suffocate unless wearing a space suit with oxygen
 * - Space suits store oxygen as fluid (capacity varies by suit type)
 * - Consumes 1mB every 12 ticks
 * - Deals 2 damage every 20 ticks when no oxygen available
 */
public final class OxygenSystem {

    /**
     * Custom damage source for oxygen deprivation in space.
     * Bypasses armor since it's environmental damage.
     */
    public static final DamageSource OXYGEN_DEPRIVATION = new DamageSource("oxygen").setDamageBypassesArmor();

    /**
     * Air level to set when suffocating (mimics drowning effect).
     * Default: -80 (triggers vanilla drowning animation)
     */
    public static final int SUFFOCATION_AIR_LEVEL = -80;

    /**
     * Maximum air level for players (vanilla constant).
     * In 1.12.2, this is 300 ticks.
     */
    public static final int MAX_AIR = 300;

    // Sealed room cache for performance optimization.
    // Keyed by (dimension, pos) to isolate different worlds.
    // No time-based expiry — invalidated only by block change events,
    // since room structure never changes unless a block is placed/broken.
    private static final Map<Integer, Map<BlockPos, Set<BlockPos>>> SEALED_ROOM_CACHE = new ConcurrentHashMap<>();


    private OxygenSystem() {
    }

    /**
     * Amount of oxygen consumed per interval (in millibuckets).
     * Default: 1mB
     */
    public static int getOxygenConsumptionAmount() {
        return AdAstraConfig.oxygenConsumptionAmount;
    }

    /**
     * How often to consume oxygen from space suit (in ticks).
     * Default: 12 ticks (0.6 seconds)
     */
    public static int getOxygenConsumptionInterval() {
        return AdAstraConfig.oxygenConsumptionInterval;
    }

    /**
     * How often to check oxygen and apply damage (in ticks).
     * Default: 20 ticks (1 second)
     */
    public static int getOxygenDamageInterval() {
        return AdAstraConfig.oxygenDamageInterval;
    }

    /**
     * Amount of damage dealt when suffocating.
     * Default: 2.0 hearts per interval
     */
    public static float getOxygenDamageAmount() {
        return AdAstraConfig.oxygenDamageAmount;
    }

    /**
     * Check oxygen availability for a player and handle consumption/damage.
     * This should be called every tick from a tick handler.
     *
     * @param player The player to check
     * @param world The world the player is in
     */
    public static void checkOxygen(EntityPlayer player, World world) {
        if (player == null || world == null) {
            return;
        }

        PerformanceTracker.startSystemTiming("environment");

        // Skip for creative/spectator players
        if (player.capabilities.isCreativeMode || player.isSpectator()) {
            PerformanceTracker.endSystemTiming("environment");
            return;
        }

        // Skip if oxygen system is disabled in config
        if (AdAstraConfig.disableOxygen) {
            PerformanceTracker.endSystemTiming("environment");
            return;
        }

        // Check if oxygen is available at player's location
        boolean hasOxygen = AdAstraEvents.EntityOxygenEvent.fire(player, hasOxygen(player, world));
        if (hasOxygen) {
            // Player has oxygen, reset air level to full
            if (player.getAir() < MAX_AIR) {
                player.setAir(MAX_AIR);
            }
            PerformanceTracker.endSystemTiming("environment");
            return;
        }

        // No oxygen available - check if wearing space suit with oxygen
        if (OxygenUtils.isWearingSpaceSuit(player)) {
            if (handleSpaceSuitOxygen(player)) {
                // Successfully consumed suit oxygen or has oxygen available
                if (player.getAir() < MAX_AIR) {
                    player.setAir(MAX_AIR);
                }
                PerformanceTracker.endSystemTiming("environment");
                return;
            }
        }

        // No oxygen source available - suffocate
        if (world instanceof WorldServer
            && !AdAstraEvents.OxygenTickEvent.fire((WorldServer) world, player)) {
            PerformanceTracker.endSystemTiming("environment");
            return;
        }

        applySuffocationDamage(player);
        PerformanceTracker.endSystemTiming("environment");
    }

    /**
     * Check if the player has access to oxygen.
     *
     * @param player The player to check
     * @param world The world
     * @return true if oxygen is available, false otherwise
     */
    private static boolean hasOxygen(EntityPlayer player, World world) {
        // Check dimension-level oxygen
        if (OxygenUtils.hasOxygenInDimension(world)) {
            return true;
        }

        // Check position-level oxygen (e.g., from oxygen distributors)
        return OxygenUtils.hasOxygenAtPosition(world, player.getPosition());
    }

    /**
     * Handle oxygen consumption from space suit.
     * Only consumes oxygen every consumption interval (configurable).
     * If chest piece oxygen is insufficient, tries to refill from gas tanks in inventory.
     *
     * @param player The player wearing the space suit
     * @return true if suit has oxygen (or doesn't need to consume this tick), false if empty
     */
    private static boolean handleSpaceSuitOxygen(EntityPlayer player) {
        // Check if it's time to consume oxygen
        boolean shouldConsume = player.ticksExisted % getOxygenConsumptionInterval() == 0;

        if (!shouldConsume) {
            // Not time to consume yet, check if suit has any oxygen
            return OxygenUtils.getSpaceSuitOxygen(player) > 0;
        }

        // Time to consume oxygen - try to consume from chest piece
        int consumed = OxygenUtils.consumeSpaceSuitOxygen(player, getOxygenConsumptionAmount());

        if (consumed >= getOxygenConsumptionAmount()) {
            // Successfully consumed oxygen from chest piece
            playOxygenOuttakeSound(player);
            player.inventory.markDirty();
            return true;
        }

        // Chest piece oxygen is insufficient - try to refill from inventory gas tanks
        if (tryRefillFromInventory(player)) {
            // Successfully refilled from gas tank
            player.inventory.markDirty();
            OxygenUtils.syncInventory(player);

            // Try consuming again after refill
            consumed = OxygenUtils.consumeSpaceSuitOxygen(player, getOxygenConsumptionAmount());
            if (consumed >= getOxygenConsumptionAmount()) {
                playOxygenOuttakeSound(player);
                return true;
            }
        }

        // No oxygen available
        return false;
    }

    public static void handleUnderwaterSpaceSuitBreathing(EntityPlayer player) {
        if (!player.isInsideOfMaterial(Material.WATER) || !OxygenUtils.isWearingSpaceSuit(player)) {
            return;
        }
        if (player.ticksExisted % getOxygenConsumptionInterval() != 0) {
            return;
        }
        int amount = getOxygenConsumptionAmount();
        if (OxygenUtils.getSpaceSuitOxygen(player) <= amount) {
            return;
        }
        int consumed = OxygenUtils.consumeSpaceSuitOxygen(player, amount);
        if (consumed >= amount) {
            player.setAir(Math.min(MAX_AIR, player.getAir() + 40));
        }
    }

    /**
     * Play oxygen outtake sound when player consumes oxygen from suit.
     * @param player The player consuming oxygen
     */
    private static void playOxygenOuttakeSound(EntityPlayer player) {
        if (player.world != null && !player.world.isRemote) {
            // Play a subtle sound at player's position
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                ModSounds.OXYGEN_OUTTAKE, SoundCategory.PLAYERS, 0.3f, 1.0f + player.world.rand.nextFloat() * 0.1f);
        }
    }

    /**
     * Apply suffocation damage to the player.
     * Damage is only applied every damage interval (configurable).
     *
     * @param player The player to damage
     */
    private static void applySuffocationDamage(EntityPlayer player) {
        // Set air level to trigger drowning visuals
        player.setAir(SUFFOCATION_AIR_LEVEL);

        // Only damage every damage interval
        if (player.ticksExisted % getOxygenDamageInterval() == 0) {
            player.attackEntityFrom(OXYGEN_DEPRIVATION, getOxygenDamageAmount());
        }
    }

    /**
     * Try to refill the player's space suit chest piece from gas tanks in inventory.
     * Searches inventory for gas tanks with oxygen and transfers oxygen to the chest piece.
     *
     * @param player The player whose suit should be refilled
     * @return true if oxygen was successfully transferred, false otherwise
     */
    private static boolean tryRefillFromInventory(EntityPlayer player) {
        net.minecraft.inventory.EntityEquipmentSlot chestSlot = net.minecraft.inventory.EntityEquipmentSlot.CHEST;
        net.minecraft.item.ItemStack chestStack = player.getItemStackFromSlot(chestSlot);

        // Get the chest piece's fluid handler
        net.minecraftforge.fluids.capability.IFluidHandlerItem chestHandler =
            net.minecraftforge.fluids.FluidUtil.getFluidHandler(chestStack);

        if (chestHandler == null) {
            return false;
        }

        // Search inventory for gas tanks with oxygen
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            net.minecraft.item.ItemStack invStack = player.inventory.getStackInSlot(i);

            // Skip empty items and non-gas-tank items
            if (invStack.isEmpty() || !(invStack.getItem() instanceof earth.terrarium.adastra.common.items.GasTankItem)) {
                continue;
            }

            // Skip gas tanks without oxygen
            if (!earth.terrarium.adastra.common.items.GasTankItem.canSupplyOxygen(invStack)) {
                continue;
            }

            // Try to transfer oxygen from gas tank to chest piece
            net.minecraftforge.fluids.capability.IFluidHandlerItem tankHandler =
                net.minecraftforge.fluids.FluidUtil.getFluidHandler(invStack);

            if (tankHandler != null) {
                // Transfer as much oxygen as possible (use gas tank distribution amount)
                net.minecraftforge.fluids.FluidStack transferred =
                    net.minecraftforge.fluids.FluidUtil.tryFluidTransfer(
                        chestHandler,
                        tankHandler,
                        earth.terrarium.adastra.common.items.GasTankItem.GAS_TANK_DISTRIBUTION_AMOUNT,
                        true
                    );

                if (transferred != null && transferred.amount > 0) {
                    // Update the gas tank item in inventory
                    player.inventory.setInventorySlotContents(i, tankHandler.getContainer());
                    player.inventory.markDirty();
                    OxygenUtils.syncInventory(player);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Find all blocks in a sealed room starting from the given position using flood fill algorithm.
     * Uses BFS (Breadth-First Search) to explore connected air blocks and check if the space is sealed.
     * Results are cached with no timed expiry — invalidated by block change events.
     *
     * Based on 1.20.x FloodFill3D implementation from:
     * earth.terrarium.adastra.common.utils.floodfill.FloodFill3D
     *
     * @param world The world to search in
     * @param start Starting position (usually center of oxygen distributor output)
     * @param maxBlocks Maximum number of blocks to check (prevents performance issues and limits room size)
     * @return Set of BlockPos representing the sealed room, or null if the room is not sealed or exceeds maxBlocks
     */
    /**
     * Find all blocks in a sealed room starting from the given position using
     * flood fill (BFS). Results are cached per dimension with no time-based
     * expiry — caches are invalidated only by block place/break events.
     *
     * @param world     the world to search in
     * @param start     starting position (oxygen distributor output)
     * @param maxBlocks maximum blocks before assuming the room is unsealed
     * @return the set of blocks in the sealed room, or {@code null} if unsealed
     */
    @Nullable
    public static Set<BlockPos> findSealedRoom(World world, BlockPos start, int maxBlocks) {
        if (world == null || start == null || maxBlocks <= 0) {
            return null;
        }

        int dim = world.provider.getDimension();
        Map<BlockPos, Set<BlockPos>> dimCache = SEALED_ROOM_CACHE.get(dim);
        if (dimCache != null) {
            Set<BlockPos> cached = dimCache.get(start);
            if (cached != null) {
                return cached.isEmpty() ? null : cached;
            }
        }

        Set<BlockPos> result = performFloodFill(world, start, maxBlocks);
        SEALED_ROOM_CACHE
            .computeIfAbsent(dim, k -> new ConcurrentHashMap<>())
            .put(start, result != null ? result : Collections.emptySet());
        return result;
    }

    /**
     * BFS flood fill. Performance optimizations over a naive BFS:
     * <ul>
     *   <li>{@code canBlockSeeSky} is deferred — only called when the block
     *       is at or above start Y <em>and</em> the block above is non-solid
     *       (cheap {@code isFullCube + getLightOpacity} pre-check). In a
     *       sealed underground room this eliminates &gt;99% of sky checks.</li>
     *   <li>{@code LinkedHashSet.add()} serves as an atomic check-and-insert,
     *       removing the separate {@code contains()} call per node.</li>
     * </ul>
     */
    @Nullable
    private static Set<BlockPos> performFloodFill(World world, BlockPos start, int maxBlocks) {
        // Quick pre-check: distributor itself sees the sky → not sealed
        if (start.getY() >= 0 && couldBeSkyExposed(world, start)
            && world.canBlockSeeSky(start)) {
            return null;
        }

        Set<BlockPos> visited = new LinkedHashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        int startY = start.getY();

        while (!queue.isEmpty() && visited.size() < maxBlocks) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) {
                continue; // already visited
            }

            for (EnumFacing dir : EnumFacing.values()) {
                BlockPos neighbor = current.offset(dir);
                if (visited.contains(neighbor)) {
                    continue;
                }

                IBlockState state = world.getBlockState(neighbor);
                if (!isBlockPassable(world, neighbor, state, dir.getOpposite())) {
                    continue; // solid boundary
                }

                // ---- lazy sky-exposure check ----
                if (neighbor.getY() >= startY && couldBeSkyExposed(world, neighbor)) {
                    if (world.canBlockSeeSky(neighbor)) {
                        return null; // leak to outside
                    }
                }

                queue.add(neighbor);
            }
        }

        return visited.size() < maxBlocks ? visited : null;
    }

    /**
     * Fast pre-filter before calling the expensive {@code canBlockSeeSky}.
     * Only returns {@code true} when the block directly above is non-full-cube
     * (air, slab, glass, etc.), meaning sky light <em>could</em> theoretically
     * reach this position. Uses only cached block-state properties.
     */
    private static boolean couldBeSkyExposed(World world, BlockPos pos) {
        IBlockState above = world.getBlockState(pos.up());
        return !above.isFullCube() && above.getLightOpacity(world, pos.up()) < 15;
    }

    /**
     * Invalidate cached sealed rooms within {@code radius} blocks of a
     * block change. Call from {@code BlockEvent.PlaceEvent} /
     * {@code BlockEvent.BreakEvent} so that room scans reflect new
     * walls/doors immediately.
     */
    public static void invalidateNearbyCache(World world, BlockPos center, int radius) {
        if (world == null || center == null) return;
        int dim = world.provider.getDimension();
        Map<BlockPos, Set<BlockPos>> dimCache = SEALED_ROOM_CACHE.get(dim);
        if (dimCache == null || dimCache.isEmpty()) return;

        long r2 = (long) radius * radius;
        dimCache.keySet().removeIf(cachedPos -> {
            long dx = cachedPos.getX() - center.getX();
            long dy = cachedPos.getY() - center.getY();
            long dz = cachedPos.getZ() - center.getZ();
            return dx * dx + dy * dy + dz * dz <= r2;
        });
    }

    /**
     * Invalidate a single cache entry (legacy API).
     */
    public static void invalidateSealedRoomCache(BlockPos pos) {
        for (Map<BlockPos, Set<BlockPos>> dimCache : SEALED_ROOM_CACHE.values()) {
            dimCache.remove(pos);
        }
    }

    /**
     * Clear all cached sealed rooms across all dimensions.
     */
    public static void clearSealedRoomCache() {
        SEALED_ROOM_CACHE.clear();
    }
    /**
     * Check if a block allows oxygen to pass through it.
     * Based on 1.20.x TEST_FULL_SEAL predicate logic.
     *
     * @param world The world
     * @param pos Block position
     * @param state Block state at position
     * @param fromDirection Direction we're coming from
     * @return true if oxygen can pass through, false if it's sealed
     */
    private static boolean isBlockPassable(World world, BlockPos pos, IBlockState state, EnumFacing fromDirection) {
        // Air blocks are always passable
        if (state.getBlock().isAir(state, world, pos)) {
            return true;
        }

        Block block = state.getBlock();
        if (ModBlockTags.PASSES_FLOOD_FILL.contains(block)) {
            return true;
        }
        if (ModBlockTags.BLOCKS_FLOOD_FILL.contains(block)) {
            return false;
        }

        // Check if block is a full cube (solid on all sides)
        if (state.isFullCube()) {
            return false; // Solid blocks don't allow oxygen to pass
        }

        // For partial blocks, check if the side facing the direction we came from is solid
        if (!isSideSolid(world, pos, state, fromDirection)) {
            return true; // Side is not solid, oxygen can pass
        }

        // The side is solid, but we need to check if this partial block has other openings
        // Check other directions to see if there's a potential air path
        for (EnumFacing dir : EnumFacing.values()) {
            if (dir.getAxis() == fromDirection.getAxis()) {
                continue; // Skip the axis we came from
            }

            BlockPos adjacentPos = pos.offset(dir);
            IBlockState adjacentState = world.getBlockState(adjacentPos);

            // If there's air adjacent in another direction, this partial block has openings
            if (adjacentState.getBlock().isAir(adjacentState, world, adjacentPos)) {
                return true;
            }
        }

        // All checks suggest this block seals in this direction
        return false;
    }

    /**
     * Check if a block side is solid (blocks oxygen flow).
     *
     * @param world The world
     * @param pos Block position
     * @param state Block state
     * @param side Side to check
     * @return true if the side is solid and blocks oxygen
     */
    private static boolean isSideSolid(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        // In 1.12.2, we use isSideSolid method
        return state.isSideSolid(world, pos, side);
    }

    /**
     * Check if a passable block position represents a leak to the outside.
     * A leak occurs when we encounter air or a non-sealed block that is not enclosed.
     *
     * For simplicity, we consider air blocks as potential interior space.
     * The flood fill will naturally find leaks by exceeding maxBlocks or reaching open space.
     *
     * @param world The world
     * @param pos Block position
     * @param state Block state
     * @return true if this represents a leak to outside, false if it's interior space
     */
    private static boolean isLeakingToOutside(World world, BlockPos pos, IBlockState state) {
        // Simple heuristic: if it's air and we can't find any solid blocks nearby in any direction,
        // it might be open to outside. However, the maxBlocks limit handles most cases.
        // For now, we'll rely on the maxBlocks limit to detect leaks.
        // More sophisticated leak detection would require checking if we can reach world boundaries
        // or unloaded chunks, which is expensive.

        // Return false to let the flood fill naturally find leaks through the maxBlocks limit
        return false;
    }

    /**
     * Check if a block at the given position is sealed (blocks oxygen flow).
     * This is a simplified check used by oxygen distributors and other systems.
     *
     * @param world The world
     * @param pos Block position to check
     * @return true if the block is sealed (blocks oxygen), false otherwise
     */
    public static boolean isBlockSealed(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }

        IBlockState state = world.getBlockState(pos);

        // Air is not sealed
        if (state.getBlock().isAir(state, world, pos)) {
            return false;
        }

        // Full cubes are sealed
        if (state.isFullCube()) {
            return true;
        }

        // Check if all sides are solid (fully enclosed block)
        for (EnumFacing facing : EnumFacing.values()) {
            if (!state.isSideSolid(world, pos, facing)) {
                return false; // At least one side is not solid
            }
        }

        return true; // All sides are solid
    }
}
