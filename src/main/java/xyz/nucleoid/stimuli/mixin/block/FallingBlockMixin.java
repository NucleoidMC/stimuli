package xyz.nucleoid.stimuli.mixin.block;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockFallEvent;

@Mixin(FallingBlock.class)
public class FallingBlockMixin {
    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    private void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        try (EventInvokers invokers = Stimuli.select().at(world, pos)) {
            ActionResult result = invokers.get(BlockFallEvent.EVENT).onBlockFall(world, pos, state);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }
}
