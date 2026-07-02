# Ad Astra Reborn

Ad Astra Reborn is a Minecraft 1.12.2 / Cleanroom port of Ad Astra. The goal of the project is to bring the Ad Astra 1.20.x space-exploration content back to the 1.12.2 ecosystem while reusing the original assets for blocks, items, models, GUIs, vehicles and planets wherever possible.

[中文说明](README_zh_cn.md)

## Status

This repository is an active port and gameplay reconstruction, not a finished release. Rockets, launch pads, machines, space stations, planet travel, environment systems and CraftTweaker hooks are implemented, but many behaviors still need in-game validation on real modpacks.

Author and maintainer: GingerYJ

Credits: original Ad Astra content by Terrarium and Ad Astra contributors.

## Features

- Tiered rockets, launch pads, landers and rover gameplay.
- Planet travel for the Moon, Mars, Mercury, Venus and Glacio, plus optional Nether and End travel through rockets.
- Orbit and space station gameplay with configurable construction materials.
- Space environment systems for oxygen, temperature and gravity.
- Machines and GUIs such as the NASA Workbench, compressor, fuel refinery, oxygen loader, oxygen distributor, gravity normalizer, energizer, cryo freezer, coal generator, solar panel, water pump and etrionic blast furnace.
- HEI recipe displays for Ad Astra machine recipes.
- Patchouli guide book integration.
- CraftTweaker APIs for NASA Workbench recipes, rocket fuel tiers, planet rocket tiers, space station recipes and custom planet definitions.

## Requirements

- Minecraft 1.12.2 with Cleanroom Loader / Forge-compatible 1.12.2 runtime.
- Java toolchain configured by Gradle. The project currently uses Java 25 for the Gradle toolchain and Java 21 source/target compatibility.
- Patchouli is required at runtime.
- Had Enough Items is used for recipe integration.
- CraftTweaker is optional at runtime, but required when using the provided scripts and ZenScript APIs.

## Build And Run

```powershell
.\gradlew.bat compileJava
.\gradlew.bat build
.\gradlew.bat runClient
```

The generated mod jar is produced under `build/libs`.

The default development CraftTweaker script is stored at:

```text
run/client/scripts/ad_astra_defaults.zs
```

## Configuration

The generated config file is placed in its own folder:

```text
config/ad_astra/ad_astra.cfg
```

Important options include oxygen, temperature, gravity, machine speed and energy multipliers, planet dimension toggles, Nether/End rocket travel, world generation and per-entity spawn caps for planet dimensions.

## CraftTweaker APIs

Use these imports in ZenScript:

```zenscript
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
import mods.ad_astra.CustomPlanets;
```

### NASA Workbench

```zenscript
NASAWorkbench.addRecipe(String id, IItemStack[] inputs, IItemStack output, int width, int height, int time, int energy);
NASAWorkbench.removeRecipe(String id);
NASAWorkbench.removeByOutput(IItemStack output);
```

Example:

```zenscript
NASAWorkbench.addRecipe("ad_astra:tier_5_rocket_from_crt",
    [
        <ad_astra:rocket_nose_cone>,
        <ad_astra:etrium_block>,
        <ad_astra:etrium_block>,
        <ad_astra:etrium_block>,
        <ad_astra:etrium_block>,
        <ad_astra:etrium_block>,
        <minecraft:beacon>,
        <ad_astra:rocket_fin>,
        <ad_astra:calorite_tank>,
        <ad_astra:calorite_tank>,
        <ad_astra:rocket_fin>,
        <ad_astra:rocket_fin>,
        <ad_astra:calorite_engine>,
        <ad_astra:rocket_fin>
    ],
    <ad_astra:tier_5_rocket>,
    3,
    5,
    200,
    10);
```

### Rocket Fuel

```zenscript
RocketFuel.addFuel(String fluidName, int fuelTier);
RocketFuel.removeFuel(String fluidName);
```

Higher-tier fuel can power lower-tier rockets. Lower-tier fuel cannot power higher-tier rockets.

Example:

```zenscript
RocketFuel.addFuel("lava", 3);
```

### Planet Rocket Tiers

```zenscript
PlanetTiers.setPlanetTier(int dimensionId, int tier);
PlanetTiers.removePlanetTier(int dimensionId);
```

Example:

```zenscript
PlanetTiers.setPlanetTier(1201, 1); // Moon
```

### Space Stations

```zenscript
SpaceStation.setRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.setRecipe(String orbit, IIngredient[] ingredients);
SpaceStation.replaceRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.replaceRecipe(String orbit, IIngredient[] ingredients);
SpaceStation.addRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.addRecipe(String orbit, IIngredient[] ingredients);
SpaceStation.removeRecipe(String orbit);
SpaceStation.removeRecipeById(String id);
```

Example:

```zenscript
SpaceStation.setRecipe("nether_orbit",
    [
        <minecraft:obsidian>,
        <minecraft:glowstone>,
        <minecraft:quartz_block>,
        <minecraft:iron_block>
    ],
    [64, 32, 32, 16]);
```

### Custom Planets

```zenscript
CustomPlanets.create(String id, int dimensionId);
CustomPlanets.getRegisteredCount();
CustomPlanets.hasPlanet(String id);
```

The builder returned by `CustomPlanets.create` supports:

```zenscript
.name(String name)
.displayName(String displayName)
.saveFolder(String saveFolder)
.biome(String biomeId)
.surface(IBlock block)
.stone(IBlock block)
.icon(IItemStack stack)
.iconBlock(IBlock block)
.skyLight(boolean hasSkyLight)
.canRespawn(boolean canRespawn)
.environment(boolean oxygen, int temperature, double gravity, int solarPower)
.tier(int tier)
.dayLength(int dayLength)
.colors(double fogRed, double fogGreen, double fogBlue, double skyRed, double skyGreen, double skyBlue)
.addOre(IBlock oreBlock, IBlock replaceBlock, int veinSize, int countPerChunk, int minY, int maxY)
.addFluidLake(ILiquidStack fluidStack, int countPerChunk, int minY, int maxY)
.addFluidBlock(IBlock fluidBlock, int countPerChunk, int minY, int maxY)
.enableDimensionRegistration(boolean enabled)
.register()
```

Template:

```zenscript
CustomPlanets.create("example:basalt_moon", 1301)
    .displayName("Basalt Moon")
    .tier(2)
    .biome("minecraft:desert")
    .surface(<block:minecraft:stone>)
    .stone(<block:minecraft:stone>)
    .icon(<minecraft:obsidian>)
    .environment(false, -40, 0.42, 18)
    .addOre(<block:minecraft:iron_ore>, <block:minecraft:stone>, 6, 6, 4, 48)
    .addFluidLake(<liquid:lava> * 1000, 1, 8, 32)
    .register();
```

See `docs/custom_planets_crt_template.zs` for a larger example.
