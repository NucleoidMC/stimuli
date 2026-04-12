package xyz.nucleoid.stimuli.event.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerInput;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayer} attempts to clicks in inventory.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the action.
 * <li>{@link EventResult#DENY} cancels further processing and cancels the action.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the attack succeeds.
 */
public interface PlayerInventoryActionEvent {
    StimulusEvent<PlayerInventoryActionEvent> EVENT = StimulusEvent.create(PlayerInventoryActionEvent.class, ctx -> (player, slot, actionType, button) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onInventoryAction(player, slot, actionType, button);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onInventoryAction(ServerPlayer player, int slot, ContainerInput actionType, int button);
}
