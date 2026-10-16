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
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/clock/ServerClockManager;moveToTimeMarker(Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/clock/ServerClockManager$MoveResult;", ordinal = 0))
    private void fastForwardDaytimeHealingModeExplosionsOnNightSkipped(ServerClockManager instance, Holder<?> clock, ResourceKey<?> timeMarker, Operation<Object> original, @Local(argsOnly = true) BooleanSupplier shouldKeepTicking) {
        original.call(instance, clock, timeMarker);
        DaylightCycleEvents.ON_NIGHT_SKIPPED.invoker().onNightSkipped(((ServerLevel) (Object) this), shouldKeepTicking);
    }


    // 26.3: 爆炸入口 ServerLevel.explode(...)，在入口处把 ExplosionInteraction 附加给后续创建的 ServerExplosion
    @Unique
    private Level.ExplosionInteraction creeperhealing$currentInteraction = null;

    @Inject(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V", at = @At("HEAD"))
    private void creeperhealing$captureExplosionInteraction(Entity source, DamageSource damageSource, ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, Level.ExplosionInteraction interaction, CallbackInfo ci) {
        this.creeperhealing$currentInteraction = interaction;
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

    @Override
    public Level.ExplosionInteraction creeperhealing$getCurrentExplosionInteraction() {
        Level.ExplosionInteraction interaction = this.creeperhealing$currentInteraction;
        this.creeperhealing$currentInteraction = null;
        return interaction;
    }

}

