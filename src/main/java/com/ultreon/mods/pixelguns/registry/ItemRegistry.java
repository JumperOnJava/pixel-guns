package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.armor.ArmoredArmor;
import com.ultreon.mods.pixelguns.item.*;
import com.ultreon.mods.pixelguns.item.ammo.variant.RocketItem;
import com.ultreon.mods.pixelguns.item.ammo.variant.*;
import com.ultreon.mods.pixelguns.item.attachment.*;
import com.ultreon.mods.pixelguns.item.gun.variant.*;
import com.ultreon.mods.pixelguns.util.ResourcePath;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ItemRegistry {

    // Ammunition
    public static final Item SHOTGUN_SHELL = ItemRegistry.register("shotgun_shell", new ShotgunShellItem());
    public static final Item LIGHT_BULLETS = ItemRegistry.register("light_bullets", new LightBulletsItem());
    public static final Item MEDIUM_BULLETS = ItemRegistry.register("medium_bullets", new MediumBulletsItem());
    public static final Item HEAVY_BULLETS = ItemRegistry.register("heavy_bullets", new HeavyBulletsItem());
    public static final Item ROCKET = ItemRegistry.register("rocket", new RocketItem());
    public static final Item ENERGY_BATTERY = ItemRegistry.register("energy_battery", new EnergyBatteryItem());

    // Guns
    public static final Item PISTOL = ItemRegistry.register("pistol", new PistolItem());
    public static final Item REVOLVER = ItemRegistry.register("revolver", new RevolverItem());
    public static final Item COMBAT_SHOTGUN = ItemRegistry.register("combat_shotgun", new CombatShotgunItem());
    public static final Item SUBMACHINE_GUN = ItemRegistry.register("submachine_gun", new SubmachineGunItem());
    public static final Item LIGHT_ASSAULT_RIFLE = ItemRegistry.register("light_assault_rifle", new LightAssaultRifleItem());
    public static final Item ASSAULT_RIFLE = ItemRegistry.register("assault_rifle", new AssaultRifleItem());
    public static final Item SNIPER_RIFLE = ItemRegistry.register("sniper_rifle", new SniperRifleItem());
    public static final Item ROCKET_LAUNCHER = ItemRegistry.register("rocket_launcher", new RocketLauncherItem());
    public static final Item INFINITY_GUN = ItemRegistry.register("infinity_gun", new InfinityGunItem());

    // Armor
    public static final Item ARMORED_VEST = ItemRegistry.register("armored_vest", new ArmoredArmor(EquipmentSlot.CHEST));
    public static final Item GAS_MASK = ItemRegistry.register("gas_mask", new GasMaskItem());

    // Weapons
    public static final Item KATANA = ItemRegistry.register("katana", new KatanaItem());
    public static final Item CROWBAR = ItemRegistry.register("crowbar", new CrowbarItem());
    public static final Item GRENADE = ItemRegistry.register("grenade", new GrenadeItem());

    // Attachments
    public static final Item SHORT_SCOPE = ItemRegistry.register("short_scope", new ShortScopeItem());
    public static final Item MEDIUM_SCOPE = ItemRegistry.register("medium_scope", new MediumScopeItem());
    public static final Item LONG_SCOPE = ItemRegistry.register("long_scope", new LongScopeItem());
    public static final Item HEAVY_STOCK = ItemRegistry.register("heavy_stock", new HeavyStockItem());
    public static final Item SPECIALISED_GRIP = ItemRegistry.register("specialised_grip", new SpecialisedGripItem());

    // Block Items
    public static final Item WORKSHOP = ItemRegistry.register(BlockRegistry.WORKSHOP, ItemGroupRegistry.MISC);
    public static final Item BOTTLE = ItemRegistry.register(BlockRegistry.BOTTLE, ItemGroupRegistry.MISC);

    public static final Item POLICE_SHIELD = ItemRegistry.register("police_shield", new ShieldItem(new Item.Settings().maxCount(800)));

    private static Item register(Block block, ItemGroup itemGroup) {
        BlockItem blockItem = new BlockItem(block, new Item.Settings());
        return ItemRegistry.register(Registries.BLOCK.getId(blockItem.getBlock()), blockItem);
    }

    private static Item register(String name, Item item) {
        return ItemRegistry.register(ResourcePath.get(name), item);
    }

    private static Item register(Identifier identifier, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
        }
        return Registry.register(Registries.ITEM, identifier, item);
    }
}