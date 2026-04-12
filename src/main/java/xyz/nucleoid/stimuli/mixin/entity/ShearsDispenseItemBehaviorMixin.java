package xyz.nucleoid.stimuli.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.entity.EntityShearEvent;

@Mixin(ShearsDispenseItemBehavior.class)
public class ShearsDispenseItemBehaviorMixin {
    @WrapOperation(
            method = "tryShearEntity",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/Shearable;readyForShearing()Z"
            )
    )
    private static boolean onEntityShear(Shearable shearable, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
        if (!original.call(shearable)) {
            return false;
        }

        // Entities are selected from the level by the LivingEntity class
        var entity = (LivingEntity) shearable;

        var events = Stimuli.select();

        try (var invokers = events.forEntityAt(entity, pos)) {
            var result = invokers.get(EntityShearEvent.EVENT).onShearEntity(entity, null, null, pos);
            if (result == EventResult.DENY) {
                return false;
            }
        }

        return true;
    }
}
