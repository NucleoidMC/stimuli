package xyz.nucleoid.stimuli.event.world;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a wither is attempted to be summoned by building its structure in the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the wither to be summoned.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the wither to be summoned.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface WitherSummonEvent {
    StimulusEvent<WitherSummonEvent> EVENT = StimulusEvent.create(WitherSummonEvent.class, ctx -> (world, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onSummonWither(world, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onSummonWither(ServerWorld world, BlockPos pos);
}
