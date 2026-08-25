package net.langball.coffee.registry;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.menu.MachineMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CoffeeWork.MODID);

    public static final RegistryObject<MenuType<MachineMenu>> MACHINE = REGISTER.register("machine",
            () -> IForgeMenuType.create(MachineMenu::new));

    private ModMenus() {
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
