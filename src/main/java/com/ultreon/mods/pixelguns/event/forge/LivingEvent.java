package com.ultreon.mods.pixelguns.event.forge;

import net.minecraft.entity.LivingEntity;

public class LivingEvent extends EntityEvent {

    private final LivingEntity livingEntity;

    public LivingEvent(LivingEntity entity) {
        super(entity);
        livingEntity = entity;
    }

    @Override
    public LivingEntity getEntity() {
        return livingEntity;
    }
}
