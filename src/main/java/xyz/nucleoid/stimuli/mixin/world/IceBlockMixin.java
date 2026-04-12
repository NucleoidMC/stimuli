package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.IceMeltEvent;

@Mixin(IceBlock.class)
public class IceBlockMixin {
    @Inject(method = "melt", at = @At("HEAD"), cancellable = true)
    private void applyIceMeltEvent(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (!level.isClientSide()) {
            try (var invokers = Stimuli.select().at(level, pos)) {
                var result = invokers.get(IceMeltEvent.EVENT).onIceMelt((ServerLevel) level, pos);
                if (result == EventResult.DENY) {
                    ci.cancel();
                }
            }
        }
    }
}
