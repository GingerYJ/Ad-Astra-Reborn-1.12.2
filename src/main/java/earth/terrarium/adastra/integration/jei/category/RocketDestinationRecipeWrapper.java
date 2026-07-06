package earth.terrarium.adastra.integration.jei.category;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RocketDestinationRecipeWrapper implements IRecipeWrapper {

    private static final int LIST_X = 8;
    private static final int LIST_Y = 38;
    private static final int COLUMN_WIDTH = 80;
    private static final int ROW_HEIGHT = 13;
    private static final int ROWS_PER_COLUMN = 8;

    private final RocketDestinationRecipe recipe;

    public RocketDestinationRecipeWrapper(RocketDestinationRecipe recipe) {
        this.recipe = recipe;
    }

    public RocketDestinationRecipe getRecipe() {
        return recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.getRocketStack());
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        drawStarMapBackground(recipeWidth, recipeHeight);

        minecraft.fontRenderer.drawStringWithShadow(
            I18n.format("jei.ad_astra.rocket_destinations.tier", recipe.getRocketTier()), 30, 8, 0xEAF6FF);
        minecraft.fontRenderer.drawString(
            I18n.format("jei.ad_astra.rocket_destinations.page", recipe.getPage() + 1, recipe.getTotalPages()),
            30, 20, 0x8FB7D8);

        drawLegend(minecraft, recipeWidth);

        List<RocketDestinationRecipe.Destination> destinations = recipe.getDestinations();
        for (int i = 0; i < destinations.size(); i++) {
            RocketDestinationRecipe.Destination destination = destinations.get(i);
            int column = i / ROWS_PER_COLUMN;
            int row = i % ROWS_PER_COLUMN;
            int x = LIST_X + column * COLUMN_WIDTH;
            int y = LIST_Y + row * ROW_HEIGHT;
            drawDestination(minecraft, destination, x, y);
        }

        minecraft.fontRenderer.drawString(I18n.format("jei.ad_astra.rocket_destinations.hint"), 8, 145, 0x7EA4C4);
    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        RocketDestinationRecipe.Destination destination = destinationAt(mouseX, mouseY);
        if (destination == null) {
            return Collections.emptyList();
        }
        List<String> tooltip = new ArrayList<>();
        tooltip.add(displayName(destination));
        tooltip.add(I18n.format("jei.ad_astra.rocket_destinations.dimension", destination.getDimensionId()));
        tooltip.add(I18n.format("jei.ad_astra.rocket_destinations.required_tier", destination.getRequiredTier()));
        tooltip.add(destination.canReach(recipe.getRocketTier())
            ? TextFormatting.GREEN + I18n.format("jei.ad_astra.rocket_destinations.reachable")
            : TextFormatting.RED + I18n.format("jei.ad_astra.rocket_destinations.unreachable"));
        return tooltip;
    }

    private RocketDestinationRecipe.Destination destinationAt(int mouseX, int mouseY) {
        List<RocketDestinationRecipe.Destination> destinations = recipe.getDestinations();
        for (int i = 0; i < destinations.size(); i++) {
            int column = i / ROWS_PER_COLUMN;
            int row = i % ROWS_PER_COLUMN;
            int x = LIST_X + column * COLUMN_WIDTH;
            int y = LIST_Y + row * ROW_HEIGHT;
            if (mouseX >= x && mouseX < x + COLUMN_WIDTH - 3 && mouseY >= y && mouseY < y + ROW_HEIGHT) {
                return destinations.get(i);
            }
        }
        return null;
    }

    private void drawDestination(Minecraft minecraft, RocketDestinationRecipe.Destination destination, int x, int y) {
        boolean reachable = destination.canReach(recipe.getRocketTier());
        int bg = reachable ? 0x553B6A8F : 0x552B2630;
        int border = reachable ? 0xFF5FD7FF : 0xFF7E4650;
        int dot = reachable ? 0xFF7DFFB1 : 0xFFFF6565;
        int text = reachable ? 0xFFE9FBFF : 0xFF9A9A9A;

        Gui.drawRect(x, y, x + COLUMN_WIDTH - 6, y + ROW_HEIGHT - 2, bg);
        Gui.drawRect(x, y, x + COLUMN_WIDTH - 6, y + 1, border);
        Gui.drawRect(x, y + ROW_HEIGHT - 3, x + COLUMN_WIDTH - 6, y + ROW_HEIGHT - 2, 0x44102035);
        Gui.drawRect(x + 4, y + 4, x + 8, y + 8, dot);
        Gui.drawRect(x + 5, y + 3, x + 7, y + 9, dot);

        String name = minecraft.fontRenderer.trimStringToWidth(displayName(destination), 45);
        minecraft.fontRenderer.drawString(name, x + 12, y + 2, text);
        minecraft.fontRenderer.drawString(String.valueOf(destination.getRequiredTier()), x + COLUMN_WIDTH - 16, y + 2,
            reachable ? 0xFFB7F7FF : 0xFFFFA0A0);
    }

    private void drawLegend(Minecraft minecraft, int recipeWidth) {
        int x = recipeWidth - 77;
        int y = 8;
        Gui.drawRect(x, y, x + 5, y + 5, 0xFF7DFFB1);
        minecraft.fontRenderer.drawString(I18n.format("jei.ad_astra.rocket_destinations.reachable"), x + 8, y - 1, 0xB7F7FF);
        Gui.drawRect(x, y + 11, x + 5, y + 16, 0xFFFF6565);
        minecraft.fontRenderer.drawString(I18n.format("jei.ad_astra.rocket_destinations.unreachable"), x + 8, y + 10, 0xFFB0B0);
    }

    private void drawStarMapBackground(int recipeWidth, int recipeHeight) {
        Gui.drawRect(0, 0, recipeWidth, recipeHeight, 0xFF020612);
        Gui.drawRect(0, 0, recipeWidth, 32, 0xFF061A33);
        Gui.drawRect(0, 32, recipeWidth, 78, 0xFF041122);
        Gui.drawRect(0, 78, recipeWidth, recipeHeight, 0xFF01040D);
        Gui.drawRect(0, 34, recipeWidth, 36, 0x663766AA);
        for (int i = 0; i < 34; i++) {
            int x = (i * 37 + 11) % Math.max(1, recipeWidth);
            int y = (i * 19 + 7) % Math.max(1, recipeHeight - 16);
            int color = (i % 5 == 0) ? 0xCCBFDFFF : 0x889BC7FF;
            Gui.drawRect(x, y, x + 1, y + 1, color);
        }
        Gui.drawRect(4, 4, recipeWidth - 4, 5, 0x665FD7FF);
        Gui.drawRect(4, recipeHeight - 5, recipeWidth - 4, recipeHeight - 4, 0x444092C0);
        Gui.drawRect(4, 4, 5, recipeHeight - 4, 0x444092C0);
        Gui.drawRect(recipeWidth - 5, 4, recipeWidth - 4, recipeHeight - 4, 0x444092C0);
    }

    private String displayName(RocketDestinationRecipe.Destination destination) {
        String key = "planet.ad_astra." + destination.getName();
        String value = I18n.format(key);
        return value.equals(key) ? destination.getName() : value;
    }
}
