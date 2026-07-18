package earth.terrarium.adastra.common.world.custom;

import earth.terrarium.adastra.Reference;
import earth.terrarium.adastra.common.world.PlanetDimensionProperties;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class CustomPlanetDefinition {

    public static final int DEFAULT_DAY_LENGTH = 24000;

    private final ResourceLocation id;
    private final ResourceLocation solarSystem;
    private final String planetName;
    @Nullable
    private final String displayName;
    private final int dimensionId;
    private final int orbitDimensionId;
    private final String saveFolder;
    private final ResourceLocation biomeId;
    private final IBlockState surfaceBlock;
    private final IBlockState stoneBlock;
    private final IBlockState fillerBlock;
    private final ItemStack iconStack;
    private final boolean hasSkyLight;
    private final boolean canRespawn;
    private final boolean oxygen;
    private final short temperature;
    private final float gravity;
    private final int solarPower;
    private final int orbitSolarPower;
    private final int tier;
    private final int dayLength;
    private final Vec3d fogColor;
    private final Vec3d skyColor;
    private final boolean registerDimension;
    private final List<OreDefinition> ores;
    private final List<FluidLakeDefinition> fluidLakes;

    private CustomPlanetDefinition(Builder builder) {
        this.id = builder.id;
        this.solarSystem = builder.solarSystem;
        this.planetName = builder.planetName == null ? sanitizeName(builder.id.getPath()) : sanitizeName(builder.planetName);
        this.displayName = builder.displayName;
        this.dimensionId = builder.dimensionId;
        this.orbitDimensionId = builder.orbitDimensionId;
        this.saveFolder = builder.saveFolder == null ? defaultSaveFolder(builder.id) : builder.saveFolder;
        this.biomeId = builder.biomeId;
        this.surfaceBlock = builder.surfaceBlock;
        this.stoneBlock = builder.stoneBlock;
        this.fillerBlock = builder.fillerBlock == null ? builder.stoneBlock : builder.fillerBlock;
        this.iconStack = builder.iconStack.copy();
        this.hasSkyLight = builder.hasSkyLight;
        this.canRespawn = builder.canRespawn;
        this.oxygen = builder.oxygen;
        this.temperature = builder.temperature;
        this.gravity = builder.gravity;
        this.solarPower = builder.solarPower;
        this.orbitSolarPower = builder.orbitSolarPower;
        this.tier = builder.tier;
        this.dayLength = builder.dayLength;
        this.fogColor = builder.fogColor;
        this.skyColor = builder.skyColor;
        this.registerDimension = builder.registerDimension;
        this.ores = Collections.unmodifiableList(new ArrayList<>(builder.ores));
        this.fluidLakes = Collections.unmodifiableList(new ArrayList<>(builder.fluidLakes));
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceLocation getSolarSystem() {
        return solarSystem;
    }

    public String getPlanetName() {
        return planetName;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    public String getTranslationKey() {
        return "planet." + id.getNamespace() + "." + id.getPath();
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public int getOrbitDimensionId() {
        return orbitDimensionId;
    }

    public String getSaveFolder() {
        return saveFolder;
    }

    public ResourceLocation getBiomeId() {
        return biomeId;
    }

    public IBlockState getSurfaceBlock() {
        return surfaceBlock;
    }

    public IBlockState getStoneBlock() {
        return stoneBlock;
    }

    public IBlockState getFillerBlock() {
        return fillerBlock;
    }

    public ItemStack getIconStack() {
        return iconStack.copy();
    }

    public boolean hasSkyLight() {
        return hasSkyLight;
    }

    public boolean canRespawn() {
        return canRespawn;
    }

    public boolean hasOxygen() {
        return oxygen;
    }

    public short getTemperature() {
        return temperature;
    }

    public float getGravity() {
        return gravity;
    }

    public int getSolarPower() {
        return solarPower;
    }

    public int getOrbitSolarPower() {
        return orbitSolarPower;
    }

    public int getTier() {
        return tier;
    }

    public int getDayLength() {
        return dayLength;
    }

    public Vec3d getFogColor() {
        return fogColor;
    }

    public Vec3d getSkyColor() {
        return skyColor;
    }

    public boolean shouldRegisterDimension() {
        return registerDimension;
    }

    public List<OreDefinition> getOres() {
        return ores;
    }

    public List<FluidLakeDefinition> getFluidLakes() {
        return fluidLakes;
    }


    public static Builder builder(ResourceLocation id, int dimensionId) {
        return new Builder(id, dimensionId);
    }

    public PlanetDimensionProperties toDimensionProperties() {
        return new PlanetDimensionProperties(
            planetName,
            dimensionId,
            saveFolder,
            ForgeRegistries.BIOMES.getValue(biomeId) == null ? Biomes.DEFAULT : ForgeRegistries.BIOMES.getValue(biomeId),
            surfaceBlock,
            fillerBlock,
            hasSkyLight,
            canRespawn,
            oxygen,
            temperature,
            gravity,
            solarPower,
            tier,
            dayLength,
            fogColor,
            skyColor
        );
    }

    public PlanetDimensionProperties toOrbitDimensionProperties() {
        return new PlanetDimensionProperties(
            planetName + "_orbit",
            orbitDimensionId,
            saveFolder + "_ORBIT",
            ForgeRegistries.BIOMES.getValue(biomeId) == null ? Biomes.DEFAULT : ForgeRegistries.BIOMES.getValue(biomeId),
            Blocks.AIR.getDefaultState(),
            Blocks.AIR.getDefaultState(),
            true,
            false,
            false,
            (short) -270,
            0.0F,
            orbitSolarPower,
            tier,
            dayLength,
            new Vec3d(0.0D, 0.0D, 0.0D),
            new Vec3d(0.0D, 0.0D, 0.0D)
        );
    }

    public static PlanetDimensionProperties fallbackProperties(int dimensionId) {
        return builder(new ResourceLocation(Reference.MOD_ID, "missing_custom_" + dimensionId), dimensionId)
            .saveFolder("DIM_AD_ASTRA_MISSING_CUSTOM_" + dimensionId)
            .build()
            .toDimensionProperties();
    }

    public static PlanetDimensionProperties fallbackOrbitProperties(int orbitDimensionId) {
        int planetDimensionId = orbitDimensionId - 1;
        return builder(new ResourceLocation(Reference.MOD_ID, "missing_custom_orbit_" + orbitDimensionId), planetDimensionId)
            .saveFolder("DIM_AD_ASTRA_MISSING_CUSTOM_ORBIT_" + orbitDimensionId)
            .build()
            .toOrbitDimensionProperties();
    }


    public static ResourceLocation parseId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty.");
        }
        if (id.contains(":")) {
            return new ResourceLocation(id);
        }
        return new ResourceLocation(Reference.MOD_ID, id);
    }

    public ResourceLocation getOrbitDimensionLocation() {
        return new ResourceLocation(id.getNamespace(), id.getPath() + "_orbit");
    }
    public static IBlockState stateFromBlock(Block block, int meta) {
        if (block == null) {
            throw new IllegalArgumentException("block cannot be null.");
        }
        return block.getStateFromMeta(meta);
    }

    private static String defaultSaveFolder(ResourceLocation id) {
        return "DIM_AD_ASTRA_CUSTOM_" + sanitizeName(id.getNamespace() + "_" + id.getPath()).toUpperCase(Locale.ROOT);
    }

    private static String sanitizeName(String value) {
        String lower = value == null ? "custom_planet" : value.toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder(lower.length());
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_') {
                builder.append(c);
            } else {
                builder.append('_');
            }
        }
        return builder.length() == 0 ? "custom_planet" : builder.toString();
    }

    private static IBlockState requireState(IBlockState state, String name) {
        if (state == null) {
            throw new IllegalArgumentException(name + " cannot be null.");
        }
        return state;
    }

    private static int positive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive.");
        }
        return value;
    }

    private static int nonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative.");
        }
        return value;
    }

    private static int clampY(int value, String name) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(name + " must be between 0 and 255.");
        }
        return value;
    }

    public static final class OreDefinition {

        private final String configKey;
        private final IBlockState oreBlock;
        private final IBlockState replaceBlock;
        private final int veinSize;
        private final int countPerChunk;
        private final int minY;
        private final int maxY;

        public OreDefinition(IBlockState oreBlock, IBlockState replaceBlock, int veinSize, int countPerChunk, int minY, int maxY) {
            this(null, oreBlock, replaceBlock, veinSize, countPerChunk, minY, maxY);
        }

        public OreDefinition(String configKey, IBlockState oreBlock, IBlockState replaceBlock,
                             int veinSize, int countPerChunk, int minY, int maxY) {
            this.oreBlock = requireState(oreBlock, "oreBlock");
            this.replaceBlock = requireState(replaceBlock, "replaceBlock");
            this.configKey = sanitizeConfigKey(configKey, this.oreBlock);
            this.veinSize = positive(veinSize, "veinSize");
            this.countPerChunk = nonNegative(countPerChunk, "countPerChunk");
            this.minY = clampY(minY, "minY");
            this.maxY = clampY(maxY, "maxY");
            if (this.minY > this.maxY) {
                throw new IllegalArgumentException("minY cannot be greater than maxY.");
            }
        }

        /** Stable suffix used for the generated worldgen_<planet> config properties. */
        public String getConfigKey() {
            return configKey;
        }

        public IBlockState getOreBlock() {
            return oreBlock;
        }

        public IBlockState getReplaceBlock() {
            return replaceBlock;
        }

        public int getVeinSize() {
            return veinSize;
        }

        public int getCountPerChunk() {
            return countPerChunk;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxY() {
            return maxY;
        }

        private static String sanitizeConfigKey(String value, IBlockState oreBlock) {
            String source = value == null ? "" : value.trim();
            if (source.isEmpty() && oreBlock.getBlock().getRegistryName() != null) {
                source = oreBlock.getBlock().getRegistryName().getPath();
            }
            if (source.isEmpty()) {
                source = "ore";
            }

            String lower = source.toLowerCase(Locale.ROOT);
            StringBuilder builder = new StringBuilder(lower.length());
            for (int i = 0; i < lower.length(); i++) {
                char c = lower.charAt(i);
                if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_') {
                    builder.append(c);
                } else {
                    builder.append('_');
                }
            }
            return builder.length() == 0 ? "ore" : builder.toString();
        }
    }

    public static final class FluidLakeDefinition {

        private final IBlockState fluidBlock;
        private final IBlockState replaceBlock;
        private final int lakeSize;
        private final int countPerChunk;
        private final int minY;
        private final int maxY;

        public FluidLakeDefinition(IBlockState fluidBlock, IBlockState replaceBlock, int lakeSize, int countPerChunk, int minY, int maxY) {
            this.fluidBlock = requireState(fluidBlock, "fluidBlock");
            this.replaceBlock = requireState(replaceBlock, "replaceBlock");
            this.lakeSize = positive(lakeSize, "lakeSize");
            this.countPerChunk = nonNegative(countPerChunk, "countPerChunk");
            this.minY = clampY(minY, "minY");
            this.maxY = clampY(maxY, "maxY");
            if (this.minY > this.maxY) {
                throw new IllegalArgumentException("minY cannot be greater than maxY.");
            }
        }

        public IBlockState getFluidBlock() {
            return fluidBlock;
        }

        public IBlockState getReplaceBlock() {
            return replaceBlock;
        }

        public int getLakeSize() {
            return lakeSize;
        }

        public int getCountPerChunk() {
            return countPerChunk;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxY() {
            return maxY;
        }
    }


    public static final class Builder {


        private final ResourceLocation id;
        private ResourceLocation solarSystem;
        private String planetName;
        @Nullable
        private String displayName;
        private final int dimensionId;
        private int orbitDimensionId;
        private String saveFolder;
        private ResourceLocation biomeId = new ResourceLocation("minecraft", "plains");
        private IBlockState surfaceBlock = Blocks.GRASS.getDefaultState();
        private IBlockState stoneBlock = Blocks.STONE.getDefaultState();
        private IBlockState fillerBlock;
        private ItemStack iconStack = new ItemStack(Blocks.GRASS);
        private boolean hasSkyLight = true;
        private boolean canRespawn = true;
        private boolean oxygen;
        private short temperature;
        private float gravity = 1.0F;
        private int solarPower = 10;
        private int orbitSolarPower = 10;
        private int tier = 1;
        private int dayLength = DEFAULT_DAY_LENGTH;
        private Vec3d fogColor = new Vec3d(0.0D, 0.0D, 0.0D);
        private Vec3d skyColor = new Vec3d(0.5D, 0.7D, 1.0D);
        private boolean registerDimension;
        private final List<OreDefinition> ores = new ArrayList<>();
        private final List<FluidLakeDefinition> fluidLakes = new ArrayList<>();

        private Builder(ResourceLocation id, int dimensionId) {
            if (id == null) {
                throw new IllegalArgumentException("Custom planet id cannot be null.");
            }
            if (dimensionId == 0) {
                throw new IllegalArgumentException("Custom planets cannot use the overworld dimension id.");
            }
            this.id = id;
            this.dimensionId = dimensionId;
            this.orbitDimensionId = dimensionId + 1;
            this.solarSystem = new ResourceLocation(id.getNamespace(), "solar_system");
        }

        public Builder planetName(String planetName) {
            this.planetName = planetName;
            return this;
        }

        public Builder displayName(@Nullable String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder solarSystem(ResourceLocation solarSystem) {
            if (solarSystem == null) {
                throw new IllegalArgumentException("solarSystem cannot be null.");
            }
            this.solarSystem = solarSystem;
            return this;
        }

        public Builder saveFolder(String saveFolder) {
            if (saveFolder == null || saveFolder.trim().isEmpty()) {
                throw new IllegalArgumentException("saveFolder cannot be empty.");
            }
            this.saveFolder = saveFolder.trim();
            return this;
        }

        public Builder biome(ResourceLocation biomeId) {
            if (biomeId == null) {
                throw new IllegalArgumentException("biomeId cannot be null.");
            }
            this.biomeId = biomeId;
            return this;
        }

        public Builder surfaceBlock(IBlockState surfaceBlock) {
            this.surfaceBlock = requireState(surfaceBlock, "surfaceBlock");
            return this;
        }

        public Builder stoneBlock(IBlockState stoneBlock) {
            this.stoneBlock = requireState(stoneBlock, "stoneBlock");
            return this;
        }

        public Builder fillerBlock(IBlockState fillerBlock) {
            this.fillerBlock = requireState(fillerBlock, "fillerBlock");
            return this;
        }

        public Builder orbitDimensionId(int orbitDimensionId) {
            if (orbitDimensionId == 0) {
                throw new IllegalArgumentException("Orbit dimension id cannot be 0 (overworld).");
            }
            this.orbitDimensionId = orbitDimensionId;
            return this;
        }

        public Builder iconStack(ItemStack iconStack) {
            if (iconStack == null || iconStack.isEmpty()) {
                throw new IllegalArgumentException("iconStack cannot be empty.");
            }
            this.iconStack = iconStack.copy();
            return this;
        }

        public Builder skyLight(boolean hasSkyLight) {
            this.hasSkyLight = hasSkyLight;
            return this;
        }

        public Builder canRespawn(boolean canRespawn) {
            this.canRespawn = canRespawn;
            return this;
        }

        public Builder environment(boolean oxygen, short temperature, float gravity, int solarPower) {
            this.oxygen = oxygen;
            this.temperature = temperature;
            this.gravity = gravity;
            this.solarPower = solarPower;
            this.orbitSolarPower = solarPower;
            return this;
        }

        public Builder orbitSolarPower(int orbitSolarPower) {
            this.orbitSolarPower = orbitSolarPower;
            return this;
        }

        public Builder tier(int tier) {
            this.tier = positive(tier, "tier");
            return this;
        }

        public Builder dayLength(int dayLength) {
            this.dayLength = positive(dayLength, "dayLength");
            return this;
        }

        public Builder fogColor(double red, double green, double blue) {
            this.fogColor = new Vec3d(red, green, blue);
            return this;
        }

        public Builder skyColor(double red, double green, double blue) {
            this.skyColor = new Vec3d(red, green, blue);
            return this;
        }

        public Builder registerDimension(boolean registerDimension) {
            this.registerDimension = registerDimension;
            return this;
        }

        public Builder addOre(IBlockState oreBlock, IBlockState replaceBlock, int veinSize, int countPerChunk, int minY, int maxY) {
            return addOre(null, oreBlock, replaceBlock, veinSize, countPerChunk, minY, maxY);
        }

        public Builder addOre(String configKey, IBlockState oreBlock, IBlockState replaceBlock,
                              int veinSize, int countPerChunk, int minY, int maxY) {
            this.ores.add(new OreDefinition(configKey, oreBlock, replaceBlock, veinSize, countPerChunk, minY, maxY));
            return this;
        }

        public Builder addFluidLake(IBlockState fluidBlock, IBlockState replaceBlock, int lakeSize, int countPerChunk, int minY, int maxY) {
            this.fluidLakes.add(new FluidLakeDefinition(fluidBlock, replaceBlock, lakeSize, countPerChunk, minY, maxY));
            return this;
        }


        public CustomPlanetDefinition build() {
            return new CustomPlanetDefinition(this);
        }
    }
}
