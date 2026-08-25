package net.langball.coffee.block;

import net.langball.coffee.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IForgeShearable;

import java.util.List;

public final class FourStageCropBlock extends CropBlock implements IForgeShearable {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    private final String cropId;

    public FourStageCropBlock(String cropId, Properties properties) {
        super(properties);
        this.cropId = cropId;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    protected Item getBaseSeedId() {
        return ModItems.get(cropId.equals("coffee_tree") ? "coffee_seeds" : "blueberry_bush").get();
    }

    @Override
    protected boolean mayPlaceOn(BlockState soil, BlockGetter level, BlockPos pos) {
        return soil.is(BlockTags.DIRT) || soil.is(Blocks.FARMLAND);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (getAge(state) < getMaxAge()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            String resultId = cropId.equals("coffee_tree") ? "coffee_seeds" : "blueberry";
            popResource(level, pos, new ItemStack(ModItems.get(resultId).get()));
            level.setBlock(pos, getStateForAge(1), 2);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public List<ItemStack> onSheared(Player player, ItemStack tool, Level level, BlockPos pos, int fortune) {
        String plantItemId = cropId.equals("coffee_tree") ? "coffee_tree" : "blueberry_bush";
        int count = cropId.equals("coffee_tree") ? 1 : 2;
        return List.of(new ItemStack(ModItems.get(plantItemId).get(), count));
    }
}
