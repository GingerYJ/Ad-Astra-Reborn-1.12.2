package earth.terrarium.adastra.common.rocket;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.items.ConfigurableRocketItem;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.File;

public final class ConfigurableRocketSpec {

    private static final ConfigurableRocketSpec FALLBACK = new ConfigurableRocketSpec(
        "configurable_rocket_fallback",
        "配置火箭",
        7,
        9000,
        7,
        new ResourceLocation(Reference.MOD_ID, "textures/entity/rocket/tier_7_rocket.png"),
        null,
        "ad_astra:textures/entity/rocket/tier_7_rocket.png");

    private final String id;
    private final String displayName;
    private final int tier;
    private final int fuelCapacity;
    private final int modelTier;
    private final ResourceLocation texture;
    private final File externalTextureFile;
    private final String textureDisplayName;
    private ConfigurableRocketItem item;

    public ConfigurableRocketSpec(String id, String displayName, int tier, int fuelCapacity, int modelTier, ResourceLocation texture) {
        this(id, displayName, tier, fuelCapacity, modelTier, texture, null, texture.toString());
    }

    public ConfigurableRocketSpec(String id, String displayName, int tier, int fuelCapacity, int modelTier,
                                  ResourceLocation texture, @Nullable File externalTextureFile, String textureDisplayName) {
        this.id = id;
        this.displayName = displayName;
        this.tier = tier;
        this.fuelCapacity = fuelCapacity;
        this.modelTier = modelTier;
        this.texture = texture;
        this.externalTextureFile = externalTextureFile;
        this.textureDisplayName = textureDisplayName;
    }

    public static ConfigurableRocketSpec fallback() {
        return FALLBACK;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getTier() {
        return tier;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public int getModelTier() {
        return modelTier;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @Nullable
    public File getExternalTextureFile() {
        return externalTextureFile;
    }

    public boolean hasExternalTextureFile() {
        return externalTextureFile != null;
    }

    public String getTextureDisplayName() {
        return textureDisplayName;
    }

    public ConfigurableRocketItem getItem() {
        return item;
    }

    public void setItem(ConfigurableRocketItem item) {
        this.item = item;
    }
}
