package xyz.nucleoid.stimuli.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a nether portal is attempted to be opened within the level.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the portal to be opened.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the portal to be opened.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface NetherPortalOpenEvent {
    StimulusEvent<NetherPortalOpenEvent> EVENT = StimulusEvent.create(NetherPortalOpenEvent.class, ctx -> (level, lowerCorner) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onOpenNetherPortal(level, lowerCorner);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onOpenNetherPortal(ServerLevel level, BlockPos lowerCorner);
}
