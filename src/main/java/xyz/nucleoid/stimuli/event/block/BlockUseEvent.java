package xyz.nucleoid.stimuli.event.block;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to use a block by interacting.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the use.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the use.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the use succeeds and proceeds with normal logic.
 */
public interface BlockUseEvent {
    StimulusEvent<BlockUseEvent> EVENT = StimulusEvent.create(BlockUseEvent.class, ctx -> (player, hand, hitResult) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onUse(player, hand, hitResult);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onUse(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult);
}
