package com.ultreon.mods.pixelguns.item.gun.variant;

import com.ultreon.mods.pixelguns.registry.ItemRegistry;
import com.ultreon.mods.pixelguns.item.gun.GunItem;
import com.ultreon.mods.pixelguns.registry.SoundRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;

public class CombatShotgunItem extends GunItem {
    public CombatShotgunItem() {
        super(
            GunItem.AmmoLoadingType.SEMI_AUTOMATIC,
            5.5f,
            128,
            14,
            6,
            ItemRegistry.SHOTGUN_SHELL,
            26,
            9.25f,
            30.0f,
            5,
            LoadingType.INDIVIDUAL,
            new SoundEvent[] {SoundRegistry.RELOAD_COMBAT_SHOTGUN_P1, SoundRegistry.RELOAD_COMBAT_SHOTGUN_P2, SoundRegistry.RELOAD_COMBAT_SHOTGUN_P3},
            SoundRegistry.SHOTGUN_COMBAT,
            6,
            false,
            new int[] {1, 4, 13},
            new ItemStack[] {
                new ItemStack(Items.IRON_INGOT, 24)
            }
        );
    }
}
