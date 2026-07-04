package earth.terrarium.adastra.client.hud;

import earth.terrarium.adastra.api.systems.PlanetData;
import earth.terrarium.adastra.client.systems.ClientData;
import earth.terrarium.adastra.common.config.AdAstraConfig;
import earth.terrarium.adastra.common.items.AdAstraArmorItem;
import earth.terrarium.adastra.common.items.GasTankItem;
import earth.terrarium.adastra.common.registry.ModBlocks;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.registry.ModItems;
import earth.terrarium.adastra.common.tile.GravityNormalizerTileEntity;
import earth.terrarium.adastra.common.tile.OxygenDistributorTileEntity;
import earth.terrarium.adastra.common.util.MachineStateUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.Locale;

public final class AdAstraHudOverlay {

    private static final int PANEL_PADDING = 5;
    private static final int ROW_HEIGHT = 13;
    private static final int ROW_COUNT = 4;
    private static final int MAX_PANEL_WIDTH = 184;
    private static final int MIN_PANEL_WIDTH = 132;
    private static final int LABEL_WIDTH = 44;
    private static final int BAR_HEIGHT = 4;
    private static final int ENVIRONMENT_SCAN_RADIUS = 16;
    private static final int ENVIRONMENT_SCAN_INTERVAL_TICKS = 20;
    private static final float WARNING_FLASH_SPEED = 0.1f;
    private static final float LOW_OXYGEN_THRESHOLD = 0.2f;
    private static final int EXTREME_TEMP_LOW = -100;
    private static final int EXTREME_TEMP_HIGH = 100;

    private static int lastEnvironmentScanTick = -ENVIRONMENT_SCAN_INTERVAL_TICKS;
    private static EnvironmentSnapshot cachedEnvironment = EnvironmentSnapshot.EMPTY;

    private AdAstraHudOverlay() {
    }

    public static void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        if (player == null || player.isSpectator() || minecraft.gameSettings.hideGUI || minecraft.gameSettings.showDebugInfo || minecraft.currentScreen != null) {
            return;
        }
        if (!isWearingSpaceHudSuit(player)) {
            return;
        }

        float hudScale = getHudScale();
        int panelX = AdAstraConfig.oxygenBarX;
        int panelY = AdAstraConfig.oxygenBarY;
        ScaledResolution resolution = event.getResolution();
        int availableWidth = Math.max(0, (int) (resolution.getScaledWidth() / hudScale) - panelX * 2);
        int panelWidth = Math.min(MAX_PANEL_WIDTH, availableWidth);
        if (panelWidth < MIN_PANEL_WIDTH) {
            return;
        }

        EnvironmentSnapshot environment = getEnvironment(player);
        HudData data = HudData.from(player, environment);
        draw(minecraft.fontRenderer, panelX, panelY, hudScale, panelWidth, data);
    }

    private static EnvironmentSnapshot getEnvironment(EntityPlayer player) {
        if (player.ticksExisted - lastEnvironmentScanTick >= ENVIRONMENT_SCAN_INTERVAL_TICKS) {
            cachedEnvironment = scanEnvironment(player);
            lastEnvironmentScanTick = player.ticksExisted;
        }
        return cachedEnvironment;
    }

    private static EnvironmentSnapshot scanEnvironment(EntityPlayer player) {
        World world = player.world;
        if (world == null) {
            return EnvironmentSnapshot.EMPTY;
        }

        WorldEnvironment worldEnvironment = WorldEnvironment.from(world);
        BlockPos playerPos = player.getPosition();
        BlockPos min = playerPos.add(-ENVIRONMENT_SCAN_RADIUS, -ENVIRONMENT_SCAN_RADIUS, -ENVIRONMENT_SCAN_RADIUS);
        BlockPos max = playerPos.add(ENVIRONMENT_SCAN_RADIUS, ENVIRONMENT_SCAN_RADIUS, ENVIRONMENT_SCAN_RADIUS);

        boolean oxygen = worldEnvironment.oxygen;
        boolean hasGravity = worldEnvironment.hasGravity;
        float gravityValue = worldEnvironment.gravity;
        boolean foundGravityNormalizer = false;

        for (BlockPos mutablePos : BlockPos.getAllInBoxMutable(min, max)) {
            IBlockState state = world.getBlockState(mutablePos);
            Block block = state.getBlock();

            if (!oxygen && block == ModBlocks.OXYGEN_DISTRIBUTOR && isMachineLit(state) && reachesOxygenDistributor(world, mutablePos, playerPos)) {
                oxygen = true;
            } else if (block == ModBlocks.GRAVITY_NORMALIZER && isMachineLit(state) && reachesGravityNormalizer(world, mutablePos, playerPos)) {
                hasGravity = true;
                gravityValue = getTargetGravity(world, mutablePos) * 9.8f;
                foundGravityNormalizer = true;
            }

            if (oxygen && foundGravityNormalizer) {
                break;
            }
        }

        return new EnvironmentSnapshot(oxygen, hasGravity, gravityValue, worldEnvironment.hasTemperature, worldEnvironment.temperature);
    }

    private static boolean isMachineLit(IBlockState state) {
        return MachineStateUtils.isLit(state);
    }

    private static boolean reachesOxygenDistributor(World world, BlockPos distributorPos, BlockPos playerPos) {
        TileEntity tile = world.getTileEntity(distributorPos);
        if (tile instanceof OxygenDistributorTileEntity) {
            int radius = Math.max(1, ((OxygenDistributorTileEntity) tile).getWorkingRadius());
            return distributorPos.distanceSq(playerPos) <= radius * radius;
        }
        return true;
    }

    private static boolean reachesGravityNormalizer(World world, BlockPos normalizerPos, BlockPos playerPos) {
        TileEntity tile = world.getTileEntity(normalizerPos);
        if (tile instanceof GravityNormalizerTileEntity) {
            int radius = Math.max(1, ((GravityNormalizerTileEntity) tile).getWorkingRadius());
            return normalizerPos.distanceSq(playerPos) <= radius * radius;
        }
        return true;
    }

    private static float getTargetGravity(World world, BlockPos normalizerPos) {
        TileEntity tile = world.getTileEntity(normalizerPos);
        if (tile instanceof GravityNormalizerTileEntity) {
            return ((GravityNormalizerTileEntity) tile).getTargetGravity();
        }
        return 1.0f;
    }

    private static void draw(FontRenderer font, int panelX, int panelY, float hudScale, int panelWidth, HudData data) {
        HudRow[] rows = new HudRow[]{
            data.oxygenRow,
            data.temperatureRow,
            data.gravityRow,
            data.energyRow
        };

        int valueWidth = 0;
        for (HudRow row : rows) {
            valueWidth = Math.max(valueWidth, font.getStringWidth(row.value));
        }

        int panelHeight = PANEL_PADDING * 2 + ROW_COUNT * ROW_HEIGHT;
        int contentRight = panelX + panelWidth - PANEL_PADDING;
        int barX = panelX + PANEL_PADDING + LABEL_WIDTH;
        int barWidth = Math.max(24, contentRight - barX - valueWidth - 6);

        GlStateManager.pushMatrix();
        GlStateManager.scale(hudScale, hudScale, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        // Flash warning border if critical conditions
        int borderColor = 0x8855FFFF;
        if (data.hasWarning) {
            float flashIntensity = getFlashIntensity();
            int red = (int) (255 * flashIntensity);
            borderColor = 0x88000000 | (red << 16) | 0x3333;
        }

        Gui.drawRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x66000000);
        Gui.drawRect(panelX, panelY, panelX + 1, panelY + panelHeight, borderColor);

        int y = panelY + PANEL_PADDING;
        for (HudRow row : rows) {
            drawRow(font, row, panelX + PANEL_PADDING, y, barX, barWidth, contentRight);
            y += ROW_HEIGHT;
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static float getHudScale() {
        return Math.max(0.25F, Math.min(4.0F, AdAstraConfig.oxygenBarScale));
    }

    private static void drawRow(FontRenderer font, HudRow row, int labelX, int y, int barX, int barWidth, int contentRight) {
        int barY = y + 8;
        int filledWidth = Math.max(0, Math.min(barWidth, (int) Math.round(barWidth * row.ratio)));

        // Apply warning flash to text if needed
        int labelColor = row.labelColor;
        int valueColor = row.valueColor;
        if (row.warning) {
            float flashIntensity = getFlashIntensity();
            labelColor = blendColor(row.labelColor, 0xFFFFFF, flashIntensity * 0.5f);
            valueColor = blendColor(row.valueColor, 0xFF3333, flashIntensity * 0.7f);
        }

        font.drawStringWithShadow(row.label, labelX, y, labelColor);
        Gui.drawRect(barX, barY, barX + barWidth, barY + BAR_HEIGHT, 0xAA151515);
        if (filledWidth > 0) {
            Gui.drawRect(barX, barY, barX + filledWidth, barY + BAR_HEIGHT, 0xFF000000 | row.barColor);
        }
        Gui.drawRect(barX, barY + BAR_HEIGHT, barX + barWidth, barY + BAR_HEIGHT + 1, 0x55222222);
        font.drawStringWithShadow(row.value, contentRight - font.getStringWidth(row.value), y, valueColor);
    }

    private static String percent(long amount, long capacity) {
        if (capacity <= 0) {
            return "--";
        }
        return String.format(Locale.ROOT, "%d%%", Math.round((amount * 100.0d) / capacity));
    }

    private static String oneDecimal(float value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static float getFlashIntensity() {
        long time = Minecraft.getMinecraft().world != null ? Minecraft.getMinecraft().world.getTotalWorldTime() : 0;
        return (float) ((Math.sin(time * WARNING_FLASH_SPEED) + 1.0) / 2.0);
    }

    private static int blendColor(int color1, int color2, float ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }

    private static double ratio(long amount, long capacity) {
        if (capacity <= 0) {
            return 0.0d;
        }
        return Math.max(0.0d, Math.min(1.0d, amount / (double) capacity));
    }

    private static double temperatureRatio(int temperature) {
        return Math.max(0.0d, Math.min(1.0d, (temperature + 200.0d) / 700.0d));
    }

    private static int temperatureColor(int temperature) {
        if (temperature < -50) {
            return 0x66CCFF;
        }
        if (temperature > 60) {
            return 0xFF7855;
        }
        return 0x55FF55;
    }

    private static boolean hasTemperatureProtection(EntityPlayer player) {
        return isWearingSpaceHudSuit(player);
    }

    private static boolean isWearingSpaceHudSuit(EntityPlayer player) {
        return isWearingSet(player, ModItems.SPACE_HELMET, ModItems.SPACE_SUIT, ModItems.SPACE_PANTS, ModItems.SPACE_BOOTS)
            || isWearingSet(player, ModItems.NETHERITE_SPACE_HELMET, ModItems.NETHERITE_SPACE_SUIT, ModItems.NETHERITE_SPACE_PANTS, ModItems.NETHERITE_SPACE_BOOTS)
            || isWearingSet(player, ModItems.JET_SUIT_HELMET, ModItems.JET_SUIT, ModItems.JET_SUIT_PANTS, ModItems.JET_SUIT_BOOTS);
    }

    private static boolean isWearingSet(EntityPlayer player, net.minecraft.item.Item helmet, net.minecraft.item.Item chest, net.minecraft.item.Item legs, net.minecraft.item.Item boots) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == helmet
            && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == chest
            && player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == legs
            && player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == boots;
    }

    private static InventoryTotals collectInventoryTotals(EntityPlayer player) {
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (chest.isEmpty()) {
            return new InventoryTotals(0, 0, 0, 0);
        }

        OxygenTotals oxygenTotals = getOxygenTotals(chest);
        long energy = 0;
        long energyCapacity = 0;
        if (chest.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = chest.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null && storage.getMaxEnergyStored() > 0) {
                energy = Math.max(0, storage.getEnergyStored());
                energyCapacity = storage.getMaxEnergyStored();
            }
        }

        return new InventoryTotals(oxygenTotals.amount, oxygenTotals.capacity, energy, energyCapacity);
    }

    private static OxygenTotals getOxygenTotals(ItemStack stack) {
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack.copy());
        if (handler == null) {
            return OxygenTotals.EMPTY;
        }

        long amount = 0;
        long capacity = 0;
        boolean knownOxygenContainer = stack.getItem() instanceof GasTankItem
            || stack.getItem() instanceof AdAstraArmorItem;
        for (IFluidTankProperties property : handler.getTankProperties()) {
            FluidStack contents = property.getContents();
            boolean containsOxygen = contents != null && contents.getFluid() == ModFluids.OXYGEN;
            if (containsOxygen) {
                amount += Math.max(0, contents.amount);
            }
            if (knownOxygenContainer || containsOxygen) {
                capacity += Math.max(0, property.getCapacity());
            }
        }

        return capacity > 0 ? new OxygenTotals(amount, capacity) : OxygenTotals.EMPTY;
    }

    private static final class HudData {

        private final HudRow oxygenRow;
        private final HudRow temperatureRow;
        private final HudRow gravityRow;
        private final HudRow energyRow;
        private final boolean hasWarning;

        private HudData(HudRow oxygenRow, HudRow temperatureRow, HudRow gravityRow, HudRow energyRow, boolean hasWarning) {
            this.oxygenRow = oxygenRow;
            this.temperatureRow = temperatureRow;
            this.gravityRow = gravityRow;
            this.energyRow = energyRow;
            this.hasWarning = hasWarning;
        }

        private static HudData from(EntityPlayer player, EnvironmentSnapshot environment) {
            InventoryTotals totals = collectInventoryTotals(player);
            boolean protectedBySuit = hasTemperatureProtection(player);

            String oxygenValue = totals.oxygenCapacity > 0 ? percent(totals.oxygen, totals.oxygenCapacity)
                : environment.oxygen ? I18n.format("hud.ad_astra.local") : I18n.format("hud.ad_astra.none");
            double oxygenRatio = totals.oxygenCapacity > 0 ? ratio(totals.oxygen, totals.oxygenCapacity) : environment.oxygen ? 1.0d : 0.0d;

            // Low oxygen warning detection
            boolean lowOxygen = !environment.oxygen && totals.oxygenCapacity > 0 && oxygenRatio < LOW_OXYGEN_THRESHOLD;
            boolean noOxygen = !environment.oxygen && totals.oxygenCapacity > 0 && totals.oxygen <= 0;

            // 浣庢哀璀﹀憡锛氭哀姘斿閲?0涓旀哀姘斺墹0鏃舵樉绀虹孩鑹诧紝浣庝簬20%鏃舵樉绀烘鑹茶鍛婏紝鍚﹀垯鏄剧ず钃濊壊
            int oxygenColor;
            if (environment.oxygen) {
                oxygenColor = 0x55FF55;
            } else if (totals.oxygenCapacity > 0) {
                if (totals.oxygen <= 0) {
                    oxygenColor = 0xE53253;
                } else if (oxygenRatio < 0.2d) {
                    oxygenColor = 0xFF9944;
                } else {
                    oxygenColor = 0x99CCFF;
                }
            } else {
                oxygenColor = 0x99CCFF;
            }

            HudRow oxygenRow = new HudRow(
                I18n.format("hud.ad_astra.oxygen"),
                oxygenValue,
                oxygenRatio,
                0xDCEFFF,
                oxygenColor,
                oxygenColor,
                noOxygen || lowOxygen);

            // Extreme temperature warning
            boolean extremeTemp = environment.hasTemperature && !protectedBySuit &&
                (environment.temperature < EXTREME_TEMP_LOW || environment.temperature > EXTREME_TEMP_HIGH);

            String temperatureValue = environment.hasTemperature ? I18n.format("text.ad_astra.temperature", String.valueOf(environment.temperature))
                : protectedBySuit ? I18n.format("hud.ad_astra.suit") : I18n.format("hud.ad_astra.pending");
            int temperatureColor = environment.hasTemperature ? temperatureColor(environment.temperature)
                : protectedBySuit ? 0xFFB35C : 0xAAAAAA;
            HudRow temperatureRow = new HudRow(
                I18n.format("hud.ad_astra.temperature"),
                temperatureValue,
                environment.hasTemperature ? temperatureRatio(environment.temperature) : protectedBySuit ? 1.0d : 0.0d,
                0xFFE6D4,
                temperatureColor,
                temperatureColor,
                extremeTemp);

            HudRow gravityRow = new HudRow(
                I18n.format("hud.ad_astra.gravity"),
                I18n.format("text.ad_astra.gravity", oneDecimal(environment.gravityValue)),
                Math.max(0.0d, Math.min(1.0d, environment.gravityValue / 19.6d)),
                0xE7E0FF,
                environment.hasGravity ? 0xBA8CFF : 0x88AAFF,
                environment.hasGravity ? 0xBA8CFF : 0xDDDDDD,
                false);

            String energyValue = totals.energyCapacity > 0 ? percent(totals.energy, totals.energyCapacity) : I18n.format("hud.ad_astra.none");
            int energyColor = totals.energyCapacity > 0 && totals.energy <= 0 ? 0xE53253 : 0x55FFFF;
            HudRow energyRow = new HudRow(
                I18n.format("hud.ad_astra.energy"),
                energyValue,
                ratio(totals.energy, totals.energyCapacity),
                0xDAFFFF,
                energyColor,
                totals.energyCapacity > 0 ? energyColor : 0xAAAAAA,
                false);

            boolean hasWarning = noOxygen || lowOxygen || extremeTemp;

            return new HudData(oxygenRow, temperatureRow, gravityRow, energyRow, hasWarning);
        }
    }

    private static final class HudRow {

        private final String label;
        private final String value;
        private final double ratio;
        private final int labelColor;
        private final int barColor;
        private final int valueColor;
        private final boolean warning;

        private HudRow(String label, String value, double ratio, int labelColor, int barColor, int valueColor, boolean warning) {
            this.label = label;
            this.value = value;
            this.ratio = ratio;
            this.labelColor = labelColor;
            this.barColor = barColor;
            this.valueColor = valueColor;
            this.warning = warning;
        }
    }

    private static final class InventoryTotals {

        private final long oxygen;
        private final long oxygenCapacity;
        private final long energy;
        private final long energyCapacity;

        private InventoryTotals(long oxygen, long oxygenCapacity, long energy, long energyCapacity) {
            this.oxygen = oxygen;
            this.oxygenCapacity = oxygenCapacity;
            this.energy = energy;
            this.energyCapacity = energyCapacity;
        }
    }

    private static final class OxygenTotals {

        private static final OxygenTotals EMPTY = new OxygenTotals(0, 0);

        private final long amount;
        private final long capacity;

        private OxygenTotals(long amount, long capacity) {
            this.amount = amount;
            this.capacity = capacity;
        }
    }

    private static final class EnvironmentSnapshot {

        private static final EnvironmentSnapshot EMPTY = new EnvironmentSnapshot(true, false, 9.8f, false, 0);

        private final boolean oxygen;
        private final boolean hasGravity;
        private final float gravityValue;
        private final boolean hasTemperature;
        private final int temperature;

        private EnvironmentSnapshot(boolean oxygen, boolean hasGravity, float gravityValue, boolean hasTemperature, int temperature) {
            this.oxygen = oxygen;
            this.hasGravity = hasGravity;
            this.gravityValue = gravityValue;
            this.hasTemperature = hasTemperature;
            this.temperature = temperature;
        }
    }

    private static final class WorldEnvironment {

        private final boolean oxygen;
        private final boolean hasGravity;
        private final float gravity;
        private final boolean hasTemperature;
        private final int temperature;

        private WorldEnvironment(boolean oxygen, boolean hasGravity, float gravity, boolean hasTemperature, int temperature) {
            this.oxygen = oxygen;
            this.hasGravity = hasGravity;
            this.gravity = gravity;
            this.hasTemperature = hasTemperature;
            this.temperature = temperature;
        }

        private static WorldEnvironment from(World world) {
            PlanetData localData = ClientData.getLocalData();
            if (localData != null) {
                return new WorldEnvironment(
                    localData.oxygen(),
                    true,
                    localData.gravity() * 9.8f,
                    true,
                    localData.temperature());
            }

            Object provider = world.provider;
            boolean oxygen = invokeBoolean(provider, "hasOxygen", true);
            Number gravity = invokeNumber(provider, "getGravity");
            Number temperature = invokeNumber(provider, "getTemperature");
            return new WorldEnvironment(
                oxygen,
                gravity != null,
                gravity == null ? 9.8f : gravity.floatValue(),
                temperature != null,
                temperature == null ? 0 : temperature.intValue());
        }

        private static boolean invokeBoolean(Object target, String methodName, boolean fallback) {
            Object value = invokeNoArg(target, methodName);
            return value instanceof Boolean ? (Boolean) value : fallback;
        }

        private static Number invokeNumber(Object target, String methodName) {
            Object value = invokeNoArg(target, methodName);
            return value instanceof Number ? (Number) value : null;
        }

        private static Object invokeNoArg(Object target, String methodName) {
            if (target == null) {
                return null;
            }
            try {
                return target.getClass().getMethod(methodName).invoke(target);
            } catch (ReflectiveOperationException | SecurityException e) {
                return null;
            }
        }
    }
}
