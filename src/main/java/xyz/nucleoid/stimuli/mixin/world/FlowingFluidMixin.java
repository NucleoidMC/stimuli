package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.FluidFlowEvent;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
    @Inject(method = "canMaybePassThrough(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Z", at = @At("RETURN"), cancellable = true)
    private void applyFluidFlowEvent(BlockGetter blockView, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, CallbackInfoReturnable<Boolean> ci) {
        if (!(blockView instanceof ServerLevel level)) {
            return;
        }

        try (var invokers = Stimuli.select().at(level, flowTo)) {
            var result = invokers.get(FluidFlowEvent.EVENT)
                    .onFluidFlow(level, fluidPos, fluidBlockState, flowDirection, flowTo, flowToBlockState);
            if (result == EventResult.DENY) {
                ci.setReturnValue(false);
            }
        }
    }
}
