package xyz.nucleoid.stimuli.mixin.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.projectile.ProjectileHitEvent;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {
    public ProjectileMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        if (this.level().isClientSide()) {
            return;
        }

        try (var invokers = Stimuli.select().forEntity(this)) {
            var self = (Projectile) (Object) this;
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                var result = invokers.get(ProjectileHitEvent.ENTITY).onHitEntity(self, (EntityHitResult) hitResult);
                if (result == EventResult.DENY) {
                    ci.cancel();
                }
            } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                var result = invokers.get(ProjectileHitEvent.BLOCK).onHitBlock(self, (BlockHitResult) hitResult);
                if (result == EventResult.DENY) {
                    ci.cancel();
                }
            }
        }
    }
}
