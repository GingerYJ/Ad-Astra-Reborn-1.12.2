package earth.terrarium.adastra.client.gui;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packet.PacketLandPlanet;
import earth.terrarium.adastra.common.util.PlanetTravelHelper;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Locale;

public class PlanetSelectionGui extends GuiScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites/planets/selection_menu.png");
    private static final int BACKGROUND_WIDTH = 256;
    private static final int BACKGROUND_HEIGHT = 160;

    private final int rocketTier;

    public PlanetSelectionGui(int rocketTier) {
        this.rocketTier = rocketTier;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = (width - BACKGROUND_WIDTH) / 2 + 24;
        int y = (height - BACKGROUND_HEIGHT) / 2 + 34;
        int id = 0;
        for (PlanetDimensionProperties planet : PlanetTravelHelper.getPlanets()) {
            boolean enabled = PlanetTravelHelper.canRocketTierReach(rocketTier, planet);
            GuiButton button = new GuiButton(id++, x, y, 96, 20, getPlanetLabel(planet));
            button.enabled = enabled;
            buttonList.add(button);
            y += 24;
        }
        buttonList.add(new GuiButton(100, (width - BACKGROUND_WIDTH) / 2 + 136, (height - BACKGROUND_HEIGHT) / 2 + 126, 84, 20, I18n.format("gui.cancel")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 100) {
            mc.displayGuiScreen(null);
            return;
        }
        PlanetDimensionProperties[] planets = PlanetTravelHelper.getPlanets();
        if (button.id >= 0 && button.id < planets.length && button.enabled) {
            NetworkHandler.CHANNEL.sendToServer(new PacketLandPlanet(planets[button.id].getDimensionId()));
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int x = (width - BACKGROUND_WIDTH) / 2;
        int y = (height - BACKGROUND_HEIGHT) / 2;
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        drawCenteredString(fontRenderer, I18n.format("gui.ad_astra.planets.title"), width / 2, y + 12, 0xFFFFFF);
        drawString(fontRenderer, I18n.format("gui.ad_astra.planets.rocket_tier", rocketTier), x + 136, y + 42, 0xC9E6FF);
        drawString(fontRenderer, I18n.format("gui.ad_astra.planets.hint"), x + 136, y + 58, 0xA0A0A0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String getPlanetLabel(PlanetDimensionProperties planet) {
        return I18n.format("planet.ad_astra." + planet.getName().toLowerCase(Locale.ROOT));
    }
}
