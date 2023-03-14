package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.network.packet.c2s.play.*;
import com.ultreon.mods.pixelguns.network.packet.s2c.play.GunCooldownS2CPacket;
import com.ultreon.mods.pixelguns.network.packet.s2c.play.GunRecoilS2CPacket;
import com.ultreon.mods.pixelguns.util.ResourcePath;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class PacketRegistry {
    public static final Identifier GUN_RECOIL = ResourcePath.get("recoil");
    public static final Identifier GUN_RELOAD = ResourcePath.get("reload");
    public static final Identifier GUN_SHOOT = ResourcePath.get("shoot");
    public static final Identifier GUN_AIM = ResourcePath.get("aim");
    public static final Identifier WORKSHOP_ASSEMBLE = ResourcePath.get("assemble");
    public static final Identifier GUN_COOLDOWN_2_S = ResourcePath.get("cooldown_2_s");
    public static final Identifier GUN_COOLDOWN_2_C = ResourcePath.get("cooldown_2_c");

    public static class CLIENT {
        public static void registerPackets() {
            PacketRegistry.CLIENT.registerPacket(GUN_RECOIL, new GunRecoilS2CPacket());
            PacketRegistry.CLIENT.registerPacket(GUN_COOLDOWN_2_C, new GunCooldownS2CPacket());
        }

        private static void registerPacket(Identifier id, ClientPlayNetworking.PlayChannelHandler packetHandler) {
            ClientPlayNetworking.registerGlobalReceiver(id, packetHandler);
        }
    }

    public static class SERVER {

        public static void registerPackets() {
            PacketRegistry.SERVER.registerPacket(GUN_RELOAD, new GunReloadC2SPacket());
            PacketRegistry.SERVER.registerPacket(GUN_SHOOT, new GunShootC2SPacket());
            PacketRegistry.SERVER.registerPacket(WORKSHOP_ASSEMBLE, new WorkshopCraftC2SPacket());
            PacketRegistry.SERVER.registerPacket(GUN_COOLDOWN_2_S, new GunCooldownC2SPacket());
        }

        private static void registerPacket(Identifier id, ServerPlayNetworking.PlayChannelHandler packetHandler) {
            ServerPlayNetworking.registerGlobalReceiver(id, packetHandler);
        }
    }
}
