package net.langball.coffee.registry;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.compat.teastory.TeaStoryCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoffeeWork.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = REGISTER.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.coffeework"))
            .icon(ModCreativeTabs::createIcon)
            .displayItems((parameters, output) -> ModItems.ENTRIES.forEach((id, item) -> {
                if (TeaStoryCompat.shouldShowInCreative(id)) {
                    output.accept(item.get());
                }
            }))
            .build());

    private static ItemStack createIcon() {
        ItemStack icon = ModItems.get("coffee_seeds").get().getDefaultInstance();
        icon.getOrCreateTag().putInt("CustomModelData", 1);
        return icon;
    }

    private ModCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
