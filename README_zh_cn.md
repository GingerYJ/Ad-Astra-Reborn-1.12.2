# Ad Astra Reborn 1.0.0

Ad Astra Reborn 是面向 Minecraft 1.12.2 / Cleanroom 兼容环境的太空探索模组。当前 **1.0.0** 版本已经从单纯移植扩展为一个较完整的太空探索整合版本：包含多等级火箭、大量行星与轨道、空间站、行星专属资源、机器生产线、HEI/JEI 信息显示、CraftTweaker 脚本接口，以及面向整合包服主的多文件配置系统。

[English README](README.md)

## 核心内容概览

### 火箭与星图旅行

- 多等级火箭、发射台、降落舱与漫游车玩法。
- 火箭升空后的目的地选择 UI 已重写为星图风格：支持动态行星、轨道显示、滚轮缩放、左键拖动、行星名称开关。
- 每个行星/轨道需要的最低火箭等级可以通过配置文件修改。
- 对火箭按 HEI/JEI 用处键可查看“火箭目的地”页面，显示当前火箭可达/不可达的行星。
- 支持通过配置文件新增额外火箭，不需要写 Java 代码。

### 内置行星与轨道

当前版本包含原有 Ad Astra 风格目的地和大量新增天体：

- 月球、月球轨道
- 火星、火星轨道
- 水星、水星轨道
- 金星、金星轨道
- 霜原星、霜原星轨道
- 可选下界轨道、末地轨道
- 谷神星、谷神星轨道
- 冥王星、冥王星轨道
- 妊神星、妊神星轨道
- 柯伊伯带
- 木星轨道
- 木卫一、木卫一轨道
- 木卫二、木卫二轨道
- 木卫三、木卫三轨道
- 木卫四、木卫四轨道
- 土卫二、土卫二轨道
- 土卫六、土卫六轨道
- 米兰达、米兰达轨道
- 海卫一、海卫一轨道
- 火卫一、火卫一轨道
- 巴纳德 C、巴纳德 C 轨道
- 巴纳德 C1、巴纳德 C1 轨道
- 天仓五 F、天仓五 F 轨道
- 比邻星 b、比邻星 b 轨道

每个目的地可以拥有独立维度 ID、重力、进入火箭等级、世界生成、敌对生物生成和空间站规则。

### 行星资源与世界生成

- 增加多种行星地表方块、石头、风化层、冰壳、间歇泉、水晶和装饰方块。
- 多个行星拥有专属矿物资源链。
- 矿石挖掘掉落对应粗矿。
- 矿石和粗矿均补齐熔炼为锭的配方。
- 锭与金属块之间补齐 9 合 1 / 1 拆 9 配方。
- HEI/JEI 中可以查看资源来源，例如查询某种行星石头或矿物时能看到它来自哪个星球。
- 每个行星的矿脉配置已拆分到独立分类，避免新旧行星配置混杂。
- 支持通过配置文件为指定星球追加矿物方块、箱子、装饰方块等世界生成规则。

### 机器与生产线

已接入或重建的机器/系统包括：

- NASA 工作台
- 压缩机
- 燃油精炼机
- 氧气装载机
- 氧气分配器
- 重力调节器
- 充能器
- 冷冻机
- 燃煤发电机
- 太阳能板
- 水泵
- 高炉 / Etrionic Blast Furnace
- 空间站建造配方
- HEI/JEI 机器配方与行星信息显示

### 太空环境系统

- 氧气系统
- 温度系统
- 不同维度重力倍率
- 不同维度太阳能倍率
- 行星敌对生物生成控制
- 行星维度专属存档目录支持，可把 Ad Astra 维度集中存放到世界存档内的专用文件夹

## 配置文件

配置文件生成目录：

```text
config/ad_astra/
```

当前版本已将原本过大的单文件配置拆分为多个文件：

- `core.cfg`：通用玩法开关和基础设置
- `client.cfg`：客户端显示、声音和 UI 设置
- `machines.cfg`：机器速度、耗能、配方行为
- `dimensions.cfg`：维度 ID、维度启用、重力倍率、行星火箭等级
- `mobs.cfg`：每个行星的生物生成配置
- `worldgen.cfg`：结构、矿脉、自定义方块/矿物生成、每行星矿脉参数
- `debug.cfg`：调试开关
- `rockets.cfg`：配置驱动的额外火箭

多数配置项带有中文注释，方便整合包作者和服务器服主直接修改。

## 通过配置文件新增火箭

额外火箭配置文件：

```text
config/ad_astra/rockets.cfg
```

额外火箭贴图文件夹：

```text
config/ad_astra/rocket_png/
```

首次启动时会自动生成一个默认 7 阶火箭贴图模板，方便直接改色：

```text
config/ad_astra/rocket_png/custom_tier_8_rocket.png
```

示例：

```cfg
S:customRockets <
    custom_tier_8_rocket|八阶火箭|8|10000|7|custom_tier_8_rocket.png
 >
```

字段格式：

```text
注册ID|显示名称|火箭等级|燃料容量mB|复用模型等级|贴图
```

说明：

- `注册ID` 必须唯一，不能与已有火箭冲突。
- `显示名称` 会用于物品名称和火箭库存 UI 标题。
- `火箭等级` 会参与行星进入判断和燃料等级判断。
- `燃料容量mB` 控制火箭可装载燃料量。
- `复用模型等级` 可复用 1~7 阶火箭模型。
- `贴图` 可填写 `rocket_png` 文件夹内 PNG 文件名，也可填写资源包路径。

## CraftTweaker / ZenScript 支持

可用导入：

```zenscript
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
import mods.ad_astra.CustomPlanets;
```

### 火箭燃料

火箭燃料等级应通过脚本控制。例如把岩浆设置为 7 级燃料：

```zenscript
RocketFuel.addFuel("lava", 7);
<minecraft:lava_bucket>.addTooltip("燃料等级：7");
```

当前版本不再在 mod 源码中默认写死岩浆燃料等级，是否启用岩浆燃料完全由脚本决定。

### NASA 工作台配方

```zenscript
NASAWorkbench.addRecipe(String id, IItemStack[] inputs, IItemStack output, int width, int height, int time, int energy);
NASAWorkbench.removeRecipe(String id);
NASAWorkbench.removeByOutput(IItemStack output);
```

### 行星最低火箭等级

```zenscript
PlanetTiers.setPlanetTier(int dimensionId, int tier);
PlanetTiers.removePlanetTier(int dimensionId);
```

也可以优先使用 `dimensions.cfg` 中的行星火箭等级配置。

### 空间站配方

```zenscript
SpaceStation.setRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.replaceRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.addRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.removeRecipe(String orbit);
SpaceStation.removeRecipeById(String id);
```

### 自定义行星

```zenscript
CustomPlanets.create(String id, int dimensionId)
    .displayName("玄武岩卫星")
    .tier(2)
    .biome("minecraft:desert")
    .surface(<block:minecraft:stone>)
    .stone(<block:minecraft:stone>)
    .icon(<minecraft:obsidian>)
    .environment(false, -40, 0.42, 18)
    .addOre(<block:minecraft:iron_ore>, <block:minecraft:stone>, 6, 6, 4, 48)
    .addFluidLake(<liquid:lava> * 1000, 1, 8, 32)
    .register();
```

开发环境脚本目录：

```text
run/client/scripts/
```

## 运行需求

- Minecraft 1.12.2
- Cleanroom Loader / Forge 兼容 1.12.2 运行环境
- Patchouli：运行时必需
- HEI/JEI：推荐安装，用于配方和信息显示
- CraftTweaker：可选；使用脚本、自定义行星、自定义燃料时需要安装

## 构建与运行

```powershell
.\gradlew.bat compileJava processResources
.\gradlew.bat build
.\gradlew.bat runClient
```

生成的 jar 位于：

```text
build/libs
```

## 鸣谢

维护者：GingerYJ

原 Ad Astra 内容与资源归 Terrarium 和 Ad Astra 贡献者所有。本项目在 Minecraft 1.12.2 环境下进行了移植、适配和扩展。
