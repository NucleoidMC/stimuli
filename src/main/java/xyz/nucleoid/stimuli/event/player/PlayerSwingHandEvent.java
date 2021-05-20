package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} swings their hand.
 */
public interface PlayerSwingHandEvent {
    StimulusEvent<PlayerSwingHandEvent> EVENT = StimulusEvent.create(PlayerSwingHandEvent.class, ctx -> (player, hand) -> {
        try {
            for (PlayerSwingHandEvent listener : ctx.getListeners()) {
                listener.onSwingHand(player, hand);
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
    });

    void onSwingHand(ServerPlayerEntity player, Hand hand);
}
