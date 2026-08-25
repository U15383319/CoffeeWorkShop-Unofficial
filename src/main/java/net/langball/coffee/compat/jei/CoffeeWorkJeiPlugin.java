package net.langball.coffee.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.client.screen.MachineScreen;
import net.langball.coffee.menu.MachineGuiLayout;
import net.langball.coffee.recipe.MachineRecipe;
import net.langball.coffee.registry.ModBlocks;
import net.langball.coffee.registry.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public final class CoffeeWorkJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
            new ResourceLocation(CoffeeWork.MODID, "jei_plugin");
    private static final Map<MachineBlock.Kind, RecipeType<MachineRecipe>> RECIPE_TYPES =
            new EnumMap<>(MachineBlock.Kind.class);

    static {
        for (MachineBlock.Kind kind : MachineBlock.Kind.values()) {
            RECIPE_TYPES.put(kind, RecipeType.create(
                    CoffeeWork.MODID, kind.serializedName(), MachineRecipe.class));
        }
    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        for (MachineBlock.Kind kind : MachineBlock.Kind.values()) {
            registration.addRecipeCategories(new MachineRecipeCategory(kind, recipeType(kind), guiHelper));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            CoffeeWork.LOGGER.warn("JEI requested Coffee Workshop recipes before a client world was available");
            return;
        }
        int recipeCount = 0;
        for (MachineBlock.Kind kind : MachineBlock.Kind.values()) {
            List<MachineRecipe> recipes = level.getRecipeManager()
                    .getAllRecipesFor(ModRecipes.type(kind).get());
            registration.addRecipes(recipeType(kind), recipes);
            recipeCount += recipes.size();
        }
        CoffeeWork.LOGGER.info("Registered {} machine recipes with JEI", recipeCount);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (MachineBlock.Kind kind : MachineBlock.Kind.values()) {
            registration.addRecipeCatalyst(ModBlocks.get(machineBlockId(kind)).get(), recipeType(kind));
        }
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        for (MachineBlock.Kind kind : MachineBlock.Kind.values()) {
            RecipeType<MachineRecipe> recipeType = recipeType(kind);
            registration.addRecipeTransferHandler(
                    new MachineRecipeTransferHandler(kind, recipeType, registration.getTransferHelper()),
                    recipeType);
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(MachineScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(
                    MachineScreen screen, double mouseX, double mouseY) {
                MachineBlock.Kind kind = screen.getMenu().kind();
                MachineGuiLayout layout = MachineGuiLayout.forKind(kind);
                return List.of(IGuiClickableArea.createBasic(
                        layout.progressX(), layout.progressY(),
                        layout.progressWidth(), layout.progressHeight(), recipeType(kind)));
            }
        });
    }

    static RecipeType<MachineRecipe> recipeType(MachineBlock.Kind kind) {
        return RECIPE_TYPES.get(kind);
    }

    static String machineBlockId(MachineBlock.Kind kind) {
        return switch (kind) {
            case GRINDER -> "grinder_off";
            case COFFEE_MACHINE -> "coffeemachine_off";
            case ICECREAM_MACHINE -> "icecreammachine_off";
            case ROLLER -> "roller_off";
            case OVEN -> "oven_off";
        };
    }
}
