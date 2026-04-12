package xyz.nucleoid.stimuli.mixin.player;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.item.ItemThrowEvent;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Shadow @Final public NonNullList<Slot> slots;

    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void onSlotAction(int slot, int button, ContainerInput type, Player player, CallbackInfo ci) {
        if (player.level().isClientSide()) {
            return;
        }

        if (type == ContainerInput.THROW || type == ContainerInput.PICKUP) {
            ItemStack stack = null;
            if (type == ContainerInput.PICKUP && slot == -999) {
                stack = player.containerMenu.getCarried();
            } else if (type == ContainerInput.THROW && slot >= 0 && slot < this.slots.size()) {
                stack = this.slots.get(slot).getItem();
            }

            if (stack != null) {
                if (this.shouldBlockThrowingItems(player, slot, stack)) {
                    player.containerMenu.setCarried(stack);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onClosed(Player player, CallbackInfo ci) {
        var cursor = player.containerMenu.getCarried();
        if (cursor.isEmpty()) {
            return;
        }

        if (this.shouldBlockThrowingItems(player, -999, cursor)) {
            if (player.getInventory().add(cursor)) {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
        }
    }

    private boolean shouldBlockThrowingItems(Player player, int slot, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            try (var invokers = Stimuli.select().forEntity(player)) {
                return invokers.get(ItemThrowEvent.EVENT)
                        .onThrowItem(serverPlayer, slot, stack) == EventResult.DENY;
            }
        }

        return false;
    }
}
