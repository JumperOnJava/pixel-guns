package com.ultreon.mods.pixelguns.network.packet.c2s.play;

import com.ultreon.mods.pixelguns.registry.PacketRegistry;
import com.ultreon.mods.pixelguns.util.InventoryUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ItemScatterer;

import java.util.ArrayList;
import java.util.List;

public class WorkshopCraftC2SPacket implements ServerPlayNetworking.PlayChannelHandler {

    public static void send(ItemStack[] ingredients, ItemStack result) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(ingredients.length);

		for (ItemStack ingredient : ingredients) {
			buf.writeItemStack(ingredient);
		}

        buf.writeItemStack(result);
        ClientPlayNetworking.send(PacketRegistry.WORKSHOP_ASSEMBLE, buf);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        List<ItemStack> ingredients = new ArrayList<>();
        int count = buf.readInt();

        for (int i = 0; i < count; i++) {
            ingredients.add(buf.readItemStack());
        }

        InventoryUtil.removeStacks(player, ingredients.toArray(new ItemStack[0]));
        ItemScatterer.spawn(player.world, player.getX(), player.getY(), player.getZ(), buf.readItemStack());
    }
}
