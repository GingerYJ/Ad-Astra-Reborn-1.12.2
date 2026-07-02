#loader crafttweaker

import mods.ad_astra.CustomPlanets;

// First-pass template: this stores Ad Astra custom planet data for later Java/UI/worldgen hooks.
// It does not create a live Minecraft dimension by itself in the current implementation.

CustomPlanets.create("example:basalt_moon", 1301)
    .name("basalt_moon")
    .displayName("Basalt Moon")
    .tier(2)
    .biome("minecraft:desert")
    .surface(<block:minecraft:stone>)
    .stone(<block:minecraft:stone>)
    .icon(<minecraft:obsidian>)
    .environment(false, -40, 0.42, 18)
    .colors(0.02, 0.02, 0.03, 0.18, 0.20, 0.28)
    .addOre(<block:minecraft:coal_ore>, <block:minecraft:stone>, 8, 10, 8, 64)
    .addOre(<block:minecraft:iron_ore>, <block:minecraft:stone>, 6, 6, 4, 48)
    .addFluidLake(<liquid:lava> * 1000, 1, 8, 32)
    .enableDimensionRegistration(false)
    .register();
