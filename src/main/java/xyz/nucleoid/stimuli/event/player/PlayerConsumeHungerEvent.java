package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} loses hunger.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the player to lose hunger.
 * <li>{@link EventResult#DENY} cancels further processing and prevents the player from losing hunger.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface PlayerConsumeHungerEvent {
    StimulusEvent<PlayerConsumeHungerEvent> EVENT = StimulusEvent.create(PlayerConsumeHungerEvent.class, ctx -> (player, foodLevel, saturation, exhaustion) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onConsumeHunger(player, foodLevel, saturation, exhaustion);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onConsumeHunger(ServerPlayerEntity player, int foodLevel, float saturation, float exhaustion);
}
