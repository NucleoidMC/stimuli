package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.CoralBlock;
import net.minecraft.block.CoralBlockBlock;
import net.minecraft.block.CoralFanBlock;
import net.minecraft.block.CoralWallFanBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.CoralDeathEvent;

@Mixin(value = {
    CoralBlock.class,
    CoralBlockBlock.class,
    CoralFanBlock.class,
    CoralWallFanBlock.class,
})
public class CoralBlockMixin {
    @WrapOperation(
            method = "scheduledTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
            )
    )
    public boolean onScheduledTickSetBlockState(ServerWorld world, BlockPos pos, BlockState to, int flags, Operation<Boolean> original, BlockState from) {
        var events = Stimuli.select();

        try (var invokers = events.at(world, pos)) {
            var result = invokers.get(CoralDeathEvent.EVENT).onCoralDeath(world, pos, from, to);
            if (result == ActionResult.FAIL) {
                return false;
            }
        }

        return original.call(world, pos, to, flags);
    }
}
