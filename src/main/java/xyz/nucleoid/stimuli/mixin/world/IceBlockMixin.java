package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.world.IceMeltEvent;

@Mixin(IceBlock.class)
public class IceBlockMixin {
    @Inject(method = "melt", at = @At("HEAD"), cancellable = true)
    private void applyIceMeltEvent(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
        if (!world.isClient) {
            try (EventInvokers invokers = Stimuli.select().at(world, pos)) {
                ActionResult result = invokers.get(IceMeltEvent.EVENT).onIceMelt((ServerWorld) world, pos);
                if (result == ActionResult.FAIL) {
                    ci.cancel();
                }
            }
        }
    }
}
