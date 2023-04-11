package com.ultreon.mods.pixelguns.block;

import com.ultreon.mods.pixelguns.client.screen.handler.WorkshopScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

// TODO Fix adjacent blocks preventing saw from being rendered
public class WorkshopBlock extends EasyBlock {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final Text TITLE = Text.translatable("container.pixel_guns.workshop");
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    public WorkshopBlock() {
        super(FabricBlockSettings.of(Material.WOOD).strength(2.5f).sounds(BlockSoundGroup.WOOD).nonOpaque());
        setDefaultState(
                getDefaultState()
                .with(WATERLOGGED, false)
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
        );
    }

    @Override
    protected boolean isWaterloggable() {
        return true;
    }

    @Override
    protected boolean isRotatable() {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.CONSUME;
        }
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) -> new WorkshopScreenHandler(syncId, playerInventory), TITLE);
    }
}
