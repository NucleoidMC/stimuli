package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a coral or coral fan block dies.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the coral to die.
 * <li>{@link EventResult#DENY} cancels further processing and prevents the coral from dying.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the coral dies.
 */
public interface CoralDeathEvent {
    StimulusEvent<CoralDeathEvent> EVENT = StimulusEvent.create(CoralDeathEvent.class, ctx -> (world, pos, from, to) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onCoralDeath(world, pos, from, to);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onCoralDeath(ServerWorld world, BlockPos pos, BlockState from, BlockState to);
}
