package com.ultreon.mods.pixelguns.item.gun;

import com.ultreon.mods.pixelguns.PixelGuns;
import com.ultreon.mods.pixelguns.PixelGunsClient;
import com.ultreon.mods.pixelguns.block.BottleBlock;
import com.ultreon.mods.pixelguns.event.GunEvents;
import com.ultreon.mods.pixelguns.registry.KeyBindRegistry;
import com.ultreon.mods.pixelguns.registry.PacketRegistry;
import com.ultreon.mods.pixelguns.util.InventoryUtil;
import com.ultreon.mods.pixelguns.util.WorkshopCraftable;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class GunItem extends Item implements WorkshopCraftable {

    public final boolean isAutomatic;
    protected final float damage;
    protected final int range;
    public final int fireCooldown;
    private final int magazineSize;
    public final Item ammunition;
    private final int reloadCooldown;
    protected final float bulletSpread;
    protected final float recoil;
    protected final int pelletCount;
    private final LoadingType loadingType;
    private final SoundEvent[] reloadSounds;
    private final int[] reloadSoundStages;
    protected final SoundEvent fireAudio;
    private final int reloadCycles;
    public final boolean isScoped;
    private final ItemStack[] craftingRequirements;

    public GunItem(boolean isAutomatic, float damage, int range, int fireCooldown, int magazineSize, Item ammunition, int reloadCooldown, float bulletSpread, float recoil, int pelletCount, LoadingType loadingType, SoundEvent[] reloadSounds, SoundEvent fireAudio, int reloadCycles, boolean isScoped, int[] reloadStages, ItemStack[] craftingRequirements) {
        super(new FabricItemSettings().maxCount(1));
        this.isAutomatic = isAutomatic;
        this.damage = damage;
        this.range = range;
        this.fireCooldown = fireCooldown;
        this.magazineSize = magazineSize;
        this.ammunition = ammunition;
        this.reloadCooldown = reloadCooldown;
        this.bulletSpread = bulletSpread;
        this.recoil = recoil;
        this.pelletCount = pelletCount;
        this.loadingType = loadingType;
        this.reloadSounds = reloadSounds;
        this.fireAudio = fireAudio;
        this.reloadCycles = reloadCycles;
        this.isScoped = isScoped;
        this.reloadSoundStages = reloadStages;
        this.craftingRequirements = craftingRequirements;
    }

    public static boolean isLoaded(ItemStack stack) {
        return GunItem.remainingAmmo(stack) > 0;
    }

    public static int remainingAmmo(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        return nbtCompound.getInt("Clip");
    }

    public static int reserveAmmoCount(PlayerEntity player, Item item) {
        return InventoryUtil.itemCountInInventory(player, item);
    }

    public void setDefaultNBT(NbtCompound nbtCompound) {
        nbtCompound.putInt("reloadTick", 0);
        nbtCompound.putInt("currentCycle", 1);
        nbtCompound.putInt("Clip", 0);
        nbtCompound.putBoolean("isScoped", this.isScoped);
        nbtCompound.putBoolean("isReloading", false);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.literal(Formatting.GRAY + "Ammo Type: " + Formatting.WHITE + this.ammunition.getName().getString()));
        tooltip.add(Text.literal(Formatting.GRAY + "Damage: " + Formatting.WHITE + this.damage));
        tooltip.add(Text.literal(Formatting.GRAY + "Ammo: " + Formatting.WHITE + GunItem.remainingAmmo(stack) + "/" + this.magazineSize));
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull World world, @NotNull Entity entity, int slot, boolean selected) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        ItemCooldownManager cooldownManager = ((PlayerEntity) entity).getItemCooldownManager();

        if (!nbtCompound.contains("uuid")) {
            nbtCompound.putUuid("uuid", UUID.randomUUID());
        }

        if (!(nbtCompound.contains("reloadTick") && nbtCompound.contains("Clip") && nbtCompound.contains("isScoped") && nbtCompound.contains("isReloading"))) {
            this.setDefaultNBT(nbtCompound);
        }

        if (world.isClient() && ((PlayerEntity) entity).getStackInHand(Hand.MAIN_HAND) == stack && KeyBindRegistry.RELOAD_KEY.isPressed() && GunItem.remainingAmmo(stack) < this.magazineSize && GunItem.reserveAmmoCount((PlayerEntity) entity, this.ammunition) > 0 && !nbtCompound.getBoolean("isReloading")) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(true);
            ClientPlayNetworking.send(PixelGuns.id("reload"), buf);
        }

        if (nbtCompound.getBoolean("isReloading") && (((PlayerEntity) entity).getStackInHand(Hand.MAIN_HAND) != stack || GunItem.reserveAmmoCount((PlayerEntity) entity, this.ammunition) <= 0 && this.reloadCycles <= 1 || nbtCompound.getInt("reloadTick") >= this.reloadCooldown || GunItem.remainingAmmo(stack) >= this.magazineSize && this.reloadCycles <= 1)) {
            nbtCompound.putBoolean("isReloading", false);
        }

        if (nbtCompound.getBoolean("isReloading")) {
            this.doReloadTick(world, nbtCompound, (PlayerEntity) entity, stack);
        }
        else {
            if (nbtCompound.getInt("reloadTick") > this.reloadSoundStages[2] && nbtCompound.getInt("reloadTick") <= this.reloadCooldown) {
                this.finishReload((PlayerEntity) entity, stack);
            }
            nbtCompound.putInt("reloadTick", 0);
        }

        if (cooldownManager.isCoolingDown(stack.getItem())) {
            float cooldown = cooldownManager.getCooldownProgress(stack.getItem(), 0);

            if (world.isClient) {
                PixelGunsClient.addOrUpdateTrackedGuns(nbtCompound.getUuid("uuid"), cooldown);
            }
            else {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid(nbtCompound.getUuid("uuid"));
                buf.writeFloat(cooldown);

                for (ServerPlayerEntity serverPlayer : PlayerLookup.tracking(entity)) {
                    if (!serverPlayer.getUuid().equals(entity.getUuid())) {
                        ServerPlayNetworking.send(serverPlayer, PacketRegistry.GUN_COOLDOWN, buf);
                    }
                }
            }
        }
    }

    protected void doReloadTick(World world, NbtCompound nbtCompound, PlayerEntity player, ItemStack stack) {
        int reloadTick = nbtCompound.getInt("reloadTick");
        nbtCompound.putInt("reloadTick", nbtCompound.getInt("reloadTick") + 1);
        if (!world.isClient()) {
            if (reloadTick == this.reloadSoundStages[0]) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), this.reloadSounds[0], SoundCategory.MASTER, 1.0f, 1.0f);
            }
            else if (reloadTick == this.reloadSoundStages[1]) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), this.reloadSounds[1], SoundCategory.MASTER, 1.0f, 1.0f);
            }
            else if (reloadTick == this.reloadSoundStages[2]) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), this.reloadSounds[2], SoundCategory.MASTER, 1.0f, 1.0f);
            }
        }
        switch (this.loadingType) {
            case CLIP -> {
                if (reloadTick < this.reloadCooldown) {
                    break;
                }
                if (GunItem.reserveAmmoCount(player, this.ammunition) <= 0) {
                    break;
                }

                nbtCompound.putInt("currentCycle", 1);
                this.finishReload(player, stack);
                nbtCompound.putInt("reloadTick", 0);
            }
            case INDIVIDUAL -> {
                if (reloadTick < this.reloadSoundStages[2]) {
                    break;
                }
                if (nbtCompound.getInt("currentCycle") >= this.reloadCycles) {
                    break;
                }
                if (GunItem.reserveAmmoCount(player, this.ammunition) <= 0) {
                    break;
                }

                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + 1);
                InventoryUtil.removeItemFromInventory(player, this.ammunition, 1);
                if (GunItem.remainingAmmo(stack) < this.magazineSize && GunItem.reserveAmmoCount(player, this.ammunition) > 0) {
                    nbtCompound.putInt("reloadTick", this.reloadSoundStages[1]);
                }
                nbtCompound.putInt("currentCycle", nbtCompound.getInt("Clip"));
            }
        }
    }

    protected void handleHit(HitResult result, ServerWorld world, ServerPlayerEntity damageSource) {
        GunEvents.GUN_HIT.invokeEvent(event -> event.onGunHit(result, world, damageSource));

        if (result instanceof EntityHitResult entityHitResult) {
            entityHitResult.getEntity().damage(DamageSource.player(damageSource), this.damage);
        }
        else if (result instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();

            if (blockHitResult.getType() == HitResult.Type.MISS) {
                return;
            }

            if (world.getBlockState(pos).getBlock() instanceof BottleBlock bottleBlock) {
                world.breakBlock(pos, false);
            }

            ParticleEffect particleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(blockHitResult.getBlockPos()));
            world.spawnParticles(particleEffect, blockHitResult.getPos().x, blockHitResult.getPos().y, blockHitResult.getPos().z, 1, 0, 0, 0, 1);
        }
    }

    public void shoot(PlayerEntity player, ItemStack stack) {
        GunEvents.GUN_SHOT_PRE.invokeEvent(event -> event.onGunShotPre(player, stack));

        if (player.world.isClient) {
            GunEvents.GUN_SHOT_POST.invokeEvent(event -> event.onGunShotPost(player, stack));
            return;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        ServerWorld world = serverPlayer.getWorld();
        player.getItemCooldownManager().set(this, this.fireCooldown);
        for (int i = 0; i < this.pelletCount; ++i) {
            // TODO bullet spread
            this.handleHit(GunHitscanHelper.getCollision(player, this.range), world, serverPlayer);
        }

        if (!player.getAbilities().creativeMode) {
            this.useAmmo(stack);
        }
        this.playFireAudio(world, player);

        GunEvents.GUN_SHOT_POST.invokeEvent(event -> event.onGunShotPost(player, stack));
    }

    public void playFireAudio(World world, PlayerEntity user) {
        world.playSound(null, user.getX(), user.getY(), user.getZ(), this.fireAudio, SoundCategory.MASTER, 1.0f, 1.0f);
    }

    public float getRecoil() {
        return this.recoil;
    }

    protected void useAmmo(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") - 1);
    }

    public void finishReload(PlayerEntity player, ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (nbtCompound.getInt("Clip") <= 0) {
            if (GunItem.reserveAmmoCount(player, this.ammunition) > this.magazineSize) {
                nbtCompound.putInt("Clip", this.magazineSize);
                InventoryUtil.removeItemFromInventory(player, this.ammunition, this.magazineSize);
            }
            else {
                nbtCompound.putInt("Clip", GunItem.reserveAmmoCount(player, this.ammunition));
                InventoryUtil.removeItemFromInventory(player, this.ammunition, GunItem.reserveAmmoCount(player, this.ammunition));
            }
        }
        else {
            int ammoToLoad = this.magazineSize - nbtCompound.getInt("Clip");
            if (GunItem.reserveAmmoCount(player, this.ammunition) >= ammoToLoad) {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + ammoToLoad);
                InventoryUtil.removeItemFromInventory(player, this.ammunition, ammoToLoad);
            }
            else {
                nbtCompound.putInt("Clip", nbtCompound.getInt("Clip") + GunItem.reserveAmmoCount(player, this.ammunition));
                InventoryUtil.removeItemFromInventory(player, this.ammunition, GunItem.reserveAmmoCount(player, this.ammunition));
            }
        }
    }

    @Override
    public ItemStack[] getIngredients() {
        return craftingRequirements;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    public enum LoadingType {
        INDIVIDUAL,
        CLIP
    }
}

