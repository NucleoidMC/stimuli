package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.FlowerPotBlock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link FlowerPotBlock} is interacted with in a way that would cause the contents of the flower pot to change.
 * An empty stack in the given hand represents a flower pot being emptied, while a non-empty stack represents a flower being potted.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the flower pot to be modified.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the modification.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the modification succeeds and proceeds with normal logic.
 */
public interface FlowerPotModifyEvent {
    StimulusEvent<FlowerPotModifyEvent> EVENT = StimulusEvent.create(FlowerPotModifyEvent.class, ctx -> (player, hand, hitResult) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onModifyFlowerPot(player, hand, hitResult);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onModifyFlowerPot(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult);
}