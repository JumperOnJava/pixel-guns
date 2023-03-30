package com.ultreon.mods.pixelguns.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(value = EnvType.CLIENT)
public class KeyBindRegistry {

    public static final KeyBinding RELOAD_KEY = new KeyBinding("key.pixel_guns.reload", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.pixel_guns.binds");

    public static void registerKeyBinds() {
        KeyBindingHelper.registerKeyBinding(RELOAD_KEY);
    }
}
