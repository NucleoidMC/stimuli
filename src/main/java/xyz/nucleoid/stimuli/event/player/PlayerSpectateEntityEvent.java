package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface PlayerSpectateEntityEvent {
    StimulusEvent<PlayerSpectateEntityEvent> EVENT = StimulusEvent.create(PlayerSpectateEntityEvent.class, ctx -> (player, target) -> {
                try {
                    for (var listener : ctx.getListeners()) {
                        var result = listener.onSpectateEntity(player, target);
                        if (result != EventResult.PASS) {
                            return result;
                        }
                    }
                } catch (Throwable t) {
                    ctx.handleException(t);
                }
                return EventResult.PASS;
            });

    EventResult onSpectateEntity(ServerPlayer player, Entity target);
}
