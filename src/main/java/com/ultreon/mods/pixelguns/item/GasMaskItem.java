package com.ultreon.mods.pixelguns.item;

import com.ultreon.mods.pixelguns.client.GeoRendererGenerator;
import com.ultreon.mods.pixelguns.registry.ArmorRegistry;
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

public class GasMaskItem extends GeoArmor {

    public GasMaskItem() {
        super(ArmorRegistry.HAZARD, EquipmentSlot.HEAD);
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

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new ArmorRenderProvider() {

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                return GeoRendererGenerator.item(GasMaskItem.this);
            }
        });
    }
}