package net.langball.coffee.registry;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.ColdBrewPotBlock;
import net.langball.coffee.block.DrinkServingBlock;
import net.langball.coffee.block.FourStageCropBlock;
import net.langball.coffee.block.LegacyCakeBlock;
import net.langball.coffee.block.LegacyDirectionalBlock;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.block.PlateBlock;
import net.langball.coffee.block.VanillaCropBlock;
import net.langball.coffee.content.ContentCatalog;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModBlocks {
    public static final DeferredRegister<Block> REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CoffeeWork.MODID);
    public static final Map<String, RegistryObject<Block>> ENTRIES;

    static {
        Map<String, RegistryObject<Block>> entries = new LinkedHashMap<>();
        ContentCatalog.BLOCK_IDS.forEach(id -> entries.put(id, REGISTER.register(id, () -> create(id))));
        ENTRIES = Collections.unmodifiableMap(entries);
    }

    private ModBlocks() {
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    public static RegistryObject<Block> get(String id) {
        RegistryObject<Block> block = ENTRIES.get(id);
        if (block == null) {
            throw new IllegalArgumentException("Unknown Coffee Workshop block: " + id);
        }
        return block;
    }

    private static Block create(String id) {
        String kind = ContentCatalog.BLOCK_KINDS.getOrDefault(id, "plain");
        return switch (kind) {
            case "machine" -> new MachineBlock(MachineBlock.Kind.fromBlockId(id), id.endsWith("_on"));
            case "directional" -> id.endsWith("_plate")
                    ? new DrinkServingBlock(id, directionalProperties(id))
                    : id.equals("plate")
                    ? new PlateBlock(directionalProperties(id))
                    : new LegacyDirectionalBlock(directionalProperties(id));
            case "cake" -> new LegacyCakeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.WOOL).noOcclusion());
            case "crop4" -> new FourStageCropBlock(id,
                    BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks()
                            .instabreak().sound(SoundType.CROP));
            case "crop8" -> new VanillaCropBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks()
                            .instabreak().sound(SoundType.CROP));
            case "ferment" -> new ColdBrewPotBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(1.0F).sound(SoundType.METAL).noOcclusion().randomTicks());
            default -> new Block(plainProperties(id));
        };
    }

    private static BlockBehaviour.Properties directionalProperties(String id) {
        SoundType sound = id.startsWith("bag_") || id.startsWith("double_bag_") ? SoundType.WOOL : SoundType.STONE;
        float strength = id.endsWith("_plate") || id.equals("plate") ? 0.2F : 1.5F;
        return BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(strength).sound(sound).noOcclusion();
    }

    private static BlockBehaviour.Properties plainProperties(String id) {
        if (id.equals("soda_ore")) {
            return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.STONE);
        }
        return BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F).sound(SoundType.WOOD).noOcclusion();
    }
}
