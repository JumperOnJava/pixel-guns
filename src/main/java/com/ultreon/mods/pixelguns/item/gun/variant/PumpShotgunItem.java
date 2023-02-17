package com.ultreon.mods.pixelguns.item.gun.variant;

import com.ultreon.mods.pixelguns.registry.ItemRegistry;
import com.ultreon.mods.pixelguns.item.gun.GunItem;
import com.ultreon.mods.pixelguns.registry.SoundRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;

public class PumpShotgunItem extends GunItem {
    public PumpShotgunItem() {
        super(
            GunItem.AmmoLoadingType.SEMI_AUTOMATIC,
            18.0f,
            128,
            5,
            5,
            ItemRegistry.SHOTGUN_SHELL,
            20,
            0.25f,
            30.0f,
            5,
            LoadingType.INDIVIDUAL,
            new SoundEvent[] {SoundRegistry.RELOAD_COMBAT_SHOTGUN_P1, SoundRegistry.RELOAD_COMBAT_SHOTGUN_P2, SoundRegistry.RELOAD_COMBAT_SHOTGUN_P3},
            SoundRegistry.COMBAT_SHOTGUN,
            5,
            false,
            new int[] {1, 4, 13},
            new ItemStack[] {
                new ItemStack(Items.IRON_INGOT, 24)
            }
        );
    }
}
