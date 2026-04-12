package xyz.nucleoid.stimuli.event.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link LivingEntity} attempts to trample a block, such as farmland or turtle eggs.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the trample.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the trample.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the trample succeeds.
 */
public interface BlockTrampleEvent {
    StimulusEvent<BlockTrampleEvent> EVENT = StimulusEvent.create(BlockTrampleEvent.class, ctx -> (entity, level, pos, from, to) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onTrample(entity, level, pos, from, to);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onTrample(LivingEntity entity, ServerLevel level, BlockPos pos, BlockState from, BlockState to);
}
