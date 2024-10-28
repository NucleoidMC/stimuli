package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.IceMeltEvent;

@Mixin(FrostedIceBlock.class)
public class FrostedIceBlockMixin {
    @Inject(method = "increaseAge", at = @At("HEAD"), cancellable = true)
    private void applyIceMeltEvent(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
        if (!world.isClient) {
            try (var invokers = Stimuli.select().at(world, pos)) {
                var result = invokers.get(IceMeltEvent.EVENT).onIceMelt((ServerWorld) world, pos);
                if (result == EventResult.DENY) {
                    ci.cancel();
                }
            }
        }
    }
}
