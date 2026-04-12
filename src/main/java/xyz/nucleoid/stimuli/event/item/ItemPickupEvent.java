package xyz.nucleoid.stimuli.event.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayer} attempts to pick up an item entity from the level.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the item to be picked up.
 * <li>{@link EventResult#DENY} cancels further processing and prevents the item from being picked up.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the item may be picked up as normal.
 */
public interface ItemPickupEvent {
    StimulusEvent<ItemPickupEvent> EVENT = StimulusEvent.create(ItemPickupEvent.class, ctx -> {
        return (player, entity, stack) -> {
            try {
                for (var listener : ctx.getListeners()) {
                    var result = listener.onPickupItem(player, entity, stack);
                    if (result != EventResult.PASS) {
                        return result;
                    }
                }
            } catch (Throwable t) {
                ctx.handleException(t);
            }
            return EventResult.PASS;
        };
    });

    EventResult onPickupItem(ServerPlayer player, ItemEntity entity, ItemStack stack);
}
