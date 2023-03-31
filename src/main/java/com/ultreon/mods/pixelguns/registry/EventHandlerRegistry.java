package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.client.handler.RecoilHandler;
import com.ultreon.mods.pixelguns.event.GunEvents;

public class EventHandlerRegistry {
	public static void registerEventHandlers() {
		GunEvents.GUN_SHOT_POST.registerListener(RecoilHandler::onGunFire);
	}
}
