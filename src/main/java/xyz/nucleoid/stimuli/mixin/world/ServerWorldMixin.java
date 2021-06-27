package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntitySpawnEvent;

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

}
