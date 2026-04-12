package xyz.nucleoid.stimuli.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a wither is attempted to be summoned by building its structure in the level.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the wither to be summoned.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the wither to be summoned.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface WitherSummonEvent {
    StimulusEvent<WitherSummonEvent> EVENT = StimulusEvent.create(WitherSummonEvent.class, ctx -> (level, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSummonWither(level, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onSummonWither(ServerLevel level, BlockPos pos);
}
