package xyz.nucleoid.stimuli.event.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayer} attempts to use an item by interacting.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link InteractionResult#PASS} moves on to the next listener.
 * <li>Other results terminate the item use attempt with their normal side effects.</ul>
 * <p>
 * If all listeners return {@link InteractionResult#PASS}, the use attempt proceeds with normal logic.
 */
public interface ItemUseEvent {
    StimulusEvent<ItemUseEvent> EVENT = StimulusEvent.create(ItemUseEvent.class, ctx -> (player, hand) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onUse(player, hand);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return InteractionResult.PASS;
    });

    InteractionResult onUse(ServerPlayer player, InteractionHand hand);
}
