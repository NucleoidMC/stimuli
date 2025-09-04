package xyz.nucleoid.stimuli.event.block;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when an entity attempts to melt powder snow.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the powder snow to melt.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the powder snow to melt.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface PowderSnowMeltEvent {
    StimulusEvent<PowderSnowMeltEvent> EVENT = StimulusEvent.create(PowderSnowMeltEvent.class, ctx -> (entity, world, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPowderSnowMelt(entity, world, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onPowderSnowMelt(Entity entity, ServerWorld world, BlockPos pos);
}
