package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.PixelGuns;
import com.ultreon.mods.pixelguns.network.packet.c2s.play.GunReloadC2SPacket;
import com.ultreon.mods.pixelguns.network.packet.c2s.play.GunShootC2SPacket;
import com.ultreon.mods.pixelguns.network.packet.c2s.play.WorkshopCraftC2SPacket;
import com.ultreon.mods.pixelguns.network.packet.s2c.play.GrenadeExplodeS2CPacket;
import com.ultreon.mods.pixelguns.network.packet.s2c.play.GunCooldownS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class PacketRegistry {

    public static final Identifier GUN_RELOAD = PixelGuns.id("reload");
    public static final Identifier GUN_SHOOT = PixelGuns.id("shoot");
    public static final Identifier WORKSHOP_ASSEMBLE = PixelGuns.id("assemble");
    public static final Identifier GUN_COOLDOWN = PixelGuns.id("cooldown");
    public static final Identifier GRENADE_EXPLODE = PixelGuns.id("grenade_explode");

    public static class CLIENT {
        public static void registerPackets() {
            PacketRegistry.CLIENT.registerPacket(GUN_COOLDOWN, new GunCooldownS2CPacket());
            PacketRegistry.CLIENT.registerPacket(GRENADE_EXPLODE, new GrenadeExplodeS2CPacket());
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
        }

        private static void registerPacket(Identifier id, ServerPlayNetworking.PlayChannelHandler packetHandler) {
            ServerPlayNetworking.registerGlobalReceiver(id, packetHandler);
        }
    }
}
