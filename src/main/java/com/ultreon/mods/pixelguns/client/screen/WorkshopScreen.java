package com.ultreon.mods.pixelguns.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import com.ultreon.mods.pixelguns.PixelGuns;
import com.ultreon.mods.pixelguns.client.screen.handler.WorkshopScreenHandler;
import com.ultreon.mods.pixelguns.network.packet.c2s.play.WorkshopCraftC2SPacket;
import com.ultreon.mods.pixelguns.registry.ItemRegistry;
import com.ultreon.mods.pixelguns.registry.TagRegistry;
import com.ultreon.mods.pixelguns.util.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.minecraft.util.math.MathHelper;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkshopScreen extends HandledScreen<WorkshopScreenHandler> {

    private static final Identifier TEXTURE = PixelGuns.id("textures/gui/container/workshop.png");

    private Tab currentTab;
    private final List<Tab> tabs = new ArrayList<>();
    private List<ItemStack> materials;
//    private List<MaterialItem> filteredMaterials;
    private final PlayerInventory playerInventory;
    // private WorkbenchBlockEntity workbench;
    private ButtonWidget btnCraft;
//    private CheckBox checkBoxMaterials;
    private final Map<Tab, CircularLinkedList<ItemStack>> recipes = new HashMap<>();

    public WorkshopScreen(WorkshopScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);

        this.playerInventory = playerInventory;

        this.backgroundWidth = 275;
        this.backgroundHeight = 184;

        this.playerInventoryTitleX = this.x + 8;
        this.playerInventoryTitleY = this.y + 91;

        // Populate tabs
        tabs.add(Tabs.GUNS);
        tabs.add(Tabs.AMMUNITION);
        tabs.add(Tabs.ATTACHMENTS);

        // Populate guns
        recipes.put(Tabs.GUNS, new CircularLinkedList<>());
        for (RegistryEntry<Item> gun : Registries.ITEM.iterateEntries(TagRegistry.GUNS)) {
            recipes.get(Tabs.GUNS).add(gun.value().getDefaultStack());
        }

        // Populate ammo
        recipes.put(Tabs.AMMUNITION, new CircularLinkedList<>());
        for (RegistryEntry<Item> bullet : Registries.ITEM.iterateEntries(TagRegistry.AMMUNITION)) {
            recipes.get(Tabs.AMMUNITION).add(bullet.value().getDefaultStack());
        }

        // Populate attachments
        recipes.put(Tabs.ATTACHMENTS, new CircularLinkedList<>());
        for (RegistryEntry<Item> bullet : Registries.ITEM.iterateEntries(TagRegistry.ATTACHMENTS)) {
            recipes.get(Tabs.ATTACHMENTS).add(bullet.value().getDefaultStack());
        }

        currentTab = Tabs.GUNS;
        this.materials = List.of(((WorkshopCraftable) recipes.get(currentTab).currentElement().getItem()).getIngredients());
    }

    @Override
    protected void init() {
        super.init();
        // Left Arrow
        this.addDrawableChild(new ButtonWidget(this.x + 9, this.y + 18, 15, 20, Text.literal("<"), button -> {
            recipes.get(currentTab).prev();
            this.materials = List.of(((WorkshopCraftable) this.getDisplayStack().getItem()).getIngredients());
        }, a -> Text.translatable("gui.pixel_guns.previous_item")));

        // Right Arrow
        this.addDrawableChild(new ButtonWidget(this.x + 153, this.y + 18, 15, 20, Text.literal(">"), button -> {
            recipes.get(currentTab).next();
            this.materials = List.of(((WorkshopCraftable) this.getDisplayStack().getItem()).getIngredients());
        }, a -> Text.translatable("gui.pixel_guns.next_item")));

        // Assemble Button
        this.btnCraft = this.addDrawableChild(new ButtonWidget(this.x + 195, this.y + 16, 74, 20, Text.translatable("gui.pixel_guns.assemble"), button -> {
            WorkshopCraftable currentItem = (WorkshopCraftable) this.getDisplayStack().getItem();
            WorkshopCraftC2SPacket.send(currentItem.getIngredients(), this.getDisplayStack());
        }, a -> Text.translatable("gui.pixel_guns.assemble_item")));

        // Disable the Assemble Button
        this.btnCraft.active = false;
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();

        boolean canAssemble = true;

        WorkshopCraftable workshopItem = (WorkshopCraftable) this.getDisplayStack().getItem();

        for (ItemStack stack : workshopItem.getIngredients()) {
            if (InventoryUtil.itemCountInInventory(playerInventory.player, stack.getItem()) < stack.getCount()) {
                canAssemble = false;
                break;
            }
        }

        this.btnCraft.active = canAssemble;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);

        for (int i = 0; i < this.tabs.size(); i++) {
            if (RenderUtil.isMouseWithin((int) mouseX, (int) mouseY, this.x + 28 * i, this.y - 28, 28, 28)) {
                this.currentTab = this.tabs.get(i);
                this.recipes.get(currentTab).resetIndex(); // TODO see if this is required
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        return result;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack, int i, int j) {
        this.textRenderer.draw(matrixStack, this.title, this.titleX, this.titleY, 4210752);
        this.textRenderer.draw(matrixStack, this.playerInventory.getDisplayName(), this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.enableBlend();

        renderUnselectedTabs(matrices);
        renderBackgroundTexture(matrices);
        renderSelectedTab(matrices);

        renderCurrentItem(matrices);
        renderItemName(matrices);

        renderIngredients(matrices);
    }

    private void renderSelectedTab(MatrixStack matrices) {
        if (this.currentTab != null)
        {
            int i = this.tabs.indexOf(this.currentTab);
            int u = i == 0 ? 80 : 108;
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, TEXTURE);
            this.drawTexture(matrices, this.x + 28 * i, this.y - 28, u, 214, 28, 32);
            MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(this.currentTab.icon(), this.x + 28 * i + 6, this.y - 28 + 8);
            MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(this.textRenderer, this.currentTab.icon(), this.x + 28 * i + 6, this.y - 28 + 8, null);
        }
    }

    private void renderUnselectedTabs(MatrixStack matrices) {
        for (int i = 0; i < this.tabs.size(); i++)
        {
            Tab tab = this.tabs.get(i);
            if (tab != this.currentTab)
            {
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, TEXTURE);
                this.drawTexture(matrices, this.x + 28 * i, this.y - 28, 80, 184, 28, 32);
                MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(tab.icon(), this.x + 28 * i + 6, this.y - 28 + 8);
                MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(this.textRenderer, tab.icon(), this.x + 28 * i + 6, this.y - 28 + 8, null);
            }
        }
    }

    private void renderIngredients(MatrixStack matrices) {
        for (int i = 0; i < this.materials.size(); i++) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, TEXTURE);

            ItemStack stack = this.materials.get(i);
            if(!stack.isEmpty()) {
                DiffuseLighting.disableGuiDepthLighting();
                if (true/*materialItem.isEnabled()*/) {
                    this.drawTexture(matrices, this.x + 172, this.y + i * 19 + 63, 0, 184, 80, 19);
                } else {
                    this.drawTexture(matrices, this.x + 172, this.y + i * 19 + 63, 0, 222, 80, 19);
                }
                String name = stack.getName().getString();
                if(this.textRenderer.getWidth(name) > 55)
                {
                    name = this.textRenderer.trimToWidth(name, 50).trim() + "...";
                }
                this.textRenderer.draw(matrices, name, this.x + 172 + 22, this.y + i * 19 + 6 + 63, Color.WHITE.getRGB());

                MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(stack, this.x + 172 + 2, this.y + i * 19 + 1 + 63);

                /*if(this.checkBoxMaterials.isToggled())
                {
                    int count = InventoryUtil.itemCountInInventory(MinecraftClient.getInstance().player, stack.getItem());
                    stack = stack.copy();
                    stack.setCount(stack.getCount() - count);
                }*/

                MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(this.textRenderer, stack, this.x + 172 + 2, this.y + i * 19 + 1 + 63, null);
            }
        }
    }

    private void renderItemName(MatrixStack matrices) {
        this.client.textRenderer.draw(matrices, this.getDisplayStack().getName(), this.x + 90 - this.client.textRenderer.getWidth(this.getDisplayStack().getName())/2, this.y + 25, 0xFFFFFF);
    }


    private void renderCurrentItem(MatrixStack matrices) {
        float partialTicks = MinecraftClient.getInstance().getTickDelta();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(x + 8, y + 17, 160, 70);

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();

        modelViewStack.translate(x + 88, y + 60, 100);
        modelViewStack.scale(75F, -75F, 75F);
        modelViewStack.multiply(new Quaternionf(new AxisAngle4f(MathHelper.RADIANS_PER_DEGREE * (MinecraftClient.getInstance().gameRenderer.ticks + partialTicks), 0.0F, 1.0F, 0.0F)));
        modelViewStack.multiply(new Quaternionf(new AxisAngle4f(MathHelper.RADIANS_PER_DEGREE * 30F, 0.0F, 0.0F, -1.0F)));
        RenderSystem.applyModelViewMatrix();
        VertexConsumerProvider.Immediate buffer = this.client.getBufferBuilders().getEntityVertexConsumers();
        MinecraftClient.getInstance().getItemRenderer().renderItem(this.getDisplayStack(), ModelTransformation.Mode.FIXED, false, matrices, buffer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, RenderUtil.getModel(this.getDisplayStack()));
        buffer.draw();

        modelViewStack.pop();
        RenderSystem.applyModelViewMatrix();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void renderBackgroundTexture(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, 173, 184);
        drawTexture(matrices, x + 173, y, 78, 184, 173, 0, 1, 184, 256, 256);
        drawTexture(matrices, x + 251, y, 174, 0, 24, 184);
        drawTexture(matrices, x + 172, y + 16, 198, 0, 20, 20);
    }

    protected ItemStack getDisplayStack() {
        return recipes.get(currentTab).currentElement();
    }

    private record Tab(ItemStack icon) {}

    static class Tabs {
        public static final Tab GUNS = new Tab(new ItemStack(ItemRegistry.ASSAULT_RIFLE));
        public static final Tab AMMUNITION = new Tab(new ItemStack(ItemRegistry.MEDIUM_BULLETS));
        public static final Tab ATTACHMENTS = new Tab(new ItemStack(ItemRegistry.LONG_SCOPE));
    }
}