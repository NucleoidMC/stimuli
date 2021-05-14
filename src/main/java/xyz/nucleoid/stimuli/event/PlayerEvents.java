package xyz.nucleoid.stimuli.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

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

    public interface Death {
        ActionResult onDeath(ServerPlayerEntity player, DamageSource source);
    }

    public interface Damage {
        ActionResult onDamage(ServerPlayerEntity player, DamageSource source, float amount);
    }

    public interface Chat {
        ActionResult onSendChatMessage(ServerPlayerEntity sender, Text message);
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

    public interface ConsumeHunger {
        ActionResult onConsumeHunger(ServerPlayerEntity player, int foodLevel, float saturation, float exhaustion);
    }
}
