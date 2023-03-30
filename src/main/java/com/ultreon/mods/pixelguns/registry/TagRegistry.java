package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.PixelGuns;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class TagRegistry {

	public static final TagKey<Item> GUNS = TagRegistry.registerItem("guns");
	public static final TagKey<Item> AMMUNITION = TagRegistry.registerItem("ammunition");
	public static final TagKey<Item> ATTACHMENTS = TagRegistry.registerItem("attachments");

	private static TagKey<Item> registerItem(String name) {
		return TagKey.of(RegistryKeys.ITEM, PixelGuns.id(name));
	}
}
