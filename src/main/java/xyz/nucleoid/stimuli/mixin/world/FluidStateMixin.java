package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.FluidRandomTickEvent;

@Mixin(FluidState.class)
public class FluidStateMixin {
    @WrapWithCondition(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/Fluid;randomTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/util/RandomSource;)V"))
    private boolean applyFluidRandomTickEvent(Fluid fluid, ServerLevel level, BlockPos pos, FluidState state, RandomSource random) {
        try (var invokers = Stimuli.select().at(level, pos)) {
            var result = invokers.get(FluidRandomTickEvent.EVENT).onFluidRandomTick(level, pos, state);
            if (result == EventResult.DENY) {
                return false;
            }
        }

        return true;
    }
}
