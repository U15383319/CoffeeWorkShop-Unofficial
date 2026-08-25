package net.langball.coffee.block;

import net.langball.coffee.registry.ModBlocks;
import net.langball.coffee.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class DrinkServingBlock extends PlateBlock {
    private final String drinkId;

    public DrinkServingBlock(String blockId, Properties properties) {
        super(properties);
        this.drinkId = blockId.substring(0, blockId.length() - "_plate".length());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || !ModItems.ENTRIES.containsKey(drinkId)) {
            return super.use(state, level, pos, player, hand, hit);
        }

        if (!level.isClientSide) {
            ItemStack drink = new ItemStack(ModItems.get(drinkId).get());
            if (!player.getInventory().add(drink)) {
                player.drop(drink, false);
            }

            BlockState emptyPlate = ModBlocks.get("plate").get().defaultBlockState();
            if (emptyPlate.hasProperty(FACING) && state.hasProperty(FACING)) {
                emptyPlate = emptyPlate.setValue(FACING, state.getValue(FACING));
            }
            level.setBlock(pos, emptyPlate, 3);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
