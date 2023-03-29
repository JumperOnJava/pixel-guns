package com.ultreon.mods.pixelguns;

import com.ultreon.mods.pixelguns.registry.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import software.bernie.geckolib.network.GeckoLibNetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Environment(value = EnvType.CLIENT)
public class PixelGunsClient implements ClientModInitializer {

    public static final Map<UUID, Float> TRACKED_GUN_COOLDOWNS = new HashMap<>();

    public void onInitializeClient() {
        KeybindRegistry.registerKeybinds();

        EntityRegistry.RENDERER.registerEntityRenderers();
        BlockRegistry.RENDERER.registerBlockRenderers();
        ScreenHandlerRegistry.RENDERER.registerScreenRenderers();

        PacketRegistry.CLIENT.registerPackets();

        ModelPredicateRegistry.registerModelPredicates();

        GeckoLibNetwork.registerClientReceiverPackets();
    }

    public static void addOrUpdateTrackedGuns(UUID uuid, float cooldown) {
        if (PixelGunsClient.TRACKED_GUN_COOLDOWNS.replace(uuid, cooldown) == null) {
            PixelGunsClient.TRACKED_GUN_COOLDOWNS.put(uuid, cooldown);
        }
    }
}