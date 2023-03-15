package com.ultreon.mods.pixelguns.item;

import com.ultreon.mods.pixelguns.armor.HazardArmor;
import com.ultreon.mods.pixelguns.client.GeoRendererGenerator;
import com.ultreon.mods.pixelguns.util.LivingEntityAccessor;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GasMaskItem extends HazardArmor implements GeoItem {

    public GasMaskItem() {
        super(EquipmentSlot.HEAD);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.packRgb(114, 164, 161);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof ServerPlayerEntity player) {
            List<Entity> entities = new ArrayList<>();
            entities.addAll(world.getEntitiesByClass(AreaEffectCloudEntity.class, entity.getBoundingBox(), cloud -> true));
            entities.addAll(world.getEntitiesByClass(PotionEntity.class, entity.getBoundingBox(), potion -> true));
            entities.addAll(world.getEntitiesByClass(LlamaSpitEntity.class, entity.getBoundingBox(), spit -> true));

            if (player.getInventory().armor.get(EquipmentSlot.HEAD.getEntitySlotId()) == stack && !player.isCreative() && !entities.isEmpty()) {
                LivingEntityAccessor accessor = ((LivingEntityAccessor) player);
                if (stack.getDamage() < stack.getMaxDamage()) {
                    stack.damage(1, player.getRandom(), player);
                    accessor.isAffectedBySplashPotions(false);
                }
                else {
                    accessor.isAffectedBySplashPotions(true);
                }
            }
        }
    }

    /*
     * Animation Side
     */

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    @Override
    public void createRenderer(Consumer<Object> consumer) {
//        super.createRenderer(consumer);
        consumer.accept(new RenderProvider() {
            private GeoItemRenderer<GasMaskItem> renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = GeoRendererGenerator.item(GasMaskItem.this);

                return this.renderer;
            }

            private GeoArmorRenderer<?> armorRenderer;

            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                if (this.armorRenderer == null)
                    this.armorRenderer = GeoRendererGenerator.armor(GasMaskItem.this);

                this.armorRenderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.armorRenderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}