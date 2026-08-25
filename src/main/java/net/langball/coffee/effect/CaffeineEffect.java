package net.langball.coffee.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public final class CaffeineEffect extends MobEffect {
    public CaffeineEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x5B3824);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().magic(), 1.0F);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return (amplifier >= 64 || duration >= 48000) && (duration & 1) == 0;
    }
}
