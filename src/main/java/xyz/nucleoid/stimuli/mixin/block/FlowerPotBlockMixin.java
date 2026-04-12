package xyz.nucleoid.stimuli.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), cancellable = true)
    private void onModifyFlowerPot0(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntityAt(serverPlayer, pos)) {
            var result = invokers.get(FlowerPotModifyEvent.EVENT).onModifyFlowerPot(serverPlayer, hand, hitResult);

            if (result == EventResult.DENY) {
                // notify the client that this action did not go through
                int slot = SlotHelper.getHandSlot(serverPlayer, hand);
                SlotHelper.updateSlot(serverPlayer, slot);

                ci.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addItem(Lnet/minecraft/world/item/ItemStack;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void onModifyFlowerPot1(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> ci, @Local ItemStack stack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        try (var invokers = Stimuli.select().forEntityAt(serverPlayer, pos)) {
            var result = invokers.get(FlowerPotModifyEvent.EVENT).onModifyFlowerPot(serverPlayer, InteractionHand.MAIN_HAND, hitResult);

            if (result == EventResult.DENY) {
                // notify the client that this action did not go through
                int slot = SlotHelper.getFirstModifiedSlot(serverPlayer, stack);
                SlotHelper.updateSlot(serverPlayer, slot);

                ci.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }
}
