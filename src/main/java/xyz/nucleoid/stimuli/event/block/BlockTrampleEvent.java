package xyz.nucleoid.stimuli.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
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
    StimulusEvent<BlockTrampleEvent> EVENT = StimulusEvent.create(BlockTrampleEvent.class, ctx -> (entity, world, pos, from, to) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onTrample(entity, world, pos, from, to);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onTrample(LivingEntity entity, ServerWorld world, BlockPos pos, BlockState from, BlockState to);
}
