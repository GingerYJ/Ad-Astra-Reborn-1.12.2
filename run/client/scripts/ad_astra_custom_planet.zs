#loader crafttweaker

import mods.ad_astra.CustomPlanets;
import mods.ad_astra.SpaceStation;

// Custom Planet: Mineral World

var ironBlock = <minecraft:iron_block>;
var ironOre = <minecraft:iron_ore>;
var goldOre = <minecraft:gold_ore>;
var diamondOre = <minecraft:diamond_ore>;
var redstoneOre = <minecraft:redstone_ore>;
var coalOre = <minecraft:coal_ore>;
var lapisOre = <minecraft:lapis_ore>;
var emeraldOre = <minecraft:emerald_ore>;
var goldBlock = <minecraft:gold_block>;
var diamondBlock = <minecraft:diamond_block>;
var stone = <minecraft:stone>;

CustomPlanets.create("ad_astra:mineral_world", 1301)
    .name("mineral_world")
    .displayName("Mineral World")
    .tier(3)
    .biome("minecraft:desert")
    .surface(ironBlock)
    .stone(ironOre)
    .icon(<minecraft:diamond_block>)
    .environment(false, 80, 1.5, 32)
    .colors(0.55, 0.35, 0.15, 0.75, 0.55, 0.25)
    .addOre(ironOre, stone, 12, 25, 0, 128)
    .addOre(goldOre, stone, 6, 12, 0, 64)
    .addOre(diamondOre, stone, 4, 8, 0, 32)
    .addOre(redstoneOre, stone, 8, 15, 0, 48)
    .addOre(coalOre, stone, 10, 20, 32, 128)
    .addOre(lapisOre, stone, 5, 10, 0, 48)
    .addOre(emeraldOre, stone, 3, 4, 0, 32)
    .addOre(ironBlock, stone, 4, 2, 0, 24)
    .addOre(goldBlock, stone, 2, 1, 0, 16)
    .addOre(diamondBlock, stone, 1, 1, 0, 12)
    .enableDimensionRegistration(true)
    .register();

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

