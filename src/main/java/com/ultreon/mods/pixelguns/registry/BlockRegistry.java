package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.block.BottleBlock;
import com.ultreon.mods.pixelguns.block.WorkshopBlock;
import com.ultreon.mods.pixelguns.util.ResourcePath;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockRegistry {
    public static final Block WORKSHOP;
    public static final Block BOTTLE;

    static {
        WORKSHOP = BlockRegistry.register("workshop", new WorkshopBlock());
        BOTTLE = BlockRegistry.register("bottle", new BottleBlock(AbstractBlock.Settings.copy(Blocks.GLASS)));
    }

    private static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, ResourcePath.get(name), block);
    }

    @Environment(value = EnvType.CLIENT)
    public static class RENDERER {
        public static void registerBlockRenderers() {
            RENDERER.registerBlockRenderer(BlockRegistry.WORKSHOP, RenderLayer.getCutout());
        }

        private static void registerBlockRenderer(Block block, RenderLayer renderLayer) {
            BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer);
        }
    }
}