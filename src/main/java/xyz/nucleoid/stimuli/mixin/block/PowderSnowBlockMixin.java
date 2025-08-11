package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.PowderSnowMeltEvent;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
    @WrapWithCondition(method = "method_67681", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private static boolean applyPowderSnowMeltEvent(World world, BlockPos pos, boolean drop, @Local Entity entity) {
        if (world instanceof ServerWorld serverWorld) {
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
