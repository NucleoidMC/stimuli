package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayer} swings their hand.
 */
public interface PlayerSwingHandEvent {
    StimulusEvent<PlayerSwingHandEvent> EVENT = StimulusEvent.create(PlayerSwingHandEvent.class, ctx -> (player, hand) -> {
        try {
            for (var listener : ctx.getListeners()) {
                listener.onSwingHand(player, hand);
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
    });

    void onSwingHand(ServerPlayer player, InteractionHand hand);
}
