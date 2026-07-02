# Ad Astra Reborn

Ad Astra Reborn 是面向 Minecraft 1.12.2 / Cleanroom 的 Ad Astra 移植项目。项目目标是把 Ad Astra 1.20.x 的太空探索内容移植回 1.12.2 生态，并尽量直接复用原源码中的方块、物品、模型、GUI、载具和行星资产。

[English README](README.md)

## 当前状态

这个仓库仍处于移植和玩法补全阶段，不是最终发布版。火箭、发射台、机器、空间站、行星旅行、环境系统和 CraftTweaker 接口已经接入，但不少细节仍需要在实际游戏和整合包环境中继续验证。

作者与维护者：GingerYJ

鸣谢：原 Ad Astra 内容来自 Terrarium 与 Ad Astra 贡献者。

## 已实现内容

- 多等级火箭、发射台、降落舱和漫游车玩法。
- 月球、火星、水星、金星、冰川星的行星旅行，并可配置把下界和末地加入火箭目的地。
- 轨道与空间站玩法，空间站建造材料可配置。
- 氧气、温度、重力等太空环境系统。
- NASA 工作台、压缩机、燃油精炼机、氧气装载机、氧气分配器、重力调节器、充能器、冷冻机、燃煤发电机、太阳能板、水泵、高炉等机器与 GUI。
- HEI 中的机器配方显示。
- Patchouli 指南书集成。
- CraftTweaker 接口：NASA 工作台配方、火箭燃料等级、行星最低火箭等级、空间站配方、自定义行星定义。

## 需求

- Minecraft 1.12.2，Cleanroom Loader 或兼容 Forge 1.12.2 的运行环境。
- Gradle 配置的 Java 工具链。当前项目使用 Java 25 工具链，源码/目标兼容级别为 Java 21。
- Patchouli 是运行时必需依赖。
- HEI 用于配方显示。
- CraftTweaker 是可选运行时依赖；如果要使用脚本和 ZenScript 接口则需要安装。

## 构建和运行

```powershell
.\gradlew.bat compileJava
.\gradlew.bat build
.\gradlew.bat runClient
```

构建出的模组 jar 位于 `build/libs`。

默认开发用 CraftTweaker 脚本位置：

```text
run/client/scripts/ad_astra_defaults.zs
```

## 配置文件

配置文件会生成到单独文件夹：

```text
config/ad_astra/ad_astra.cfg
```

主要可配置项包括氧气、温度、重力、机器速度和耗能倍率、行星维度开关、下界/末地火箭旅行、世界生成，以及行星维度中每类实体的数量上限。

## CraftTweaker 接口

ZenScript 中可使用以下 import：

```zenscript
import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.PlanetTiers;
import mods.ad_astra.SpaceStation;
import mods.ad_astra.CustomPlanets;
```

### NASA 工作台

```zenscript
NASAWorkbench.addRecipe(String id, IItemStack[] inputs, IItemStack output, int width, int height, int time, int energy);
NASAWorkbench.removeRecipe(String id);
NASAWorkbench.removeByOutput(IItemStack output);
```

示例：

```zenscript
NASAWorkbench.addRecipe("ad_astra:tier_5_rocket_from_crt",
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
    10);
```

### 火箭燃料

```zenscript
RocketFuel.addFuel(String fluidName, int fuelTier);
RocketFuel.removeFuel(String fluidName);
```

高等级燃料可以驱动低等级火箭，低等级燃料不能驱动高等级火箭。

示例：

```zenscript
RocketFuel.addFuel("lava", 3);
```

### 行星最低火箭等级

```zenscript
PlanetTiers.setPlanetTier(int dimensionId, int tier);
PlanetTiers.removePlanetTier(int dimensionId);
```

示例：

```zenscript
PlanetTiers.setPlanetTier(1201, 1); // 月球
```

### 空间站配方

```zenscript
SpaceStation.setRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.setRecipe(String orbit, IIngredient[] ingredients);
SpaceStation.replaceRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.replaceRecipe(String orbit, IIngredient[] ingredients);
SpaceStation.addRecipe(String orbit, IIngredient[] ingredients, int[] counts);
SpaceStation.addRecipe(String orbit, IIngredient[] ingredients);
SpaceStation.removeRecipe(String orbit);
SpaceStation.removeRecipeById(String id);
```

示例：

```zenscript
SpaceStation.setRecipe("nether_orbit",
    [
        <minecraft:obsidian>,
        <minecraft:glowstone>,
        <minecraft:quartz_block>,
        <minecraft:iron_block>
    ],
    [64, 32, 32, 16]);
```

### 自定义行星

```zenscript
CustomPlanets.create(String id, int dimensionId);
CustomPlanets.getRegisteredCount();
CustomPlanets.hasPlanet(String id);
```

`CustomPlanets.create` 返回的 builder 支持：

```zenscript
.name(String name)
.displayName(String displayName)
.saveFolder(String saveFolder)
.biome(String biomeId)
.surface(IBlock block)
.stone(IBlock block)
.icon(IItemStack stack)
.iconBlock(IBlock block)
.skyLight(boolean hasSkyLight)
.canRespawn(boolean canRespawn)
.environment(boolean oxygen, int temperature, double gravity, int solarPower)
.tier(int tier)
.dayLength(int dayLength)
.colors(double fogRed, double fogGreen, double fogBlue, double skyRed, double skyGreen, double skyBlue)
.addOre(IBlock oreBlock, IBlock replaceBlock, int veinSize, int countPerChunk, int minY, int maxY)
.addFluidLake(ILiquidStack fluidStack, int countPerChunk, int minY, int maxY)
.addFluidBlock(IBlock fluidBlock, int countPerChunk, int minY, int maxY)
.enableDimensionRegistration(boolean enabled)
.register()
```

模板：

```zenscript
CustomPlanets.create("example:basalt_moon", 1301)
    .displayName("Basalt Moon")
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

更完整的例子见 `docs/custom_planets_crt_template.zs`。
