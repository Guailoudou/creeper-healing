package io.github.arkosammy12.creeperhealing.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.arkosammy12.creeperhealing.explosions.ducks.ServerWorldDuck;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import io.github.arkosammy12.creeperhealing.explosions.ducks.ExplosionImplDuck;
import io.github.arkosammy12.creeperhealing.util.callbacks.DaylightCycleEvents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin implements ServerWorldDuck {

    @Unique
    private final Collection<BlockPos> affectedBlockPositions = new ArrayList<>();

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerLevel;setTimeOfDay(J)V", ordinal = 0))
    private void fastForwardDaytimeHealingModeExplosionsOnNightSkipped(ServerLevel instance, long timeOfDay, Operation<Void> original, @Local(argsOnly = true) BooleanSupplier shouldKeepTicking) {
        original.call(instance, timeOfDay);
        DaylightCycleEvents.ON_NIGHT_SKIPPED.invoker().onNightSkipped(((ServerLevel) (Object) this), shouldKeepTicking);
    }

    @WrapOperation(method = "createExplosion", at = @At(value = "NEW", target = "(Lnet/minecraft/server/world/ServerLevel;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionDamageCalculator;Lnet/minecraft/util/math/Vec3;FZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"))
    private Explosion attachExplosionSourceTypeToExplosion(ServerLevel world, Entity entity, DamageSource damageSource, ExplosionDamageCalculator behavior, Vec3 pos, float power, boolean createFire, Explosion.DestructionType destructionType, Operation<Explosion> original, @Local(argsOnly = true) World.ExplosionSourceType explosionSourceType) {
        Explosion explosion = original.call(world, entity, damageSource, behavior, pos, power, createFire, destructionType);
        ((ExplosionImplDuck) explosion).creeperhealing$setExplosionSourceType(explosionSourceType);
        return explosion;
    }

    @Override
    public void creeperhealing$addAffectedPositions(Collection<BlockPos> affectedPositions) {
        this.affectedBlockPositions.addAll(affectedPositions);
    }

    @Override
    public void creeperhealing$clearAffectedPositions() {
        this.affectedBlockPositions.clear();
    }

    @Override
    public boolean creeperhealing$isAffectedPosition(BlockPos pos) {
        return this.affectedBlockPositions.contains(pos);
    }

}

