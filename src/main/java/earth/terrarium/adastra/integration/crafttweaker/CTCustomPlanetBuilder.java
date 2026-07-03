package earth.terrarium.adastra.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import earth.terrarium.adastra.common.world.custom.CustomPlanetDefinition;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.ad_astra.CustomPlanetBuilder")
public final class CTCustomPlanetBuilder {

    private final CustomPlanetDefinition.Builder builder;

    CTCustomPlanetBuilder(CustomPlanetDefinition.Builder builder) {
        this.builder = builder;
    }

    @ZenMethod("name")
    public CTCustomPlanetBuilder name(String name) {
        builder.planetName(name);
        return this;
    }

    @ZenMethod("displayName")
    public CTCustomPlanetBuilder displayName(String displayName) {
        builder.displayName(displayName);
        return this;
    }

    @ZenMethod("saveFolder")
    public CTCustomPlanetBuilder saveFolder(String saveFolder) {
        builder.saveFolder(saveFolder);
        return this;
    }

    @ZenMethod("biome")
    public CTCustomPlanetBuilder biome(String biomeId) {
        builder.biome(new ResourceLocation(biomeId));
        return this;
    }

    @ZenMethod("surface")
    public CTCustomPlanetBuilder surface(IBlock block) {
        builder.surfaceBlock(toBlockState(block));
        return this;
    }

    @ZenMethod("surface")
    public CTCustomPlanetBuilder surface(IItemStack stack) {
        builder.surfaceBlock(toBlockState(stack));
        return this;
    }

    @ZenMethod("stone")
    public CTCustomPlanetBuilder stone(IBlock block) {
        builder.stoneBlock(toBlockState(block));
        return this;
    }

    @ZenMethod("stone")
    public CTCustomPlanetBuilder stone(IItemStack stack) {
        builder.stoneBlock(toBlockState(stack));
        return this;
    }

    @ZenMethod("icon")
    public CTCustomPlanetBuilder icon(IItemStack stack) {
        builder.iconStack(toItemStack(stack));
        return this;
    }

    @ZenMethod("iconBlock")
    public CTCustomPlanetBuilder iconBlock(IBlock block) {
        Block mcBlock = toBlock(block);
        builder.iconStack(new ItemStack(mcBlock, 1, safeMeta(block)));
        return this;
    }

    @ZenMethod("skyLight")
    public CTCustomPlanetBuilder skyLight(boolean hasSkyLight) {
        builder.skyLight(hasSkyLight);
        return this;
    }

    @ZenMethod("canRespawn")
    public CTCustomPlanetBuilder canRespawn(boolean canRespawn) {
        builder.canRespawn(canRespawn);
        return this;
    }

    @ZenMethod("environment")
    public CTCustomPlanetBuilder environment(boolean oxygen, int temperature, double gravity, int solarPower) {
        builder.environment(oxygen, (short) temperature, (float) gravity, solarPower);
        return this;
    }

    @ZenMethod("tier")
    public CTCustomPlanetBuilder tier(int tier) {
        builder.tier(tier);
        return this;
    }

    @ZenMethod("dayLength")
    public CTCustomPlanetBuilder dayLength(int dayLength) {
        builder.dayLength(dayLength);
        return this;
    }

    @ZenMethod("colors")
    public CTCustomPlanetBuilder colors(double fogRed, double fogGreen, double fogBlue, double skyRed, double skyGreen, double skyBlue) {
        builder.fogColor(fogRed, fogGreen, fogBlue);
        builder.skyColor(skyRed, skyGreen, skyBlue);
        return this;
    }

    @ZenMethod("addOre")
    public CTCustomPlanetBuilder addOre(IBlock oreBlock, IBlock replaceBlock, int veinSize, int countPerChunk, int minY, int maxY) {
        builder.addOre(toBlockState(oreBlock), toBlockState(replaceBlock), veinSize, countPerChunk, minY, maxY);
        return this;
    }

    @ZenMethod("addOre")
    public CTCustomPlanetBuilder addOre(IItemStack oreStack, IItemStack replaceStack, int veinSize, int countPerChunk, int minY, int maxY) {
        builder.addOre(toBlockState(oreStack), toBlockState(replaceStack), veinSize, countPerChunk, minY, maxY);
        return this;
    }

    @ZenMethod("addFluidLake")
    public CTCustomPlanetBuilder addFluidLake(ILiquidStack fluidStack, int countPerChunk, int minY, int maxY) {
        FluidStack mcStack = CraftTweakerMC.getLiquidStack(fluidStack);
        if (mcStack == null || mcStack.getFluid() == null || mcStack.getFluid().getBlock() == null) {
            throw new IllegalArgumentException("Liquid stack must expose a fluid block.");
        }
        Fluid fluid = mcStack.getFluid();
        builder.addFluidLake(fluid, fluid.getBlock().getDefaultState(), countPerChunk, minY, maxY);
        return this;
    }

    @ZenMethod("addFluidBlock")
    public CTCustomPlanetBuilder addFluidBlock(IBlock fluidBlock, int countPerChunk, int minY, int maxY) {
        builder.addFluidLake(null, toBlockState(fluidBlock), countPerChunk, minY, maxY);
        return this;
    }

    @ZenMethod("enableDimensionRegistration")
    public CTCustomPlanetBuilder enableDimensionRegistration(boolean enabled) {
        builder.registerDimension(enabled);
        return this;
    }

    @ZenMethod("register")
    public void register() {
        CustomPlanetDefinition definition = builder.build();
        if (definition.shouldRegisterDimension()) {
            CraftTweakerAPI.logWarning("Ad Astra custom planet " + definition.getId()
                + " requested dimension registration from CraftTweaker. The definition is stored, but dimensions must be registered from an early Java/config hook.");
        }
        CTCustomPlanets.apply(definition);
    }

    private static IBlockState toBlockState(IBlock block) {
        Block mcBlock = toBlock(block);
        return CustomPlanetDefinition.stateFromBlock(mcBlock, safeMeta(block));
    }

    private static IBlockState toBlockState(IItemStack stack) {
        ItemStack mcStack = toItemStack(stack);
        Item item = mcStack.getItem();
        if (item == null) {
            throw new IllegalArgumentException("Item stack " + stack + " has no item.");
        }
        Block block = Block.getBlockFromItem(item);
        if (block == null) {
            throw new IllegalArgumentException("Item stack " + stack + " does not correspond to a block.");
        }
        return CustomPlanetDefinition.stateFromBlock(block, mcStack.getMetadata());
    }

    private static Block toBlock(IBlock block) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null.");
        }
        Block mcBlock = CraftTweakerMC.getBlock(block);
        if (mcBlock == null) {
            throw new IllegalArgumentException("CraftTweaker block " + block + " did not expose a Minecraft block.");
        }
        return mcBlock;
    }

    private static ItemStack toItemStack(IItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Item stack cannot be empty.");
        }
        ItemStack itemStack = CraftTweakerMC.getItemStack(stack);
        if (itemStack == null || itemStack.isEmpty()) {
            throw new IllegalArgumentException("CraftTweaker item stack " + stack + " did not expose a Minecraft item stack.");
        }
        return itemStack.copy();
    }

    private static int safeMeta(IBlock block) {
        return Math.max(0, block.getMeta());
    }
}

