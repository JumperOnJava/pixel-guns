package com.ultreon.mods.pixelguns.entity.projectile.thrown;

import com.ultreon.mods.pixelguns.registry.EntityRegistry;
import com.ultreon.mods.pixelguns.registry.ItemRegistry;
import com.ultreon.mods.pixelguns.registry.SoundRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class GrenadeEntity extends ThrownItemEntity implements GeoEntity {

    public GrenadeEntity(EntityType<? extends GrenadeEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrenadeEntity(World world, LivingEntity owner) {
        super(EntityRegistry.GRENADE, owner, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0f);
    }

    private void explode() {
        if (world.isClient) return;

        Explosion explosion = world.createExplosion(this, null, new ExplosionBehavior() {
            @Override
            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                return Optional.of(0.0f);
            }
        }, getX(), getY(), getZ(), 1.0f, false, World.ExplosionSourceType.MOB);

        Box box = new Box(getBlockPos()).expand(6);
        for (Entity entity : world.getNonSpectatingEntities(Entity.class, box)) {
            entity.damage(DamageSource.explosion(explosion), 10);
        }

        discard();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        explode();
        world.playSound(hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z, SoundRegistry.GRENADE_EXPLODE, SoundCategory.MASTER, 0.8f, 0.8f, false);
    }

    

    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.GRENADE;
    }



    /*
     * Animation Side
     */

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", state -> PlayState.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}