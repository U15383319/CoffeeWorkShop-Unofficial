package net.langball.coffee.recipe;

import com.google.gson.JsonObject;
import net.langball.coffee.CoffeeConfig;
import net.langball.coffee.block.MachineBlock;
import net.langball.coffee.registry.ModRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class MachineRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final MachineBlock.Kind kind;
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int processingTime;
    private final float experience;

    public MachineRecipe(ResourceLocation id, MachineBlock.Kind kind, Ingredient ingredient,
                         ItemStack result, int processingTime, float experience) {
        this.id = id;
        this.kind = kind;
        this.ingredient = ingredient;
        this.result = result;
        this.processingTime = processingTime;
        this.experience = experience;
    }

    @Override
    public boolean matches(SimpleContainer container, net.minecraft.world.level.Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.serializer(kind).get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.type(kind).get();
    }

    public int processingTime() {
        return processingTime;
    }

    public MachineBlock.Kind kind() {
        return kind;
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public ItemStack result() {
        return result.copy();
    }

    public float experience() {
        return experience;
    }

    public static final class Serializer implements RecipeSerializer<MachineRecipe> {
        private final MachineBlock.Kind kind;

        public Serializer(MachineBlock.Kind kind) {
            this.kind = kind;
        }

        @Override
        public MachineRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            JsonObject resultObject = GsonHelper.getAsJsonObject(json, "result");
            ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(resultObject, "item"));
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) {
                throw new IllegalArgumentException("Unknown machine recipe result item " + itemId + " in " + id);
            }
            int count = GsonHelper.getAsInt(resultObject, "count", 1);
            int time = GsonHelper.getAsInt(json, "processing_time", CoffeeConfig.MACHINE_TIME.get());
            float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);
            return new MachineRecipe(id, kind, ingredient, new ItemStack(item, count), time, experience);
        }

        @Nullable
        @Override
        public MachineRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            int time = buffer.readVarInt();
            float experience = buffer.readFloat();
            return new MachineRecipe(id, kind, ingredient, result, time, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MachineRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.processingTime);
            buffer.writeFloat(recipe.experience);
        }
    }
}
