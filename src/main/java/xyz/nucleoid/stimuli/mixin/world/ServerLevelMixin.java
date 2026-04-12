package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.duck.ExplosionCancellable;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.entity.EntitySpawnEvent;
import xyz.nucleoid.stimuli.event.world.FireTickEvent;
import xyz.nucleoid.stimuli.event.world.SnowFallEvent;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
    private void applyEntitySpawnEvent(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        try (var invokers = Stimuli.select().at((ServerLevel) (Object) this, entity.blockPosition())) {
            var result = invokers.get(EntitySpawnEvent.EVENT).onSpawn(entity);
            if (result == EventResult.DENY) {
                cir.setReturnValue(false);
            }
        }
    }

    @WrapOperation(method = "tickPrecipitation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean applySnowFallEvent(Biome instance, LevelReader level, BlockPos pos, Operation<Boolean> original) {
        if (!original.call(instance, level, pos)) {
            return false;
        }

        ServerLevel serverWorld = (ServerLevel) level;

        try (var invokers = Stimuli.select().at(serverWorld, pos)) {
            var result = invokers.get(SnowFallEvent.EVENT).onSnowFall(serverWorld, pos);
            if (result == EventResult.DENY) {
                return false;
            }
        }

        return true;
    }

    @Inject(
            method = "explode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;explode()I", shift = At.Shift.AFTER),
            cancellable = true
    )
    private void cancelExplosion(CallbackInfo ci, @Local ServerExplosion explosion) {
        if (explosion instanceof ExplosionCancellable cancellable && cancellable.stimuli$isCancelled()) {
            ci.cancel();
        }
    }

    @WrapMethod(method = "canSpreadFireAround")
    public boolean canFireSpread(BlockPos pos, Operation<Boolean> original) {
        var level = (ServerLevel) (Object) this;
        try (var invokers = Stimuli.select().at(level, pos)) {
            var result = invokers.get(FireTickEvent.EVENT).onFireTick(level, pos);
            if (result == EventResult.ALLOW) {
                return true;
            } else if (result == EventResult.DENY) {
                return false;
            }
        }
        return original.call(pos);
    }
}
