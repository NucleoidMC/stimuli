package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.PowderSnowMeltEvent;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
    @WrapWithCondition(method = "lambda$entityInside$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private static boolean applyPowderSnowMeltEvent(Level level, BlockPos pos, boolean drop, @Local Entity entity) {
        if (level instanceof ServerLevel serverWorld) {
            try (var invokers = Stimuli.select().at(serverWorld, pos)) {
                var result = invokers.get(PowderSnowMeltEvent.EVENT).onPowderSnowMelt(entity, serverWorld, pos);
                if (result == EventResult.DENY) {
                    return false;
                }
            }
        }

        return true;
    }
}
