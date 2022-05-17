package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockRandomTickEvent;

import java.util.Random;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V"))
    private void applyBlockRandomTickEvent(Block block, BlockState state, ServerWorld world, BlockPos pos, Random random) {
        try (var invokers = Stimuli.select().at(world, pos)) {
            var result = invokers.get(BlockRandomTickEvent.EVENT).onBlockRandomTick(world, pos, state);
            if (result == ActionResult.FAIL) {
                return;
            }
        }

        block.randomTick(state, world, pos, random);
    }}
