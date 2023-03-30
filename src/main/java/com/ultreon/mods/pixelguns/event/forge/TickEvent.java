package com.ultreon.mods.pixelguns.event.forge;

import com.ultreon.mods.pixelguns.util.LogicalSide;

public class TickEvent extends Event {

    public final Type type;
    public final LogicalSide side;
    public final Phase phase;

    public TickEvent(Type type, LogicalSide side, Phase phase) {
        this.type = type;
        this.side = side;
        this.phase = phase;
    }

    public static class RenderTickEvent extends TickEvent {

        public final float renderTickTime;

        public RenderTickEvent(Phase phase, float renderTickTime) {
            super(Type.RENDER, LogicalSide.CLIENT, phase);
            this.renderTickTime = renderTickTime;
        }
    }

    public enum Type {
        LEVEL, PLAYER, CLIENT, SERVER, RENDER
    }

    public enum Phase {
        START, END
    }
}
