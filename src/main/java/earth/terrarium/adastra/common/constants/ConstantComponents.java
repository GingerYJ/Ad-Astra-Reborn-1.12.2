package earth.terrarium.adastra.common.constants;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

/**
 * Centralized UI constant components ported from Ad Astra 1.20.x.
 * Uses 1.12.2 TextComponentTranslation instead of 1.20's Component.translatable().
 */
public class ConstantComponents {

    // === Key bindings ===
    public static final ITextComponent TOGGLE_SUIT_FLIGHT_KEY = new TextComponentTranslation("key.ad_astra.toggle_suit_flight");
    public static final ITextComponent OPEN_RADIO_KEY = new TextComponentTranslation("key.ad_astra.open_radio");
    public static final ITextComponent AD_ASTRA_CATEGORY = new TextComponentTranslation("key.categories.adastra");

    // === Suit flight messages ===
    public static final ITextComponent SUIT_FLIGHT_ENABLED = styled("message.ad_astra.suit_flight_enabled", TextFormatting.GOLD);
    public static final ITextComponent SUIT_FLIGHT_DISABLED = styled("message.ad_astra.suit_flight_disabled", TextFormatting.GOLD);

    // === Oxygen display ===
    public static final ITextComponent TRUE = new TextComponentTranslation("text.ad_astra.oxygen_true");
    public static final ITextComponent FALSE = new TextComponentTranslation("text.ad_astra.oxygen_false");

    // === Side config tooltips ===
    public static final ITextComponent SIDE_CONFIG = styled("tooltip.ad_astra.side_config", TextFormatting.GRAY);
    public static final ITextComponent REDSTONE_CONTROL = styled("tooltip.ad_astra.redstone_control", TextFormatting.RED);
    public static final ITextComponent ETRIONIC_BLAST_FURNACE_MODE = styled("tooltip.ad_astra.etrionic_blast_furnace.furnace_mode", TextFormatting.AQUA);

    // === Active / Inactive ===
    public static final ITextComponent ACTIVE = new TextComponentTranslation("tooltip.ad_astra.active");
    public static final ITextComponent INACTIVE = new TextComponentTranslation("tooltip.ad_astra.inactive");

    // === Distribution modes ===
    public static final ITextComponent SEQUENTIAL = new TextComponentTranslation("tooltip.ad_astra.distribution_mode.sequential");
    public static final ITextComponent ROUND_ROBIN = new TextComponentTranslation("tooltip.ad_astra.distribution_mode.round_robin");

    // === Capacitor ===
    public static final ITextComponent CAPACITOR_ENABLED = new TextComponentTranslation("tooltip.ad_astra.capacitor.enabled");
    public static final ITextComponent CAPACITOR_DISABLED = new TextComponentTranslation("tooltip.ad_astra.capacitor.disabled");

    // === Mode change ===
    public static final ITextComponent CHANGE_MODE_SEQUENTIAL = new TextComponentTranslation("tooltip.ad_astra.change_mode.sequential");
    public static final ITextComponent CHANGE_MODE_ROUND_ROBIN = new TextComponentTranslation("tooltip.ad_astra.change_mode.round_robin");

    // === Shift description ===
    public static final ITextComponent SHIFT_DESCRIPTION = styled("tooltip.ad_astra.shift_description", TextFormatting.GRAY);

    // === Item info tooltips ===
    public static final ITextComponent TI_69_INFO = styled("info.ad_astra.ti_69", TextFormatting.GRAY);
    public static final ITextComponent ETRIONIC_CAPACITOR_INFO = styled("info.ad_astra.etrionic_capacitor", TextFormatting.GRAY);
    public static final ITextComponent WRENCH_INFO = styled("info.ad_astra.wrench", TextFormatting.GRAY);
    public static final ITextComponent ZIP_GUN_INFO = styled("info.ad_astra.zip_gun", TextFormatting.GRAY);
    public static final ITextComponent GAS_TANK_INFO = styled("info.ad_astra.gas_tank", TextFormatting.GRAY);
    public static final ITextComponent CABLE_INFO = styled("info.ad_astra.cable", TextFormatting.GRAY);
    public static final ITextComponent FLUID_PIPE_INFO = styled("info.ad_astra.fluid_pipe", TextFormatting.GRAY);
    public static final ITextComponent CABLE_DUCT = styled("info.ad_astra.cable_duct", TextFormatting.GRAY);
    public static final ITextComponent FLUID_DUCT_INFO = styled("info.ad_astra.fluid_duct", TextFormatting.GRAY);
    public static final ITextComponent SPACE_SUIT_INFO = styled("info.ad_astra.space_suit", TextFormatting.GRAY);
    public static final ITextComponent NETHERITE_SPACE_SUIT_INFO = styled("info.ad_astra.netherite_space_suit", TextFormatting.GRAY);
    public static final ITextComponent JET_SUIT_INFO = styled("info.ad_astra.jet_suit", TextFormatting.GRAY);
    public static final ITextComponent SLIDING_DOOR_INFO = styled("info.ad_astra.sliding_door", TextFormatting.GRAY);
    public static final ITextComponent FLAG_INFO = styled("info.ad_astra.flag", TextFormatting.GRAY);
    public static final ITextComponent LAUNCH_PAD_INFO = styled("info.ad_astra.launch_pad", TextFormatting.GRAY);
    public static final ITextComponent GLOBE_INFO = styled("info.ad_astra.globe", TextFormatting.GRAY);
    public static final ITextComponent VENT_INFO = styled("info.ad_astra.vent", TextFormatting.GRAY);
    public static final ITextComponent RADIO_INFO = styled("info.ad_astra.radio", TextFormatting.GRAY);

    // === Vehicle info ===
    public static final ITextComponent ROCKET_INFO = styled("info.ad_astra.rocket", TextFormatting.GRAY);
    public static final ITextComponent ROVER_INFO = styled("info.ad_astra.rover", TextFormatting.GRAY);
    public static final ITextComponent LANDER_INFO = styled("info.ad_astra.lander", TextFormatting.GRAY);

    // === Machine info ===
    public static final ITextComponent COMPRESSOR_INFO = styled("info.ad_astra.compressor", TextFormatting.GRAY);
    public static final ITextComponent ETRIONIC_BLAST_FURNACE_INFO = styled("info.ad_astra.etrionic_blast_furnace", TextFormatting.GRAY);
    public static final ITextComponent FUEL_REFINERY_INFO = styled("info.ad_astra.fuel_refinery", TextFormatting.GRAY);
    public static final ITextComponent OXYGEN_LOADER_INFO = styled("info.ad_astra.oxygen_loader", TextFormatting.GRAY);
    public static final ITextComponent OXYGEN_DISTRIBUTOR_INFO = styled("info.ad_astra.oxygen_distributor", TextFormatting.GRAY);
    public static final ITextComponent SOLAR_PANEL_INFO = styled("info.ad_astra.solar_panel", TextFormatting.GRAY);
    public static final ITextComponent WATER_PUMP_INFO = styled("info.ad_astra.water_pump", TextFormatting.GRAY);
    public static final ITextComponent NASA_WORKBENCH_INFO = styled("info.ad_astra.nasa_workbench", TextFormatting.GRAY);
    public static final ITextComponent GRAVITY_NORMALIZER_INFO = styled("info.ad_astra.gravity_normalizer", TextFormatting.GRAY);
    public static final ITextComponent ENERGIZER_INFO = styled("info.ad_astra.energizer", TextFormatting.GRAY);
    public static final ITextComponent CRYO_FREEZER_INFO = styled("info.ad_astra.cryo_freezer", TextFormatting.GRAY);
    public static final ITextComponent OXYGEN_SENSOR_INFO = styled("info.ad_astra.oxygen_sensor", TextFormatting.GRAY);

    // === Pipe modes ===
    public static final ITextComponent PIPE_NORMAL = new TextComponentTranslation("tooltip.ad_astra.pipe.normal");
    public static final ITextComponent PIPE_INSERT = new TextComponentTranslation("tooltip.ad_astra.pipe.insert");
    public static final ITextComponent PIPE_EXTRACT = new TextComponentTranslation("tooltip.ad_astra.pipe.extract");
    public static final ITextComponent PIPE_NONE = new TextComponentTranslation("tooltip.ad_astra.pipe.none");

    // === Door states ===
    public static final ITextComponent DOOR_LOCKED = new TextComponentTranslation("tooltip.ad_astra.door.locked");
    public static final ITextComponent DOOR_UNLOCKED = new TextComponentTranslation("tooltip.ad_astra.door.unlocked");

    // === Fluid tank ===
    public static final ITextComponent CLEAR_FLUID_TANK = styled("tooltip.ad_astra.clear_fluid_tank", TextFormatting.RED);

    // === Navigation ===
    public static final ITextComponent NEXT = styled("tooltip.ad_astra.next", TextFormatting.GRAY);
    public static final ITextComponent PREVIOUS = styled("tooltip.ad_astra.previous", TextFormatting.GRAY);
    public static final ITextComponent RESET_TO_DEFAULT = styled("tooltip.ad_astra.reset_to_default", TextFormatting.RED);

    // === Side config labels ===
    public static final ITextComponent SIDE_CONFIG_SLOTS = new TextComponentTranslation("side_config.ad_astra.slots");
    public static final ITextComponent SIDE_CONFIG_ENERGY = new TextComponentTranslation("side_config.ad_astra.energy");
    public static final ITextComponent SIDE_CONFIG_FLUID = new TextComponentTranslation("side_config.ad_astra.fluid");
    public static final ITextComponent SIDE_CONFIG_INPUT_SLOTS = new TextComponentTranslation("side_config.ad_astra.input_slots");
    public static final ITextComponent SIDE_CONFIG_OUTPUT_SLOTS = new TextComponentTranslation("side_config.ad_astra.output_slots");
    public static final ITextComponent SIDE_CONFIG_EXTRACTION_SLOTS = new TextComponentTranslation("side_config.ad_astra.extraction_slots");
    public static final ITextComponent SIDE_CONFIG_INPUT_FLUID = new TextComponentTranslation("side_config.ad_astra.input_fluid");
    public static final ITextComponent SIDE_CONFIG_OUTPUT_FLUID = new TextComponentTranslation("side_config.ad_astra.output_fluid");

    // === Detector states ===
    public static final ITextComponent DETECTOR_INVERTED_TRUE = new TextComponentTranslation("text.ad_astra.detector.inverted_true");
    public static final ITextComponent DETECTOR_INVERTED_FALSE = new TextComponentTranslation("text.ad_astra.detector.inverted_false");
    public static final ITextComponent DETECTOR_OXYGEN_MODE = new TextComponentTranslation("text.ad_astra.detector.oxygen_mode");
    public static final ITextComponent DETECTOR_GRAVITY_MODE = new TextComponentTranslation("text.ad_astra.detector.gravity_mode");
    public static final ITextComponent DETECTOR_TEMPERATURE_MODE = new TextComponentTranslation("text.ad_astra.detector.temperature_mode");

    // === Distribution area ===
    public static final ITextComponent OXYGEN_DISTRIBUTION_AREA = styled("tooltip.ad_astra.oxygen_distribution_area", TextFormatting.AQUA);
    public static final ITextComponent GRAVITY_DISTRIBUTION_AREA = styled("tooltip.ad_astra.gravity_distribution_area", TextFormatting.AQUA);

    // === Flag URL ===
    public static final ITextComponent FLAG_URL = new TextComponentTranslation("text.ad_astra.text.flag_url");
    public static final ITextComponent CONFIRM = new TextComponentTranslation("text.ad_astra.text.confirm");
    public static final ITextComponent NOT_THE_OWNER = styled("message.ad_astra.flag.not_owner", TextFormatting.RED);

    // === Launch messages ===
    public static final ITextComponent NOT_ENOUGH_FUEL = styled("message.ad_astra.not_enough_fuel", TextFormatting.RED);
    public static final ITextComponent INVALID_LAUNCHING_DIMENSION = styled("message.ad_astra.invalid_launching_dimension", TextFormatting.RED);

    // === Planet selection ===
    public static final ITextComponent CATALOG = new TextComponentTranslation("text.ad_astra.text.catalog");
    public static final ITextComponent LAND = new TextComponentTranslation("text.ad_astra.text.land");
    public static final ITextComponent SPACE_STATION = new TextComponentTranslation("text.ad_astra.text.space_station");
    public static final ITextComponent CONSTRUCT = new TextComponentTranslation("text.ad_astra.text.construct_space_station");
    public static final ITextComponent SPACE_STATION_ALREADY_EXISTS = styled("text.ad_astra.space_station.already_exists", TextFormatting.RED);
    public static final ITextComponent CONSTRUCTION_COST = new TextComponentTranslation("tooltip.ad_astra.construction_cost");

    // === Helper: creates a styled translatable text component ===
    private static ITextComponent styled(String key, TextFormatting formatting) {
        ITextComponent component = new TextComponentTranslation(key);
        component.getStyle().setColor(formatting);
        return component;
    }
}