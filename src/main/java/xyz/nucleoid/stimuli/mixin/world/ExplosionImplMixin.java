package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.duck.ExplosionCancellable;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.ExplosionDetonatedEvent;

import java.util.List;

@Mixin(ExplosionImpl.class)
public abstract class ExplosionImplMixin implements Explosion, ExplosionCancellable {
    @Shadow @Final private ServerWorld world;

    @Unique private List<BlockPos> blocksToDestroy;
    @Unique private boolean cancelled;

    @Override
    public boolean stimuli$isCancelled() {
        return this.cancelled;
    }

    @Shadow
    private List<BlockPos> getBlocksToDestroy() {
        throw new AssertionError();
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void onExplode(CallbackInfo ci) {
        var pos = BlockPos.ofFloored(this.getPosition());

        try (var invokers = Stimuli.select().at(this.world, pos)) {
            this.blocksToDestroy = this.getBlocksToDestroy();

            var result = invokers.get(ExplosionDetonatedEvent.EVENT).onExplosionDetonated((Explosion) (Object) this, this.blocksToDestroy);

            if (result == EventResult.DENY) {
                this.cancelled = true;
                ci.cancel();
            }
        }
    }

    @WrapOperation(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionImpl;getBlocksToDestroy()Ljava/util/List;"))
    private List<BlockPos> replaceBlocksToDestroy(ExplosionImpl explosion, Operation<List<BlockPos>> operation) {
        if (this.blocksToDestroy == null) {
            return operation.call(explosion);
        }

        return this.blocksToDestroy;
    }
}
