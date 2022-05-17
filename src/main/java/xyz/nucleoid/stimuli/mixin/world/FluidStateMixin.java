package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.FluidRandomTickEvent;
import xyz.nucleoid.stimuli.mixin.FluidAccessor;

import java.util.Random;

@Mixin(FluidState.class)
public class FluidStateMixin {
    @Redirect(method = "onRandomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/Fluid;onRandomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/FluidState;Ljava/util/Random;)V"))
    private void applyFluidRandomTickEvent(Fluid fluid, World world, BlockPos pos, FluidState state, Random random) {
        ServerWorld serverWorld = (ServerWorld) world;

        try (var invokers = Stimuli.select().at(world, pos)) {
            var result = invokers.get(FluidRandomTickEvent.EVENT).onFluidRandomTick(serverWorld, pos, state);
            if (result == ActionResult.FAIL) {
                return;
            }
        }

        ((FluidAccessor) fluid).callOnRandomTick(world, pos, state, random);
    }
}
