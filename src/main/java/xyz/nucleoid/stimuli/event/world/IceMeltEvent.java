package xyz.nucleoid.stimuli.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when ice attempts to be melted in the level.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the ice to melt.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the ice to melt.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface IceMeltEvent {
    StimulusEvent<IceMeltEvent> EVENT = StimulusEvent.create(IceMeltEvent.class, ctx -> (level, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onIceMelt(level, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onIceMelt(ServerLevel level, BlockPos pos);
}
