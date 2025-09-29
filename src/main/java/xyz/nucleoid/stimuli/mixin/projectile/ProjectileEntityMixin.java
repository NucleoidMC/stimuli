package xyz.nucleoid.stimuli.mixin.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.projectile.ProjectileHitEvent;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin extends Entity {
    public ProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        if (this.getEntityWorld().isClient()) {
            return;
        }

        try (var invokers = Stimuli.select().forEntity(this)) {
            var self = (ProjectileEntity) (Object) this;
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
