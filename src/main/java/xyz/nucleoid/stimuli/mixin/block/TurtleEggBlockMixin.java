package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockTrampleEvent;

@Mixin(TurtleEggBlock.class)
public class TurtleEggBlockMixin {
    @Inject(method = "destroyEgg", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TurtleEggBlock;decreaseEggs(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void trampleTurtleEgg(Level level, BlockState from, BlockPos pos, Entity entity, int inverseChance, CallbackInfo ci) {
        if (level instanceof ServerLevel serverWorld && entity instanceof LivingEntity livingEntity) {
            BlockState to = Blocks.AIR.defaultBlockState();
            if (from.hasProperty(TurtleEggBlock.EGGS)) {
                int eggs = from.getValue(TurtleEggBlock.EGGS);
                if (eggs > 1) {
                    to = from.setValue(TurtleEggBlock.EGGS, eggs - 1);
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
