package net.langball.coffee;

import net.minecraftforge.common.ForgeConfigSpec;

public final class CoffeeConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue MACHINE_TIME = BUILDER
            .comment("Default processing time, in ticks, for Coffee Workshop machines.")
            .defineInRange("machineProcessingTime", 200, 20, 72000);

    public static final ForgeConfigSpec.BooleanValue GENERATE_SODA_ORE = BUILDER
            .comment("Whether soda ore may be added to overworld biomes.")
            .define("generateSodaOre", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private CoffeeConfig() {
    }
}
