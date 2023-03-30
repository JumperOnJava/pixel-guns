package com.ultreon.mods.pixelguns.event;

import com.ultreon.mods.pixelguns.event.forge.PlayerEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * <p>Fired when a player shoots a gun.</p>
 */
public class GunFireEvent extends PlayerEvent {

    private final ItemStack stack;

    public GunFireEvent(PlayerEntity player, ItemStack stack) {
        super(player);
        this.stack = stack;
    }

    /**
     * @return The stack the player was holding when firing the gun
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * @return Whether this event was fired on the client side
     */
    public boolean isClient() {
        return this.getEntity().getWorld().isClient();
    }

    /**
     * <p>Fired when a player is about to shoot a bullet.</p>
     */
    public static class Pre extends GunFireEvent {
        public Pre(PlayerEntity player, ItemStack stack) {
            super(player, stack);
        }
    }

    /**
     * <p>Fired after a player has shot a bullet.</p>
     */
    public static class Post extends GunFireEvent {
        public Post(PlayerEntity player, ItemStack stack) {
            super(player, stack);
        }
    }
}