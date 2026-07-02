package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PlanetSelectionGui extends GuiScreen {

    private static final ResourceLocation SELECTION_MENU = texture("selection_menu");
    private static final ResourceLocation SMALL_SELECTION_MENU = texture("small_selection_menu");
    private static final ResourceLocation BUTTON = texture("button");
    private static final ResourceLocation BUTTON_HIGHLIGHTED = texture("button_highlighted");
    private static final ResourceLocation BACK_BUTTON = texture("back_button");
    private static final ResourceLocation BACK_BUTTON_HIGHLIGHTED = texture("back_button_highlighted");
    private static final ResourceLocation PLUS_BUTTON = texture("plus_button");
    private static final ResourceLocation PLUS_BUTTON_HIGHLIGHTED = texture("plus_button_highlighted");
    private static final ResourceLocation SUN = new ResourceLocation(Reference.MOD_ID, "textures/environment/sun.png");

    private static final int MENU_X = 7;
    private static final int MENU_WIDTH = 209;
    private static final int MENU_HEIGHT = 177;
    private static final int SMALL_MENU_WIDTH = 105;
    private static final int BUTTON_WIDTH = 99;
    private static final int BUTTON_HEIGHT = 20;

    private final int rocketTier;
    private final List<PlanetDimensionProperties> planets = new ArrayList<>();
    private PlanetDimensionProperties selectedPlanet;
    private int scrollAmount;

    public PlanetSelectionGui(int rocketTier) {
        this.rocketTier = rocketTier;
    }

    @Override
    public void initGui() {
        planets.clear();
        if (mc.player != null && mc.player.dimension != 0) {
            planets.add(PlanetTravelHelper.EARTH_PROPERTIES);
        }
        for (PlanetDimensionProperties planet : PlanetTravelHelper.getPlanets()) {
            if (mc.player == null || mc.player.dimension != planet.getDimensionId()) {
                planets.add(planet);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawSpaceBackground();
        drawSolarSystem(partialTicks);
        drawSelectionMenu(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0) {
            return;
        }

        int menuY = menuY();
        if (selectedPlanet != null && isInside(mouseX, mouseY, MENU_X + 3, menuY + 3, 12, 12)) {
            selectedPlanet = null;
            scrollAmount = 0;
            return;
        }

        int listTop = height / 2 - 41;
        for (int i = 0; i < planets.size(); i++) {
            PlanetDimensionProperties planet = planets.get(i);
            int buttonY = listTop + i * 24 - scrollAmount;
            if (buttonY <= height / 2 - 63 || buttonY >= height / 2 + 88) {
                continue;
            }
            if (isInside(mouseX, mouseY, MENU_X + 3, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
                && PlanetTravelHelper.canRocketTierReach(rocketTier, planet)) {
                selectedPlanet = planet;
                return;
            }
        }

        if (selectedPlanet != null && isInside(mouseX, mouseY, MENU_X + 107, height / 2 - 77, BUTTON_WIDTH, BUTTON_HEIGHT)) {
            NetworkHandler.CHANNEL.sendToServer(new PacketLandPlanet(selectedPlanet.getDimensionId()));
            mc.displayGuiScreen(null);
        }
        if (selectedPlanet != null && isInside(mouseX, mouseY, MENU_X + 107, height / 2 - 41, 12, 12)
            && canConstructSelectedSpaceStation()) {
            NetworkHandler.CHANNEL.sendToServer(new PacketConstructSpaceStation(selectedPlanet.getDimensionId()));
            mc.displayGuiScreen(null);
            return;
        }
        if (selectedPlanet != null) {
            int orbitDimensionId = PlanetTravelHelper.getOrbitDimensionId(selectedPlanet.getDimensionId());
            List<SpaceStation> stations = getSelectedSpaceStations();
            int stationTop = height / 2 + 29;
            for (int i = 0; i < stations.size() && i < 2; i++) {
                int y = stationTop + i * 22;
                if (isInside(mouseX, mouseY, MENU_X + 107, y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                    SpaceStation station = stations.get(i);
                    if (isPlayerAtSpaceStation(station)) {
                        mc.player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.already_here"), true);
                        return;
                    }
                    NetworkHandler.CHANNEL.sendToServer(new PacketLandSpaceStation(orbitDimensionId, station.getPosition()));
                    mc.displayGuiScreen(null);
                    return;
                }
            }
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        if (Keyboard.getEventKeyState() && Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
            return;
        }
        super.handleKeyboardInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = org.lwjgl.input.Mouse.getEventDWheel();
        if (wheel == 0) {
            return;
        }
        int mouseX = org.lwjgl.input.Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - org.lwjgl.input.Mouse.getEventY() * height / mc.displayHeight - 1;
        if (isInside(mouseX, mouseY, MENU_X, height / 2 - 43, 112, 131)) {
            int maxScroll = Math.max(0, planets.size() * 24 - 131);
            scrollAmount += wheel < 0 ? 12 : -12;
            scrollAmount = Math.max(0, Math.min(maxScroll, scrollAmount));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    private void drawSelectionMenu(int mouseX, int mouseY) {
        int menuY = menuY();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(selectedPlanet == null ? SMALL_SELECTION_MENU : SELECTION_MENU);
        drawModalRectWithCustomSizedTexture(MENU_X, menuY, 0.0F, 0.0F,
            selectedPlanet == null ? SMALL_MENU_WIDTH : MENU_WIDTH, MENU_HEIGHT,
            selectedPlanet == null ? SMALL_MENU_WIDTH : MENU_WIDTH, MENU_HEIGHT);

        if (selectedPlanet != null) {
            drawTexturedButton(BACK_BUTTON, BACK_BUTTON_HIGHLIGHTED, MENU_X + 3, menuY + 3, 12, 12, mouseX, mouseY, true);
        }

        String title = selectedPlanet == null ? I18n.format("text.ad_astra.text.catalog") : getPlanetLabel(selectedPlanet);
        drawCenteredString(fontRenderer, title, MENU_X + 50, height / 2 - 60, 0xFFFFFF);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scale = mc.gameSettings.guiScale;
        if (scale == 0) {
            scale = 1000;
        }
        int factor = 1;
        while (factor < scale && mc.displayWidth / (factor + 1) >= 320 && mc.displayHeight / (factor + 1) >= 240) {
            factor++;
        }
        GL11.glScissor(MENU_X * factor, (height - (height / 2 + 88)) * factor, 112 * factor, 131 * factor);
        drawPlanetButtons(mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (selectedPlanet != null) {
            drawRightPanel(mouseX, mouseY);
        }
    }

    private void drawPlanetButtons(int mouseX, int mouseY) {
        int listTop = height / 2 - 41;
        for (int i = 0; i < planets.size(); i++) {
            PlanetDimensionProperties planet = planets.get(i);
            int x = MENU_X + 3;
            int y = listTop + i * 24 - scrollAmount;
            boolean enabled = PlanetTravelHelper.canRocketTierReach(rocketTier, planet);
            boolean hovered = enabled && isInside(mouseX, mouseY, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
            drawTexturedButton(BUTTON, BUTTON_HIGHLIGHTED, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY, enabled);
            int color = enabled ? (hovered || planet == selectedPlanet ? 0xFFFFFF : 0xCFCFCF) : 0x555555;
            drawCenteredString(fontRenderer, getPlanetLabel(planet), x + BUTTON_WIDTH / 2, y + 6, color);
        }
    }

    private void drawRightPanel(int mouseX, int mouseY) {
        drawTexturedButton(BUTTON, BUTTON_HIGHLIGHTED, MENU_X + 107, height / 2 - 77, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY, true);
        drawCenteredString(fontRenderer, I18n.format("text.ad_astra.text.land"), MENU_X + 156, height / 2 - 71, 0xFFFFFF);
        boolean hasSpaceStation = hasSelectedSpaceStation();
        boolean canConstruct = canConstructSelectedSpaceStation();
        drawTexturedButton(PLUS_BUTTON, PLUS_BUTTON_HIGHLIGHTED, MENU_X + 107, height / 2 - 41, 12, 12, mouseX, mouseY, canConstruct);
        drawString(fontRenderer, I18n.format("text.ad_astra.text.construct_space_station"), MENU_X + 123, height / 2 - 38,
            canConstruct ? 0xDDEEFF : 0x666666);
        if (hasSpaceStation) {
            drawString(fontRenderer, I18n.format("gui.ad_astra.planets.space_station_exists"), MENU_X + 123, height / 2 - 26, 0x888888);
        } else {
            drawSpaceStationRequirements(mouseX, mouseY);
        }
        drawCenteredString(fontRenderer, I18n.format("text.ad_astra.text.space_station"), MENU_X + 156, height / 2 + 17, 0xFFFFFF);

        List<SpaceStation> stations = getSelectedSpaceStations();
        if (stations.isEmpty()) {
            drawCenteredString(fontRenderer, I18n.format("gui.ad_astra.planets.no_space_stations"), MENU_X + 156, height / 2 + 39, 0x888888);
            drawSpaceStationRequirementTooltip(mouseX, mouseY);
            return;
        }

        int stationTop = height / 2 + 29;
        for (int i = 0; i < stations.size() && i < 2; i++) {
            SpaceStation station = stations.get(i);
            int y = stationTop + i * 22;
            drawTexturedButton(BUTTON, BUTTON_HIGHLIGHTED, MENU_X + 107, y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY, true);
            drawCenteredString(fontRenderer, station.getName(), MENU_X + 156, y + 6, 0xFFFFFF);
        }
        drawSpaceStationRequirementTooltip(mouseX, mouseY);
    }

    private void drawSpaceStationRequirements(int mouseX, int mouseY) {
        SpaceStationRecipe recipe = getSelectedSpaceStationRecipe();
        if (recipe == null || mc.player == null) {
            return;
        }

        List<SpaceStationRecipe.IngredientRequirement> requirements = recipe.getRequirements();
        int startX = MENU_X + 111;
        int iconY = height / 2 - 20;
        for (int i = 0; i < requirements.size() && i < 4; i++) {
            SpaceStationRecipe.IngredientRequirement requirement = requirements.get(i);
            ItemStack displayStack = requirement.getDisplayStack();
            if (displayStack.isEmpty()) {
                continue;
            }

            int x = startX + i * 23;
            int owned = requirement.countMatching(mc.player.inventory);
            int needed = requirement.getCount();
            boolean enough = owned >= needed || mc.player.capabilities.isCreativeMode;
            drawItemStack(displayStack, x, iconY);

            String count = Math.min(owned, needed) + "/" + needed;
            int color = enough ? 0x9CFF9C : 0xFF7777;
            drawCenteredString(fontRenderer, count, x + 8, iconY + 18, color);
        }
    }

    private void drawSpaceStationRequirementTooltip(int mouseX, int mouseY) {
        SpaceStationRecipe recipe = getSelectedSpaceStationRecipe();
        if (recipe == null) {
            return;
        }
        List<SpaceStationRecipe.IngredientRequirement> requirements = recipe.getRequirements();
        int startX = MENU_X + 111;
        int iconY = height / 2 - 20;
        for (int i = 0; i < requirements.size() && i < 4; i++) {
            SpaceStationRecipe.IngredientRequirement requirement = requirements.get(i);
            ItemStack displayStack = requirement.getDisplayStack();
            if (displayStack.isEmpty()) {
                continue;
            }
            int x = startX + i * 23;
            if (isInside(mouseX, mouseY, x, iconY, 16, 16)) {
                int owned = mc.player == null ? 0 : requirement.countMatching(mc.player.inventory);
                int needed = requirement.getCount();
                List<String> tooltip = new ArrayList<>();
                tooltip.add(displayStack.getDisplayName());
                tooltip.add(I18n.format("tooltip.ad_astra.requirement", owned, needed, displayStack.getDisplayName()));
                drawHoveringText(tooltip, mouseX, mouseY);
                return;
            }
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlayIntoGUI(fontRenderer, stack, x, y, null);
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private boolean canConstructSelectedSpaceStation() {
        if (selectedPlanet == null || mc.player == null) {
            return false;
        }
        if (!PlanetTravelHelper.canRocketTierReach(rocketTier, selectedPlanet)) {
            return false;
        }
        if (hasSelectedSpaceStation()) {
            return false;
        }
        SpaceStationRecipe recipe = getSelectedSpaceStationRecipe();
        return recipe != null && recipe.canCraft(mc.player);
    }

    private boolean hasSelectedSpaceStation() {
        return !getSelectedSpaceStations().isEmpty();
    }

    private SpaceStationRecipe getSelectedSpaceStationRecipe() {
        if (selectedPlanet == null) {
            return null;
        }
        int orbitDimensionId = PlanetTravelHelper.getOrbitDimensionId(selectedPlanet.getDimensionId());
        ResourceLocation orbitLocation = PlanetTravelHelper.getOrbitDimensionLocation(orbitDimensionId);
        return orbitLocation == null ? null : RecipeRegistry.findSpaceStationRecipe(orbitLocation);
    }

    private List<SpaceStation> getSelectedSpaceStations() {
        List<SpaceStation> stations = new ArrayList<>();
        if (selectedPlanet == null || mc.player == null) {
            return stations;
        }
        IAdAstraPlayer capability = AdAstraCapabilities.getPlayer(mc.player);
        if (capability == null) {
            return stations;
        }
        int orbitDimensionId = PlanetTravelHelper.getOrbitDimensionId(selectedPlanet.getDimensionId());
        for (SpaceStation station : capability.getSpaceStations()) {
            if (station.getDimension() == orbitDimensionId) {
                stations.add(station);
            }
        }
        stations.sort(Comparator.comparing(SpaceStation::getName));
        return stations;
    }

    private boolean isPlayerAtSpaceStation(SpaceStation station) {
        return mc.player != null
            && station != null
            && mc.player.dimension == station.getDimension()
            && Math.abs(mc.player.posX - (station.getPosition().getX() + 0.5D)) <= 40.0D
            && Math.abs(mc.player.posZ - (station.getPosition().getZ() + 0.5D)) <= 40.0D;
    }

    private void drawTexturedButton(ResourceLocation normal, ResourceLocation highlighted, int x, int y, int width, int height,
                                    int mouseX, int mouseY, boolean enabled) {
        boolean hovered = enabled && isInside(mouseX, mouseY, x, y, width, height);
        mc.getTextureManager().bindTexture(hovered ? highlighted : normal);
        GlStateManager.color(enabled ? 1.0F : 0.35F, enabled ? 1.0F : 0.35F, enabled ? 1.0F : 0.35F, 1.0F);
        drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawSpaceBackground() {
        drawRect(0, 0, width, height, 0xFF000419);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = -height; i <= width; i += 24) {
            buffer.pos(i, 0, 0).color(15, 37, 89, 255).endVertex();
            buffer.pos(i + height, height, 0).color(15, 37, 89, 255).endVertex();
        }
        for (int i = width + height; i >= 0; i -= 24) {
            buffer.pos(i, 0, 0).color(15, 37, 89, 255).endVertex();
            buffer.pos(i - height, height, 0).color(15, 37, 89, 255).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawSolarSystem(float partialTicks) {
        drawOrbit(30, 0xFF24327B);
        drawOrbit(60, 0xFF24327B);
        drawOrbit(90, 0xFF24327B);
        drawOrbit(120, 0xFF24327B);

        mc.getTextureManager().bindTexture(SUN);
        drawScaledCustomSizeModalRect(width / 2 - 8, height / 2 - 8, 0.0F, 0.0F, 8, 8, 16, 16, 8.0F, 8.0F);
        drawPlanetIcon("mercury", 30, 0.8F, partialTicks, 16);
        drawPlanetIcon("venus", 60, 0.55F, partialTicks, 16);
        drawPlanetIcon("moon", 90, 0.35F, partialTicks, 8);
        drawPlanetIcon("mars", 120, 0.22F, partialTicks, 16);
    }

    private void drawOrbit(int radius, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 96; i++) {
            double angle = Math.PI * 2.0D * i / 96.0D;
            int x = (int) (width / 2.0D + Math.cos(angle) * radius);
            int y = (int) (height / 2.0D + Math.sin(angle) * radius);
            buffer.pos(x, y, 0).color((color >> 16) & 255, (color >> 8) & 255, color & 255, 255).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawPlanetIcon(String name, int radius, float speed, float partialTicks, int textureSize) {
        double angle = (System.currentTimeMillis() / 1000.0D * speed + partialTicks * 0.01D) % (Math.PI * 2.0D);
        int x = (int) (width / 2.0D + Math.cos(angle) * radius) - 6;
        int y = (int) (height / 2.0D + Math.sin(angle) * radius) - 6;
        mc.getTextureManager().bindTexture(new ResourceLocation(Reference.MOD_ID, "textures/environment/" + name + ".png"));
        drawScaledCustomSizeModalRect(x, y, 0.0F, 0.0F, textureSize, textureSize, 12, 12, textureSize, textureSize);
    }

    private int menuY() {
        return height / 2 - 88;
    }

    private boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private String getPlanetLabel(PlanetDimensionProperties planet) {
        if (planet.getDimensionId() == 0) {
            return I18n.format("planet.minecraft.overworld");
        }
        if (planet.getDimensionId() == PlanetTravelHelper.NETHER_DIMENSION_ID) {
            return I18n.format("planet.minecraft.nether");
        }
        if (planet.getDimensionId() == PlanetTravelHelper.END_DIMENSION_ID) {
            return I18n.format("planet.minecraft.the_end");
        }
        return I18n.format("planet.ad_astra." + planet.getName().toLowerCase(Locale.ROOT));
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/planets/" + name + ".png");
    }
}
