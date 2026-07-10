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
- Patchouli，必需
- Had Enough Items / JEI，推荐
- CraftTweaker `4.1.20`，可选；使用 ZenScript 接口时需要

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
| `dimensions.cfg` | 内置行星的维度、重力和火箭等级 |
| `external_dimensions.cfg` | 接入其他 Mod 已存在的维度 |
| `mobs.cfg` | 行星生物生成 |
| `worldgen.cfg` | 结构、矿物和世界生成 |
| `debug.cfg` | 调试选项 |
| `rockets.cfg` | 配置驱动的额外火箭 |

修改维度列表后应重启游戏或服务器。

### 接入其他 Mod 的维度

需要接入 Galacticraft 或其他模组已经注册的维度时，编辑：

```text
config/ad_astra/external_dimensions.cfg
```

默认列表为空。每行格式为：

```text
维度ID|显示ID|最低火箭等级|温度|重力倍率|天光倍率|显示名称（可选）
```

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

示例：

```cfg
S:customRockets <
    custom_tier_8_rocket|八阶火箭|8|10000|7|custom_tier_8_rocket.png
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
- Patchouli, required
- Had Enough Items / JEI, recommended
- CraftTweaker `4.1.20`, optional and required only for ZenScript integration

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
| `dimensions.cfg` | Built-in planet dimensions, gravity, and rocket tiers |
| `external_dimensions.cfg` | Existing dimensions supplied by other mods |
| `mobs.cfg` | Planet mob spawning |
| `worldgen.cfg` | Structures, ores, and world generation |
| `debug.cfg` | Debug options |
| `rockets.cfg` | Configuration-driven additional rockets |

Restart the game or server after changing dimension lists.

### Integrating External Dimensions

To expose existing Galacticraft or other mod dimensions through Ad Astra rocket travel, edit:

```text
config/ad_astra/external_dimensions.cfg
```

The default list is empty. Each row uses:

```text
dimension ID|display ID|minimum rocket tier|temperature|gravity multiplier|skylight multiplier|display name (optional)
```

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

Example:

```cfg
S:customRockets <
    custom_tier_8_rocket|Tier 8 Rocket|8|10000|7|custom_tier_8_rocket.png
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
