package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.duck.ExplosionCancellable;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.ExplosionDetonatedEvent;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin implements Explosion, ExplosionCancellable {
    @Shadow @Final private ServerLevel level;

    @Unique private List<BlockPos> blocksToDestroy;
    @Unique private boolean cancelled;

    @Override
    public boolean stimuli$isCancelled() {
        return this.cancelled;
    }

    @Shadow
    private List<BlockPos> calculateExplodedPositions() {
        throw new AssertionError();
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void onExplode(CallbackInfoReturnable<Integer> cir) {
        var pos = BlockPos.containing(this.center());

        try (var invokers = Stimuli.select().at(this.level, pos)) {
            this.blocksToDestroy = this.calculateExplodedPositions();

            var result = invokers.get(ExplosionDetonatedEvent.EVENT).onExplosionDetonated((Explosion) (Object) this, this.blocksToDestroy);

            if (result == EventResult.DENY) {
                this.cancelled = true;
				cir.cancel();
            }
        }
    }

    @WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerExplosion;calculateExplodedPositions()Ljava/util/List;"))
    private List<BlockPos> replaceBlocksToDestroy(ServerExplosion explosion, Operation<List<BlockPos>> operation) {
        if (this.blocksToDestroy == null) {
            return operation.call(explosion);
        }

        return this.blocksToDestroy;
    }
}
