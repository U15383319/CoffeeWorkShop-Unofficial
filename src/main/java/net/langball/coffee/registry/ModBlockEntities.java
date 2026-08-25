package net.langball.coffee.registry;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.entity.MachineBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CoffeeWork.MODID);

    public static final RegistryObject<BlockEntityType<MachineBlockEntity>> MACHINE = REGISTER.register("machine", () ->
            BlockEntityType.Builder.of(MachineBlockEntity::new, machineBlocks()).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    private static Block[] machineBlocks() {
        return ModBlocks.ENTRIES.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("grinder")
                        || entry.getKey().startsWith("coffeemachine")
                        || entry.getKey().startsWith("icecreammachine")
                        || entry.getKey().startsWith("roller")
                        || entry.getKey().startsWith("oven"))
                .map(entry -> entry.getValue().get())
                .toArray(Block[]::new);
    }
}
