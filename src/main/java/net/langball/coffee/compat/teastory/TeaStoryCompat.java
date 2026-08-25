package net.langball.coffee.compat.teastory;

import net.langball.coffee.registry.ModEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public final class TeaStoryCompat {
    public static final String MOD_ID = "teastory";

    private static final Set<String> TEA_DRINKS = Set.of(
            "coffee_green_tea",
            "coffee_black_tea",
            "coffee_milk_tea",
            "coffee_mandarin_drink",
            "coffee_green_tea_ice",
            "coffee_black_tea_ice",
            "coffee_milk_tea_ice",
            "coffee_mandarin_drink_ice"
    );

    private TeaStoryCompat() {
    }

    public static boolean shouldShowInCreative(String id) {
        String drinkId = id.endsWith("_plate") ? id.substring(0, id.length() - "_plate".length()) : id;
        return !TEA_DRINKS.contains(drinkId) || ModList.get().isLoaded(MOD_ID);
    }

    /**
     * Applies the effects from the original 1.12 Tea the Story integration.
     *
     * @return true when the item is one of the eight Tea the Story drinks
     */
    public static boolean applyDrinkEffects(LivingEntity consumer, String id) {
        if (!TEA_DRINKS.contains(id)) {
            return false;
        }
        if (!ModList.get().isLoaded(MOD_ID)) {
            return true;
        }

        boolean iced = id.endsWith("_ice");
        boolean mandarin = id.startsWith("coffee_mandarin_drink");
        int duration = mandarin ? 800 : 600;
        int speedAmplifier = mandarin ? 1 : 0;

        addStackingEffect(consumer, MobEffects.DIG_SPEED, duration, speedAmplifier);
        addStackingEffect(consumer, MobEffects.MOVEMENT_SPEED, duration, speedAmplifier);

        if (id.startsWith("coffee_green_tea")) {
            addRegistryEffect(consumer, "agility", duration, 0);
        } else if (id.startsWith("coffee_black_tea")) {
            addStackingEffect(consumer, MobEffects.HEALTH_BOOST, duration, 0);
        } else {
            addStackingEffect(consumer, MobEffects.REGENERATION, duration, 0);
        }

        if (mandarin) {
            addRegistryEffect(consumer, "life_drain", duration, 0);
        }
        if (iced) {
            if (id.startsWith("coffee_milk_tea") || mandarin) {
                addStackingEffect(consumer, ModEffects.GOLDEN_HEART.get(), duration, 0);
            }
            addStackingEffect(consumer, ModEffects.RELAX.get(), duration, 1);
        }
        addStackingEffect(consumer, ModEffects.CAFFEINE.get(), mandarin ? 4800 : 3600, 0);
        return true;
    }

    private static void addRegistryEffect(LivingEntity consumer, String effectId, int duration, int amplifier) {
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(MOD_ID, effectId));
        if (effect != null) {
            addStackingEffect(consumer, effect, duration, amplifier);
        }
    }

    private static void addStackingEffect(LivingEntity consumer, MobEffect effect, int duration, int amplifier) {
        MobEffectInstance current = consumer.getEffect(effect);
        if (current != null) {
            duration += current.getDuration();
            amplifier = current.getAmplifier() + 1;
        }
        consumer.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }
}
