# Ad Astra Reborn - CraftTweaker 接口文档

## 目录

1. [自定义行星 (Custom Planets)](#自定义行星)
2. [NASA工作台配方 (NASA Workbench Recipes)](#nasa工作台配方)
3. [火箭燃料 (Rocket Fuel)](#火箭燃料)
4. [行星等级配置 (Planet Tier Config)](#行星等级配置)
5. [空间站配方 (Space Station Recipes)](#空间站配方)

---

## 自定义行星

通过 CraftTweaker 脚本，你可以创建全新的可探索行星，每个行星都有独立的地形、矿物、环境和维度。

### 接口类

```zenscript
mods.ad_astra.CustomPlanets
```

### 方法

#### `create(String id, int dimensionId)`

创建一个新的自定义行星定义。

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | String | 行星唯一标识符。格式为 `"命名空间:路径"`，如 `"ad_astra:mineral_world"`。如果省略命名空间，默认使用 `"ad_astra"`。此ID用于内部注册、翻译键和存档文件夹名称。 |
| `dimensionId` | int | 行星维度ID。必须是正整数，且不能为0（0是主世界）。每个自定义行星需要唯一的维度ID，建议从1301开始分配，避免与原版和其他Mod冲突。 |

**返回值：** `CustomPlanetBuilder` - 用于链式配置行星的构建器对象。

---

#### `hasPlanet(String id)`

检查指定ID的自定义行星是否已注册。

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | String | 要检查的行星ID。 |

**返回值：** `boolean` - 如果行星已注册返回 `true`，否则返回 `false`。

---

#### `getRegisteredCount()`

获取已注册的自定义行星数量。

**返回值：** `int` - 已注册的自定义行星总数。

---

### CustomPlanetBuilder 配置方法

调用 `create()` 后返回的构建器对象，支持链式调用以下方法：

#### `.name(String planetName)`

设置行星的内部名称。此名称用于存档文件夹命名和内部标识。

| 参数 | 类型 | 说明 |
|------|------|------|
| `planetName` | String | 行星的内部名称。只能包含小写字母、数字和下划线。如 `"mineral_world"`。如果未设置，默认使用ID的路径部分。 |

---

#### `.displayName(String displayName)`

设置行星的显示名称，用于UI界面（如火箭选择界面）。

| 参数 | 类型 | 说明 |
|------|------|------|
| `displayName` | String | 玩家在UI中看到的行星名称。如 `"Mineral World"`。支持通过语言文件进行本地化翻译。 |

---

#### `.saveFolder(String saveFolder)`

设置维度存档文件夹名称。

| 参数 | 类型 | 说明 |
|------|------|------|
| `saveFolder` | String | 世界存档中该维度的文件夹名称。如 `"DIM_MINERAL_WORLD"`。如果未设置，自动生成格式为 `DIM_AD_ASTRA_CUSTOM_命名空间_路径` 的名称。 |

---

#### `.biome(String biomeId)`

设置行星的地形生成生物群系。

| 参数 | 类型 | 说明 |
|------|------|------|
| `biomeId` | String | 生物群系ID。格式为 `"命名空间:路径"`，如 `"minecraft:desert"`（沙漠）、`"minecraft:plains"`（平原）、`"minecraft:ice_flats"`（冰原）。决定地表植被、草块颜色、降水等环境特征。 |

---

#### `.surface(IBlock block)` / `.surface(IItemStack stack)`

设置行星地表最顶层的方块。

| 参数 | 类型 | 说明 |
|------|------|------|
| `block` | IBlock | 地表方块。如 `<block:minecraft:grass>`（草方块）、`<block:minecraft:sand>`（沙子）、`<block:minecraft:stone>`（石头）。这是玩家站在行星表面时脚下的方块。 |
| `stack` | IItemStack | 同上，通过物品堆形式传入。 |

---

#### `.stone(IBlock block)` / `.stone(IItemStack stack)`

设置行星地下的主要岩石方块。

| 参数 | 类型 | 说明 |
|------|------|------|
| `block` | IBlock | 地下岩石方块。如 `<block:minecraft:stone>`（石头）、`<block:minecraft:netherrack>`（地狱岩）。此方块构成地表以下的大部分地形。 |
| `stack` | IItemStack | 同上，通过物品堆形式传入。 |

---

#### `.icon(IItemStack stack)`

设置行星在火箭选择UI中显示的图标。

| 参数 | 类型 | 说明 |
|------|------|------|
| `stack` | IItemStack | 图标物品。如 `<minecraft:diamond_block>`（钻石块）、`<minecraft:obsidian>`（黑曜石）。建议使用有代表性的方块物品。 |

---

#### `.iconBlock(IBlock block)`

通过方块形式设置行星图标。

| 参数 | 类型 | 说明 |
|------|------|------|
| `block` | IBlock | 图标方块。如 `<block:minecraft:diamond_block>`。 |

---

#### `.skyLight(boolean hasSkyLight)`

设置行星是否有天空光照。

| 参数 | 类型 | 说明 |
|------|------|------|
| `hasSkyLight` | boolean | `true` 表示有天空光照（类似主世界），`false` 表示无天空光照（类似下界）。影响怪物生成、作物生长等。 |

---

#### `.canRespawn(boolean canRespawn)`

设置玩家是否可以在该行星重生。

| 参数 | 类型 | 说明 |
|------|------|------|
| `canRespawn` | boolean | `true` 允许玩家在此行星设置重生点并死亡后在此重生，`false` 则死亡后返回主世界重生。 |

---

#### `.environment(boolean oxygen, int temperature, double gravity, int solarPower)`

设置行星的环境参数。

| 参数 | 类型 | 说明 |
|------|------|------|
| `oxygen` | boolean | 行星是否有氧气。`false` 表示需要氧气装备，否则玩家会受到缺氧伤害。 |
| `temperature` | int | 温度值（摄氏度）。如 `-270` 表示极寒（需要温度调节），`80` 表示极热（需要冷却），`15` 表示适宜。影响玩家是否需要温度防护装备。 |
| `gravity` | double | 重力倍数。如 `1.0` 等于主世界重力，`0.5` 表示一半重力（跳得更高，下落更慢），`1.5` 表示更强重力。 |
| `solarPower` | int | 太阳能发电效率。如 `16` 表示标准效率，数值越高太阳能板发电量越大。 |

---

#### `.tier(int tier)`

设置到达该行星所需的最低火箭等级。

| 参数 | 类型 | 说明 |
|------|------|------|
| `tier` | int | 火箭等级要求。如 `1` 表示1级火箭即可到达，`3` 需要3级或更高级火箭。玩家驾驶低于此等级的火箭时无法选择该行星作为目的地。 |

---

#### `.dayLength(int dayLength)`

设置行星的一天长度（以游戏刻为单位）。

| 参数 | 类型 | 说明 |
|------|------|------|
| `dayLength` | int | 一天的游戏刻数。主世界为 `24000`（20分钟）。如 `12000` 表示10分钟一天，`48000` 表示40分钟一天。 |

---

#### `.colors(double fogRed, double fogGreen, double fogBlue, double skyRed, double skyGreen, double skyBlue)`

设置行星的雾气和天空颜色。

| 参数 | 类型 | 说明 |
|------|------|------|
| `fogRed` | double | 雾气颜色红色分量，范围 `0.0` ~ `1.0`。 |
| `fogGreen` | double | 雾气颜色绿色分量，范围 `0.0` ~ `1.0`。 |
| `fogBlue` | double | 雾气颜色蓝色分量，范围 `0.0` ~ `1.0`。 |
| `skyRed` | double | 天空颜色红色分量，范围 `0.0` ~ `1.0`。 |
| `skyGreen` | double | 天空颜色绿色分量，范围 `0.0` ~ `1.0`。 |
| `skyBlue` | double | 天空颜色蓝色分量，范围 `0.0` ~ `1.0`。 |

---

#### `.addOre(IBlock oreBlock, IBlock replaceBlock, int veinSize, int countPerChunk, int minY, int maxY)` / `.addOre(IItemStack oreStack, IItemStack replaceStack, int veinSize, int countPerChunk, int minY, int maxY)`

添加矿物生成到行星。

| 参数 | 类型 | 说明 |
|------|------|------|
| `oreBlock` / `oreStack` | IBlock / IItemStack | 要生成的矿物方块。如 `<block:minecraft:iron_ore>`（铁矿石）、`<block:minecraft:diamond_ore>`（钻石矿石）。 |
| `replaceBlock` / `replaceStack` | IBlock / IItemStack | 被替换的方块。通常是行星的石头方块，如 `<block:minecraft:stone>`。 |
| `veinSize` | int | 单个矿脉的最大方块数。如 `8` 表示每个矿脉最多8个方块。数值越大矿脉越密集。 |
| `countPerChunk` | int | 每个区块尝试生成矿脉的次数。如 `20` 表示每个区块尝试生成20次。数值越高矿物越丰富。 |
| `minY` | int | 矿脉生成的最低Y坐标。范围 `0` ~ `255`。 |
| `maxY` | int | 矿脉生成的最高Y坐标。范围 `0` ~ `255`，必须大于等于 `minY`。 |

---

#### `.enableDimensionRegistration(boolean enabled)`

设置是否在CraftTweaker阶段注册维度（已弃用，保留兼容性）。

| 参数 | 类型 | 说明 |
|------|------|------|
| `enabled` | boolean | 当前版本此设置不影响实际行为，维度在游戏初始化时自动注册。建议始终设置为 `true` 或省略。 |

---

#### `.register()`

完成配置并注册行星。必须在构建器链的最后调用。

**注意：** 调用此方法后，行星定义被存储并在游戏初始化时注册。此方法无返回值，会结束构建器链。

---

### 完整示例

```zenscript
#loader crafttweaker

import mods.ad_astra.CustomPlanets;
import mods.ad_astra.SpaceStation;

// ========== 自定义行星：矿物世界 ==========
// 一个充满各种矿物资源的行星，地表为铁块，地下为铁矿石

var ironBlock = <minecraft:iron_block>;
var ironOre = <minecraft:iron_ore>;
var goldOre = <minecraft:gold_ore>;
var diamondOre = <minecraft:diamond_ore>;
var redstoneOre = <minecraft:redstone_ore>;
var coalOre = <minecraft:coal_ore>;
var lapisOre = <minecraft:lapis_ore>;
var emeraldOre = <minecraft:emerald_ore>;
var goldBlock = <minecraft:gold_block>;
var diamondBlock = <minecraft:diamond_block>;
var stone = <minecraft:stone>;

CustomPlanets.create("ad_astra:mineral_world", 1301)
    // 内部名称，用于存档和标识
    .name("mineral_world")
    // 在火箭选择UI中显示的名称
    .displayName("Mineral World")
    // 需要3级火箭才能到达
    .tier(3)
    // 使用沙漠生物群系（干燥、无降水）
    .biome("minecraft:desert")
    // 地表是铁块
    .surface(ironBlock)
    // 地下是铁矿石
    .stone(ironOre)
    // 在UI中显示钻石块图标
    .icon(<minecraft:diamond_block>)
    // 无氧气、温度80°C（极热）、重力1.5倍、太阳能32
    .environment(false, 80, 1.5, 32)
    // 雾气为棕黄色，天空为橙黄色（沙漠行星氛围）
    .colors(0.55, 0.35, 0.15, 0.75, 0.55, 0.25)
    // 添加各种矿物生成
    .addOre(ironOre, stone, 12, 25, 0, 128)      // 铁矿石，丰富
    .addOre(goldOre, stone, 6, 12, 0, 64)        // 金矿石
    .addOre(diamondOre, stone, 4, 8, 0, 32)       // 钻石矿石
    .addOre(redstoneOre, stone, 8, 15, 0, 48)     // 红石矿石
    .addOre(coalOre, stone, 10, 20, 32, 128)      // 煤炭矿石
    .addOre(lapisOre, stone, 5, 10, 0, 48)        // 青金石矿石
    .addOre(emeraldOre, stone, 3, 4, 0, 32)        // 绿宝石矿石
    .addOre(ironBlock, stone, 4, 2, 0, 24)         // 铁块（稀有）
    .addOre(goldBlock, stone, 2, 1, 0, 16)         // 金块（极稀有）
    .addOre(diamondBlock, stone, 1, 1, 0, 12)      // 钻石块（极稀有）
    // 注册行星
    .register();

// ========== 为该行星添加空间站配方 ==========
// 玩家可以在该行星轨道上建造空间站

SpaceStation.setRecipe("mineral_world_orbit",
    [
        <minecraft:iron_block>,      // 需要64个铁块
        <minecraft:gold_block>,      // 需要32个金块
        <minecraft:diamond_block>,     // 需要16个钻石块
        <minecraft:obsidian>,          // 需要64个黑曜石
        <minecraft:glowstone>          // 需要32个萤石
    ],
    [64, 32, 16, 64, 32]);

print("Ad Astra: Mineral World custom planet and space station loaded.");
```

---

## NASA工作台配方

通过 CraftTweaker 添加或修改 NASA 工作台的合成配方。NASA 工作台用于制作火箭和太空装备。

**NASA 工作台槽位布局：**
- 共 **14个输入槽位**（索引 0-13），排列为不规则形状
- 1个输出槽位（索引 14）
- 配方通过 width 和 height 定义在输入区域中的形状
- 当输入物品数超过 9 个时，使用精确槽位匹配（每个槽位必须对应正确物品）
- 当输入物品数不超过 9 个时，使用3x3网格匹配（支持在区域内平移和镜像）

### 接口类

`zenscript
mods.ad_astra.NASAWorkbench
`

### 方法

#### addRecipe(String id, IItemStack[] inputs, IItemStack output, int width, int height, int time, int energy)

添加一个新的 NASA 工作台配方。

| 参数 | 类型 | 说明 |
|------|------|------|
| id | String | 配方唯一名称。用于标识和后续删除。建议使用 modid:recipe_name 格式。 |
| inputs | IItemStack[] | 输入物品数组。按行优先顺序排列，长度为 width * height。空位使用 null 表示。 |
| output | IItemStack | 合成产物。如 <ad_astra:rocket_t1>（1级火箭）。 |
| width | int | 配方在输入区域中的宽度（1-3）。 |
| height | int | 配方在输入区域中的高度（1-3）。 |
| time | int | 合成所需时间（刻）。20刻 = 1秒。 |
| energy | int | 每刻消耗的能量。 |

**示例1 - 标准3x3配方（9个输入）：**
`zenscript
import mods.ad_astra.NASAWorkbench;

// 添加一个3x3配方（width=3, height=3）
NASAWorkbench.addRecipe(ad_astra:rocket_t1_custom,
    [
        <minecraft:iron_block>, <minecraft:iron_block>, <minecraft:iron_block>,
        <minecraft:iron_block>, null,                    <minecraft:iron_block>,
        <minecraft:iron_block>, <minecraft:iron_block>, <minecraft:iron_block>
    ],
    <ad_astra:rocket_t1>,
    3, 3, 200, 10
);
`

**示例2 - 复杂配方（14个输入，精确槽位）：**
`zenscript
// 添加一个14输入的火箭配方
// 注意：当 inputs 数组长度超过9时，使用精确槽位匹配
NASAWorkbench.addRecipe(ad_astra:rocket_t5_custom,
    [
        <minecraft:diamond_block>, <minecraft:beacon>,      <minecraft:diamond_block>,
        <minecraft:iron_block>,    <ad_astra:rocket_t4>,    <minecraft:iron_block>,
        <minecraft:obsidian>,      <minecraft:obsidian>,    <minecraft:obsidian>,
        <minecraft:glowstone>,     <minecraft:redstone_block>, <minecraft:glowstone>,
        <minecraft:iron_block>,    <minecraft:iron_block>
    ],
    <ad_astra:rocket_t5>,
    3, 3, 400, 20
);
`

**示例3 - 小型配方（2x2）：**
`zenscript
// 添加一个2x2配方
NASAWorkbench.addRecipe(ad_astra:small_part,
    [
        <minecraft:iron_ingot>, <minecraft:iron_ingot>,
        <minecraft:iron_ingot>, <minecraft:iron_ingot>
    ],
    <ad_astra:iron_plate>,
    2, 2, 100, 5
);
`

---

#### removeRecipe(String id)

删除指定的 NASA 工作台配方。

| 参数 | 类型 | 说明 |
|------|------|------|
| id | String | 要删除的配方ID。 |

**示例：**
`zenscript
NASAWorkbench.removeRecipe(ad_astra:rocket_t1);
`

---

#### removeByOutput(IItemStack output)

删除所有产出指定物品的 NASA 工作台配方。

| 参数 | 类型 | 说明 |
|------|------|------|
| output | IItemStack | 要移除的产出物品。 |

**示例：**
`zenscript
NASAWorkbench.removeByOutput(<ad_astra:rocket_t1>);
`

## 火箭燃料

通过 CraftTweaker 为火箭添加或修改燃料类型。不同等级的火箭需要不同等级的燃料。

### 接口类

```zenscript
mods.ad_astra.RocketFuel
```

### 方法

#### `addFuel(ILiquidStack fuel, int tier)`

添加一种火箭燃料。

| 参数 | 类型 | 说明 |
|------|------|------|
| `fuel` | ILiquidStack | 燃料液体。如 `<liquid:fuel>`、`<liquid:oil>`、`<liquid:lava>`。 |
| `tier` | int | 燃料等级。`1` 为最低级，`7` 为最高级。高等级燃料可以驱动低等级火箭，但低等级燃料不能驱动高等级火箭。 |

**示例：**
```zenscript
import mods.ad_astra.RocketFuel;

// 将熔岩添加为3级燃料
RocketFuel.addFuel(<liquid:lava> * 1000, 3);

// 将原油添加为1级燃料
RocketFuel.addFuel(<liquid:oil> * 1000, 1);
```

---

#### `removeFuel(ILiquidStack fuel)`

移除一种燃料。

| 参数 | 类型 | 说明 |
|------|------|------|
| `fuel` | ILiquidStack | 要移除的燃料液体。 |

---

#### `getFuelTier(ILiquidStack fuel)`

获取指定液体的燃料等级。

| 参数 | 类型 | 说明 |
|------|------|------|
| `fuel` | ILiquidStack | 要查询的液体。 |

**返回值：** `int` - 燃料等级，如果不是燃料则返回 `-1`。

---

## 行星等级配置

通过 CraftTweaker 修改到达特定行星所需的火箭等级。可以覆盖默认配置。

### 接口类

```zenscript
mods.ad_astra.PlanetTiers
```

### 方法

#### `setTier(String planetId, int tier)`

设置到达指定行星所需的火箭等级。

| 参数 | 类型 | 说明 |
|------|------|------|
| `planetId` | String | 行星ID。可以是原版行星 `"moon"`、`"mars"`、`"mercury"`、`"venus"`、`"glacio"`，或自定义行星 `"ad_astra:mineral_world"`。 |
| `tier` | int | 所需的最低火箭等级。 |

**示例：**
```zenscript
import mods.ad_astra.PlanetTiers;

// 将月球改为需要2级火箭（原为1级）
PlanetTiers.setTier("moon", 2);

// 将下界设为需要4级火箭
PlanetTiers.setTier("nether", 4);

// 自定义行星等级
PlanetTiers.setTier("ad_astra:mineral_world", 3);
```

---

#### `getTier(String planetId)`

获取指定行星当前的等级要求。

| 参数 | 类型 | 说明 |
|------|------|------|
| `planetId` | String | 行星ID。 |

**返回值：** `int` - 所需的火箭等级。

---

#### `resetTier(String planetId)`

重置指定行星的等级要求为默认值。

---

## 空间站配方

通过 CraftTweaker 为行星轨道添加空间站建造配方。玩家可以在轨道上建造空间站作为中转基地。

### 接口类

```zenscript
mods.ad_astra.SpaceStation
```

### 方法

#### `setRecipe(String orbitId, IItemStack[] items, int[] counts)`

设置建造空间站所需的材料。

| 参数 | 类型 | 说明 |
|------|------|------|
| `orbitId` | String | 轨道ID。格式为 `"行星名称_orbit"`。对于原版行星使用 `"moon_orbit"`、`"mars_orbit"` 等。对于自定义行星，使用 `"mineral_world_orbit"`（即行星名称 + `"_orbit"`）。 |
| `items` | IItemStack[] | 需要的物品数组。最多5种物品。 |
| `counts` | int[] | 对应物品的数量数组。长度必须与 `items` 相同。 |

**示例：**
```zenscript
import mods.ad_astra.SpaceStation;

// 为月球轨道设置空间站配方
SpaceStation.setRecipe("moon_orbit",
    [
        <minecraft:iron_block>,
        <minecraft:glass>,
        <minecraft:glowstone>
    ],
    [32, 64, 16]);

// 为下界轨道设置空间站配方
SpaceStation.setRecipe("nether_orbit",
    [
        <minecraft:obsidian>,
        <minecraft:iron_block>,
        <minecraft:glowstone>,
        <minecraft:quartz_block>
    ],
    [64, 32, 16, 32]);

// 为自定义行星轨道设置空间站配方
SpaceStation.setRecipe("mineral_world_orbit",
    [
        <minecraft:iron_block>,
        <minecraft:gold_block>,
        <minecraft:diamond_block>,
        <minecraft:obsidian>,
        <minecraft:glowstone>
    ],
    [64, 32, 16, 64, 32]);
```

---

#### `removeRecipe(String orbitId)`

移除指定轨道的空间站配方。

| 参数 | 类型 | 说明 |
|------|------|------|
| `orbitId` | String | 轨道ID。 |

---

#### `hasRecipe(String orbitId)`

检查指定轨道是否有空间站配方。

| 参数 | 类型 | 说明 |
|------|------|------|
| `orbitId` | String | 轨道ID。 |

**返回值：** `boolean` - 如果有配方返回 `true`。

---

## 完整综合示例

```zenscript
#loader crafttweaker

import mods.ad_astra.CustomPlanets;
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;

// ========== 1. 创建自定义行星 ==========
CustomPlanets.create("ad_astra:crystal_world", 1302)
    .name("crystal_world")
    .displayName("Crystal World")
    .tier(4)
    .biome("minecraft:ice_flats")
    .surface(<block:minecraft:diamond_block>)
    .stone(<block:minecraft:stone>)
    .icon(<minecraft:diamond>)
    .environment(false, -100, 0.8, 24)
    .colors(0.1, 0.3, 0.5, 0.2, 0.4, 0.8)
    .addOre(<block:minecraft:diamond_ore>, <block:minecraft:stone>, 8, 30, 0, 64)
    .addOre(<block:minecraft:emerald_ore>, <block:minecraft:stone>, 6, 15, 0, 48)
    .register();

// ========== 2. 添加NASA工作台配方 ==========
NASAWorkbench.addRecipe("ad_astra:rocket_t5_custom",
    <ad_astra:rocket_t5>,
    [
        [<minecraft:diamond_block>, <minecraft:beacon>, <minecraft:diamond_block>],
        [<minecraft:iron_block>, <ad_astra:rocket_t4>, <minecraft:iron_block>],
        [<minecraft:obsidian>, <minecraft:obsidian>, <minecraft:obsidian>]
    ]
);

// ========== 3. 添加火箭燃料 ==========
RocketFuel.addFuel(<liquid:lava> * 1000, 3);

// ========== 4. 修改行星等级要求 ==========
PlanetTiers.setTier("nether", 4);
PlanetTiers.setTier("end", 5);

// ========== 5. 添加空间站配方 ==========
SpaceStation.setRecipe("crystal_world_orbit",
    [
        <minecraft:diamond_block>,
        <minecraft:iron_block>,
        <minecraft:glowstone>
    ],
    [32, 64, 16]);

print("Ad Astra custom content loaded successfully!");
```

---

## 注意事项

1. **维度ID冲突**：确保自定义行星的维度ID不与其他Mod或原版维度冲突。建议使用1300以上的ID。

2. **行星名称**：`name()` 方法设置的名称用于内部存档文件夹命名，只能包含小写字母、数字和下划线。

3. **轨道ID**：空间站配方的 `orbitId` 必须是行星名称 + `"_orbit"`。对于自定义行星 `"ad_astra:mineral_world"`，其轨道ID为 `"mineral_world_orbit"`（不含命名空间前缀）。

4. **燃料等级**：高等级燃料可以驱动低等级火箭，但低等级燃料不能驱动高等级火箭。例如，3级燃料可以给1、2、3级火箭使用，但1级燃料不能给2级火箭使用。

5. **矿物生成**：`addOre()` 的 `replaceBlock` 参数决定了矿物替换哪种方块。确保设置为行星的 `stone()` 方块，否则矿物不会生成。

6. **温度值**：温度值影响玩家是否需要温度调节装备。建议范围：
   - `-270` ~ `-100`：极寒，需要加热
   - `-50` ~ `50`：适宜，无需温度装备
   - `60` ~ `150`：极热，需要冷却

7. **脚本加载顺序**：`#loader crafttweaker` 确保脚本在正确的加载阶段执行。所有 Ad Astra 的 CRT 接口都需要此加载器。

---

*文档版本：1.0*
*适配 Ad Astra Reborn 1.12.2*