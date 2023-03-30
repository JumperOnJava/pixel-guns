package com.ultreon.mods.pixelguns.client.handler;

import com.ultreon.mods.pixelguns.event.GunFireEvent;
import com.ultreon.mods.pixelguns.event.forge.TickEvent;
import com.ultreon.mods.pixelguns.item.gun.GunItem;
import com.ultreon.mods.pixelguns.registry.ConfigRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class RecoilHandler {

    private static float CAMERA_RECOIL;
    private static float PROGRESS_CAMERA_RECOIL;

    public static void onGunFire(GunFireEvent.Post event) {
        if (!event.isClient()) {
            return;
        }

        if (!ConfigRegistry.enable_recoil) {
            return;
        }

        ItemStack heldItem = event.getStack();
        GunItem gunItem = (GunItem) heldItem.getItem();
        CAMERA_RECOIL = gunItem.getRecoil() * getAdsRecoilReduction();
        PROGRESS_CAMERA_RECOIL = 0F;
    }

    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || CAMERA_RECOIL <= 0) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) {
            return;
        }

        if (!ConfigRegistry.enable_recoil) {
            return;
        }

        float recoilAmount = CAMERA_RECOIL * mc.getTickDelta() * 0.15F;
        float startProgress = PROGRESS_CAMERA_RECOIL / CAMERA_RECOIL;
        float endProgress = (PROGRESS_CAMERA_RECOIL + recoilAmount) / CAMERA_RECOIL;

        float pitch = mc.player.getPitch();
        if (startProgress < 0.2F) {
            mc.player.setPitch(pitch - ((endProgress - startProgress) / 0.2F) * CAMERA_RECOIL);
        }
        else {
            mc.player.setPitch(pitch + ((endProgress - startProgress) / 0.8F) * CAMERA_RECOIL);
        }

        PROGRESS_CAMERA_RECOIL += recoilAmount;

        if (PROGRESS_CAMERA_RECOIL >= CAMERA_RECOIL) {
            CAMERA_RECOIL = 0;
            PROGRESS_CAMERA_RECOIL = 0;
        }
    }

    public static float getAdsRecoilReduction() {
        return MinecraftClient.getInstance().options.useKey.isPressed() ? 0.5f : 1.0f;
    }
}