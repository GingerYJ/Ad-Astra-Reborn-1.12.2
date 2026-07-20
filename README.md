# Ad Astra Reborn 1.12.2

Ad Astra Reborn 是面向 Minecraft 1.12.2 的太空探索模组，运行于 Cleanroom Loader。项目当前的注册、维度、火箭和配置模型以 `src/main/java` 与 `src/main/resources` 中的实现为准。

[中文说明](#中文说明) | [English](#english)

## 中文说明

### 项目概览

| 项目 | 内容 |
| --- | --- |
| Mod ID | `ad_astra` |
| Mod 名称 | `Ad Astra Reborn` |
| 版本 | `1.0.0` |
| Minecraft | `1.12.2` |
| Loader | Cleanroom Loader `0.5.12-alpha` |
| Java | Java 25 工具链，Java 21 源码/目标兼容级别 |
| 作者 | GingerYJ |
| 源码仓库 | <https://github.com/GingerYJ/Ad-Astra-Reborn-1.12.2> |

### 功能概览

- 15 个代码内置的固定火箭实体与物品，等级为 1-15。
- `rockets.cfg` 仅用于新增第三方高级火箭，配置火箭等级为 16-255。
- 19 个代码内置注册的 Ad Astra 地表行星；原版地球仍使用主世界维度。
- 全服务器只有一个 `ad_astra:space_station` 空间站维度，所有玩家共享同一座空间站。
- 火箭发射台、着陆器、漫游车、太空服、喷气背包和燃料系统。
- 氧气、温度、重力、太阳能、缺氧伤害和行星环境效果。
- NASA 工作台、压缩机、Etrionic 电力高炉、燃油精炼机、低温冷冻机、氧气设备、发电设备、水泵和重力标准化器。
- 行星生物群系、地形、矿脉、流体湖、村庄/结构以及行星专属资源。
- 金属建筑方块、管道、电缆、旗帜、收音机和环境装饰。
- JSON 配方、JEI/HEI 配方查看集成、CraftTweaker 和 Java API。
- 安装 Patchouli 后可使用 Astrodux 游戏内指南。

### 内置行星与维度

项目当前有 19 个由代码拥有并注册的 Ad Astra 地表行星。它们使用同一套内置注册模型，第三方注册不能覆盖这些行星的资源 ID 或地表维度 ID。

地球是原版主世界，不计入下表的 19 个 Ad Astra 行星。所有行星只注册表面维度，不再为每个行星注册独立空间站或轨道维度。

| 行星键 | 名称 | 表面维度 ID | 最低火箭等级 |
| --- | --- | ---: | ---: |
| `moon` | 月球 | `108490` | 1 |
| `mars` | 火星 | `108491` | 2 |
| `mercury` | 水星 | `108492` | 3 |
| `venus` | 金星 | `108493` | 3 |
| `glacio` | 霜原星 | `108494` | 4 |
| `ceres` | 谷神星 | `108495` | 3 |
| `jupiter` | 木星 | `108496` | 4 |
| `saturn` | 土星 | `108497` | 5 |
| `uranus` | 天王星 | `108498` | 6 |
| `neptune` | 海王星 | `108499` | 7 |
| `orcus` | 奥库斯 | `108500` | 8 |
| `pluto` | 冥王星 | `108501` | 9 |
| `haumea` | 妊神星 | `108502` | 8 |
| `quaoar` | 创神星 | `108503` | 8 |
| `makemake` | 鸟神星 | `108504` | 8 |
| `gonggong` | 共工星 | `108505` | 9 |
| `eris` | 阋神星 | `108506` | 9 |
| `sedna` | 塞德娜 | `108507` | 10 |
| `proxima_centauri_b` | 比邻星 b | `108508` | 11 |

维度 ID 是代码拥有的注册数据，不应通过配置文件修改。唯一的空间站维度为：

- 资源名：`ad_astra:space_station`
- 维度 ID：`107489`
- 存档状态：服务端世界数据 `adastra_space_station`
- 建造规则：每个服务器存档最多建造一次，固定在空间站维度的全局原点区域。

其他 Mod 已经注册的维度可以通过 `external_dimensions.cfg` 接入星图和环境 API，但该配置不会创建维度，也不会为外部维度创建空间站。

### 火箭

Tier 1-15 是独立注册的内置火箭。火箭等级、燃料容量、实体类型和燃料等级检查都由代码固定，不能通过 `rockets.cfg` 覆盖。

| 等级 | 物品 ID | 实体 ID | 固定燃料容量 |
| ---: | --- | --- | ---: |
| 1 | `tier_1_rocket` | `tier_1_rocket` | `3000 mB` |
| 2 | `tier_2_rocket` | `tier_2_rocket` | `4000 mB` |
| 3 | `tier_3_rocket` | `tier_3_rocket` | `5000 mB` |
| 4 | `tier_4_rocket` | `tier_4_rocket` | `6000 mB` |
| 5 | `tier_5_rocket` | `tier_5_rocket` | `7000 mB` |
| 6 | `tier_6_rocket` | `tier_6_rocket` | `8000 mB` |
| 7 | `tier_7_rocket` | `tier_7_rocket` | `9000 mB` |
| 8 | `item_tier_8_rocket` | `tier_8_rocket` | `18000 mB` |
| 9 | `item_tier_9_rocket` | `tier_9_rocket` | `19000 mB` |
| 10 | `item_tier_10_rocket` | `tier_10_rocket` | `20000 mB` |
| 11 | `item_tier_11_rocket` | `tier_11_rocket` | `21000 mB` |
| 12 | `item_tier_12_rocket` | `tier_12_rocket` | `22000 mB` |
| 13 | `item_tier_13_rocket` | `tier_13_rocket` | `23000 mB` |
| 14 | `item_tier_14_rocket` | `tier_14_rocket` | `24000 mB` |
| 15 | `item_tier_15_rocket` | `tier_15_rocket` | `25000 mB` |

`rockets.cfg` 中的每一行格式为：

```text
id|displayName|rocketTier|fuelCapacity|modelTier|texture
```

配置火箭规则：

- `rocketTier` 范围为 `16-255`，决定火箭的飞行能力和行星准入等级。
- `fuelCapacity` 范围为 `1000-64000 mB`。
- `modelTier` 范围为 `1-15`，只决定外观，不改变火箭等级。
- `texture` 为空时使用对应的内置火箭贴图。
- 相对 PNG 贴图放在 `config/ad_astra/rocket_png/` 下；无效贴图会回退到对应的内置贴图。
- 配置 ID 不能占用内置火箭、`configurable_rocket` 或其他已经注册的火箭 ID。
- 火箭只能使用 `fuelTier >= rocketTier` 的燃料；第三方燃料可以通过现有燃料 API 注册。

示例：

```cfg
S:customRockets <
    example_rocket|示例高级火箭|16|18000|8|
>
```

旧配置中等级为 1-15 的自定义火箭会在读取时提升为等级 16，并写入警告；不会覆盖同等级的内置火箭。

### 全局空间站

空间站不是按行星拥有的列表，而是存档级单例：

- 所有玩家看到并进入同一个空间站维度和同一组固定坐标。
- 首次建造需要玩家能够到达当前选择的行星，并满足唯一空间站配方的材料要求。
- 首次成功建造后，状态同步给在线玩家；后续玩家直接进入同一空间站。
- `PacketLandSpaceStation` 只提交进入请求，服务器从存档数据读取维度和坐标。
- 配方资源只有 `data/ad_astra/machine_recipes/space_station/space_station.json`，不再按行星拆分。

### 配置文件

首次运行后，配置文件位于 `config/ad_astra/`：

| 文件 | 当前用途 |
| --- | --- |
| `core.cfg` | 通用玩法、氧气、温度、重力、性能和平衡设置 |
| `client.cfg` | 客户端界面、HUD、音量和显示设置 |
| `machines.cfg` | 机器处理速度、能量、流体、容量和管道设置 |
| `dimensions.cfg` | 已注册地表行星的 `rocketTier` 覆盖值，范围 `0-15` |
| `mobs.cfg` | 行星生物生成白名单、数量上限、重生间隔和缺氧免疫 |
| `worldgen.cfg` | 行星矿脉列表、世界生成倍率和矿脉调试日志 |
| `external_dimensions.cfg` | 接入其他 Mod 已注册的维度 |
| `rockets.cfg` | 新增第三方高级火箭，等级范围 `16-255` |

`dimensions.cfg` 只允许调整行星最低火箭等级；行星 ID、资源 ID、世界生成和空间站归属由代码维护。`ad_astra.cfg`、`debug.cfg` 仅保留用于旧配置迁移或兼容处理。

#### 行星等级

每个行星使用 `planet_<行星键>` 分类，例如：

```cfg
[planet_moon]
I:rocketTier=1
```

`0` 表示不限制火箭等级。空间站不使用行星等级配置。

#### 外部维度

`external_dimensions.cfg` 使用 `general` 和 `dimensions` 分类：

```cfg
enableExternalDimensionTravel=true

S:externalDimensions <
    -28|galacticraftcore:moon|1|-173|0.166|24|银河月球
>
```

每行格式为：

```text
dimensionId|resourceId|minimumRocketTier|temperature|gravity|skyLightOrSolarPower|displayName(optional)
```

外部维度必须已经注册；未注册维度和内置维度会被忽略。外部维度最低火箭等级范围为 `0-255`，温度范围为 `-32768` 至 `32767`，重力范围为 `0-10`，天空光照/太阳能值范围为 `0-1024`。

#### 生物生成

`mobs.cfg` 的 `planetMobSpawnWhitelist` 每行格式为：

```text
planet|entityId|spawnType|weight|minGroup|maxGroup|maxCount
```

示例：

```cfg
moon|minecraft:ender_dragon|monster|1|1|1|1
```

有效白名单行会自动加入缺氧免疫；没有有效白名单的行星继续使用生物群系默认生成。

#### 矿脉生成

`worldgen.cfg` 按 `planet_ore_<行星键>` 分类保存 `oreVeins`：

```cfg
[planet_ore_moon]
S:oreVeins <
    moon|minecraft:iron_ore|8|4|20|60|default
>
```

格式为 `planet|blockId[@meta]|veinSize|countPerChunk|minY|maxY|replaceTargets`。矿脉大小范围为 `1-64`，每区块次数范围为 `0-100`，Y 坐标范围为 `-80` 至 `320`。修改只影响新生成区块。

### 指令

```text
/adastra help
/adastra planets
/adastra setdimension <dimensionId> [x y z]
/adastra tpdim <dimensionId> [x y z]
/adastra dimtp <dimensionId> [x y z]
/adastra radio refresh
/adastra tps [detailed]
```

`/adastra planets` 打开行星选择界面；`setdimension`、`tpdim` 和 `dimtp` 用于测试传送。`radio refresh` 重新加载收音机电台，`tps` 显示服务器性能信息。行星选择和测试传送需要相应的权限等级。

### CraftTweaker 与 Java API

CraftTweaker / ZenScript 的完整方法、参数范围和示例见 [docs/CRT_INTERFACE.md](docs/CRT_INTERFACE.md)。当前脚本入口包括：

```zenscript
import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
```

当前接口支持：

- `CustomPlanets`：创建第三方自定义行星。自定义行星只注册表面维度，不创建独立空间站维度。
- `NASAWorkbench`：添加、删除和替换 NASA 工作台配方。
- `RocketFuel`：添加或删除燃料等级。
- `PlanetTiers`：按维度覆盖最低火箭等级；行星等级使用 `0-15`，外部维度可使用 `0-255`。
- `SpaceStation`：修改唯一全局空间站配方，方法不接受行星或轨道参数：

```zenscript
SpaceStation.setRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.setRecipe(IIngredient[] ingredients);
SpaceStation.replaceRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.replaceRecipe(IIngredient[] ingredients);
SpaceStation.addRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.addRecipe(IIngredient[] ingredients);
SpaceStation.removeRecipe();
SpaceStation.removeRecipeById(String id);
```

Java API 位于 `earth.terrarium.adastra.api`，主要接口包括：

- `PlanetApi`：查询和注册行星元数据，并区分表面行星、空间站和外部维度。
- `OxygenApi`：查询、设置和移除局部氧气区域。
- `GravityApi`：查询和修改重力。
- `TemperatureApi`：查询温度和环境适宜性。
- `AdAstraEvents`：监听氧气、温度、重力和环境事件。

相关文档：

- [API 使用说明](src/main/java/earth/terrarium/adastra/api/API_README.md)
- [API 结构摘要](src/main/java/earth/terrarium/adastra/api/API_PORT_SUMMARY.md)
- [API 实现指南](src/main/java/earth/terrarium/adastra/api/IMPLEMENTATION_GUIDE.md)

### 构建与运行

项目使用 Gradle、Unimined 和 Cleanroom Loader。Windows 下可以使用 Gradle Wrapper：

```powershell
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

构建产物位于 `build/libs/`。开发运行目录为 `run/client/` 和 `run/server/`；CraftTweaker 开发脚本放在 `run/client/scripts/`。

### 项目结构

```text
src/main/java/earth/terrarium/adastra/  Java 源码、玩法系统和 API
src/main/resources/                     方块、物品、模型、语言、配方和世界生成资源
src/test/                                资源完整性与注册表测试
docs/                                    CraftTweaker 文档
gradle/                                  Gradle 脚本与 Wrapper
```

### 许可证与鸣谢

本项目使用 [MIT License](LICENSE)。原始 Ad Astra 内容和资源归 Terrarium 与 Ad Astra 贡献者所有。本项目由 GingerYJ 维护，针对 Minecraft 1.12.2 进行适配和扩展。

---

## English

Ad Astra Reborn is a space-exploration mod for Minecraft 1.12.2 running on Cleanroom Loader. The current registration, dimension, rocket, and configuration model is defined by the implementation in `src/main/java` and `src/main/resources`.

### Project Overview

| Item | Value |
| --- | --- |
| Mod ID | `ad_astra` |
| Mod name | `Ad Astra Reborn` |
| Version | `1.0.0` |
| Minecraft | `1.12.2` |
| Loader | Cleanroom Loader `0.5.12-alpha` |
| Java | Java 25 toolchain, Java 21 source/target compatibility |
| Author | GingerYJ |
| Repository | <https://github.com/GingerYJ/Ad-Astra-Reborn-1.12.2> |

### Features

- Fifteen code-owned built-in rocket entities and items, tiers 1-15.
- `rockets.cfg` only adds third-party advanced rockets with tiers 16-255.
- Nineteen code-registered Ad Astra surface planets; Earth remains the vanilla Overworld.
- One `ad_astra:space_station` dimension shared by every player on the server.
- Launch pads, landers, rovers, space suits, jet suits, and rocket fuel systems.
- Oxygen, temperature, gravity, solar power, oxygen-deprivation damage, and planetary environments.
- NASA Workbench, Compressor, Etrionic Blast Furnace, Fuel Refinery, Cryo Freezer, oxygen equipment, generators, Water Pump, and Gravity Normalizer.
- Planet biomes, terrain, ores, fluid lakes, villages/structures, and planet-specific resources.
- Metal building blocks, pipes, cables, flags, radio stations, and environmental decorations.
- JSON recipes, JEI/HEI recipe integration, CraftTweaker, and Java API integration.
- The Astrodux in-game guide is available when Patchouli is installed.

### Built-in Planets and Dimensions

The project has nineteen code-owned Ad Astra surface planets. They all use the same built-in registration model, and third-party registration cannot replace their resource IDs or surface dimension IDs.

Earth is the vanilla Overworld and is not counted in the nineteen Ad Astra planets below. Every listed planet registers only a surface dimension. No per-planet space-station or orbit dimension is created.

| Planet key | Name | Surface dimension ID | Minimum rocket tier |
| --- | --- | ---: | ---: |
| `moon` | Moon | `108490` | 1 |
| `mars` | Mars | `108491` | 2 |
| `mercury` | Mercury | `108492` | 3 |
| `venus` | Venus | `108493` | 3 |
| `glacio` | Glacio | `108494` | 4 |
| `ceres` | Ceres | `108495` | 3 |
| `jupiter` | Jupiter | `108496` | 4 |
| `saturn` | Saturn | `108497` | 5 |
| `uranus` | Uranus | `108498` | 6 |
| `neptune` | Neptune | `108499` | 7 |
| `orcus` | Orcus | `108500` | 8 |
| `pluto` | Pluto | `108501` | 9 |
| `haumea` | Haumea | `108502` | 8 |
| `quaoar` | Quaoar | `108503` | 8 |
| `makemake` | Makemake | `108504` | 8 |
| `gonggong` | Gonggong | `108505` | 9 |
| `eris` | Eris | `108506` | 9 |
| `sedna` | Sedna | `108507` | 10 |
| `proxima_centauri_b` | Proxima Centauri b | `108508` | 11 |

Dimension IDs are code-owned registration data and should not be edited in configuration. The only space-station dimension is:

- Resource name: `ad_astra:space_station`
- Dimension ID: `107489`
- Saved-data key: `adastra_space_station`
- Construction: at most once per server save, at a fixed area around the global origin of the station dimension.

Dimensions already registered by another mod can be exposed to the star map and environment API through `external_dimensions.cfg`. This does not create a dimension or a space station for that dimension.

### Rockets

Tiers 1-15 are independently registered built-in rockets. Their tier, fuel capacity, entity type, and fuel-tier checks are fixed in code and cannot be overridden by `rockets.cfg`.

| Tier | Item ID | Entity ID | Fixed fuel capacity |
| ---: | --- | --- | ---: |
| 1 | `tier_1_rocket` | `tier_1_rocket` | `3000 mB` |
| 2 | `tier_2_rocket` | `tier_2_rocket` | `4000 mB` |
| 3 | `tier_3_rocket` | `tier_3_rocket` | `5000 mB` |
| 4 | `tier_4_rocket` | `tier_4_rocket` | `6000 mB` |
| 5 | `tier_5_rocket` | `tier_5_rocket` | `7000 mB` |
| 6 | `tier_6_rocket` | `tier_6_rocket` | `8000 mB` |
| 7 | `tier_7_rocket` | `tier_7_rocket` | `9000 mB` |
| 8 | `item_tier_8_rocket` | `tier_8_rocket` | `18000 mB` |
| 9 | `item_tier_9_rocket` | `tier_9_rocket` | `19000 mB` |
| 10 | `item_tier_10_rocket` | `tier_10_rocket` | `20000 mB` |
| 11 | `item_tier_11_rocket` | `tier_11_rocket` | `21000 mB` |
| 12 | `item_tier_12_rocket` | `tier_12_rocket` | `22000 mB` |
| 13 | `item_tier_13_rocket` | `tier_13_rocket` | `23000 mB` |
| 14 | `item_tier_14_rocket` | `tier_14_rocket` | `24000 mB` |
| 15 | `item_tier_15_rocket` | `tier_15_rocket` | `25000 mB` |

Each `rockets.cfg` row uses:

```text
id|displayName|rocketTier|fuelCapacity|modelTier|texture
```

Rules for configurable rockets:

- `rocketTier` is `16-255` and controls flight capability and planet access.
- `fuelCapacity` is `1000-64000 mB`.
- `modelTier` is `1-15` and controls appearance only.
- An empty `texture` uses the matching built-in rocket texture.
- Relative PNG files belong in `config/ad_astra/rocket_png/`; invalid textures fall back to the built-in texture.
- Config IDs cannot use built-in rocket IDs, `configurable_rocket`, or another registered rocket ID.
- A rocket accepts fuel with `fuelTier >= rocketTier`; third-party fuel can be registered through the existing fuel API.

Example:

```cfg
S:customRockets <
    example_rocket|Example Advanced Rocket|16|18000|8|
>
```

Legacy configurable rows with tiers 1-15 are raised to tier 16 when read and produce a warning; they do not replace built-in rockets.

### Global Space Station

The station is save-level singleton data rather than a per-planet list:

- Every player uses the same station dimension and fixed station coordinates.
- The first construction requires access to the selected planet and the materials in the single global station recipe.
- After the first successful construction, the state is synchronized to online players; later players enter the same station.
- `PacketLandSpaceStation` submits only an entry request; the server reads the dimension and position from saved data.
- The only station recipe resource is `data/ad_astra/machine_recipes/space_station/space_station.json`; recipes are not split by planet.

### Configuration

After the first run, configuration files are generated under `config/ad_astra/`:

| File | Current purpose |
| --- | --- |
| `core.cfg` | General gameplay, oxygen, temperature, gravity, performance, and balance settings |
| `client.cfg` | Client interface, HUD, audio, and display settings |
| `machines.cfg` | Machine processing speed, energy, fluid, capacity, and pipe settings |
| `dimensions.cfg` | `rocketTier` overrides for registered surface planets, range `0-15` |
| `mobs.cfg` | Planet mob spawn whitelists, caps, respawn intervals, and oxygen immunity |
| `worldgen.cfg` | Planet ore rows, world-generation multiplier, and ore debug logging |
| `external_dimensions.cfg` | Dimensions already registered by another mod |
| `rockets.cfg` | New third-party advanced rockets, tiers `16-255` |

`dimensions.cfg` can change only the minimum rocket tier. Planet IDs, resource IDs, world generation, and station ownership are code-owned. `ad_astra.cfg` and `debug.cfg` remain only for legacy migration or compatibility handling.

#### Planet tiers

Each planet uses a `planet_<planet key>` category, for example:

```cfg
[planet_moon]
I:rocketTier=1
```

`0` means no rocket-tier restriction. The space station does not use a planet-tier entry.

#### External dimensions

`external_dimensions.cfg` uses `general` and `dimensions` categories:

```cfg
enableExternalDimensionTravel=true

S:externalDimensions <
    -28|galacticraftcore:moon|1|-173|0.166|24|Galaxy Moon
>
```

Each row is:

```text
dimensionId|resourceId|minimumRocketTier|temperature|gravity|skyLightOrSolarPower|displayName(optional)
```

The dimension must already be registered. Unregistered and built-in dimensions are ignored. External-dimension tiers range from `0-255`, temperature from `-32768` to `32767`, gravity from `0-10`, and skylight/solar power from `0-1024`.

#### Mob spawning

Each `mobs.cfg` `planetMobSpawnWhitelist` row is:

```text
planet|entityId|spawnType|weight|minGroup|maxGroup|maxCount
```

Example:

```cfg
moon|minecraft:ender_dragon|monster|1|1|1|1
```

Valid whitelist rows also grant oxygen immunity. Planets without valid whitelist rows keep biome-default spawning.

#### Ore generation

`worldgen.cfg` stores `oreVeins` under `planet_ore_<planet key>` categories:

```cfg
[planet_ore_moon]
S:oreVeins <
    moon|minecraft:iron_ore|8|4|20|60|default
>
```

The format is `planet|blockId[@meta]|veinSize|countPerChunk|minY|maxY|replaceTargets`. Vein size is `1-64`, count per chunk is `0-100`, and Y is `-80` to `320`. Changes affect newly generated chunks only.

### Commands

```text
/adastra help
/adastra planets
/adastra setdimension <dimensionId> [x y z]
/adastra tpdim <dimensionId> [x y z]
/adastra dimtp <dimensionId> [x y z]
/adastra radio refresh
/adastra tps [detailed]
```

`/adastra planets` opens the planet selection interface. `setdimension`, `tpdim`, and `dimtp` are for teleport testing. `radio refresh` reloads radio stations, and `tps` reports server performance. Planet selection and test teleportation require the corresponding permission level.

### CraftTweaker and Java API

See [docs/CRT_INTERFACE.md](docs/CRT_INTERFACE.md) for complete CraftTweaker / ZenScript methods, parameter ranges, and examples. Current script entry points include:

```zenscript
import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
```

Current interfaces provide:

- `CustomPlanets`: creates third-party custom planets. Custom planets register surface dimensions only.
- `NASAWorkbench`: adds, removes, and replaces NASA Workbench recipes.
- `RocketFuel`: adds or removes fuel tiers.
- `PlanetTiers`: overrides minimum rocket tiers by dimension; planets use `0-15`, while external dimensions use `0-255`.
- `SpaceStation`: modifies the single global station recipe. Its methods do not accept a planet or orbit argument:

```zenscript
SpaceStation.setRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.setRecipe(IIngredient[] ingredients);
SpaceStation.replaceRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.replaceRecipe(IIngredient[] ingredients);
SpaceStation.addRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.addRecipe(IIngredient[] ingredients);
SpaceStation.removeRecipe();
SpaceStation.removeRecipeById(String id);
```

The Java API is under `earth.terrarium.adastra.api`:

- `PlanetApi`: queries and registers planet metadata, and distinguishes surface planets, the station, and external dimensions.
- `OxygenApi`: queries, sets, and removes local oxygen areas.
- `GravityApi`: queries and changes gravity.
- `TemperatureApi`: queries temperature and habitability.
- `AdAstraEvents`: listens for oxygen, temperature, gravity, and environment events.

Related documentation:

- [API usage guide](src/main/java/earth/terrarium/adastra/api/API_README.md)
- [API port summary](src/main/java/earth/terrarium/adastra/api/API_PORT_SUMMARY.md)
- [API implementation guide](src/main/java/earth/terrarium/adastra/api/IMPLEMENTATION_GUIDE.md)

### Build and Run

The project uses Gradle, Unimined, and Cleanroom Loader. On Windows, use the Gradle Wrapper:

```powershell
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

Build outputs are written to `build/libs/`. Development run directories are `run/client/` and `run/server/`; CraftTweaker development scripts belong in `run/client/scripts/`.

### Project Structure

```text
src/main/java/earth/terrarium/adastra/  Java source, gameplay systems, and APIs
src/main/resources/                     Blocks, items, models, languages, recipes, and worldgen assets
src/test/                                Resource-integrity and registry tests
docs/                                    CraftTweaker documentation
gradle/                                  Gradle scripts and Wrapper
```

### License and Credits

This project is licensed under the [MIT License](LICENSE). Original Ad Astra content and assets belong to Terrarium and the Ad Astra contributors. The project is maintained by GingerYJ and adapted and extended for Minecraft 1.12.2.
