package xyz.nucleoid.stimuli.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when snow attempts to fall in the level.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the snow to fall.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the snow to fall.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface SnowFallEvent {
    StimulusEvent<SnowFallEvent> EVENT = StimulusEvent.create(SnowFallEvent.class, ctx -> (level, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSnowFall(level, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onSnowFall(ServerLevel level, BlockPos pos);
}
