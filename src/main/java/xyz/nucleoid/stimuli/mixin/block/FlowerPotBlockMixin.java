package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.FlowerPotModifyEvent;

@Mixin(FlowerPotBlock.class)
public class FlowerPotBlockMixin {
    // After 'if (noPottedBlock != this.isEmpty()) {'
    @Inject(method = "onUse", at = @At(value = "JUMP", opcode = Opcodes.IF_ICMPEQ, ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private void onModifyFlowerPot(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> ci) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntityAt(serverPlayer, pos)) {
            var result = invokers.get(FlowerPotModifyEvent.EVENT).onModifyFlowerPot(serverPlayer, hand, hitResult);

            if (result == ActionResult.FAIL) {
                // notify the client that this action did not go through
                int slot = hand == Hand.MAIN_HAND ? serverPlayer.getInventory().selectedSlot : 40;
                var stack = serverPlayer.getStackInHand(hand);
                serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID, 0, slot, stack));

                ci.setReturnValue(ActionResult.CONSUME);
            }
        }
    }
}
