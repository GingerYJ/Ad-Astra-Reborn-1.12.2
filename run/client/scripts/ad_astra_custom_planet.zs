#loader crafttweaker

import mods.ad_astra.CustomPlanets;
import mods.ad_astra.SpaceStation;

// ============================================================
// Custom Planet: Mineral World (øÛŒÔ ¿ΩÁ)
// A planet rich in ores, with lava lakes and extreme gravity.
// ============================================================

CustomPlanets.create("ad_astra:mineral_world", 1301)
    .name("mineral_world")
    .displayName("Mineral World")
    .tier(3)
    .biome("minecraft:desert")
    .surface(<block:minecraft:iron_block>)
    .stone(<block:minecraft:iron_ore>)
    .icon(<minecraft:diamond_block>)
    .environment(false, 80, 1.5, 32)
    .colors(0.55, 0.35, 0.15, 0.75, 0.55, 0.25)
    // Iron ore veins (very common, replaces stone)
    .addOre(<block:minecraft:iron_ore>, <block:minecraft:stone>, 12, 25, 0, 128)
    // Gold ore veins
    .addOre(<block:minecraft:gold_ore>, <block:minecraft:stone>, 6, 12, 0, 64)
    // Diamond ore veins
    .addOre(<block:minecraft:diamond_ore>, <block:minecraft:stone>, 4, 8, 0, 32)
    // Redstone ore veins
    .addOre(<block:minecraft:redstone_ore>, <block:minecraft:stone>, 8, 15, 0, 48)
    // Coal ore veins (surface level)
    .addOre(<block:minecraft:coal_ore>, <block:minecraft:stone>, 10, 20, 32, 128)
    // Lapis lazuli ore veins
    .addOre(<block:minecraft:lapis_ore>, <block:minecraft:stone>, 5, 10, 0, 48)
    // Emerald ore veins (rare)
    .addOre(<block:minecraft:emerald_ore>, <block:minecraft:stone>, 3, 4, 0, 32)
    // Iron block veins (very rare, treasure clusters)
    .addOre(<block:minecraft:iron_block>, <block:minecraft:stone>, 4, 2, 0, 24)
    // Gold block veins (extremely rare)
    .addOre(<block:minecraft:gold_block>, <block:minecraft:stone>, 2, 1, 0, 16)
    // Diamond block veins (legendary)
    .addOre(<block:minecraft:diamond_block>, <block:minecraft:stone>, 1, 1, 0, 12)
    // Lava lakes (common, surface to deep)
    .addFluidLake(<liquid:lava> * 1000, 8, 8, 64)
    .enableDimensionRegistration(true)
    .register();

// ============================================================
// Space Station Recipe for Mineral World Orbit
// ============================================================

SpaceStation.setRecipe("mineral_world_orbit",
    [
        <minecraft:iron_block>,
        <minecraft:gold_block>,
        <minecraft:diamond_block>,
        <minecraft:obsidian>,
        <minecraft:glowstone>
    ],
    [64, 32, 16, 64, 32]);

print("Ad Astra: Mineral World custom planet and space station loaded.");