package earth.terrarium.adastra.client.render;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.rocket.ConfigurableRocketSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public final class ConfigurableRocketTextureManager {

    private static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);
    private static final ResourceLocation FALLBACK = ConfigurableRocketSpec.builtInTextureForModelTier(7);
    private static final Set<ResourceLocation> LOADED = new HashSet<>();
    private static final Set<ResourceLocation> FAILED = new HashSet<>();

    private ConfigurableRocketTextureManager() {
    }

    public static ResourceLocation textureFor(ConfigurableRocketSpec spec) {
        if (spec == null) {
            return FALLBACK;
        }
        ResourceLocation fallback = spec.getBuiltInTexture();
        if (!spec.hasExternalTextureFile()) {
            return spec.getTexture();
        }

        ResourceLocation location = spec.getTexture();
        if (LOADED.contains(location)) {
            return location;
        }
        if (FAILED.contains(location)) {
            return fallback;
        }

        File file = spec.getExternalTextureFile();
        if (file == null || !file.isFile()) {
            LOGGER.warn("Configurable rocket texture file not found: {}", file);
            FAILED.add(location);
            return fallback;
        }

        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                LOGGER.warn("Configurable rocket texture file is not a readable PNG: {}", file);
                FAILED.add(location);
                return fallback;
            }
            ITextureObject texture = new DynamicTexture(image);
            Minecraft.getMinecraft().getTextureManager().loadTexture(location, texture);
            LOADED.add(location);
            return location;
        } catch (Exception exception) {
            LOGGER.warn("Failed to load configurable rocket texture: {}", file, exception);
            FAILED.add(location);
            return fallback;
        }
    }
}
