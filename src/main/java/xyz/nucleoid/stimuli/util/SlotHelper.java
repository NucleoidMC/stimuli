package xyz.nucleoid.stimuli.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class SlotHelper {
    private SlotHelper() {
    }

    /**
     * Updates the client about a single slot to match the server's state.
     */
    public static void updateSlot(ServerPlayer player, int slot) {
        player.connection.send(player.getInventory().createInventoryUpdatePacket(slot));
    }

	public static int getHandSlot(ServerPlayer player, InteractionHand hand) {
		return hand == InteractionHand.MAIN_HAND ? player.getInventory().getSelectedSlot() : Inventory.SLOT_OFFHAND;
	}

    /**
     * Determines the first slot that would be affected by {@link Inventory#add(ItemStack)}.
     */
    public static int getFirstModifiedSlot(ServerPlayer player, ItemStack stack) {
        var inventory = player.getInventory();

        int slot = inventory.getSlotWithRemainingSpace(stack);
        return slot == -1 ? inventory.getFreeSlot() : slot;
    }
}
