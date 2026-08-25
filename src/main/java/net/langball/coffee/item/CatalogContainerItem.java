package net.langball.coffee.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public final class CatalogContainerItem extends Item {
    private final Supplier<? extends Item> remainder;

    public CatalogContainerItem(Properties properties, Supplier<? extends Item> remainder) {
        super(properties);
        this.remainder = remainder;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return new ItemStack(remainder.get());
    }
}
