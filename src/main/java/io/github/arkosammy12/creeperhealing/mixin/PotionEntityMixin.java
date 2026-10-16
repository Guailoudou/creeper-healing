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
import org.spongepowered.asm.mixin.injection.At;
import io.github.arkosammy12.creeperhealing.util.callbacks.SplashPotionCallbacks;

@Mixin(AbstractThrownPotion.class)
public abstract class PotionEntityMixin extends ThrowableItemProjectile {

    public PotionEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, World world) {
        super(entityType, world);
    }

    // Make explosions start healing when you throw a potion of healing or regeneration on them
    @WrapOperation(method = "onBlockHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object onSplashPotionHit(ItemStack instance, DataComponentType type, Object fallback, Operation<Object> original, @Local(argsOnly = true) BlockHitResult blockHitResult) {
        Object result = original.call(instance, type, fallback);
        if (!(((AbstractThrownPotion) (Object) this) instanceof SplashPotionEntity)) {
            return result;
        }
        if (result instanceof PotionContents potionContentsComponent) {
            World world = this.getEntityWorld();
            SplashPotionCallbacks.ON_COLLISION.invoker().onPotionCollide(((AbstractThrownPotion) (Object) this), potionContentsComponent, blockHitResult, world);
        }
        return result;
    }

}
