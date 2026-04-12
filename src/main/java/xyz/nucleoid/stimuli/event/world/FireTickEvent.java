package xyz.nucleoid.stimuli.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when fire attempts to tick in the level.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the fire to tick.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the fire to tick.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface FireTickEvent {
    StimulusEvent<FireTickEvent> EVENT = StimulusEvent.create(FireTickEvent.class, ctx -> (level, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onFireTick(level, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onFireTick(ServerLevel level, BlockPos pos);
}
