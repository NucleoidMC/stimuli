package xyz.nucleoid.stimuli.event.item;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to drop an item, from the hotbar or from the inventory.
 * Do note that the provided slot may be negative on certain circumstances, so proceed with caution.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and drops the item.
 * <li>{@link EventResult#DENY} cancels further handlers and does not drop the item.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface ItemThrowEvent {
    StimulusEvent<ItemThrowEvent> EVENT = StimulusEvent.create(ItemThrowEvent.class, ctx -> (player, slot, stack) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onThrowItem(player, slot, stack);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onThrowItem(ServerPlayerEntity player, int slot, ItemStack stack);
}
