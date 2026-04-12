package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.TntIgniteEvent;

@Mixin(TntBlock.class)
public class TntBlockMixin {
    @Inject(method = "prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void primeTnt(Level level, BlockPos pos, LivingEntity igniter, CallbackInfoReturnable<Boolean> ci) {
        if (!level.isClientSide()) {
            try (var invokers = Stimuli.select().at(level, pos)) {
                var result = invokers.get(TntIgniteEvent.EVENT).onIgniteTnt((ServerLevel) level, pos, igniter);
                if (result == EventResult.DENY) {
                    level.setBlockAndUpdate(pos, Blocks.TNT.defaultBlockState());
                    ci.setReturnValue(false);
                }
            }
        }
    }
}
