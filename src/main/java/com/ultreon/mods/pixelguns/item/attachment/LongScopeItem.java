package com.ultreon.mods.pixelguns.item.attachment;

import com.ultreon.mods.pixelguns.util.WorkshopCraftable;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class LongScopeItem extends Item implements WorkshopCraftable {

    public LongScopeItem() {
        super(new FabricItemSettings());
    }

    @Override
    public ItemStack[] getIngredients() {
        return new ItemStack[]{
                new ItemStack(Items.GLOWSTONE_DUST, 1),
                new ItemStack(Items.IRON_INGOT, 8)
        };
    }
}
