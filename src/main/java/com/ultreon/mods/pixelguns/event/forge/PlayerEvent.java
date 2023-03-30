package com.ultreon.mods.pixelguns.event.forge;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerEvent extends LivingEvent {

    private final PlayerEntity player;

    public PlayerEvent(PlayerEntity player) {
        super(player);
        this.player = player;
    }

    @Override
    public PlayerEntity getEntity() {
        return player;
    }
}