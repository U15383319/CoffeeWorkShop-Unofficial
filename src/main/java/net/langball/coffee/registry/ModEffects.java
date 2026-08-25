package net.langball.coffee.registry;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.effect.CaffeineEffect;
import net.langball.coffee.effect.GoldenHeartEffect;
import net.langball.coffee.effect.RelaxEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> REGISTER =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CoffeeWork.MODID);

    public static final RegistryObject<MobEffect> RELAX = REGISTER.register("relax",
            RelaxEffect::new);
    public static final RegistryObject<MobEffect> CAFFEINE = REGISTER.register("caffeine", CaffeineEffect::new);
    public static final RegistryObject<MobEffect> GOLDEN_HEART = REGISTER.register("golden_heart", GoldenHeartEffect::new);

    private ModEffects() {
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
