package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.world.ExplosionDetonatedEvent;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow @Final private World world;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;

    @Inject(method = "affectWorld", at = @At("HEAD"))
    private void affectWorld(boolean particles, CallbackInfo ci) {
        if (!this.world.isClient) {
            var pos = BlockPos.ofFloored(this.x, this.y, this.z);

            try (var invokers = Stimuli.select().at(this.world, pos)) {
                invokers.get(ExplosionDetonatedEvent.EVENT).onExplosionDetonated((Explosion) (Object) this, particles);
            }
        }
    }
}
