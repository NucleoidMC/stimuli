package xyz.nucleoid.stimuli.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class PlayerEvents {
    /**
     * Called when a {@link ServerPlayerEntity} dies.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and kills the player.
     * <li>{@link ActionResult#FAIL} cancels further processing and does not kill the player.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the player is killed.
     */
    public static final StimulusEvent<Death> DEATH = StimulusEvent.create(Death.class, ctx -> (player, source) -> {
        try {
            for (Death listener : ctx.getListeners()) {
                ActionResult result = listener.onDeath(player, source);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public static final StimulusEvent<Damage> DAMAGE = StimulusEvent.create(Damage.class, ctx -> (player, source, amount) -> {
        try {
            for (Damage listener : ctx.getListeners()) {
                ActionResult result = listener.onDamage(player, source, amount);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    /**
     * Called when a {@link ServerPlayerEntity} sends a message in chat.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the message to be sent.
     * <li>{@link ActionResult#FAIL} cancels further processing and the message being sent.
     * <li>{@link ActionResult#PASS} moves on to the next listener.
     * </ul>
     */
    public static final StimulusEvent<Chat> CHAT = StimulusEvent.create(Chat.class, ctx -> (sender, message) -> {
        try {
            for (Chat listener : ctx.getListeners()) {
                ActionResult result = listener.onSendChatMessage(sender, message);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
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
    public static final StimulusEvent<ThrowItem> THROW_ITEM = StimulusEvent.create(ThrowItem.class, ctx -> (player, slot, stack) -> {
        try {
            for (ThrowItem listener : ctx.getListeners()) {
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

    public static final StimulusEvent<SwingHand> SWING_HAND = StimulusEvent.create(SwingHand.class, ctx -> (player, hand) -> {
        try {
            for (SwingHand listener : ctx.getListeners()) {
                listener.onSwingHand(player, hand);
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
    });

    /**
     * Called when any {@link ServerPlayerEntity} attempts to attack another {@link Entity}.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the attack.
     * <li>{@link ActionResult#FAIL} cancels further processing and cancels the attack.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the attack succeeds.
     */
    public static final StimulusEvent<AttackEntity> ATTACK_ENTITY = StimulusEvent.create(AttackEntity.class, ctx -> (attacker, hand, attacked, hitResult) -> {
        try {
            for (AttackEntity listener : ctx.getListeners()) {
                ActionResult result = listener.onAttackEntity(attacker, hand, attacked, hitResult);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    /**
     * Called when a {@link ServerPlayerEntity} fires an {@link ArrowEntity},
     * either with a bow or with a crossbow.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further handlers and executes vanilla behavior.
     * <li>{@link ActionResult#FAIL} cancels further handlers and does not execute vanilla behavior.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     */
    public static final StimulusEvent<FireArrow> FIRE_ARROW = StimulusEvent.create(FireArrow.class, ctx -> (user, tool, arrows, remaining, projectile) -> {
        try {
            for (FireArrow listener : ctx.getListeners()) {
                ActionResult result = listener.onFireArrow(user, tool, arrows, remaining, projectile);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return ActionResult.PASS;
    });

    public static final StimulusEvent<UseBlock> USE_BLOCK = StimulusEvent.create(UseBlock.class, ctx -> {
        return (player, hand, hitResult) -> {
            try {
                for (UseBlock listener : ctx.getListeners()) {
                    ActionResult result = listener.onUseBlock(player, hand, hitResult);
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

    public static final StimulusEvent<UseItem> USE_ITEM = StimulusEvent.create(UseItem.class, ctx -> {
        return (player, hand) -> {
            try {
                for (UseItem listener : ctx.getListeners()) {
                    TypedActionResult<ItemStack> result = listener.onUseItem(player, hand);
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

    public static final StimulusEvent<UseEntity> USE_ENTITY = StimulusEvent.create(UseEntity.class, ctx -> {
        return (player, entity, hand, hitResult) -> {
            try {
                for (UseEntity listener : ctx.getListeners()) {
                    ActionResult result = listener.onUseEntity(player, entity, hand, hitResult);
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
     * Called when a {@link ServerPlayerEntity} attempts to punch a block.
     *
     * <p>Upon return:
     * <ul>
     * <li>{@link ActionResult#SUCCESS} cancels further processing and allows the punch.
     * <li>{@link ActionResult#FAIL} cancels further processing and cancels the punch.
     * <li>{@link ActionResult#PASS} moves on to the next listener.</ul>
     * <p>
     * If all listeners return {@link ActionResult#PASS}, the punch succeeds and the player could begin to break the block.
     */
    public static final StimulusEvent<PunchBlock> PUNCH_BLOCK = StimulusEvent.create(PunchBlock.class, ctx -> {
        return (puncher, direction, pos) -> {
            try {
                for (PunchBlock listener : ctx.getListeners()) {
                    ActionResult result = listener.onPunchBlock(puncher, direction, pos);
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

    public static final StimulusEvent<CraftRecipe> CRAFT_RECIPE = StimulusEvent.create(CraftRecipe.class, ctx -> {
        return (player, recipe) -> {
            try {
                for (CraftRecipe listener : ctx.getListeners()) {
                    ActionResult result = listener.onCraftRecipe(player, recipe);
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

    public static final StimulusEvent<ConsumeHunger> CONSUME_HUNGER = StimulusEvent.create(ConsumeHunger.class, ctx -> {
        return (player, foodLevel, saturation, exhaustion) -> {
            try {
                for (ConsumeHunger listener : ctx.getListeners()) {
                    ActionResult result = listener.onConsumeHunger(player, foodLevel, saturation, exhaustion);
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

    public static final StimulusEvent<PickupItem> PICKUP_ITEM = StimulusEvent.create(PickupItem.class, ctx -> {
        return (player, entity, stack) -> {
            try {
                for (PickupItem listener : ctx.getListeners()) {
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

    public interface Death {
        ActionResult onDeath(ServerPlayerEntity player, DamageSource source);
    }

    public interface Damage {
        ActionResult onDamage(ServerPlayerEntity player, DamageSource source, float amount);
    }

    public interface Chat {
        ActionResult onSendChatMessage(ServerPlayerEntity sender, Text message);
    }

    public interface ThrowItem {
        ActionResult onThrowItem(ServerPlayerEntity player, int slot, ItemStack stack);
    }

    public interface SwingHand {
        void onSwingHand(ServerPlayerEntity player, Hand hand);
    }

    public interface AttackEntity {
        ActionResult onAttackEntity(ServerPlayerEntity attacker, Hand hand, Entity attacked, EntityHitResult hitResult);
    }

    public interface FireArrow {
        ActionResult onFireArrow(ServerPlayerEntity user, ItemStack tool, ArrowItem arrowItem, int remainingUseTicks, PersistentProjectileEntity projectile);
    }

    public interface UseBlock {
        ActionResult onUseBlock(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult);
    }

    public interface UseItem {
        TypedActionResult<ItemStack> onUseItem(ServerPlayerEntity player, Hand hand);
    }

    public interface UseEntity {
        ActionResult onUseEntity(ServerPlayerEntity player, Entity entity, Hand hand, EntityHitResult hitResult);
    }

    public interface PunchBlock {
        ActionResult onPunchBlock(ServerPlayerEntity puncher, Direction direction, BlockPos pos);
    }

    public interface CraftRecipe {
        ActionResult onCraftRecipe(ServerPlayerEntity player, Recipe<?> recipe);
    }

    public interface ConsumeHunger {
        ActionResult onConsumeHunger(ServerPlayerEntity player, int foodLevel, float saturation, float exhaustion);
    }

    public interface PickupItem {
        ActionResult onPickupItem(ServerPlayerEntity player, ItemEntity entity, ItemStack stack);
    }
}
