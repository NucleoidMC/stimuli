package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.FireTickEvent;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @WrapOperation(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean test(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule, Operation<Boolean> original, @Local(argsOnly = true) ServerWorld world, @Local(argsOnly = true) BlockPos pos) {
        try (var invokers = Stimuli.select().at(world, pos)) {
            var result = invokers.get(FireTickEvent.EVENT).onFireTick(world, pos);
            if (result == EventResult.ALLOW) {
                return true;
            } else if (result == EventResult.DENY) {
                return false;
            }
        }

        return original.call(instance, rule);
    }
}
