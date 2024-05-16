package xyz.nucleoid.stimuli.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntityShearEvent;

@Mixin(ShearsDispenserBehavior.class)
public class ShearsDispenserBehaviorMixin {
    @WrapOperation(
            method = "tryShearEntity",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Shearable;isShearable()Z"
            )
    )
    private static boolean onEntityShear(Shearable shearable, Operation<Boolean> original, @Local(argsOnly = true) BlockPos pos) {
        if (!original.call(shearable)) {
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
