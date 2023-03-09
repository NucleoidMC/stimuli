package xyz.nucleoid.stimuli.event.player;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface PlayerSpectateEntityEvent {
    StimulusEvent<PlayerSpectateEntityEvent> EVENT = StimulusEvent.create(PlayerSpectateEntityEvent.class, ctx -> (player, target) -> {
                try {
                    for (var listener : ctx.getListeners()) {
                        var result = listener.onSpectateEntity(player, target);
                        if (result != ActionResult.PASS) {
                            return result;
                        }
                    }
                } catch (Throwable t) {
                    ctx.handleException(t);
                }
                return ActionResult.PASS;
            });

    ActionResult onSpectateEntity(ServerPlayerEntity player, Entity target);
}
