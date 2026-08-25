package net.langball.coffee.registry;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.recipe.MachineRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CoffeeWork.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CoffeeWork.MODID);

    private static final Map<MachineBlock.Kind, RegistryObject<RecipeType<MachineRecipe>>> TYPE_BY_KIND =
            new EnumMap<>(MachineBlock.Kind.class);
    private static final Map<MachineBlock.Kind, RegistryObject<RecipeSerializer<MachineRecipe>>> SERIALIZER_BY_KIND =
            new EnumMap<>(MachineBlock.Kind.class);

    static {
        for (MachineBlock.Kind kind : MachineBlock.Kind.values()) {
            String name = kind.serializedName();
            TYPE_BY_KIND.put(kind, TYPES.register(name, () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return CoffeeWork.MODID + ':' + name;
                }
            }));
            SERIALIZER_BY_KIND.put(kind, SERIALIZERS.register(name, () -> new MachineRecipe.Serializer(kind)));
        }
    }

    private ModRecipes() {
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        SERIALIZERS.register(bus);
    }

    public static RegistryObject<RecipeType<MachineRecipe>> type(MachineBlock.Kind kind) {
        return TYPE_BY_KIND.get(kind);
    }

    public static RegistryObject<RecipeSerializer<MachineRecipe>> serializer(MachineBlock.Kind kind) {
        return SERIALIZER_BY_KIND.get(kind);
    }
}
