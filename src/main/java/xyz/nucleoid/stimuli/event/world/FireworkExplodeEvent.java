package xyz.nucleoid.stimuli.event.world;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a firework attempts to explode in the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the firework to explode.
 * <li>{@link EventResult#DENY} cancels further handlers and makes the firework fizzle out.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface FireworkExplodeEvent {
    StimulusEvent<FireworkExplodeEvent> EVENT = StimulusEvent.create(FireworkExplodeEvent.class, ctx -> (firework) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onFireworkExplode(firework);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onFireworkExplode(FireworkRocketEntity firework);
}