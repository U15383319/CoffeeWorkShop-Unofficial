package net.langball.coffee;

import net.langball.coffee.registry.ModEffects;
import net.langball.coffee.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class CommonEvents {
    private CommonEvents() {
    }

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        if (player.hasEffect(ModEffects.CAFFEINE.get())) {
            event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
            player.displayClientMessage(Component.translatable("coffeework.message.bed.caffeine"), true);
        }
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != VillagerProfession.FARMER) {
            return;
        }

        event.getTrades().get(1).add((trader, random) -> new MerchantOffer(
                new ItemStack(Items.EMERALD), new ItemStack(ModItems.get("coffee_seeds").get(), 2),
                16, 2, 0.05F));
        event.getTrades().get(2).add((trader, random) -> new MerchantOffer(
                new ItemStack(ModItems.get("coffee_bean").get(), 8), new ItemStack(Items.EMERALD),
                16, 10, 0.05F));
        event.getTrades().get(3).add((trader, random) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 4), new ItemStack(ModItems.get("coffee_latte").get()),
                12, 20, 0.05F));
    }
}
