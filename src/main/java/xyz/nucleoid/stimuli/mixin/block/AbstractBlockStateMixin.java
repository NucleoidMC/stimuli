package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient()) {
            var events = Stimuli.select();
            try (var invokers = events.forEntityAt(player, hit.getBlockPos())) {
                var state = world.getBlockState(hit.getBlockPos());
                var result = invokers.get(BlockUseEvent.USE).onBlockUse(state, world, hit.getBlockPos(), player, hand, hit);

                if (result == ActionResult.FAIL) {
                    // notify the client that this action did not go through
                    int slot = hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : 40;
                    var stack = player.getStackInHand(hand);
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID, 0, slot, stack));

                    cir.setReturnValue(ActionResult.FAIL);
                }
            }
        }
    }
}
