package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockTrampleEvent;

@Mixin(TurtleEggBlock.class)
public class TurtleEggBlockMixin {
    @Inject(method = "tryBreakEgg", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/TurtleEggBlock;breakEgg(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void trampleTurtleEgg(World world, BlockState from, BlockPos pos, Entity entity, int inverseChance, CallbackInfo ci) {
        if (world instanceof ServerWorld serverWorld && entity instanceof LivingEntity livingEntity) {
            BlockState to = Blocks.AIR.getDefaultState();
            if (from.contains(TurtleEggBlock.EGGS)) {
                int eggs = from.get(TurtleEggBlock.EGGS);
                if (eggs > 1) {
                    to = from.with(TurtleEggBlock.EGGS, eggs - 1);
                }
            }

            try (var invokers = Stimuli.select().forEntityAt(entity, pos)) {
                var trampleResult = invokers.get(BlockTrampleEvent.EVENT).onTrample(livingEntity, serverWorld, pos, from, to);
                if (trampleResult == EventResult.DENY) {
                    ci.cancel();
                }
            }
        }
    }
}
