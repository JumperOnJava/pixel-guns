package com.ultreon.mods.pixelguns.network.packet.s2c.play;

import com.ultreon.mods.pixelguns.PixelGunsClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class GunCooldownS2CPacket implements ClientPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = buf.readItemStack();
        float cooldown = buf.readFloat();
        PixelGunsClient.TRACKED_GUN_COOLDOWNS.put(stack.getItem(), cooldown);
    }
}
