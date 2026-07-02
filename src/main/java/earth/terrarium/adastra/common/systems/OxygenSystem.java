package earth.terrarium.adastra.common.systems;

import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.performance.PerformanceTracker;
import earth.terrarium.adastra.common.registry.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    private static final int MAX_AIR = 300;

    // Sealed room cache for performance optimization
    private static final Map<BlockPos, CachedSealedRoom> SEALED_ROOM_CACHE = new ConcurrentHashMap<>();
    private static final int SEALED_ROOM_CACHE_LIFETIME = 60; // 3 seconds

    private static class CachedSealedRoom {
        final Set<BlockPos> positions;
        final long timestamp;
        final boolean isSealed;

        CachedSealedRoom(Set<BlockPos> positions, long timestamp, boolean isSealed) {
            this.positions = positions;
            this.timestamp = timestamp;
            this.isSealed = isSealed;
        }

        boolean isExpired(long currentTime) {
            return currentTime - timestamp > SEALED_ROOM_CACHE_LIFETIME;
        }
    }

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
        if (hasOxygen(player, world)) {
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
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Find all blocks in a sealed room starting from the given position using flood fill algorithm.
     * Uses BFS (Breadth-First Search) to explore connected air blocks and check if the space is sealed.
     * Results are cached for SEALED_ROOM_CACHE_LIFETIME ticks to improve performance.
     *
     * Based on 1.20.x FloodFill3D implementation from:
     * earth.terrarium.adastra.common.utils.floodfill.FloodFill3D
     *
     * @param world The world to search in
     * @param start Starting position (usually center of oxygen distributor output)
     * @param maxBlocks Maximum number of blocks to check (prevents performance issues and limits room size)
     * @return Set of BlockPos representing the sealed room, or null if the room is not sealed or exceeds maxBlocks
     */
    @Nullable
    public static Set<BlockPos> findSealedRoom(World world, BlockPos start, int maxBlocks) {
        if (world == null || start == null || maxBlocks <= 0) {
            return null;
        }

        // Check cache first
        long currentTime = world.getTotalWorldTime();
        CachedSealedRoom cached = SEALED_ROOM_CACHE.get(start);
        if (cached != null && !cached.isExpired(currentTime)) {
            return cached.isSealed ? cached.positions : null;
        }

        // Perform flood fill
        Set<BlockPos> result = performFloodFill(world, start, maxBlocks);

        // Cache the result
        boolean isSealed = result != null;
        SEALED_ROOM_CACHE.put(start, new CachedSealedRoom(result, currentTime, isSealed));

        // Clean up old cache entries periodically
        if (world.getTotalWorldTime() % 100 == 0) {
            cleanupSealedRoomCache(currentTime);
        }

        return result;
    }

    /**
     * Perform the actual flood fill without caching.
     */
    @Nullable
    private static Set<BlockPos> performFloodFill(World world, BlockPos start, int maxBlocks) {
        // Use LinkedHashSet to maintain insertion order (useful for debugging/rendering)
        Set<BlockPos> visitedPositions = new LinkedHashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(start);

        // BFS flood fill
        while (!queue.isEmpty() && visitedPositions.size() < maxBlocks) {
            BlockPos current = queue.poll();

            // Skip if already visited
            if (visitedPositions.contains(current)) {
                continue;
            }

            visitedPositions.add(current);

            // Check all 6 directions (up, down, north, south, east, west)
            for (EnumFacing direction : EnumFacing.values()) {
                BlockPos neighbor = current.offset(direction);

                // Skip if already visited
                if (visitedPositions.contains(neighbor)) {
                    continue;
                }

                IBlockState neighborState = world.getBlockState(neighbor);

                // Check if this neighbor allows oxygen to pass through
                if (isBlockPassable(world, neighbor, neighborState, direction.getOpposite())) {
                    // If the block is passable (air or non-sealed), check if we're leaking to outside
                    if (isLeakingToOutside(world, neighbor, neighborState)) {
                        // Room is not sealed - oxygen would leak out
                        return null;
                    }

                    // This is a valid air space within the room
                    queue.add(neighbor);
                } else {
                    // This is a solid/sealing block - it forms the boundary of the room
                    // We don't add it to the queue, but it's not a leak either
                }
            }
        }

        // Check if we exceeded the maximum block limit
        if (visitedPositions.size() >= maxBlocks) {
            // Room is too large or infinite
            return null;
        }

        return visitedPositions;
    }

    /**
     * Clean up expired sealed room cache entries.
     */
    private static void cleanupSealedRoomCache(long currentTime) {
        SEALED_ROOM_CACHE.entrySet().removeIf(entry -> entry.getValue().isExpired(currentTime));
    }

    /**
     * Invalidate sealed room cache for a specific position.
     * Call this when blocks are placed/broken near oxygen distributors.
     */
    public static void invalidateSealedRoomCache(BlockPos pos) {
        SEALED_ROOM_CACHE.remove(pos);
    }

    /**
     * Clear all sealed room caches.
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

        // TODO: Add support for block tags when implemented
        // In 1.20.x, this checks ModBlockTags.PASSES_FLOOD_FILL and BLOCKS_FLOOD_FILL
        // For now, we'll use basic block property checks

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
