package net.langball.coffee.compat.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.menu.MachineGuiLayout;
import net.langball.coffee.menu.MachineMenu;
import net.langball.coffee.recipe.MachineRecipe;
import net.langball.coffee.registry.ModMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class MachineRecipeTransferHandler implements IRecipeTransferHandler<MachineMenu, MachineRecipe> {
    private static final int RECIPE_SLOT_START = 0;
    private static final int RECIPE_SLOT_COUNT = 1;
    private static final int INVENTORY_SLOT_COUNT = 36;

    private final MachineBlock.Kind kind;
    private final RecipeType<MachineRecipe> recipeType;
    private final IRecipeTransferHandlerHelper helper;
    private final IRecipeTransferHandler<MachineMenu, MachineRecipe> delegate;

    public MachineRecipeTransferHandler(MachineBlock.Kind kind, RecipeType<MachineRecipe> recipeType,
                                        IRecipeTransferHandlerHelper helper) {
        this.kind = kind;
        this.recipeType = recipeType;
        this.helper = helper;
        this.delegate = helper.createUnregisteredRecipeTransferHandler(
                helper.createBasicRecipeTransferInfo(
                        MachineMenu.class,
                        ModMenus.MACHINE.get(),
                        recipeType,
                        RECIPE_SLOT_START,
                        RECIPE_SLOT_COUNT,
                        MachineGuiLayout.forKind(kind).machineSlotCount(),
                        INVENTORY_SLOT_COUNT));
    }

    @Override
    public Class<? extends MachineMenu> getContainerClass() {
        return MachineMenu.class;
    }

    @Override
    public Optional<MenuType<MachineMenu>> getMenuType() {
        return Optional.of(ModMenus.MACHINE.get());
    }

    @Override
    public RecipeType<MachineRecipe> getRecipeType() {
        return recipeType;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(MachineMenu menu, MachineRecipe recipe,
                                               IRecipeSlotsView recipeSlots, Player player,
                                               boolean maxTransfer, boolean doTransfer) {
        if (menu.kind() != kind || recipe.kind() != kind) {
            return helper.createUserErrorWithTooltip(
                    Component.translatable("jei.coffeework.transfer.wrong_machine"));
        }
        return delegate.transferRecipe(menu, recipe, recipeSlots, player, maxTransfer, doTransfer);
    }
}
