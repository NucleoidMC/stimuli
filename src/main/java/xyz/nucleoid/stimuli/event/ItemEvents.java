package xyz.nucleoid.stimuli.event;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;

public final class ItemEvents {
    public static final StimulusEvent<Use> USE = StimulusEvent.create(Use.class, ctx -> {
        return (player, hand) -> {
            try {
                for (Use listener : ctx.getListeners()) {
                    TypedActionResult<ItemStack> result = listener.onUse(player, hand);
                    if (result.getResult() != ActionResult.PASS) {
                        return result;
                    }
                }
            } catch (Throwable t) {
                ctx.handleException(t);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        };
    });

    public static final StimulusEvent<Craft> CRAFT = StimulusEvent.create(Craft.class, ctx -> {
        return (player, recipe) -> {
            try {
                for (Craft listener : ctx.getListeners()) {
                    ActionResult result = listener.onCraft(player, recipe);
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

    public static final StimulusEvent<Pickup> PICKUP = StimulusEvent.create(Pickup.class, ctx -> {
        return (player, entity, stack) -> {
            try {
                for (Pickup listener : ctx.getListeners()) {
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

    /**
     * Called when a {@link ServerPlayerEntity} attempts to drop an item, from the hotbar or from the inventory.
     * Do note that the provided slot may be negative on certain circumstances, so proceed with caution.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further handlers and drops the item.
     * <li>{@link ActionResult#FAIL} cancels further handlers and does not drop the item.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     */
    public static final StimulusEvent<Throw> THROW = StimulusEvent.create(Throw.class, ctx -> (player, slot, stack) -> {
        try {
            for (Throw listener : ctx.getListeners()) {
                ActionResult result = listener.onThrowItem(player, slot, stack);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });


    public interface Throw {
        ActionResult onThrowItem(ServerPlayerEntity player, int slot, ItemStack stack);
    }

    public interface Use {
        TypedActionResult<ItemStack> onUse(ServerPlayerEntity player, Hand hand);
    }

    public interface Craft {
        ActionResult onCraft(ServerPlayerEntity player, Recipe<?> recipe);
    }

    public interface Pickup {
        ActionResult onPickupItem(ServerPlayerEntity player, ItemEntity entity, ItemStack stack);
    }
}
