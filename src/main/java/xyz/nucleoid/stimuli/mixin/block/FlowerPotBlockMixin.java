package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.FlowerPotModifyEvent;

@Mixin(FlowerPotBlock.class)
public class FlowerPotBlockMixin {
    @Inject(method = "onUseWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), cancellable = true)
    private void onModifyFlowerPot0(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> ci) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntityAt(serverPlayer, pos)) {
            var result = invokers.get(FlowerPotModifyEvent.EVENT).onModifyFlowerPot(serverPlayer, hand, hitResult);

            if (result == ActionResult.FAIL) {
                // notify the client that this action did not go through
                int slot = hand == Hand.MAIN_HAND ? serverPlayer.getInventory().selectedSlot : 40;
                serverPlayer.networkHandler.sendPacket(serverPlayer.getInventory().createSlotSetPacket(slot));

                ci.setReturnValue(ActionResult.CONSUME);
            }
        }
    }

    @Inject(method = "onUse", at = @At(value = "NEW", target = "Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE), cancellable = true)
    private void onModifyFlowerPot1(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> ci) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntityAt(serverPlayer, pos)) {
            var result = invokers.get(FlowerPotModifyEvent.EVENT).onModifyFlowerPot(serverPlayer, Hand.MAIN_HAND, hitResult);
            //Maybe try to update the slot the item shows at and update it to fix desync
            if (result == ActionResult.FAIL) {
                ci.setReturnValue(ActionResult.CONSUME);
            }
        }
    }
}
