# Development Notes: Forge 1.20.1 Port Compatibility

Recorded on 2026-10-14. The following notes were moved from the compatibility section of the original README.

- JEI, Jade, and Patchouli are optional dependencies; the core mod works without them. The development environment loads JEI `15.20.0.112`, Jade `11.13.3`, and Patchouli `1.20.1-85-FORGE` by default.
- Torcherino, Tough As Nails, and Tea the Story: Recount are optional dependencies. Integrations have been restored for the Forge 1.20.1 versions: Torcherino `20.0.0`, Tough As Nails `9.2.0.171` (with GlitchCore `0.0.1.1`), and Tea Recount `0.4.6.1.0.1`.
- Integrations using the legacy HeatAndClimate and IC2 APIs have not yet been restored. When running without those mods, materials previously referenced through the Ore Dictionary use equivalent Minecraft or Coffee Workshop materials.
- The 1.12.2 version used metadata to store many item variants. This port registers those variants as separate entries, so old worlds and player inventories cannot be upgraded directly without data loss. Back up your saves first and create dedicated data migration tools as needed.
