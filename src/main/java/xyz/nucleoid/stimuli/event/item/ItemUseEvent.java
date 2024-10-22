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
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the use.
 * <li>{@link ActionResult#FAIL} cancels further processing and cancels the use.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the use succeeds and proceeds with normal logic.
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
