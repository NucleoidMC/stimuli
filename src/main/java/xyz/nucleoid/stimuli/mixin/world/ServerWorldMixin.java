package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntitySpawnEvent;
import xyz.nucleoid.stimuli.event.world.SnowFallEvent;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "spawnEntity", at = @At("HEAD"), cancellable = true)
    private void applyEntitySpawnEvent(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        try (var invokers = Stimuli.select().at((ServerWorld) (Object) this, entity.getBlockPos())) {
            var result = invokers.get(EntitySpawnEvent.EVENT).onSpawn(entity);
            if (result == ActionResult.FAIL) {
                cir.setReturnValue(false);
            }
        }
    }

    @WrapOperation(method = "tickIceAndSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;canSetSnow(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean applySnowFallEvent(Biome instance, WorldView world, BlockPos pos, Operation<Boolean> original) {
        if (!original.call(instance, world, pos)) {
            return false;
        }

        ServerWorld serverWorld = (ServerWorld) world;

        try (var invokers = Stimuli.select().at(serverWorld, pos)) {
            var result = invokers.get(SnowFallEvent.EVENT).onSnowFall(serverWorld, pos);
            if (result == ActionResult.FAIL) {
                return false;
            }
        }

        return true;
    }
}
