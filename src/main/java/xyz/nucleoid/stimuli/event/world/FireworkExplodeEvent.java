package xyz.nucleoid.stimuli.event.world;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a firework attempts to explode in the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further handlers and allows the firework to explode.
 * <li>{@link ActionResult#FAIL} cancels further handlers and makes the firework fizzle out.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 */
public interface FireworkExplodeEvent {
    StimulusEvent<FireworkExplodeEvent> EVENT = StimulusEvent.create(FireworkExplodeEvent.class, ctx -> (firework) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onFireworkExplode(firework);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onFireworkExplode(FireworkRocketEntity firework);
}