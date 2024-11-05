package xyz.nucleoid.stimuli.event.item;

import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called when a {@link ServerPlayerEntity} attempts to craft an item either in a crafting table or in their inventory.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and allows the item to be crafted.
 * <li>{@link EventResult#DENY} cancels further processing and prevents the player from crafting.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the item can be crafted normally.
 */
public interface ItemCraftEvent {
    StimulusEvent<ItemCraftEvent> EVENT = StimulusEvent.create(ItemCraftEvent.class, ctx -> {
        return (player, recipe) -> {
            try {
                for (var listener : ctx.getListeners()) {
                    var result = listener.onCraft(player, recipe);
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

    EventResult onCraft(ServerPlayerEntity player, Recipe<?> recipe);
}
