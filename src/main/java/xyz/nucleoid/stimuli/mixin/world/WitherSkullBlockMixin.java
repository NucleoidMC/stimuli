package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.WitherSummonEvent;

@Mixin(WitherSkullBlock.class)
public class WitherSkullBlockMixin {
    @Inject(method = "checkSpawn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/SkullBlockEntity;)V", at = @At("HEAD"), cancellable = true)
    private static void onPlaced(Level level, BlockPos pos, SkullBlockEntity blockEntity, CallbackInfo ci) {
        if (!level.isClientSide()) {
            try (var invokers = Stimuli.select().at(level, pos)) {
                var result = invokers.get(WitherSummonEvent.EVENT).onSummonWither((ServerLevel) level, pos);
                if (result == EventResult.DENY) {
                    ci.cancel();
                }
            }
        }
    }
}
