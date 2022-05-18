package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.world.FireTickEvent;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    private boolean test(GameRules gameRules, GameRules.Key<GameRules.BooleanRule> rule, BlockState state, ServerWorld world, BlockPos pos, AbstractRandom random) {
        try (var invokers = Stimuli.select().at(world, pos)) {
            var result = invokers.get(FireTickEvent.EVENT).onFireTick(world, pos);
            if (result == ActionResult.SUCCESS) {
                return true;
            } else if (result == ActionResult.FAIL) {
                return false;
            }
        }

        return gameRules.getBoolean(GameRules.DO_FIRE_TICK);
    }
}
