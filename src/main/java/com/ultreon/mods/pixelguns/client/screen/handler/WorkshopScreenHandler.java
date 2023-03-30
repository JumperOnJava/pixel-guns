package com.ultreon.mods.pixelguns.client.screen.handler;

import com.ultreon.mods.pixelguns.registry.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class WorkshopScreenHandler extends ScreenHandler {

    public WorkshopScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerRegistry.WORKSHOP_SCREEN_HANDLER, syncId);

        // Player's Hotbar
        int index = 0;
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, index++, 8 + 18 * x, 160));
        }

        // Player's Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, index++, 8 + 18 * x, 102 + 18 * y));
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (index == 0) {
                if (!this.insertItem(slotStack, 1, 36, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (index < 28) {
                    if (!this.insertItem(slotStack, 28, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index <= 36 && !this.insertItem(slotStack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            else {
                slot.markDirty();
            }

            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }

        return stack;
    }
}