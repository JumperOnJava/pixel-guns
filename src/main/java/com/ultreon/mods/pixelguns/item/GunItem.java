package com.ultreon.mods.pixelguns.item;

import io.netty.buffer.Unpooled;
import com.ultreon.mods.pixelguns.PixelGuns;
import com.ultreon.mods.pixelguns.PixelGunsClient;
import com.ultreon.mods.pixelguns.util.InventoryUtil;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

public abstract class GunItem extends Item {

    private final Minecraft client;

    protected final float gunDamage;
    private final int rateOfFire;
    private final int magSize;
    private final Item ammoType;
    private final int reloadCooldown;
    private final float bulletSpread;
    private final float gunRecoil;
    private final int pelletCount;
    private final int loadingType;
    private final SoundEvent reload1;
    private final SoundEvent reload2;
    private final SoundEvent reload3;
    protected final SoundEvent shootSound;
    private final int reloadCycles;
    private final boolean isScoped;
    private final int reloadStage1;
    private final int reloadStage2;
    private final int reloadStage3;

    public GunItem(Properties settings, float gunDamage, int rateOfFire, int magSize, Item ammoType, int reloadCooldown, float bulletSpread, float gunRecoil, int pelletCount, int loadingType, SoundEvent reload1, SoundEvent reload2, SoundEvent reload3, SoundEvent shootSound, int reloadCycles, boolean isScoped, int reloadStage1, int reloadStage2, int reloadStage3) {
        super(settings.durability(magSize * 10 + 1));
        this.gunDamage = gunDamage;
        this.rateOfFire = rateOfFire;
        this.magSize = magSize;
        this.ammoType = ammoType;
        this.reloadCooldown = reloadCooldown;
        this.bulletSpread = bulletSpread;
        this.gunRecoil = gunRecoil;
        this.pelletCount = pelletCount;
        this.loadingType = loadingType;
        this.reload1 = reload1;
        this.reload2 = reload2;
        this.reload3 = reload3;
        this.shootSound = shootSound;
        this.reloadCycles = reloadCycles;
        this.isScoped = isScoped;
        this.reloadStage1 = reloadStage1;
        this.reloadStage2 = reloadStage2;
        this.reloadStage3 = reloadStage3;

        client = Minecraft.getInstance();
    }

    public static boolean isLoaded(ItemStack stack) {
        return GunItem.remainingAmmo(stack) > 0;
    }

    private static int remainingAmmo(ItemStack stack) {
        CompoundTag nbtCompound = stack.getOrCreateTag();
        return nbtCompound.getInt("Clip");
    }

    public static int reserveAmmoCount(Player player, Item item) {
        return InventoryUtil.itemCountInInventory(player, item);
    }

    public void setDefaultNBT(CompoundTag nbtCompound) {
        nbtCompound.putInt("reloadTick", 0);
        nbtCompound.putInt("currentCycle", 1);
        nbtCompound.putInt("Clip", 0);
        nbtCompound.putBoolean("isScoped", this.isScoped);
        nbtCompound.putBoolean("isReloading", false);
    }

    public void inventoryTick(ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        CompoundTag nbtCompound = stack.getOrCreateTag();
        if (!(nbtCompound.contains("reloadTick") && nbtCompound.contains("Clip") && nbtCompound.contains("isScoped") && nbtCompound.contains("isReloading"))) {
            this.setDefaultNBT(nbtCompound);
        }
        if (world.isClientSide() && ((Player) entity).getItemInHand(InteractionHand.MAIN_HAND) == stack && PixelGunsClient.reloadToggle.isDown() && GunItem.remainingAmmo(stack) < this.magSize && GunItem.reserveAmmoCount((Player) entity, this.ammoType) > 0 && !nbtCompound.getBoolean("isReloading")) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBoolean(true);
            ClientPlayNetworking.send(new ResourceLocation("pixel_guns", "reload"), buf);
        }
        if (nbtCompound.getBoolean("isReloading") && (((Player) entity).getItemInHand(InteractionHand.MAIN_HAND) != stack || GunItem.reserveAmmoCount((Player) entity, this.ammoType) <= 0 && this.reloadCycles <= 1 || nbtCompound.getInt("reloadTick") >= this.reloadCooldown || GunItem.remainingAmmo(stack) >= this.magSize && this.reloadCycles <= 1)) {
            nbtCompound.putBoolean("isReloading", false);
        }
        if (nbtCompound.getBoolean("isReloading")) {
            this.doReloadTick(world, nbtCompound, (Player) entity, stack);
        } else {
            if (nbtCompound.getInt("reloadTick") > this.reloadStage3 && nbtCompound.getInt("reloadTick") <= this.reloadCooldown) {
                this.finishReload((Player) entity, stack);
            }
            nbtCompound.putInt("reloadTick", 0);
        }
    }

    private void doReloadTick(Level world, CompoundTag nbtCompound, Player player, ItemStack stack) {
        int rTick = nbtCompound.getInt("reloadTick");
        nbtCompound.putInt("reloadTick", nbtCompound.getInt("reloadTick") + 1);
        if (!world.isClientSide()) {
            if (rTick == this.reloadStage1) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), this.reload1, SoundSource.MASTER, 1.0f, 1.0f);
            } else if (rTick == this.reloadStage2) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), this.reload2, SoundSource.MASTER, 1.0f, 1.0f);
            } else if (rTick == this.reloadStage3 + 1) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), this.reload3, SoundSource.MASTER, 1.0f, 1.0f);
            }
        }
        switch (this.loadingType) {
            case 1 -> {
                if (rTick < this.reloadCooldown || GunItem.reserveAmmoCount(player, this.ammoType) <= 0) break;
                nbtCompound.putInt("currentCycle", 1);
                this.finishReload(player, stack);
                nbtCompound.putInt("reloadTick", 0);
            }
            case 2 -> {
                if (rTick < this.reloadStage3 || nbtCompound.getInt("currentCycle") >= this.reloadCycles || GunItem.reserveAmmoCount(player, this.ammoType) <= 0)
                    break;
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + 1);
                InventoryUtil.removeItemFromInventory(player, this.ammoType, 1);
                if (GunItem.remainingAmmo(stack) < this.magSize && GunItem.reserveAmmoCount(player, this.ammoType) > 0) {
                    nbtCompound.putInt("reloadTick", this.reloadStage2);
                }
                nbtCompound.putInt("currentCycle", nbtCompound.getInt("Clip"));
                stack.setDamageValue(this.getMaxDamage() - (nbtCompound.getInt("Clip") * 10 + 1));
            }
        }
    }

    public InteractionResultHolder<ItemStack> use(@NotNull Level world, Player user, @NotNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!client.options.keyAttack.isDown()) {
            return InteractionResultHolder.fail(itemStack);
        }
        if (hand == InteractionHand.MAIN_HAND && !user.isSprinting() && GunItem.isLoaded(itemStack)) {
            this.shoot(world, user, itemStack);
            if (this.reloadCycles > 1) {
                itemStack.getOrCreateTag().putInt("currentCycle", itemStack.getOrCreateTag().getInt("Clip"));
            }
            itemStack.getOrCreateTag().putInt("reloadTick", 0);
            itemStack.getOrCreateTag().putBoolean("isReloading", false);
        }
        return InteractionResultHolder.fail(itemStack);
    }

    public HitResult getHitResult(Level world, Player player, Vec3 origin, Vec3 direction, double maxDistance) {
        Vec3 destination = origin.add(direction.scale(maxDistance));
        HitResult hitResult = world.clip(new ClipContext(origin, destination, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hitResult.getType() != HitResult.Type.MISS) {
            destination = hitResult.getLocation();
        }
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(world, player, origin, destination, player.getBoundingBox().expandTowards(direction.scale(maxDistance)).inflate(1.0), (entity) -> true);
        if (entityHitResult != null) {
            hitResult = entityHitResult;
        }
        return hitResult;
    }

    public void shoot(Level world, Player user, ItemStack stack) {
        float kick = user.getXRot() - this.getRecoil(user);
        user.getCooldowns().addCooldown(this, this.rateOfFire);
        if (!world.isClientSide()) {
            for (int i = 0; i < this.pelletCount; ++i) {
                // TODO spread, balancing, testing
                int maxDistance = 0;
                if (this.ammoType == ModItems.STANDARD_HANDGUN_BULLET) maxDistance = 25;
                if (this.ammoType == ModItems.HEAVY_HANDGUN_BULLET) maxDistance = 35;
                if (this.ammoType == ModItems.STANDARD_RIFLE_BULLET) maxDistance = 50;
                if (this.ammoType == ModItems.HEAVY_RIFLE_BULLET) maxDistance = 75;
                if (this.ammoType == ModItems.SHOTGUN_SHELL) maxDistance = 25;

                HitResult result = getHitResult(world, user, user.getEyePosition(), user.getLookAngle(), maxDistance);
                if (result instanceof EntityHitResult) {
                    EntityHitResult entityHitResult = (EntityHitResult) result;
                    float damage = this.gunDamage;
                    if (user.distanceTo(entityHitResult.getEntity()) > maxDistance / 2) {
                        damage /= 2;
                    }
                    entityHitResult.getEntity().hurt(DamageSource.playerAttack(user), damage);
                
                    PixelGuns.LOGGER.info(damage + " " + entityHitResult.getEntity().getType().toShortString());
                }
            }
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(kick);
            ServerPlayNetworking.send((ServerPlayer) user, PixelGuns.RECOIL_PACKET_ID, buf);
//            user.setXRot(kick);
        }
        if (!user.getAbilities().instabuild) {
            this.useAmmo(stack);
            stack.hurtAndBreak(10, (LivingEntity) user, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        playShootSound(world, user, stack);
    }

    public void playShootSound(Level world, Player user, ItemStack stack) {
        world.playSound(null, user.getX(), user.getY(), user.getZ(), this.shootSound, SoundSource.MASTER, 1.0f, 1.0f);
    }

    private float getRecoil(Player user) {
        return client.options.keyUse.isDown() ? this.gunRecoil / 2.0f : this.gunRecoil;
    }

    private void useAmmo(ItemStack stack) {
        CompoundTag nbtCompound = stack.getOrCreateTag();
        nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") - 1);
    }

    public void finishReload(Player player, ItemStack stack) {
        CompoundTag nbtCompound = stack.getOrCreateTag();
        if (nbtCompound.getInt("Clip") <= 0) {
            if (GunItem.reserveAmmoCount(player, this.ammoType) > this.magSize) {
                nbtCompound.putInt("Clip", this.magSize);
                InventoryUtil.removeItemFromInventory(player, this.ammoType, this.magSize);
            } else {
                nbtCompound.putInt("Clip", GunItem.reserveAmmoCount(player, this.ammoType));
                InventoryUtil.removeItemFromInventory(player, this.ammoType, GunItem.reserveAmmoCount(player, this.ammoType));
            }
        } else {
            int ammoToLoad = this.magSize - nbtCompound.getInt("Clip");
            if (GunItem.reserveAmmoCount(player, this.ammoType) >= ammoToLoad) {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + ammoToLoad);
                InventoryUtil.removeItemFromInventory(player, this.ammoType, ammoToLoad);
            } else {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + GunItem.reserveAmmoCount(player, this.ammoType));
                InventoryUtil.removeItemFromInventory(player, this.ammoType, GunItem.reserveAmmoCount(player, this.ammoType));
            }
        }
        stack.setDamageValue(this.getMaxDamage() - (nbtCompound.getInt("Clip") * 10 + 1));
    }

    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }
}

