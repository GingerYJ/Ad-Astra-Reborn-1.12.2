package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.api.client.events.AdAstraClientEvents;
import earth.terrarium.adastra.common.capability.SpaceStation;
import earth.terrarium.adastra.common.config.ExternalDimensionConfig;
import earth.terrarium.adastra.common.menus.PlanetsMenu;
import earth.terrarium.adastra.common.menus.base.PlanetsMenuProvider;
import earth.terrarium.adastra.common.recipe.SpaceStationRecipe;
import earth.terrarium.adastra.common.util.PlanetTravelHelper;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import earth.terrarium.adastra.common.world.custom.CustomPlanetRegistry;
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
    private static final ResourceLocation SOLAR_SYSTEM = new ResourceLocation(Reference.MOD_ID, "solar_system");

    private static final int MENU_X = 7;
    private static final int MENU_WIDTH = 209;
    private static final int MENU_HEIGHT = 177;
    private static final int SMALL_MENU_WIDTH = 105;
    private static final int BUTTON_WIDTH = 99;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACE_STATION_LIST_HEIGHT = 45;
    private static final int SPACE_STATION_ROW_SPACING = 22;

    private final int rocketTier;
    private final int rocketEntityId;
    private final List<PlanetDimensionProperties> planets = new ArrayList<>();
    private final List<PlanetHitbox> planetHitboxes = new ArrayList<>();
    private PlanetsMenu menu;
    private PlanetDimensionProperties selectedPlanet;
    private int scrollAmount;
    private int spaceStationScrollAmount;
    private float mapZoom = 1.0F;
    private int mapOffsetX;
    private int mapOffsetY;
    private boolean draggingMap;
    private int lastDragMouseX;
    private int lastDragMouseY;

    private static boolean showPlanetNames = true;

    private static final int[] ORBIT_RADII = new int[]{95, 155, 215, 275, 340, 410, 485, 570, 650, 735, 825, 920, 1020, 1125, 1235, 1350, 1470, 1595, 1725, 1860, 2000, 2145, 2295, 2450, 2610, 2775, 2945, 3120, 3300, 3485, 3675, 3870};
    private static final float ORBIT_SCALE_DENOMINATOR = 1800.0F;
    private static final int RIGHT_PANEL_X = MENU_X + 112;
    private static final int RIGHT_PANEL_WIDTH = 92;

    private static class LayoutSpec {
        final int orbit;
        final double angle;
        final String parent;
        final int satelliteRadius;
        final double satelliteAngle;

        LayoutSpec(int orbit, double angle) {
            this.orbit = orbit;
            this.angle = angle;
            this.parent = null;
            this.satelliteRadius = 0;
            this.satelliteAngle = 0.0D;
        }

        LayoutSpec(String parent, int satelliteRadius, double satelliteAngle) {
            this.orbit = -1;
            this.angle = 0.0D;
            this.parent = parent;
            this.satelliteRadius = satelliteRadius;
            this.satelliteAngle = satelliteAngle;
        }
    }

    private static class PlanetRenderNode {
        final String key;
        final String label;
        final PlanetDimensionProperties planet;
        final double x;
        final double y;
        final int radius;
        final boolean selectable;
        final boolean selected;

        PlanetRenderNode(String key, String label, PlanetDimensionProperties planet, double x, double y, int radius,
                         boolean selectable, boolean selected) {
            this.key = key;
            this.label = label;
            this.planet = planet;
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.selectable = selectable;
            this.selected = selected;
        }
    }

    private static class PlanetHitbox {
        final PlanetDimensionProperties planet;
        final double x;
        final double y;
        final int radius;

        PlanetHitbox(PlanetDimensionProperties planet, double x, double y, int radius) {
            this.planet = planet;
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        boolean contains(int mouseX, int mouseY) {
            double dx = mouseX - x;
            double dy = mouseY - y;
            return dx * dx + dy * dy <= radius * radius;
        }
    }

    private static class OrbitRing {
        final double x;
        final double y;
        final double radius;
        final int color;

        OrbitRing(double x, double y, double radius, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }
    }

    public PlanetSelectionGui(int rocketTier, int rocketEntityId) {
        this.rocketTier = rocketTier;
        this.rocketEntityId = rocketEntityId;
    }

    @Override
    public void initGui() {
        if (mc.player == null) {
            return;
        }
        menu = PlanetsMenuProvider.createClientMenu(mc.player, rocketTier, rocketEntityId);
        planets.clear();
        planets.addAll(menu.getAvailablePlanets());
        selectedPlanet = menu.getCurrentOrbitPlanet();
        if (selectedPlanet != null && !menu.canReach(selectedPlanet)) {
            selectedPlanet = null;
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
        if (mouseButton != 0 || menu == null) {
            return;
        }

        draggingMap = false;

        if (isInsideNameToggle(mouseX, mouseY)) {
            showPlanetNames = !showPlanetNames;
            return;
        }

        int menuY = menuY();
        if (selectedPlanet != null && isInside(mouseX, mouseY, MENU_X + 3, menuY + 3, 12, 12)) {
            selectedPlanet = null;
            scrollAmount = 0;
            spaceStationScrollAmount = 0;
            return;
        }

        for (PlanetHitbox hitbox : planetHitboxes) {
            if (hitbox.contains(mouseX, mouseY) && menu.canReach(hitbox.planet)) {
                selectedPlanet = hitbox.planet;
                spaceStationScrollAmount = 0;
                return;
            }
        }

        int listTop = height / 2 - 41;
        for (int i = 0; i < planets.size(); i++) {
            PlanetDimensionProperties planet = planets.get(i);
            int buttonY = listTop + i * 24 - scrollAmount;
            if (buttonY <= height / 2 - 63 || buttonY >= height / 2 + 88) {
                continue;
            }
            if (isInside(mouseX, mouseY, MENU_X + 3, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
                && menu.canReach(planet)) {
                selectedPlanet = planet;
                spaceStationScrollAmount = 0;
                return;
            }
        }

        if (selectedPlanet != null && isInside(mouseX, mouseY, RIGHT_PANEL_X, menuY + 10, RIGHT_PANEL_WIDTH, BUTTON_HEIGHT)) {
            menu.landOnPlanet(selectedPlanet);
            mc.displayGuiScreen(null);
        }
        if (selectedPlanet != null && isInside(mouseX, mouseY, RIGHT_PANEL_X, height / 2 - 41, 12, 12)
            && canConstructSelectedSpaceStation()) {
            menu.constructSpaceStation(selectedPlanet);
            mc.displayGuiScreen(null);
            return;
        }
        if (selectedPlanet != null) {
            List<SpaceStation> stations = getSelectedSpaceStations();
            int stationTop = height / 2 + 43;
            for (int i = 0; i < stations.size(); i++) {
                int y = stationTop + i * SPACE_STATION_ROW_SPACING - spaceStationScrollAmount;
                if (y <= stationTop - BUTTON_HEIGHT || y >= stationTop + SPACE_STATION_LIST_HEIGHT) {
                    continue;
                }
                if (isInside(mouseX, mouseY, RIGHT_PANEL_X, y, RIGHT_PANEL_WIDTH, BUTTON_HEIGHT)) {
                    SpaceStation station = stations.get(i);
                    if (isPlayerAtSpaceStation(station)) {
                        mc.player.sendStatusMessage(new TextComponentTranslation("message.ad_astra.space_station.already_here"), true);
                        return;
                    }
                    menu.landOnSpaceStation(selectedPlanet, station.getPosition());
                    mc.displayGuiScreen(null);
                    return;
                }
            }
        }

        if (!isInsideSelectionPanel(mouseX, mouseY)) {
            draggingMap = true;
            lastDragMouseX = mouseX;
            lastDragMouseY = mouseY;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (draggingMap && clickedMouseButton == 0) {
            mapOffsetX += mouseX - lastDragMouseX;
            mapOffsetY += mouseY - lastDragMouseY;
            lastDragMouseX = mouseX;
            lastDragMouseY = mouseY;
            clampMapOffset();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            draggingMap = false;
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
        if (menu == null) {
            return;
        }
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
        } else if (selectedPlanet != null && isInside(mouseX, mouseY, RIGHT_PANEL_X, height / 2 + 43, RIGHT_PANEL_WIDTH, SPACE_STATION_LIST_HEIGHT)) {
            List<SpaceStation> stations = getSelectedSpaceStations();
            int maxScroll = Math.max(0, stations.size() * SPACE_STATION_ROW_SPACING - SPACE_STATION_LIST_HEIGHT);
            spaceStationScrollAmount += wheel < 0 ? 12 : -12;
            spaceStationScrollAmount = Math.max(0, Math.min(maxScroll, spaceStationScrollAmount));
        } else {
            float previousZoom = mapZoom;
            mapZoom *= wheel > 0 ? 1.20F : 0.83F;
            if (mapZoom < 0.35F) {
                mapZoom = 0.35F;
            }
            if (mapZoom > 3.0F) {
                mapZoom = 3.0F;
            }
            if (previousZoom != 0.0F && mapZoom != previousZoom) {
                mapOffsetX = Math.round(mapOffsetX * (mapZoom / previousZoom));
                mapOffsetY = Math.round(mapOffsetY * (mapZoom / previousZoom));
                clampMapOffset();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    private void drawSelectionMenu(int mouseX, int mouseY) {
        if (menu == null) {
            return;
        }
        int menuY = menuY();
        int panelWidth = selectedPlanet == null ? SMALL_MENU_WIDTH : MENU_WIDTH;
        drawRect(MENU_X, menuY, MENU_X + panelWidth, menuY + MENU_HEIGHT, 0xAA000000);
        drawBorder(MENU_X, menuY, panelWidth, MENU_HEIGHT, 0xFFEFEFE6);
        drawRect(MENU_X + 1, menuY + 38, MENU_X + panelWidth - 1, menuY + 40, 0xFFEFEFE6);

        if (selectedPlanet != null) {
            drawTexturedButton(BACK_BUTTON, BACK_BUTTON_HIGHLIGHTED, MENU_X + 3, menuY + 3, 12, 12, mouseX, mouseY, true);
            drawCenteredString(fontRenderer, "<", MENU_X + 9, menuY + 5, 0xFFFFFFFF);
        }

        String title = selectedPlanet == null ? I18n.format("text.ad_astra.text.catalog") : getPlanetLabel(selectedPlanet);
        drawCenteredString(fontRenderer, title, MENU_X + SMALL_MENU_WIDTH / 2, menuY + 15, 0xFFFFFF);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int factor = getGuiScaleFactor();
        GL11.glScissor(MENU_X * factor, (height - (height / 2 + 88)) * factor, 112 * factor, 131 * factor);
        drawPlanetButtons(mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        int maxScroll = Math.max(0, planets.size() * 24 - 131);
        if (maxScroll > 0) {
            int trackX = MENU_X + 105;
            int trackY = height / 2 - 41;
            int trackHeight = 129;
            int thumbHeight = Math.max(18, trackHeight * 131 / Math.max(131, planets.size() * 24));
            int thumbY = trackY + (trackHeight - thumbHeight) * scrollAmount / maxScroll;
            drawRect(trackX, trackY, trackX + 3, trackY + trackHeight, 0x55FFFFFF);
            drawRect(trackX, thumbY, trackX + 3, thumbY + thumbHeight, 0xFFFFFFFF);
        }

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
            boolean enabled = menu.canReach(planet);
            boolean hovered = enabled && isInside(mouseX, mouseY, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
            drawTexturedButton(BUTTON, BUTTON_HIGHLIGHTED, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY, enabled);
            int color = enabled ? (hovered || planet == selectedPlanet ? 0xFFFFFF : 0x777777) : 0x555555;
            drawCenteredString(fontRenderer, getPlanetLabel(planet), x + BUTTON_WIDTH / 2, y + 6, color);
        }
    }

    private void drawRightPanel(int mouseX, int mouseY) {
        int menuY = menuY();
        drawTexturedButton(BUTTON, BUTTON_HIGHLIGHTED, RIGHT_PANEL_X, menuY + 10, RIGHT_PANEL_WIDTH, BUTTON_HEIGHT, mouseX, mouseY, true);
        drawCenteredString(fontRenderer, I18n.format("text.ad_astra.text.land"), RIGHT_PANEL_X + RIGHT_PANEL_WIDTH / 2, menuY + 16, 0xFFFFFF);
        boolean hasSpaceStation = hasSelectedSpaceStation();
        boolean canConstruct = canConstructSelectedSpaceStation();
        drawTexturedButton(PLUS_BUTTON, PLUS_BUTTON_HIGHLIGHTED, RIGHT_PANEL_X, height / 2 - 41, 12, 12, mouseX, mouseY, canConstruct);
        drawString(fontRenderer, I18n.format("text.ad_astra.text.construct_space_station"), RIGHT_PANEL_X + 16, height / 2 - 38,
            canConstruct ? 0xDDEEFF : 0x666666);
        if (hasSpaceStation) {
            drawString(fontRenderer, I18n.format("gui.ad_astra.planets.space_station_exists"), RIGHT_PANEL_X + 16, height / 2 - 26, 0x888888);
        } else {
            drawSpaceStationRequirements(mouseX, mouseY);
        }
        drawCenteredString(fontRenderer, I18n.format("text.ad_astra.text.space_station"), RIGHT_PANEL_X + RIGHT_PANEL_WIDTH / 2, height / 2 + 31, 0xFFFFFF);

        List<SpaceStation> stations = getSelectedSpaceStations();
        if (stations.isEmpty()) {
            drawCenteredString(fontRenderer, I18n.format("gui.ad_astra.planets.no_space_stations"), RIGHT_PANEL_X + RIGHT_PANEL_WIDTH / 2, height / 2 + 55, 0x888888);
            drawSpaceStationRequirementTooltip(mouseX, mouseY);
            return;
        }

        int stationTop = height / 2 + 43;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int factor = getGuiScaleFactor();
        GL11.glScissor(RIGHT_PANEL_X * factor, (height - (stationTop + SPACE_STATION_LIST_HEIGHT)) * factor,
            RIGHT_PANEL_WIDTH * factor, SPACE_STATION_LIST_HEIGHT * factor);
        for (int i = 0; i < stations.size(); i++) {
            SpaceStation station = stations.get(i);
            int y = stationTop + i * SPACE_STATION_ROW_SPACING - spaceStationScrollAmount;
            if (y <= stationTop - BUTTON_HEIGHT || y >= stationTop + SPACE_STATION_LIST_HEIGHT) {
                continue;
            }
            drawTexturedButton(BUTTON, BUTTON_HIGHLIGHTED, RIGHT_PANEL_X, y, RIGHT_PANEL_WIDTH, BUTTON_HEIGHT, mouseX, mouseY, true);
            drawCenteredString(fontRenderer, station.getName(), RIGHT_PANEL_X + RIGHT_PANEL_WIDTH / 2, y + 6, 0xFFFFFF);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        drawSpaceStationRequirementTooltip(mouseX, mouseY);
    }

    private void drawSpaceStationRequirements(int mouseX, int mouseY) {
        SpaceStationRecipe recipe = getSelectedSpaceStationRecipe();
        if (recipe == null || mc.player == null) {
            return;
        }

        List<SpaceStationRecipe.IngredientRequirement> requirements = recipe.getRequirements();
        for (int i = 0; i < requirements.size() && i < 4; i++) {
            SpaceStationRecipe.IngredientRequirement requirement = requirements.get(i);
            ItemStack displayStack = requirement.getDisplayStack();
            if (displayStack.isEmpty()) {
                continue;
            }

            int x = requirementSlotX(i);
            int y = requirementSlotY(i);
            int owned = menu.getOwnedIngredientCount(requirement);
            int needed = requirement.getCount();
            boolean enough = owned >= needed || mc.player.capabilities.isCreativeMode;
            drawItemStack(displayStack, x, y);

            String count = Math.min(owned, needed) + "/" + needed;
            int color = enough ? 0x9CFF9C : 0xFF7777;
            drawCenteredString(fontRenderer, count, x + 8, y + 17, color);
        }
    }

    private void drawSpaceStationRequirementTooltip(int mouseX, int mouseY) {
        SpaceStationRecipe recipe = getSelectedSpaceStationRecipe();
        if (recipe == null) {
            return;
        }
        List<SpaceStationRecipe.IngredientRequirement> requirements = recipe.getRequirements();
        for (int i = 0; i < requirements.size() && i < 4; i++) {
            SpaceStationRecipe.IngredientRequirement requirement = requirements.get(i);
            ItemStack displayStack = requirement.getDisplayStack();
            if (displayStack.isEmpty()) {
                continue;
            }
            int x = requirementSlotX(i);
            int y = requirementSlotY(i);
            if (isInside(mouseX, mouseY, x, y, 16, 16)) {
                int owned = menu == null ? 0 : menu.getOwnedIngredientCount(requirement);
                int needed = requirement.getCount();
                List<String> tooltip = new ArrayList<>();
                tooltip.add(displayStack.getDisplayName());
                tooltip.add(I18n.format("tooltip.ad_astra.requirement", owned, needed, displayStack.getDisplayName()));
                drawHoveringText(tooltip, mouseX, mouseY);
                return;
            }
        }
    }

    private int requirementSlotX(int index) {
        return RIGHT_PANEL_X + 6 + (index % 2) * 43;
    }

    private int requirementSlotY(int index) {
        return height / 2 - 22 + (index / 2) * 25;
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
        if (selectedPlanet == null || menu == null) {
            return false;
        }
        if (!menu.canReach(selectedPlanet)) {
            return false;
        }
        return menu.canConstructSpaceStation(selectedPlanet);
    }

    private boolean hasSelectedSpaceStation() {
        return selectedPlanet != null && menu.hasSpaceStation(selectedPlanet);
    }

    private SpaceStationRecipe getSelectedSpaceStationRecipe() {
        return selectedPlanet == null ? null : menu.getSpaceStationRecipe(selectedPlanet);
    }

    private List<SpaceStation> getSelectedSpaceStations() {
        return selectedPlanet == null ? new ArrayList<>() : menu.getSpaceStations(selectedPlanet);
    }

    private boolean isPlayerAtSpaceStation(SpaceStation station) {
        return menu != null && menu.isPlayerAtSpaceStation(station);
    }

    private void drawBorder(int x, int y, int width, int height, int color) {
        drawRect(x, y, x + width, y + 2, color);
        drawRect(x, y + height - 2, x + width, y + height, color);
        drawRect(x, y, x + 2, y + height, color);
        drawRect(x + width - 2, y, x + width, y + height, color);
    }

    private void drawTexturedButton(ResourceLocation normal, ResourceLocation highlighted, int x, int y, int width, int height,
                                    int mouseX, int mouseY, boolean enabled) {
        boolean hovered = enabled && isInside(mouseX, mouseY, x, y, width, height);
        int fill = enabled ? (hovered ? 0xFFF1F1F1 : 0xFFE0E0E0) : 0xFF353535;
        int border = enabled ? 0xFFFFFFFF : 0xFF666666;
        drawRect(x, y, x + width, y + height, fill);
        drawBorder(x, y, width, height, border);
    }

    private void drawSpaceBackground() {
        drawGradientRect(0, 0, width, height, 0xFF020819, 0xFF00020A);
        drawGalaxyBand();
        drawFilledCircle(width / 4, height / 3, Math.max(width, height) / 3, 0x120B4E9A, 64);
        drawFilledCircle(width * 2 / 3, height / 2, Math.max(width, height) / 4, 0x102A1A6F, 64);
    }

    private void drawCpuStars() {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 210; i++) {
            int x = pseudoRandom(i * 31 + 7, width + 40) - 20;
            int y = pseudoRandom(i * 47 + 19, height + 40) - 20;
            boolean brightStar = pseudoRandom(i * 17 + 5, 100) < 6;
            int pulse = (int) ((Math.sin((time / 560.0D) + i * 0.73D) + 1.0D) * 28.0D);
            int alpha = 42 + pseudoRandom(i * 13 + 11, 70) + pulse;
            if (alpha > 165) alpha = 165;
            int color = alpha << 24 | 0xE8F2FF;
            drawRect(x, y, x + 1, y + 1, color);
            if (brightStar) {
                int glowColor = Math.min(70, alpha / 2) << 24 | 0xAFCBFF;
                drawRect(x - 1, y, x + 2, y + 1, glowColor);
                drawRect(x, y - 1, x + 1, y + 2, glowColor);
                drawRect(x, y, x + 1, y + 1, color);
            }
        }
    }

    private int pseudoRandom(int seed, int max) {
        int value = seed * 1103515245 + 12345;
        value ^= value >>> 16;
        value &= 0x7fffffff;
        return max <= 0 ? 0 : value % max;
    }

    private void drawSolarSystem(float partialTicks) {
        planetHitboxes.clear();
        int mapLeft = MENU_X + SMALL_MENU_WIDTH + 34;
        int mapRight = width - 20;
        int mapWidth = Math.max(220, mapRight - mapLeft);
        double centerX = mapLeft + mapWidth / 2.0D + mapOffsetX;
        double centerY = height / 2.0D + mapOffsetY;
        int availableRadius = Math.max(80, Math.min(mapWidth / 2 - 18, height / 2 - 18));
        float baseScale = Math.max(0.18F, availableRadius / ORBIT_SCALE_DENOMINATOR);
        float scale = Math.max(0.08F, Math.min(1.80F, baseScale * mapZoom));

        List<PlanetRenderNode> nodes = buildPlanetNodes(centerX, centerY, scale);
        List<OrbitRing> rings = buildOrbitRings(centerX, centerY, scale, nodes);
        int sunRadius = Math.max(4, Math.round(25 * scale));
        drawCpuStars();
        for (OrbitRing ring : rings) {
            drawOrbitGlow(ring.x, ring.y, ring.radius, ring.color);
        }
        drawSun(centerX, centerY, sunRadius);
        for (PlanetRenderNode node : nodes) {
            drawPlanetNode(node);
        }

        for (PlanetRenderNode node : nodes) {
            if (node.selectable && node.planet != null) {
                planetHitboxes.add(new PlanetHitbox(node.planet, node.x, node.y, node.radius + 8));
            } else {
                PlanetDimensionProperties target = getAnchorClickTarget(node.key);
                if (target != null) {
                    planetHitboxes.add(new PlanetHitbox(target, node.x, node.y, node.radius + 8));
                }
            }
        }

        drawString(fontRenderer, "\u7f29\u653e " + Math.round(mapZoom * 100.0F) + "%", width - 78, 10, 0xFFCDD8FF);
        drawString(fontRenderer, "\u6eda\u8f6e\u7f29\u653e / \u5de6\u952e\u62d6\u52a8", width - 132, 22, 0x99CDD8FF);
        drawNameToggleButton();

        AdAstraClientEvents.RenderSolarSystemEvent.fire(
            new AdAstraClientEvents.RenderContext(mc, this, partialTicks),
            SOLAR_SYSTEM,
            width,
            height);
    }

    private List<OrbitRing> buildOrbitRings(double centerX, double centerY, float scale, List<PlanetRenderNode> nodes) {
        List<OrbitRing> rings = new ArrayList<>();
        List<Integer> primaryOrbitIndexes = new ArrayList<>();
        for (PlanetRenderNode node : nodes) {
            LayoutSpec spec = getRenderLayoutSpec(node.planet, node.key);
            if (spec == null || spec.parent != null) {
                continue;
            }
            int orbitIndex = Math.max(0, spec.orbit);
            if (!primaryOrbitIndexes.contains(orbitIndex)) {
                primaryOrbitIndexes.add(orbitIndex);
                double radius = Math.max(8.0D, getOrbitRadius(orbitIndex) * scale);
                rings.add(new OrbitRing(centerX, centerY, radius, (orbitIndex & 1) == 0 ? 0xCCF5FAFF : 0x99F5FAFF));
            }
        }
        for (PlanetRenderNode node : nodes) {
            LayoutSpec spec = getLayoutSpec(node.planet, node.key);
            if (spec != null && spec.parent != null) {
                PlanetRenderNode parent = findNode(nodes, spec.parent);
                if (parent != null) {
                    rings.add(new OrbitRing(parent.x, parent.y, Math.max(4.0D, spec.satelliteRadius * scale), 0x88F5FAFF));
                }
            }
        }
        return rings;
    }

    private List<PlanetRenderNode> buildPlanetNodes(double centerX, double centerY, float scale) {
        List<PlanetRenderNode> nodes = new ArrayList<>();
        for (PlanetDimensionProperties planet : planets) {
            String key = getPlanetKey(planet);
            LayoutSpec spec = getLayoutSpec(planet, key);
            if (spec != null && spec.parent != null) {
                continue;
            }
            nodes.add(createPrimaryNode(planet, key, centerX, centerY, scale, true));
        }

        for (PlanetDimensionProperties planet : planets) {
            String key = getPlanetKey(planet);
            LayoutSpec spec = getLayoutSpec(planet, key);
            if (spec == null || spec.parent == null) {
                continue;
            }
            PlanetRenderNode parent = findNode(nodes, spec.parent);
            if (parent == null) {
                PlanetDimensionProperties parentPlanet = findPlanetByKey(spec.parent);
                parent = createPrimaryNode(parentPlanet, spec.parent, centerX, centerY, scale, parentPlanet != null);
                nodes.add(parent);
            }
            double satelliteRadius = Math.max(4.0D, spec.satelliteRadius * scale);
            double angle = Math.toRadians(spec.satelliteAngle + orbitTimeDegrees(key, true, 0));
            double x = parent.x + Math.cos(angle) * satelliteRadius;
            double y = parent.y + Math.sin(angle) * satelliteRadius;
            int radius = getPlanetRenderRadius(key, scale, false);
            nodes.add(new PlanetRenderNode(key, getPlanetLabel(planet), planet, x, y, radius, true, isSelectedPlanet(planet)));
        }

        return nodes;
    }

    private PlanetRenderNode createPrimaryNode(PlanetDimensionProperties planet, String key, double centerX, double centerY, float scale, boolean selectable) {
        LayoutSpec spec = getRenderLayoutSpec(planet, key);
        int orbitIndex = Math.max(0, spec.orbit);
        double orbitRadius = Math.max(18.0D, getOrbitRadius(orbitIndex) * scale);
        double angle = Math.toRadians(spec.angle + orbitTimeDegrees(key, false, orbitIndex));
        double x = centerX + Math.cos(angle) * orbitRadius;
        double y = centerY + Math.sin(angle) * orbitRadius;
        int radius = getPlanetRenderRadius(key, scale, true);
        boolean selected = planet != null && isSelectedPlanet(planet);
        String label = planet == null ? getAnchorLabel(key) : getPlanetLabel(planet);
        return new PlanetRenderNode(key, label, planet, x, y, radius, selectable && planet != null, selected);
    }

    private double orbitTimeDegrees(String key, boolean satellite, int orbitIndex) {
        long millis = System.currentTimeMillis() % 3600000L;
        double seconds = millis / 1000.0D;
        int hash = Math.abs(key.hashCode());
        if (satellite) {
            return seconds * (2.4D + (hash % 6) * 0.45D);
        }
        double speed = Math.max(0.10D, 0.82D - orbitIndex * 0.026D);
        return seconds * (speed + (hash % 5) * 0.012D);
    }

    private PlanetRenderNode findNode(List<PlanetRenderNode> nodes, String key) {
        for (PlanetRenderNode node : nodes) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    private PlanetDimensionProperties getAnchorClickTarget(String key) {
        for (PlanetDimensionProperties planet : planets) {
            String planetKey = getPlanetKey(planet);
            LayoutSpec spec = getLayoutSpec(planet, planetKey);
            if (spec != null && key.equals(spec.parent) && menu.canReach(planet)) {
                return planet;
            }
        }
        return null;
    }

    private PlanetDimensionProperties findPlanetByKey(String key) {
        if ("earth".equals(key)) {
            return PlanetTravelHelper.EARTH_PROPERTIES;
        }
        for (PlanetDimensionProperties planet : planets) {
            if (key.equals(getPlanetKey(planet))) {
                return planet;
            }
        }
        for (PlanetDimensionProperties planet : PlanetTravelHelper.getPlanets()) {
            if (key.equals(getPlanetKey(planet))) {
                return planet;
            }
        }
        return null;
    }

    private boolean isSelectedPlanet(PlanetDimensionProperties planet) {
        return selectedPlanet != null && planet != null && selectedPlanet.getDimensionId() == planet.getDimensionId();
    }

    private String getPlanetKey(PlanetDimensionProperties planet) {
        if (planet == null || planet.getName() == null) {
            return "unknown";
        }
        if (ExternalDimensionConfig.isExternalDimension(planet.getDimensionId())) {
            return "external_" + planet.getDimensionId();
        }
        String name = planet.getName().toLowerCase(Locale.ROOT);
        int colon = name.indexOf(':');
        return colon >= 0 ? name.substring(colon + 1) : name;
    }

    private LayoutSpec getLayoutSpec(PlanetDimensionProperties planet, String key) {
        if (planet != null) {
            ExternalDimensionConfig.ExternalDimensionEntry external =
                ExternalDimensionConfig.getEntry(planet.getDimensionId());
            if (external != null) {
                int orbit = getExternalOrbitStart() + external.getOrder();
                double angle = normalizeDegrees(35.0D + external.getOrder() * 137.508D);
                return new LayoutSpec(orbit, angle);
            }
        }
        return getLayoutSpec(key);
    }

    private LayoutSpec getLayoutSpec(String key) {
        if ("mercury".equals(key)) return new LayoutSpec(0, 150.0D);
        if ("venus".equals(key)) return new LayoutSpec(1, 132.0D);
        if ("earth".equals(key)) return new LayoutSpec(2, 18.0D);
        if ("moon".equals(key)) return new LayoutSpec("earth", 52, -26.0D);
        if ("mars".equals(key)) return new LayoutSpec(3, -34.0D);
        if ("glacio".equals(key)) return new LayoutSpec(5, 105.0D);
        if ("mineral_world".equals(key)) return new LayoutSpec(getSpecialOuterOrbit(key), 50.0D);
        if (isNetherKey(key)) return new LayoutSpec(getSpecialOuterOrbit(key), -96.0D);
        if (isEndKey(key)) return new LayoutSpec(getSpecialOuterOrbit(key), -52.0D);
        return null;
    }

    private LayoutSpec getRenderLayoutSpec(PlanetDimensionProperties planet, String key) {
        LayoutSpec spec = getLayoutSpec(planet, key);
        return spec == null ? getFallbackLayout(key) : spec;
    }

    private LayoutSpec getRenderLayoutSpec(String key) {
        LayoutSpec spec = getLayoutSpec(key);
        return spec == null ? getFallbackLayout(key) : spec;
    }

    private LayoutSpec getFallbackLayout(String key) {
        int hash = key.hashCode() & 0x7FFFFFFF;
        int outerStart = getSpecialOuterOrbit("the_end") + 1;
        int orbit = outerStart + getUnknownOuterIndex(key);
        double angle = (hash % 360);
        return new LayoutSpec(orbit, angle);
    }

    private int getExternalOrbitStart() {
        int outermostOrbit = -1;
        for (PlanetDimensionProperties planet : planets) {
            if (ExternalDimensionConfig.isExternalDimension(planet.getDimensionId())) {
                continue;
            }
            String key = getPlanetKey(planet);
            LayoutSpec spec = getRenderLayoutSpec(key);
            if (spec.parent == null) {
                outermostOrbit = Math.max(outermostOrbit, spec.orbit);
            }
        }
        return outermostOrbit + 1;
    }

    private int getOrbitRadius(int orbitIndex) {
        if (orbitIndex < ORBIT_RADII.length) {
            return ORBIT_RADII[Math.max(0, orbitIndex)];
        }
        int lastIndex = ORBIT_RADII.length - 1;
        int spacing = ORBIT_RADII[lastIndex] - ORBIT_RADII[lastIndex - 1];
        return ORBIT_RADII[lastIndex] + (orbitIndex - lastIndex) * spacing;
    }

    private double normalizeDegrees(double angle) {
        double normalized = angle % 360.0D;
        return normalized < 0.0D ? normalized + 360.0D : normalized;
    }

    private int getSpecialOuterOrbit(String key) {
        int base = Math.max(0, ORBIT_RADII.length - 8);
        if ("mineral_world".equals(key)) return base;
        if (isNetherKey(key)) return Math.min(ORBIT_RADII.length - 1, base + 1);
        if (isEndKey(key)) return Math.min(ORBIT_RADII.length - 1, base + 2);
        return base;
    }

    private boolean isNetherKey(String key) {
        return "nether".equals(key) || "the_nether".equals(key);
    }

    private boolean isEndKey(String key) {
        return "the_end".equals(key) || "end".equals(key);
    }

    private int getUnknownOuterIndex(String key) {
        List<String> unknownKeys = new ArrayList<>();
        for (PlanetDimensionProperties planet : planets) {
            if (ExternalDimensionConfig.isExternalDimension(planet.getDimensionId())) {
                continue;
            }
            String candidate = getPlanetKey(planet);
            if (candidate.equals(key) || isOuterFallbackKey(candidate)) {
                if (!unknownKeys.contains(candidate)) {
                    unknownKeys.add(candidate);
                }
            }
        }
        unknownKeys.sort(String::compareTo);
        int index = unknownKeys.indexOf(key);
        return Math.max(0, index);
    }

    private boolean isOuterFallbackKey(String key) {
        return getLayoutSpec(key) == null;
    }

    private String getAnchorLabel(String key) {
        if ("earth".equals(key)) return I18n.format("planet.minecraft.overworld");
        PlanetDimensionProperties planet = findPlanetByKey(key);
        return planet == null ? formatPlanetName(key) : getPlanetLabel(planet);
    }

    private int getPlanetRenderRadius(String key, float scale, boolean primary) {
        int base = primary ? 16 : 12;
        if ("sun".equals(key)) base = 25;
        if ("earth".equals(key) || "mars".equals(key) || "venus".equals(key)) base = 18;
        if ("moon".equals(key)) base = 11;
        return Math.max(primary ? 3 : 2, Math.round(base * Math.max(0.20F, scale)));
    }

    private void drawSun(double centerX, double centerY, int radius) {
        drawFilledCircle(centerX, centerY, radius + 22, 0x22FFC83A, 48);
        drawFilledCircle(centerX, centerY, radius + 12, 0x44FFB72F, 48);
        drawFilledCircle(centerX, centerY, radius, 0xFFFFB72F, 48);
        drawFilledCircle(centerX - radius / 3.0D, centerY - radius / 3.0D, Math.max(3, radius / 2), 0xFFFFFFD8, 32);
        drawFilledCircle(centerX + radius / 3.0D, centerY + radius / 5.0D, Math.max(2, radius / 5), 0x66B85B00, 24);
        drawCircleOutline(centerX, centerY, radius + 2, 0xCCFFF6A0, 56);
    }

    private void drawPlanetNode(PlanetRenderNode node) {
        int color = getPlanetColor(node.key, node.planet);
        int highlight = lighten(color, 58);
        int shadow = darken(color, 72);
        int radius = node.radius;

        if (node.selected) {
            drawCircleOutline(node.x, node.y, radius + 7, 0xEEFFFFFF, 64);
            drawCircleOutline(node.x, node.y, radius + 10, 0x887FB7FF, 64);
        }


        drawFilledCircle(node.x, node.y, radius + 3, 0x66050A1E, 40);
        drawFilledCircle(node.x, node.y, radius, color, 44);
        drawPlanetDetails(node.key, node.x, node.y, radius, color);
        drawFilledCircle(node.x - Math.max(1, radius / 3), node.y - Math.max(1, radius / 3), Math.max(2, radius / 3), highlight, 28);
        drawCircleOutline(node.x, node.y, radius, shadow, 48);
        if (showPlanetNames) {
            drawPlanetLabel(node);
        }
    }

    private void drawPlanetLabel(PlanetRenderNode node) {
        drawString(fontRenderer, node.label, (int) Math.round(node.x + node.radius + 4), (int) Math.round(node.y - 3), node.selectable ? 0xDDEEFF : 0x8FA0B8);
    }

    private void drawPlanetDetails(String key, double x, double y, int radius, int color) {
        if (isGasPlanet(key)) {
            drawLine(x - radius + 3, y - radius / 3, x + radius - 3, y - radius / 4, 0xAAFFF4CA);
            drawLine(x - radius + 2, y + 1, x + radius - 2, y + 2, 0x88FFF4CA);
            drawLine(x - radius + 4, y + radius / 3, x + radius - 4, y + radius / 4, 0x77FFF4CA);
        } else if (isIcePlanet(key)) {
            drawLine(x - radius / 2, y, x + radius / 2, y - radius / 4, 0xAAFFFFFF);
            drawLine(x - radius / 4, y + radius / 3, x + radius / 2, y + radius / 5, 0x88FFFFFF);
        } else if ("earth".equals(key)) {
            drawFilledCircle(x - radius / 4, y - radius / 5, Math.max(2, radius / 4), 0xAA4FD06B, 16);
            drawFilledCircle(x + radius / 4, y + radius / 5, Math.max(2, radius / 5), 0x994FD06B, 16);
        } else if ("mineral_world".equals(key)) {
            drawLine(x, y - radius + 3, x + radius / 2, y, 0xAAFFFFFF);
            drawLine(x + radius / 2, y, x, y + radius - 3, 0x88FFFFFF);
            drawLine(x, y + radius - 3, x - radius / 2, y, 0x66FFFFFF);
            drawLine(x - radius / 2, y, x, y - radius + 3, 0x66FFFFFF);
        } else {
            drawFilledCircle(x - radius / 3, y - radius / 4, Math.max(1, radius / 5), darken(color, 45), 14);
            drawFilledCircle(x + radius / 4, y + radius / 6, Math.max(1, radius / 6), darken(color, 35), 14);
        }
    }

    private boolean isGasPlanet(String key) {
        return "venus".equals(key);
    }

    private boolean isIcePlanet(String key) {
        return "glacio".equals(key);
    }

    private int getPlanetColor(String key, PlanetDimensionProperties planet) {
        if ("mercury".equals(key)) return 0xFF8B806B;
        if ("venus".equals(key)) return 0xFFD7B86B;
        if ("earth".equals(key)) return 0xFF2F86DF;
        if ("moon".equals(key)) return 0xFFCFD6DE;
        if ("mars".equals(key)) return 0xFFB94D2E;
        if ("glacio".equals(key)) return 0xFFB7D9FF;
        if ("mineral_world".equals(key)) return 0xFF22B66F;
        if (isNetherKey(key)) return 0xFFC12B22;
        if (isEndKey(key)) return 0xFFBBA3E8;
        if (planet != null && planet.getSkyColor() != null) {
            int red = Math.max(60, Math.min(235, (int) (planet.getSkyColor().x * 255.0D)));
            int green = Math.max(60, Math.min(235, (int) (planet.getSkyColor().y * 255.0D)));
            int blue = Math.max(60, Math.min(235, (int) (planet.getSkyColor().z * 255.0D)));
            return 0xFF000000 | red << 16 | green << 8 | blue;
        }
        return 0xFF78A0DC;
    }

    private int lighten(int color, int amount) {
        int red = Math.min(255, ((color >> 16) & 255) + amount);
        int green = Math.min(255, ((color >> 8) & 255) + amount);
        int blue = Math.min(255, (color & 255) + amount);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    private int darken(int color, int amount) {
        int red = Math.max(0, ((color >> 16) & 255) - amount);
        int green = Math.max(0, ((color >> 8) & 255) - amount);
        int blue = Math.max(0, (color & 255) - amount);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    private void drawGalaxyBand() {
        int startX = -width / 5;
        int startY = height * 3 / 4;
        int endX = width + width / 5;
        int endY = height / 5;
        drawBandQuad(startX, startY, endX, endY, Math.max(80, height / 3), 0x080D3E7A);
        drawBandQuad(startX, startY, endX, endY, Math.max(46, height / 5), 0x142057A4);
        drawBandQuad(startX + 18, startY - 10, endX + 18, endY - 10, Math.max(22, height / 11), 0x182D7AD0);
        drawBandQuad(startX - 24, startY + 18, endX - 24, endY + 18, Math.max(18, height / 13), 0x10372A83);
    }

    private void drawBandQuad(int startX, int startY, int endX, int endY, int thickness, int color) {
        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.max(1.0D, Math.sqrt(dx * dx + dy * dy));
        double nx = -dy / length * thickness;
        double ny = dx / length * thickness;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        addVertex(buffer, startX + nx, startY + ny, withAlpha(color, Math.max(0, ((color >> 24) & 255) / 3)));
        addVertex(buffer, endX + nx, endY + ny, color);
        addVertex(buffer, endX - nx, endY - ny, color);
        addVertex(buffer, startX - nx, startY - ny, withAlpha(color, Math.max(0, ((color >> 24) & 255) / 3)));
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void addVertex(BufferBuilder buffer, double x, double y, int color) {
        buffer.pos(x, y, 0).color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255).endVertex();
    }

    private void drawFilledCircle(double centerX, double centerY, int radius, int color, int segments) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.disableTexture2D();
        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(centerX, centerY, 0).color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255).endVertex();
        for (int i = 0; i <= segments; i++) {
            double angle = -Math.PI * 2.0D * i / segments;
            buffer.pos(centerX + Math.cos(angle) * radius, centerY + Math.sin(angle) * radius, 0)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawCircleOutline(double centerX, double centerY, double radius, int color, int segments) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < segments; i++) {
            double angle = Math.PI * 2.0D * i / segments;
            buffer.pos(centerX + Math.cos(angle) * radius, centerY + Math.sin(angle) * radius, 0)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawOrbitGlow(double centerX, double centerY, double radius, int color) {
        drawCircleOutline(centerX, centerY, radius + 1, withAlpha(color, 0x30), 160);
        drawCircleOutline(centerX, centerY, radius, withAlpha(color, 0xB8), 160);
    }

    private int withAlpha(int color, int alpha) {
        return (alpha & 255) << 24 | color & 0x00FFFFFF;
    }

    private void drawEllipseOutline(double centerX, double centerY, int radiusX, int radiusY, double rotationDegrees, int color, int segments) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        double rotation = Math.toRadians(rotationDegrees);
        double cosR = Math.cos(rotation);
        double sinR = Math.sin(rotation);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < segments; i++) {
            double angle = Math.PI * 2.0D * i / segments;
            double x = Math.cos(angle) * radiusX;
            double y = Math.sin(angle) * radiusY;
            double rotatedX = x * cosR - y * sinR;
            double rotatedY = x * sinR + y * cosR;
            buffer.pos(centerX + rotatedX, centerY + rotatedY, 0)
                .color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255).endVertex();
        }
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawLine(double x1, double y1, double x2, double y2, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x1, y1, 0).color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255).endVertex();
        buffer.pos(x2, y2, 0).color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private int menuY() {
        return height / 2 - 88;
    }

    private void drawNameToggleButton() {
        int x = nameToggleX();
        int y = nameToggleY();
        int width = 82;
        int height = 15;
        drawRect(x, y, x + width, y + height, 0x88000000);
        drawBorder(x, y, width, height, showPlanetNames ? 0xFFDDEEFF : 0xFF777777);
        String value = showPlanetNames ? I18n.format("gui.ad_astra.planets.names_on") : I18n.format("gui.ad_astra.planets.names_off");
        drawCenteredString(fontRenderer, value, x + width / 2, y + 4, showPlanetNames ? 0xFFEAF4FF : 0xFF999999);
    }

    private boolean isInsideNameToggle(int mouseX, int mouseY) {
        return isInside(mouseX, mouseY, nameToggleX(), nameToggleY(), 82, 15);
    }

    private int nameToggleX() {
        return Math.max(MENU_X + SMALL_MENU_WIDTH + 40, width - 102);
    }

    private int nameToggleY() {
        return 36;
    }

    private boolean isInsideSelectionPanel(int mouseX, int mouseY) {
        int panelWidth = selectedPlanet == null ? 112 : MENU_WIDTH;
        return isInside(mouseX, mouseY, MENU_X, menuY(), panelWidth, MENU_HEIGHT);
    }

    private void clampMapOffset() {
        int maxX = Math.max(width * 2, 400);
        int maxY = Math.max(height * 2, 300);
        mapOffsetX = Math.max(-maxX, Math.min(maxX, mapOffsetX));
        mapOffsetY = Math.max(-maxY, Math.min(maxY, mapOffsetY));
    }

    private boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private int getGuiScaleFactor() {
        int scale = mc.gameSettings.guiScale;
        if (scale == 0) {
            scale = 1000;
        }
        int factor = 1;
        while (factor < scale && mc.displayWidth / (factor + 1) >= 320 && mc.displayHeight / (factor + 1) >= 240) {
            factor++;
        }
        return factor;
    }

    private String getPlanetLabel(PlanetDimensionProperties planet) {
        ExternalDimensionConfig.ExternalDimensionEntry external =
            ExternalDimensionConfig.getEntry(planet.getDimensionId());
        if (external != null && external.getDisplayName() != null) {
            return external.getDisplayName();
        }
        if (planet.getDimensionId() == 0) {
            return I18n.format("planet.minecraft.overworld");
        }
        if (planet.getDimensionId() == PlanetTravelHelper.NETHER_DIMENSION_ID) {
            return I18n.format("planet.minecraft.nether");
        }
        if (planet.getDimensionId() == PlanetTravelHelper.END_DIMENSION_ID) {
            return I18n.format("planet.minecraft.the_end");
        }
        String name = planet.getName().toLowerCase(Locale.ROOT);
        int colonIndex = name.indexOf(':');
        String namespacedKey = colonIndex >= 0
            ? "planet." + name.substring(0, colonIndex) + "." + name.substring(colonIndex + 1)
            : null;
        if (namespacedKey != null) {
            String translated = I18n.format(namespacedKey);
            if (!translated.equals(namespacedKey)) {
                return translated;
            }
        }

        String adAstraKey = "planet.ad_astra." + name;
        String translated = I18n.format(adAstraKey);
        if (!translated.equals(adAstraKey)) {
            return translated;
        }
        // Try to get displayName from CustomPlanetRegistry
        CustomPlanetDefinition def = CustomPlanetRegistry.getByDimensionId(planet.getDimensionId());
        if (def != null && def.getDisplayName() != null) {
            return def.getDisplayName();
        }
        // Fallback to formatted name
        return formatPlanetName(planet.getName());
    }

    private String formatPlanetName(String name) {
        if (name == null || name.isEmpty()) return "";
        int colonIndex = name.indexOf(':');
        String display = colonIndex >= 0 ? name.substring(colonIndex + 1) : name;
        String[] parts = display.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (result.length() > 0) result.append(" ");
            result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase());
        }
        return result.toString();
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/planets/" + name + ".png");
    }
}

