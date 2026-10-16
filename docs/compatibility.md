# 开发记录：Forge 1.20.1 移植兼容性

记录日期：2026-10-14。以下内容迁移自原 README 的兼容性说明。

- JEI、Jade 和 Patchouli 均为可选依赖；未安装时核心模组仍可运行。开发环境默认加载 JEI `15.20.0.112`、Jade `11.13.3` 与 Patchouli `1.20.1-85-FORGE`。
- Torcherino、Tough As Nails 和 Tea the Story: Recount 均为可选依赖；已按 Forge 1.20.1 的 Torcherino `20.0.0`、Tough As Nails `9.2.0.171`（及 GlitchCore `0.0.1.1`）和 Tea Recount `0.4.6.1.0.1` 恢复联动。
- HeatAndClimate 与 IC2 的旧版专用 API 集成尚未恢复；原矿辞材料在独立运行时使用了 Minecraft 或 Coffee Workshop 的等价材料。
- 1.12.2 使用 metadata 保存大量物品变种，本移植将其拆成独立注册项，因此旧世界和旧玩家背包不能直接无损升级。请先备份存档，并按需要制作专门的数据迁移工具。
