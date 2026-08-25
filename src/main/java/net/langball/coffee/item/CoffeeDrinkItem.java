package net.langball.coffee.item;

import net.langball.coffee.block.LegacyDirectionalBlock;
import net.langball.coffee.compat.teastory.TeaStoryCompat;
import net.langball.coffee.compat.toughasnails.ToughAsNailsCompat;
import net.langball.coffee.registry.ModBlocks;
import net.langball.coffee.registry.ModEffects;
import net.langball.coffee.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;

import java.util.List;

public final class CoffeeDrinkItem extends Item {
    private static final String SERVINGS_USED = "CoffeeWorkServingsUsed";
    private final String id;

    public CoffeeDrinkItem(String id, Properties properties) {
        super(properties);
        this.id = id;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockState clicked = level.getBlockState(context.getClickedPos());
        String servingId = id + "_plate";
        if (servingsUsed(context.getItemInHand()) != 0 || !clicked.is(ModBlocks.get("plate").get())
                || !ModBlocks.ENTRIES.containsKey(servingId)) {
            return super.useOn(context);
        }

        if (!level.isClientSide) {
            BlockState serving = ModBlocks.get(servingId).get().defaultBlockState();
            if (serving.hasProperty(LegacyDirectionalBlock.FACING)
                    && clicked.hasProperty(LegacyDirectionalBlock.FACING)) {
                serving = serving.setValue(LegacyDirectionalBlock.FACING,
                        clicked.getValue(LegacyDirectionalBlock.FACING));
            }
            level.setBlock(context.getClickedPos(), serving, 3);
            Player player = context.getPlayer();
            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        ItemStack oneServing = stack.copy();
        oneServing.setCount(1);
        super.finishUsingItem(oneServing, level, consumer);
        if (!level.isClientSide) {
            applyDrinkEffects(consumer);
            if (consumer instanceof Player player && ModList.get().isLoaded(ToughAsNailsCompat.MOD_ID)) {
                ToughAsNailsCompat.afterDrink(player, id);
            }
        }

        if (consumer instanceof Player player && player.getAbilities().instabuild) {
            return stack;
        }

        int used = servingsUsed(stack) + 1;
        if (used < maxServings()) {
            stack.getOrCreateTag().putInt(SERVINGS_USED, used);
            return stack;
        }

        stack.shrink(1);
        if (id.equals("espresso") || id.equals("coffee_instant_cup")) {
            return stack;
        }
        String cupId = id.endsWith("_ice") ? "cup_glass" : "cup";
        return new ItemStack(ModItems.get(cupId).get());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return servingsUsed(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * (maxServings() - servingsUsed(stack)) / maxServings());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float remaining = (float) (maxServings() - servingsUsed(stack)) / maxServings();
        return Mth.hsvToRgb(Math.max(0.0F, remaining) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("coffeework.tooltip.servings",
                maxServings() - servingsUsed(stack), maxServings()).withStyle(ChatFormatting.GRAY));
    }

    private int maxServings() {
        if (id.equals("espresso")) {
            return 1;
        }
        if (id.startsWith("coffee_americano") || id.startsWith("cocoa")) {
            return 4;
        }
        return 3;
    }

    private static int servingsUsed(ItemStack stack) {
        return stack.hasTag() ? Math.max(0, stack.getTag().getInt(SERVINGS_USED)) : 0;
    }

    private void applyDrinkEffects(LivingEntity consumer) {
        if (TeaStoryCompat.applyDrinkEffects(consumer, id)) {
            return;
        }

        boolean coldBrew = id.contains("coldbrew");
        int relaxDuration = relaxDuration();
        int relaxAmplifier = coldBrew ? 2 : id.startsWith("coffee_instant") ? 0 : 1;
        int caffeineDuration = id.startsWith("coffee_instant") ? 1200
                : id.startsWith("cocoa_strong") ? 1500
                : id.startsWith("cocoa") ? 1200 : 3600;
        int caffeineAmplifier = coldBrew || id.equals("espresso") ? 1 : 0;

        addStackingEffect(consumer, ModEffects.RELAX.get(), relaxDuration, relaxAmplifier);
        addStackingEffect(consumer, ModEffects.CAFFEINE.get(), caffeineDuration, caffeineAmplifier);

        boolean coffeeBuffs = !id.startsWith("coffee_instant") && !id.startsWith("cocoa");
        int vanillaAmplifier = coldBrew ? 1 : 0;
        if (coffeeBuffs) {
            addStackingEffect(consumer, MobEffects.DIG_SPEED, relaxDuration, vanillaAmplifier);
            addStackingEffect(consumer, MobEffects.MOVEMENT_SPEED, relaxDuration, vanillaAmplifier);
        }
        if (id.startsWith("coffee_americano") && id.endsWith("_ice")) {
            addStackingEffect(consumer, MobEffects.FIRE_RESISTANCE, relaxDuration, 0);
        }

        boolean hasMilk = id.contains("latte") || id.contains("cappuccino")
                || id.contains("macchiato") || id.contains("mochaccino") || id.startsWith("cocoa");
        if (hasMilk) {
            addStackingEffect(consumer, ModEffects.GOLDEN_HEART.get(), goldenHeartDuration(), vanillaAmplifier);
        }

        addFruitEffects(consumer, vanillaAmplifier);
        if (id.contains("caramel") || id.contains("macchiato")) {
            addStackingEffect(consumer, MobEffects.DAMAGE_RESISTANCE, relaxDuration, vanillaAmplifier);
        }
        if (id.contains("chocolate") || id.contains("mochaccino") || id.startsWith("cocoa")) {
            addStackingEffect(consumer, MobEffects.DAMAGE_BOOST, goldenHeartDuration(), vanillaAmplifier);
        }
        if (id.contains("mint")) {
            addStackingEffect(consumer, MobEffects.JUMP, relaxDuration, vanillaAmplifier);
        }
        if (id.contains("vanilla")) {
            addStackingEffect(consumer, MobEffects.ABSORPTION, relaxDuration, vanillaAmplifier);
        }
        if (id.contains("sakura")) {
            addStackingEffect(consumer, MobEffects.DAMAGE_BOOST, relaxDuration, 0);
            addStackingEffect(consumer, MobEffects.REGENERATION, relaxDuration, 0);
        }
    }

    private int relaxDuration() {
        boolean iced = id.endsWith("_ice");
        if (id.startsWith("coffee_instant")) return 1200;
        if (id.startsWith("cocoa")) return iced ? 900 : 600;
        if (id.contains("sakura")) return iced ? 1200 : 900;
        if (id.equals("espresso")) return 600;
        if (id.startsWith("coffee_americano")) {
            if (id.contains("nitro")) return 1000;
            return iced ? 800 : 600;
        }
        if (id.contains("coldbrew")) {
            if (id.contains("latte")) return iced ? 1200 : 900;
            return iced ? 900 : 600;
        }
        boolean specialty = id.contains("cappuccino") || id.contains("macchiato") || id.contains("mochaccino");
        return iced ? specialty ? 1500 : 1200 : specialty ? 1200 : 900;
    }

    private int goldenHeartDuration() {
        if (id.startsWith("cocoa")) {
            boolean strong = id.contains("strong");
            return id.endsWith("_ice") ? strong ? 900 : 600 : strong ? 600 : 400;
        }
        return relaxDuration();
    }

    private void addFruitEffects(LivingEntity consumer, int amplifier) {
        if (!id.contains("fruit")) {
            return;
        }
        int saturation;
        int healthBoost;
        if (id.contains("coldbrew")) {
            boolean latte = id.contains("latte");
            if (id.endsWith("_ice")) {
                saturation = latte ? 300 : 150;
                healthBoost = latte ? 1200 : 600;
            } else {
                saturation = latte ? 150 : 50;
                healthBoost = latte ? 600 : 300;
            }
        } else if (id.startsWith("coffee_americano")) {
            saturation = id.contains("nitro") ? 150 : id.endsWith("_ice") ? 100 : 50;
            healthBoost = id.contains("nitro") ? 600 : id.endsWith("_ice") ? 500 : 300;
        } else {
            saturation = id.endsWith("_ice") ? 200 : 150;
            healthBoost = id.endsWith("_ice") ? 800 : 600;
        }
        addStackingEffect(consumer, MobEffects.SATURATION, saturation, amplifier);
        addStackingEffect(consumer, MobEffects.HEALTH_BOOST, healthBoost, amplifier);
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
