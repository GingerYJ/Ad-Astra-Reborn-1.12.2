package earth.terrarium.adastra.common.recipe;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Recipe implementation for the NASA Workbench.
 * Complex multi-slot crafting with the 14 input slots used by rocket recipes.
 */
public class NASAWorkbenchRecipe extends MachineRecipe {

    private final int width;
    private final int height;

    /**
     * Creates a NASA Workbench recipe.
     *
     * @param id Recipe identifier
     * @param inputs List of input ItemStacks
     * @param outputs List of output ItemStacks
     * @param width Recipe pattern width (1-3)
     * @param height Recipe pattern height (1-3)
     * @param processingTime Total time in ticks to process this recipe
     * @param energyPerTick Energy consumed per tick during processing
     */
    public NASAWorkbenchRecipe(String id, List<ItemStack> inputs, List<ItemStack> outputs, int width, int height, int processingTime, int energyPerTick) {
        super(id, copyStacks(inputs), copyStacks(outputs), processingTime, energyPerTick);
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    /**
     * Creates a NASA Workbench recipe with a single output.
     *
     * @param id Recipe identifier
     * @param inputs List of input ItemStacks
     * @param output Output ItemStack
     * @param width Recipe pattern width (1-3)
     * @param height Recipe pattern height (1-3)
     * @param processingTime Total time in ticks to process this recipe
     * @param energyPerTick Energy consumed per tick during processing
     */
    public NASAWorkbenchRecipe(String id, List<ItemStack> inputs, ItemStack output, int width, int height, int processingTime, int energyPerTick) {
        this(id, inputs, Arrays.asList(output), width, height, processingTime, energyPerTick);
    }

    @Override
    public boolean matches(ItemStack[] stacks) {
        if (stacks == null) {
            return false;
        }
        if (usesExactSlots()) {
            return matchesExactSlots(stacks);
        }
        if (stacks.length < 9) {
            return false;
        }
        if (!areExtraSlotsEmpty(stacks)) {
            return false;
        }

        // Match the recipe pattern within the 3x3 grid
        for (int row = 0; row <= 3 - height; row++) {
            for (int col = 0; col <= 3 - width; col++) {
                if (matchesAt(stacks, row, col, true) || matchesAt(stacks, row, col, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchesExactSlots(ItemStack[] stacks) {
        if (stacks.length < inputs.size()) {
            return false;
        }
        for (int i = 0; i < inputs.size(); i++) {
            ItemStack patternStack = inputs.get(i);
            ItemStack slotStack = stacks[i];
            if (patternStack.isEmpty()) {
                if (slotStack != null && !slotStack.isEmpty()) {
                    return false;
                }
            } else if (slotStack == null || !RecipeHelper.itemsMatch(slotStack, patternStack) || slotStack.getCount() < patternStack.getCount()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the recipe pattern matches at a specific position in the grid.
     *
     * @param stacks The 3x3 grid of ItemStacks
     * @param startRow Starting row for pattern matching
     * @param startCol Starting column for pattern matching
     * @param mirror Whether to check mirrored pattern
     * @return true if pattern matches at this position
     */
    private boolean matchesAt(ItemStack[] stacks, int startRow, int startCol, boolean mirror) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int gridIndex = row * 3 + col;
                ItemStack gridStack = stacks[gridIndex];

                // Determine which input to compare against
                int patternRow = row - startRow;
                int patternCol = col - startCol;

                // Outside the pattern area should be empty
                if (patternRow < 0 || patternRow >= height || patternCol < 0 || patternCol >= width) {
                    if (gridStack != null && !gridStack.isEmpty()) {
                        return false;
                    }
                    continue;
                }

                // Inside the pattern area
                int patternIndex;
                if (mirror) {
                    patternIndex = patternRow * width + (width - 1 - patternCol);
                } else {
                    patternIndex = patternRow * width + patternCol;
                }

                if (patternIndex >= inputs.size()) {
                    if (gridStack != null && !gridStack.isEmpty()) {
                        return false;
                    }
                    continue;
                }

                ItemStack patternStack = inputs.get(patternIndex);
                if (patternStack.isEmpty()) {
                    if (gridStack != null && !gridStack.isEmpty()) {
                        return false;
                    }
                } else {
                    if (gridStack == null || !RecipeHelper.itemsMatch(gridStack, patternStack) || gridStack.getCount() < patternStack.getCount()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isValidForSlot(int slot, ItemStack stack) {
        if (slot < 0 || stack == null || stack.isEmpty()) {
            return false;
        }
        if (usesExactSlots()) {
            if (slot >= inputs.size()) {
                return false;
            }
            ItemStack patternStack = inputs.get(slot);
            return !patternStack.isEmpty() && RecipeHelper.itemsMatch(stack, patternStack);
        }
        for (ItemStack input : inputs) {
            if (!input.isEmpty() && RecipeHelper.itemsMatch(stack, input)) {
                return true;
            }
        }
        return false;
    }

    public boolean consumeInputs(ItemStack[] stacks) {
        int[] requiredCounts = getRequiredInputCounts(stacks);
        if (requiredCounts == null) {
            return false;
        }
        for (int i = 0; i < requiredCounts.length && i < stacks.length; i++) {
            if (requiredCounts[i] > 0 && stacks[i] != null) {
                stacks[i].shrink(requiredCounts[i]);
            }
        }
        return true;
    }

    public boolean matchesOutput(ItemStack output) {
        return output != null && !output.isEmpty() && RecipeHelper.itemsMatchWithNBT(getResult(), output);
    }

    private int[] getRequiredInputCounts(ItemStack[] stacks) {
        if (stacks == null) {
            return null;
        }
        if (usesExactSlots()) {
            if (!matchesExactSlots(stacks)) {
                return null;
            }
            int[] counts = new int[stacks.length];
            for (int i = 0; i < inputs.size() && i < counts.length; i++) {
                ItemStack input = inputs.get(i);
                if (!input.isEmpty()) {
                    counts[i] = input.getCount();
                }
            }
            return counts;
        }
        if (stacks.length < 9 || !areExtraSlotsEmpty(stacks)) {
            return null;
        }
        for (int row = 0; row <= 3 - height; row++) {
            for (int col = 0; col <= 3 - width; col++) {
                if (matchesAt(stacks, row, col, true)) {
                    return getRequiredInputCountsAt(stacks.length, row, col, true);
                }
                if (matchesAt(stacks, row, col, false)) {
                    return getRequiredInputCountsAt(stacks.length, row, col, false);
                }
            }
        }
        return null;
    }

    private int[] getRequiredInputCountsAt(int stackCount, int startRow, int startCol, boolean mirror) {
        int[] counts = new int[stackCount];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int patternIndex = mirror ? row * width + (width - 1 - col) : row * width + col;
                if (patternIndex >= inputs.size()) {
                    continue;
                }
                ItemStack input = inputs.get(patternIndex);
                if (input.isEmpty()) {
                    continue;
                }
                int gridIndex = (startRow + row) * 3 + startCol + col;
                if (gridIndex >= 0 && gridIndex < counts.length) {
                    counts[gridIndex] = input.getCount();
                }
            }
        }
        return counts;
    }

    private boolean areExtraSlotsEmpty(ItemStack[] stacks) {
        for (int i = 9; i < stacks.length; i++) {
            if (stacks[i] != null && !stacks[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean usesExactSlots() {
        return inputs.size() > 9 || height > 3;
    }

    /**
     * Gets the recipe pattern width.
     *
     * @return Pattern width (1-3)
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the recipe pattern height.
     *
     * @return Pattern height (1-3)
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the primary output ItemStack for this recipe.
     *
     * @return Primary output ItemStack
     */
    public ItemStack getResult() {
        return getOutput();
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        List<ItemStack> copies = new ArrayList<>();
        if (stacks == null) {
            return copies;
        }
        for (ItemStack stack : stacks) {
            copies.add(stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return copies;
    }
}
