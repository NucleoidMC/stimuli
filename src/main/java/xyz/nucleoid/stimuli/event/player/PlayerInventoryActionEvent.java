package xyz.nucleoid.stimuli.event.player;

import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when any {@link ServerPlayerEntity} attempts to clicks in inventory.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the action.
 * <li>{@link ActionResult#FAIL} cancels further processing and cancels the action.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the attack succeeds.
 */
public interface PlayerInventoryActionEvent {
    StimulusEvent<PlayerInventoryActionEvent> EVENT = StimulusEvent.create(PlayerInventoryActionEvent.class, ctx -> (player, slot, actionType, button) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onInventoryAction(player, slot, actionType, button);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    ActionResult onInventoryAction(ServerPlayerEntity player, int slot, SlotActionType actionType, int button);
}
