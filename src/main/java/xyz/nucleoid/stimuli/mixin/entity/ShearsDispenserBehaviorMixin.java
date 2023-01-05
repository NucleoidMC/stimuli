package xyz.nucleoid.stimuli.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntityShearEvent;

@Mixin(ShearsDispenserBehavior.class)
public class ShearsDispenserBehaviorMixin {
    @Redirect(
            method = "tryShearEntity",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Shearable;isShearable()Z"
            )
    )
    private static boolean onEntityShear(Shearable shearable, ServerWorld world, BlockPos pos) {
        if (!shearable.isShearable()) {
            return false;
        }

        // Entities are selected from the world by the LivingEntity class
        var entity = (LivingEntity) shearable;

        var events = Stimuli.select();

        try (var invokers = events.forEntityAt(entity, pos)) {
            var result = invokers.get(EntityShearEvent.EVENT).onShearEntity(entity, null, null, pos);
            if (result == ActionResult.FAIL) {
                return false;
            }
        }

        return true;
    }
}
