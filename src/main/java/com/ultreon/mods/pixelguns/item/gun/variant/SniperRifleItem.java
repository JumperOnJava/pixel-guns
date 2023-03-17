package com.ultreon.mods.pixelguns.item.gun.variant;

import com.ultreon.mods.pixelguns.item.gun.GunItem;
import com.ultreon.mods.pixelguns.registry.ItemRegistry;
import com.ultreon.mods.pixelguns.registry.SoundRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

public class SniperRifleItem extends GunItem {
    public SniperRifleItem() {
        super(
            false,
            25.0f,
            128,
            20,
            5,
            ItemRegistry.HEAVY_BULLETS,
            26,
            0.01f,
            25.0f,
            1,
            LoadingType.INDIVIDUAL,
            SoundRegistry.SNIPER_RIFLE_RELOAD,
            SoundRegistry.SNIPER_RIFLE_FIRE,
            5,
            true,
            new int[] {1, 8, 17},
            new ItemStack[] {
                new ItemStack(Items.IRON_INGOT, 45),
                new ItemStack(Items.OAK_PLANKS, 5),
                new ItemStack(Items.IRON_INGOT, 5)
            }
        );
    }
}
