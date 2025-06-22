package xyz.nucleoid.stimuli.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public final class SlotHelper {
    private SlotHelper() {
    }

    /**
     * Updates the client about a single slot to match the server's state.
     */
    public static void updateSlot(ServerPlayerEntity player, int slot) {
        player.networkHandler.sendPacket(player.getInventory().createSlotSetPacket(slot));
    }

	public static int getHandSlot(ServerPlayerEntity player, Hand hand) {
		return hand == Hand.MAIN_HAND ? player.getInventory().getSelectedSlot() : PlayerInventory.OFF_HAND_SLOT;
	}

    /**
     * Determines the first slot that would be affected by {@link PlayerInventory#insertStack(ItemStack)}.
     */
    public static int getFirstModifiedSlot(ServerPlayerEntity player, ItemStack stack) {
        var inventory = player.getInventory();

        int slot = inventory.getOccupiedSlotWithRoomForStack(stack);
        return slot == -1 ? inventory.getEmptySlot() : slot;
    }
}
