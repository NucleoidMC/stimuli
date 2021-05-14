package xyz.nucleoid.stimuli.mixin.block;

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
import xyz.nucleoid.stimuli.EventInvokers;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.BlockEvents;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {
    @Inject(method = "onLandedUpon", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void breakFarmland(World world, BlockPos pos, Entity entity, float distance, CallbackInfo ci) {
        if (world instanceof ServerWorld) {
            try (EventInvokers invokers = Stimuli.select().at(world, pos)) {
                ActionResult result = invokers.get(BlockEvents.BREAK).onBreak((ServerPlayerEntity) entity, (ServerWorld) world, pos);
                if (result == ActionResult.FAIL) {
                    ci.cancel();
                }
            }
        }
    }
}
