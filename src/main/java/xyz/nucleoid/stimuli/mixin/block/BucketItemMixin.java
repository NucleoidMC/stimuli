package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.FluidPlaceEvent;

@Mixin(BucketItem.class)
public class BucketItemMixin {
    @Inject(
            method = "placeFluid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void onPlace(PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir, BlockState state) {
        if (world instanceof ServerWorld serverWorld) {
            var serverPlayer = player instanceof ServerPlayerEntity sp ? sp : null;
            var events = Stimuli.select();

            try (var invokers = player != null ? events.forEntityAt(player, pos) : events.at(world, pos)) {
                var result = invokers.get(FluidPlaceEvent.EVENT).onFluidPlace(serverWorld, pos, serverPlayer, hitResult);
                if (result == ActionResult.FAIL) {
                    if (serverPlayer != null) {
                        serverPlayer.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, state));
                    }
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
