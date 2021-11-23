package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockPlaceEvent;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private void onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> ci) {
        if (!(context.getPlayer() instanceof ServerPlayerEntity player)) {
            return;
        }

        var blockPos = context.getBlockPos();

        try (var invokers = Stimuli.select().forEntityAt(player, blockPos)) {
            var state = context.getWorld().getBlockState(blockPos);
            invokers.get(BlockPlaceEvent.AFTER).onPlace(player, player.getWorld(), blockPos, state);
        }
    }

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void onPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> ci) {
        if (!(context.getPlayer() instanceof ServerPlayerEntity player)) {
            return;
        }

        var blockPos = context.getBlockPos();

        try (var invokers = Stimuli.select().forEntityAt(player, blockPos)) {
            var result = invokers.get(BlockPlaceEvent.BEFORE).onPlace(player, player.getWorld(), blockPos, state, context);

            if (result == ActionResult.FAIL) {
                // notify the client that this action did not go through
                int slot = context.getHand() == Hand.MAIN_HAND ? player.getInventory().selectedSlot : 40;
                var stack = context.getStack();
                player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID, 0, slot, stack));

                ci.setReturnValue(false);
            }
        }
    }
}
