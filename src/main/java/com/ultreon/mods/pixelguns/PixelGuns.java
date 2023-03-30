package com.ultreon.mods.pixelguns;

import com.ultreon.mods.pixelguns.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PixelGuns implements ModInitializer {

    public static final String MOD_ID = "pixel_guns";
    public static final Logger LOGGER = LoggerFactory.getLogger("pixel_guns");

    public void onInitialize() {
        EntityRegistry.init();
        ItemRegistry.init();
        BlockRegistry.init();
        SoundRegistry.init();
        RecipeRegistry.init();
        ScreenHandlerRegistry.init();

        EventHandlerRegistry.registerEventHandlers();
        PacketRegistry.SERVER.registerPackets();
        ConfigRegistry.registerConfig();
        ItemGroupRegistry.registerItemGroups();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            client.execute(PixelGunsClient.TRACKED_GUN_COOLDOWNS::clear);
        });
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}