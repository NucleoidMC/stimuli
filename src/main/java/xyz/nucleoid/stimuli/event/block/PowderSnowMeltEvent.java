package xyz.nucleoid.stimuli.event.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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
    StimulusEvent<PowderSnowMeltEvent> EVENT = StimulusEvent.create(PowderSnowMeltEvent.class, ctx -> (entity, level, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onPowderSnowMelt(entity, level, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onPowderSnowMelt(Entity entity, ServerLevel level, BlockPos pos);
}
