package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.item.ItemThrowEvent;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Shadow @Final public DefaultedList<Slot> slots;

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void onSlotAction(int slot, int button, SlotActionType type, PlayerEntity player, CallbackInfo ci) {
        if (player.world.isClient) {
            return;
        }

        if (type == SlotActionType.THROW || type == SlotActionType.PICKUP) {
            ItemStack stack = null;
            if (type == SlotActionType.PICKUP && slot == -999) {
                stack = player.currentScreenHandler.getCursorStack();
            } else if (type == SlotActionType.THROW && slot >= 0 && slot < this.slots.size()) {
                stack = this.slots.get(slot).getStack();
            }

            if (stack != null) {
                if (this.shouldBlockThrowingItems(player, slot, stack)) {
                    player.currentScreenHandler.setCursorStack(stack);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "onClosed", at = @At("HEAD"))
    private void onClosed(PlayerEntity player, CallbackInfo ci) {
        var cursor = player.currentScreenHandler.getCursorStack();
        if (cursor.isEmpty()) {
            return;
        }

        if (this.shouldBlockThrowingItems(player, -999, cursor)) {
            if (player.getInventory().insertStack(cursor)) {
                player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
            }
        }
    }

    private boolean shouldBlockThrowingItems(PlayerEntity player, int slot, ItemStack stack) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            try (var invokers = Stimuli.select().forEntity(player)) {
                return invokers.get(ItemThrowEvent.EVENT)
                        .onThrowItem(serverPlayer, slot, stack) == ActionResult.FAIL;
            }
        }

        return false;
    }
}
