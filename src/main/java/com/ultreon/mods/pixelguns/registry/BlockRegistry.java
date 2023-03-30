package com.ultreon.mods.pixelguns.registry;

import com.ultreon.mods.pixelguns.PixelGuns;
import com.ultreon.mods.pixelguns.block.BottleBlock;
import com.ultreon.mods.pixelguns.block.WorkshopBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockRegistry {

    public static final Block WORKSHOP = BlockRegistry.register("workshop", new WorkshopBlock());
    public static final Block LIME_BOTTLE = BlockRegistry.register("lime_bottle", new BottleBlock(AbstractBlock.Settings.copy(Blocks.GLASS)));
    public static final Block LEMON_BOTTLE = BlockRegistry.register("lemon_bottle", new BottleBlock(AbstractBlock.Settings.copy(Blocks.GLASS)));
    public static final Block ORANGE_BOTTLE = BlockRegistry.register("orange_bottle", new BottleBlock(AbstractBlock.Settings.copy(Blocks.GLASS)));

    public static void init() {}

    private static Block register(String name, Block block) {
        return Registry.register(Registries.BLOCK, PixelGuns.id(name), block);
    }
}