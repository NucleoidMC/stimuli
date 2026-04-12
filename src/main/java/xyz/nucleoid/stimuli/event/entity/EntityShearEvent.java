package xyz.nucleoid.stimuli.event.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.StimulusEvent;

/**
 * Called before a {@linkplain net.minecraft.world.entity.Shearable shearable} entity is sheared by a player or dispenser.
 *
 * <p>Upon return:
 * <ul>
 * <li>{@link EventResult#ALLOW} cancels further processing and shears the entity.
 * <li>{@link EventResult#DENY} cancels further processing and doesn't shear the entity.
 * <li>{@link EventResult#PASS} moves on to the next listener.</ul>
 * <p>
 * If all listeners return {@link EventResult#PASS}, the entity is sheared.
 *
 * <p>If the entity is being sheared by a player, then {@code player} and {@code hand} will be provided.
 * If the entity is being sheared by a dispenser, then {@code pos} will be provided.
 */
public interface EntityShearEvent {
    StimulusEvent<EntityShearEvent> EVENT = StimulusEvent.create(EntityShearEvent.class, ctx -> (entity, player, hand, pos) -> {
        try {
            for (var listener : ctx.getListeners()) {
                var result = listener.onShearEntity(entity, player, hand, pos);
                if (result != EventResult.PASS) {
                    return result;
                }
            }
        } catch (Throwable t) {
            ctx.handleException(t);
        }
        return EventResult.PASS;
    });

    EventResult onShearEntity(LivingEntity entity, @Nullable ServerPlayer player, @Nullable InteractionHand hand, @Nullable BlockPos pos);
}
