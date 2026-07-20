# Ad Astra Reborn 1.12.2

Ad Astra Reborn 是面向 Minecraft 1.12.2 的太空探索模组，基于 Cleanroom Loader，提供火箭发射、行星维度、共享空间站、氧气与环境系统、机器、资源生成和模组整合功能。

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
| 作者 | GingerYJ |
| 源码仓库 | <https://github.com/GingerYJ/Ad-Astra-Reborn-1.12.2> |

### 主要功能

- 1 至 15 阶内置火箭，以及可通过配置定义的 16 至 255 阶额外火箭。
- 火箭发射台、着陆器、漫游车、太空服、喷气背包和相关燃料系统。
- 支持缩放、拖动、行星名称显示和可达性判断的行星选择界面。
- 19 个内置地表行星，以及一个所有玩家共享的空间站维度。
- 氧气、温度、重力、太阳能、缺氧伤害和行星环境效果。
- NASA 工作台、压缩机、Etrionic 高炉、燃油精炼机、低温冷冻机、氧气设备、发电设备、抽水机和重力标准化器。
- 行星生物群系、地形、矿脉、流体湖、村庄或结构等世界生成内容。
- 行星专属资源、金属建筑方块、管道、电缆、旗帜、收音机和环境装饰。
- JSON 配方、HEI/JEI 配方查看器、CraftTweaker 和 Java API 集成。
- 安装 Patchouli 后可使用内置 Astrodux 游戏内指南。

### 内置行星

| 分类 | 行星键 | 名称 |
| --- | --- | --- |
| 核心行星 | `moon` | 月球 |
| 核心行星 | `mars` | 火星 |
| 核心行星 | `mercury` | 水星 |
| 核心行星 | `venus` | 金星 |
| 核心行星 | `glacio` | 霜原星 |
| 额外行星 | `ceres` | 谷神星 |
| 额外行星 | `jupiter` | 木星 |
| 额外行星 | `saturn` | 土星 |
| 额外行星 | `uranus` | 天王星 |
| 额外行星 | `neptune` | 海王星 |
| 额外行星 | `orcus` | 奥库斯 |
| 额外行星 | `pluto` | 冥王星 |
| 额外行星 | `haumea` | 妊神星 |
| 额外行星 | `quaoar` | 创神星 |
| 额外行星 | `makemake` | 鸟神星 |
| 额外行星 | `gonggong` | 共工星 |
| 额外行星 | `eris` | 阋神星 |
| 额外行星 | `sedna` | 塞德娜 |
| 额外行星 | `proxima_centauri_b` | 比邻星 b |

### 维度说明

- 主世界仍使用 Minecraft 原版维度 ID `0`。
- 五个核心行星使用 `108490` 至 `108494`。
- 额外行星使用 `108495` 至 `108508`。
- 共享空间站使用维度 ID `107489`。
- 当前版本不为每颗行星创建独立轨道维度；所有空间站使用同一个共享维度。
- 上述维度 ID 和行星注册数据由代码维护，不建议手动修改。
- 其他 Mod 已注册的维度可以通过 `external_dimensions.cfg` 接入星图和环境系统。

### 配置文件

首次运行后，配置文件位于 `config/ad_astra/`：

| 文件 | 用途 |
| --- | --- |
| `core.cfg` | 通用玩法、氧气、温度、重力、性能和平衡设置 |
| `client.cfg` | 客户端界面、HUD、音量和显示设置 |
| `machines.cfg` | 机器速度、能量、流体、容量和管道设置 |
| `dimensions.cfg` | 各地表行星的火箭等级限制 |
| `mobs.cfg` | 行星生物白名单、数量上限和缺氧生存规则 |
| `worldgen.cfg` | 矿脉、世界生成倍率和矿脉调试日志 |
| `external_dimensions.cfg` | 接入其他 Mod 已注册的维度 |
| `rockets.cfg` | 配置驱动的额外火箭 |

`ad_astra.cfg` 和 `debug.cfg` 仅用于旧配置迁移或兼容处理，不是当前主要运行时配置。配置迁移时，旧文件会移动到带时间戳的备份目录；配置修改通常需要重启游戏或服务器，矿脉修改只影响新生成区块。

#### 自定义生物生成

`mobs.cfg` 的 `planetMobSpawnWhitelist` 格式如下：

```text
行星键|实体 ID|生成类型|生成权重|最小群组数量|最大群组数量|实体数量上限
```

示例：

```cfg
moon|minecraft:ender_dragon|monster|1|1|1|1
```

有效的生物白名单行会同时加入缺氧免疫规则。没有有效白名单时，行星继续使用生物群系默认生成列表；存在有效白名单时，则由白名单接管该行星的自然生成。

#### 自定义矿脉

`worldgen.cfg` 中的行星矿脉格式如下：

```text
行星键|方块 ID[@元数据]|矿脉大小|每区块次数|最低 Y|最高 Y|替换目标
```

示例：

```cfg
moon|minecraft:iron_ore|8|4|20|60|default
```

支持其他 Mod 的方块 ID、元数据和逗号分隔的替换目标。有效范围为矿脉大小 `1-64`、每区块次数 `0-100`、Y 坐标 `-80` 至 `320`。

#### 外部维度

`external_dimensions.cfg` 不会创建维度，只接入其他 Mod 已注册的维度：

```text
维度 ID|资源 ID|最低火箭等级|温度|重力倍率|天空光照/太阳能值|显示名称（可选）
```

示例：

```cfg
S:externalDimensions <
    -28|galacticraftcore:moon|1|-173|0.166|24|银河月球
>
```

#### 配置额外火箭

`rockets.cfg` 的每行格式为：

```text
注册 ID|显示名称|火箭等级|燃料容量（mB）|模型等级|贴图
```

配置火箭的火箭等级范围为 `16-255`，燃料容量范围为 `1000-64000 mB`，模型等级范围为 `1-15`。留空贴图时使用内置贴图；外部 PNG 文件放在 `config/ad_astra/rocket_png/`。

### 指令

```text
/adastra help
/adastra planets
/adastra setdimension <dimensionId> [x y z]
/adastra radio refresh
/adastra tps [detailed]
```

`/adastra planets` 打开行星选择界面；`setdimension` 用于测试传送，并支持 `tpdim` 和 `dimtp` 别名。传送、性能查看等管理指令需要对应权限等级。

### CraftTweaker 与 Java API

CraftTweaker / ZenScript 的完整方法、参数范围和示例见 [docs/CRT_INTERFACE.md](docs/CRT_INTERFACE.md)。当前提供的脚本入口包括：

```zenscript
import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
```

可用于创建自定义行星、添加或删除 NASA 工作台配方、注册火箭燃料、覆盖行星火箭等级和修改共享空间站配方。

Java API 位于 `earth.terrarium.adastra.api`，当前通过 `ServiceLoader` 接入的主要接口包括：

- `PlanetApi`：查询和注册行星元数据。
- `OxygenApi`：查询、设置和移除局部氧气区域。
- `GravityApi`：查询和修改重力。
- `TemperatureApi`：查询温度和环境适宜性。
- `AdAstraEvents`：监听氧气、温度、重力和环境事件。

相关文档：

- [API 使用说明](src/main/java/earth/terrarium/adastra/api/API_README.md)
- [API 结构摘要](src/main/java/earth/terrarium/adastra/api/API_PORT_SUMMARY.md)
- [API 实现指南](src/main/java/earth/terrarium/adastra/api/IMPLEMENTATION_GUIDE.md)

### 构建与运行

项目使用 Gradle、Unimined 和 Cleanroom Loader。构建脚本声明 Java 25 工具链，并以 Java 21 作为源码编译兼容级别。

```powershell
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

构建产物位于 `build/libs/`。开发运行目录使用 `run/client/` 和 `run/server/`；CraftTweaker 开发脚本放在 `run/client/scripts/`。

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

Ad Astra Reborn is a space exploration mod for Minecraft 1.12.2, built for Cleanroom Loader. It adds rocket launches, planet dimensions, a shared space station, oxygen and environmental systems, machines, resource generation, and mod integration features.

### Project Overview

| Item | Value |
| --- | --- |
| Mod ID | `ad_astra` |
| Mod name | `Ad Astra Reborn` |
| Version | `1.0.0` |
| Minecraft | `1.12.2` |
| Loader | Cleanroom Loader `0.5.12-alpha` |
| Author | GingerYJ |
| Repository | <https://github.com/GingerYJ/Ad-Astra-Reborn-1.12.2> |

### Features

- Built-in tier 1 through tier 15 rockets, plus configurable tier 16 through tier 255 rockets.
- Launch pads, landers, rovers, space suits, jet suits, and rocket fuel systems.
- A planet selection interface with zooming, dragging, planet labels, and reachability checks.
- 19 built-in surface planets and one space-station dimension shared by all players.
- Oxygen, temperature, gravity, solar power, oxygen-deprivation damage, and planetary environment effects.
- Machines including the NASA Workbench, Compressor, Etrionic Blast Furnace, Fuel Refinery, Cryo Freezer, oxygen equipment, generators, Water Pump, and Gravity Normalizer.
- Planet biomes, terrain, ores, fluid lakes, villages or structures, and other world-generation content.
- Planet-specific resources, metal building blocks, pipes, cables, flags, radio stations, and environmental decorations.
- JSON recipes, HEI/JEI recipe integration, CraftTweaker, and Java API integration.
- The built-in Astrodux guidebook is available when Patchouli is installed.

### Built-in Planets

| Group | Planet key | Name |
| --- | --- | --- |
| Core | `moon` | Moon |
| Core | `mars` | Mars |
| Core | `mercury` | Mercury |
| Core | `venus` | Venus |
| Core | `glacio` | Glacio |
| Additional | `ceres` | Ceres |
| Additional | `jupiter` | Jupiter |
| Additional | `saturn` | Saturn |
| Additional | `uranus` | Uranus |
| Additional | `neptune` | Neptune |
| Additional | `orcus` | Orcus |
| Additional | `pluto` | Pluto |
| Additional | `haumea` | Haumea |
| Additional | `quaoar` | Quaoar |
| Additional | `makemake` | Makemake |
| Additional | `gonggong` | Gonggong |
| Additional | `eris` | Eris |
| Additional | `sedna` | Sedna |
| Additional | `proxima_centauri_b` | Proxima Centauri b |

### Dimensions

- The Overworld keeps the vanilla dimension ID `0`.
- The five core planets use IDs `108490` through `108494`.
- Additional planets use IDs `108495` through `108508`.
- The shared space station uses dimension ID `107489`.
- This version does not create a separate orbit dimension for every planet; all space stations use the same shared dimension.
- These dimension IDs and planet registration data are code-owned and should not be edited manually.
- Dimensions registered by other mods can be exposed to the star map and environment systems through `external_dimensions.cfg`.

### Configuration

After the first run, configuration files are generated under `config/ad_astra/`:

| File | Purpose |
| --- | --- |
| `core.cfg` | General gameplay, oxygen, temperature, gravity, performance, and balance settings |
| `client.cfg` | Client interface, HUD, audio, and display settings |
| `machines.cfg` | Machine speed, energy, fluid, capacity, and pipe settings |
| `dimensions.cfg` | Rocket restrictions for surface planets |
| `mobs.cfg` | Planet mob whitelists, entity caps, and oxygen-survival rules |
| `worldgen.cfg` | Ores, world-generation multipliers, and ore debug logging |
| `external_dimensions.cfg` | Dimensions already registered by other mods |
| `rockets.cfg` | Configuration-driven additional rockets |

`ad_astra.cfg` and `debug.cfg` are retained for legacy migration or compatibility handling and are not the main runtime configuration files. During migration, old files are moved to a timestamped backup directory. Configuration changes normally require a game or server restart; ore changes affect new chunks only.

#### Custom Mob Spawning

`mobs.cfg` uses the following `planetMobSpawnWhitelist` format:

```text
planet key|entity ID|spawn type|spawn weight|min group size|max group size|entity count cap
```

Example:

```cfg
moon|minecraft:ender_dragon|monster|1|1|1|1
```

Valid whitelist rows also grant oxygen immunity. A planet with no valid whitelist rows keeps its biome defaults; a planet with valid rows is controlled by those rows for natural spawning.

#### Custom Ores

Planet ore rows in `worldgen.cfg` use:

```text
planet key|block ID[@meta]|vein size|count per chunk|min Y|max Y|replace targets
```

Example:

```cfg
moon|minecraft:iron_ore|8|4|20|60|default
```

Block IDs from other mods, metadata, and comma-separated replacement targets are supported. Valid ranges are vein size `1-64`, count per chunk `0-100`, and Y `-80` through `320`.

#### External Dimensions

`external_dimensions.cfg` does not create dimensions. It exposes dimensions already registered by another mod:

```text
dimension ID|resource ID|minimum rocket tier|temperature|gravity multiplier|skylight/solar value|display name (optional)
```

Example:

```cfg
S:externalDimensions <
    -28|galacticraftcore:moon|1|-173|0.166|24|Galactic Moon
>
```

#### Configurable Rockets

Each `rockets.cfg` row uses:

```text
registry ID|display name|rocket tier|fuel capacity in mB|model tier|texture
```

Configurable rocket tiers range from `16-255`, fuel capacity ranges from `1000-64000 mB`, and model tiers range from `1-15`. Leave the texture empty to use a built-in texture. External PNG files belong in `config/ad_astra/rocket_png/`.

### Commands

```text
/adastra help
/adastra planets
/adastra setdimension <dimensionId> [x y z]
/adastra radio refresh
/adastra tps [detailed]
```

`/adastra planets` opens the planet selection interface. `setdimension` is intended for teleport testing and also has the aliases `tpdim` and `dimtp`. Teleportation and performance commands require the corresponding permission level.

### CraftTweaker and Java API

See [docs/CRT_INTERFACE.md](docs/CRT_INTERFACE.md) for complete CraftTweaker / ZenScript methods, parameter ranges, and examples. The available script entry points include:

```zenscript
import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
```

They can create custom planets, add or remove NASA Workbench recipes, register rocket fuels, override planet rocket tiers, and modify the shared space-station recipe.

The Java API is under `earth.terrarium.adastra.api`. The main interfaces currently connected through `ServiceLoader` are:

- `PlanetApi`: query and register planet metadata.
- `OxygenApi`: query, set, and remove local oxygen areas.
- `GravityApi`: query and modify gravity.
- `TemperatureApi`: query temperature and habitability.
- `AdAstraEvents`: listen for oxygen, temperature, gravity, and environment events.

Related documentation:

- [API usage guide](src/main/java/earth/terrarium/adastra/api/API_README.md)
- [API port summary](src/main/java/earth/terrarium/adastra/api/API_PORT_SUMMARY.md)
- [API implementation guide](src/main/java/earth/terrarium/adastra/api/IMPLEMENTATION_GUIDE.md)

### Build and Run

The project uses Gradle, Unimined, and Cleanroom Loader. The build declares a Java 25 toolchain and Java 21 source compatibility.

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
src/test/                                Resource integrity and registry tests
docs/                                    CraftTweaker documentation
gradle/                                  Gradle scripts and Wrapper
```

### License and Credits

This project is licensed under the [MIT License](LICENSE). Original Ad Astra content and assets belong to Terrarium and the Ad Astra contributors. This project is maintained by GingerYJ and adapted and extended for Minecraft 1.12.2.
