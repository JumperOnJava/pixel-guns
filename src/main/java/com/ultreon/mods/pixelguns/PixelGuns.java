package com.ultreon.mods.pixelguns;

import com.ultreon.mods.pixelguns.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PixelGuns implements ModInitializer {
    public static final String MOD_ID = "pixel_guns";
    public static final Logger LOGGER = LoggerFactory.getLogger("pixel_guns");

    public void onInitialize() {
        new EntityRegistry();
        new ItemRegistry();
        new BlockRegistry();
        new SoundRegistry();
        new RecipeRegistry();
        new ScreenHandlerRegistry();

        EventHandlerRegistry.registerEventHandlers();
        PacketRegistry.SERVER.registerPackets();
        ConfigRegistry.registerConfig();
        ItemGroupRegistry.registerItemGroups();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            client.execute(PixelGunsClient.TRACKED_GUN_COOLDOWNS::clear);
        });
    }
}