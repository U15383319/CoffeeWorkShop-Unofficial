package net.langball.coffee.registry;

import net.langball.coffee.CoffeeWork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTER =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CoffeeWork.MODID);

    public static final RegistryObject<SoundEvent> KUSA_NOSHI_TO_NE = register("records.kusa_noshi_to_ne");
    public static final RegistryObject<SoundEvent> LAZY_LADY_KAGUYA = register("records.lazy_lady_kaguya");
    public static final RegistryObject<SoundEvent> THE_GRIMOIRE_OF_MARISA = register("records.the_grimoire_of_marisa");

    private ModSounds() {
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    private static RegistryObject<SoundEvent> register(String id) {
        ResourceLocation location = new ResourceLocation(CoffeeWork.MODID, id);
        return REGISTER.register(id, () -> SoundEvent.createVariableRangeEvent(location));
    }
}
