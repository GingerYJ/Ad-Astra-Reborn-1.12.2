#loader crafttweaker

import mods.ad_astra.NASAWorkbench;
import mods.ad_astra.RocketFuel;
import mods.ad_astra.SpaceStation;

// 燃料等级：高等级燃料可以驱动低等级火箭，低等级燃料不能驱动高等级火箭。
RocketFuel.addFuel("lava", 3);

<ad_astra:fuel_bucket>.addTooltip("燃料等级：1");
<ad_astra:cryo_fuel_bucket>.addTooltip("燃料等级：2");
<minecraft:lava_bucket>.addTooltip("燃料等级：3");

// 下界轨道空间站建造材料。需要在配置中启用“将下界加入行星”后，选择界面才会出现下界空间站入口。
SpaceStation.setRecipe("nether_orbit",
    [
        <minecraft:obsidian>,
        <minecraft:glowstone>,
        <minecraft:quartz_block>,
        <minecraft:iron_block>
    ],
    [64, 32, 32, 16]);

// 五阶火箭 NASA 工作台配方：沿用 14 槽火箭布局，并加入信标。
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
