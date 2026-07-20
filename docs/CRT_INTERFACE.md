# Ad Astra Reborn CraftTweaker / ZenScript 教程

本文档对应 Minecraft 1.12.2、CraftTweaker 4.1.20 和 Ad Astra Reborn 1.0.0。

## 目录

- [准备脚本](#准备脚本)
- [两种维度接入方式](#两种维度接入方式)
- [自定义行星](#自定义行星)
- [NASA 工作台配方](#nasa-工作台配方)
- [火箭燃料](#火箭燃料)
- [行星火箭等级](#行星火箭等级)
- [空间站配方](#空间站配方)
- [排错](#排错)
- [API 快速参考](#api-快速参考)

## 准备脚本

开发环境脚本目录：

```text
run/client/scripts/
```

普通整合包实例脚本目录：

```text
scripts/
```

创建一个 `.zs` 文件，并在开头写：

```zenscript
#loader crafttweaker

import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
```

脚本修改后建议完整重启客户端或服务器。自定义维度注册不应依赖热重载。

## 两种维度接入方式

请先判断你要处理的是哪一种维度：

### 1. 其他 Mod 已经创建的维度

例如 Galacticraft 月球、火星或某个矿物世界 Mod 的维度。这些维度已经拥有自己的 WorldProvider 和地形生成。

不要使用 `CustomPlanets.create()` 重复注册它们。请编辑：

```text
config/ad_astra/external_dimensions.cfg
```

格式与示例见项目 [README](../README.md#接入其他-mod-的维度)。

### 2. 由 Ad Astra Reborn 创建的新行星

如果你需要一个全新的行星、地形和表面维度，可以使用：

```zenscript
CustomPlanets.create(...)
```

默认情况下，构建器只登记行星数据。只有调用：

```zenscript
.enableDimensionRegistration(true)
```

才会请求 Ad Astra Reborn 注册行星表面维度。

## 自定义行星

### 最小可运行示例

```zenscript
#loader crafttweaker

import mods.ad_astra.CustomPlanets;

CustomPlanets.create("example:basalt_moon", 1401)
    .name("basalt_moon")
    .displayName("玄武岩卫星")
    .tier(2)
    .biome("minecraft:desert")
    .surface(<block:minecraft:stone>)
    .stone(<block:minecraft:stone>)
    .icon(<minecraft:obsidian>)
    .skyLight(true)
    .canRespawn(false)
    .environment(false, -40, 0.42, 18)
    .dayLength(24000)
    .colors(0.02, 0.02, 0.03, 0.18, 0.20, 0.28)
    .addOre(
        <block:minecraft:iron_ore>,
        <block:minecraft:stone>,
        6,
        8,
        4,
        48
    )
    .enableDimensionRegistration(true)
    .register();
```

这个示例会使用：

- 行星维度 ID：`1401`
- 行星存档目录：自动生成
- 行星资源 ID：`example:basalt_moon`
- 空间站维度：所有玩家共享唯一的 `ad_astra:space_station`

### 创建与查询

```zenscript
CustomPlanets.create(String id, int dimensionId);
CustomPlanets.hasPlanet(String id);
CustomPlanets.getRegisteredCount();
```

说明：

- `id` 建议使用 `modid:path` 格式。
- 省略命名空间时使用 `ad_astra`。
- `dimensionId` 不能为 `0`。
- 同一脚本加载周期内，行星 ID 和维度 ID 应保持唯一。

示例：

```zenscript
if (!CustomPlanets.hasPlanet("example:basalt_moon")) {
    print("Basalt Moon has not been registered yet.");
}
```

### 构建器方法

| 方法 | 说明 |
| --- | --- |
| `.name(String)` | 内部行星名称；会被整理为小写字母、数字和下划线 |
| `.displayName(String)` | 星图中显示的名称 |
| `.saveFolder(String)` | 自定义存档文件夹名称 |
| `.biome(String)` | 生物群系资源 ID |
| `.surface(IBlock/IItemStack)` | 地表方块 |
| `.stone(IBlock/IItemStack)` | 主要地下方块，也是默认填充方块 |
| `.icon(IItemStack)` | 星图图标物品 |
| `.iconBlock(IBlock)` | 使用方块设置星图图标 |
| `.skyLight(boolean)` | 是否有天空光 |
| `.canRespawn(boolean)` | 是否允许在该维度重生 |
| `.environment(boolean, int, double, int)` | 氧气、温度、重力、太阳能倍率 |
| `.tier(int)` | 最低火箭等级，必须大于 0 |
| `.dayLength(int)` | 一天的游戏刻数，必须大于 0 |
| `.colors(double...)` | 雾 RGB 和天空 RGB，每项通常使用 `0.0` 至 `1.0` |
| `.addOre(...)` | 添加矿物生成 |
| `.enableDimensionRegistration(boolean)` | 是否注册新的行星表面维度 |
| `.register()` | 完成并提交定义，必须最后调用 |

### 环境参数

```zenscript
.environment(boolean oxygen, int temperature, double gravity, int solarPower)
```

| 参数 | 示例 | 说明 |
| --- | --- | --- |
| `oxygen` | `false` | `false` 时需要氧气保护 |
| `temperature` | `-40` | Ad Astra 温度系统使用的摄氏温度 |
| `gravity` | `0.42` | `1.0` 为主世界重力 |
| `solarPower` | `18` | 太阳能与环境使用的倍率值 |

### 矿物生成

```zenscript
.addOre(
    IBlock ore,
    IBlock replace,
    int veinSize,
    int countPerChunk,
    int minY,
    int maxY
)
```

也可以用 `IItemStack` 方块物品作为前两个参数。

重要规则：

- `veinSize` 必须大于 0。
- `countPerChunk` 不能小于 0。
- `minY` 和 `maxY` 必须在 `0` 至 `255`。
- `minY` 不能大于 `maxY`。
- `replace` 必须与实际地下方块匹配，否则矿物没有可替换的方块。

例如 `.stone(<block:minecraft:stone>)` 时：

```zenscript
.addOre(
    <block:minecraft:diamond_ore>,
    <block:minecraft:stone>,
    4,
    3,
    4,
    24
)
```

### 完整自定义行星与空间站示例

```zenscript
#loader crafttweaker

import mods.ad_astra.CustomPlanets;
import mods.ad_astra.SpaceStation;

CustomPlanets.create("example:mineral_world", 1401)
    .name("mineral_world")
    .displayName("矿物世界")
    .saveFolder("DIM_MINERAL_WORLD")
    .tier(3)
    .biome("minecraft:desert")
    .surface(<block:minecraft:iron_block>)
    .stone(<block:minecraft:stone>)
    .icon(<minecraft:diamond_block>)
    .skyLight(true)
    .canRespawn(false)
    .environment(false, 80, 1.5, 32)
    .dayLength(36000)
    .colors(0.55, 0.35, 0.15, 0.75, 0.55, 0.25)
    .addOre(<block:minecraft:iron_ore>, <block:minecraft:stone>, 12, 24, 0, 128)
    .addOre(<block:minecraft:gold_ore>, <block:minecraft:stone>, 6, 10, 0, 64)
    .addOre(<block:minecraft:diamond_ore>, <block:minecraft:stone>, 4, 5, 0, 32)
    .enableDimensionRegistration(true)
    .register();

SpaceStation.setRecipe(
    [
        <ore:blockIron>,
        <ore:blockGold>,
        <minecraft:diamond_block>,
        <minecraft:obsidian>
    ],
    [64, 32, 16, 64]
);
```

## NASA 工作台配方

### 添加配方

```zenscript
NASAWorkbench.addRecipe(
    String id,
    IItemStack[] inputs,
    IItemStack output,
    int width,
    int height,
    int time,
    int energy
);
```

示例：

```zenscript
import mods.ad_astra.NASAWorkbench;

NASAWorkbench.addRecipe(
    "example:compressed_rocket_part",
    [
        <minecraft:iron_ingot>, <minecraft:iron_ingot>,
        <minecraft:iron_ingot>, <minecraft:iron_ingot>
    ],
    <ad_astra:iron_plate>,
    2,
    2,
    100,
    5
);
```

说明：

- `id` 不能为空；再次添加相同 ID 会替换该配方。
- `inputs` 不能为空；空槽可写 `null`。
- `output` 不能为空。
- `width`、`height`、`time` 最低会按 `1` 处理。
- `energy` 最低会按 `0` 处理。

14 槽火箭配方示例：

```zenscript
NASAWorkbench.addRecipe(
    "example:tier_5_rocket",
    [
        <ad_astra:rocket_nose_cone>,
        <ad_astra:etrium_block>,
        <ad_astra:etrium_block>,
        <ad_astra:etrium_block>,
        <ad_astra:etrium_block>,
        <ad_astra:etrium_block>,
        <minecraft:beacon>,
        <ad_astra:rocket_fin>,
        <ad_astra:calorite_tank>,
        <ad_astra:calorite_tank>,
        <ad_astra:rocket_fin>,
        <ad_astra:rocket_fin>,
        <ad_astra:calorite_engine>,
        <ad_astra:rocket_fin>
    ],
    <ad_astra:tier_5_rocket>,
    3,
    5,
    200,
    10
);
```

### 删除配方

按 ID 删除：

```zenscript
NASAWorkbench.removeRecipe("example:compressed_rocket_part");
```

按输出物品删除所有匹配配方：

```zenscript
NASAWorkbench.removeByOutput(<ad_astra:tier_1_rocket>);
```

## 火箭燃料

接口接收的是 Forge 流体注册名称字符串，不是 `ILiquidStack`。

### 添加燃料

```zenscript
RocketFuel.addFuel(String fluidName, int fuelTier);
```

示例：

```zenscript
import mods.ad_astra.RocketFuel;

RocketFuel.addFuel("lava", 7);
RocketFuel.addFuel("fuel", 1);
RocketFuel.addFuel("cryo_fuel", 2);
```

要求：

- 流体名称必须已经在 Forge 流体注册表中存在。
- `fuelTier` 必须大于 0。
- 高等级燃料可以用于较低等级火箭；低等级燃料不能驱动更高等级火箭。

可使用 CraftTweaker 的 `/ct liquids` 等命令查看当前实例中的流体名称。

### 删除燃料

```zenscript
RocketFuel.removeFuel("lava");
```

## 行星火箭等级

此接口按数字维度 ID 设置，不按行星名称设置。

### 设置覆盖

```zenscript
PlanetTiers.setPlanetTier(int dimensionId, int tier);
```

示例：

```zenscript
import mods.ad_astra.PlanetTiers;

// 维度 -28 需要至少 2 阶火箭。
PlanetTiers.setPlanetTier(-28, 2);

// 0 表示不限制火箭等级。
PlanetTiers.setPlanetTier(130, 0);
```

### 删除覆盖

```zenscript
PlanetTiers.removePlanetTier(-28);
```

对于 `external_dimensions.cfg` 中的外部维度，配置文件本身已经包含最低火箭等级。通常直接修改配置更清晰；CRT 覆盖适合整合包脚本统一调整。

## 空间站配方

空间站接口接受 `IIngredient[]`，因此既支持物品，也支持矿物词典。

### 设置或替换

以下三个方法效果相同，都会设置或替换全局空间站配方：

```zenscript
SpaceStation.setRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.replaceRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.addRecipe(IIngredient[] ingredients, int[] counts);
```

示例：

```zenscript
import mods.ad_astra.SpaceStation;

SpaceStation.setRecipe(
    [
        <ore:blockIron>,
        <minecraft:glass>,
        <minecraft:glowstone>
    ],
    [32, 64, 16]
);
```

也可以直接使用物品堆数量，由接口读取每个 ingredient 的数量：

```zenscript
SpaceStation.setRecipe(
    [
        <ore:blockIron> * 32,
        <minecraft:glass> * 64,
        <minecraft:glowstone> * 16
    ]
);
```

规则：

- 不写命名空间时默认使用 `ad_astra`。
- 材料数组不能为空。
- 使用 `counts` 时，两个数组长度必须一致。
- 每个数量必须大于 0。
- 配方不绑定行星或维度，所有玩家使用同一个空间站配方。

### 删除配方

删除全局空间站配方：

```zenscript
SpaceStation.removeRecipe();
```

按配方 ID 删除：

```zenscript
SpaceStation.removeRecipeById("ad_astra:recipe_space_station");
```

## 排错

### 脚本没有加载

检查：

- 是否安装 CraftTweaker 4.1.20。
- 文件扩展名是否为 `.zs`。
- 文件是否放在实例的 `scripts/`，开发环境是否放在 `run/client/scripts/`。
- 是否写了 `#loader crafttweaker`。
- `crafttweaker.log` 和 `latest.log` 中是否有 ZenScript 错误。

### 自定义行星出现在星图但无法进入

检查：

- 是否调用 `.enableDimensionRegistration(true)`。
- 行星 ID 和行星维度 ID 是否冲突。
自定义行星只注册表面维度；空间站维度固定为全局 `ad_astra:space_station`。
- 脚本是否在维度注册阶段之前正常加载。

### 游戏提示维度 ID 已注册

换一组空闲的行星维度 ID；空间站使用固定的 `ad_astra:space_station` 维度，不需要为行星分配额外维度 ID。

不要用 `CustomPlanets.create()` 接管其他 Mod 已注册的维度；这类目标应写入 `external_dimensions.cfg`。

### 矿物不生成

检查 `.addOre()` 的替换方块是否与 `.stone()` 一致，并使用新生成区块测试。已经生成的区块不会自动重新生成矿物。

### 燃料脚本无效

`RocketFuel.addFuel()` 需要流体注册名称字符串：

```zenscript
RocketFuel.addFuel("lava", 7);
```

不要传：

```zenscript
RocketFuel.addFuel(<liquid:lava> * 1000, 7);
```

### 行星等级脚本无效

使用数字维度 ID：

```zenscript
PlanetTiers.setPlanetTier(-28, 2);
```

不要传行星名称：

```zenscript
PlanetTiers.setPlanetTier("moon", 2);
```

## API 快速参考

```zenscript
// Custom planets
CustomPlanets.create(String id, int dimensionId);
CustomPlanets.getRegisteredCount();
CustomPlanets.hasPlanet(String id);

// Custom planet builder
.name(String name)
.displayName(String displayName)
.saveFolder(String saveFolder)
.biome(String biomeId)
.surface(IBlock block)
.surface(IItemStack stack)
.stone(IBlock block)
.stone(IItemStack stack)
.icon(IItemStack stack)
.iconBlock(IBlock block)
.skyLight(boolean hasSkyLight)
.canRespawn(boolean canRespawn)
.environment(boolean oxygen, int temperature, double gravity, int solarPower)
.tier(int tier)
.dayLength(int dayLength)
.colors(double fogR, double fogG, double fogB, double skyR, double skyG, double skyB)
.addOre(IBlock ore, IBlock replace, int veinSize, int countPerChunk, int minY, int maxY)
.addOre(IItemStack ore, IItemStack replace, int veinSize, int countPerChunk, int minY, int maxY)
.enableDimensionRegistration(boolean enabled)
.register()

// NASA Workbench
NASAWorkbench.addRecipe(String id, IItemStack[] inputs, IItemStack output, int width, int height, int time, int energy);
NASAWorkbench.removeRecipe(String id);
NASAWorkbench.removeByOutput(IItemStack output);

// Rocket fuel
RocketFuel.addFuel(String fluidName, int fuelTier);
RocketFuel.removeFuel(String fluidName);

// Planet tier overrides
PlanetTiers.setPlanetTier(int dimensionId, int tier);
PlanetTiers.removePlanetTier(int dimensionId);

// Space stations
SpaceStation.setRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.setRecipe(IIngredient[] ingredients);
SpaceStation.replaceRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.replaceRecipe(IIngredient[] ingredients);
SpaceStation.addRecipe(IIngredient[] ingredients, int[] counts);
SpaceStation.addRecipe(IIngredient[] ingredients);
SpaceStation.removeRecipe();
SpaceStation.removeRecipeById(String id);
```

> 当前 Java 接口中没有公开 `addFluidLake` ZenScript 方法。脚本中请不要调用该方法。
