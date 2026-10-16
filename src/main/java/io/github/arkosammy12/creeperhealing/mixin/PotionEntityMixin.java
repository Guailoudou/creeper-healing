package io.github.arkosammy12.creeperhealing.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import io.github.arkosammy12.creeperhealing.util.callbacks.SplashPotionCallbacks;

@Mixin(AbstractThrownPotion.class)
public abstract class PotionEntityMixin extends ThrowableItemProjectile {

    public PotionEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }



    // Make explosions start healing when you throw a potion of healing or regeneration on them
    // 26.3: onHitAsPotion(ServerLevel, ItemStack, HitResult) 是药水命中统一入口
    @Inject(method = "onHitAsPotion", at = @At("HEAD"))
    private void onSplashPotionHit(net.minecraft.server.level.ServerLevel serverLevel, ItemStack stack, net.minecraft.world.phys.HitResult hitResult, CallbackInfo ci) {
        if (!(hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }
        if (!(stack.getItem() instanceof net.minecraft.world.item.SplashPotionItem)) {
            return;
        }
        PotionContents potionContentsComponent = stack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (potionContentsComponent == PotionContents.EMPTY) {
            return;
        }
        Level world = this.level();
        SplashPotionCallbacks.ON_COLLISION.invoker().onPotionCollide(((AbstractThrownPotion) (Object) this), potionContentsComponent, blockHitResult, world);
    }

}