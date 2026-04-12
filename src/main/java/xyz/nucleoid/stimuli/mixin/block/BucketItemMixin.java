package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.BlockHitResult;
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
            method = "emptyContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"
            ),
            cancellable = true
    )
    private void onPlace(LivingEntity user, Level level, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir, @Local FlowingFluid fluid, @Local BlockState state) {
        if (level instanceof ServerLevel serverWorld) {
            var serverPlayer = user instanceof ServerPlayer sp ? sp : null;
            var events = Stimuli.select();

            try (var invokers = user != null ? events.forEntityAt(user, pos) : events.at(level, pos)) {
                var result = invokers.get(FluidPlaceEvent.EVENT).onFluidPlace(serverWorld, pos, serverPlayer, hitResult);
                if (result == EventResult.DENY) {
                    if (serverPlayer != null) {
                        serverPlayer.connection.send(new ClientboundBlockUpdatePacket(pos, state));
                    }
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
