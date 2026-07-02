# Ad Astra 1.12.2 API - Implementation Guide

## For Ad Astra Core Developers

This guide explains how to implement the API interfaces within the Ad Astra mod itself.

## ServiceLoader Implementation Pattern

Each API interface needs a concrete implementation class registered with Java's ServiceLoader.

### Step 1: Create Implementation Classes

Create implementation classes in a separate package, e.g., `earth.terrarium.adastra.common.api.impl`

Example for `OxygenApi`:

```java
package earth.terrarium.adastra.common.api.impl;

import earth.terrarium.adastra.api.systems.OxygenApi;
import earth.terrarium.adastra.common.systems.OxygenSystem;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Collection;

public class OxygenApiImpl implements OxygenApi {
    
    @Override
    public boolean hasOxygen(World world) {
        return OxygenSystem.hasOxygen(world);
    }
    
    @Override
    public boolean hasOxygen(int dimensionId) {
        return OxygenSystem.hasOxygen(dimensionId);
    }
    
    @Override
    public boolean hasOxygen(World world, BlockPos pos) {
        return OxygenSystem.hasOxygen(world, pos);
    }
    
    @Override
    public boolean hasOxygen(Entity entity) {
        return OxygenSystem.hasOxygen(entity);
    }
    
    @Override
    public void setOxygen(World world, BlockPos pos, boolean oxygen) {
        OxygenSystem.setOxygen(world, pos, oxygen);
    }
    
    @Override
    public void setOxygen(World world, Collection<BlockPos> positions, boolean oxygen) {
        for (BlockPos pos : positions) {
            OxygenSystem.setOxygen(world, pos, oxygen);
        }
    }
    
    @Override
    public void removeOxygen(World world, BlockPos pos) {
        OxygenSystem.removeOxygen(world, pos);
    }
    
    @Override
    public void removeOxygen(World world, Collection<BlockPos> positions) {
        for (BlockPos pos : positions) {
            OxygenSystem.removeOxygen(world, pos);
        }
    }
    
    @Override
    public void entityTick(WorldServer world, EntityLivingBase entity) {
        OxygenSystem.tickEntity(world, entity);
    }
}
```

### Step 2: Register with ServiceLoader

Create a file at: `src/main/resources/META-INF/services/earth.terrarium.adastra.api.systems.OxygenApi`

Contents:
```
earth.terrarium.adastra.common.api.impl.OxygenApiImpl
```

Repeat for each API interface:

- `META-INF/services/earth.terrarium.adastra.api.systems.GravityApi`
- `META-INF/services/earth.terrarium.adastra.api.systems.TemperatureApi`
- `META-INF/services/earth.terrarium.adastra.api.planets.PlanetApi`
- `META-INF/services/earth.terrarium.adastra.api.recipes.RecipeApi`
- `META-INF/services/earth.terrarium.adastra.api.vehicles.VehicleApi`

### Step 3: Integrate with Existing Systems

#### Oxygen System Integration

In `CommonEventHandler` or wherever oxygen is checked:

```java
// Before applying oxygen deprivation
if (!AdAstraEvents.OxygenTickEvent.fire(world, entity)) {
    return; // Event was cancelled
}

// Check if entity has oxygen (with event hook)
boolean hasOxygen = OxygenApi.API.hasOxygen(entity);
hasOxygen = AdAstraEvents.EntityOxygenEvent.fire(entity, hasOxygen);
```

#### Gravity System Integration

In gravity tick handler:

```java
// Get gravity with event hook
float gravity = GravityApi.API.getGravity(entity);
gravity = AdAstraEvents.EntityGravityEvent.fire(entity, gravity);

// Before applying gravity effects
if (!AdAstraEvents.GravityTickEvent.fire(world, entity, travelVector, pos)) {
    return; // Event was cancelled
}

// Handle zero gravity
if (gravity < 0.01f) {
    if (!AdAstraEvents.ZeroGravityTickEvent.fire(world, entity, travelVector, pos)) {
        return;
    }
}
```

#### Temperature System Integration

In temperature tick handler:

```java
// Before applying temperature effects
if (!AdAstraEvents.TemperatureTickEvent.fire(world, entity)) {
    return;
}

short temperature = TemperatureApi.API.getTemperature(entity);

if (TemperatureApi.API.isHot(world, pos)) {
    if (!AdAstraEvents.HotTemperatureTickEvent.fire(world, entity)) {
        return; // Don't burn
    }
    // Apply burning
}

if (TemperatureApi.API.isCold(world, pos)) {
    if (!AdAstraEvents.ColdTemperatureTickEvent.fire(world, entity)) {
        return; // Don't freeze
    }
    // Apply freezing
}
```

#### Planet Registry Integration

Create `PlanetApiImpl` that wraps `ModDimensions`:

```java
public class PlanetApiImpl implements PlanetApi {
    
    private static final Map<Integer, Planet> PLANETS = new HashMap<>();
    
    static {
        // Register default planets from ModDimensions
        PLANETS.put(ModDimensions.MOON_ID, createPlanetFromProperties(ModDimensions.MOON_PROPERTIES));
        PLANETS.put(ModDimensions.MARS_ID, createPlanetFromProperties(ModDimensions.MARS_PROPERTIES));
        // ... etc
    }
    
    @Override
    public Planet getPlanet(int dimensionId) {
        return PLANETS.get(dimensionId);
    }
    
    @Override
    public void registerPlanet(Planet planet) {
        PLANETS.put(planet.getDimensionId(), planet);
    }
    
    private static Planet createPlanetFromProperties(PlanetDimensionProperties props) {
        return Planet.builder(props.getDimensionId())
            .oxygen(!props.requiresOxygen())
            .temperature(props.getTemperature())
            .gravity(props.getGravity())
            .solarPower(props.getSolarPower())
            .tier(props.getTier())
            .build();
    }
}
```

#### Recipe System Integration

Create `RecipeApiImpl` that wraps existing recipe registries:

```java
public class RecipeApiImpl implements RecipeApi {
    
    @Override
    public boolean registerCompressingRecipe(String id, List<ItemStack> inputs, 
                                            List<ItemStack> outputs, int time, int energy) {
        CompressingRecipe recipe = new CompressingRecipe(id, inputs, outputs, time, energy);
        CompressingRecipes.register(recipe);
        return true;
    }
    
    @Override
    public List<?> getRecipes(RecipeType type) {
        switch (type) {
            case COMPRESSING:
                return CompressingRecipes.getAllRecipes();
            case ALLOY_SMELTING:
                return AlloySmeltingRecipes.getAllRecipes();
            // ... etc
        }
        return Collections.emptyList();
    }
    
    @Override
    public Object findRecipe(RecipeType type, ItemStack[] inputs) {
        switch (type) {
            case COMPRESSING:
                return CompressingRecipes.findRecipe(inputs);
            // ... etc
        }
        return null;
    }
}
```

#### Vehicle System Integration

If vehicle entities don't exist yet, this can be stubbed for future implementation:

```java
public class VehicleApiImpl implements VehicleApi {
    
    private static final Map<ResourceLocation, VehicleDefinition> VEHICLES = new HashMap<>();
    
    @Override
    public boolean registerVehicleType(ResourceLocation id, VehicleType type, 
                                      int tier, int fuelCapacity) {
        if (VEHICLES.containsKey(id)) {
            return false;
        }
        VEHICLES.put(id, new VehicleDefinition(id, type, tier, fuelCapacity));
        return true;
    }
    
    @Override
    public boolean isVehicle(Entity entity) {
        // Check if entity is a rocket or rover
        return entity instanceof EntityRocket || entity instanceof EntityRover;
    }
    
    @Override
    public VehicleData getVehicleData(Entity entity) {
        if (entity instanceof IVehicle) {
            return ((IVehicle) entity).getVehicleData();
        }
        return null;
    }
}
```

## Testing the API

### Unit Test Example

```java
public class ApiIntegrationTest {
    
    @Test
    public void testOxygenApi() {
        // API should be loadable
        assertNotNull(OxygenApi.API);
        
        // Test basic functionality
        World world = ... // mock or test world
        BlockPos pos = new BlockPos(0, 64, 0);
        
        OxygenApi.API.setOxygen(world, pos, true);
        assertTrue(OxygenApi.API.hasOxygen(world, pos));
        
        OxygenApi.API.removeOxygen(world, pos);
        // Should return to dimension default
    }
    
    @Test
    public void testPlanetRegistration() {
        Planet testPlanet = Planet.builder(9999)
            .oxygen(false)
            .temperature((short) -50)
            .gravity(0.3f)
            .tier(2)
            .build();
        
        PlanetApi.API.registerPlanet(testPlanet);
        
        Planet retrieved = PlanetApi.API.getPlanet(9999);
        assertNotNull(retrieved);
        assertEquals(0.3f, retrieved.getGravity(), 0.001f);
    }
}
```

## Event Testing

Test that events can be registered and fired:

```java
@Test
public void testEventSystem() {
    AtomicBoolean eventFired = new AtomicBoolean(false);
    
    AdAstraEvents.OxygenTickEvent.register((world, entity) -> {
        eventFired.set(true);
        return true;
    });
    
    // Fire event
    WorldServer world = ... // mock
    EntityLivingBase entity = ... // mock
    AdAstraEvents.OxygenTickEvent.fire(world, entity);
    
    assertTrue(eventFired.get());
}
```

## Debugging Tips

1. **ServiceLoader Issues**: If API loading fails, check:
   - Service files are in `META-INF/services/`
   - File names exactly match the interface fully-qualified name
   - No extra whitespace in service files
   - Implementation classes are public with public no-arg constructor

2. **Event Not Firing**: Check:
   - Event listener is registered during init phase
   - `fire()` method is actually called in your code
   - Event handler returns correct boolean

3. **Null API**: Check:
   - ServiceLoader file exists and has correct class name
   - Implementation class is on the classpath
   - No typos in package names

## Performance Considerations

1. **Event Overhead**: Events use ArrayList iteration, minimal overhead
2. **ServiceLoader**: Loaded once at startup, no runtime cost
3. **API Calls**: Direct method calls after loading, no reflection
4. **Position Queries**: Cache results where possible for repeated checks

## Backwards Compatibility

When adding new methods to API interfaces:

1. Add `@since 1.x.x` Javadoc tag
2. Consider adding default implementations (Java 8+)
3. Document breaking changes in release notes
4. Deprecate old methods before removal (1+ version notice period)

Example:
```java
/**
 * New method added in version 1.2.0
 * @since 1.2.0
 */
default boolean newMethod() {
    return false; // Safe default
}
```

## Summary

The API implementation follows a clean separation:

- **API Package** (`earth.terrarium.adastra.api`) - Public stable interfaces
- **Implementation Package** (`earth.terrarium.adastra.common.api.impl`) - Internal implementations
- **ServiceLoader** - Loose coupling between API and implementation
- **Events** - Extension points for third-party mods

This design allows third-party mods to depend only on the API interfaces without coupling to internal implementation details.
