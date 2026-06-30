# Ad Astra Reborn 1.12.2 Porting Roadmap

This project is a Cleanroom/Forge 1.12.2 port of the Ad Astra 1.20.x source tree.
The current goal is feature parity over time, not a narrow compatibility shim.

## Current Foundation

- Mod identity changed from the template placeholder to `ad_astra`.
- Java package root is `earth.terrarium.adastra`.
- Main mod entrypoint is `earth.terrarium.adastra.AdAstraReborn`.
- Coremod loading plugin is `earth.terrarium.adastra.AdAstraLoadingPlugin`.
- Mixin configs are `ad_astra.default.mixin.json` and `ad_astra.mod.mixin.json`.
- Access transformer file is `ad_astra_at.cfg`.
- Forge event registration placeholders exist for blocks, items, entities, sounds, tile entities, GUI, network, config, common events, and client events.
- The project currently builds successfully with `gradlew.bat build`.

## Current Content Status

- Creative tab exists as `AdAstraCreativeTab`.
- First-pass client model binding exists in `ClientRegistry`.
- First-pass sound events, `sounds.json`, and copied `.ogg` assets are aligned for the 1.20 sound keys:
  `rocket_launch`, `rocket`, `wrench`, `sliding_door_close`, `sliding_door_open`,
  `oxygen_intake`, `oxygen_outtake`, and `gravity_normalizer_idle`.
- A broad `en_us.lang` has been generated from the copied 1.20 `en_us.json` with 1.12.2 key names.
- `zh_cn.lang` and `zh_cn.json` are aligned with their current English key sets
  and include the first pass of Chinese player-facing UI, HUD, tooltip, radio,
  planet selection, space station, Zip Gun, gas tank, tag, and config strings.
- The first registered gameplay content is intentionally limited to simple materials, food, and plain blocks.
- The second registered block batch adds 1.12.2 vanilla-behavior variants where the original asset states already fit:
  stairs, walls, buttons, pressure plates, fences, fence gates, ladders, normal doors, and trapdoors.
- The third registered block batch adds slabs with hidden double-slab blocks, slab state mapping for the copied
  1.20 `type=bottom/top/double` blockstates, axis-based pillars/logs, and huge-mushroom-style aeronos/strophar stems.
- The fourth registered block batch adds vent, metal pillar/glowing pillar/marked pillar variants, and all 32
  industrial lamp variants with 1.20-compatible `face`/`facing` blockstates.
- The first loot/drop behavior batch adds a 1.12 ore wrapper and ports drops/experience for registered Ad Astra
  resource ores with direct 1.12 targets: raw desh, raw ostrum, raw calorite, cheese, ice shard, coal, diamond,
  and lapis lazuli.
- The first 1.12 recipe batch adds 24 `assets/ad_astra/recipes` files for material compaction/decompaction:
  cheese, raw desh/ostrum/calorite blocks, and steel/desh/ostrum/calorite ingot/block/nugget loops.
- The first OreDictionary batch registers Ad Astra ingots, nuggets, plates, rods, raw materials, raw blocks,
  resource blocks, ores, cheese, and ice shard entries. The first 24 direct crafting recipes now use
  `forge:ore_dict` ingredients instead of fixed item-only inputs.
- The second direct crafting recipe batch adds 12 low-risk generated 1.20 crafting conversions for plain component and
  equipment items: iron rod, steel rod, gas tank, engine frame, fan, rocket fin, oxygen gear, tiered engines, and
  etrionic capacitor. The third through eighth direct crafting recipe batches add 87 low-risk Moon, Mars, Mercury,
  Venus, Glacio, and Permafrost terrain/decor conversions covering stone/cobble/brick/polished/chiseled/permafrost
  variants, slab, stair, wall, tile, and pillar outputs. The ninth direct
  crafting batch adds 20 low-risk metal decorative recipes for iron, steel,
  desh, ostrum, and calorite plating, plateblock, panel, and pillar blocks. The
  tenth direct crafting batch adds 20 matching metal plating button, pressure
  plate, slab, and stair recipes. The eleventh direct crafting batch adds 25
  low-risk aeronos, strophar, and glacian wood-family recipes. The twelfth
  direct crafting batch adds 16 colored flag recipes using 1.12 wool metadata.
  The thirteenth direct crafting batch adds 32 normal and small industrial lamp
  recipes using 1.12 dye metadata and the existing `ingotSteel` OreDictionary
  mapping. The fourteenth direct crafting batch adds 17 low-risk factory block,
  encased block, pipe, launch pad, vent, photovoltaic cell, wheel, and wrench
  recipes. The fifteenth direct crafting batch adds 17 airlock, door, sliding
  door, Venus sandstone, polished conglomerate, sky stone, radio, TI-69, and
  space helmet recipes.
  These passes deliberately skipped machine recipes, stonecutting, compatibility/tag-heavy recipes, 1.20-only vanilla
  ids, and recipes that would consume filled/charged NBT-bearing items as ordinary ingredients.
- The first smelting conversion batch adds `ModSmeltingRecipes` and ports the 1.20 smelting/blasting sources that
  have direct 1.12.2 equivalents: raw desh/ostrum/calorite, Ad Astra resource ores, vanilla coal/diamond/iron/gold/
  lapis ore outputs, cobblestone-to-stone conversions, and cracked planetary brick conversions.
- The first non-machine visual placeholder batch registers launch pad, airlock, reinforced door, aeronos mushroom,
  and strophar mushroom using copied 1.20 assets while their multi-block/launch/door behavior remains pending.
- Radio now has a first-pass 1.12.2 block and TileEntity implementation with an eight-direction `facing` property,
  persisted station URL field, persisted playing state, vanilla TileEntity client sync, and a minimal station settings
  GUI. Right-click opens the GUI, and client edits are sent through server-validated packets before updating the
  TileEntity. Streaming audio and shared station list data remain pending.
- The first machine visual placeholder batch registers the nine standard `facing`/`lit`/`powered` machine blocks:
  coal generator, compressor, etrionic blast furnace, NASA workbench, fuel refinery, oxygen loader, solar panel,
  water pump, and cryo freezer. Their tile entities, containers, recipes, energy/fluid behavior, and GUIs remain pending.
- The first flag TileEntity behavior batch registers all 16 flag colors with 1.20-compatible `facing`, `half`, and
  `waterlogged` blockstates, persists owner/name, URL, base color, and pattern placeholder fields, syncs them to
  clients, and adds right-click status/reset feedback for the later URL GUI and TESR. Flag image loading and special
  renderers remain pending.
- The first globe TileEntity behavior batch registers the six planet globes with 1.20-compatible `powered` and
  `waterlogged` blockstates, persists 1.20-style torque/Y rotation state, rotates on right click, and keeps spinning
  while redstone-powered. Special renderers and rendered item handling remain pending.
- Cable duct and fluid pipe duct now have simple 1.12.2 visual placeholder blocks using the copied 1.20 cube models.
  Actual pipe/cable tile entities, transfer networks, and side connection behavior remain pending.
- The first special machine state placeholder batch registers oxygen distributor, gravity normalizer, energizer, and
  oxygen sensor with 1.20-compatible blockstate properties. Their TileEntities, menus, recipes, energy/fluid behavior,
  detection behavior, and GUIs remain pending.
- The first pipe/cable visual connection batch registers steel cable, desh cable, desh fluid pipe, and ostrum fluid
  pipe with 1.20-compatible six-sided connection properties. Adjacent pipes render as `normal` connections; insert,
  extract, transfer networks, side configuration, and TileEntities remain pending.
- The first cable transfer batch gives steel cable and desh cable TileEntities a simplified Forge Energy buffer and
  tick-based transfer loop. Steel cable moves up to 150 FE/t and desh cable moves up to 500 FE/t between adjacent
  Forge Energy handlers, and energy cables now visually connect to adjacent Forge Energy blocks. Full pipe-network
  controllers and multi-pipe network balancing remain pending.
- The first fluid pipe transfer batch gives desh fluid pipe and ostrum fluid pipe TileEntities a simplified Forge
  `FluidTank` buffer and tick-based transfer loop. Desh fluid pipe moves up to 150 mB/t and ostrum fluid pipe moves up
  to 500 mB/t between adjacent Forge fluid handlers, and fluid pipes now visually connect to adjacent fluid-capable
  blocks. Full pipe-network controllers, multi-pipe network balancing, and machine-specific fluid recipes remain
  pending.
- The first pipe side-mode batch adds per-face `none`/`normal`/`insert`/`extract` state to cable and fluid pipe
  TileEntities, persists it to NBT, displays configured connection modes in the copied 1.20 pipe blockstates, lets the
  wrench cycle a clicked pipe face, and makes transfer logic obey those modes. Adjacent pipe-to-pipe connections still
  render/connect as normal, so full network-aware mode propagation remains pending.
- The sliding door/airlock first-pass behavior now covers iron, steel, desh, ostrum, calorite, airlock, and reinforced
  door variants as 3x3 structures. Placement fills all parts, breaking any part removes the full door without duplicate
  drops, right-click toggles open/closed, Shift+right-click with a wrench toggles the saved lock flag, redstone syncs
  the powered state, collision becomes passable while open, and TileEntities persist part, lock/open/powered state, and
  slide progress. Full 1.20 animated BER rendering remains a later polish pass.
- The first Forge fluid registration batch adds oxygen, hydrogen, oil, fuel, and cryo fuel as Forge 1.12.2 `Fluid`
  instances and `BlockFluidClassic` blocks. Fluid buckets, gas tanks, machine tanks, freezing/evaporation behavior,
  and Botarium container replacement remain pending.
- The first fluid bucket item batch registers oxygen bucket, hydrogen bucket, oil bucket, fuel bucket, and cryo fuel
  bucket with copied 1.20 item assets and 1.12 `ItemBucket` behavior. Machine tanks and stricter oxygen/hydrogen
  placement rules remain pending.
- The first standalone utility/equipment item coverage batch registers wrench, zip gun, TI-69, etrionic capacitor,
  gas tank, large gas tank, all three space suit armor sets, space painting, rocket/rover items, and the 12 spawn egg
  item ids. Space suit items are currently wearable armor placeholders using copied 1.20 item/entity armor assets.
  Rocket/rover items place first-pass vehicles, and the 12 spawn eggs create their matching first-pass mob entities.
  TI-69 now has a first-pass local environment readout. Zip gun now has first-pass Forge fluid propellant storage and
  right-click propulsion using oxygen or hydrogen; Space Painting now places a first-pass 1.12 hanging entity with
  copied variant textures. Particle/audio parity, exact zero-gravity tuning, exact painting title/author polish,
  exact vehicle flow, and exact spawn egg behavior still need follow-up behavior. Jet Suit chest pieces now expose
  Forge Energy storage and first-pass server-validated powered flight via synced jump/sprint/toggle key state.
- The first entity registry coverage batch registers all 20 source entity ids with minimal 1.12.2 placeholder classes:
  air vortex, tier 1 rover, four rocket tiers, lander, 12 mob ids, and ice spit. A 1.12-only `space_painting`
  hanging entity is also registered to carry the 1.20 painting variants that no longer have a painting-variant registry
  in this runtime. These entries preserve registry ids,
  source dimensions, fire immunity where obvious, tracker settings, and spawn egg colors, but they do not yet implement
  real vehicle behavior, AI, spawn rules, attributes, drops, projectiles, renderers, or models.
- The first mob AI/attribute batch gives the 12 Ad Astra mob ids baseline 1.12.2 movement, swimming, wandering,
  watching, melee, hurt-response, hostile/player-targeting, and passive/neutral behavior where appropriate. Each mob now
  has first-pass health, movement speed, attack damage, and follow range attributes, and sulfur creepers have a simple
  server-side fuse/explosion behavior. Exact 1.20 AI, drops, sounds, animations, renderers, and models
  remain pending.
- The first TileEntity registry coverage batch registers all 19 source block entity ids and connects the machine,
  globe, flag, sliding door, cable, fluid pipe, and radio block classes to minimal placeholder TileEntity classes.
  These TileEntities currently preserve ids and world attachment only; inventories, energy, fluids, ticking, networking,
  GUIs, side configuration, special rendering, and real block behavior remain pending.
- The first machine storage foundation batch adds a common 1.12.2 machine TileEntity base with item handler inventory,
  Forge Energy storage, Forge `FluidTank`, NBT persistence, redstone-control state, and per-face item/energy/fluid side
  mode state. The 13 machine TileEntities now use this base with first-pass slot counts and tier capacities derived from
  the 1.20 machine constructors/config. Recipe execution, GUI containers, network sync, and exact machine-specific rules
  remain pending.
- The first real machine behavior batch makes the shared machine base tick server-side and ports the Coal Generator's
  basic fuel-to-energy loop: slot 1 accepts furnace fuel, burn progress persists to NBT, generated energy is inserted
  internally at 20 FE/t, and the copied `lit` blockstate is synced while the generator runs. Coal Generator GUI,
  charge-item handling, automatic side transfer, config UI, and network sync remain pending.
- The first Solar Panel behavior batch adds daylight/sky checks and server-side generation into its internal Forge
  Energy buffer. It currently uses the copied Earth planet metadata value of 16 FE/t as a fixed fallback until the
  1.12.2 planet data loader and per-dimension solar power system are ported.
- The first Water Pump behavior batch ports the core server-side pump loop: when redstone control allows it, the block
  below is water, at least 20 FE is available, and its tank has room, the pump consumes 20 FE/t and produces 50 mB/t of
  water into its internal Forge `FluidTank`. Water Pump GUI, particles, fluid container slots, and exact side-config
  automation remain pending.
- The first item energy batch replaces the Etrionic Capacitor placeholder with a Forge Energy item capability backed by
  NBT. It stores 250,000 FE, accepts up to 250 FE/t, extracts up to 500 FE/t, displays a first-pass tooltip and
  durability bar, supports right-click active toggling, supports shift-right-click sequential/round-robin mode switching,
  and can distribute energy from the player's inventory to other Forge Energy items every 5 ticks.
- The first gas tank item batch replaces the gas tank and large gas tank placeholders with Forge fluid item capabilities
  backed by item NBT. Gas tanks only accept Ad Astra oxygen, store 1,000 mB and 3,000 mB respectively, show oxygen
  tooltip/durability-bar state, expose static oxygen drain helpers for later suit integration, and can distribute oxygen
  to other Forge fluid item containers while held.
- The first Energizer behavior batch lets the Energizer use its 2,000,000 FE internal buffer to charge Forge Energy
  items in slot 0 at up to the ostrum-tier 500 FE/t rate while retaining adjacent energy push behavior. Energizer GUI,
  particles, block item charge persistence, and exact side-config automation remain pending.
- The first Fuel Refinery behavior batch ports the oil-to-fuel loop with separate 3,000 mB input/output tanks,
  two-stage fluid-container slot transfers, Forge fluid capability input/output, 30 FE and 5 mB oil consumed per
  operation, 5 mB fuel produced per operation, NBT persistence for both tanks, and first-pass tank GUI fields. Fuel
  Refinery GUI, network sync, recipe JSON loading, and exact side-config automation remain pending.
- The first Oxygen Loader behavior batch ports the water/oxygen-to-oxygen loop with separate 3,000 mB input/output
  tanks, two-stage fluid-container slot transfers, Forge fluid capability input/output, 30 FE consumed per operation,
  water-to-oxygen and oxygen-pass-through source recipe semantics, NBT persistence for both tanks, and first-pass tank
  GUI fields. Oxygen Loader GUI, network sync, recipe JSON loading, and exact side-config automation remain pending.
- The first Oxygen Distributor behavior batch gives the Distributor a usable server-side maintenance loop: slot 0 can
  pull FE from batteries, slot 1 accepts oxygen or water fluid containers, the inherited DESH tank stores oxygen or
  water, and the machine consumes FE plus oxygen-equivalent fluid to maintain a radius/count state. It persists working
  state/radius/count/consumption data, exposes first-pass GUI fields, and leaves `isProvidingOxygen`/`getWorkingRadius`
  hooks for the later global oxygen system. Sealed-room flood fill, player oxygen effects, particles, TESR, GUI screens,
  recipe JSON loading, and exact side-config automation remain pending.
- The first Oxygen Sensor/Detector behavior batch gives the sensor a server-side scan loop that checks nearby Oxygen
  Distributors through `isProvidingOxygen(BlockPos)`, persists detection/cooldown/radius state, exposes first-pass GUI
  fields, drives the copied `lit`/`powered` blockstates, and emits redstone power. Temperature and gravity detection
  modes are explicit placeholders until the global systems are ported.
- The first Gravity Normalizer behavior batch gives the normalizer a matching first-pass server-side maintenance loop:
  slot 0 can pull FE from batteries, the machine estimates a bounded spherical working area, consumes FE based on the
  covered block count, persists radius/count/energy/target-gravity state, exposes GUI fields, and leaves
  `isNormalizingGravity`/`getTargetGravity` hooks for the later global gravity system. Sealed-room flood fill, actual
  entity motion overrides, idle sound, particles, TESR, target-gravity controls, and exact side-config automation remain
  pending.
- The first Cryo Freezer behavior batch ports the item-to-cryo-fuel loop: slot 1 accepts 1.12-mapped cryo-freezing
  inputs, the machine consumes 40 FE/t while progressing recipes, outputs cryo fuel into a 10,000 mB extract-only tank,
  supports slot 2 to slot 3 fluid container filling, persists cook progress and tank contents to NBT, and exposes
  first-pass progress/tank GUI fields. The 1.20 `blue_ice` recipe is intentionally deferred because vanilla 1.12.2 has
  no blue ice item; Cryo Freezer GUI, network sync, recipe JSON loading, and exact side-config automation remain
  pending.
- The first Compressor behavior batch ports the direct item-to-item compression loop for the 10 copied compressing
  recipes that map cleanly to 1.12.2 items/blocks: iron, steel, desh, ostrum, and calorite ingots/blocks to plates. The
  machine consumes 20 FE/t, tracks cook progress, writes output to slot 2, persists progress and active recipe to NBT,
  and exposes first-pass progress GUI fields. Compressor GUI, network sync, recipe JSON loading, and exact
  side-config automation remain pending.
- The first Etrionic Blast Furnace behavior batch ports the copied custom alloying recipe that maps cleanly to 1.12.2:
  iron ingot plus coal/charcoal produces steel ingot in alloying mode. The machine uses slots 1-4 as unordered inputs,
  slots 5-8 as outputs, consumes 20 FE/t for 100 ticks, persists cook progress and mode to NBT, and exposes first-pass
  progress/mode GUI fields. Blasting mode is retained in NBT/fields but remains a placeholder until 1.20 blasting
  recipes are deliberately mapped to 1.12.2 furnace-style recipes; GUI, network sync, recipe JSON loading, and exact
  side-config automation remain pending.
- The first NASA Workbench behavior batch ports the copied 15-slot rocket assembly layout into the 1.12.2 TileEntity:
  slots 0-13 are fixed recipe inputs, slot 14 is the rocket output, and the four copied tier 1-4 rocket recipes are
  hardcoded against the directly mapped Ad Astra blocks/components. Matching inputs are consumed server-side and the
  corresponding rocket item is placed in the output slot. GUI, recipe JSON loading, particles/sounds, preview-only
  output semantics, and exact side-config automation remain pending.
- The first machine GUI/container batch wires the Forge 1.12 GUI handler into supported machine blocks and adds a
  reusable `Container`/`GuiContainer` pair that draws the copied 1.20 machine GUI textures at their native dimensions.
  Coal Generator, Compressor, Etrionic Blast Furnace, Fuel Refinery, Oxygen Loader, Solar Panel, Water Pump, Energizer,
  Cryo Freezer, NASA Workbench, and Gravity Normalizer now have first-pass right-click inventory screens with player
  inventory transfer and machine field sync. Progress/tank/energy overlays, redstone/side-configuration controls,
  buttons, tooltips, and exact 1.20 screen behavior remain pending.
- The first machine battery-slot batch ports the 1.20 `POWER_MACHINE` charge-slot behavior into the shared 1.12.2
  machine base: supported machines pull Forge Energy from FE-capable items in slot 0 before ticking. Compressor, Cryo
  Freezer, Fuel Refinery, Oxygen Loader, Etrionic Blast Furnace, Water Pump, Oxygen Distributor, and Gravity Normalizer
  can now reserve slot 0 for machine power; Energizer, Coal Generator, and Solar Panel are explicitly excluded because
  they charge items or generate energy. The Forge item handler now also delegates insertion validity to machine slot
  rules so automation cannot bypass the battery/input/output distinction.
- The first machine energy-output batch adds a shared Forge Energy push path based on the existing per-face side mode
  storage. Coal Generator and Solar Panel now default their energy sides to `PUSH` and attempt to send up to their tier
  max output to adjacent Forge Energy receivers each tick. Full cable networks, side-configuration GUI, and item
  charge-slot GUI/sync polish remain pending.
- The first wrench behavior batch replaces the wrench placeholder with a 1.12.2 item implementation. Right-clicking a
  machine TileEntity cycles the clicked face's selected side-mode type and plays the copied wrench sound. Air-right-click
  changes the selected machine mode type between item, energy, and fluid; sneaking cycles backwards. Wrench also cycles
  cable/fluid-pipe face modes between `none`, `normal`, `insert`, and `extract`. Sliding-door locking, client/server
  side-config packets, and full GUI configuration remain pending.
- The first machine automatic side-transfer batch makes saved side modes active for item, energy, and fluid automation.
  Machines pull items, FE, and fluids from faces set to `PULL`/`PUSH_PULL`, push items and fluids to faces set to
  `PUSH`/`PUSH_PULL`, expose sided item handlers that honor each machine's slot rules, and keep existing generator energy
  output paths. Exact 1.20 side configuration screens, per-machine transfer rates, and server/client config packets remain
  pending.
- The first planetary dimension registration batch adds Forge 1.12.2 `DimensionType`/`DimensionManager` entries for
  moon, mars, mercury, venus, and glacio using stable ids 1201-1205. Each dimension has a dedicated `WorldProvider`,
  source-derived first-pass gravity/temperature/oxygen/solar/tier hooks, a fixed Ad Astra biome provider,
  skylight/celestial-angle hooks, and a simple flat planetary surface generator so worlds can load without real 1.20
  terrain generation. Real
  crater/noise terrain, custom planetary biomes, structure placement, the 43 hand-authored worldgen JSON files, the 56
  NBT structures, and generated worldgen data conversion remain pending.
- The first planetary biome/spawn batch registers five Forge 1.12.2 Ad Astra biomes for the planet dimensions and maps
  the 1.20 biome spawn weights into their spawn lists: moon spawns corrupted Lunarians and Star Crawlers, Mars spawns
  Martian Raptors, Mercury spawns Magma Cubes, Venus spawns Moglers, Sulfur Creepers, and Zombified Pygros, and Glacio
  spawns Glacian Rams. Exact 1.20 spawn placement predicates, mob charges, biome variation, config toggles, and runtime
  balancing remain pending.
- The first planet-selection travel batch lets launched rockets open a minimal destination screen at high altitude, then
  sends a server packet to land the player on a selected registered planet when the rocket tier can reach it. This first
  pass transfers the player only; full rocket/lander persistence, launch pad validation, orbits, stations, and
  return-position storage remain pending.
- The first client HUD overlay pass is wired through Forge 1.12 `RenderGameOverlayEvent.Post`. It draws a compact
  oxygen/temperature/gravity/energy panel away from the vanilla hotbar and health bars, using player inventory oxygen
  tanks and Forge Energy items where available, plus local visible oxygen distributor/gravity normalizer state as a
  best-effort environment hint. The real environment systems, dedicated sync packets, warning behavior, vehicle HUD, and
  exact 1.20 overlay layout remain pending.
- The first client entity renderer pass registers Forge 1.12 render factories for all 20 source Ad Astra entity ids,
  plus the 1.12-only `space_painting` hanging entity. Mobs use incremental copied-texture/model renderers, vehicles
  have first-pass source-inspired model renderers, Ice Spit uses an item projectile renderer, Air Vortex uses a small
  translucent placeholder renderer, and Space Painting renders copied per-variant painting textures. Runtime visual
  validation, exact model geometry polish, animation layers, and special projectile effects remain pending.
- Current Java registration count is 333 visible blocks and 82 standalone items.
- The current visible block registry has matching copied blockstate files. All non-fluid visible block items have
  matching copied item model files; fluid block item models are intentionally absent because access should go through
  buckets/tanks rather than ordinary `ItemBlock`s.
- Iron, gold, and copper planetary ores still drop themselves until the 1.20 raw vanilla items are mapped to a
  deliberate 1.12 equivalent.
- Glacio copper ore also intentionally has no smelting output yet because vanilla Minecraft 1.12.2 has no copper
  ingot target.
- Remaining functional equipment behavior, machine logic, full flag/globe rendering and radio audio/station list data, full sliding-door
  animation/rendering polish, exact vehicle launch flow, and richer entity-backed items remain intentionally incomplete
  until their 1.12.2 behavior exists. The 1.20 block registration gap is now 0 block ids after the fluid registration batch.
  The remaining 1.20 item registration gap, excluding automatically registered block items, is now 0 item ids after the
  first standalone utility/equipment/vehicle/spawn-egg item coverage batch. The remaining 1.20 entity registration gap
  is now 0 entity ids after the first entity registry coverage batch. The remaining 1.20 block entity registration gap
  is now 0 block entity ids after the first TileEntity registry coverage batch.

## Asset Sources Already Copied

The 1.12.2 resource tree now contains assets copied from:

- `Ad-Astra-1.20.x/Ad-Astra-1.20.x/common/src/main/resources/assets`
- `Ad-Astra-1.20.x/Ad-Astra-1.20.x/common/src/main/resources/data`
- `Ad-Astra-1.20.x/Ad-Astra-1.20.x/common/src/main/generated/resources/assets`
- `Ad-Astra-1.20.x/Ad-Astra-1.20.x/common/src/main/generated/resources/data`

Important note: the copied `data` files are source material from Minecraft 1.20 data packs.
They are not all directly loadable by Minecraft 1.12.2 and must be converted system by system.

## Major Port Targets

### Phase 1: 1.12.2 Runtime Foundation

- Replace remaining template-only scaffolding with real Ad Astra package structure.
- Add a stable `AdAstraCreativeTab`.
- Add registration helpers for 1.12.2 `Block`, `Item`, `SoundEvent`, `EntityEntry`, `TileEntity`, and GUI IDs.
- Add common constants for registry names copied from the 1.20 registries.
- Add a first pass of config categories for machines, oxygen, temperature, gravity, and dimensions.

### Phase 2: Assets and Data Conversion

- Keep `assets/ad_astra/textures`, `models`, `blockstates`, `sounds`, `lang`, and GUI textures as canonical source assets.
- Convert 1.20 `sounds.json` and sound events into 1.12.2 `SoundEvent` registrations.
- Convert 1.20 generated recipes into 1.12.2 crafting/smelting/custom recipe loaders. The first direct crafting
  conversion is started in `assets/ad_astra/recipes`; its material inputs now use Forge 1.12 OreDictionary
  ingredients where the 1.20 source used tags. Subsequent direct crafting passes have started simple Ad Astra component
  chains and Moon/Mars/Mercury terrain/decor families whose inputs are already registered or have deliberate OreDictionary
  replacements. The first smelting conversion is started in `ModSmeltingRecipes`; 1.20 blasting is folded into normal
  furnace smelting because Minecraft 1.12.2 has no vanilla blast furnace.
- Convert 1.20 loot tables into 1.12.2 block drop code or compatible loot tables where practical. Ore drops for
  currently registered non-raw-vanilla resources are started.
- Replace tag usage with 1.12.2 OreDictionary, explicit registries, or custom sets.

### Phase 3: Basic Content

- Port simple materials and components first: ingots, nuggets, plates, rods, raw materials, engines, tanks, oxygen gear, wheel, fan, rocket fins, nose cone, cheese, ice shard. Basic material items are started.
- Port simple decorative blocks: metal blocks, raw blocks, factory blocks, panels, plateblocks, plating, pillars, stairs, slabs, buttons, pressure plates, lamps. Plain cube variants, stairs, slabs, buttons, pressure plates, walls, axis variants, vent, industrial lamps, launch pad visual placeholder, airlock/reinforced door visual placeholders, small mushrooms, radio visual/orientation behavior, flags, globes, duct visuals, pipe/cable visual connections, sliding-door state placeholders, standard machine visual placeholders, special machine state placeholders, and Forge fluid blocks are started; transfer-capable pipe/cable TileEntities, true sliding-door behavior, special rendered machine blocks, and machine behavior are pending work.
- Port planetary terrain blocks and ores for moon, mars, mercury, venus, and glacio. Plain cube variants, ores, stairs, slabs, walls, and pillar variants are started.
- Port wood and plant sets: aeronos, strophar, and glacian. Plain planks/caps/leaves/fur plus stems/logs, stairs, slabs, fences, fence gates, ladders, doors, trapdoors, buttons, and pressure plates are started.

### Phase 4: Fluids and Transfer

- Rebuild oxygen, hydrogen, oil, fuel, and cryo fuel with Forge 1.12.2 fluids. The first Forge `Fluid` and fluid
  block registration batch is started.
- Rebuild buckets and tank items. The first fluid bucket and gas tank item batches are started; machine tank integration
  remains pending.
- Replace Botarium fluid containers with Forge `FluidTank` backed implementations.
- Rebuild cable and fluid pipe blocks, their tile entities, side modes, extraction, insertion, and wrench behavior.

### Phase 5: Machines and Containers

- Port machine base classes: inventory, energy, fluid, redstone control, side configuration, ticking, recipes, and NBT sync.
- Port tile entities and GUIs for:
  - Coal Generator
  - Compressor
  - Etrionic Blast Furnace
  - NASA Workbench
  - Fuel Refinery
  - Oxygen Loader
  - Solar Panel
  - Water Pump
  - Oxygen Distributor
  - Gravity Normalizer
  - Energizer
  - Cryo Freezer
  - Oxygen Sensor
- Rebuild GUI screens using 1.12.2 `Container`, `GuiContainer`, and `GuiScreen`.

### Phase 6: Core Space Systems

- Port oxygen checks, oxygen distribution, suit oxygen storage, and oxygen HUD.
- Port temperature checks, heat/cold protection, and environmental damage.
- Port gravity multipliers, zero/low gravity motion, and gravity normalizer effects.
- Port destroyed-in-space item/entity behavior and fluid freezing/evaporation.
- Port TI-69 environmental readout.
- Refine zip gun particles/audio/tuning, and refine Jet Suit particles, animation, glide parity, and tuning.

### Phase 7: Vehicles and Launch Flow

- Port rocket, rover, lander, vehicle inventory, fuel storage, controls, sounds, and renderers.
- Port launch pad placement and validation.
- Port launch, landing, orbit selection, and station landing packets.
- Port planets screen and station construction flow.

### Phase 8: Dimensions and World Generation

- Rebuild dimensions in 1.12.2 using `DimensionType`, `WorldProvider`, `BiomeProvider`, and `IChunkGenerator`.
- Port five planets: moon, mars, mercury, venus, and glacio. First-pass dimension registration and safe placeholder
  providers/generators are started.
- Convert 1.20 worldgen JSON and NBT structures into 1.12.2 generators.
- Rebuild craters, noise settings, ores, meteorites, oil wells, villages, moon dungeon, mars temple, venus structures, and space station placement.
- 2026-07-01 source-data gap check: the relevant 1.20 surface is 194 files across dimensions, dimension types,
  biome tags, biome JSONs, configured/placed features, carver, density/noise settings, structure/structure_set,
  template_pool, processor_list, and 56 NBT structures. Matching copies exist in `src/main/resources/data/ad_astra`,
  but they are still 1.20 datapack source material rather than 1.12.2 runtime inputs.
- Current 1.12.2 runtime coverage is intentionally smaller: five Java-registered planet dimensions, five fixed Java
  planet biomes, source-derived environment values, and a flat `AdAstraChunkGenerator`. The first runtime `populate`
  pass now places deterministic Java-local ore veins in the y=1..62 filler layer. NBT structures, structure lookup,
  crater/noise terrain, template pools, orbit dimensions, and non-ore features are not live yet.
- 2026-07-01 ore-vein implementation pass: `AdAstraChunkGenerator.populate(int chunkX, int chunkZ)` now uses a local
  `PlanetOreSpec` table with planet id/name, output block state, replaceable block set, vein size, count per chunk, and
  source/effective 1.12 Y range. It seeds deterministic per-chunk random from the world seed and chunk coords, and clamps
  1.20 Y ranges into the current flat terrain (`1..62`) until real vertical terrain exists.
- First-pass runtime ore set from 1.20 configured/placed features:
  Moon cheese/desh/ice shard/iron; Mars diamond/ice shard/iron/ostrum; Mercury
  iron; Venus calorite/coal/diamond/gold; Glacio coal/ice shard/iron/lapis.
  Glacio copper remains deferred because its gameplay output is still a separate
  copper policy. Glacio deepslate vanilla ore features and Moon soul soil are
  still deferred from this first Ad Astra ore pass.
- Next low-conflict Phase 8 order:
  1. Validate and tune the runtime ore pass in a dev planet world; keep Glacio copper disabled until a copper output
     policy exists.
  2. Add simple non-NBT features: Moon soul soil, Mars rock blobs, then Venus infernal spire columns.
  3. Add a minimal NBT template loader/placer and validate it with meteor variants or the size-1 oil well template.
  4. Add simple single-start structure predicates for oil well, lunar tower, and Mars temple.
  5. Defer Moon dungeon, Lunarian village, Pygro/crimson structures, Venus bullet/tower, and biome-tagged structure
     distribution until a 1.12.2 jigsaw/template-pool equivalent or explicit piece connector exists.
  6. Defer crater/noise terrain and orbit/space-station placement until after feature and NBT placement smoke tests.

### Phase 9: Mobs and Renderers

- Port entity registrations, attributes, AI, spawn rules, models, textures, and renderers for:
  - Lunarian
  - Corrupted Lunarian
  - Lunarian Wandering Trader
  - Star Crawler
  - Martian Raptor
  - Pygro
  - Zombified Pygro
  - Pygro Brute
  - Mogler
  - Zombified Mogler
  - Sulfur Creeper
  - Glacian Ram
  - Ice Spit
  - Air Vortex
- Current 1.12.2 status has registry ids, first-pass mob AI/attributes,
  spawn eggs, planet spawn lists, and renderer factories for all 20 source
  entity ids. The renderer coverage is intentionally incremental: most mobs
  still use vanilla biped placeholders, Martian Raptors, Star Crawlers, Glacian
  Rams, Pygro-family mobs, Mogler-family mobs, and Lunarian-family mobs now use
  first-pass 1.12 `ModelBase`/`ModelBiped` ports of their 1.20 hardcoded Java
  models, sulfur creepers have synced fuse/powered state with creeper swelling
  and charge visuals, tier 1-4 rockets now use first-pass source-inspired
  `ModelRocket`/`RenderRocket` bindings with copied tier textures, the lander
  now uses a first-pass source-inspired `ModelLander`/`RenderLander` binding
  with copied texture, the rover now uses a first-pass source-inspired
  `ModelRover`/`RenderRover` binding with copied texture and wheel animation,
  Ice Spit renders as an item projectile, and Air Vortex remains a visible
  debug-style cube. Ice Spit now has first-pass source-like projectile behavior
  with thrown damage,
  SPIT/SNOWBALL particles, owner/position constructors, broadcast discard event,
  and corrupted Lunarian ranged attack AI integration.
- Next low-conflict order for this phase:
  1. Vehicle client polish: exact geometry passes for rocket/lander/rover and
     vehicle item renderers.
- Keep vehicle inventory/fuel/control/menu work separate from renderer-only
  batches because it touches networking, GUI containers, launch flow, and the
  main thread's equipment tick work.
- Keep recipe JSON work out of this phase; another worker owns that surface.

### Phase 10: Client Polish and Compatibility

- Port custom skies, planet renderers, acid rain, particles, overlays, and block entity renderers.
- Port flags, globes, sliding doors, radio, and rendered items.
- Port Patchouli guide content if a 1.12.2-compatible dependency is selected.
- Rebuild JEI/HEI integration for machine recipes.
- Treat Create, Mekanism, Thermal, Immersive Engineering, and Tech Reborn integration recipes as optional later work.

## Verification Gates

- `gradlew.bat build` must pass after each foundation change.
- Last verified with `gradlew.bat build` on 2026-06-30 after the vent, metal pillar, and industrial lamp batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first ore loot/drop behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first direct 1.12 recipe batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first OreDictionary and ore-dictionary recipe batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first smelting conversion batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first non-machine visual placeholder and radio batches.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first standard machine visual placeholder batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the flag, globe, and duct visual placeholder batches.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first special machine state placeholder batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first pipe/cable visual connection batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first sliding-door state placeholder batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Forge fluid registration batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first fluid bucket item batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first standalone utility/equipment/vehicle/spawn-egg
  item coverage batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first entity registry coverage batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first TileEntity registry coverage batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first machine storage foundation batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Coal Generator ticking behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Solar Panel generation behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first machine Forge Energy output batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first wrench machine side-config behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first cable Forge Energy transfer batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first fluid pipe Forge Fluid transfer batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Water Pump behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first item energy and Energizer behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Etrionic Capacitor inventory distribution batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first pipe side-mode and wrench connection batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Fuel Refinery and Oxygen Loader behavior batches.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Cryo Freezer behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Compressor behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Etrionic Blast Furnace alloying behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first NASA Workbench TileEntity behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first machine battery-slot behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Oxygen Distributor behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first machine GUI/container batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first gas tank oxygen storage item batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Gravity Normalizer behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first Oxygen Sensor/Detector behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first flag/globe/radio TileEntity behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first sliding-door/airlock behavior batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first machine automatic side-transfer batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first planetary dimension registration batch.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first client HUD overlay pass.
- Last verified with `gradlew.bat build` on 2026-06-30 after the first client entity renderer pass.
- Last verified with `gradlew.bat build` on 2026-06-30 after the Zip Gun propulsion, Radio GUI, Chinese lang alignment,
  and second direct crafting recipe batches.
- Last verified with `gradlew.bat build` on 2026-06-30 after the Jet Suit powered flight, Moon terrain/decor direct
  crafting recipe, and entity render gap documentation batches.
- Last verified with `gradlew.bat build` on 2026-07-01 after the Ice Spit/corrupted Lunarian ranged attack and Mars
  terrain/decor direct crafting recipe batches.
- Last verified with `gradlew.bat build` on 2026-07-01 after the Sulfur Creeper
  synced swelling/charge renderer, Mercury terrain/decor direct crafting
  recipes, Chinese lang parity check, and ore worldgen feasibility documentation.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first runtime
  planet ore populate pass, Venus terrain/decor direct crafting recipes, and
  Martian Raptor/Star Crawler model-port feasibility notes.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  `ModelMartianRaptor` renderer binding and Glacio terrain/decor direct crafting
  recipes.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  `ModelStarCrawler` renderer binding and Permafrost terrain/decor direct
  crafting recipes.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  `ModelGlacianRam` renderer binding, metal decorative direct crafting recipes,
  and Chinese JSON/lang parity pass.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  Pygro-family renderer bindings and metal plating button/pressure-plate/slab/
  stair direct crafting recipes.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  Mogler-family renderer bindings and aeronos/strophar/glacian wood-family
  direct crafting recipes.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  Lunarian-family renderer bindings and colored flag direct crafting recipes.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  tier 1-4 rocket model renderer bindings and industrial lamp direct crafting
  recipes.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  `ModelLander` renderer binding.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  `ModelRover` renderer binding.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  vehicle model renderer trio and factory/encased/pipe utility direct crafting
  recipes.
- Last verified with `gradlew.bat build` on 2026-07-01 after the first
  Space Painting hanging entity and airlock/door/sandstone utility direct
  crafting recipes.
- Every content phase should add a minimal in-game smoke test checklist.
- Asset migrations should be checked by counting copied files and by launching a client once content registries exist.
- Worldgen and vehicle phases require manual runtime testing in a dev client.
