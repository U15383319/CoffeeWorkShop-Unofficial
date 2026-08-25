# Coffee Workshop Unofficial — Forge 1.20.1

这是 Coffee Workshop `1.2.8.1` 从 Forge 1.12.2 移植到 Forge 1.20.1 的非官方版本。原始代码与美术资源来自 [0999312/CoffeeWorkshop](https://github.com/0999312/CoffeeWorkshop)。

## 构建

需要 Java 17。Windows 下运行：

```powershell
.\gradlew.bat build
```

成品位于 `build/libs/coffeework-1.2.8.1+forge.1.20.1.jar`。

开发环境可使用：

```powershell
.\gradlew.bat runClient
.\gradlew.bat runServer
```

默认开发运行会自动加载 JEI、Jade、Patchouli、Torcherino、Tough As Nails（含 GlitchCore）和 Tea the Story: Recount，便于直接测试内容与全部已恢复联动；这些模组只进入开发运行时，不会打包进成品 JAR。若要验证联动均保持可选，可附加 `-PskipRestoredCompatRuntime`，临时跳过 Torcherino、Tough As Nails、GlitchCore 与 Tea Recount。

## 已移植内容

- 110 个方块和 444 个物品，其中包含 149 种食物与 57 种饮品（8 种 Tea Recount 饮品仅在联动模组存在时显示与合成）。
- 咖啡树、蓝莓丛、香草作物与冷萃壶。
- 火力研磨器、咖啡机、冰淇淋机、火力卷板器和粘土烤炉；包括容器、进度同步、燃料、自动化物品能力及自定义配方类型。
- JEI 机器配方联动：五类机器各自拥有配方分类、工作站入口、配方转移，以及机器界面内的配方查询按钮。
- Patchouli 双语咖啡指南：包含基础、机器、饮品与食物三类共 10 个条目，可用书与咖啡种子无序合成。
- Torcherino 联动：五类机器的服务端方块实体 tick 可被 Torcherino 直接加速。
- Tough As Nails 联动：所有饮品恢复口渴值与水分值，并按热饮/冰饮调整玩家体温。
- Tea the Story: Recount 联动：恢复绿茶、红茶、奶茶、鸳鸯及其冰饮的 8 个条件配方与原版联动效果。
- 放松、咖啡因、金色心灵三种效果；饮品恢复了多次饮用、效果叠加和杯子返还。
- 饮品上盘/取回、蛋糕、农民交易、三张音乐唱片和苏打矿世界生成。
- 279 个核心数据包配方、1 个 Patchouli 条件配方、8 个 Tea Recount 条件配方、方块掉落表及中、英、日语言资源。

## 资源迁移

如需从原仓库重新同步资源，运行：

```powershell
.\tools\migrate_legacy_assets.ps1 -LegacyProject E:\Cosmos\projects\minecraft\CoffeeWorkshop
```

脚本会复制旧资源、转换 1.20.1 模型/方块状态/语言格式、拆分原 metadata 物品，并重新生成内容目录、配方、掉落表与世界生成数据。

## 兼容性说明

- JEI、Jade 和 Patchouli 均为可选依赖；未安装时核心模组仍可运行。开发环境默认加载 JEI `15.20.0.112`、Jade `11.13.3` 与 Patchouli `1.20.1-85-FORGE`。
- Torcherino、Tough As Nails 和 Tea the Story: Recount 均为可选依赖；已按 Forge 1.20.1 的 Torcherino `20.0.0`、Tough As Nails `9.2.0.171`（及 GlitchCore `0.0.1.1`）和 Tea Recount `0.4.6.1.0.1` 恢复联动。
- HeatAndClimate 与 IC2 的旧版专用 API 集成尚未恢复；原矿辞材料在独立运行时使用了 Minecraft 或 Coffee Workshop 的等价材料。
- 1.12.2 使用 metadata 保存大量物品变种，本移植将其拆成独立注册项，因此旧世界和旧玩家背包不能直接无损升级。请先备份存档，并按需要制作专门的数据迁移工具。
- 项目与移植资源继续遵循原项目的 `All Rights Reserved` 声明；发布前请确认已取得相应授权。
