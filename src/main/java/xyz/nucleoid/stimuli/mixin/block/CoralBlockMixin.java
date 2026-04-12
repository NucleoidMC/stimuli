package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.CoralDeathEvent;

@Mixin(value = {
    CoralPlantBlock.class,
    CoralBlock.class,
    CoralFanBlock.class,
    CoralWallFanBlock.class,
})
public class CoralBlockMixin {
    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            )
    )
    public boolean onScheduledTickSetBlockState(ServerLevel level, BlockPos pos, BlockState to, int flags, Operation<Boolean> original, BlockState from) {
        var events = Stimuli.select();

        try (var invokers = events.at(level, pos)) {
            var result = invokers.get(CoralDeathEvent.EVENT).onCoralDeath(level, pos, from, to);
            if (result == EventResult.DENY) {
                return false;
            }
        }

        return original.call(level, pos, to, flags);
    }
}
