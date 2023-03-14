package com.ultreon.mods.pixelguns.network.packet.c2s.play;

import com.ultreon.mods.pixelguns.network.packet.s2c.play.GunCooldownS2CPacket;
import com.ultreon.mods.pixelguns.registry.PacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class GunCooldownC2SPacket implements ServerPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = buf.readItemStack();
        float cooldown = buf.readFloat();
        server.execute(() -> {
            PacketByteBuf buf1 = PacketByteBufs.create();
            buf1.writeItemStack(stack);
            buf1.writeFloat(cooldown);

            for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking(player)) {
                ServerPlayNetworking.send(serverPlayer, PacketRegistry.GUN_COOLDOWN_2_C, buf1);
            }

            ServerPlayNetworking.send(player, PacketRegistry.GUN_COOLDOWN_2_C, buf1);
        });
    }
}
