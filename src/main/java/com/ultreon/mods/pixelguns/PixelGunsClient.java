package com.ultreon.mods.pixelguns;

import com.ultreon.mods.pixelguns.registry.*;
import com.ultreon.mods.pixelguns.registry.ModelPredicateRegistry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import software.bernie.geckolib.network.GeckoLibNetwork;

import java.util.HashMap;
import java.util.Map;

@Environment(value = EnvType.CLIENT)
public class PixelGunsClient implements ClientModInitializer {

    public static final Map<Item, Float> TRACKED_GUN_COOLDOWNS = new HashMap<>();

    public void onInitializeClient() {
        KeybindRegistry.registerKeybinds();

        EntityRegistry.RENDERER.registerEntityRenderers();
        BlockRegistry.RENDERER.registerBlockRenderers();
        ScreenHandlerRegistry.RENDERER.registerScreenRenderers();

        PacketRegistry.CLIENT.registerPackets();

        ModelPredicateRegistry.registerModelPredicates();

        GeckoLibNetwork.registerClientReceiverPackets();
    }
}