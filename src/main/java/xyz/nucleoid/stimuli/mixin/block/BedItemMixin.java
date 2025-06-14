package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.BedItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockPlaceEvent;

@Mixin(BedItem.class)
public class BedItemMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void onPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> ci) {
        if (!(context.getPlayer() instanceof ServerPlayerEntity player)) {
            return;
        }

        var blockPos = context.getBlockPos();

        try (var invokers = Stimuli.select().forEntityAt(player, blockPos)) {
            var result = invokers.get(BlockPlaceEvent.BEFORE).onPlace(player, player.getWorld(), blockPos, state, context);

            if (result == EventResult.DENY) {
                // notify the client that this action did not go through
                int slot = context.getHand() == Hand.MAIN_HAND ? player.getInventory().getSelectedSlot() : 40;
                player.networkHandler.sendPacket(player.getInventory().createSlotSetPacket(slot));

                ci.setReturnValue(false);
            }
        }
    }
}
