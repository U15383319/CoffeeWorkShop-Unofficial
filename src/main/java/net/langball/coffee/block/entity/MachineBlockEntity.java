package net.langball.coffee.block.entity;

import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.menu.MachineMenu;
import net.langball.coffee.recipe.MachineRecipe;
import net.langball.coffee.registry.ModBlockEntities;
import net.langball.coffee.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class MachineBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_COUNT = 3;
    public static final int DATA_COUNT = 4;

    private final ItemStackHandler items = new ItemStackHandler(SLOT_COUNT) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != SLOT_OUTPUT;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> items);

    private int burnTime;
    private int burnTimeTotal;
    private int progress;
    private int progressTotal = 200;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnTimeTotal;
                case 2 -> progress;
                case 3 -> progressTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnTimeTotal = value;
                case 2 -> progress = value;
                case 3 -> progressTotal = value;
                default -> { }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public MachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MACHINE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MachineBlockEntity machine) {
        MachineBlock.Kind kind = machine.kind();
        ItemStack input = machine.items.getStackInSlot(SLOT_INPUT);
        SimpleContainer recipeInput = new SimpleContainer(input.copy());
        Optional<MachineRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.type(kind).get(), recipeInput, level);

        if (machine.burnTime > 0) {
            machine.burnTime--;
        }

        boolean canProcess = recipe.filter(machine::canAcceptResult).isPresent();
        if (canProcess && kind != MachineBlock.Kind.COFFEE_MACHINE && machine.burnTime <= 0) {
            machine.consumeFuel(kind);
        }

        boolean running = canProcess && (kind == MachineBlock.Kind.COFFEE_MACHINE || machine.burnTime > 0);
        if (running) {
            MachineRecipe activeRecipe = recipe.orElseThrow();
            machine.progressTotal = Math.max(1, activeRecipe.processingTime());
            machine.progress++;
            if (machine.progress >= machine.progressTotal) {
                machine.craft(activeRecipe, recipeInput);
                machine.progress = 0;
            }
        } else if (machine.progress > 0) {
            machine.progress = Math.max(0, machine.progress - 2);
        }

        if (state.hasProperty(MachineBlock.LIT) && state.getValue(MachineBlock.LIT) != running) {
            level.setBlock(pos, state.setValue(MachineBlock.LIT, running), 3);
        }
        machine.setChanged();
    }

    private void consumeFuel(MachineBlock.Kind kind) {
        ItemStack fuel = items.getStackInSlot(SLOT_FUEL);
        int duration = fuelDuration(kind, fuel);
        if (duration <= 0) {
            return;
        }
        burnTime = duration;
        burnTimeTotal = duration;
        ItemStack consumed = items.extractItem(SLOT_FUEL, 1, false);
        returnCraftingRemainder(SLOT_FUEL, consumed);
    }

    private static int fuelDuration(MachineBlock.Kind kind, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        if (kind == MachineBlock.Kind.ICECREAM_MACHINE) {
            if (stack.is(Items.SNOWBALL)) return 100;
            if (stack.is(Blocks.ICE.asItem())) return 200;
            if (stack.is(Blocks.PACKED_ICE.asItem())) return 400;
            if (stack.is(Blocks.BLUE_ICE.asItem())) return 800;
            return 0;
        }
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
    }

    private boolean canAcceptResult(MachineRecipe recipe) {
        ItemStack result = recipe.getResultItem(level.registryAccess());
        ItemStack output = items.getStackInSlot(SLOT_OUTPUT);
        if (result.isEmpty()) return false;
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(output, result)) return false;
        return output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), items.getSlotLimit(SLOT_OUTPUT));
    }

    private void craft(MachineRecipe recipe, SimpleContainer recipeInput) {
        ItemStack result = recipe.assemble(recipeInput, level.registryAccess());
        ItemStack output = items.getStackInSlot(SLOT_OUTPUT);
        ItemStack consumed = items.extractItem(SLOT_INPUT, 1, false);
        returnCraftingRemainder(SLOT_INPUT, consumed);
        if (output.isEmpty()) {
            items.setStackInSlot(SLOT_OUTPUT, result.copy());
        } else {
            output.grow(result.getCount());
        }
    }

    private void returnCraftingRemainder(int preferredSlot, ItemStack consumed) {
        if (!consumed.hasCraftingRemainingItem()) {
            return;
        }
        ItemStack remaining = items.insertItem(preferredSlot, consumed.getCraftingRemainingItem(), false);
        if (!remaining.isEmpty() && level != null) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), remaining);
        }
    }

    public MachineBlock.Kind kind() {
        if (getBlockState().getBlock() instanceof MachineBlock block) {
            return block.kind();
        }
        return MachineBlock.Kind.GRINDER;
    }

    public ItemStackHandler items() {
        return items;
    }

    public ContainerData data() {
        return data;
    }

    public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D,
                worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public void dropContents() {
        if (level == null || level.isClientSide) return;
        for (int slot = 0; slot < items.getSlots(); slot++) {
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(),
                    worldPosition.getZ(), items.getStackInSlot(slot));
        }
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new MachineMenu(containerId, inventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", items.serializeNBT());
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnTimeTotal", burnTimeTotal);
        tag.putInt("Progress", progress);
        tag.putInt("ProgressTotal", progressTotal);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.deserializeNBT(tag.getCompound("Items"));
        burnTime = tag.getInt("BurnTime");
        burnTimeTotal = tag.getInt("BurnTimeTotal");
        progress = tag.getInt("Progress");
        progressTotal = Math.max(1, tag.getInt("ProgressTotal"));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemCapability = LazyOptional.of(() -> items);
    }
}
