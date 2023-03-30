package com.ultreon.mods.pixelguns.event.forge;

public interface EventHandler<T extends Event> {

	void onEvent(T event);
}
