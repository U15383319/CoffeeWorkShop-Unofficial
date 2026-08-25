package net.langball.coffee.menu;

import net.langball.coffee.block.entity.MachineBlockEntity;
import net.langball.coffee.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public final class MachineMenu extends AbstractContainerMenu {
    private final MachineBlockEntity machine;
    private final ContainerData data;
    private final MachineGuiLayout layout;

    public MachineMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerId, inventory,
                (MachineBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()),
                new SimpleContainerData(MachineBlockEntity.DATA_COUNT));
    }

    public MachineMenu(int containerId, Inventory inventory, MachineBlockEntity machine, ContainerData data) {
        super(ModMenus.MACHINE.get(), containerId);
        this.machine = machine;
        this.data = data;
        this.layout = MachineGuiLayout.forKind(machine.kind());

        addSlot(new SlotItemHandler(machine.items(), MachineBlockEntity.SLOT_INPUT,
                layout.inputX(), layout.inputY()));
        if (layout.hasFuelSlot()) {
            addSlot(new SlotItemHandler(machine.items(), MachineBlockEntity.SLOT_FUEL,
                    layout.fuelX(), layout.fuelY()));
        }
        addSlot(new SlotItemHandler(machine.items(), MachineBlockEntity.SLOT_OUTPUT,
                layout.outputX(), layout.outputY()) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack source = slot.getItem();
        ItemStack copy = source.copy();
        if (index < layout.machineSlotCount()) {
            if (!moveItemStackTo(source, layout.machineSlotCount(), slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(source, 0, layout.insertionSlotEnd(), false)) {
            return ItemStack.EMPTY;
        }

        if (source.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return machine != null && machine.stillValid(player);
    }

    public int progressPixels(int width) {
        int total = data.get(3);
        return total <= 0 ? 0 : data.get(2) * width / total;
    }

    public int burnPixels(int height) {
        int total = data.get(1);
        return total <= 0 ? 0 : data.get(0) * height / total;
    }

    public net.langball.coffee.block.MachineBlock.Kind kind() {
        return machine == null ? net.langball.coffee.block.MachineBlock.Kind.GRINDER : machine.kind();
    }

    public MachineGuiLayout layout() {
        return layout;
    }
}
