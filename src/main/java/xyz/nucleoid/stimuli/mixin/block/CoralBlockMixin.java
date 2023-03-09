package xyz.nucleoid.stimuli.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.CoralBlock;
import net.minecraft.block.CoralBlockBlock;
import net.minecraft.block.CoralFanBlock;
import net.minecraft.block.CoralWallFanBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.CoralDeathEvent;

@Mixin(value = {
    CoralBlock.class,
    CoralBlockBlock.class,
    CoralFanBlock.class,
    CoralWallFanBlock.class,
})
public class CoralBlockMixin {
    @Redirect(
            method = "scheduledTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
            )
    )
    public boolean onScheduledTickSetBlockState(ServerWorld world, BlockPos pos, BlockState to, int flags, BlockState from) {
        var events = Stimuli.select();

        try (var invokers = events.at(world, pos)) {
            var result = invokers.get(CoralDeathEvent.EVENT).onCoralDeath(world, pos, from, to);
            if (result == ActionResult.FAIL) {
                return false;
            }
        }

        return world.setBlockState(pos, to, flags);
    }
}
