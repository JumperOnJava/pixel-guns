package com.ultreon.mods.pixelguns.network.packet.s2c.play;

import com.ultreon.mods.pixelguns.registry.ConfigRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class GunRecoilS2CPacket implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        float kick = buf.readFloat();
        assert client.world != null;
        PlayerEntity player =  client.world.getPlayerByUuid(buf.readUuid());
        client.execute(() -> {
            if (ConfigRegistry.enable_recoil) {
                assert player != null;
                player.setPitch(kick);
            }
        });
    }
}