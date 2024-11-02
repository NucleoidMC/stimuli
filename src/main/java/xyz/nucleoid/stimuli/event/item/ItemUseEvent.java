package xyz.nucleoid.stimuli.event.item;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to use an item by interacting.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#PASS} moves on to the next listener.
 * <li>Other results terminate the item use attempt with their normal side effects.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the use attempt proceeds with normal logic.
 */
public interface ItemUseEvent {
    StimulusEvent<ItemUseEvent> EVENT = StimulusEvent.create(ItemUseEvent.class, ctx -> (player, hand) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onUse(player, hand);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onUse(ServerPlayerEntity player, Hand hand);
}
