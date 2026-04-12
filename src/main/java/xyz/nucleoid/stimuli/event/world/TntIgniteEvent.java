package xyz.nucleoid.stimuli.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when TNT is ignited within the level.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the TNT to be ignited.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the TNT to be ignited.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface TntIgniteEvent {
    StimulusEvent<TntIgniteEvent> EVENT = StimulusEvent.create(TntIgniteEvent.class, ctx -> (level, pos, igniter) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onIgniteTnt(level, pos, igniter);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onIgniteTnt(ServerLevel level, BlockPos pos, @Nullable LivingEntity igniter);
}
