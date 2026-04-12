package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockTrampleEvent;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {
    @Inject(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmlandBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void breakFarmland(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        if (level instanceof ServerLevel serverWorld && entity instanceof LivingEntity livingEntity) {
            try (var invokers = Stimuli.select().forEntityAt(entity, pos)) {
                var trampleResult = invokers.get(BlockTrampleEvent.EVENT).onTrample(livingEntity, serverWorld, pos, state, Blocks.DIRT.defaultBlockState());
                if (trampleResult == EventResult.DENY) {
                    ci.cancel();
                    return;
                }

                if (livingEntity instanceof ServerPlayer player) {
                    var breakResult = invokers.get(BlockBreakEvent.EVENT).onBreak(player, serverWorld, pos);
                    if (breakResult == EventResult.DENY) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
