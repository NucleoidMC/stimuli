package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.world.TntIgniteEvent;

@Mixin(TntBlock.class)
public class TntBlockMixin {
    @Inject(method = "primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void primeTnt(World world, BlockPos pos, LivingEntity igniter, CallbackInfoReturnable<Boolean> ci) {
        if (!world.isClient) {
            try (var invokers = Stimuli.select().at(world, pos)) {
                var result = invokers.get(TntIgniteEvent.EVENT).onIgniteTnt((ServerWorld) world, pos, igniter);
                if (result == EventResult.DENY) {
                    world.setBlockState(pos, Blocks.TNT.getDefaultState());
                    ci.setReturnValue(false);
                }
            }
        }
    }
}
