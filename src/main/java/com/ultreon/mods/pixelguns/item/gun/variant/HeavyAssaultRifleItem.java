package com.ultreon.mods.pixelguns.item.gun.variant;

import com.ultreon.mods.pixelguns.registry.ItemRegistry;
import com.ultreon.mods.pixelguns.item.gun.GunItem;
import com.ultreon.mods.pixelguns.registry.SoundRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;

public class HeavyAssaultRifleItem extends GunItem {

    public HeavyAssaultRifleItem() {
        super(
            GunItem.AmmoLoadingType.AUTOMATIC,
            8.0f,
            128,
            3,
            50,
            ItemRegistry.MEDIUM_BULLETS,
            48,
            0.125f,
            3.0f,
            1,
            LoadingType.CLIP,
            new SoundEvent[] {SoundRegistry.RELOAD_HEAVY_AR_P1, SoundRegistry.RELOAD_HEAVY_AR_P2, SoundRegistry.RELOAD_HEAVY_AR_P3},
            SoundRegistry.ASSAULTRIFLE_HEAVY,
            1,
            false,
            new int[] {6, 22, 40},
            new ItemStack[] {
                new ItemStack(Items.IRON_INGOT, 32)
            }
        );
    }
}
