# Ad Astra Reborn 1.0.0

Ad Astra Reborn is a Minecraft 1.12.2 / Cleanroom-compatible space exploration mod inspired by Ad Astra and expanded for the 1.12.2 ecosystem. Version **1.0.0** focuses on a complete playable space progression: tiered rockets, many planets and orbits, configurable travel requirements, space stations, planetary resources, machine recipes, HEI/JEI displays and CraftTweaker scripting hooks.

[中文说明](README_zh_cn.md)

## What This Mod Adds

### Space travel and rockets

- Tiered rockets, launch pads, landers and rover gameplay.
- Rocket travel UI rewritten as a dynamic star map with orbit display, animated planets, zoom/drag controls and optional planet names.
- Rocket destination availability is based on rocket tier and can be changed through configuration.
- Rockets expose HEI/JEI usage information: pressing the usage key on a rocket shows reachable and unreachable destinations.
- Custom configurable rockets can be added from config without writing Java code.

### Built-in planets and orbits

The mod contains the original Ad Astra-style destinations and many integrated additional celestial bodies:

- Moon and Moon Orbit
- Mars and Mars Orbit
- Mercury and Mercury Orbit
- Venus and Venus Orbit
- Glacio and Glacio Orbit
- Nether Orbit and End Orbit, when enabled
- Ceres and Ceres Orbit
- Pluto and Pluto Orbit
- Haumea and Haumea Orbit
- Kuiper Belt
- Jupiter Orbit
- Io and Io Orbit
- Europa and Europa Orbit
- Ganymede and Ganymede Orbit
- Callisto and Callisto Orbit
- Enceladus and Enceladus Orbit
- Titan and Titan Orbit
- Miranda and Miranda Orbit
- Triton and Triton Orbit
- Phobos and Phobos Orbit
- Barnarda C and Barnarda C Orbit
- Barnarda C1 and Barnarda C1 Orbit
- Tau Ceti F and Tau Ceti F Orbit
- Proxima B and Proxima B Orbit

Each destination can have its own dimension ID, gravity, travel tier, world-generation settings, mob rules and orbit/space-station behavior.

### Planet resources and world generation

- Planet-specific terrain blocks, stones, regoliths, ice crusts, geysers, crystals and decorative blocks.
- Dedicated ore and metal resource chains for many planets.
- Ore blocks drop their matching raw material.
- Ore and raw material smelting recipes are provided.
- Ingot-to-metal-block and metal-block-to-ingot recipes are provided.
- Planet resource acquisition is visible in HEI/JEI, so players can inspect where a resource is found.
- Ore generation is configurable per planet and separated into clear config categories.
- Custom block/ore generation rules can be added to specific planets through config.

### Machines and progression

Implemented machine and progression systems include:

- NASA Workbench
- Compressor
- Fuel Refinery
- Oxygen Loader
- Oxygen Distributor
- Gravity Normalizer
- Energizer
- Cryo Freezer
- Coal Generator
- Solar Panel
- Water Pump
- Etrionic Blast Furnace
- Space station construction recipes
- HEI/JEI recipe categories for machine recipes and space exploration information

### Environment systems

- Oxygen handling
- Temperature handling
- Gravity scaling per dimension
- Solar power scaling per dimension
- Planet mob spawn controls
- Dedicated dimension save folder support for Ad Astra dimensions

## Configuration

Configuration files are generated under:

```text
config/ad_astra/
```

The large all-in-one config has been split into smaller files:

- `core.cfg` - common gameplay toggles and base settings
- `client.cfg` - client display and UI options
- `machines.cfg` - machine speed, energy and recipe behavior
- `dimensions.cfg` - dimension IDs, enable toggles, gravity and planet rocket tiers
- `mobs.cfg` - per-planet mob spawn configuration
- `worldgen.cfg` - structures, ore generation, custom planet blocks and per-planet ore settings
- `debug.cfg` - debug and diagnostic switches
- `rockets.cfg` - extra configurable rockets

Most config entries contain Chinese comments for pack makers and server owners.

## Configurable Rockets

Extra rockets can be added through:

```text
config/ad_astra/rockets.cfg
```

Custom rocket textures are stored in:

```text
config/ad_astra/rocket_png/
```

On first startup the mod creates a default editable template:

```text
config/ad_astra/rocket_png/custom_tier_8_rocket.png
```

Example:

```cfg
S:customRockets <
    custom_tier_8_rocket|八阶火箭|8|10000|7|custom_tier_8_rocket.png
 >
```

Format:

```text
id|display name|rocket tier|fuel capacity mB|model tier|texture
```

- `id` must be unique.
- `display name` is shown in item names and the rocket UI.
- `rocket tier` controls travel access and fuel requirements.
- `fuel capacity` controls rocket tank size.
- `model tier` reuses the existing tier 1-7 rocket models.
- `texture` can be an external PNG in `rocket_png` or a normal resource location.

## CraftTweaker / ZenScript Integration

Available imports:

```zenscript
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
import mods.ad_astra.CustomPlanets;
```

### Rocket fuel

Rocket fuel tiers are scriptable. For example, lava can be made a tier 7 rocket fuel by script:

```zenscript
RocketFuel.addFuel("lava", 7);
<minecraft:lava_bucket>.addTooltip("燃料等级：7");
```

The mod no longer hardcodes lava as a default rocket fuel; pack scripts control it.

### NASA Workbench recipes

```zenscript
NASAWorkbench.addRecipe(String id, IItemStack[] inputs, IItemStack output, int width, int height, int time, int energy);
NASAWorkbench.removeRecipe(String id);
NASAWorkbench.removeByOutput(IItemStack output);
```

### Planet rocket tiers

```zenscript
PlanetTiers.setPlanetTier(int dimensionId, int tier);
PlanetTiers.removePlanetTier(int dimensionId);
```

### Space station recipes

```zenscript
SpaceStation.setRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.replaceRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.addRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.removeRecipe(String orbit);
SpaceStation.removeRecipeById(String id);
```

### Custom planets

```zenscript
CustomPlanets.create(String id, int dimensionId)
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

Development scripts can be placed in:

```text
run/client/scripts/
```

## Requirements

- Minecraft 1.12.2
- Cleanroom Loader / Forge-compatible 1.12.2 runtime
- Patchouli is required
- HEI/JEI is recommended for recipes and information displays
- CraftTweaker is optional, but required for scripts and custom planet/fuel APIs

## Build And Run

```powershell
.\gradlew.bat compileJava processResources
.\gradlew.bat build
.\gradlew.bat runClient
```

Generated jars are placed under:

```text
build/libs
```

## Credits

Maintainer: GingerYJ

Original Ad Astra content and assets are credited to Terrarium and Ad Astra contributors. This project adapts and expands the experience for Minecraft 1.12.2.
