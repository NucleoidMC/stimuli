package xyz.nucleoid.stimuli.event.world;

import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;

/**
 * Called when an {@link Explosion} is detonated in the level.
 * <p>
 * This event can be used to modify the blocks affected by the explosion, for example,
 * by modifying the passed list of block positions to destroy.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the explosion to be occur.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the explosion to occur.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface ExplosionDetonatedEvent {
    StimulusEvent<ExplosionDetonatedEvent> EVENT = StimulusEvent.create(ExplosionDetonatedEvent.class, ctx -> (explosion, blocksToDestroy) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onExplosionDetonated(explosion, blocksToDestroy);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onExplosionDetonated(Explosion explosion, List<BlockPos> blocksToDestroy);
}
