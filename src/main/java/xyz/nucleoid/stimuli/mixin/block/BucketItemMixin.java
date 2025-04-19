package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.FluidPlaceEvent;

@Mixin(BucketItem.class)
public class BucketItemMixin {
    @Inject(
            method = "placeFluid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"
            ),
            cancellable = true
    )
    private void onPlace(LivingEntity user, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir, @Local FlowableFluid fluid, @Local BlockState state) {
        if (world instanceof ServerWorld serverWorld) {
            var serverPlayer = user instanceof ServerPlayerEntity sp ? sp : null;
            var events = Stimuli.select();

            try (var invokers = user != null ? events.forEntityAt(user, pos) : events.at(world, pos)) {
                var result = invokers.get(FluidPlaceEvent.EVENT).onFluidPlace(serverWorld, pos, serverPlayer, hitResult);
                if (result == EventResult.DENY) {
                    if (serverPlayer != null) {
                        serverPlayer.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, state));
                    }
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
