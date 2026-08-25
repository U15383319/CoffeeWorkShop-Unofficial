package net.langball.coffee.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.recipe.MachineRecipe;
import net.langball.coffee.registry.ModBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class MachineRecipeCategory implements IRecipeCategory<MachineRecipe> {
    private static final int WIDTH = 116;
    private static final int HEIGHT = 54;

    private final MachineBlock.Kind kind;
    private final RecipeType<MachineRecipe> recipeType;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public MachineRecipeCategory(MachineBlock.Kind kind, RecipeType<MachineRecipe> recipeType,
                                 IGuiHelper guiHelper) {
        this.kind = kind;
        this.recipeType = recipeType;
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(
                ModBlocks.get(CoffeeWorkJeiPlugin.machineBlockId(kind)).get()));
        this.arrow = guiHelper.createAnimatedRecipeArrow(100);
    }

    @Override
    public RecipeType<MachineRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.coffeework.category." + kind.serializedName());
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MachineRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(9, 19)
                .setStandardSlotBackground()
                .addIngredients(recipe.ingredient());
        builder.addOutputSlot(89, 19)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(MachineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        arrow.draw(graphics, 46, 19);
    }

    @Override
    public ResourceLocation getRegistryName(MachineRecipe recipe) {
        return recipe.getId();
    }
}
