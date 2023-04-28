package com.ultreon.mods.pixelguns.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.mods.pixelguns.PixelGuns;
import com.ultreon.mods.pixelguns.client.screen.handler.WorkshopScreenHandler;
import com.ultreon.mods.pixelguns.registry.WorkshopTabsRegistry;
import com.ultreon.mods.pixelguns.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class WorkshopScreen extends HandledScreen<WorkshopScreenHandler> {

    private static final Identifier TEXTURE = PixelGuns.id("textures/gui/container/workshop.png");

    private float tick;
    private final List<WorkshopTabsRegistry.WorkshopTab> tabs;

    public WorkshopScreen(WorkshopScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        playerInventoryTitleX = x + 8;
        playerInventoryTitleY = y + 91;
        tabs = handler.getTabs();
    }

    @Override
    protected void init() {
        super.init();

        addDrawableChild(new ButtonWidget(x + 86, y + 29, 15, 20, Text.literal("<"), button -> {
            handler.updateRecipe(-1);
        }, textSupplier -> Text.translatable("gui.pixel_guns.previous_item")));

        addDrawableChild(new ButtonWidget(x + 151, y + 29, 15, 20, Text.literal(">"), button -> {
            handler.updateRecipe(1);
        }, textSupplier -> Text.translatable("gui.pixel_guns.next_item")));
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        tick++;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);

        for (WorkshopTabsRegistry.WorkshopTab tab : tabs) {
            if (RenderUtil.isMouseWithin(mouseX, mouseY, x + 28 * tabs.indexOf(tab), y - 28, 28, 28)) {
                renderTooltip(matrices, tab.getDisplayName(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        renderUnselectedTabs(matrices);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight + 18);

        renderSelectedTab(matrices);
        render3DResultItem(matrices);
        renderMissingIngredients(matrices);
    }

    private void renderMissingIngredients(MatrixStack matrices) {
        List<Slot> ingredientSlots = handler.getIngredientSlots();
        if (handler.getCurrentRecipe() != null) {
            List<Pair<Ingredient, Integer>> ingredients = handler.getCurrentRecipe().getIngredientPairs();

            for (int i = 0; i < ingredients.size(); i++) {
                Slot ingredientSlot = ingredientSlots.get(i);
                Pair<Ingredient, Integer> pair = ingredients.get(i);
                if (!ingredientSlot.hasStack() && noneHasIngredient(pair.getLeft())) {
                    renderIngredientWithCount(matrices, pair.getLeft(), pair.getRight(), ingredientSlot.x, ingredientSlot.y);
                }
            }
        }
    }

    private boolean noneHasIngredient(Ingredient ingredient) {
        int i = 0;
        for (Slot slot : handler.getIngredientSlots()) {
            if (slot.hasStack()) {
                if (!ingredient.test(slot.getStack())) {
                    i++;
                }
            }
            else {
                i++;
            }
        }

        return i == handler.getIngredientSlots().size();
    }

    private void renderUnselectedTabs(MatrixStack matrices) {
        for (int i = 0; i < tabs.size(); i++) {
            WorkshopTabsRegistry.WorkshopTab tab = tabs.get(i);
            if (tab != handler.getCurrentTab()) {
                renderTab(matrices, tab, i, 184);
            }
        }
    }

    private void renderSelectedTab(MatrixStack matrices) {
        WorkshopTabsRegistry.WorkshopTab currentTab = handler.getCurrentTab();
        if (currentTab != null && tabs.contains(currentTab)) {
            int i = tabs.indexOf(currentTab);
            renderTab(matrices, currentTab, i, 214);
        }
    }

    private void renderTab(MatrixStack matrices, WorkshopTabsRegistry.WorkshopTab tab, int index, int v) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, TEXTURE);
        drawTexture(matrices, x + 28 * index, y - 28, index == 0 ? 0 : 28, v, 28, 32);

        MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(tab.iconStack(), x + 28 * index + 6, y - 28 + 8);
        MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(textRenderer, tab.iconStack(), x + 28 * index + 6, y - 28 + 8, null);
    }

    private void renderIngredientWithCount(MatrixStack matrices, Ingredient ingredient, int count, int x, int y) {
        x += this.x;
        y += this.y;
        ItemRenderer itemRenderer = client.getItemRenderer();
        ItemStack[] stacks = ingredient.getMatchingStacks();
        ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[MathHelper.floor(tick / 30) % stacks.length].copy();

        stack.setCount(count);
        DrawableHelper.fill(matrices, x, y, x + 16, y + 16, 822018048);
        itemRenderer.renderInGui(stack, x, y);
        RenderSystem.depthFunc(516);
        DrawableHelper.fill(matrices, x, y, x + 16, y + 16, 822083583);
        RenderSystem.depthFunc(515);
        itemRenderer.renderGuiItemOverlay(client.textRenderer, stack, x, y);
    }

    private void render3DResultItem(MatrixStack matrices) {
        if (handler.getCurrentRecipe() == null) {
            return;
        }

        float partialTicks = MinecraftClient.getInstance().getTickDelta();
        ItemStack resultStack = handler.getCurrentRecipe().getOutput().copy();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(x + 8, y + 17, 70, 70);

        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();

        modelViewStack.translate(x + 40, y + 55, 100);
        modelViewStack.scale(50, -50, 50);
        modelViewStack.multiply(new Quaternionf(new AxisAngle4f(MathHelper.RADIANS_PER_DEGREE * (tick + partialTicks), 0, 1, 0)));
        modelViewStack.multiply(new Quaternionf(new AxisAngle4f(MathHelper.RADIANS_PER_DEGREE * 30F, 0, 0, -1)));
        RenderSystem.applyModelViewMatrix();
        VertexConsumerProvider.Immediate buffer = client.getBufferBuilders().getEntityVertexConsumers();
        itemRenderer.renderItem(resultStack, ModelTransformation.Mode.FIXED, false, matrices, buffer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, RenderUtil.getModel(resultStack));
        buffer.draw();

        modelViewStack.pop();
        RenderSystem.applyModelViewMatrix();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (WorkshopTabsRegistry.WorkshopTab tab : tabs) {
                if (RenderUtil.isMouseWithin((int) mouseX, (int) mouseY, x + 28 * tabs.indexOf(tab), y - 28, 28, 28)) {
                    if (handler.getCurrentTab() != tab) {
                        handler.setCurrentTab(tab);
                        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
