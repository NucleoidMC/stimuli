package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {
    @Inject(method = "onLandedUpon", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void breakFarmland(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (world instanceof ServerWorld serverWorld && entity instanceof ServerPlayerEntity player) {
            try (var invokers = Stimuli.select().forEntityAt(player, pos)) {
                var result = invokers.get(BlockBreakEvent.EVENT).onBreak(player, serverWorld, pos);
                if (result == ActionResult.FAIL) {
                    ci.cancel();
                }
            }
        }
    }
}
