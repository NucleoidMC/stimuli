package xyz.nucleoid.stimuli.mixin.world;

import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
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
import xyz.nucleoid.stimuli.event.WorldEvents;

@Mixin(TntBlock.class)
public class TntBlockMixin {
    @Inject(method = "primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true)
    private static void primeTnt(World world, BlockPos pos, LivingEntity igniter, CallbackInfo ci) {
        if (!world.isClient) {
            try (EventInvokers invokers = Stimuli.select().at(world, pos)) {
                ActionResult result = invokers.get(WorldEvents.IGNITE_TNT).onIgniteTnt((ServerWorld) world, pos, igniter);
                if (result == ActionResult.FAIL) {
                    ci.cancel();
                }
            }
        }
    }
}
