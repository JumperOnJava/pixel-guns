package com.ultreon.mods.pixelguns.item.recipe;

import com.ultreon.mods.pixelguns.registry.RecipeRegistry;
import com.ultreon.mods.pixelguns.registry.WorkshopTabsRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkshopRecipe implements Recipe<Inventory> {

    private final Identifier id;
    private final WorkshopTabsRegistry.WorkshopTab tab;
    private final DefaultedList<Pair<Ingredient, Integer>> ingredients;
    private final ItemStack result;

    public WorkshopRecipe(Identifier id, WorkshopTabsRegistry.WorkshopTab tab, DefaultedList<Pair<Ingredient, Integer>> ingredients, ItemStack result) {
        this.id = id;
        this.tab = tab;
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        RecipeMatcher matcher = new RecipeMatcher();
        int stackCount = 0;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            for (Pair<Ingredient, Integer> pair : ingredients) {
                if (!stack.isEmpty() && pair.getLeft().test(stack)) {
                    if (stack.getCount() >= pair.getRight()) {
                        stackCount++;
                    }
                }
            }
        }

        return stackCount == ingredients.size() && matcher.match(this, null);
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return result;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.WORKSHOP_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.WORKSHOP_RECIPE_TYPE;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public WorkshopTabsRegistry.WorkshopTab getTab() {
        return tab;
    }

    public List<Pair<Ingredient, Integer>> getIngredientPairs() {
        return ingredients;
    }
}
