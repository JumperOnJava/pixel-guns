package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.block.WorkshopBlock;
import com.ultreon.mods.pixelguns.util.ResourcePath;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {
    public static final Block WORKSHOP = BlockRegistry.register("workshop", new WorkshopBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5f).sounds(BlockSoundGroup.WOOD).nonOpaque()));

    public static void registerBlockRenderers() {
        BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistry.WORKSHOP, RenderLayer.getCutout());
    }

    private static Block register(String name, Block block) {
        return Registry.register(Registry.BLOCK, ResourcePath.get(name), block);
    }
}