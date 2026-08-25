package net.langball.coffee.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;

public final class GoldenHeartEffect extends MobEffect {
    public GoldenHeartEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD55C);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        for (MobEffectInstance instance : new ArrayList<>(entity.getActiveEffects())) {
            if (instance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                entity.removeEffect(instance.getEffect());
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
