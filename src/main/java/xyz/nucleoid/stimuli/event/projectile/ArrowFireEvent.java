package xyz.nucleoid.stimuli.event.projectile;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface ArrowFireEvent {
    /**
     * Called when a {@link ServerPlayer} fires an {@link Arrow} from a bow or crossbow.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link EventResult#ALLOW} cancels further handlers and executes vanilla behavior.
     * <li>{@link EventResult#DENY} cancels further handlers and does not execute vanilla behavior.
     * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
     */
    StimulusEvent<ArrowFireEvent> EVENT = StimulusEvent.create(ArrowFireEvent.class, ctx -> (user, tool, arrows, remaining, projectile) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onFireArrow(user, tool, arrows, remaining, projectile);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onFireArrow(ServerPlayer user, ItemStack tool, ArrowItem arrowItem, int remainingUseTicks, AbstractArrow projectile);
}
