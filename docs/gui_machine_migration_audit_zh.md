# GUI/机器移植覆盖面审计

审计范围：`src/main/java` 中当前 GUI、Container、IGuiHandler、网络同步类，以及总目标中的 15 个菜单和 13 类机器。

本报告只做覆盖面审计和任务拆分建议。本次未修改渲染、模型、配方、掉落表或运行逻辑文件。

## 当前已实现的 GUI/Container/网络类

### GUI 入口

- `earth.terrarium.adastra.ModGuiHandler`
  - 已实现 `IGuiHandler`。
  - 服务端：根据 GUI id 和坐标取 `AdAstraMachineTileEntity`，创建 `AdAstraMachineContainer`。
  - 客户端：创建同一个 `AdAstraMachineContainer`，再包进 `AdAstraMachineGui`。
  - 只处理方块机器 GUI；不处理实体载具 GUI。

- `earth.terrarium.adastra.common.blocks.AdAstraMachineGuiHelper`
  - `AdAstraMachineBlock`、`AdAstraAttachedMachineBlock`、`AdAstraEnergizerBlock` 右键时走这里。
  - 只有 `AdAstraMachineContainer.idFor(tile) >= 0` 的机器会打开 GUI。
  - 因此已存在 TileEntity 但没有 `idFor` 映射的机器不会打开 GUI，例如氧气分配器。

- `AdAstraReborn.preInit`
  - 已注册 `NetworkHandler.init()`、`ModTileEntities.register()` 和 `NetworkRegistry.INSTANCE.registerGuiHandler(...)`。

### Container

- `earth.terrarium.adastra.common.container.AdAstraMachineContainer`
  - 当前唯一的机器容器类，继承 `Container`。
  - 已实现机器槽、玩家背包槽、shift-click 合并、`IContainerListener.sendWindowProperty` 字段同步。
  - 当前 `layoutFor/idFor` 覆盖 11 个 GUI id：
    - 燃煤发电机
    - 压缩机
    - 电力高炉
    - 燃料精炼机
    - 氧气装载机
    - 太阳能板
    - 水泵
    - 充能器
    - 冷冻机
    - NASA 工作台
    - 重力调节器
  - 未覆盖氧气分配器、氧气传感器、火箭、月球车、着陆器、星球选择菜单。

### GuiContainer / GuiScreen

- `earth.terrarium.adastra.client.gui.AdAstraMachineGui`
  - 当前唯一的机器 `GuiContainer`。
  - 使用 `textures/gui/container/*.png` 和 `textures/gui/sprites/*.png` 绘制机器背景、槽位框、能量条、流体条、进度条和少量状态文本。
  - 已绘制燃煤发电机、压缩机、电力高炉、燃料精炼机、氧气装载机、太阳能板、水泵、充能器、冷冻机、重力调节器的主要状态。
  - `ENERGIZER` 当前没有专用贴图，代码回退使用 `compressor.png`。
  - 目前没有 1.20 风格的侧面配置按钮、红石控制按钮、配置页按钮，也没有向服务端提交机器设置的专用网络包。

- `earth.terrarium.adastra.client.gui.PlanetSelectionGui`
  - 简化版星球选择 `GuiScreen`。
  - 通过 `PacketOpenPlanetSelection` 在客户端打开，通过 `PacketLandPlanet` 向服务端提交目标维度。
  - 不是 1.20 的 `PlanetsMenu/PlanetsScreen` 完整移植；缺空间站、多层级星图、复杂禁用/解锁逻辑。

- `earth.terrarium.adastra.client.gui.RadioStationGui`
  - 收音机独立 `GuiScreen`。
  - 不属于总目标列出的 15 个菜单，但当前已可通过网络包打开和保存电台。

### 网络同步类

- `earth.terrarium.adastra.common.network.NetworkHandler`
  - 使用 `SimpleNetworkWrapper`。
  - 已注册 5 个包：
    - `PacketOpenPlanetSelection`：客户端打开星球选择界面。
    - `PacketLandPlanet`：服务端处理降落/传送。
    - `PacketOpenRadioGui`：客户端打开收音机界面。
    - `PacketSetRadioStation`：服务端写入收音机电台。
    - `PacketSyncKeybinds`：服务端同步按键状态。
  - 当前没有机器 GUI 控制类网络包，例如：
    - 设置机器字段/滑块
    - 切换电力高炉模式
    - 设置重力目标值/范围
    - 切换侧面输入输出
    - 切换红石控制
    - 打开实体载具 GUI

## 对照 15 个菜单的覆盖情况

| 目标菜单 | 1.12 当前状态 | 结论 |
| --- | --- | --- |
| 燃煤发电机 | 有 TileEntity、Container 布局、GuiContainer、能量/燃烧字段同步 | 部分完成 |
| 压缩机 | 有 TileEntity、Container 布局、GuiContainer、压缩配方和进度字段 | 部分完成 |
| 电力高炉 | 有 TileEntity、Container 布局、GuiContainer、合金/冶炼逻辑和模式字段 | 部分完成；缺模式切换交互 |
| 燃料精炼机 | 有 TileEntity、双流体罐、Container 布局、GuiContainer 流体/能量显示 | 部分完成 |
| 氧气装载机 | 有 TileEntity、双流体罐、Container 布局、GuiContainer 流体/能量显示 | 部分完成 |
| 水泵 | 有 TileEntity、Container 布局、GuiContainer 能量/水显示 | 部分完成 |
| 太阳能板 | 有 TileEntity、Container 布局、GuiContainer 能量/发电状态显示 | 部分完成 |
| 氧气分配器 | 有 TileEntity 和 `oxygen_distributor.png` 贴图，但无 GUI id、无 `layoutFor/idFor`、右键不会打开 GUI | 缺失菜单接线 |
| 重力调节器 | 有 TileEntity、Container 布局、GuiContainer 状态/滑块显示 | 部分完成；滑块显示不可交互 |
| 冷冻机 | 有 TileEntity、Container 布局、GuiContainer 流体/进度显示 | 部分完成 |
| NASA 工作台 | 有 TileEntity、Container 布局、GuiContainer 贴图和槽位 | 部分完成；当前偏自动合成，和 1.20 输出点击合成语义不同 |
| 火箭 | 有实体和 `rocket.png` GUI 贴图资产，但没有 Container/Gui/打开入口 | 缺失 |
| 月球车 | 有实体和 `rover.png` GUI 贴图资产，但没有 Container/Gui/打开入口 | 缺失 |
| 着陆器 | 有实体和 `lander.png` GUI 贴图资产，但没有 Container/Gui/打开入口 | 缺失 |
| 星球选择 | 有简化 `PlanetSelectionGui` 和网络包 | 部分完成；不是完整 1.20 菜单体系 |

整体判断：15 个目标菜单中，0 个可以视为完整 1.20 等价完成；11 个机器菜单中有 10 个主线机器已能打开通用 GUI，氧气分配器缺菜单接线；3 个载具菜单缺失；星球选择已有简化版本。

## 对照 13 类机器的覆盖情况

| 机器 | TileEntity | GUI/Container | 机器逻辑 | 主要缺口 |
| --- | --- | --- | --- | --- |
| 燃煤发电机 | `CoalGeneratorTileEntity` | 已接入 | 燃料转 FE、推电、燃烧字段 | 缺 1.20 配置页/红石/侧面 GUI |
| 压缩机 | `CompressorTileEntity` | 已接入 | 压缩配方、能量消耗、输出槽 | 缺完整配方数据迁移和配置 GUI |
| 电力高炉 | `EtrionicBlastFurnaceTileEntity` | 已接入 | 合金/冶炼、输出槽、模式字段 | 缺客户端切换模式按钮/包 |
| NASA 工作台 | `NasaWorkbenchTileEntity` | 已接入 | 4 阶火箭配方 | 当前自动合成，缺 1.20 输出点击/按钮语义 |
| 燃料精炼机 | `FuelRefineryTileEntity` | 已接入 | 油转燃料、容器倒入/装出 | 缺配置 GUI 和更完整进度/配方表达 |
| 氧气装载机 | `OxygenLoaderTileEntity` | 已接入 | 水/氧气转输出氧气、音效 | 缺配置 GUI 和更完整进度表达 |
| 太阳能板 | `SolarPanelTileEntity` | 已接入 | 白天见天发电、推电 | 缺维度/星球发电差异 |
| 水泵 | `WaterPumpTileEntity` | 已接入 | 下方水源产水、耗电 | 缺更完整环境规则 |
| 氧气分配器 | `OxygenDistributorTileEntity` | 未接入 | 供氧范围、耗氧/耗电、空气漩涡 | 首要缺 GUI id、容器布局、状态显示 |
| 重力调节器 | `GravityNormalizerTileEntity` | 已接入 | 范围估算、耗电、目标重力字段 | GUI 只显示，缺交互提交 |
| 充能器 | `EnergizerTileEntity` | 已接入 1.12 特色 GUI | 给 FE 物品充电、推/拉电 | 1.20 无正式菜单；当前贴图回退 compressor |
| 冷冻机 | `CryoFreezerTileEntity` | 已接入 | 冰/冰片转低温燃料、装桶 | 缺配置 GUI 和完整配方数据化 |
| 氧气传感器 | `DetectorTileEntity` | 无菜单 | 氧气探测、红石输出、扳手切模式/反转 | 温度/重力检测仍是 placeholder；1.20 也不是正式菜单目标 |

基础能力已经落在 `AdAstraMachineTileEntity`：库存、Forge Energy、FluidTank、侧面模式、红石控制、NBT、Capability、字段同步。缺口主要在 GUI 交互、网络提交和 1.20 具体屏幕语义。

## 资源资产覆盖

`src/main/resources/assets/ad_astra/textures/gui/container` 当前已有以下目标贴图：

- `coal_generator.png`
- `compressor.png`
- `cryo_freezer.png`
- `etrionic_blast_furnace.png`
- `fuel_refinery.png`
- `gravity_normalizer.png`
- `nasa_workbench.png`
- `oxygen_distributor.png`
- `oxygen_loader.png`
- `solar_panel.png`
- `water_pump.png`
- `rocket.png`
- `rover.png`
- `lander.png`

这说明 GUI 资产基础较好。下一步主要是 Java 侧 Container/Gui/网络接线，而不是补贴图。

## 下一批适合并行实现的任务

### 任务 1：氧气分配器 GUI 接线

建议负责范围：

- `src/main/java/earth/terrarium/adastra/common/registry/ModGuiIds.java`
- `src/main/java/earth/terrarium/adastra/common/container/AdAstraMachineContainer.java`
- `src/main/java/earth/terrarium/adastra/client/gui/AdAstraMachineGui.java`

目标：

- 增加 `OXYGEN_DISTRIBUTOR` GUI id。
- 在 `layoutFor/idFor` 中接入 `OxygenDistributorTileEntity`。
- 使用现有 `oxygen_distributor.png` 贴图。
- 显示能量、氧气/水存量、供氧状态、供氧范围、每 tick 耗能/耗氧。

风险较低，因为 TileEntity 和贴图已存在，主要缺的是 GUI 映射。

### 任务 2：载具 GUI 基础层

建议负责范围：

- `src/main/java/earth/terrarium/adastra/common/entities/vehicles/*`
- 新增 `src/main/java/earth/terrarium/adastra/common/container/*Vehicle*.java`
- 新增 `src/main/java/earth/terrarium/adastra/client/gui/*Vehicle*.java`
- 必要时新增 `src/main/java/earth/terrarium/adastra/common/network/packet/PacketOpenVehicleGui.java`

目标：

- 为火箭、月球车、着陆器建立 1.12 的 Container/Gui 基础。
- 第一阶段可先做燃料/库存只读显示和基础槽位，不急着完成完整发射库存逻辑。
- 使用现有 `rocket.png`、`rover.png`、`lander.png`。

注意：

- 该任务不要同时重构机器 GUI。
- 若需要修改 `ModGuiIds`、`ModGuiHandler`、`NetworkHandler`，应由该任务单独占用这些共享文件，避免和任务 1 同时写同一行。

### 任务 3：机器交互网络包

建议负责范围：

- 新增 `src/main/java/earth/terrarium/adastra/common/network/packet/PacketSetMachineField.java`
- 新增或扩展 `PacketSetMachineConfig.java`
- `src/main/java/earth/terrarium/adastra/common/network/NetworkHandler.java`
- `src/main/java/earth/terrarium/adastra/common/tile/AdAstraMachineTileEntity.java`

目标：

- 给重力调节器目标重力/范围、电力高炉模式、红石控制、侧面配置提供服务端提交路径。
- 保持包内做距离、维度、TileEntity 类型校验。
- 先做通用字段提交，再逐步在 GUI 上挂按钮/滑块。

注意：

- 这项和任务 1 都可能碰 `AdAstraMachineGui`。若并行，应先只做服务端包和 TileEntity API，GUI 按钮另开后续任务。

### 任务 4：NASA 工作台合成语义对齐

建议负责范围：

- `src/main/java/earth/terrarium/adastra/common/tile/NasaWorkbenchTileEntity.java`
- `src/main/java/earth/terrarium/adastra/common/container/AdAstraMachineContainer.java`
- 必要时 `src/main/java/earth/terrarium/adastra/client/gui/AdAstraMachineGui.java`

目标：

- 避免当前自动合成直接消耗材料。
- 改为接近 1.20 的输出点击或按钮确认合成。
- 确认输出槽不可放入、可取出，并在取出/点击时消耗输入。

注意：

- 这项和任务 1 会共享通用 Container/GUI 文件，不建议与任务 1 同时改同一工作树。

### 任务 5：星球选择界面升级

建议负责范围：

- `src/main/java/earth/terrarium/adastra/client/gui/PlanetSelectionGui.java`
- `src/main/java/earth/terrarium/adastra/common/network/packet/PacketOpenPlanetSelection.java`
- `src/main/java/earth/terrarium/adastra/common/network/packet/PacketLandPlanet.java`
- `src/main/java/earth/terrarium/adastra/common/util/PlanetTravelHelper.java`

目标：

- 在现有简化 GUI 基础上增加更接近 1.20 的星球可达性、禁用原因、选择状态和后续空间站入口。
- 这项与机器 GUI 文件基本隔离，适合并行。

## 建议优先级

1. 先补氧气分配器 GUI 接线，因为它是 11 个机器菜单里当前唯一明显断开的菜单。
2. 再补机器交互网络包，否则重力调节器、电力高炉、侧面配置等只能显示，不能像 1.20 那样操作。
3. 然后拆载具 GUI，因为火箭/月球车/着陆器涉及实体库存、燃料、骑乘和网络，范围比单个机器大。
4. NASA 工作台合成语义和星球选择升级可并行推进，但不要和通用机器 GUI 接线抢同一组文件。

## 需要游戏内重点验证的内容

- 每个已接入机器右键是否能打开 GUI。
- 氧气分配器补接线后，右键是否打开界面，能量/氧气/范围数值是否随机器运行变化。
- 重力调节器 GUI 的显示字段是否与实际范围、耗电一致；后续加交互后再验证滑块是否能保存。
- NASA 工作台不要在未确认合成时吞材料。
- 火箭/月球车/着陆器后续补 GUI 后，骑乘交互和打开 GUI 的按键/右键逻辑不能互相冲突。
