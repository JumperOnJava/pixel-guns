package com.ultreon.mods.pixelguns.item.ammo.variant;

import com.ultreon.mods.pixelguns.item.ammo.BulletItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class HeavyBulletsItem extends BulletItem {

    public HeavyBulletsItem() {
        super(new FabricItemSettings().maxCount(64));
    }

    @Override
    public ItemStack[] getIngredients() {
        return List.of(
                new ItemStack(Items.COPPER_INGOT, 4),
                new ItemStack(Items.GUNPOWDER, 1)
        ).toArray(new ItemStack[0]);
    }
}
