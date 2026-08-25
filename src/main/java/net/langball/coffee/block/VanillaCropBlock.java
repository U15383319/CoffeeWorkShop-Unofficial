package net.langball.coffee.block;

import net.langball.coffee.registry.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.CropBlock;

public final class VanillaCropBlock extends CropBlock {
    public VanillaCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Item getBaseSeedId() {
        return ModItems.get("vanilla_seeds").get();
    }
}
