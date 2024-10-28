package xyz.nucleoid.stimuli.event.world;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.item.ItemUsageContext;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when an end portal is attempted to be opened within the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further handlers and allows the portal to be opened.
 * <li>{@link EventResult#DENY} cancels further handlers and does not allow the portal to be opened.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 */
public interface EndPortalOpenEvent {
    StimulusEvent<EndPortalOpenEvent> EVENT = StimulusEvent.create(EndPortalOpenEvent.class, ctx -> (context, patternResult) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onOpenEndPortal(context, patternResult);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onOpenEndPortal(ItemUsageContext context, BlockPattern.Result patternResult);
}
