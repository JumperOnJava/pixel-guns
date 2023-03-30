package com.ultreon.mods.pixelguns.item.ammo.variant;

import com.ultreon.mods.pixelguns.item.ammo.BulletItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class EnergyBatteryItem extends BulletItem {

    public EnergyBatteryItem() {
        super(new FabricItemSettings().maxCount(12));
    }

    @Override
    public boolean hasGlint(ItemStack itemStack) {
        return true;
    }

    @Override
    public ItemStack[] getIngredients() {
        return List.of(
                new ItemStack(Items.IRON_INGOT, 8),
                new ItemStack(Items.LIGHTNING_ROD, 1)
        ).toArray(new ItemStack[0]);
    }
}
