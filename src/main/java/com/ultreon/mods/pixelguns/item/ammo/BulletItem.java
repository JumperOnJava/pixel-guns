package com.ultreon.mods.pixelguns.item.ammo;

import com.ultreon.mods.pixelguns.util.WorkshopCraftable;
import net.minecraft.item.Item;

public abstract class BulletItem extends Item implements WorkshopCraftable {

	public BulletItem(Settings settings) {
		super(settings);
	}
}
