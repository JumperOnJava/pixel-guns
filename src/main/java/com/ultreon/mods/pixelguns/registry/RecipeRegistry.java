package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.PixelGuns;
import com.ultreon.mods.pixelguns.item.recipe.ArmoredVestRecipe;
import com.ultreon.mods.pixelguns.item.recipe.RepairArmoredVest;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class RecipeRegistry {

    public static final RecipeSerializer<?> ARMORED_VEST = register("armored_vest", new SpecialRecipeSerializer<>(ArmoredVestRecipe::new));
    public static final RecipeSerializer<?> REPAIR_ARMORED_VEST = register("repair_armored_vest", new SpecialRecipeSerializer<>(RepairArmoredVest::new));

    public static void init() {}

    private static RecipeSerializer<?> register(String name, RecipeSerializer<?> serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, PixelGuns.id(name), serializer);
    }
}
