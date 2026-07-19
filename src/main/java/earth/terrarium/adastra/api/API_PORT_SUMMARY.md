# Ad Astra 1.12.2 API Port - Summary

## Overview

Successfully ported the Ad Astra API layer from 1.20.x to 1.12.2, adapting for Forge 1.12.2's systems including the Registry system, Event system, and Capability system.

## API Structure

### Core Package: `earth.terrarium.adastra.api`

#### Helper Classes
- **`ApiHelper.java`** - ServiceLoader implementation for loading API implementations
- **`CapabilityHelper.java`** - Utilities for Forge Capability integration patterns
- **`package-info.java`** - Comprehensive API documentation with usage examples

### Systems Package: `earth.terrarium.adastra.api.systems`

Environmental systems for planet atmospheres:

- **`OxygenApi.java`** - Query and modify oxygen availability
  - `hasOxygen(World)`, `hasOxygen(BlockPos)`, `hasOxygen(Entity)`
  - `setOxygen()`, `removeOxygen()` for creating sealed rooms
  - `entityTick()` for applying oxygen deprivation

- **`GravityApi.java`** - Query and modify gravity effects
  - `getGravity(World)`, `getGravity(BlockPos)`, `getGravity(Entity)`
  - `setGravity()`, `removeGravity()` for gravity generators
  - Supports custom gravity modifiers per-position

- **`TemperatureApi.java`** - Query and modify temperature
  - `getTemperature(World)`, `getTemperature(BlockPos)`, `getTemperature(Entity)`
  - `isLiveable()`, `isHot()`, `isCold()` for environmental checks
  - `setTemperature()`, `removeTemperature()` for climate control

- **`PlanetData.java`** - Efficient bit-packed data structure
  - Packs oxygen (1 bit), temperature (16 bits), gravity (15 bits) into 32-bit int
  - Used for network synchronization
  - 2 decimal places precision for gravity

### Planets Package: `earth.terrarium.adastra.api.planets`

Planet registration and metadata:

- **`Planet.java`** - Planet metadata class
  - Dimension ID, oxygen, temperature, gravity
  - Solar power generation multiplier
  - Technology tier requirement (1-4)
  - Orbit dimension ID for space stations
  - Builder pattern for easy construction
  - Constants for default planets (MOON_DIM, MARS_DIM, etc.)

- **`PlanetApi.java`** - Planet query and registration API
  - `getPlanet(World)`, `getPlanet(int dimensionId)`
  - `isPlanet()`, `isSpace()`, `isExtraterrestrial()`
  - `getSolarPower()` for energy generation
  - `registerPlanet()` for third-party planet registration

### Events Package: `earth.terrarium.adastra.api.events`

Event hooks for gameplay mechanics:

- **`AdAstraEvents.java`** - Comprehensive event system
  - `OxygenTickEvent` - Cancel oxygen deprivation
  - `EntityOxygenEvent` - Modify oxygen availability for entities
  - `TemperatureTickEvent` - Cancel temperature effects
  - `HotTemperatureTickEvent` - Cancel burning damage
  - `ColdTemperatureTickEvent` - Cancel freezing damage
  - `GravityTickEvent` - Cancel gravity effects
  - `EntityGravityEvent` - Modify gravity for entities
  - `ZeroGravityTickEvent` - Handle zero gravity
  - `AcidRainTickEvent` - Handle acid rain (Venus)
  - `EnvironmentTickEvent` - Random environmental effects

All events use listener pattern with `register()` and `fire()` methods.

### Recipes Package: `earth.terrarium.adastra.api.recipes`

Custom machine recipe registration:

- **`RecipeApi.java`** - Recipe registration API
  - `RecipeType` enum: NASA_WORKBENCH, COMPRESSING, ALLOY_SMELTING, FUEL_REFINING, CRYO_FREEZING, OXYGEN_LOADING
  - `registerCompressingRecipe()` - Add Compressor recipes
  - `registerAlloySmeltingRecipe()` - Add Alloy Smelter recipes
  - `registerCryoFreezingRecipe()` - Add Cryo Freezer recipes
  - `getRecipes()`, `findRecipe()`, `removeRecipe()` for querying

### Vehicles Package: `earth.terrarium.adastra.api.vehicles`

Custom rocket and rover registration:

- **`VehicleApi.java`** - Vehicle registration and management
  - `VehicleType` enum: ROCKET, ROVER, LANDER, CUSTOM
  - `registerVehicleType()` - Register custom vehicles
  - `isVehicle()`, `isRocket()`, `isRover()` - Entity checks
  - `getVehicleData()` - Access vehicle properties
  - `spawnVehicle()` - Programmatic spawning
  - `getVehicleItem()` - Get vehicle item stacks
  - `canLaunchFrom()` - Check launch restrictions

- **`VehicleData.java`** - Vehicle runtime data interface
  - Fuel management: `getFuel()`, `addFuel()`, `consumeFuel()`
  - Health/durability: `getHealth()`, `repair()`
  - Capabilities: `providesOxygen()`, `providesTemperatureProtection()`
  - Inventory: `getInventorySize()`
  - Custom data storage: `getCustomData()`, `setCustomData()`

## Key Adaptations for 1.12.2

### From 1.20.x to 1.12.2

1. **Dimension System**
   - Changed from `ResourceKey<Level>` to `int` dimension IDs
   - Replaced `Level` with `World`
   - Replaced `ServerLevel` with `WorldServer`

2. **Registry System**
   - Removed modern codec-based serialization
   - Used direct constructor pattern instead of records
   - Added Builder pattern for Planet class

3. **Event System**
   - Replaced modern event architecture with simple listener lists
   - Used `@FunctionalInterface` for event listeners
   - Implemented manual `register()` and `fire()` methods

4. **API Loading**
   - Kept ServiceLoader pattern (Java 6+ compatible)
   - Modified to iterate ServiceLoader instead of using Optional

5. **Capabilities**
   - Added `CapabilityHelper` for Forge Capability integration
   - Documented capability patterns in API

6. **Position and Entity Classes**
   - `BlockPos` remains same
   - `Entity` remains same
   - Changed `LivingEntity` to `EntityLivingBase`
   - Changed `Vec3` to `Vec3d`

7. **Block States**
   - Changed `BlockState` to `IBlockState`

## Documentation

- **`API_README.md`** - Comprehensive usage guide with examples
- **Javadoc** - Extensive documentation on all public methods
- **`package-info.java`** - Package-level overview with examples

## Integration Examples Included

The API README includes complete examples for:

1. Checking oxygen availability
2. Querying gravity values
3. Registering custom planets
4. Listening to environmental events
5. Registering machine recipes
6. Creating custom rockets
7. Accessing vehicle data

## Backward Compatibility Features

- Built-in surface dimension IDs use the reserved 108490-108494 range
- Energy system uses Forge Energy (FE) compatible with RF
- Capability patterns follow standard Forge 1.12.2 conventions
- ServiceLoader pattern ensures loose coupling

## Files Created

Total: 14 API files + 1 Reference class

### Core (3 files)
- `ApiHelper.java`
- `CapabilityHelper.java`
- `package-info.java`

### Systems (4 files)
- `GravityApi.java`
- `OxygenApi.java`
- `TemperatureApi.java`
- `PlanetData.java`

### Planets (2 files)
- `Planet.java`
- `PlanetApi.java`

### Events (1 file)
- `AdAstraEvents.java`

### Recipes (1 file)
- `RecipeApi.java`

### Vehicles (2 files)
- `VehicleApi.java`
- `VehicleData.java`

### Documentation (1 file)
- `API_README.md`

### Supporting (1 file)
- `Reference.java` (mod constants)

## Next Steps for Implementation

To complete the API integration, the following implementation classes need to be created:

1. **Service Implementations**
   - Create implementation classes for each API interface
   - Register them in `META-INF/services/` files for ServiceLoader

2. **Integration with Existing Systems**
   - Connect `GravityApi` implementation to existing gravity system
   - Connect `OxygenApi` implementation to existing oxygen system
   - Connect `TemperatureApi` implementation to existing temperature system
   - Connect `PlanetApi` to `ModDimensions` registry

3. **Event Firing**
   - Add `AdAstraEvents.fire()` calls in appropriate event handlers
   - Integrate with existing `CommonEventHandler`

4. **Recipe Integration**
   - Connect `RecipeApi` to existing recipe registries
   - Add methods to `RecipeLoader` for third-party registration

5. **Vehicle Integration**
   - Connect `VehicleApi` to entity registration system
   - Implement `VehicleData` wrapper for vehicle entities

## API Features Summary

✅ **Complete** - All major systems covered
✅ **Documented** - Comprehensive Javadoc and examples
✅ **Stable** - Clear interfaces with backward compatibility notes
✅ **Extensible** - Builder patterns and event hooks
✅ **1.12.2 Compatible** - Uses Forge 1.12.2 systems throughout
✅ **Third-party Friendly** - Easy integration with minimal dependencies

The API layer provides a clean, stable interface for third-party mod integration while maintaining compatibility with Ad Astra's internal implementation details.
