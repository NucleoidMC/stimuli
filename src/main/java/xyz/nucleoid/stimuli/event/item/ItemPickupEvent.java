package xyz.nucleoid.stimuli.event.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to pick up an item entity from the world.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the item to be picked up.
 * <li>{@link ActionResult#FAIL} cancels further processing and prevents the item from being picked up.
 * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link ActionResult#PASS}, the item may be picked up as normal.
 */
public interface ItemPickupEvent {
    StimulusEvent<ItemPickupEvent> EVENT = StimulusEvent.create(ItemPickupEvent.class, ctx -> {
        return (player, entity, stack) -> {
            try {
                for (ItemPickupEvent listener : ctx.getListeners()) {
                    ActionResult result = listener.onPickupItem(player, entity, stack);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
            } catch (Throwable t) {
                ctx.handleException(t);
            }
            return ActionResult.PASS;
        };
    });

    ActionResult onPickupItem(ServerPlayerEntity player, ItemEntity entity, ItemStack stack);
}
