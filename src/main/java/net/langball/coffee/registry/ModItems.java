package net.langball.coffee.registry;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.content.ContentCatalog;
import net.langball.coffee.item.CoffeeDrinkItem;
import net.langball.coffee.item.CatalogContainerItem;
import net.langball.coffee.item.RelaxingFoodItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, CoffeeWork.MODID);
    public static final Map<String, RegistryObject<Item>> ENTRIES;

    static {
        Map<String, RegistryObject<Item>> entries = new LinkedHashMap<>();
        ContentCatalog.ITEM_IDS.forEach(id -> entries.put(id, REGISTER.register(id, () -> create(id))));
        ENTRIES = Collections.unmodifiableMap(entries);
    }

    private ModItems() {
    }

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    public static RegistryObject<Item> get(String id) {
        RegistryObject<Item> item = ENTRIES.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Unknown Coffee Workshop item: " + id);
        }
        return item;
    }

    private static Item create(String id) {
        Item record = createRecord(id);
        if (record != null) {
            return record;
        }
        if (id.equals("coffee_seeds")) {
            return new ItemNameBlockItem(ModBlocks.get("coffee_tree").get(), new Item.Properties());
        }
        if (id.equals("vanilla_seeds")) {
            return new ItemNameBlockItem(ModBlocks.get("vanilla_crop").get(), new Item.Properties());
        }
        if (ModBlocks.ENTRIES.containsKey(id)) {
            return new BlockItem(ModBlocks.get(id).get(), properties(id));
        }
        if (ContentCatalog.DRINK_IDS.contains(id)) {
            return new CoffeeDrinkItem(id, properties(id).stacksTo(1).food(food(id, true)));
        }
        if (ContentCatalog.FOOD_IDS.contains(id)) {
            return new RelaxingFoodItem(id, properties(id).food(food(id, false)));
        }
        String remainder = craftingRemainder(id);
        if (remainder != null) {
            return new CatalogContainerItem(properties(id), () -> get(remainder).get());
        }
        if (id.equals("coldbrew_bottle")) {
            return new CatalogContainerItem(properties(id).stacksTo(4), () -> net.minecraft.world.item.Items.GLASS_BOTTLE);
        }
        return new Item(properties(id));
    }

    private static String craftingRemainder(String id) {
        if (id.equals("mixing_bowl") || id.equals("mooncake_model")) {
            return id;
        }
        if (id.startsWith("iron_bowl_") && !id.equals("iron_bowl")) {
            return "iron_bowl";
        }
        if (id.startsWith("syrup_") && !id.equals("syrup_empty")) {
            return "syrup_empty";
        }
        if (id.endsWith("_plate_model")) {
            return "cake_model_plate";
        }
        if ((id.startsWith("jiggy_cake_") && id.endsWith("_model"))
                || id.equals("brownie_model") || id.equals("tiramisu_model")) {
            return "cake_model_square";
        }
        if ((id.startsWith("cake_sponge_") || id.startsWith("mousse_") || id.equals("cake_cheese_model"))
                && id.endsWith("_model")) {
            return "cake_model";
        }
        return null;
    }

    private static Item createRecord(String id) {
        return switch (id) {
            case "records_kusa_noshi_to_ne" ->
                    new RecordItem(1, ModSounds.KUSA_NOSHI_TO_NE, properties(id), 2279);
            case "records_lazy_lady_kaguya" ->
                    new RecordItem(2, ModSounds.LAZY_LADY_KAGUYA, properties(id), 1160);
            case "records_the_grimoire_of_marisa" ->
                    new RecordItem(3, ModSounds.THE_GRIMOIRE_OF_MARISA, properties(id), 3088);
            default -> null;
        };
    }

    private static Item.Properties properties(String id) {
        Item.Properties properties = new Item.Properties();
        if (id.contains("model") || id.equals("iron_bowl") || id.equals("small_model")
                || id.equals("mixing_bowl") || id.equals("cup") || id.equals("cup_glass")) {
            properties.stacksTo(16);
        }
        if (id.startsWith("record_") || id.startsWith("records_")) {
            properties.stacksTo(1);
        }
        if (id.equals("mixing_bowl") || id.equals("mooncake_model")) {
            properties.stacksTo(1);
        }
        if (id.contains("knife")) {
            properties.durability(128);
        }
        return properties;
    }

    private static FoodProperties food(String id, boolean drink) {
        int nutrition;
        float saturation;
        if (drink) {
            nutrition = id.equals("espresso") ? 1
                    : id.startsWith("cocoa") ? 2
                    : id.contains("latte") || id.contains("cappuccino")
                    || id.contains("macchiato") || id.contains("mochaccino") ? 3 : 2;
            saturation = id.equals("espresso") ? 0.1F : 0.2F;
        } else if (id.contains("raw")) {
            nutrition = 1;
            saturation = 0.2F;
        } else if (id.startsWith("sandwich") || id.startsWith("pie_")) {
            nutrition = 10;
            saturation = 0.8F;
        } else if (id.contains("cake") || id.contains("muffin") || id.contains("icecream")) {
            nutrition = 6;
            saturation = 0.6F;
        } else {
            nutrition = 5;
            saturation = 0.5F;
        }
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation);
        if (drink) {
            builder.alwaysEat();
        }
        return builder.build();
    }
}
