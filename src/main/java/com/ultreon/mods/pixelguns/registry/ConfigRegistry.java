package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.PixelGuns;
import eu.midnightdust.lib.config.MidnightConfig;

public class ConfigRegistry extends MidnightConfig {

    @Entry
    public static boolean enable_recoil = true;

    public static void registerConfig() {
        init(PixelGuns.MOD_ID, ConfigRegistry.class);
    }
}
