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
- `zh_cn.lang` is aligned with `en_us.lang` using the copied 1.20 Chinese strings plus current 1.12.2 UI/HUD additions.
  The latest language pass covers Radio GUI, planet selection/space station text, TI-69/HUD strings, machine energy/fluid
  tooltips, side configuration text, and Zip Gun propellant/help text with no missing keys, extra keys, placeholder
  mismatches, duplicates, or malformed lines.
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

- Full sliding-door BER animation/rendering polish. First-pass 3x3 placement,
  synced parts, lock/open/powered state, sounds, collision, and TileEntity
  persistence are implemented.
- Launch pad structure validation and rocket integration.
- Airlock and reinforced door full 1.20 renderer polish. First-pass functional
  3x3 door behavior is implemented.
- Radio station list, audio playback packets, and streaming implementation. A minimal station URL GUI with
  server-validated state update packets is implemented.
- Flag custom URL/image GUI, networking, image loading, and renderer.
- Globe special renderer and item renderer.
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
- Rocket and rover items now place first-pass vehicle entities, and the 12
  spawn egg items now create their matching Ad Astra mob entities on block use.
- Wrench has a first-pass 1.12.2 implementation for machine side modes:
  right-click cycles the clicked face's energy mode, and sneaking cycles
  backwards or fluid mode on machines with tanks.
- Etrionic Capacitor now has a first-pass Forge Energy item capability backed
  by NBT, with 250,000 FE capacity, 250 FE/t input, 500 FE/t output, a tooltip,
  durability bar, right-click active toggling, shift-right-click
  sequential/round-robin mode switching, and inventory energy distribution to
  other Forge Energy items.
- Gas Tank and Large Gas Tank now have first-pass Forge fluid item capabilities
  backed by item NBT. They only accept Ad Astra oxygen, expose empty and filled
  creative variants, show oxygen tooltip/durability-bar state, provide static
  oxygen drain helpers for later suit integration, and can distribute oxygen to
  other Forge fluid item containers while held.
- Zip Gun now has a first-pass Forge fluid item capability backed by item NBT.
  It accepts oxygen or hydrogen as propellant, exposes empty and filled
  creative variants, shows propellant tooltip/durability-bar state, consumes
  propellant while used, supports dual-wield boost, and applies low-gravity
  movement using the current dimension gravity hook.
- Jet Suit chest pieces now expose both oxygen and Forge Energy item
  capabilities, show oxygen and energy tooltip state, expose charged creative
  variants, and support a first-pass server-validated flight loop. Pressing the
  suit-flight key toggles flight, holding jump provides upward thrust, and
  holding jump plus sprint applies forward thrust while consuming FE.
- All source item ids are currently covered by standalone item registrations or
  automatically registered block items.
- Independent item model coverage is currently clean: 0 missing models.

Implementation notes:

- Armor must be rebuilt with 1.12 `ItemArmor`, armor material definitions,
  texture layers, dye/NBT behavior, and environmental protection hooks.
- Zip Gun still needs exact 1.20 particle/audio parity, better zero-gravity
  tuning, and runtime gameplay testing.
- Jet suit still needs exact 1.20 particles, model animation, Elytra-like
  glide parity, config persistence, and runtime gameplay testing.
- Gas tank machine filling, suit oxygen consumption, and exact capacity/config
  parity remain pending.
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
- Machine tanks and gas tank item capabilities are started. Suit integration
  and exact machine filling behavior remain pending.

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
- Etrionic Blast Furnace now has a first-pass alloying loop for the copied
  steel recipe that maps directly to 1.12.2: iron ingot plus coal/charcoal in
  slots 1-4 becomes steel ingot in slots 5-8 while consuming 20 FE/t for 100
  ticks. Blasting mode is persisted but remains behaviorally pending.
- NASA Workbench now has a first-pass 15-slot TileEntity behavior matching the
  copied 1.20 rocket assembly layout: slots 0-13 accept the fixed tier 1-4
  rocket ingredients, slot 14 exposes the output rocket, and completed recipes
  are consumed server-side using hardcoded 1.12.2 item/block mappings.
- The first machine GUI/container foundation is in place. The 1.12 GUI handler
  opens supported machine TileEntities from their blocks, uses copied
  `textures/gui/container` assets at native size, syncs machine fields through
  a reusable `Container`, and currently covers Coal Generator, Compressor,
  Etrionic Blast Furnace, Fuel Refinery, Oxygen Loader, Solar Panel, Water Pump,
  Energizer, Cryo Freezer, NASA Workbench, and Gravity Normalizer.
- Gravity Normalizer now has a first-pass maintenance loop with FE battery-slot
  input, bounded radius/count state, target-gravity persistence, energy-per-tick
  accounting, GUI fields, and query hooks for the later global gravity motion
  system.
- Oxygen Sensor/Detector now has a first-pass scan loop that checks nearby
  Oxygen Distributors, updates copied `lit`/`powered` blockstate properties,
  emits redstone power, persists scan state, and exposes detection fields.
  Temperature and gravity sensor modes are explicit placeholders until those
  global systems exist.
- The shared machine base now ports the 1.20 `POWER_MACHINE` battery slot
  behavior with Forge Energy: machines that can receive energy pull from
  FE-capable items in slot 0 before their server tick, and exposed item
  handlers use the same slot validation as sided inventory insertion. Energizer
  keeps slot 0 as an item-charging slot, while Coal Generator and Solar Panel
  remain generator-only.
- The shared machine base now has a first-pass Forge Energy push helper that
  uses saved per-face side modes. Coal Generator and Solar Panel default energy
  sides to `PUSH` and can send power to adjacent Forge Energy receivers.
- The wrench can now mutate saved machine side modes in-world, giving the
  side-configuration state a gameplay entry point before the GUI screens and
  side-config packets are ported. The held wrench stores a selected machine
  config type and can switch between item, energy, and fluid side modes.
- The shared machine base now has first-pass automatic side transfer. Saved
  item side modes drive item import/export through sided handlers, energy pull
  modes draw Forge Energy from adjacent extractors, and fluid modes move fluids
  through each machine's exposed fluid capability. Exact 1.20 side-config GUI,
  network packets, and per-machine transfer tuning remain pending.
- Steel cable and desh cable TileEntities now have first-pass Forge Energy
  transfer behavior with 1.20-derived rates of 150 FE/t and 500 FE/t, plus
  visual connections to adjacent Forge Energy blocks.
- Desh fluid pipe and ostrum fluid pipe TileEntities now have first-pass Forge
  Fluid transfer behavior with 1.20-derived rates of 150 mB/t and 500 mB/t,
  plus visual connections to adjacent fluid-capable blocks.
- Cable and fluid pipe TileEntities now persist per-face pipe modes, render
  configured modes on adjacent capability connections, can be cycled with the
  wrench, and obey insert/extract/normal/none when moving energy or fluids.
- Globe TileEntities now persist torque/Y rotation, rotate on right-click, keep
  rotating while redstone-powered, and sync state to clients for a later special
  renderer.
- Flag TileEntities now persist owner/name, URL, base color, and pattern
  placeholder fields, sync them to clients, and expose right-click status/reset
  feedback for later GUI and TESR work.
- Radio TileEntities now persist a station URL field and playing state, sync
  them to clients, and expose a minimal right-click station settings GUI. The
  GUI can save/clear the URL and toggle the playing flag through a server-
  validated packet. The 1.20 station list flow and audio stream are still
  pending.
- Sliding doors, airlocks, and reinforced doors now have first-pass 3x3
  placement/removal, right-click and redstone open/close behavior, wrench lock
  toggling, passable open collision, sounds, and TileEntity persistence for
  part, lock/open/powered state, and slide progress.
- Recipe execution for remaining machines, recipe JSON loading for hardcoded
  first-pass machines, exact side configuration semantics,
  networking, full GUI controls/overlays, special renderer, charge-slot GUI/sync
  polish, and most real block behavior remain pending.

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
- Direct 1.12 crafting JSON coverage is now 81 files:
  - 24 material compaction/decompaction recipes for cheese, raw
    desh/ostrum/calorite, and steel/desh/ostrum/calorite ingot/block/nugget
    loops.
  - 12 low-risk component/equipment recipes converted from generated 1.20
    crafting data: `iron_rod`, `steel_rod`, `gas_tank`, `engine_frame`, `fan`,
    `rocket_fin`, `oxygen_gear`, `steel_engine`, `desh_engine`,
    `ostrum_engine`, `calorite_engine`, and `etrionic_capacitor`.
  - 15 low-risk Moon terrain/decor recipes converted from generated 1.20
    crafting data: `moon_stone_slab`, `moon_stone_stairs`,
    `moon_cobblestone_slab`, `moon_cobblestone_stairs`, `moon_stone_bricks`,
    `moon_stone_brick_slab`, `moon_stone_brick_stairs`,
    `moon_stone_brick_wall`, `polished_moon_stone`,
    `polished_moon_stone_slab`, `polished_moon_stone_stairs`,
    `chiseled_moon_stone_bricks`, `chiseled_moon_stone_slab`,
    `chiseled_moon_stone_stairs`, and `moon_pillar`.
  - 15 low-risk Mars terrain/decor recipes converted from generated 1.20
    crafting data: `mars_stone_slab`, `mars_stone_stairs`,
    `mars_cobblestone_slab`, `mars_cobblestone_stairs`, `mars_stone_bricks`,
    `mars_stone_brick_slab`, `mars_stone_brick_stairs`,
    `mars_stone_brick_wall`, `polished_mars_stone`,
    `polished_mars_stone_slab`, `polished_mars_stone_stairs`,
    `chiseled_mars_stone_bricks`, `chiseled_mars_stone_slab`,
    `chiseled_mars_stone_stairs`, and `mars_pillar`.
  - 15 low-risk Mercury terrain/decor recipes converted from generated 1.20
    crafting data: `mercury_stone_slab`, `mercury_stone_stairs`,
    `mercury_cobblestone_slab`, `mercury_cobblestone_stairs`,
    `mercury_stone_bricks`, `mercury_stone_brick_slab`,
    `mercury_stone_brick_stairs`, `mercury_stone_brick_wall`,
    `polished_mercury_stone`, `polished_mercury_stone_slab`,
    `polished_mercury_stone_stairs`, `chiseled_mercury_stone_bricks`,
    `chiseled_mercury_stone_slab`, `chiseled_mercury_stone_stairs`, and
    `mercury_pillar`.
- Latest crafting gap pass inspected the 313 generated top-level vanilla
  crafting recipes: 286 shaped, 27 shapeless, 136 with 1.20 item tags, and no
  recipe conditions. Safe direct conversion requires either an existing 1.12
  item id or an explicit OreDictionary/tag replacement.
- Next direct-crafting candidates are the remaining simple Ad Astra decorative
  families whose inputs are already registered blocks/items or established
  ore-dict materials: Venus/Glacio stone/cobble/brick/polished/
  chiseled variants, metal panels/plateblocks/plating/button/pressure-plate/
  slab/stair variants, and aeronos/strophar/glacian wood-family recipes. Review
  vanilla 1.12 metadata mappings before converting colored wool flags or
  recipes that reference 1.20-only vanilla ids.
- Deferred recipe categories remain custom machine JSON loaders
  (`compressing`, `alloying`, `cryo_freezing`, `oxygen_loading`, `refining`,
  `nasa_workbench`, `space_station_recipe`), `stonecutting`, compatibility/tag
  recipes without an OreDictionary mapping, and recipes that would consume
  NBT-bearing filled tanks or charged items as ordinary ingredients.
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

- A shared `EnvironmentUtils` helper now checks world-provider oxygen and local
  Oxygen Distributor coverage.
- Space suit, netherite space suit, and jet suit chest pieces now expose Forge
  fluid oxygen item capabilities with empty/filled creative variants, oxygen
  tooltip, and oxygen durability bar. Jet Suit chest pieces additionally expose
  a Forge Energy item capability for powered flight.
- Server-side player ticks now drain suit oxygen in airless environments or
  apply oxygen suffocation damage when the player has no usable suit oxygen.
- Server-side player ticks now also apply first-pass Jet Suit flight using
  client-synced jump/sprint/toggle key state and consume FE from the chest
  piece before applying upward or forward thrust.
- TI-69 now has a first-pass right-click environment readout for local oxygen,
  temperature, and gravity.
- Exact 1.20 temperature damage, gravity movement, fluid freezing/evaporation,
  item destruction, TI-69 runtime UI, and HUD warning polish remain pending.

Current status:

- System APIs are not functionally ported yet.
- Client HUD has a first-pass display for oxygen, temperature, gravity, and
  energy. It currently uses inventory gas tanks/Forge Energy items and
  best-effort local machine state, while authoritative oxygen/temperature/
  gravity simulation and sync packets remain pending.

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

- Vehicle items place matching first-pass vehicle entities.
- Vehicle entity ids are registered with copied source dimensions. The rover
  has basic riding/steering, rockets carry tier values and simplified fuel, and
  first-pass client render factories make rockets, rover, and lander visible
  with simple textured placeholders.
- Rockets now open a minimal planet selection screen after ascending to the
  first-pass atmosphere threshold. The selection packet lands the player on a
  chosen registered planet when the server-side rocket tier can reach it. The
  rocket/lander entity is not transferred yet.
- Full rocket launch sequence, launch pad validation, vehicle inventory, fuel
  UI, real vehicle models, return-position storage, orbits, and space stations
  remain pending.

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
- First-pass Forge 1.12.2 dimension registration is present for moon, mars,
  mercury, venus, and glacio with stable ids 1201-1205.
- Each planet has a dedicated `WorldProvider`, source-derived placeholder
  environment hooks for oxygen, temperature, gravity, solar power, and tier, a
  fixed vanilla-biome provider, skylight/celestial-angle hooks, and a simple
  flat planetary surface generator using existing Ad Astra terrain blocks.
- True 1.20 terrain parity remains pending: the 43 hand-authored worldgen JSON
  files, generated worldgen data, crater biome/density functions, custom
  planetary biome providers, ore/feature placement, structure generation, and
  56 NBT structure files still need deliberate 1.12.2 conversion.

Worldgen/structure gap snapshot, 2026-07-01:

- Source data compared from 1.20:
  - `common/src/main/resources/data/ad_astra`: 104 relevant files:
    5 dimensions, 56 NBT structures, 4 density functions, 5 noise settings,
    1 processor list, 8 structures, 8 structure sets, and 17 template pools.
  - `common/src/main/generated/resources/data/ad_astra`: 90 relevant files:
    6 orbit dimensions, 11 dimension types, 8 worldgen biome tags, 10 biomes,
    1 configured carver, 26 configured features, 1 density function,
    1 noise setting, and 26 placed features.
  - Total worldgen/structure/dimension source surface checked: 194 files.
  - No `biome_modifier` or `biome_modifiers` files were found in the 1.20
    common resource or generated data trees; structure biome membership is
    expressed through `tags/worldgen/biome/has_structure/*`.
- Copied-but-not-loadable in 1.12.2:
  - The target resource tree contains matching copies of the same 194 relevant
    files under `src/main/resources/data/ad_astra`, but Minecraft 1.12.2 does
    not datapack-load these registries.
  - `data/ad_astra/dimension` and `dimension_type` are source material only.
    The five planet dimensions are registered by Java; the six orbit dimension
    JSONs and all 11 dimension type JSONs are not runtime inputs.
  - `worldgen/biome` has 10 copied biome JSONs. Five have first-pass Java
    equivalents, but only top/filler, temperature/rainfall, and basic spawn
    lists are represented. JSON features, carvers, effects, particles,
    precipitation/freezing, and spawn costs are not loaded.
  - `worldgen/configured_feature` and `worldgen/placed_feature` contain 26
    copied feature pairs: planet ores, `moon_soul_soil`, `mars_rock`,
    small/large infernal spire columns, and Glacio deepslate/copper variants.
    None are generated by the current 1.12.2 chunk generator.
  - `worldgen/configured_carver/moon_crater.json`, 5 density functions, and 6
    noise settings are copied but ignored by the flat 1.12.2 generator.
  - `worldgen/structure`, `structure_set`, `template_pool`, and
    `processor_list/void_processor.json` are copied but have no 1.12.2
    registry, jigsaw, random-spread, or processor implementation.
  - The 56 NBT files are copied under `data/ad_astra/structures`, but no 1.12.2
    loader/placer consumes them from that location yet. A port will need either
    a custom resource reader or a deliberate 1.12 template-resource conversion.
- Hardcoded first-pass coverage that already exists:
  - Moon, Mars, Mercury, Venus, and Glacio are registered with stable ids
    1201-1205.
  - Each planet has a dedicated `WorldProvider`, source-derived environment
    hooks for oxygen, temperature, gravity, solar power, tier, day length, fog,
    and sky color.
  - `AdAstraBiomeProvider` is a fixed-biome provider for one biome per planet.
  - `AdAstraChunkGenerator` creates a safe flat world: bedrock at y=0, filler
    from y=1 to y=62, and a surface block at y=63.
  - Five Java `PlanetBiome` instances exist: `lunar_wastelands`,
    `martian_wastelands`, `mercury_deltas`, `venus_wastelands`, and
    `glacio_snowy_barrens`. Their spawn lists cover the first-pass planet mob
    distribution, but not 1.20 spawn costs or placement predicates.
- Completely unconverted or not represented by runtime logic:
  - Orbit dimensions: `earth_orbit`, `moon_orbit`, `mars_orbit`,
    `mercury_orbit`, `venus_orbit`, and `glacio_orbit`.
  - Biome variants: `glacio_ice_peaks`, `infernal_venus_barrens`,
    `martian_canyon_creek`, `martian_polar_caps`, and `orbit`.
  - True noise terrain, caves/canyons, crater biome source, crater density
    function, Moon crater carver, and surface height variation.
  - All configured/placed feature generation, including ores, Moon soul soil,
    Mars rock blobs, and Venus infernal spires.
  - All structure placement and lookup APIs. `populate` is empty,
    `generateStructures` returns false, and nearest/inside-structure queries
    return null.
  - NBT structure placement, jigsaw/template-pool assembly, random-spread
    structure spacing/separation, structure void processing, loot/spawner
    finalization in structures, and space-station placement.

Ore vein feasibility snapshot, 2026-07-01:

- 1.12.2 runtime entry point:
  - `AdAstraWorldProvider.createChunkGenerator()` constructs
    `AdAstraChunkGenerator(world, getProperties())` for each registered planet.
  - `AdAstraChunkGenerator.generateChunk()` currently creates only the safe
    flat base terrain: bedrock at y=0, filler y=1..62, surface y=63.
  - `AdAstraChunkGenerator.populate(int chunkX, int chunkZ)` is empty and is
    the lowest-risk place to add first-pass ore veins. Keep `generateChunk()`
    terrain-only for this batch.
  - The generator already carries `PlanetDimensionProperties`, so first-pass
    ore dispatch can use `properties.getName()` or `properties.getDimensionId()`
    without consulting global dimension registries. Current `AdAstraBiomeProvider`
    is fixed per planet, so dimension-level ore tables are sufficient for the
    initial implementation; keep an optional biome allow-list in the spec for
    later biome variants.
- Suggested minimal Java-local interface for the next implementation:
  - Add a private/static `PlanetOreSpec`-style table near the chunk generator or
    in a small worldgen helper. Fields should be: planet id/name, optional
    allowed biomes, output `IBlockState`, replaceable `Block` set, vein size,
    count per chunk, source min/max Y, effective 1.12 min/max Y, and
    `discardChanceOnAirExposure` for later parity.
  - In `populate`, create a deterministic per-chunk `Random` from
    `world.getSeed()`, `chunkX`, and `chunkZ`, then run each spec's
    `countPerChunk` attempts inside the chunk.
  - Use `WorldGenMinable` with a replacement predicate instead of ad hoc block
    walking. The predicate should test the source replaceable set first, then
    be narrowed by current filler policy where needed.
  - Clamp 1.20 Y ranges to the current flat 1.12 terrain until real vertical
    terrain exists: `effectiveMinY = max(1, sourceMinY)`,
    `effectiveMaxY = min(AdAstraChunkGenerator.SURFACE_Y - 1, sourceMaxY)`.
    Skip a spec if the clamped range is empty.
  - 1.20 uses `discard_chance_on_air_exposure` for some ores. The current flat
    generator has no caves/carvers, so the first pass can store this value but
    ignore it. Revisit it when caves, craters, or noise terrain can expose
    generated veins to air.
- Replacement mapping from 1.20 tags to current 1.12 blocks:

| Planet | 1.20 ore biomes with these features | Current 1.12 biome | Source replaceables | First-pass 1.12 replacement policy |
| --- | --- | --- | --- | --- |
| Moon | `lunar_wastelands` | `ModBiomes.LUNAR_WASTELANDS` | `moon_stone`, `moon_deepslate` | Allow `MOON_STONE`; allowing `MOON_DEEPSLATE` is harmless but not visible until terrain creates it. |
| Mars | `martian_wastelands`, `martian_canyon_creek`, `martian_polar_caps` | `ModBiomes.MARTIAN_WASTELANDS` | `mars_stone` | Allow `MARS_STONE`. |
| Mercury | `mercury_deltas` | `ModBiomes.MERCURY_DELTAS` | `mercury_stone` | Allow `MERCURY_STONE`. |
| Venus | `venus_wastelands`, `infernal_venus_barrens` | `ModBiomes.VENUS_WASTELANDS` | `venus_stone` | Allow `VENUS_STONE`. |
| Glacio | `glacio_snowy_barrens`, `glacio_ice_peaks` | `ModBiomes.GLACIO_SNOWY_BARRENS` | `glacio_stone`, `permafrost` | Start with `GLACIO_STONE` to avoid punching surface permafrost; add `PERMAFROST` later if source-like near-surface replacement is desired. |

- Direct ore specs from 1.20 generated configured/placed feature data:

| Planet | 1.20 placed/configured feature | Output block in 1.12 | Vein size | Count/chunk | Source Y | Effective 1.12 Y | Notes |
| --- | --- | --- | ---: | ---: | --- | --- | --- |
| Moon | `moon_cheese_ore` | `MOON_CHEESE_ORE` | 8 | 9 | 6..192 | 6..62 | Direct Ad Astra ore block. |
| Moon | `moon_desh_ore` | `MOON_DESH_ORE` | 9 | 9 | -80..80 | 1..62 | Direct Ad Astra ore block. |
| Moon | `moon_ice_shard_ore` | `MOON_ICE_SHARD_ORE` | 10 | 8 | -32..32 | 1..32 | Direct Ad Astra ore block. |
| Moon | `moon_iron_ore` | `MOON_IRON_ORE` | 11 | 10 | -24..56 | 1..56 | Direct Ad Astra planet iron ore block; drop/smelt mapping is already a separate loot/smelting concern. |
| Mars | `mars_diamond_ore` | `MARS_DIAMOND_ORE` | 7 | 5 | -80..80 | 1..62 | Direct Ad Astra ore block. |
| Mars | `mars_ice_shard_ore` | `MARS_ICE_SHARD_ORE` | 10 | 8 | -32..32 | 1..32 | Direct Ad Astra ore block. |
| Mars | `mars_iron_ore` | `MARS_IRON_ORE` | 11 | 10 | -24..56 | 1..56 | Direct Ad Astra planet iron ore block. |
| Mars | `mars_ostrum_ore` | `MARS_OSTRUM_ORE` | 8 | 8 | -80..80 | 1..62 | Direct Ad Astra ore block. |
| Mercury | `mercury_iron_ore` | `MERCURY_IRON_ORE` | 8 | 20 | -80..192 | 1..62 | Direct Ad Astra planet iron ore block. |
| Venus | `venus_calorite_ore` | `VENUS_CALORITE_ORE` | 8 | 8 | -80..80 | 1..62 | Direct Ad Astra ore block. |
| Venus | `venus_coal_ore` | `VENUS_COAL_ORE` | 17 | 20 | -80..192 | 1..62 | Direct Ad Astra ore block. |
| Venus | `venus_diamond_ore` | `VENUS_DIAMOND_ORE` | 9 | 5 | -80..80 | 1..62 | Direct Ad Astra ore block. |
| Venus | `venus_gold_ore` | `VENUS_GOLD_ORE` | 10 | 4 | -64..32 | 1..32 | Direct Ad Astra planet gold ore block. |
| Glacio | `glacio_coal_ore` | `GLACIO_COAL_ORE` | 17 | 20 | -80..192 | 1..62 | Direct Ad Astra ore block. |
| Glacio | `glacio_copper_ore` | `GLACIO_COPPER_ORE` | 17 | 16 | -16..112 | 1..62 | Block can be generated directly, but gameplay output is still a separate 1.12 copper decision. Safe to include only if block placement parity is the goal. |
| Glacio | `glacio_ice_shard_ore` | `GLACIO_ICE_SHARD_ORE` | 17 | 8 | -32..32 | 1..32 | Direct Ad Astra ore block. |
| Glacio | `glacio_iron_ore` | `GLACIO_IRON_ORE` | 11 | 10 | -24..56 | 1..56 | Direct Ad Astra planet iron ore block. |
| Glacio | `glacio_lapis_ore` | `GLACIO_LAPIS_ORE` | 9 | 2 | -32..32 | 1..32 | Direct Ad Astra ore block. |

- Defer from the first "place Ad Astra planet ore blocks" pass:
  - `glacio_deepslate_coal_ore`, `glacio_deepslate_copper_ore`,
    `glacio_deepslate_iron_ore`, and `glacio_deepslate_lapis_ore` output
    vanilla `minecraft:deepslate_*_ore` in 1.20 and target
    `minecraft:deepslate_ore_replaceables`. Minecraft 1.12.2 has neither
    vanilla deepslate nor vanilla copper. Do not generate these until a
    deliberate 1.12 deepslate/copper policy exists.
  - `moon_soul_soil` is a configured ore feature in 1.20 data, but it outputs
    vanilla soul soil, not an Ad Astra ore block. Keep it for the later simple
    non-NBT feature batch.

Low-risk ore implementation order:

1. Add only the population harness in `AdAstraChunkGenerator.populate`: seed
   random, resolve planet specs from `properties`, clamp source Y ranges, and
   call a helper such as `generateOreVeins`.
2. Implement replacement predicates and generate one planet first, preferably
   Moon, because its current world uses a simple `MOON_STONE` filler and has
   four direct ore blocks with varied count/Y settings.
3. Add Mars, Mercury, Venus, and non-copper Glacio direct ore specs after Moon
   smoke testing. Keep Glacio copper behind an explicit decision flag or a
   clearly named spec group if the team wants block-placement parity before
   copper output parity.
4. Store but ignore air-exposure discard on the first pass. Add it only after
   caves/craters/noise terrain can expose veins.
5. Leave deepslate variants, `moon_soul_soil`, `mars_rock`, infernal spires,
   NBT structures, and crater/noise terrain out of this batch.

Ore acceptance checklist for the next Java batch:

- A new world/chunk on each planet contains the expected direct ore blocks in
  the filler layer and no ores in orbit/Overworld dimensions.
- Moon, Mars, Mercury, Venus, and Glacio generation all use the planet's
  current `PlanetDimensionProperties` and fixed biome without cross-planet ore
  leakage.
- Ores replace only the intended planetary filler/replacement blocks; surface
  blocks remain intact unless a spec deliberately allows them.
- Clamped Y ranges are visible in practice: shallow ores such as Moon cheese can
  appear above y=6, low-range ores such as Venus gold/Glacio lapis stay below
  y=32, and no ore attempts target y=0 bedrock or y=63 surface.
- Recreating the same world seed produces stable ore locations for the same
  chunk coordinates.
- The batch does not introduce Java/resource edits outside the worldgen classes
  selected for implementation, and no 1.20 datapack JSON is treated as a live
  1.12 runtime input.

NBT/resource grouping:

- Early single-template candidates:
  - 6 meteor NBT variants: `meteor1` through `meteor6`. These are copied but
    not referenced by 1.20 structure JSON in the inspected trees, so a 1.12.2
    placement policy must be chosen deliberately.
  - 1 oil well NBT: represented in 1.20 by `oil_well` structure,
    `oil_well` structure set, and a size-1 template pool pointing at
    `ad_astra:oil`.
- Larger structure families:
  - Moon dungeon: 20 NBT pieces plus 4 dungeon template pools and
    `moon_dungeon` structure/structure_set data.
  - Lunarian village: 16 village pieces plus 5 template pools and
    `lunarian_village` structure/structure_set data.
  - Crimson/Pygro/Venus structures: 8 crimson pieces, `venus_tower.nbt`,
    `warped_watch_tower.nbt`, and Pygro/Venus structure entries that depend on
    template pools and biome tags.
  - Mars temple and lunar tower are single NBT starts in their pools, but should
    still wait until the basic NBT loader and loot/chest handling are stable.
  - `space_station.nbt` is copied, but its placement belongs with station/travel
    flow rather than normal chunk population.

Low-conflict worldgen implementation order:

1. Add a small 1.12.2 population hook in `AdAstraChunkGenerator.populate` with
   deterministic per-chunk random seeding, planet checks, surface lookup, and
   replaceable-block helpers. Keep configuration Java-local at first; do not
   introduce a full JSON/datapack loader as the first step.
2. Port ore veins as the first visible generation feature. Start with the
   straightforward planet ore sets: Moon desh/cheese/ice shard/iron, Mars
   ostrum/iron/diamond/ice shard, Mercury iron, Venus calorite/coal/diamond/gold,
   and Glacio coal/iron/lapis/ice shard. Treat Glacio copper/deepslate variants
   as explicit follow-up decisions because 1.12.2 has no vanilla copper/deepslate
   equivalents. Clamp or remap 1.20 negative-Y height ranges to the current
   1.12.2 terrain height instead of copying them literally.
3. Add non-NBT simple surface features: `moon_soul_soil`, `mars_rock` as a
   small blob feature, then small/large infernal spire columns. These are
   isolated from structure code and exercise planet-specific feature dispatch.
4. Add a minimal NBT template loading/placement path and validate it with
   meteor variants or the size-1 oil well template. This should include
   rotation, ground projection, bounding-box checks, and block/entity NBT
   compatibility checks before larger structures use it.
5. Port single-start structures after the template path is reliable: oil well
   if not already used as the loader test, then lunar tower and Mars temple.
   Wire spacing/separation only as simple 1.12.2 chunk predicates at first.
6. Port multi-piece structure assembly later: Moon dungeon, Lunarian village,
   Pygro/crimson village and towers, Venus bullet/tower, and their biome-gate
   tags. These require either a 1.12.2 jigsaw-like assembler or hand-authored
   piece connection logic, so they are not low-conflict first batches.
7. Tackle crater/noise terrain after features and template placement have smoke
   tests. Crater carving, density functions, and real planetary terrain all
   cross-cut chunk generation and can invalidate earlier placement assumptions.
8. Keep orbit dimensions and space-station structure placement separate from
   normal world population. They depend on travel/station flow, not just chunk
   generation.

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
- The 12 mob ids have first-pass 1.12.2 AI task wiring and attributes.
- Sulfur creepers have a simple server-side fuse/explosion behavior plus synced
  fuse/powered state, creeper-shaped rendering, swelling flash/scale, and a
  charged overlay.
- The 12 spawn egg item ids create their matching first-pass mob entities on
  block use; entity entries retain source spawn egg colors where applicable.
- Planet dimensions use first-pass Ad Astra biomes with source-derived natural
  spawn lists.
- Forge 1.12 client render factories are registered for all 20 entity ids.
  Most mobs use copied Ad Astra textures on safe vanilla-model placeholders,
  sulfur creepers use a creeper-shaped renderer with swelling/charge visuals,
  vehicles use simple textured box placeholders, Ice Spit renders as an item
  projectile, and Air Vortex has a small translucent placeholder renderer.
- Exact source AI, spawn placement predicates, mob charges, drops, full projectile launch behavior, vehicle
  behavior, real renderers, animation layers, and models remain pending.

Entity model/render gap snapshot, 2026-07-01:

- 1.12.2 entity registry coverage is complete for the 20 source ids, but this
  is still mostly placeholder gameplay coverage.
- 1.12.2 client renderer factory coverage is complete for the 20 source ids,
  but no 1.20 Java entity model class has been ported yet. Most mobs still use
  `ModelBiped`; `sulfur_creeper` now uses vanilla `ModelCreeper` with copied
  Ad Astra texture and a vanilla-style charged layer. Vehicles use
  `TexturedBoxModel`, `ice_spit` uses `RenderSnowball`, and `air_vortex` uses a
  custom translucent cube.
- Entity texture resources are copied with source parity under
  `assets/ad_astra/textures/entity`: 35 source files and 35 target files.
  Several copied textures are not selected by current renderers yet, including
  Lunarian profession variants and `sheared_glacian_ram`.
- Current natural spawns are first-pass planet biome lists only. Source spawn
  placement predicates and exact biome distribution remain pending.

Per-entity gap matrix:

| Entity id | Current 1.12.2 status | 1.20 source target | Remaining gaps |
| --- | --- | --- | --- |
| `air_vortex` | Registered class, size, tracker, visible translucent cube renderer. No force behavior. | Invisible/no-op renderer, no summon/save, 600 tick lifetime, source block and affected-position set, pulls entities unless ignored. | Tie to Oxygen Distributor output, lifetime/discard rules, ignore tag logic, force math, and render parity. The current visible cube is only a debug-style placeholder. |
| `tier_1_rover` | Registered, item places it, basic single-rider steering, box renderer with copied texture. | `Rover` vehicle with 18-slot inventory, fuel fluid tank, two passengers, radio/inventory multipart hitboxes, collision damage, wheel animation, model and item renderer. | Real `RoverModel` port, wheel animation, fuel/inventory GUI, two-passenger seating, multipart interactions, radio station handoff, run-over damage, item renderer. |
| `tier_1_rocket` | Registered, item places it, simplified fuel integer, riding/ascent, first-pass planet screen trigger, box renderer. | `Rocket` vehicle with tier properties, fluid fuel tank, 10-slot inventory, launch-pad binding, countdown, launch sound, smoke/flame particles, burn/explosion logic, model and item renderer. | Real model/renderer, launch countdown/pad validation, fuel container, inventory/menu, particles/sounds, obstruction/explosion, return vehicle transfer, item renderer. |
| `tier_2_rocket` | Same first-pass base as tier 1 with tier 2 size/fuel values. | Same `Rocket` source class with tier 2 properties and texture/layer. | Same rocket gaps; verify tier reach/fuel/capacity after source-like launch flow exists. |
| `tier_3_rocket` | Same first-pass base as tier 1 with tier 3 size/fuel values. | Same `Rocket` source class with tier 3 properties and texture/layer. | Same rocket gaps; verify tier reach/fuel/capacity after source-like launch flow exists. |
| `tier_4_rocket` | Same first-pass base as tier 1 with tier 4 size/fuel values. | Same `Rocket` source class with tier 4 properties, taller model, and different riding offset. | Same rocket gaps plus tier 4-specific model scale/rider offset/fuel behavior. |
| `lander` | Registered, shared vehicle motion, basic riding, box renderer with copied texture. | `Lander` vehicle with hidden rider, descent thrust, 11-slot inventory/menu, landing safety, fall explosion, sound/particles, model renderer. | Real lander model/renderer, descent controls, rider hiding/offset, inventory/menu, particles/sounds, fall explosion and landing rules. |
| `lunarian` | Registered, spawn egg, generic placeholder mob AI/attributes, biped renderer using default texture. | Villager-derived Lunarian with custom model, profession-specific textures, head/item layers, breeding, avoidance behavior, and villager data. | Replace generic hostile placeholder behavior, profession/data sync, trades/breeding, correct renderer/model, profession texture selection, held/head layers. |
| `corrupted_lunarian` | Registered, spawn egg, source-like base health/move/attack attributes, melee plus first-pass ranged `ice_spit` AI, targets players/villagers/Lunarian wandering traders/golems, biped renderer with copied texture. | Monster with melee and ranged `IceSpit`, targets players/villagers/traders/golems, custom extra-arm model and animation. | Real model/animation, exact target parity for all Lunarian trader variants, drops/sounds. |
| `lunarian_wandering_trader` | Registered, spawn egg, passive placeholder mob, biped renderer with copied texture. | Wandering Trader-derived Lunarian trader with custom Lunarian model/layers and merchant offers/spawner support. | Trader inheritance/AI, offers, spawn manager, renderer/model/layers, exact despawn and interaction behavior. |
| `star_crawler` | Registered, spawn egg, generic hostile melee AI/attributes, biped renderer with copied texture. | Monster with `StarCrawlerModel` and source attack/wander timings. | Real model/animation, source attributes/AI tuning, drops/sounds/spawn predicates. |
| `martian_raptor` | Registered, spawn egg, generic hostile melee AI/attributes, biped renderer with copied texture. | Monster with `MartianRaptorModel`, melee AI, leap-at-target behavior, and source attributes. | Real model/animation, leap behavior, source attributes/AI tuning, drops/sounds/spawn predicates. |
| `pygro` | Registered, spawn egg, generic hostile melee AI/attributes, biped renderer with copied texture. | Piglin-derived legacy entity with `PygroModel`, held-item layer, and armor layer. | Real model, item/armor layers, piglin-style behavior/equipment, source attributes, drops/sounds. |
| `zombified_pygro` | Registered, spawn egg, generic hostile melee AI/attributes, fire immune, biped renderer. | Zombified Piglin-derived entity with `ZombifiedPygroModel`, held-item layer, and armor layer. | Real model, item/armor layers, zombified anger/equipment behavior, source attributes, drops/sounds. |
| `pygro_brute` | Registered, spawn egg, generic hostile melee AI/attributes, fire immune, biped renderer. | Piglin Brute-derived entity with `PygroBruteModel`, held-item layer, and armor layer. | Real model, item/armor layers, brute AI/equipment behavior, source attributes, drops/sounds. |
| `mogler` | Registered, spawn egg, generic hostile melee AI/attributes, fire immune, biped renderer. | Hoglin-derived entity with `MoglerModel`, high health, knockback, and hoglin-style combat. | Real model, hoglin-style AI/knockback, source attributes, drops/sounds. |
| `zombified_mogler` | Registered, spawn egg, generic hostile melee AI/attributes, fire immune, biped renderer. | Zoglin-derived entity rendered with `MoglerModel` and zombified texture. | Real model, zoglin-style AI/knockback, source attributes, drops/sounds. |
| `sulfur_creeper` | Registered, spawn egg, simple fuse/explosion behavior, synced fuse and powered state, lightning charge persistence, creeper-shaped renderer, swelling flash/scale, and charged overlay with copied sulfur texture. | Creeper-derived entity with swelling scale, powered/charge layer, source model, effect cloud behavior, and suit oxygen drain on explosion. | Port the 1.20 `SulfurCreeperModel`, source explosion side effects including suit oxygen drain and effect cloud behavior, exact attributes/AI tuning, drops/sounds/spawn predicates, and runtime dev-client visual testing. |
| `glacian_ram` | Registered, spawn egg, generic neutral melee placeholder, biped renderer using normal texture. | Animal and `Shearable` entity with `GlacianRamModel`, sheared texture, ice-shard food, breeding, milking, permafrost eating, neck/head animation. | Real animal behavior, sheared state/data sync, shearing/milking/breeding, eat-permafrost goal, model/normal/sheared texture selection, sounds. |
| `ice_spit` | Registered projectile, `RenderSnowball` with `ice_shard`, owner/position constructors, 4 thrown damage on entity hit, SPIT/SNOWBALL particle trail, broadcast discard event, and first-pass corrupted Lunarian ranged attack integration. | Throwable item projectile with ice shard default item, item/snowball particles every tick, 4 thrown damage on entity hit, discard event. | Runtime test trajectory, damage, and particles in a dev client. |

Next low-conflict entity/render implementation order:

1. Port renderer/model bindings for `star_crawler` and `martian_raptor`.
   These are hostile mobs with no inventory, passenger, or merchant systems.
   Add the raptor leap AI only after the model render pass is stable.
2. Port `glacian_ram` model with normal texture first. Defer sheared texture
   selection, shearing, milking, breeding, and eat-permafrost animation until a
   data-watched sheared/eating state is added.
3. Port Pygro-family model bindings in this order: `pygro`,
   `zombified_pygro`, `pygro_brute`. Defer item/armor layers and piglin-style
   AI until the base model renderers are stable.
4. Port Mogler-family model bindings: `mogler`, then `zombified_mogler`.
   Defer Hoglin/Zoglin knockback and behavior parity.
5. Port Lunarian-family model bindings with default textures:
   `lunarian`, `corrupted_lunarian`, then `lunarian_wandering_trader`.
   Profession textures, merchant offers, wandering trader spawning, and
   corrupted ranged attacks should remain separate follow-up batches.
6. Port vehicle model renderers after the mob renderers: rockets first, then
   lander, then rover. Treat item renderers as a separate client-only batch if
   1.12 item rendering needs extra hooks.
7. Do vehicle behavior after visual parity: rover fuel/inventory/radio/two-seat
   control, rocket countdown/fuel/launch-pad/menu flow, then lander descent and
   fall explosion. These touch networking, GUI, and travel flow, so they are not
   low-conflict renderer-only work.
9. Defer full `air_vortex` behavior until Oxygen Distributor coverage is ready
   to provide source and affected-position data. A small render-parity change to
   make it invisible is safe, but the force behavior depends on oxygen system
   state.

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

- Sound events are registered first-pass, and `assets/ad_astra/sounds.json`
  plus the 14 copied `.ogg` assets are aligned with the 1.20 source sound set.
- A first-pass Forge 1.12 `RenderGameOverlayEvent` HUD is present for compact
  oxygen/temperature/gravity/energy readouts. Exact 1.20 overlay visuals,
  warning states, vehicle overlays, and config-positioned bars remain pending.
- Entity render factory coverage is present for all 20 registered Ad Astra
  entities using safe first-pass placeholder renderers and copied textures where
  available. Screens, special block renderers, particles, and real entity models
  remain mostly pending.

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
   configuration, TI-69 readouts, gas tank oxygen/fluid storage, armor
   oxygen/temperature protection, and jet suit energy flight. Refine Zip Gun
   particles/audio/tuning after its first-pass propellant propulsion baseline.
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
