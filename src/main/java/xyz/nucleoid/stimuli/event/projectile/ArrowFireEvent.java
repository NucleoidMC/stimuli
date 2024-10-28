package xyz.nucleoid.stimuli.event.projectile;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

public interface ArrowFireEvent {
    /**
     * Called when a {@link ServerPlayerEntity} fires an {@link ArrowEntity} from a bow or crossbow.
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

    EventResult onFireArrow(ServerPlayerEntity user, ItemStack tool, ArrowItem arrowItem, int remainingUseTicks, PersistentProjectileEntity projectile);
}
