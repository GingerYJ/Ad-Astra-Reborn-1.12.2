# Ad Astra Reborn 1.12.2

Ad Astra Reborn 是一个面向 Minecraft 1.12.2 与 Cleanroom 环境的太空探索模组，包含多等级火箭、动态星图、行星与轨道、空间站、机器、环境系统，以及面向整合包作者的配置和 CraftTweaker 接口。

Ad Astra Reborn is a space exploration mod for Minecraft 1.12.2 and Cleanroom. It provides tiered rockets, a dynamic star map, planets and orbits, space stations, machines, environmental systems, and pack-maker-friendly configuration and CraftTweaker APIs.

[中文](#中文说明) | [English](#english)

- 当前版本：`1.0.0`
- Mod ID：`ad_astra`
- 维护者：`GingerYJ`

## 中文说明

### 主要功能

- 1 至 7 阶火箭、发射台、着陆器和漫游车。
- 可缩放、拖动并显示轨道的动态星图。
- 多个内置行星、卫星和轨道维度。
- 氧气、温度、重力与太阳能环境系统。
- NASA 工作台、压缩机、燃油精炼机、氧气设备、发电设备等机器。
- 行星专属地形、矿物、资源和世界生成。
- 空间站建造与轨道旅行。
- HEI/JEI 配方及行星信息集成。
- 配置驱动的额外火箭。
- CraftTweaker 自定义配方、燃料、火箭等级、空间站和行星接口。
- 可将其他 Mod 已注册的维度加入火箭星图。

### 运行需求

- Minecraft `1.12.2`
- Cleanroom Loader `0.5.12-alpha` 或兼容的 1.12.2 运行环境
- Had Enough Items / JEI，推荐
- CraftTweaker `4.1.20`，可选；使用 ZenScript 接口时需要

可选兼容：

- 安装 Patchouli（帕秋莉手册）后会自动加载 Astrodux 游戏内指南书；未安装时不会出现手册，也不影响模组运行。

### 配置文件

首次运行后，配置生成在：

```text
config/ad_astra/
```

主要配置文件：

| 文件 | 用途 |
| --- | --- |
| `core.cfg` | 通用玩法和环境系统 |
| `client.cfg` | 客户端显示与 UI |
| `machines.cfg` | 机器速度、能耗与容量 |
| `dimensions.cfg` | 19 个地表行星的火箭等级 |
| `external_dimensions.cfg` | 接入其他 Mod 已存在的维度 |
| `mobs.cfg` | 行星生物生成 |
| `worldgen.cfg` | 结构、矿物和世界生成 |
| `rockets.cfg` | 配置驱动的额外火箭 |

格式升级标记由代码维护，不写入配置文件。格式升级时，旧配置会移动到 `config/ad_astra/backup_yyyyMMdd_HHmmss/`，然后生成全新默认值，不导入旧值；之后的手动修改会保留。修改后应重启游戏或服务器，矿脉修改只影响新生成的区块。

`dimensions.cfg` 为每个已注册地表行星使用一个 `planet_<planetKey>` 分类。当前范围为 19 个行星：月球、火星、水星、金星、冰川星、谷神星、木星、土星、天王星、海王星、奥尔库斯、冥王星、妊神星、创神星、鸟神星、共工星、阋神星、塞德娜和比邻星 b。轨道维度和外部维度不参与行星生物与矿脉配置。

每个行星分类仅包含 `rocketTier`，分类注释会显示对应的中文和英文行星名称。维度数字 ID、轨道 ID 和注册表 ID 仅由注册代码维护，不写入配置；`rocketTier=0` 表示不限制火箭等级。当前版本不会生成没有运行时消费者的旧配置项。

`mobs.cfg` 使用统一的 `planetMobSpawnWhitelist` 列表：

```text
planet|entityId|spawnType|weight|minGroup|maxGroup|maxCount
```

支持 `monster`、`creature`、`water`、`ambient`、`cave`。行星没有白名单行时使用生物群系默认生成；有白名单行时完全接管自然生成。实体 ID 按 Forge 注册表解析，支持 `minecraft:ender_dragon` 和其他 Mod 实体；白名单中的实体自动无视缺氧，`noOxygenEntityWhitelist` 用于额外添加实体。

`maxCount=0` 禁止该实体；其他实体使用 `planetEntityCapPerType` 独立限额。

`worldgen.cfg` 的 `worldgen` 区域只保留全局选项；每个地表行星单独使用一个 `planet_ore_<planetKey>` 区域。每个区域都包含自己的 `oreVeins` 列表，当前生成 19 个地表行星区域：

```text
planet|namespace:block[@meta]|veinSize|count|minY|maxY|replaceTargets
```

示例（为月球添加铁矿）：

```text
moon|minecraft:iron_ore|8|4|20|60|default
```

`replaceTargets` 可写 `default` 或逗号分隔的方块 ID；目标可以是矿石或普通方块，也可以来自其他 Mod。所有行使用 `oreGenerationMultiplier`，轨道维度和外部维度不生成矿脉。

范围：矿脉大小 1-64、每区块次数 0-100、Y -80~320；方块可写 `namespace:block@meta`。

### 接入其他 Mod 的维度

需要接入 Galacticraft 或其他模组已经注册的维度时，编辑：

```text
config/ad_astra/external_dimensions.cfg
```

默认列表为空。每行格式为：

```text
维度ID|显示ID|最低火箭等级|温度|重力倍率|天光倍率|显示名称（可选）
```

范围：等级 0-15、温度 -32768~32767、重力 0.0-10.0、天光 0-1024；未注册维度会保留配置但被忽略。

也兼容不带“显示名称”的 6 字段格式。

示例：

```cfg
S:externalDimensions <
    -28|galacticraftcore:moon|1|-173|0.166|24|星系-月球
    -29|galacticraftplanets:mars|2|-65|0.38|12|星系-火星
    -30|galacticraftplanets:asteroids|3|-100|0.08|20|星系-小行星带
    -31|galacticraftplanets:venus|3|464|0.90|8|星系-金星
 >
```

字段说明：

| 字段 | 说明 |
| --- | --- |
| 维度 ID | 目标 Mod 实际注册的维度数字 ID |
| 显示 ID | 星图内部使用的资源 ID，如 `galacticraftcore:moon` |
| 最低火箭等级 | `0` 表示不限制，否则需要对应等级或更高等级火箭 |
| 温度 | Ad Astra 环境系统使用的温度值 |
| 重力倍率 | `1.0` 等于主世界重力 |
| 天光倍率 | 用于太阳能与环境数据；`0` 表示无天光 |
| 显示名称 | 可选，可写成 `星系-月球` 等带前缀名称 |

注意：

- 外部维度使用目标 Mod 自己的地形，不需要配置表面方块和填充方块。
- 此功能只把维度接入火箭星图和 Ad Astra 环境数据，不会拦截传送门或其他 Mod 的旅行方式。
- 星图轨道按照配置中的有效条目顺序从内向外排列。
- 删除中间条目后，后面的条目会在下次启动时自动向内补位。
- 这些轨道是星图布局环，不会为外部维度创建独立轨道维度或空间站维度。
- 维度 ID 可从目标 Mod 的配置、日志、Wiki 或 `/forge dimensions` 等可用的维度查询命令中确认。

### 配置额外火箭

编辑：

```text
config/ad_astra/rockets.cfg
```

格式：

```text
注册ID|显示名称|火箭等级|燃料容量mB|复用模型等级|贴图
```

等级范围 1-15，燃料 1000-64000 mB，模型 1-12；贴图为空时使用内置贴图，相对 PNG 放在 `config/ad_astra/rocket_png/`。

示例：

```cfg
S:customRockets <
    lunar_scout|月球侦察火箭|8|10000|7|lunar_scout.png
 >
```

外部 PNG 放在：

```text
config/ad_astra/rocket_png/
```

### CraftTweaker

完整教程与准确的方法签名见：

- [CraftTweaker / ZenScript 教程](docs/CRT_INTERFACE.md)

可用入口：

```zenscript
import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
```

在开发运行目录中，脚本应放在：

```text
run/client/scripts/
```

整合包中则使用实例根目录的：

```text
scripts/
```

### 构建与运行

项目使用 Gradle 与 Unimined。Gradle 工具链使用 Java 25，源码编译兼容级别为 Java 21。

```powershell
.\gradlew.bat compileJava
.\gradlew.bat build
.\gradlew.bat runClient
```

构建产物位于：

```text
build/libs/
```

### 许可证与鸣谢

本项目使用 [MIT License](LICENSE)。

原始 Ad Astra 内容和资源归 Terrarium 与 Ad Astra 贡献者所有。本项目由 GingerYJ 维护，并针对 Minecraft 1.12.2 进行适配与扩展。

---

## English

### Features

- Tier 1 through tier 7 rockets, launch pads, landers, and rovers.
- A dynamic star map with zooming, dragging, labels, and orbit visualization.
- Many built-in planet, moon, and orbit dimensions.
- Oxygen, temperature, gravity, and solar-power environmental systems.
- Machines including the NASA Workbench, Compressor, Fuel Refinery, oxygen equipment, and generators.
- Planet-specific terrain, ores, resources, and world generation.
- Space station construction and orbit travel.
- HEI/JEI recipe and destination information.
- Additional rockets defined through configuration.
- CraftTweaker APIs for recipes, fuels, rocket tiers, space stations, and custom planets.
- Rocket travel integration for dimensions registered by other mods.

### Requirements

- Minecraft `1.12.2`
- Cleanroom Loader `0.5.12-alpha`, or a compatible 1.12.2 runtime
- Had Enough Items / JEI, recommended
- CraftTweaker `4.1.20`, optional and required only for ZenScript integration

Optional integration:

- Installing Patchouli automatically enables the in-game Astrodux guidebook. Without Patchouli, the guidebook is omitted and the rest of the mod remains available.

### Configuration

Configuration files are generated under:

```text
config/ad_astra/
```

Main files:

| File | Purpose |
| --- | --- |
| `core.cfg` | General gameplay and environmental systems |
| `client.cfg` | Client display and UI |
| `machines.cfg` | Machine speed, energy use, and capacity |
| `dimensions.cfg` | Rocket tiers for 19 registered surface planets |
| `external_dimensions.cfg` | Existing dimensions supplied by other mods |
| `mobs.cfg` | Planet mob spawning |
| `worldgen.cfg` | Structures, ores, and world generation |
| `rockets.cfg` | Configuration-driven additional rockets |

The format migration marker is maintained by code and is not written into configuration files. When the format changes, the old files are moved to `config/ad_astra/backup_yyyyMMdd_HHmmss/` and fresh defaults are generated; old values are not imported. Later edits are preserved. Restart the game or server after configuration changes. Ore changes affect new chunks only.

`dimensions.cfg` uses one category per registered surface planet: `planet_<planetKey>`. The current 19 planets are Moon, Mars, Mercury, Venus, Glacio, Ceres, Jupiter, Saturn, Uranus, Neptune, Orcus, Pluto, Haumea, Quaoar, Makemake, Gonggong, Eris, Sedna, and Proxima Centauri b. Orbit and external dimensions are excluded from planet mob and ore settings.

Each planet category contains only `rocketTier`; its comment shows the Chinese and English planet names. Numeric dimension, orbit, and registry IDs are code-owned and are not written to the configuration; `rocketTier=0` removes the rocket restriction. Legacy options without a runtime consumer are no longer generated.

`mobs.cfg` uses `planetMobSpawnWhitelist` rows:

```text
planet|entityId|spawnType|weight|minGroup|maxGroup|maxCount
```

Supported types are `monster`, `creature`, `water`, `ambient`, and `cave`. An empty whitelist keeps biome defaults; rows for a planet replace its full natural spawn list. Entity IDs are Forge registry IDs, so entries such as `minecraft:ender_dragon` and entities from other mods are supported. Whitelisted entities automatically ignore oxygen; `noOxygenEntityWhitelist` adds extra entities.

`maxCount=0` blocks that entity; other entities use the independent `planetEntityCapPerType` limit.

`planetMobCountRescanIntervalTicks` controls the full count calibration interval. The default `6000` is five minutes; calibration runs only in planet dimensions with players, and `0` performs only the initial calibration. `planetMobRespawnIntervalTicks` adds a cooldown after a living entity dies before its natural spawn checks are accepted; `0` keeps the default behavior. The cooldown does not block summoned or otherwise manually added entities.

In `worldgen.cfg`, the `worldgen` section contains only global options. Each surface planet has its own `planet_ore_<planetKey>` section with an `oreVeins` list. The current defaults generate 19 surface-planet sections:

```text
planet|namespace:block[@meta]|veinSize|count|minY|maxY|replaceTargets
```

Example (add iron ore to the Moon):

```text
moon|minecraft:iron_ore|8|4|20|60|default
```

Use `default` or comma-separated block IDs for `replaceTargets`. The target may be an ore or any ordinary block, including blocks from other mods. All rows use `oreGenerationMultiplier`; orbit and external dimensions do not generate ores.

Ranges: vein size 1-64, count 0-100, Y -80 to 320; blocks may use `namespace:block@meta`.

### Integrating External Dimensions

To expose existing Galacticraft or other mod dimensions through Ad Astra rocket travel, edit:

```text
config/ad_astra/external_dimensions.cfg
```

The default list is empty. Each row uses:

```text
dimension ID|display ID|minimum rocket tier|temperature|gravity multiplier|skylight multiplier|display name (optional)
```

Ranges: tier 0-15, temperature -32768 to 32767, gravity 0.0-10.0, skylight 0-1024; unregistered dimensions remain saved but are ignored.

The legacy six-field format without a display name is also accepted.

Example:

```cfg
S:externalDimensions <
    -28|galacticraftcore:moon|1|-173|0.166|24|Galaxy-Moon
    -29|galacticraftplanets:mars|2|-65|0.38|12|Galaxy-Mars
    -30|galacticraftplanets:asteroids|3|-100|0.08|20|Galaxy-Asteroids
    -31|galacticraftplanets:venus|3|464|0.90|8|Galaxy-Venus
 >
```

Field reference:

| Field | Meaning |
| --- | --- |
| Dimension ID | The numeric dimension ID registered by the target mod |
| Display ID | Resource ID used internally by the star map, such as `galacticraftcore:moon` |
| Minimum rocket tier | `0` disables the tier restriction |
| Temperature | Temperature used by Ad Astra environmental systems |
| Gravity multiplier | `1.0` matches Overworld gravity |
| Skylight multiplier | Solar/environment value; `0` means no skylight |
| Display name | Optional UI label, including prefixes such as `Galaxy-Moon` |

Important behavior:

- External dimensions keep their own terrain. Surface and filler blocks are not configured here.
- Integration only adds rocket-map and environmental data. Portals and travel systems from other mods remain available.
- Valid entries receive star-map layout rings from inner to outer in configuration order.
- Removing an entry automatically shifts all following entries inward after the next restart.
- These rings are visual star-map orbits. No dedicated orbit or space-station dimension is generated.
- Obtain numeric dimension IDs from the target mod configuration, logs, documentation, or an available dimension-list command such as `/forge dimensions`.

### Configurable Rockets

Edit:

```text
config/ad_astra/rockets.cfg
```

Format:

```text
registry ID|display name|rocket tier|fuel capacity in mB|reused model tier|texture
```

Tier 1-15, fuel 1000-64000 mB, model 1-12; an empty texture uses the built-in texture, and relative PNG files go in `config/ad_astra/rocket_png/`.

Example:

```cfg
S:customRockets <
    lunar_scout|Lunar Scout|8|10000|7|lunar_scout.png
 >
```

External PNG textures belong in:

```text
config/ad_astra/rocket_png/
```

### CraftTweaker

See the full method reference and working examples:

- [CraftTweaker / ZenScript Guide](docs/CRT_INTERFACE.md)

Available entry points:

```zenscript
import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
```

Development scripts belong in:

```text
run/client/scripts/
```

For a normal modpack instance, use:

```text
scripts/
```

### Build and Run

The project uses Gradle and Unimined. The Gradle toolchain uses Java 25, while source compatibility targets Java 21.

```powershell
.\gradlew.bat compileJava
.\gradlew.bat build
.\gradlew.bat runClient
```

Built jars are written to:

```text
build/libs/
```

### License and Credits

This project is licensed under the [MIT License](LICENSE).

Original Ad Astra content and assets are credited to Terrarium and the Ad Astra contributors. This Minecraft 1.12.2 adaptation and expansion is maintained by GingerYJ.
