package net.langball.coffee.item;

import net.langball.coffee.registry.ModEffects;
import net.langball.coffee.registry.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RelaxingFoodItem extends Item {
    private final String id;

    public RelaxingFoodItem(String id, Properties properties) {
        super(properties);
        this.id = id;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        ItemStack result = super.finishUsingItem(stack, level, consumer);
        MobEffectInstance relaxed = consumer.getEffect(ModEffects.RELAX.get());
        if (!level.isClientSide && relaxed != null && consumer instanceof Player player) {
            int amplifier = relaxed.getAmplifier();
            player.heal(amplifier + 4.0F);
            player.getFoodData().eat(amplifier + 2, amplifier * 1.25F);
        }
        if (!level.isClientSide && id.startsWith("souffle")
                && (!(consumer instanceof Player player) || !player.getAbilities().instabuild)) {
            ItemStack mold = new ItemStack(ModItems.get("small_model").get());
            if (result.isEmpty()) {
                return mold;
            }
            if (consumer instanceof Player player && !player.getInventory().add(mold)) {
                player.drop(mold, false);
            }
        }
        return result;
    }
}
