# Ad Astra 1.20.x to 1.12.2 Porting Targets

This document is a source-derived target list for porting `Ad-Astra-1.20.x` into
the Cleanroom/Forge 1.12.2 project `Ad-Astra-Reborn-1.12.2`.

The intent is content parity over time. Assets such as textures, blockstates,
models, GUI textures, sounds, NBT structures, paintings, particles, and planet
renderer data should be reused from the 1.20 source where the 1.12.2 runtime can
consume them. Runtime behavior, registries, data loading, networking, dimensions,
menus, and renderers must be adapted to 1.12.2 APIs.

## Source Inventory

Source tree inspected:

- `Ad-Astra-1.20.x/Ad-Astra-1.20.x/common/src/main/java`
- `Ad-Astra-1.20.x/Ad-Astra-1.20.x/common/src/main/resources`
- `Ad-Astra-1.20.x/Ad-Astra-1.20.x/common/src/main/generated/resources`

High-level source modules:

- Common code: blocks, block entities, items, entities, menus, recipes, network,
  planets, systems, worldgen, commands, config, tags, compat, utilities.
- Client code: screens, renderers, models, dimensions, particles, radio, sounds,
  overlays, TI-69 renderer apps.
- Platform code: Fabric and NeoForge wrappers exist in the 1.20 project, but the
  1.12.2 target should use Cleanroom/Forge-native registrations and hooks.

Registry counts from the 1.20 source:

- Blocks: 333
- Item ids: 410
- Entity types: 20
- Block entity types: 19
- Menus: 15
- Recipe types: 7
- Recipe serializers: 7
- Sound events: 8
- Fluid properties: 5
- Fluid registry entries including flowing variants: 10
- Particle types: 4
- Painting variants: 14
- World feature/carver/structure registries: 6 ids across features,
  structures, carvers, biome sources, and density functions.

Resource counts from the 1.20 source:

- Hand-authored assets:
  - `textures`: 600
  - `models`: 61
  - `blockstates`: 13
  - `sounds`: 14
  - `particles`: 4
  - `patchouli_books`: 171
  - `lang`: 7
  - `resourcefullib`: 5
- Generated assets:
  - `blockstates`: 320
  - `models`: 944
  - `planet_renderers`: 11
  - `lang`: 1
  - `resourcefullib`: 16
- Hand-authored data:
  - `dimension`: 5
  - `worldgen`: 43
  - `structures`: 56 NBT files
  - `loot_tables`: 5
  - `recipes`: 1 Patchouli book recipe
- Generated data:
  - `recipes`: 533
  - `loot_tables`: 328
  - `advancements`: 562
  - `tags`: 99
  - `worldgen`: 65
  - `dimension`: 6
  - `dimension_type`: 11
  - `planets`: 12
  - `damage_type`: 5

## Current 1.12.2 Coverage Snapshot

The target project already contains a working 1.12.2 foundation:

- Mod id: `ad_astra`
- Java package: `earth.terrarium.adastra`
- Main class: `earth.terrarium.adastra.AdAstraReborn`
- Build status: `gradlew.bat build` passes.
- Copied assets/data are present in `src/main/resources`.
- Visible block id coverage is complete: source blocks 333, target blocks 333,
  missing source block ids 0.
- Current standalone item coverage has source id parity: 82 standalone items
  registered, plus registered block items for the 333 visible blocks.
- Remaining 1.20 item ids not covered by standalone items or registered block
  items: 0.
- Current entity id coverage has source id parity: source entities 20, target
  entities 20, missing source entity ids 0.
- Current block entity id coverage has source id parity: source block entities
  19, target TileEntities 19, missing source block entity ids 0.

Important caveat: the latest item/entity coverage is registry coverage, not
feature parity. Space suits are wearable armor placeholders using copied 1.20
assets. The wrench now has first-pass machine side-configuration behavior, but
gas tanks, TI-69, zip gun, space painting, vehicle items, spawn eggs, all 20
entity classes, and most TileEntity behavior still need their real 1.12.2
implementation.

## Porting Target Groups

### 1. Runtime Foundation

Target:

- Keep `ad_astra` registry names stable.
- Keep source package names close to `earth.terrarium.adastra` where practical.
- Replace ResourcefulLib/Botarium registry helpers with Forge 1.12.2 registry
  events and local helper classes.
- Replace 1.20 components, data components, holders, registries, tags, and
  resource conditions with 1.12.2-compatible code.
- Keep config categories for machines, oxygen, temperature, gravity, fluids,
  dimensions, vehicles, and compatibility toggles.
- Maintain client/server proxy split for 1.12.2.
- Maintain network channel, GUI handler, entity registration, tile entity
  registration, sound registration, and model binding.

Current status:

- Foundation exists and builds.
- Continue expanding the foundation as functional systems are ported.

### 2. Assets and Resource Conversion

Target:

- Reuse copied 1.20 assets directly where 1.12 can load them:
  - block textures
  - item textures
  - entity textures
  - environment textures
  - GUI textures
  - painting textures
  - particle textures
  - sounds
  - many block/item JSON models
  - many blockstate JSONs after state compatibility work
- Preserve generated models/blockstates copied from 1.20 datagen.
- Convert or replace assets that rely on newer loader behavior:
  - custom model loaders
  - rendered block items
  - special renderer JSON conventions
  - client-side planet renderer metadata
  - Patchouli/resourcefullib-specific resources.
- Convert `en_us.json` style lang data to 1.12 `.lang` keys.
- Keep 1.20 `data` files as source material; do not assume they are loadable as
  a 1.12 data pack.

Current status:

- Assets and generated data are copied.
- Broad `en_us.lang` conversion exists.
- Model coverage for registered non-fluid block items and current standalone
  items is currently clean.

### 3. Blocks and Block Items

Target:

- Port all 333 block ids.
- Preserve copied visual states where possible.
- Implement correct behavior by block family:
  - simple cubes
  - CTM-like factory blocks
  - stairs, slabs, walls
  - buttons and pressure plates
  - doors and trapdoors
  - ladders, fences, fence gates
  - pillars, glowing pillars, marked pillars
  - lamps and small lamps
  - flags
  - globes
  - radio
  - launch pad
  - airlock and reinforced door
  - sliding doors
  - pipe/cable ducts
  - energy cables
  - fluid pipes
  - machine blocks
  - fluid blocks
  - planetary terrain and ores
  - aeronos, strophar, and glacian wood/plant sets.

Current status:

- All 333 source block ids are registered in the 1.12.2 target.
- Many complex blocks are currently visual or state-compatible placeholders.

Remaining behavior work:

- True 3x3 sliding doors, synced parts, lock state, animation, sounds, and tile
  entity logic.
- Launch pad structure validation and rocket integration.
- Airlock and reinforced door functional behavior.
- Radio tile entity, station list, GUI, audio playback, and persistence.
- Flag tile entity, custom URL/image support, networking, renderer.
- Globe tile entity, spinning/special renderer, item renderer.
- Pipe and cable transfer networks. Energy cables and fluid pipes have
  first-pass adjacent Forge Energy/Fluid transfer TileEntities, per-face
  `none`/`normal`/`insert`/`extract` modes, and wrench mode cycling, but not the
  full 1.20 network behavior.
- Machine tile entities, recipes, inventory, energy, fluid tanks, GUI, sync.
- Fluid-specific freezing/evaporation and placement rules.

### 4. Standalone Items and Equipment

Target:

- Port basic materials/components:
  - iron plate/rod
  - steel ingot/nugget/plate/rod
  - etrium ingot/nugget/plate/rod/core
  - desh, ostrum, calorite ingots/nuggets/plates
  - raw desh/ostrum/calorite
  - engines, tanks, wheel, fan, oxygen gear, rocket fins, rocket nose cone
  - photovoltaic cells
  - cheese and ice shard.
- Port tools and utility items:
  - wrench
  - TI-69
  - zip gun
  - etrionic capacitor
  - space painting.
- Port oxygen/fluid containers:
  - gas tank
  - large gas tank
  - fluid buckets
  - suit oxygen storage.
- Port armor/equipment:
  - space suit set
  - netherite space suit set
  - jet suit set.
- Port vehicle placing items:
  - tier 1 rocket
  - tier 2 rocket
  - tier 3 rocket
  - tier 4 rocket
  - tier 1 rover.
- Port spawn eggs after entity registration exists.

Current status:

- Basic material/component items, fluid buckets, utility items, gas tanks,
  armor sets, space painting, vehicle items, and spawn egg item ids are
  registered.
- Wrench has a first-pass 1.12.2 implementation for machine side modes:
  right-click cycles the clicked face's energy mode, and sneaking cycles
  backwards or fluid mode on machines with tanks.
- Etrionic Capacitor now has a first-pass Forge Energy item capability backed
  by NBT, with 250,000 FE capacity, 250 FE/t input, 500 FE/t output, a tooltip,
  durability bar, right-click active toggling, shift-right-click
  sequential/round-robin mode switching, and inventory energy distribution to
  other Forge Energy items.
- All source item ids are currently covered by standalone item registrations or
  automatically registered block items.
- Independent item model coverage is currently clean: 0 missing models.

Implementation notes:

- Armor must be rebuilt with 1.12 `ItemArmor`, armor material definitions,
  texture layers, dye/NBT behavior, and environmental protection hooks.
- Jet suit movement and energy storage require custom per-tick input handling
  and server validation.
- Gas tanks should use Forge fluid or a custom gas capability/NBT model.
- Vehicle items should spawn 1.12 entity classes, not just exist as icons.
- Spawn eggs should wait for entity classes and registry ids.

### 5. Fluids and Transfer

Target:

- Port fluids:
  - oxygen
  - hydrogen
  - oil
  - fuel
  - cryo fuel.
- Rebuild flowing/source behavior using Forge 1.12 fluids.
- Port buckets and tank items.
- Replace Botarium containers with Forge fluid tanks or local wrappers.
- Integrate fluids with machines:
  - oxygen loader
  - fuel refinery
  - water pump
  - cryo freezer
  - oxygen distributor
  - vehicle fuel tanks.
- Implement fluid environmental behavior:
  - oxygen/hydrogen placement restrictions
  - cryo fuel behavior
  - freezing/evaporation rules on planets.

Current status:

- Forge fluids, fluid blocks, and buckets exist as first-pass registrations.
- Machine tanks and gas tanks are pending.

### 6. Machines, Tile Entities, Menus, and GUIs

Source machine block entities:

- Coal Generator
- Compressor
- Etrionic Blast Furnace
- Oxygen Loader
- Fuel Refinery
- Water Pump
- Solar Panel
- Oxygen Distributor
- Gravity Normalizer
- Energizer
- Cryo Freezer
- Oxygen Sensor/Detector
- NASA Workbench

Source special block entities:

- Globe
- Flag
- Sliding Door
- Cable
- Fluid Pipe
- Radio

Source menu ids:

- `coal_generator_menu`
- `compressor_menu`
- `cryo_freezer_menu`
- `etrionic_blast_furnace_menu`
- `fuel_refinery_menu`
- `gravity_normalizer_menu`
- `lander_menu`
- `nasa_workbench_menu`
- `oxygen_distributor_menu`
- `oxygen_loader_menu`
- `planets_menu`
- `rocket_menu`
- `rover_menu`
- `solar_panel_menu`
- `water_pump_menu`

Target:

- Rebuild each machine with 1.12 `TileEntity`, `IInventory`/item handlers,
  Forge Energy or local energy abstraction, Forge fluid tanks, NBT persistence,
  ticking, redstone control, side configuration, and client sync.
- Rebuild 1.20 menus/screens as 1.12 `Container`, `GuiContainer`, and
  `GuiScreen`.
- Reuse GUI textures from source assets where possible.
- Rebuild configuration screens for:
  - side config
  - redstone control
  - furnace mode
  - gravity normalizer target
  - fluid tank clearing
  - station/planet selection.

Current status:

- Machine and special block TileEntity ids are registered with minimal 1.12.2
  placeholder classes.
- Machine, globe, flag, sliding door, cable, fluid pipe, and radio blocks now
  create placeholder TileEntities in-world.
- The 13 machine TileEntities now share a first-pass storage base with item
  handler inventory, Forge Energy storage, Forge `FluidTank`, NBT persistence,
  redstone-control state, and per-face item/energy/fluid side mode state.
- The shared machine base now ticks server-side, and the Coal Generator has a
  first-pass fuel-to-energy implementation using slot 1 furnace fuel, NBT burn
  progress, 20 FE/t internal generation, and `lit` blockstate updates.
- Solar Panel now has a first-pass daylight generator that requires sky access,
  obeys redstone control, fills its internal Forge Energy buffer, and uses a
  temporary 16 FE/t Earth fallback until planet metadata and per-dimension solar
  power are ported.
- Water Pump now has a first-pass server tick loop that requires a water block
  below it, consumes 20 FE/t, and generates 50 mB/t of water into its internal
  Forge `FluidTank`.
- Energizer now has a first-pass item-charging loop using its 2,000,000 FE
  internal buffer. It can charge Forge Energy items in slot 0 at up to 500 FE/t
  while still pushing energy to adjacent receivers.
- Fuel Refinery now has first-pass dual fluid tanks: oil can enter through
  Forge fluid capability or slot 1 fluid containers, 30 FE plus 5 mB oil are
  consumed per operation, and 5 mB fuel exits through Forge fluid capability or
  slot 3 fluid containers.
- Oxygen Loader now has first-pass dual fluid tanks: water or oxygen can enter
  through Forge fluid capability or slot 1 fluid containers, 30 FE is consumed
  per operation, water produces oxygen at the copied 1.20 ratio, and oxygen can
  exit through Forge fluid capability or slot 3 fluid containers.
- Cryo Freezer now has a first-pass item-to-fluid loop: ice, packed ice, and
  Ad Astra ice shards consume 40 FE/t, progress for their 1.20-derived cook
  times, and output cryo fuel to an extract-only Forge `FluidTank` or slot 2
  fluid containers. The 1.20 blue ice recipe is deferred because vanilla
  Minecraft 1.12.2 has no blue ice item.
- Compressor now has a first-pass item-to-item loop for the 10 copied
  compressing recipes that map directly to 1.12.2: iron, steel, desh, ostrum,
  and calorite ingots/blocks become plates while consuming 20 FE/t and tracking
  cook progress.
- The shared machine base now has a first-pass Forge Energy push helper that
  uses saved per-face side modes. Coal Generator and Solar Panel default energy
  sides to `PUSH` and can send power to adjacent Forge Energy receivers.
- The wrench can now mutate saved machine side modes in-world, giving the
  side-configuration state a gameplay entry point before the GUI screens and
  side-config packets are ported.
- Steel cable and desh cable TileEntities now have first-pass Forge Energy
  transfer behavior with 1.20-derived rates of 150 FE/t and 500 FE/t, plus
  visual connections to adjacent Forge Energy blocks.
- Desh fluid pipe and ostrum fluid pipe TileEntities now have first-pass Forge
  Fluid transfer behavior with 1.20-derived rates of 150 mB/t and 500 mB/t,
  plus visual connections to adjacent fluid-capable blocks.
- Cable and fluid pipe TileEntities now persist per-face pipe modes, render
  configured modes on adjacent capability connections, can be cycled with the
  wrench, and obey insert/extract/normal/none when moving energy or fluids.
- Recipe execution for other machines, automatic transfer, exact side
  configuration semantics, networking, containers, GUI screens, special
  renderer, charge-item handling, and most real block behavior remain pending.

### 7. Recipes, Loot, Tags, and Advancements

Source recipe inventory:

- 533 generated recipe JSON files.
- 1 Patchouli shaped book recipe.
- Recipe types:
  - `minecraft:crafting_shaped`: 286
  - `minecraft:crafting_shapeless`: 27
  - `minecraft:smelting`: 37
  - `minecraft:blasting`: 25
  - `minecraft:stonecutting`: 130
  - `ad_astra:compressing`: 10
  - `ad_astra:nasa_workbench`: 4
  - `ad_astra:cryo_freezing`: 4
  - `ad_astra:oxygen_loading`: 2
  - `ad_astra:refining`: 1
  - `ad_astra:alloying`: 1
  - `ad_astra:space_station_recipe`: 6

Target:

- Convert vanilla crafting recipes to 1.12 recipe JSON or code registration.
- Convert 1.20 tags to OreDictionary entries, explicit item lists, or local
  predicates.
- Fold blasting into furnace behavior unless a custom blast furnace recipe path
  is implemented.
- Convert stonecutting into crafting alternatives, machine recipes, or omit
  until a 1.12 stonecutter equivalent is chosen.
- Rebuild custom machine recipe serializers/loaders:
  - alloying
  - compressing
  - cryo freezing
  - NASA workbench
  - oxygen loading
  - refining
  - space station recipe.
- Convert block loot tables to 1.12 drop logic or compatible loot tables.
- Decide whether generated advancements should be ported, simplified, or
  deferred.

Current status:

- First crafting, OreDictionary, smelting, and ore drop batches exist.
- Full recipe/loot/advancement parity remains pending.

### 8. Space Systems

Target:

- Port oxygen system:
  - oxygen presence checks
  - oxygen distributor volumes
  - suit oxygen use
  - tank charging/draining
  - oxygen HUD warnings
  - entity/item behavior in vacuum.
- Port temperature system:
  - planetary/environment temperature
  - cold and heat protection
  - damage over time
  - gear integration.
- Port gravity system:
  - dimension gravity multipliers
  - low/zero gravity movement
  - gravity normalizer area effects
  - entity motion adjustments.
- Port environment effects:
  - acid rain
  - space damage
  - fluid freezing/evaporation
  - destroyed-in-space behavior.
- Port TI-69 readouts for oxygen, temperature, gravity, and planet state.

Current status:

- System APIs are not functionally ported yet.

### 9. Vehicles and Launch Flow

Source vehicle entities:

- `tier_1_rocket`
- `tier_2_rocket`
- `tier_3_rocket`
- `tier_4_rocket`
- `tier_1_rover`
- `lander`

Target:

- Port entity classes, sizes, data sync, NBT, inventory, fuel, passengers,
  sounds, and controls.
- Port rocket tier differences.
- Port rover movement and inventory.
- Port lander behavior.
- Port launch pad validation.
- Port launch, ascent, planet/orbit selection, landing, and space station
  routing.
- Port vehicle GUI screens:
  - rocket
  - rover
  - lander.
- Port vehicle renderers and models.

Current status:

- Vehicle item ids are registered as placeholders.
- Vehicle entity ids are registered as minimal placeholder entities with copied
  source dimensions. Movement, inventory, fuel, launch, landing, renderer, and
  controls remain pending.

### 10. Dimensions, Planets, and World Generation

Source dimensions:

- `moon`
- `mars`
- `mercury`
- `venus`
- `glacio`

Source planet metadata:

- 12 generated `planets` data files.

Source worldgen/data:

- dimension and dimension type JSONs
- noise settings
- density functions
- crater biome source and density functions
- crater world carver
- features
- configured/placed features
- structure sets
- structures
- template pools
- processor lists
- 56 NBT structure files.

Target:

- Rebuild dimensions with 1.12 `DimensionType`, `WorldProvider`,
  `BiomeProvider`, and `IChunkGenerator`.
- Port planetary biome/terrain generation for:
  - Moon
  - Mars
  - Mercury
  - Venus
  - Glacio.
- Convert 1.20 noise/worldgen JSON behavior into 1.12 generator code.
- Port craters, planetary stone layers, sands, ores, ice, permafrost, infernal
  spires, oil wells, villages, dungeons, temples, and space station placement.
- Convert NBT structures to 1.12-compatible placement logic.
- Rebuild planet metadata loading for screens, travel rules, gravity,
  temperature, oxygen, clouds, orbit, and sky rendering.

Current status:

- Planetary blocks/assets are present.
- Actual dimensions and worldgen are pending.

### 11. Entities, Mobs, AI, and Spawn Eggs

Source entity ids:

- `air_vortex`
- `corrupted_lunarian`
- `glacian_ram`
- `ice_spit`
- `lander`
- `lunarian`
- `lunarian_wandering_trader`
- `martian_raptor`
- `mogler`
- `pygro`
- `pygro_brute`
- `star_crawler`
- `sulfur_creeper`
- `tier_1_rocket`
- `tier_1_rover`
- `tier_2_rocket`
- `tier_3_rocket`
- `tier_4_rocket`
- `zombified_mogler`
- `zombified_pygro`

Target:

- Rebuild all entity classes on 1.12 entity APIs.
- Port attributes, AI tasks, attacks, special abilities, drops, spawn rules, and
  dimension spawn placement.
- Port projectile behavior:
  - ice spit
  - air vortex.
- Port spawn eggs once entity registrations exist.
- Port renderers, models, textures, layers, and animation behavior.

Current status:

- All 20 source entity ids are registered with minimal 1.12.2 placeholder
  classes.
- Spawn egg item ids are registered as placeholders; entity entries have source
  spawn egg colors where applicable.
- Full AI, attributes, spawn placement, drops, projectiles, vehicle behavior,
  renderers, and models remain pending.

### 12. Client Rendering, Screens, Particles, and Audio

Source screens:

- Machine screens for coal generator, compressor, cryo freezer, etrionic blast
  furnace, fuel refinery, gravity normalizer, NASA workbench, oxygen
  distributor, oxygen loader, solar panel, and water pump.
- Vehicle screens for lander, rocket, rover.
- Planets screen.
- Overlay screen.
- Flag URL screen.
- Base machine/configuration screen helpers.

Source renderers:

- Special block entity renderers:
  - energizer
  - flag
  - globe
  - gravity normalizer
  - oxygen distributor
  - sliding door.
- Entity renderers:
  - all mobs
  - rockets
  - rover
  - lander.
- TI-69 renderer/apps.
- Overlay renderer.
- Flag URL texture loader.

Target:

- Rebuild renderers using 1.12 rendering APIs.
- Reuse textures and entity model assets where possible.
- Recreate special rendered items for globes, energizer, machines, vehicles,
  TI-69, and space painting.
- Port particles:
  - `acid_rain`
  - `large_flame`
  - `large_smoke`
  - `oxygen_bubble`.
- Port sound events:
  - `rocket_launch`
  - `rocket`
  - `wrench`
  - `sliding_door_open`
  - `sliding_door_close`
  - `oxygen_intake`
  - `oxygen_outtake`
  - `gravity_normalizer_idle`.
- Rebuild HUD overlays for oxygen, temperature, gravity/vehicle state, and
  warnings.

Current status:

- Sound events are registered first-pass.
- Screens/renderers/particles/overlays are mostly pending.

### 13. Networking

Source packets:

- `ClientboundPlayStationPacket`
- `ClientboundSendStationsPacket`
- `ClientboundSyncLocalPlanetDataPacket`
- `ClientboundSyncPlanetsPacket`
- `ServerboundClearFluidTankPacket`
- `ServerboundConstructSpaceStationPacket`
- `ServerboundLandOnSpaceStationPacket`
- `ServerboundLandPacket`
- `ServerboundRequestStationsPacket`
- `ServerboundResetSideConfigPacket`
- `ServerboundSetFlagUrlPacket`
- `ServerboundSetFurnaceModePacket`
- `ServerboundSetGravityNormalizerTargetPacket`
- `ServerboundSetRedstoneControlPacket`
- `ServerboundSetSideConfigPacket`
- `ServerboundSetStationPacket`
- `ServerboundSyncKeybindPacket`
- `ServerboundVehicleControlPacket`

Target:

- Rebuild packets with Forge 1.12 `SimpleNetworkWrapper`.
- Keep packet responsibilities but adapt serialization and threading.
- Validate all serverbound actions server-side.
- Integrate packets with machines, planets, stations, flags, vehicles, and
  keybind state.

Current status:

- Network foundation exists.
- Packet parity is pending.

### 14. Commands, Config, Compat, and Guide Content

Target:

- Port commands where relevant to 1.12 command APIs.
- Port config options for space systems, machines, planets, entities, and
  compatibility.
- Decide compatibility scope for integrations:
  - Create
  - Mekanism
  - Thermal
  - Immersive Engineering
  - Tech Reborn
  - Modern Industrialization
  - Carry On
  - Patchouli.
- Treat integrations as optional after core content is stable.
- Port Patchouli guide only if a 1.12-compatible Patchouli dependency is used.

Current status:

- Basic config exists.
- Compat and guide content are pending decisions.

## Recommended Implementation Order

1. Replace utility/equipment/item placeholders with real behavior: wrench side
   configuration, TI-69 readouts, zip gun propulsion, gas tank oxygen/fluid
   storage, armor oxygen/temperature protection, and jet suit energy flight.
2. Replace entity placeholders with real vehicles, mobs, projectiles, spawn
   rules, AI, attributes, drops, models, and renderers.
3. Add client model/renderer binding for vehicles and mobs so entity-backed
   items and spawn eggs render correctly.
4. Extend the machine storage base into real machine behavior: ticking, side
   transfer, redstone execution rules, recipe progress, network sync, and exact
   per-machine slot validation.
5. Port machine recipes and GUIs one machine family at a time.
6. Replace visual pipe/cable placeholders with real transfer tile entities and
   wrench side configuration.
7. Implement oxygen, temperature, and gravity systems.
8. Implement rockets, rover, lander, launch pad validation, planet screen, and
   travel packets.
9. Rebuild planetary dimensions and world generation.
10. Polish client systems: HUD, particles, special renderers, radio, flags,
    globes, sliding door animation, sounds, and guide/compat content.

## Verification Gates

- `gradlew.bat build` must pass after each batch.
- Registry gap checks should be rerun after block/item/entity batches.
- Every block/item batch should verify model and texture coverage.
- Machine batches require container open/close smoke tests and NBT persistence
  tests.
- Network batches require client/server thread validation.
- Vehicle and dimension batches require manual dev-client tests.
- Worldgen batches require test worlds for each planet.
- Rendering batches require client launch checks for missing model, texture, and
  TESR errors.
