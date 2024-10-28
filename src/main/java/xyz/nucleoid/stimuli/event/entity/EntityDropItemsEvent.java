package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.stimuli.event.DroppedItemsResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

import java.util.List;

/**
 * Called when a {@link LivingEntity} drops its items on death.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and drops the current loot.
 * <li>{@link EventResult#DENY} cancels further processing and drops no loot.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * Listeners can modify the list of {@link ItemStack}s returned to them, regardless of what their result is.
 * If all listeners return {@link EventResult#PASS}, the current loot is dropped.
 */
public interface EntityDropItemsEvent {
    StimulusEvent<EntityDropItemsEvent> EVENT = StimulusEvent.create(EntityDropItemsEvent.class, ctx -> (dropper, items) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onDropItems(dropper, items);

                // modify items from listener (some may want to pass while still modifying items)
                items = result.dropStacks();

                // cancel early if allow or deny was returned by the listener
                if (result.result() != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return DroppedItemsResult.pass(items);
    });

    DroppedItemsResult onDropItems(LivingEntity dropper, List<ItemStack> items);
}
