package xyz.nucleoid.stimuli.mixin.block;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"), cancellable = true)
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        var world = context.getWorld();
        if (!context.getWorld().isClient()) {
            var events = Stimuli.select();
            var player = context.getPlayer();
            var pos = context.getBlockPos();
            try (var invokers = player == null ? events.at(world, pos) : events.forEntityAt(player, pos)) {
                var result = invokers.get(BlockUseEvent.USE_ITEM).onItemUseOnBlock((ItemStack) (Object) this, context);

                if (result == ActionResult.FAIL) {
                    // notify the client that this action did not go through
                    int slot = context.getHand() == Hand.MAIN_HAND ? player.getInventory().selectedSlot : 40;
                    var stack = context.getStack();
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID, 0, slot, stack));

                    cir.setReturnValue(ActionResult.FAIL);
                }
            }
        }
    }
}
