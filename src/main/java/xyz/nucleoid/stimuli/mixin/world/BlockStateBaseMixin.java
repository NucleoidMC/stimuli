package xyz.nucleoid.stimuli.mixin.world;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockRandomTickEvent;


@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {
    @WrapWithCondition(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"))
    private boolean applyBlockRandomTickEvent(Block block, BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        try (var invokers = Stimuli.select().at(level, pos)) {
            var result = invokers.get(BlockRandomTickEvent.EVENT).onBlockRandomTick(level, pos, state);
            if (result == EventResult.DENY) {
                return false;
            }
        }

        return true;
    }
}
