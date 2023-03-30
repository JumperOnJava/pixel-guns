package com.ultreon.mods.pixelguns.event.forge;

import net.minecraft.entity.Entity;

public class EntityEvent extends Event {

    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
