package net.langball.coffee.block;

import net.langball.coffee.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class ColdBrewPotBlock extends Block {
    public static final IntegerProperty FERM = IntegerProperty.create("ferm", 0, 8);

    public ColdBrewPotBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FERM, 0));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(FERM) < 7;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int stage = state.getValue(FERM);
        if (stage < 7) {
            level.setBlock(pos, state.setValue(FERM, stage + 1), 2);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (state.getValue(FERM) != 7 || !held.is(Items.GLASS_BOTTLE)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            ItemStack filled = new ItemStack(ModItems.get("coldbrew_bottle").get());
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            if (held.isEmpty()) {
                player.setItemInHand(hand, filled);
            } else if (!player.getInventory().add(filled)) {
                player.drop(filled, false);
            }
            level.setBlock(pos, state.setValue(FERM, 8), 2);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FERM);
    }
}
