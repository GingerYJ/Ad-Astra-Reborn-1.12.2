# Ad Astra API for Minecraft 1.12.2

This package contains the public API for integrating with the Ad Astra mod on Minecraft 1.12.2.

## Overview

The Ad Astra API provides stable interfaces for third-party mods to:

- Query and modify planet atmospheric conditions (oxygen, gravity, temperature)
- Register custom planets and celestial bodies
- Create custom machine recipes
- Register custom rockets and rovers
- Hook into environmental system events
- Access player space suit and oxygen data via Forge Capabilities

## Quick Start

### Adding the API to Your Project

Add Ad Astra as a dependency in your `build.gradle`:

```gradle
dependencies {
    compile "earth.terrarium:ad-astra:1.12.2-1.0.0"
}
```

### Basic Usage Examples

#### Check if a location has oxygen

```java
import earth.terrarium.adastra.api.systems.OxygenApi;

boolean hasOxygen = OxygenApi.API.hasOxygen(world, pos);
if (!hasOxygen) {
    // Player needs a space suit here
}
```

#### Get gravity for a dimension

```java
import earth.terrarium.adastra.api.systems.GravityApi;

float gravity = GravityApi.API.getGravity(world);
// 1.0 = Earth gravity, 0.166 = Moon gravity, etc.
```

#### Register a custom planet

```java
import earth.terrarium.adastra.api.planets.*;

@Mod.EventHandler
public void preInit(FMLPreInitializationEvent event) {
    Planet customPlanet = Planet.builder(CUSTOM_DIM_ID)
        .oxygen(false)
        .temperature((short) -100)
        .gravity(0.5f)
        .solarPower(15)
        .tier(3)
        .solarSystem(new ResourceLocation("modid", "custom_system"))
        .build();
    
    PlanetApi.API.registerPlanet(customPlanet);
}
```

#### Listen to environmental events

```java
import earth.terrarium.adastra.api.events.AdAstraEvents;

@Mod.EventHandler
public void init(FMLInitializationEvent event) {
    // Prevent oxygen deprivation for specific entities
    AdAstraEvents.OxygenTickEvent.register((world, entity) -> {
        if (entity instanceof MyCustomEntity) {
            return false; // Cancel oxygen damage
        }
        return true; // Allow normal oxygen processing
    });
    
    // Modify gravity for specific entities
    AdAstraEvents.EntityGravityEvent.register((entity, gravity) -> {
        if (entity.hasCapability(MY_CAPABILITY, null)) {
            return gravity * 0.5f; // Half gravity
        }
        return gravity;
    });
}
```

#### Register a custom machine recipe

```java
import earth.terrarium.adastra.api.recipes.RecipeApi;

@Mod.EventHandler
public void init(FMLInitializationEvent event) {
    RecipeApi.API.registerCompressingRecipe(
        "modid:compressed_custom_ore",
        Collections.singletonList(new ItemStack(customOre, 9)),
        Collections.singletonList(new ItemStack(compressedCustomOre)),
        200,  // 10 seconds (200 ticks)
        20    // 20 FE/tick
    );
}
```

#### Register a custom rocket

```java
import earth.terrarium.adastra.api.vehicles.*;

@Mod.EventHandler
public void init(FMLInitializationEvent event) {
    VehicleApi.API.registerVehicleType(
        new ResourceLocation("modid", "super_rocket"),
        VehicleApi.VehicleType.ROCKET,
        4,      // Tier 4
        100000  // 100,000 mB fuel capacity
    );
}
```

## API Packages

### `earth.terrarium.adastra.api.systems`

Environmental systems for planets:

- **`OxygenApi`** - Query and modify oxygen availability
- **`GravityApi`** - Query and modify gravity effects  
- **`TemperatureApi`** - Query and modify temperature
- **`PlanetData`** - Efficient data structure for network synchronization

### `earth.terrarium.adastra.api.planets`

Planet registration and queries:

- **`PlanetApi`** - Register and query planet data
- **`Planet`** - Planet metadata (atmosphere, solar power, tier, etc.)

### `earth.terrarium.adastra.api.events`

Event hooks for gameplay systems:

- **`AdAstraEvents`** - Event listeners for oxygen, gravity, temperature, acid rain, etc.

### `earth.terrarium.adastra.api.recipes`

Custom machine recipe registration:

- **`RecipeApi`** - Register recipes for Compressor, Alloy Smelter, Cryo Freezer, etc.

### `earth.terrarium.adastra.api.vehicles`

Custom rocket and rover registration:

- **`VehicleApi`** - Register and manage custom vehicles
- **`VehicleData`** - Access vehicle properties (fuel, health, inventory)

### `earth.terrarium.adastra.api`

Core utilities:

- **`ApiHelper`** - Internal ServiceLoader helper
- **`CapabilityHelper`** - Utilities for Forge Capability integration

## Implementation Details

### Service Loader Pattern

The API uses Java's ServiceLoader mechanism. Ad Astra provides implementations at runtime. You don't need to implement these interfaces - just use the static `API` field:

```java
OxygenApi.API.hasOxygen(world, pos);
```

### Forge Capabilities

Ad Astra uses Forge's Capability system for player data. See the existing `AdAstraCapabilities` class for examples of capability registration patterns that work with this API.

### Dimension IDs (1.12.2)

In 1.12.2, dimensions are identified by integer IDs:

- `0` - Overworld (Earth)
- `108490` - Moon
- `108491` - Mars
- `108492` - Mercury
- `108493` - Venus
- `108494` - Glacio

Custom planets should use IDs not conflicting with other mods. Ad Astra reserves surface IDs `108490`-`108508` for its 19 built-in surface planets and uses `107489` for the single global space station. There are no per-planet orbit dimensions.

### Energy System

Ad Astra uses Forge Energy (FE) for power:

- Solar panels generate FE based on planet's `solarPower` value
- Machines consume FE at rates defined in recipes
- 1 FE = 1 RF (Redstone Flux) for cross-mod compatibility

## Compatibility and Stability

### API Stability

All public interfaces in `earth.terrarium.adastra.api` are considered stable. Methods may be added in future versions, but existing methods will maintain backward compatibility.

Methods marked `@Deprecated` may be removed in future major versions - check the deprecation notice for alternatives.

### Mod Compatibility

This API is designed to work alongside:

- JEI (Just Enough Items) - for recipe viewing
- The One Probe / WAILA - for block information
- Other space/planet mods - through dimension registration

### Thread Safety

Most API methods should be called from the main game thread. Methods that modify world state (setting oxygen, gravity, etc.) are NOT thread-safe.

## Support and Issues

For API support, please:

1. Check this documentation first
2. Look at the Javadoc on each interface
3. Report issues on the Ad Astra GitHub issue tracker
4. Include your Minecraft version (1.12.2) and Forge version

## Example Integrations

See the `examples/` folder (if provided) for complete example mods demonstrating:

- Custom planet registration
- Custom machine recipe integration
- Custom rocket creation
- Environmental event handling

## License

The Ad Astra API is part of the Ad Astra mod and follows the same license terms.
