# Ad Astra Reborn 1.12.2 移植项目 - 验证清单

## 已完成内容

### 1. 基础方块与物品
- [x] 所有基础方块（石头、矿石、金属块、装饰方块等）
- [x] 所有基础物品（锭、板、齿轮、原材料等）
- [x] 台阶、楼梯、活板门、栅栏门、砖墙等变体方块
- [x] 滑动门（Iron Sliding Door、Steel Sliding Door等）
- [x] 旗帜（Flag）
- [x] 氧气分配器（Oxygen Distributor）
- [x] 重力调节器（Gravity Normalizer）
- [x] 地球仪（Globe）
- [x] 月球仪（Lunar Globe）等仪器

### 2. 机器与GUI
- [x] NASA工作台（Nasa Workbench）及GUI
- [x] 燃料精炼器（Fuel Refinery）及GUI
- [x] 氧气装载器（Oxygen Loader）及GUI
- [x] 压缩机（Compressor）及GUI
- [x] 高炉（Blast Furnace）及GUI
- [x] 充能器（Energizer）及GUI
- [x] 所有机器GUI槽位和红石按钮位置已对齐

### 3. 火箭系统
- [x] T1-T4火箭实体、物品、渲染
- [x] T5-T7火箭实体、物品、渲染（新增）
- [x] 火箭放置到发射台
- [x] 火箭燃料系统（添加燃料、消耗燃料）
- [x] 火箭发射倒计时
- [x] 火箭上升飞行
- [x] 火箭到达高度后打开星球选择UI
- [x] 星球选择UI（包含地球返回选项）
- [x] 从月球返回地球
- [x] 降落工具（Lander）生成与渲染
- [x] NASA工作台支持T1-T7配方

### 4. 实体与渲染
- [x] 漫游车（Rover）实体与渲染
- [x] 降落工具（Lander）模型修复（从1.20正确转换）
- [x] 硫磺苦力怕（Sulfur Creeper）实体
- [x] 所有方块放置模型
- [x] 所有物品栏渲染

### 5. 配方与JEI
- [x] 所有基础配方
- [x] NASA工作台T1-T7火箭配方
- [x] JEI集成（机器配方显示）

### 6. 本地化
- [x] 英文语言文件（en_us.lang）
- [x] 中文语言文件（zh_cn.lang）
- [x] 所有方块、物品、实体、流体汉化

### 7. 其他
- [x] 发射台（Launch Pad）3x3结构
- [x] 燃料桶（Fuel Bucket）与火箭交互
- [x] 氧气系统相关物品
- [x] 太空服（Space Suit）物品与模型

---

## 未完成内容 / 已知问题

### 1. 火箭系统
- [ ] 火箭在月球上无法再次起飞（需要检查月球维度的发射台验证逻辑）
- [ ] 空间站选择UI未实现（1.20源码中空间站是单独维度，1.12.2需要额外实现）
- [ ] T5-T7火箭模型使用T4模型fallback（需要独立模型）

### 2. 模型与渲染
- [ ] T5-T7火箭独立模型（当前使用T4模型+不同纹理）
- [ ] 装备模型（太空服）渲染可能仍有小问题
- [ ] 部分旗帜放置方向/位置可能需要微调

### 3. 游戏玩法
- [ ] 从月球返回地球后，玩家应该在Lander中（当前逻辑已写，需要验证）
- [ ] 火箭 cargo 物品栏在星球旅行后需要验证是否正确保留
- [ ] 多星球旅行（水星、金星、火星等）需要逐一测试

### 4. 其他
- [ ] Patchouli 书籍《银河系漫游指南》打开崩溃（需要修复BookEntry.getResource()为null的问题）
- [ ] 滑动门关闭动画（源码中是否直接关闭需要确认）

---

## 用户需要在游戏中验证的内容

### 高优先级（必须测试）
1. **火箭发射全流程**：
   - 在发射台上放置T1-T7火箭
   - 用燃料桶给火箭加燃料（应有反馈消息）
   - 玩家坐进火箭，长按空格触发倒计时
   - 倒计时结束后火箭带着玩家上升
   - 到达高度后打开星球选择UI
   - 选择月球，确认传送到月球
   - 在月球上生成Lander，玩家在其中

2. **月球返回地球**：
   - 在月球上放置发射台和火箭
   - 给火箭加燃料
   - 坐进火箭，长按空格发射
   - 选择地球返回
   - 确认回到地球并生成Lander

3. **NASA工作台**：
   - 检查T1-T4火箭配方是否正常
   - 检查T5-T7火箭配方是否可用（需要calorite材料）
   - 确认配方输出正确

4. **机器GUI**：
   - 右键打开所有机器GUI，确认不崩溃
   - 检查槽位位置是否正确
   - 检查红石控制按钮位置

### 中优先级（建议测试）
5. **物品栏渲染**：
   - 检查T5-T7火箭在物品栏中是否正确显示
   - 检查氧气分配器、重力调节器、地球仪等仪器在物品栏中是否正确显示

6. **方块放置**：
   - 检查所有可放置方块放置后模型是否正确
   - 检查滑动门放置后贴图是否正确
   - 检查门、砖墙、台阶、活板门放置后是否正确

7. **实体渲染**：
   - 检查漫游车放置后模型和贴图
   - 检查降落工具（Lander）模型和贴图
   - 检查硫磺苦力怕生成蛋生成的实体

8. **JEI**：
   - 检查所有机器的JEI配方显示
   - 检查NASA工作台的JEI配方背景

### 低优先级（可选测试）
9. **语言文件**：
   - 检查所有物品、方块、实体是否已汉化
   - 检查是否有遗漏的英文内容

10. **其他**：
    - 检查旗帜放置方向和位置
    - 检查装备（太空服）穿戴后模型
    - 检查流体（燃料、氧气）是否正确显示和汉化

---

## 最近修改的文件

1. src/main/java/earth/terrarium/adastra/common/tile/NasaWorkbenchTileEntity.java - 添加T5-T7配方
2. src/main/java/earth/terrarium/adastra/client/render/VehicleItemStackRenderer.java - 添加T5-T7物品渲染
3. src/main/java/earth/terrarium/adastra/client/render/ModelRocket.java - 添加T5-T7模型case
4. src/main/java/earth/terrarium/adastra/client/render/ModelLander.java - 从1.20正确转换lander模型
5. src/main/java/earth/terrarium/adastra/common/entities/vehicles/Tier5RocketEntity.java - T5火箭实体
6. src/main/java/earth/terrarium/adastra/common/entities/vehicles/Tier6RocketEntity.java - T6火箭实体
7. src/main/java/earth/terrarium/adastra/common/entities/vehicles/Tier7RocketEntity.java - T7火箭实体
8. src/main/resources/assets/ad_astra/lang/en_us.lang - T5-T7语言条目
9. src/main/resources/assets/ad_astra/lang/zh_cn.lang - T5-T7语言条目
10. src/main/resources/assets/ad_astra/textures/entity/rocket/tier_5_rocket.png - T5纹理
11. src/main/resources/assets/ad_astra/textures/entity/rocket/tier_6_rocket.png - T6纹理
12. src/main/resources/assets/ad_astra/textures/entity/rocket/tier_7_rocket.png - T7纹理

---

*最后更新：2026-07-02*
