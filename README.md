# Ad Astra Reborn 1.12.2

Ad Astra Reborn 是面向 Minecraft 1.12.2 与 Cleanroom Loader 的太空探索模组，提供火箭、行星、轨道、空间站、环境系统、机器、资源生成和整合接口。

Ad Astra Reborn is a space exploration mod for Minecraft 1.12.2 and Cleanroom Loader. It adds rockets, planets, orbits, space stations, environmental systems, machines, resources, world generation, and integration APIs.

[中文说明](#中文说明) | [English](#english)

- Mod ID: `ad_astra`
- Mod name: `Ad Astra Reborn`
- Version: `1.0.0`
- Author: `GingerYJ`
- Repository: <https://github.com/GingerYJ/Ad-Astra-Reborn-1.12.2>

## 中文说明

### 项目功能

- 1 至 7 阶火箭、发射台、着陆器和漫游车。
- 支持缩放、拖动和轨道显示的动态星图。
- 19 个地表行星、对应轨道维度和空间站旅行。
- 氧气、温度、重力、太阳能和环境伤害系统。
- NASA 工作台、压缩机、合金冶炼炉、燃油精炼机、低温冷冻机、氧气设备、发电设备和管道。
- 行星地形、矿脉、资源、洞穴和结构生成。
- 配置驱动的额外火箭，以及其他 Mod 维度接入。
- CraftTweaker、Java API 和 HEI/JEI 配方集成。
- 安装 Patchouli 后可使用 Astrodux 游戏内指南书。

### 19 个地表行星

内置行星：

| 行星键 | 中文名称 | English name |
| --- | --- | --- |
| `moon` | 月球 | Moon |
| `mars` | 火星 | Mars |
| `mercury` | 水星 | Mercury |
| `venus` | 金星 | Venus |
| `glacio` | 霜原星 | Glacio |

自定义行星：

| 行星键 | 中文名称 | English name |
| --- | --- | --- |
| `ceres` | 谷神星 | Ceres |
| `jupiter` | 木星 | Jupiter |
| `saturn` | 土星 | Saturn |
| `uranus` | 天王星 | Uranus |
| `neptune` | 海王星 | Neptune |
| `orcus` | 奥库斯 | Orcus |
| `pluto` | 冥王星 | Pluto |
| `haumea` | 妊神星 | Haumea |
| `quaoar` | 创神星 | Quaoar |
| `makemake` | 鸟神星 | Makemake |
| `gonggong` | 共工星 | Gonggong |
| `eris` | 阋神星 | Eris |
| `sedna` | 塞德娜 | Sedna |
| `proxima_centauri_b` | 比邻星 b | Proxima Centauri b |

Minecraft 1.12.2 的维度 ID 是整数，由代码固定注册：

- 地表行星 ID：内置行星 `108490`-`108494`，自定义行星 `108495`-`108508`。
- 轨道 ID：地球轨道为 `107489`，内置行星轨道为 `107490`-`107494`，自定义行星轨道为 `107495`-`107508`。
- 这些数字 ID、轨道 ID 和注册表 ID 不写入配置文件，也不建议手动修改。
- 配置分类从 `ModDimensions` 与 `CustomPlanetRegistry` 动态读取；未来注册的地表行星会自动获得对应配置区域。

### 配置文件

首次运行后，配置生成在：

```text
config/ad_astra/
```

| 文件 | 用途 |
| --- | --- |
| `core.cfg` | 通用玩法、环境、性能和平衡设置 |
| `client.cfg` | 客户端界面与显示设置 |
| `machines.cfg` | 机器速度、能量、流体、容量和管道设置 |
| `dimensions.cfg` | 地表行星的火箭等级限制 |
| `mobs.cfg` | 行星生物白名单、数量和缺氧生存设置 |
| `worldgen.cfg` | 行星矿脉、生成倍率和矿脉调试日志 |
| `external_dimensions.cfg` | 接入其他 Mod 已注册的维度 |
| `rockets.cfg` | 配置驱动的额外火箭 |

配置升级时，旧配置和旧版 `ad_astra.cfg` 会移动到带时间戳的 `backup_yyyyMMdd_HHmmss/` 目录，然后生成源码默认值，不导入旧配置值。之后的手动修改会保留。格式版本只由代码内部标记维护，不会生成 `configFormatVersion` 配置项。

`debug.cfg` 不再作为运行时配置文件生成；通用调试日志位于 `core.cfg`，矿脉调试日志位于 `worldgen.cfg`。修改配置通常需要重启游戏或服务器，矿脉修改只影响新生成区块。

### 行星维度配置

`dimensions.cfg` 为每个地表行星生成独立分类：

```cfg
planet_moon {
    I:rocketTier=1
}
```

每个分类只保留 `rocketTier`。数值范围为 `0-15`，其中 `0` 表示不限制火箭等级；未填写时使用该行星注册代码中的默认等级。维度 ID、轨道 ID、注册表 ID、`enabled`、重力倍率以及独立存档目录等不再作为用户配置项显示。

### 行星生物白名单

`mobs.cfg` 的 `planetMobSpawnWhitelist` 使用统一格式：

```text
行星键|实体 ID|生成类型|生成权重|最小群组数量|最大群组数量|实体数量上限
```

例如，让月球自然生成一只末影龙：

```cfg
moon|minecraft:ender_dragon|monster|1|1|1|1
```

字段说明：

- `行星键`：如 `moon`、`mars` 或 `proxima_centauri_b`。
- `实体 ID`：Forge 注册表资源 ID，如 `minecraft:ender_dragon` 或其他 Mod 的实体 ID。
- `生成类型`：`monster` 敌对生物、`creature` 被动生物、`water` 水生生物、`ambient` 环境生物、`cave` 洞穴生物。在 1.12.2 中，`cave` 使用原版的环境生物分类。
- `生成权重`：该实体在同类自然生成候选中的权重。
- `最小群组数量` 和 `最大群组数量`：一次自然生成尝试生成的实体数量范围。
- `实体数量上限`：该行星中该实体的最大总数；`0` 表示禁止该实体自然生成。

规则如下：

- 某行星没有有效白名单行时，继续使用该行星生物群系的默认生成列表。
- 某行星存在有效白名单行时，这些行完全接管该行星的自然生成，未列出的实体不会自然生成。
- 实体按 Forge 注册表延迟解析，支持末影龙和其他 Mod 的生物；不存在、非生物或格式错误的行会忽略并记录警告。
- 白名单中的有效实体会自动加入无氧生存规则。`noOxygenEntityWhitelist` 仍可额外添加不需要氧气的实体，但它只控制缺氧生存，不决定行星生成。
- `planetEntityCapPerType` 默认是 `10`，用于没有白名单数量上限的实体；白名单行的 `maxCount` 优先，并且每个行星、每种实体独立计算。
- `planetMobSpawnRateMultiplier` 名称虽然包含 multiplier，但当前行为是敌对生物总开关：`<=0` 禁止行星敌对生物生成，`>0` 保持默认生成率。

数量统计采用实体加入/离开时的增量维护，并在有玩家的行星维度中定期完整校准：

- `planetMobCountRescanIntervalTicks` 默认 `6000` tick，即 5 分钟；只有对应行星有玩家时才扫描，设为 `0` 表示只执行首次校准。
- `planetMobRespawnIntervalTicks` 默认 `0`；设置为正数后，实体死亡会进入自然生成冷却，单位为 tick。该冷却只影响自然生成，不影响召唤或手动加入的实体。

### 行星矿脉配置

`worldgen.cfg` 的 `worldgen` 分类保留全局选项，包含 `oreGenerationMultiplier` 和 `debugWorldgen`。每个地表行星使用独立区域：

```text
planet_ore_<行星键>
```

每个区域的 `oreVeins` 使用统一格式：

```text
行星键|方块 ID[@元数据]|矿脉大小|每区块次数|最低 Y|最高 Y|替换目标
```

示例，为月球添加铁矿：

```cfg
moon|minecraft:iron_ore|8|4|20|60|default
```

- `方块 ID[@元数据]` 支持 `minecraft:iron_ore`、`modid:block_name` 和 `modid:block_name@meta`。
- `替换目标` 可以是 `default`，也可以是逗号分隔的方块 ID；支持其他 Mod 的方块。
- 范围为矿脉大小 `1-64`、每区块次数 `0-100`、Y 坐标 `-80~320`。
- 方块在世界生成阶段延迟解析，以兼容 Forge 注册时序；无效方块或配置行会忽略并记录警告。
- 内置行星和自定义行星使用同一个解析器和生成路径，避免同一矿脉重复生成。
- 轨道维度和外部维度不生成行星矿脉。
- `oreGenerationMultiplier` 范围为 `0.0-10.0`，默认值为 `2.0`，修改只影响新生成区块。

### 接入外部维度

`external_dimensions.cfg` 用于接入 Galacticraft 或其他 Mod 已经注册的维度，不会创建新的维度或修改目标 Mod 的地形。

格式：

```text
维度 ID|资源 ID|最低火箭等级|温度（摄氏度）|重力倍率|天空光照/太阳能值|显示名称（可选）
```

中文示例：

```cfg
S:externalDimensions <
    -28|galacticraftcore:moon|1|-173|0.166|24|银河月球
    -29|galacticraftplanets:mars|2|-65|0.38|12|银河火星
 >
```

- `维度 ID` 必须是目标 Mod 注册的整数维度 ID。
- `资源 ID` 用于星图和环境 API，例如 `galacticraftcore:moon`。
- `最低火箭等级` 范围 `0-15`，`0` 表示不限制。
- `温度` 范围 `-32768~32767`，单位为摄氏度值。
- `重力倍率` 范围 `0.0-10.0`，`1.0` 等于主世界重力。
- `天空光照/太阳能值` 范围 `0-1024`，`0` 表示没有天空光照和太阳能值。
- `显示名称` 可选；缺少该字段的六字段旧格式仍兼容。
- 未注册的维度会被忽略并保留配置行，以便目标 Mod 加载后再次使用。

### 配置额外火箭

`rockets.cfg` 的格式为：

```text
注册 ID|显示名称|火箭等级|燃料容量（mB）|模型等级|贴图
```

中文示例：

```cfg
S:customRockets <
    lunar_scout|月球侦察火箭|8|10000|7|lunar_scout.png
 >
```

火箭等级范围为 `1-15`，燃料容量为 `1000-64000 mB`，模型等级为 `1-12`。贴图留空时使用内置贴图；相对 PNG 文件放在：

```text
config/ad_astra/rocket_png/
```

### CraftTweaker 与 Java API

CraftTweaker 的完整方法签名和示例见 [CraftTweaker / ZenScript 教程](docs/CRT_INTERFACE.md)。常用入口包括：

```zenscript
import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
```

开发运行目录使用 `run/client/scripts/`，普通整合包实例使用 `scripts/`。

Java API 文档：

- [API 使用说明](src/main/java/earth/terrarium/adastra/api/API_README.md)
- [API 结构摘要](src/main/java/earth/terrarium/adastra/api/API_PORT_SUMMARY.md)

API 覆盖氧气、温度、重力、行星查询与注册、环境事件、机器配方、自定义火箭与载具，以及 Forge Capability 集成。

### 构建与运行

项目使用 Gradle、Unimined 和 Cleanroom Loader。Gradle 工具链使用 Java 25，源码编译兼容级别为 Java 21。

```powershell
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

构建产物位于：

```text
build/libs/
```

### 许可证与鸣谢

本项目使用 [MIT License](LICENSE)。原始 Ad Astra 内容和资源归 Terrarium 与 Ad Astra 贡献者所有。本项目由 GingerYJ 维护，并针对 Minecraft 1.12.2 进行适配与扩展。

---

## English

### Features

- Tier 1 through tier 7 rockets, launch pads, landers, and rovers.
- A dynamic star map with zooming, dragging, labels, and orbit visualization.
- 19 surface planets, paired orbit dimensions, and space-station travel.
- Oxygen, temperature, gravity, solar-power, and environmental damage systems.
- Machines including the NASA Workbench, Compressor, Alloy Smelter, Fuel Refinery, Cryo Freezer, oxygen equipment, generators, and pipes.
- Planet-specific terrain, ores, resources, caves, and structures.
- Configurable additional rockets and travel integration for dimensions registered by other mods.
- CraftTweaker, Java API, and HEI/JEI recipe integration.
- The Astrodux in-game guidebook becomes available when Patchouli is installed.

### 19 Surface Planets

Built-in planets:

| Planet key | Chinese name | English name |
| --- | --- | --- |
| `moon` | 月球 | Moon |
| `mars` | 火星 | Mars |
| `mercury` | 水星 | Mercury |
| `venus` | 金星 | Venus |
| `glacio` | 霜原星 | Glacio |

Custom planets:

| Planet key | Chinese name | English name |
| --- | --- | --- |
| `ceres` | 谷神星 | Ceres |
| `jupiter` | 木星 | Jupiter |
| `saturn` | 土星 | Saturn |
| `uranus` | 天王星 | Uranus |
| `neptune` | 海王星 | Neptune |
| `orcus` | 奥库斯 | Orcus |
| `pluto` | 冥王星 | Pluto |
| `haumea` | 妊神星 | Haumea |
| `quaoar` | 创神星 | Quaoar |
| `makemake` | 鸟神星 | Makemake |
| `gonggong` | 共工星 | Gonggong |
| `eris` | 阋神星 | Eris |
| `sedna` | 塞德娜 | Sedna |
| `proxima_centauri_b` | 比邻星 b | Proxima Centauri b |

Minecraft 1.12.2 uses integer dimension IDs. Ad Astra assigns them in code:

- Surface IDs: built-in planets `108490`-`108494`, custom planets `108495`-`108508`.
- Orbit IDs: Earth orbit is `107489`, built-in planet orbits are `107490`-`107494`, and custom planet orbits are `107495`-`107508`.
- Numeric dimension IDs, orbit IDs, and registry IDs are code-owned and are not written to configuration files.
- Configuration categories are discovered from `ModDimensions` and `CustomPlanetRegistry`; newly registered surface planets receive categories automatically.

### Configuration Files

After the first run, configuration files are generated under:

```text
config/ad_astra/
```

| File | Purpose |
| --- | --- |
| `core.cfg` | General gameplay, environment, performance, and balance settings |
| `client.cfg` | Client interface and display settings |
| `machines.cfg` | Machine speed, energy, fluid, capacity, and pipe settings |
| `dimensions.cfg` | Rocket restrictions for surface planets |
| `mobs.cfg` | Planet mob whitelists, counts, and oxygen rules |
| `worldgen.cfg` | Planet ores, generation multiplier, and ore debug logging |
| `external_dimensions.cfg` | Existing dimensions registered by other mods |
| `rockets.cfg` | Configuration-driven additional rockets |

During a configuration migration, old files and the legacy `ad_astra.cfg` are moved to a timestamped `backup_yyyyMMdd_HHmmss/` directory. Fresh source defaults are then generated; old values are not imported. Later user edits are preserved. The format version is maintained by an internal code marker and no `configFormatVersion` property is generated.

`debug.cfg` is no longer generated as a runtime configuration file. General debug logging is in `core.cfg`, and ore debug logging is in `worldgen.cfg`. Configuration changes normally require a game or server restart. Ore changes affect new chunks only.

### Planet Dimension Configuration

`dimensions.cfg` creates one category for every surface planet:

```cfg
planet_moon {
    I:rocketTier=1
}
```

Each category contains only `rocketTier`. The valid range is `0-15`; `0` removes the rocket restriction. When omitted, the planet's registration default is used. Dimension IDs, orbit IDs, registry IDs, `enabled`, gravity multipliers, and dedicated save-folder settings are not exposed as user properties.

### Planet Mob Whitelist

`mobs.cfg` uses the unified `planetMobSpawnWhitelist` format:

```text
planet key|entity ID|spawn type|spawn weight|min group size|max group size|entity count cap
```

For example, make one Ender Dragon spawn naturally on the Moon:

```cfg
moon|minecraft:ender_dragon|monster|1|1|1|1
```

Field reference:

- `planet key`: for example `moon`, `mars`, or `proxima_centauri_b`.
- `entity ID`: a Forge registry resource ID such as `minecraft:ender_dragon` or an entity from another mod.
- `spawn type`: `monster` for hostile mobs, `creature` for passive mobs, `water` for water creatures, `ambient` for ambient creatures, and `cave` for cave creatures. In 1.12.2, `cave` maps to the vanilla ambient creature category.
- `spawn weight`: the weight among candidates of the same natural-spawn category.
- `min group size` and `max group size`: the number of entities in one spawn attempt.
- `entity count cap`: the maximum total for that entity on that planet; `0` disables natural spawning for the row.

Behavior:

- A planet with no valid whitelist rows keeps its biome default spawn list.
- A planet with valid whitelist rows is fully controlled by those rows; omitted entities do not spawn naturally.
- Entities are resolved lazily through the Forge registry, so Ender Dragon and entities from other mods are supported. Missing, non-living, or malformed rows are ignored with warnings.
- Valid whitelist entities automatically receive oxygen immunity. `noOxygenEntityWhitelist` can add more entities, but it controls oxygen survival only and does not define planet spawning.
- `planetEntityCapPerType` defaults to `10` for entities without a row-specific cap. A row's `maxCount` takes priority, and counts are independent per planet and entity type.
- Despite its name, `planetMobSpawnRateMultiplier` currently acts as a hostile-mob switch: `<=0` disables hostile planet spawning and `>0` keeps the default spawn rate.

Counts are maintained incrementally when entities join or leave and periodically calibrated with a full scan:

- `planetMobCountRescanIntervalTicks` defaults to `6000` ticks, or five minutes. Scans run only while the corresponding planet has players; `0` performs the initial calibration only.
- `planetMobRespawnIntervalTicks` defaults to `0`. A positive value adds a natural-spawn cooldown after an entity dies, in ticks. It affects natural spawning only, not summoned or manually added entities.

### Planet Ore Configuration

The `worldgen` category in `worldgen.cfg` contains global options including `oreGenerationMultiplier` and `debugWorldgen`. Each surface planet has its own section:

```text
planet_ore_<planet key>
```

The `oreVeins` list in each section uses one format:

```text
planet key|block ID[@meta]|vein size|count per chunk|min Y|max Y|replace targets
```

Example, add iron ore to the Moon:

```cfg
moon|minecraft:iron_ore|8|4|20|60|default
```

- `block ID[@meta]` supports `minecraft:iron_ore`, `modid:block_name`, and `modid:block_name@meta`.
- `replace targets` can be `default` or a comma-separated list of block IDs, including blocks from other mods.
- Ranges are vein size `1-64`, count per chunk `0-100`, and Y `-80` to `320`.
- Blocks are resolved when world generation first needs them to tolerate Forge registration order. Invalid blocks or rows are ignored with warnings.
- Built-in and custom planets use the same parser and generation path, preventing duplicate ore generation.
- Orbit and external dimensions do not generate planet ores.
- `oreGenerationMultiplier` ranges from `0.0` to `10.0`, defaults to `2.0`, and affects new chunks only.

### External Dimensions

Use `external_dimensions.cfg` to expose a dimension already registered by Galacticraft or another mod. It does not create a new dimension or change the target mod's terrain.

Format:

```text
dimension ID|resource ID|minimum rocket tier|temperature in Celsius|gravity multiplier|skylight/solar-power value|display name (optional)
```

Chinese example:

```cfg
S:externalDimensions <
    -28|galacticraftcore:moon|1|-173|0.166|24|银河月球
    -29|galacticraftplanets:mars|2|-65|0.38|12|银河火星
 >
```

- `dimension ID` must be the integer dimension ID registered by the target mod.
- `resource ID` is used by the star map and environment API, such as `galacticraftcore:moon`.
- `minimum rocket tier` ranges from `0-15`; `0` disables the restriction.
- `temperature` ranges from `-32768` to `32767` and is a Celsius value.
- `gravity multiplier` ranges from `0.0` to `10.0`; `1.0` matches Overworld gravity.
- `skylight/solar-power value` ranges from `0` to `1024`; `0` means no skylight or solar value.
- `display name` is optional. The legacy six-field format without a display name remains supported.
- Unregistered dimensions are ignored with the row kept for later reuse.

### Configurable Rockets

`rockets.cfg` uses:

```text
registry ID|display name|rocket tier|fuel capacity in mB|model tier|texture
```

Chinese example:

```cfg
S:customRockets <
    lunar_scout|月球侦察火箭|8|10000|7|lunar_scout.png
 >
```

Rocket tier is `1-15`, fuel capacity is `1000-64000 mB`, and model tier is `1-12`. Leave the texture empty to use a built-in texture. Relative PNG files belong in:

```text
config/ad_astra/rocket_png/
```

### CraftTweaker and Java API

See the complete method reference and examples in the [CraftTweaker / ZenScript guide](docs/CRT_INTERFACE.md). Common entry points include:

```zenscript
import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
```

For development runs, place scripts in `run/client/scripts/`. For a normal modpack instance, use `scripts/`.

Java API documentation:

- [API usage guide](src/main/java/earth/terrarium/adastra/api/API_README.md)
- [API structure summary](src/main/java/earth/terrarium/adastra/api/API_PORT_SUMMARY.md)

The API covers oxygen, temperature, gravity, planet queries and registration, environmental events, machine recipes, custom rockets and vehicles, and Forge Capability integration.

### Build and Run

The project uses Gradle, Unimined, and Cleanroom Loader. The Gradle toolchain uses Java 25, while source compilation targets Java 21 compatibility.

```powershell
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

Built jars are written to:

```text
build/libs/
```

### License and Credits

This project is licensed under the [MIT License](LICENSE). Original Ad Astra content and assets are credited to Terrarium and the Ad Astra contributors. This Minecraft 1.12.2 adaptation and expansion is maintained by GingerYJ.
