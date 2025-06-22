package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
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
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.FlowerPotModifyEvent;
import xyz.nucleoid.stimuli.util.SlotHelper;

@Mixin(FlowerPotBlock.class)
public class FlowerPotBlockMixin {
    @Inject(method = "onUseWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), cancellable = true)
    private void onModifyFlowerPot0(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> ci) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntityAt(serverPlayer, pos)) {
            var result = invokers.get(FlowerPotModifyEvent.EVENT).onModifyFlowerPot(serverPlayer, hand, hitResult);

            if (result == EventResult.DENY) {
                // notify the client that this action did not go through
                int slot = SlotHelper.getHandSlot(serverPlayer, hand);
                SlotHelper.updateSlot(serverPlayer, slot);

                ci.setReturnValue(ActionResult.CONSUME);
            }
        }
    }

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;giveItemStack(Lnet/minecraft/item/ItemStack;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void onModifyFlowerPot1(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> ci, @Local ItemStack stack) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntityAt(serverPlayer, pos)) {
            var result = invokers.get(FlowerPotModifyEvent.EVENT).onModifyFlowerPot(serverPlayer, Hand.MAIN_HAND, hitResult);

            if (result == EventResult.DENY) {
                // notify the client that this action did not go through
                int slot = SlotHelper.getFirstModifiedSlot(serverPlayer, stack);
                SlotHelper.updateSlot(serverPlayer, slot);

                ci.setReturnValue(ActionResult.CONSUME);
            }
        }
    }
}
